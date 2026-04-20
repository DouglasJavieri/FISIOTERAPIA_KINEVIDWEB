package com.fisioterapiakinevid.kinevid.rest.controller.pat;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.*;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.pat.PatientService;
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
 * @created 15/04/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/patient")
@Tag(name = "patients", description = "GestiÃ³n de pacientes del consultorio")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_PATIENT')")
    @Operation(summary = "Registrar nuevo paciente",
            description = "Registra un nuevo paciente en el sistema. Requiere permiso CREATE_PATIENT.",
            tags = {"patients"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Paciente creado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validaciÃ³n", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_PATIENT", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PatientResponseDTO>> createPatient(@Valid @RequestBody PatientRequestDTO request) {
        try {
            PatientResponseDTO created = patientService.createPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(created, "Paciente registrado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al crear paciente: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear paciente", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_PATIENT')")
    @Operation(summary = "Obtener paciente por ID",
            description = "Retorna los datos de un paciente activo por su ID. Requiere permiso VIEW_PATIENT.",
            tags = {"patients"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paciente encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Paciente no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_PATIENT", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PatientResponseDTO>> getPatientById(@PathVariable Long id) {
        try {
            PatientResponseDTO patient = patientService.getPatientById(id);
            return ok(ApiUtil.buildResponseWithDefaults(patient));
        } catch (OperationException e) {
            log.error("Error al obtener paciente con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener paciente con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('LIST_PATIENT')")
    @Operation(summary = "Listar pacientes con paginaciÃ³n",
            description = "Retorna lista paginada de pacientes. Filtra por estado o bÃºsqueda por nombre/CI. Requiere permiso LIST_PATIENT.",
            tags = {"patients"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_PATIENT", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<PatientResponseDTO>>> getAllPatients(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sortBy", defaultValue = "paternalSurname") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC") Sort.Direction sortDir,
            @RequestParam(value = "status", required = false) PatientStatus status,
            @RequestParam(value = "search", required = false) String search) {
        try {
            Pageable pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<PatientResponseDTO> result = patientService.getAllPatients(pageable, status, search);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar pacientes: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar pacientes", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active-list")
    @PreAuthorize("hasAuthority('LIST_PATIENT')")
    @Operation(summary = "Listar todos los pacientes activos (sin paginaciÃ³n)",
            description = "Retorna la lista completa de pacientes activos para uso en selectores. Requiere permiso LIST_PATIENT.",
            tags = {"patients"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_PATIENT", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<PatientResponseDTO>>> getActivePatientsListForSelector() {
        try {
            List<PatientResponseDTO> patients = patientService.getActivePatientsList();
            return ok(ApiUtil.buildResponseWithDefaults(patients));
        } catch (OperationException e) {
            log.error("Error al obtener lista de pacientes activos: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener lista de pacientes activos", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UPDATE_PATIENT')")
    @Operation(summary = "Actualizar datos del paciente",
            description = "Actualiza los datos personales del paciente. Requiere permiso UPDATE_PATIENT.",
            tags = {"patients"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paciente actualizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validaciÃ³n", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_PATIENT", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PatientResponseDTO>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateRequestDTO request) {
        try {
            PatientResponseDTO updated = patientService.updatePatient(id, request);
            return ok(ApiUtil.buildSuccessResponse(updated, "Paciente actualizado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al actualizar paciente con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar paciente con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CHANGE_PATIENT_STATUS')")
    @Operation(summary = "Cambiar estado del paciente",
            description = "Cambia el estado entre ACTIVE, INACTIVE y DISCHARGE. Requiere permiso CHANGE_PATIENT_STATUS.",
            tags = {"patients"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado cambiado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Estado invÃ¡lido o paciente no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CHANGE_PATIENT_STATUS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PatientResponseDTO>> changePatientStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangePatientStatusRequestDTO request) {
        try {
            PatientResponseDTO updated = patientService.changePatientStatus(id, request.getStatus());
            return ok(ApiUtil.buildSuccessResponse(updated, "Estado del paciente cambiado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al cambiar estado del paciente con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del paciente con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_PATIENT')")
    @Operation(summary = "Eliminar paciente (lÃ³gico)",
            description = "EliminaciÃ³n lÃ³gica: marca deleted=true y estado ELIMINATION. Requiere permiso DELETE_PATIENT.",
            tags = {"patients"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paciente eliminado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Paciente no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_PATIENT", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deletePatient(@PathVariable Long id) {
        try {
            patientService.deletePatient(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Paciente eliminado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar paciente con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar paciente con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}


