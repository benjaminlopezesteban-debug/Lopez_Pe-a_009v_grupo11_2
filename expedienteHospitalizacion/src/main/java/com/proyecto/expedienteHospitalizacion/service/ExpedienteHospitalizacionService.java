package com.proyecto.expedienteHospitalizacion.service;

import com.proyecto.expedienteHospitalizacion.client.FichaClinicaClient;
import com.proyecto.expedienteHospitalizacion.client.ReservaAtencionClient;
import com.proyecto.expedienteHospitalizacion.dto.request.ExpedienteHospitalizacionRequest;
import com.proyecto.expedienteHospitalizacion.dto.response.ExpedienteHospitalizacionResponse;
import com.proyecto.expedienteHospitalizacion.dto.response.FichaClinicaResponse;
import com.proyecto.expedienteHospitalizacion.dto.response.ReservaHoraResponse;
import com.proyecto.expedienteHospitalizacion.exceptions.ConflictException;
import com.proyecto.expedienteHospitalizacion.exceptions.NotFoundException;
import com.proyecto.expedienteHospitalizacion.exceptions.RemoteServiceException;
import com.proyecto.expedienteHospitalizacion.model.ExpedienteHospitalizacionModel;
import com.proyecto.expedienteHospitalizacion.repository.ExpedienteHospitalizacionRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Contiene la lógica de negocio del microservicio de Expediente de Hospitalización
@Service
@Transactional
public class ExpedienteHospitalizacionService {

    private final ExpedienteHospitalizacionRepository expedienteRepository;
    private final ReservaAtencionClient reservaAtencionClient;
    private final FichaClinicaClient fichaClinicaClient;

    public ExpedienteHospitalizacionService(ExpedienteHospitalizacionRepository expedienteRepository,
                                            ReservaAtencionClient reservaAtencionClient,
                                            FichaClinicaClient fichaClinicaClient) {
        this.expedienteRepository = expedienteRepository;
        this.reservaAtencionClient = reservaAtencionClient;
        this.fichaClinicaClient = fichaClinicaClient;
    }

    // Lista todos los expedientes enriquecidos
    public List<ExpedienteHospitalizacionResponse> obtenerTodos() {
        return expedienteRepository.findAll()
                .stream()
                .map(this::mapToResponseConDatos)
                .toList();
    }

    // Obtiene un expediente por su id
    public ExpedienteHospitalizacionResponse obtenerPorId(Long id) {
        ExpedienteHospitalizacionModel expediente = buscarPorId(id);
        return mapToResponseConDatos(expediente);
    }

    // Obtiene un expediente por su código
    public ExpedienteHospitalizacionResponse obtenerPorCodigo(String codExpediente) {
        ExpedienteHospitalizacionModel expediente = expedienteRepository.findByCodExpediente(codExpediente)
                .orElseThrow(() -> new NotFoundException("No existe expediente con código: " + codExpediente));
        return mapToResponseConDatos(expediente);
    }

    // Lista expedientes por rut de paciente
    public List<ExpedienteHospitalizacionResponse> obtenerPorRutPaciente(String rutPaciente) {
        return expedienteRepository.findByRutPaciente(rutPaciente)
                .stream()
                .map(this::mapToResponseConDatos)
                .toList();
    }

    // Lista expedientes por estado de digitalización
    public List<ExpedienteHospitalizacionResponse> obtenerPorDigitalizacion(boolean digitalizacion) {
        return expedienteRepository.findByDigitalizacion(digitalizacion)
                .stream()
                .map(this::mapToResponseConDatos)
                .toList();
    }

    // Crea un nuevo expediente de hospitalización
    // FLUJO: reserva existe → crear expediente → notificar a reserva con el id del expediente creado
    public ExpedienteHospitalizacionResponse guardar(ExpedienteHospitalizacionRequest request) {
        // 1. Validar que la reserva exista en el MS ReservaAtencion
        ReservaHoraResponse reserva = obtenerReservaDesdeServicio(request.getIdBooking());

        // 2. Validar que no exista ya un expediente para esa reserva
        if (expedienteRepository.existsByIdBooking(request.getIdBooking())) {
            throw new ConflictException("Ya existe un expediente para la reserva id: " + request.getIdBooking());
        }

        // 3. Validar que el código de expediente sea único
        if (expedienteRepository.existsByCodExpediente(request.getCodExpediente())) {
            throw new ConflictException("Ya existe un expediente con el código: " + request.getCodExpediente());
        }

        // 4. Persistir el expediente
        ExpedienteHospitalizacionModel expediente = new ExpedienteHospitalizacionModel();
        expediente.setCodExpediente(request.getCodExpediente());
        expediente.setRutPaciente(request.getRutPaciente());
        expediente.setDigitalizacion(request.getDigitalizacion());
        expediente.setIdBooking(request.getIdBooking());
        expediente.setIdRegistroArchivado(request.getIdRegistroArchivado());

        ExpedienteHospitalizacionModel guardado = expedienteRepository.save(expediente);

        // 5. Notificar al MS ReservaAtencion para que registre el id del expediente creado
        // Si falla esta llamada no revertimos — el expediente ya existe, se puede reintentar
        try {
            reservaAtencionClient.asignarExpediente(request.getIdBooking(), guardado.getIdExpediente());
        } catch (FeignException e) {
            // Se registra el problema pero no se lanza excepción para no perder el expediente guardado
            // En un entorno productivo aquí iría un evento de compensación o un log de alerta
            System.err.println("[WARN] No se pudo notificar la reserva " + request.getIdBooking()
                    + " con el expediente creado. Causa: " + e.getMessage());
        }

        // 6. Obtener ficha clínica del paciente para enriquecer el response
        FichaClinicaResponse fichaClinica = obtenerFichaClinicaPorPaciente(reserva.getIdPaciente());

        return mapToResponse(guardado, reserva, fichaClinica);
    }

