package com.proyecto.fichaClinica.service;

import com.proyecto.fichaClinica.client.EstanteClient;
import com.proyecto.fichaClinica.client.PacienteClient;
import com.proyecto.fichaClinica.dto.request.FichaClinicaRequest;
import com.proyecto.fichaClinica.dto.response.EstanteResponse;
import com.proyecto.fichaClinica.dto.response.FichaClinicaResponse;
import com.proyecto.fichaClinica.dto.response.PacienteResponse;
import com.proyecto.fichaClinica.dto.response.UbicacionFichaResponse;
import com.proyecto.fichaClinica.exception.NotFoundException;
import com.proyecto.fichaClinica.exception.RemoteServiceException;
import com.proyecto.fichaClinica.model.FichaClinicaModel;
import com.proyecto.fichaClinica.repository.FichaClinicaRepository;
import feign.FeignException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Contiene la lógica de negocio del microservicio de Ficha Clínica
@Service
@Transactional
public class FichaClinicaService {

    private final FichaClinicaRepository fichaClinicaRepository;
    private final PacienteClient pacienteClient;
    private final EstanteClient estanteClient;

    public FichaClinicaService(FichaClinicaRepository fichaClinicaRepository,
                               PacienteClient pacienteClient,
                               EstanteClient estanteClient) {
        this.fichaClinicaRepository = fichaClinicaRepository;
        this.pacienteClient = pacienteClient;
        this.estanteClient = estanteClient;
    }

    // Lista todas las fichas clínicas enriquecidas con paciente y estante
    public List<FichaClinicaResponse> obtenerTodas() {
        return fichaClinicaRepository.findAll()
                .stream()
                .map(this::mapToResponseConDatos)
                .toList();
    }

    // Obtiene una ficha clínica por su folioFicha
    public FichaClinicaResponse obtenerPorFolio(Long folioFicha) {
        FichaClinicaModel ficha = buscarFichaPorFolio(folioFicha);
        return mapToResponseConDatos(ficha);
    }

    // Lista todas las fichas de un paciente por su id
    public List<FichaClinicaResponse> obtenerPorPaciente(Long idPaciente) {
        // Validamos que el paciente exista antes de buscar sus fichas
        obtenerPacienteDesdeServicio(idPaciente);

        return fichaClinicaRepository.findByIdPaciente(idPaciente)
                .stream()
                .map(this::mapToResponseConDatos)
                .toList();
    }

    // Crea una nueva ficha clínica
    public FichaClinicaResponse guardar(FichaClinicaRequest request) {
        // Validamos que tanto el paciente como el estante existan
        PacienteResponse paciente = obtenerPacienteDesdeServicio(request.getIdPaciente());
        EstanteResponse estante = obtenerEstanteDesdeServicio(request.getIdEstante());

        FichaClinicaModel ficha = new FichaClinicaModel();
        ficha.setIdPaciente(request.getIdPaciente());
        ficha.setIdEstante(request.getIdEstante());
        // fechaCreacion se asigna automáticamente con @CreationTimestamp en la entidad

        FichaClinicaModel guardada = fichaClinicaRepository.save(ficha);

        return mapToResponse(guardada, paciente, estante);
    }

    // Actualiza el estante de una ficha clínica existente
    public FichaClinicaResponse actualizar(Long folioFicha, FichaClinicaRequest request) {
        FichaClinicaModel ficha = buscarFichaPorFolio(folioFicha);

        // Validamos que los nuevos ids existan en sus respectivos microservicios
        PacienteResponse paciente = obtenerPacienteDesdeServicio(request.getIdPaciente());
        EstanteResponse estante = obtenerEstanteDesdeServicio(request.getIdEstante());

        ficha.setIdPaciente(request.getIdPaciente());
        ficha.setIdEstante(request.getIdEstante());

        FichaClinicaModel actualizada = fichaClinicaRepository.save(ficha);

        return mapToResponse(actualizada, paciente, estante);
    }

