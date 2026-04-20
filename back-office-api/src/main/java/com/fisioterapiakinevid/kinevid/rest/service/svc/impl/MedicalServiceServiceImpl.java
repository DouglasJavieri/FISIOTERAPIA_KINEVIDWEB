package com.fisioterapiakinevid.kinevid.rest.service.svc.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.svc.MedicalServiceRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.svc.MedicalServiceResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.svc.MedicalServiceUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.entity.svc.MedicalService;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.svc.MedicalServiceRepository;
import com.fisioterapiakinevid.kinevid.rest.service.svc.MedicalServiceService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import com.fisioterapiakinevid.kinevid.rest.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;

    @Override
    @Transactional
    public MedicalServiceResponseDTO createService(MedicalServiceRequestDTO request) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre del servicio", request.getName(), true, 100);

            if (request.getCategory() == null) {
                throw new OperationException("La categorÃ­a del servicio es requerida");
            }
            if (medicalServiceRepository.existsByName(request.getName().trim())) {
                throw new OperationException(FormatUtil.yaRegistrado("Servicio", "nombre", request.getName()));
            }

            MedicalService service = MedicalService.builder()
                    .name(request.getName().trim())
                    .description(request.getDescription() != null ? request.getDescription().trim() : null)
                    .category(request.getCategory())
                    .durationMinutes(request.getDurationMinutes())
                    .price(request.getPrice())
                    .status(ServiceStatus.ACTIVE)
                    .build();

            medicalServiceRepository.save(service);
            log.info("Servicio mÃ©dico creado: {}", service.getName());
            return new MedicalServiceResponseDTO(service);

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al crear servicio: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear servicio", e);
            throw new OperationException("OcurriÃ³ un error inesperado al crear el servicio");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalServiceResponseDTO getServiceById(Long id) throws OperationException {
        try {
            MedicalService service = findActiveServiceById(id);
            return new MedicalServiceResponseDTO(service);
        } catch (OperationException e) {
            log.error("Error al obtener servicio con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener servicio con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al buscar el servicio");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalServiceResponseDTO> getAllServices(Pageable pageable, ServiceStatus status, ServiceCategory category) throws OperationException {
        try {
            if (category != null) {
                return medicalServiceRepository.findAllByCategory(category, pageable)
                        .map(MedicalServiceResponseDTO::new);
            }
            if (status != null) {
                return medicalServiceRepository.findAllByStatus(status, pageable)
                        .map(MedicalServiceResponseDTO::new);
            }
            return medicalServiceRepository.findAllActive(pageable)
                    .map(MedicalServiceResponseDTO::new);
        } catch (Exception e) {
            log.error("Error inesperado al listar servicios", e);
            throw new OperationException("OcurriÃ³ un error inesperado al listar servicios");
        }
    }

    @Override
    @Transactional
    public MedicalServiceResponseDTO updateService(Long id, MedicalServiceUpdateRequestDTO request) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre del servicio", request.getName(), true, 100);

            if (request.getCategory() == null) {
                throw new OperationException("La categorÃ­a del servicio es requerida");
            }

            MedicalService service = findActiveServiceById(id);

            if (medicalServiceRepository.existsByNameExcludingId(request.getName().trim(), id)) {
                throw new OperationException(FormatUtil.yaRegistrado("Servicio", "nombre", request.getName()));
            }

            service.setName(request.getName().trim());
            service.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
            service.setCategory(request.getCategory());
            service.setDurationMinutes(request.getDurationMinutes());
            service.setPrice(request.getPrice());

            medicalServiceRepository.save(service);
            log.info("Servicio mÃ©dico actualizado: ID={}", id);
            return new MedicalServiceResponseDTO(service);

        } catch (OperationException e) {
            log.error("Error al actualizar servicio con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar servicio con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al actualizar el servicio");
        }
    }

    @Override
    @Transactional
    public MedicalServiceResponseDTO changeServiceStatus(Long id, ServiceStatus status) throws OperationException {
        try {
            if (status == ServiceStatus.ELIMINATION) {
                throw new OperationException("No se puede establecer el estado ELIMINATION directamente. Use el endpoint de eliminaciÃ³n.");
            }

            MedicalService service = findActiveServiceById(id);

            if (service.getStatus() == status) {
                throw new OperationException("El servicio ya se encuentra en el estado '" + status.getDescription() + "'.");
            }

            service.setStatus(status);
            medicalServiceRepository.save(service);
            log.info("Estado del servicio ID={} cambiado a: {}", id, status);
            return new MedicalServiceResponseDTO(service);

        } catch (OperationException e) {
            log.error("Error al cambiar estado del servicio con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del servicio con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al cambiar el estado del servicio");
        }
    }

    @Override
    @Transactional
    public void deleteService(Long id) throws OperationException {
        try {
            MedicalService service = findActiveServiceById(id);
            service.setDeleted(true);
            service.setStatus(ServiceStatus.ELIMINATION);
            medicalServiceRepository.save(service);
            log.info("Servicio mÃ©dico con ID={} eliminado lÃ³gicamente", id);
        } catch (OperationException e) {
            log.error("Error al eliminar servicio con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar servicio con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al eliminar el servicio");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalServiceResponseDTO> getActiveServicesList() throws OperationException {
        try {
            return medicalServiceRepository.findAllActiveList()
                    .stream()
                    .map(MedicalServiceResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error inesperado al obtener lista de servicios activos", e);
            throw new OperationException("OcurriÃ³ un error inesperado al obtener la lista de servicios");
        }
    }

    private MedicalService findActiveServiceById(Long id) throws OperationException {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Servicio", id)));
        if (service.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Servicio", id));
        }
        return service;
    }
}