    // Actualiza los datos de un expediente existente
    public ExpedienteHospitalizacionResponse actualizar(Long id, ExpedienteHospitalizacionRequest request) {
        ExpedienteHospitalizacionModel expediente = buscarPorId(id);

        // Validar reserva si cambió
        ReservaHoraResponse reserva = obtenerReservaDesdeServicio(request.getIdBooking());

        // Validar unicidad de código si cambió
        if (!expediente.getCodExpediente().equals(request.getCodExpediente())
                && expedienteRepository.existsByCodExpediente(request.getCodExpediente())) {
            throw new ConflictException("Ya existe un expediente con el código: " + request.getCodExpediente());
        }

        expediente.setCodExpediente(request.getCodExpediente());
        expediente.setRutPaciente(request.getRutPaciente());
        expediente.setDigitalizacion(request.getDigitalizacion());
        expediente.setIdBooking(request.getIdBooking());
        expediente.setIdRegistroArchivado(request.getIdRegistroArchivado());

        ExpedienteHospitalizacionModel actualizado = expedienteRepository.save(expediente);
        FichaClinicaResponse fichaClinica = obtenerFichaClinicaPorPaciente(reserva.getIdPaciente());

        return mapToResponse(actualizado, reserva, fichaClinica);
    }

    // Asigna o actualiza el id de registro archivado en un expediente existente
    // Este método es llamado desde el MS RegistroArchivado cuando archiva un expediente
    public ExpedienteHospitalizacionResponse asignarRegistroArchivado(Long idExpediente, Long idRegistroArchivado) {
        ExpedienteHospitalizacionModel expediente = buscarPorId(idExpediente);
        expediente.setIdRegistroArchivado(idRegistroArchivado);
        ExpedienteHospitalizacionModel actualizado = expedienteRepository.save(expediente);
        return mapToResponseConDatos(actualizado);
    }

    // Elimina un expediente por id
    public void eliminar(Long id) {
        ExpedienteHospitalizacionModel expediente = buscarPorId(id);
        expedienteRepository.delete(expediente);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS — Feign calls con manejo de excepciones uniforme
    // ─────────────────────────────────────────────────────────────────────────────

    private ReservaHoraResponse obtenerReservaDesdeServicio(Long idBooking) {
        try {
            return reservaAtencionClient.obtenerReservaPorId(idBooking);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe la reserva con id: " + idBooking);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de reservas");
        }
    }

    private FichaClinicaResponse obtenerFichaClinicaPorPaciente(Long idPaciente) {
        try {
            List<FichaClinicaResponse> fichas = fichaClinicaClient.obtenerFichasPorPaciente(idPaciente);
            if (fichas.isEmpty()) {
                throw new NotFoundException("No existe ficha clínica para el paciente id: " + idPaciente);
            }
            return fichas.get(0);
        } catch (NotFoundException e) {
            throw e;
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de ficha clínica");
        }
    }

    private ExpedienteHospitalizacionModel buscarPorId(Long id) {
        return expedienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el expediente con id: " + id));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // MAPPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────────

    // Consulta reserva y ficha desde sus servicios y construye el response
    private ExpedienteHospitalizacionResponse mapToResponseConDatos(ExpedienteHospitalizacionModel expediente) {
        ReservaHoraResponse reserva = obtenerReservaDesdeServicio(expediente.getIdBooking());
        FichaClinicaResponse fichaClinica = obtenerFichaClinicaPorPaciente(reserva.getIdPaciente());
        return mapToResponse(expediente, reserva, fichaClinica);
    }

    // Construye el response completo con objetos ya obtenidos
    private ExpedienteHospitalizacionResponse mapToResponse(ExpedienteHospitalizacionModel expediente,
                                                             ReservaHoraResponse reserva,
                                                             FichaClinicaResponse fichaClinica) {
        return ExpedienteHospitalizacionResponse.builder()
                .idExpediente(expediente.getIdExpediente())
                .codExpediente(expediente.getCodExpediente())
                .rutPaciente(expediente.getRutPaciente())
                .digitalizacion(expediente.isDigitalizacion())
                .idBooking(expediente.getIdBooking())
                .idRegistroArchivado(expediente.getIdRegistroArchivado())
                .reserva(reserva)
                .fichaClinica(fichaClinica)
                .build();
    }
}