    // Elimina una ficha clínica por folioFicha
    public void eliminar(Long folioFicha) {
        FichaClinicaModel ficha = buscarFichaPorFolio(folioFicha);
        fichaClinicaRepository.delete(ficha);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // MÉTODOS DE BÚSQUEDA DE UBICACIÓN — Lógica de negocio principal
    // Devuelven en qué estante y bodega física se encuentra una ficha
    // ─────────────────────────────────────────────────────────────────────────────

    // Muestra la ubicación física de una ficha buscando por folioFicha
    public UbicacionFichaResponse obtenerUbicacionPorFolio(Long folioFicha) {
        FichaClinicaModel ficha = buscarFichaPorFolio(folioFicha);
        PacienteResponse paciente = obtenerPacienteDesdeServicio(ficha.getIdPaciente());
        EstanteResponse estante = obtenerEstanteDesdeServicio(ficha.getIdEstante());

        return buildUbicacion(ficha, paciente, estante);
    }

    // Muestra la ubicación física de la ficha buscando por rut del paciente
    public UbicacionFichaResponse obtenerUbicacionPorRut(String rut) {
        PacienteResponse paciente = obtenerPacientePorRut(rut);

        // Con el id del paciente buscamos su ficha en este MS
        List<FichaClinicaModel> fichas = fichaClinicaRepository.findByIdPaciente(paciente.getId());
        if (fichas.isEmpty()) {
            throw new NotFoundException("No existe ficha clínica para el paciente con rut: " + rut);
        }

        // Un paciente puede tener una sola ficha activa — tomamos la primera
        FichaClinicaModel ficha = fichas.get(0);
        EstanteResponse estante = obtenerEstanteDesdeServicio(ficha.getIdEstante());

        return buildUbicacion(ficha, paciente, estante);
    }

    // Muestra la ubicación física de la ficha buscando por nombre y apellido paterno del paciente
    public List<UbicacionFichaResponse> obtenerUbicacionPorNombre(String pnombre, String snombre, String appaterno) {
        // Buscamos candidatos en el MS Paciente
        List<PacienteResponse> pacientes = obtenerPacientesPorNombre(pnombre, snombre, appaterno);

        if (pacientes.isEmpty()) {
            throw new NotFoundException(
                    "No se encontraron pacientes con los datos: " + pnombre + " " + appaterno
            );
        }

        // Por cada paciente encontrado buscamos su ficha y construimos la ubicación
        return pacientes.stream()
                .flatMap(paciente -> {
                    List<FichaClinicaModel> fichas = fichaClinicaRepository.findByIdPaciente(paciente.getId());
                    return fichas.stream().map(ficha -> {
                        EstanteResponse estante = obtenerEstanteDesdeServicio(ficha.getIdEstante());
                        return buildUbicacion(ficha, paciente, estante);
                    });
                })
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS — Feign calls con manejo de excepciones uniforme
    // ─────────────────────────────────────────────────────────────────────────────

    private PacienteResponse obtenerPacienteDesdeServicio(Long idPaciente) {
        try {
            return pacienteClient.obtenerPacientePorId(idPaciente);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe el paciente con id: " + idPaciente);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de pacientes");
        }
    }

    private PacienteResponse obtenerPacientePorRut(String rut) {
        try {
            return pacienteClient.obtenerPacientePorRut(rut);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe el paciente con rut: " + rut);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de pacientes");
        }
    }

    private List<PacienteResponse> obtenerPacientesPorNombre(String pnombre, String snombre, String appaterno) {
        try {
            return pacienteClient.buscarPorNombreOApellido(pnombre, snombre, appaterno);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de pacientes");
        }
    }

    private EstanteResponse obtenerEstanteDesdeServicio(Long idEstante) {
        try {
            return estanteClient.obtenerEstantePorId(idEstante);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe el estante con id: " + idEstante);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de estantes");
        }
    }

    private FichaClinicaModel buscarFichaPorFolio(Long folioFicha) {
        return fichaClinicaRepository.findByFolioFicha(folioFicha)
                .orElseThrow(() -> new NotFoundException("No existe la ficha clínica con folio: " + folioFicha));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // MAPPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────────

    // Consulta paciente y estante desde sus servicios y construye el response
    private FichaClinicaResponse mapToResponseConDatos(FichaClinicaModel ficha) {
        PacienteResponse paciente = obtenerPacienteDesdeServicio(ficha.getIdPaciente());
        EstanteResponse estante = obtenerEstanteDesdeServicio(ficha.getIdEstante());
        return mapToResponse(ficha, paciente, estante);
    }

    // Construye el FichaClinicaResponse completo con objetos ya obtenidos
    private FichaClinicaResponse mapToResponse(FichaClinicaModel ficha,
                                               PacienteResponse paciente,
                                               EstanteResponse estante) {
        return FichaClinicaResponse.builder()
                .folioFicha(ficha.getFolioFicha())
                .fechaCreacion(ficha.getFechaCreacion())
                .idPaciente(ficha.getIdPaciente())
                .idEstante(ficha.getIdEstante())
                .paciente(paciente)
                .estante(estante)
                .build();
    }

    // Construye el DTO de ubicación física de una ficha
    private UbicacionFichaResponse buildUbicacion(FichaClinicaModel ficha,
                                                   PacienteResponse paciente,
                                                   EstanteResponse estante) {
        String nombreCompleto = paciente.getPnombre() + " "
                + (paciente.getSnombre() != null ? paciente.getSnombre() + " " : "")
                + paciente.getPapellido();

        return UbicacionFichaResponse.builder()
                .folioFicha(ficha.getFolioFicha())
                .rutPaciente(paciente.getNumRut())
                .nombreCompletoPaciente(nombreCompleto)
                .numEstante(estante.getNumEstante())
                .numBodega(estante.getNumBodega())
                .build();
    }
}
