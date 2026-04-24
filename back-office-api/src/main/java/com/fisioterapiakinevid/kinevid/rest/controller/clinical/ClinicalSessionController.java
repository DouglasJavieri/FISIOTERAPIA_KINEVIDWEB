package com.fisioterapiakinevid.kinevid.rest.controller.clinical;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ChangeSessionStatusRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.SessionServiceRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.SessionServiceResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.ClinicalSessionService;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.SessionServiceManagementService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/clinical-session")
@Tag(name = "clinical-sessions", description = "Gestión de sesiones clínicas por episodio")
@RequiredArgsConstructor
public class ClinicalSessionController {

    private final ClinicalSessionService sessionService;
    private final SessionServiceManagementService sessionServiceManagement;


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_CLINICAL_SESSION')")
    @Operation(summary = "Crear nueva sesión clínica",
            description = "Crea una sesión OPEN dentro de un episodio ACTIVO. Requiere permiso CREATE_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sesión creada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validación o episodio no activo", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalSessionResponseDTO>> createSession(
            @Valid @RequestBody ClinicalSessionRequestDTO request) {
        try {
            ClinicalSessionResponseDTO created = sessionService.createSession(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(created, "Sesión clínica creada exitosamente."));
        } catch (OperationException e) {
            log.error("Error al crear sesión: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear sesión", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/episode/{episodeId}")
    @PreAuthorize("hasAuthority('LIST_CLINICAL_SESSION')")
    @Operation(summary = "Listar sesiones de un episodio (paginado)",
            description = "Retorna todas las sesiones de un episodio. Opcionalmente filtra por estado. Requiere permiso LIST_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Episodio no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<ClinicalSessionResponseDTO>>> getSessionsByEpisode(
            @PathVariable Long episodeId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sortBy", defaultValue = "sessionNumber") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir,
            @RequestParam(value = "status", required = false) SessionStatus status) {
        try {
            var pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<ClinicalSessionResponseDTO> result = sessionService.getSessionsByEpisodeId(episodeId, status, pageable);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar sesiones del episodio ID={}: {}", episodeId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar sesiones del episodio ID={}", episodeId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_SESSION')")
    @Operation(summary = "Obtener sesión por ID",
            description = "Retorna el detalle completo de una sesión clínica. Requiere permiso VIEW_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sesión encontrada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Sesión no encontrada", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalSessionResponseDTO>> getSessionById(@PathVariable Long id) {
        try {
            ClinicalSessionResponseDTO session = sessionService.getSessionById(id);
            return ok(ApiUtil.buildResponseWithDefaults(session));
        } catch (OperationException e) {
            log.error("Error al obtener sesión ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener sesión ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CLINICAL_SESSION')")
    @Operation(summary = "Actualizar sesión clínica",
            description = "Actualiza los datos clínicos de una sesión OPEN. Requiere permiso UPDATE_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sesión actualizada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Sesión no encontrada o no está OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalSessionResponseDTO>> updateSession(
            @PathVariable Long id,
            @RequestBody ClinicalSessionUpdateRequestDTO request) {
        try {
            ClinicalSessionResponseDTO updated = sessionService.updateSession(id, request);
            return ok(ApiUtil.buildSuccessResponse(updated, "Sesión clínica actualizada exitosamente."));
        } catch (OperationException e) {
            log.error("Error al actualizar sesión ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar sesión ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_CLINICAL_SESSION')")
    @Operation(summary = "Cambiar estado de sesión",
            description = "Cambia el estado de la sesión (OPEN → CLOSED / CANCELLED). Requiere permiso UPDATE_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado cambiado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Sesión no encontrada o transición inválida", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalSessionResponseDTO>> changeSessionStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeSessionStatusRequestDTO request) {
        try {
            ClinicalSessionResponseDTO updated = sessionService.changeSessionStatus(id, request.getStatus());
            return ok(ApiUtil.buildSuccessResponse(updated, "Estado de la sesión cambiado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al cambiar estado de sesión ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado de sesión ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_CLINICAL_SESSION')")
    @Operation(summary = "Eliminar sesión (lógico)",
            description = "Eliminación lógica de una sesión. Solo si no está CERRADA. Requiere permiso DELETE_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sesión eliminada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Sesión no encontrada o está CERRADA", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deleteSession(@PathVariable Long id) {
        try {
            sessionService.deleteSession(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Sesión clínica eliminada exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar sesión ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar sesión ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    // ─────────────────────────────────────────────────────────────────────────
    //  SERVICIOS APLICADOS EN SESIÓN (N:M  session ↔ medical_service)
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{sessionId}/services")
    @PreAuthorize("hasAuthority('UPDATE_CLINICAL_SESSION')")
    @Operation(summary = "Agregar / actualizar servicio en sesión",
            description = "Agrega un servicio médico a la sesión. Si ya existía, actualiza cantidad y notas (upsert). La sesión debe estar ABIERTA. Requiere permiso UPDATE_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Servicio agregado/actualizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validación o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<SessionServiceResponseDTO>> addServiceToSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody SessionServiceRequestDTO request) {
        try {
            SessionServiceResponseDTO result = sessionServiceManagement.addServiceToSession(sessionId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(result, "Servicio agregado a la sesión exitosamente."));
        } catch (OperationException e) {
            log.error("Error al agregar servicio a sesión ID={}: {}", sessionId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al agregar servicio a sesión ID={}", sessionId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{sessionId}/services")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_SESSION')")
    @Operation(summary = "Listar servicios de una sesión",
            description = "Retorna todos los servicios médicos aplicados en una sesión. Requiere permiso VIEW_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Sesión no encontrada", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<SessionServiceResponseDTO>>> getServicesBySession(
            @PathVariable Long sessionId) {
        try {
            List<SessionServiceResponseDTO> result = sessionServiceManagement.getServicesBySession(sessionId);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar servicios de sesión ID={}: {}", sessionId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar servicios de sesión ID={}", sessionId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/services/{sessionServiceId}")
    @PreAuthorize("hasAuthority('UPDATE_CLINICAL_SESSION')")
    @Operation(summary = "Eliminar servicio de sesión (lógico)",
            description = "Elimina lógicamente un servicio de una sesión. Solo si la sesión sigue ABIERTA. Requiere permiso UPDATE_CLINICAL_SESSION.",
            tags = {"clinical-sessions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Servicio eliminado de la sesión", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Registro no encontrado o sesión no está OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_CLINICAL_SESSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> removeServiceFromSession(@PathVariable Long sessionServiceId) {
        try {
            sessionServiceManagement.removeServiceFromSession(sessionServiceId);
            return ok(ApiUtil.buildSuccessResponse(true, "Servicio eliminado de la sesión exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar servicio de sesión ID={}: {}", sessionServiceId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar servicio de sesión ID={}", sessionServiceId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}

