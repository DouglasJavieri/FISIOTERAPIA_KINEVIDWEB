package com.fisioterapiakinevid.kinevid.rest.service.svc;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.svc.MedicalServiceRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.svc.MedicalServiceResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.svc.MedicalServiceUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
public interface MedicalServiceService {

    MedicalServiceResponseDTO createService(MedicalServiceRequestDTO request) throws OperationException;

    MedicalServiceResponseDTO getServiceById(Long id) throws OperationException;

    Page<MedicalServiceResponseDTO> getAllServices(Pageable pageable, ServiceStatus status, ServiceCategory category) throws OperationException;

    MedicalServiceResponseDTO updateService(Long id, MedicalServiceUpdateRequestDTO request) throws OperationException;

    MedicalServiceResponseDTO changeServiceStatus(Long id, ServiceStatus status) throws OperationException;

    void deleteService(Long id) throws OperationException;

    List<MedicalServiceResponseDTO> getActiveServicesList() throws OperationException;
}


