package com.proyecto.prestamo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.prestamo.client.AdministrativoClient;
import com.proyecto.prestamo.client.FichaClinicaClient;
import com.proyecto.prestamo.dto.request.PrestamoRequestDTO;
import com.proyecto.prestamo.dto.response.PrestamoResponseDTO;
import com.proyecto.prestamo.exceptions.NotFoundException;
import com.proyecto.prestamo.exceptions.RemoteServiceException;
import com.proyecto.prestamo.model.PrestamoModel;
import com.proyecto.prestamo.repository.PrestamoRepository;

import feign.FeignException;

@Service
@Transactional
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final AdministrativoClient administrativoClient;
    private final FichaClinicaClient fichaClinicaClient;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           AdministrativoClient administrativoClient,
                           FichaClinicaClient fichaClinicaClient) {
        this.prestamoRepository = prestamoRepository;
        this.administrativoClient = administrativoClient;
        this.fichaClinicaClient = fichaClinicaClient;
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> findAll() {
        return prestamoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public PrestamoResponseDTO create(PrestamoRequestDTO request) {
        validarAdministrativo(request.idAdministrativo());
        validarFichaClinica(request.folioFicha());
        PrestamoModel prestamo = new PrestamoModel();
        copy(request, prestamo);
        return toResponse(prestamoRepository.save(prestamo));
    }

    public PrestamoResponseDTO update(Long id, PrestamoRequestDTO request) {
        PrestamoModel prestamo = findEntity(id);
        validarAdministrativo(request.idAdministrativo());
        validarFichaClinica(request.folioFicha());
        copy(request, prestamo);
        return toResponse(prestamoRepository.save(prestamo));
    }

    public void delete(Long id) {
        prestamoRepository.delete(findEntity(id));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS — Feign calls con manejo de excepciones uniforme
    // ─────────────────────────────────────────────────────────────────────────────

    private void validarAdministrativo(Long idAdministrativo) {
        try {
            administrativoClient.obtenerPorId(idAdministrativo);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe el administrativo con id: " + idAdministrativo);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de administrativos");
        }
    }

    private void validarFichaClinica(String folioFichaStr) {
        try {
            Long folioFicha = Long.parseLong(folioFichaStr);
            fichaClinicaClient.obtenerPorFolio(folioFicha);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Folio de ficha clínica inválido: " + folioFichaStr);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe ficha clínica con folio: " + folioFichaStr);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de ficha clínica");
        }
    }

    private PrestamoModel findEntity(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Préstamo no encontrado con id: " + id));
    }

    private void copy(PrestamoRequestDTO request, PrestamoModel prestamo) {
        prestamo.setIdAdministrativo(request.idAdministrativo());
        prestamo.setFolioFicha(request.folioFicha());
        prestamo.setFechaPrestamo(request.fechaPrestamo());
        prestamo.setFechaDevolucion(request.fechaDevolucion());
        prestamo.setEstado(request.estado());
    }

    private PrestamoResponseDTO toResponse(PrestamoModel prestamo) {
        return new PrestamoResponseDTO(
                prestamo.getIdPrestamo(),
                prestamo.getIdAdministrativo(),
                prestamo.getFolioFicha(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado()
        );
    }
}
