package com.fisioterapiakinevid.kinevid.rest.controller.emp;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.emp.*;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.EmployeeStatus;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.emp.EmployeeService;
import com.fisioterapiakinevid.kinevid.rest.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 07/04/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/employee")
@Tag(name = "employees", description = "GestiÃ³n de empleados del consultorio")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @Operation(summary = "Crear un nuevo empleado",
            description = "Registra un nuevo empleado. El usuario de acceso se asigna de forma independiente. Requiere permiso CREATE_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Empleado creado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validaciÃ³n", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody EmployeeRequestDTO request) {
        try {
            EmployeeResponseDTO created = employeeService.createEmployee(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(created, "Empleado creado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al crear empleado: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear empleado", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @Operation(summary = "Obtener empleado por ID",
            description = "Retorna los datos de un empleado activo por su ID. Requiere permiso VIEW_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleado encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Empleado no encontrado o eliminado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        try {
            EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
            return ok(ApiUtil.buildResponseWithDefaults(employee));

        } catch (OperationException e) {
            log.error("Error al obtener empleado con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener empleado con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('LIST_EMPLOYEE')")
    @Operation(summary = "Listar empleados con paginaciÃ³n",
            description = "Retorna una lista paginada de empleados activos. Filtra por estado si se envÃ­a el parÃ¡metro. Requiere permiso LIST_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir,
            @RequestParam(value = "status", required = false) EmployeeStatus status) {
        try {
            Pageable pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<EmployeeResponseDTO> result = employeeService.getAllEmployees(pageable, status);
            return ok(ApiUtil.buildResponseWithDefaults(result));

        } catch (OperationException e) {
            log.error("Error al listar empleados: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar empleados", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE')")
    @Operation(summary = "Actualizar empleado",
            description = "Actualiza los datos personales/laborales del empleado. No modifica el usuario asignado (usar /assign-user). Requiere permiso UPDATE_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleado actualizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validaciÃ³n", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<EmployeeResponseDTO>> updateEmployee(@PathVariable Long id,
                                                                            @Valid @RequestBody EmployeeUpdateRequestDTO request) {
        try {
            EmployeeResponseDTO updated = employeeService.updateEmployee(id, request);
            return ok(ApiUtil.buildSuccessResponse(updated, "Empleado actualizado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al actualizar empleado con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar empleado con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CHANGE_EMPLOYEE_STATUS')")
    @Operation(summary = "Cambiar estado de empleado",
            description = "Cambia el estado entre ACTIVE e INACTIVE. Para eliminar use el endpoint DELETE. Requiere permiso CHANGE_EMPLOYEE_STATUS.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado cambiado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Estado invÃ¡lido o empleado no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CHANGE_EMPLOYEE_STATUS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<EmployeeResponseDTO>> changeEmployeeStatus(@PathVariable Long id,
                                                                                  @Valid @RequestBody ChangeEmployeeStatusRequestDTO request) {
        try {
            EmployeeResponseDTO updated = employeeService.changeEmployeeStatus(id, request.getStatus());
            return ok(ApiUtil.buildSuccessResponse(updated, "Estado del empleado cambiado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al cambiar estado del empleado con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del empleado con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE')")
    @Operation(summary = "Eliminar empleado (lÃ³gico)",
            description = "EliminaciÃ³n lÃ³gica: marca deleted=true y estado ELIMINATION. Requiere permiso DELETE_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleado eliminado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Empleado no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Empleado eliminado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al eliminar empleado con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar empleado con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/assign-user")
    @PreAuthorize("hasAuthority('ASSIGN_USER_TO_EMPLOYEE')")
    @Operation(summary = "Asignar usuario al empleado",
            description = "Asigna una cuenta de usuario al empleado para acceso al sistema. Un usuario solo puede estar asignado a un empleado. Requiere permiso ASSIGN_USER_TO_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario asignado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Usuario no encontrado o ya asignado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso ASSIGN_USER_TO_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<EmployeeResponseDTO>> assignUserToEmployee(
            @PathVariable Long id,
            @Valid @RequestBody AssignUserToEmployeeRequestDTO request) {
        try {
            EmployeeResponseDTO updated = employeeService.assignUserToEmployee(id, request.getUserId());
            return ok(ApiUtil.buildSuccessResponse(updated, "Usuario asignado al empleado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al asignar usuario al empleado con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al asignar usuario al empleado con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/remove-user")
    @PreAuthorize("hasAuthority('REMOVE_USER_FROM_EMPLOYEE')")
    @Operation(summary = "Desvincular usuario del empleado",
            description = "Elimina la asociaciÃ³n entre el empleado y su cuenta de usuario. Requiere permiso REMOVE_USER_FROM_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario desvinculado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Empleado sin usuario asignado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso REMOVE_USER_FROM_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<EmployeeResponseDTO>> removeUserFromEmployee(@PathVariable Long id) {
        try {
            EmployeeResponseDTO updated = employeeService.removeUserFromEmployee(id);
            return ok(ApiUtil.buildSuccessResponse(updated, "Usuario desvinculado del empleado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al desvincular usuario del empleado con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al desvincular usuario del empleado con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users-available")
    @PreAuthorize("hasAuthority('ASSIGN_USER_TO_EMPLOYEE')")
    @Operation(summary = "Listar usuarios disponibles para asignar",
            description = "Retorna usuarios activos que aÃºn no tienen empleado asignado. Usado por el modal de asignaciÃ³n. Requiere permiso ASSIGN_USER_TO_EMPLOYEE.",
            tags = {"employees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso ASSIGN_USER_TO_EMPLOYEE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<UserResponseDto>>> getUsersAvailableForEmployee() {
        try {
            List<UserResponseDto> users = employeeService.getUsersAvailableForEmployee();
            return ok(ApiUtil.buildResponseWithDefaults(users));

        } catch (OperationException e) {
            log.error("Error al obtener usuarios disponibles: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener usuarios disponibles", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}


