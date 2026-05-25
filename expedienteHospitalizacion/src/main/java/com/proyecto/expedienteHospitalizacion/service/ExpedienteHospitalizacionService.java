package com.proyecto.expedienteHospitalizacion.service;

import com.proyecto.expedienteHospitalizacion.client.FichaClinicaClient;
import com.proyecto.expedienteHospitalizacion.client.ReservaAtencionClient;
import com.proyecto.expedienteHospitalizacion.dto.request.ExpedienteHospitalizacionRequest;
import com.proyecto.expedienteHospitalizacion.dto.response.ExpedienteHospitalizacionResponse;
import com.proyecto.expedienteHospitalizacion.dto.response.FichaClinicaResponse;
import com.proyecto.expedienteHospitalizacion.dto.response.ReservaHoraResponse;
import com.proyecto.expedienteHospitalizacion.exceptions.NotFoundException;
import com.proyecto.expedienteHospitalizacion.exceptions.RemoteServiceException;
import com.proyecto.expedienteHospitalizacion.model.ExpedienteHospitalizacionModel;
import com.proyecto.expedienteHospitalizacion.repository.ExpedienteHospitalizacionRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<ExpedienteHospitalizacionResponse> obtenerTodos() {
        return expedienteRepository.findAll().stream().map(this::mapToResponseConDatos).toList();
    }

    public ExpedienteHospitalizacionResponse obtenerPorId(Long id) {
        return mapToResponseConDatos(buscarPorId(id));
    }

    public ExpedienteHospitalizacionResponse obtenerPorCodigo(String codExpediente) {
        ExpedienteHospitalizacionModel expediente = expedienteRepository.findByCodExpediente(codExpediente)
                .orElseThrow(() -> new NotFoundException("No existe expediente con código: " + codExpediente));
        return mapToResponseConDatos(expediente);
    }

    public ExpedienteHospitalizacionResponse guardar(ExpedienteHospitalizacionRequest request) {
        ReservaHoraResponse reserva = obtenerReservaDesdeServicio(request.getIdBooking());

        ExpedienteHospitalizacionModel expediente = new ExpedienteHospitalizacionModel();
        expediente.setCodExpediente(request.getCodExpediente());
        expediente.setRutPaciente(request.getRutPaciente());
        expediente.setDigitalizacion(request.getDigitalizacion());
        expediente.setIdBooking(request.getIdBooking());
        expediente.setIdRegistroArchivado(request.getIdRegistroArchivado());

        ExpedienteHospitalizacionModel guardado = expedienteRepository.save(expediente);

        try {
            reservaAtencionClient.asignarExpediente(request.getIdBooking(), guardado.getIdExpediente());
        } catch (FeignException e) {
            System.err.println("[WARN] No se pudo notificar la reserva " + request.getIdBooking()
                    + " con el expediente creado. Causa: " + e.getMessage());
        }

        FichaClinicaResponse fichaClinica = obtenerFichaClinicaPorPaciente(reserva.getIdPaciente());
        return mapToResponse(guardado, reserva, fichaClinica);
    }

    public ExpedienteHospitalizacionResponse actualizar(Long id, ExpedienteHospitalizacionRequest request) {
        ExpedienteHospitalizacionModel expediente = buscarPorId(id);
        ReservaHoraResponse reserva = obtenerReservaDesdeServicio(request.getIdBooking());

        expediente.setCodExpediente(request.getCodExpediente());
        expediente.setRutPaciente(request.getRutPaciente());
        expediente.setDigitalizacion(request.getDigitalizacion());
        expediente.setIdBooking(request.getIdBooking());
        expediente.setIdRegistroArchivado(request.getIdRegistroArchivado());

        ExpedienteHospitalizacionModel actualizado = expedienteRepository.save(expediente);
        FichaClinicaResponse fichaClinica = obtenerFichaClinicaPorPaciente(reserva.getIdPaciente());
        return mapToResponse(actualizado, reserva, fichaClinica);
    }

    public ExpedienteHospitalizacionResponse asignarRegistroArchivado(Long idExpediente, Long idRegistroArchivado) {
        ExpedienteHospitalizacionModel expediente = buscarPorId(idExpediente);
        expediente.setIdRegistroArchivado(idRegistroArchivado);
        return mapToResponseConDatos(expedienteRepository.save(expediente));
    }

    public void eliminar(Long id) {
        expedienteRepository.delete(buscarPorId(id));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
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

    private ExpedienteHospitalizacionResponse mapToResponseConDatos(ExpedienteHospitalizacionModel expediente) {
        ReservaHoraResponse reserva = obtenerReservaDesdeServicio(expediente.getIdBooking());
        FichaClinicaResponse fichaClinica = obtenerFichaClinicaPorPaciente(reserva.getIdPaciente());
        return mapToResponse(expediente, reserva, fichaClinica);
    }

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
