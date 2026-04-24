package com.fisioterapiakinevid.kinevid.rest.service.emp.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.emp.EmployeeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.emp.EmployeeResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.emp.EmployeeUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import com.fisioterapiakinevid.kinevid.rest.model.entity.emp.Employee;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.EmployeeStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.emp.EmployeeRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.u.UserRepository;
import com.fisioterapiakinevid.kinevid.rest.service.emp.EmployeeService;
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
 * @created 07/04/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre", request.getFirstName(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Apellido paterno", request.getPaternalSurname(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Carnet", request.getCi(), true, 20);
            ValidationUtil.throwExceptionIfInvalidText("Especialidad", request.getSpecialty(), true, 100);

            if (request.getExpedition() == null) {
                throw new OperationException("El lugar de expediciÃ³n es requerido");
            }
            if (request.getDepartment() == null) {
                throw new OperationException("El departamento es requerido");
            }
            if (request.getAdmissionDate() == null) {
                throw new OperationException("La fecha de admisiÃ³n es requerida");
            }

            Employee employee = Employee.builder()
                    .firstName(request.getFirstName().trim())
                    .paternalSurname(request.getPaternalSurname().trim())
                    .maternalSurname(request.getMaternalSurname() != null ? request.getMaternalSurname().trim() : null)
                    .ci(request.getCi().trim())
                    .expedition(request.getExpedition())
                    .specialty(request.getSpecialty().trim())
                    .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                    .address(request.getAddress() != null ? request.getAddress().trim() : null)
                    .department(request.getDepartment())
                    .professionalEmail(request.getProfessionalEmail() != null ? request.getProfessionalEmail().trim().toLowerCase() : null)
                    .admissionDate(request.getAdmissionDate())
                    .status(EmployeeStatus.ACTIVE)
                    .build();

            employeeRepository.save(employee);
            log.info("Empleado creado: {} {}", employee.getFirstName(), employee.getPaternalSurname());
            return new EmployeeResponseDTO(employee);

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al crear empleado: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear empleado", e);
            throw new OperationException("OcurriÃ³ un error inesperado al crear el empleado");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) throws OperationException {
        try {
            Employee employee = findActiveEmployeeById(id);
            return new EmployeeResponseDTO(employee);

        } catch (OperationException e) {
            log.error("Error al obtener empleado con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener empleado con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al buscar el empleado");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable, EmployeeStatus status) throws OperationException {
        try {
            if (status != null) {
                return employeeRepository.findAllByStatus(status, pageable)
                        .map(EmployeeResponseDTO::new);
            }
            return employeeRepository.findAllActive(pageable)
                    .map(EmployeeResponseDTO::new);
        } catch (Exception e) {
            log.error("Error inesperado al listar empleados", e);
            throw new OperationException("OcurriÃ³ un error inesperado al listar empleados");
        }
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateRequestDTO request) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre", request.getFirstName(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Apellido paterno", request.getPaternalSurname(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Carnet", request.getCi(), true, 20);
            ValidationUtil.throwExceptionIfInvalidText("Especialidad", request.getSpecialty(), true, 100);

            if (request.getExpedition() == null) {
                throw new OperationException("El lugar de expediciÃ³n es requerido");
            }
            if (request.getDepartment() == null) {
                throw new OperationException("El departamento es requerido");
            }

            Employee employee = findActiveEmployeeById(id);

            employee.setFirstName(request.getFirstName().trim());
            employee.setPaternalSurname(request.getPaternalSurname().trim());
            employee.setMaternalSurname(request.getMaternalSurname() != null ? request.getMaternalSurname().trim() : null);
            employee.setCi(request.getCi().trim());
            employee.setExpedition(request.getExpedition());
            employee.setSpecialty(request.getSpecialty().trim());
            employee.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
            employee.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
            employee.setDepartment(request.getDepartment());
            employee.setProfessionalEmail(request.getProfessionalEmail() != null ? request.getProfessionalEmail().trim().toLowerCase() : null);
            employee.setAdmissionDate(request.getAdmissionDate());

            employeeRepository.save(employee);
            log.info("Empleado actualizado: ID={}", id);
            return new EmployeeResponseDTO(employee);

        } catch (OperationException e) {
            log.error("Error al actualizar empleado con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar empleado con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al actualizar el empleado");
        }
    }

    @Override
    @Transactional
    public EmployeeResponseDTO changeEmployeeStatus(Long id, EmployeeStatus status) throws OperationException {
        try {
            if (status == EmployeeStatus.ELIMINATION) {
                throw new OperationException(
                        "No se puede establecer el estado ELIMINATION directamente. Use el endpoint de eliminaciÃ³n.");
            }

            Employee employee = findActiveEmployeeById(id);

            if (employee.getStatus() == status) {
                throw new OperationException(
                        "El empleado ya se encuentra en el estado '" + status.getDescription() + "'.");
            }

            employee.setStatus(status);
            employeeRepository.save(employee);
            log.info("Estado del empleado ID={} cambiado a: {}", id, status);
            return new EmployeeResponseDTO(employee);

        } catch (OperationException e) {
            log.error("Error al cambiar estado del empleado con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del empleado con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al cambiar el estado del empleado");
        }
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) throws OperationException {
        try {
            Employee employee = findActiveEmployeeById(id);
            employee.setDeleted(true);
            employee.setStatus(EmployeeStatus.ELIMINATION);
            employeeRepository.save(employee);
            log.info("Empleado con ID={} eliminado lÃ³gicamente", id);

        } catch (OperationException e) {
            log.error("Error al eliminar empleado con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar empleado con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al eliminar el empleado");
        }
    }

    @Override
    @Transactional
    public EmployeeResponseDTO assignUserToEmployee(Long employeeId, Long userId) throws OperationException {
        try {
            Employee employee = findActiveEmployeeById(employeeId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Usuario", userId)));

            if (user.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Usuario", userId));
            }

            // Verifica que el usuario no estÃ© ya asignado a otro empleado
            if (employeeRepository.existsByUserIdExcludingId(userId, employeeId)) {
                throw new OperationException(
                        "El usuario '" + user.getUsername() + "' ya estÃ¡ asignado a otro empleado.");
            }

            employee.setUser(user);
            employeeRepository.save(employee);
            log.info("Usuario ID={} asignado al empleado ID={}", userId, employeeId);
            return new EmployeeResponseDTO(employee);

        } catch (OperationException e) {
            log.error("Error al asignar usuario al empleado ID {}: {}", employeeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al asignar usuario al empleado ID {}", employeeId, e);
            throw new OperationException("OcurriÃ³ un error inesperado al asignar el usuario al empleado");
        }
    }

    @Override
    @Transactional
    public EmployeeResponseDTO removeUserFromEmployee(Long employeeId) throws OperationException {
        try {
            Employee employee = findActiveEmployeeById(employeeId);

            if (employee.getUser() == null) {
                throw new OperationException("El empleado no tiene ningÃºn usuario asignado.");
            }

            employee.setUser(null);
            employeeRepository.save(employee);
            log.info("Usuario desvinculado del empleado ID={}", employeeId);
            return new EmployeeResponseDTO(employee);

        } catch (OperationException e) {
            log.error("Error al desvincular usuario del empleado ID {}: {}", employeeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al desvincular usuario del empleado ID {}", employeeId, e);
            throw new OperationException("OcurriÃ³ un error inesperado al desvincular el usuario del empleado");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersAvailableForEmployee() throws OperationException {
        try {
            return userRepository.findUsersAvailableForEmployee()
                    .stream()
                    .map(UserResponseDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error inesperado al obtener usuarios disponibles", e);
            throw new OperationException("OcurriÃ³ un error inesperado al obtener usuarios disponibles");
        }
    }

    private Employee findActiveEmployeeById(Long id) throws OperationException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Empleado", id)));

        if (employee.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Empleado", id));
        }
        return employee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getActiveEmployeeList() throws OperationException {
        try {
            return employeeRepository.findAllActiveList()
                    .stream()
                    .map(EmployeeResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error inesperado al obtener lista activa de empleados", e);
            throw new OperationException("Ocurrió un error inesperado al obtener la lista de empleados activos");
        }
    }
}


