package com.fisioterapiakinevid.kinevid.rest.service.emp;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.emp.EmployeeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.emp.EmployeeResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.emp.EmployeeUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;/**
 * @author Douglas Cristhian Javieri Vino
 * @created 07/04/2026
 */
public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) throws OperationException;

    EmployeeResponseDTO getEmployeeById(Long id) throws OperationException;

    Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable, EmployeeStatus status) throws OperationException;

    EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateRequestDTO request) throws OperationException;

    EmployeeResponseDTO changeEmployeeStatus(Long id, EmployeeStatus status) throws OperationException;

    void deleteEmployee(Long id) throws OperationException;

    EmployeeResponseDTO assignUserToEmployee(Long employeeId, Long userId) throws OperationException;

    EmployeeResponseDTO removeUserFromEmployee(Long employeeId) throws OperationException;

    List<UserResponseDto> getUsersAvailableForEmployee() throws OperationException;
}


