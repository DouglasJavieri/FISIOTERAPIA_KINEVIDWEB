package com.fisioterapiakinevid.kinevid.rest.controller.imaging;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.FootAnalysisService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Controlador REST para gestión de análisis de pisada.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/foot-analysis")
@Tag(name = "foot-analysis", description = "Gestión de análisis de pisada por sesión clínica")
@RequiredArgsConstructor
public class FootAnalysisController {

    private final FootAnalysisService footAnalysisService;


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_FOOT_ANALYSIS')")
    @Operation(summary = "Crear análisis de pisada",
            description = "Crea un nuevo análisis de pisada para una sesión clínica ABIERTA. Máximo uno por sesión. Requiere permiso CREATE_FOOT_ANALYSIS.",
            tags = {"foot-analysis"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Análisis creado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validación, sesión no OPEN o ya existe análisis", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<FootAnalysisResponseDTO>> createFootAnalysis(
            @Valid @RequestBody FootAnalysisRequestDTO request) {
        try {
            FootAnalysisResponseDTO created = footAnalysisService.createFootAnalysis(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(created, "Análisis de pisada creado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al crear análisis de pisada: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear análisis de pisada", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Obtener análisis por ID",
            description = "Retorna el detalle completo de un análisis de pisada con contexto clínico. Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"foot-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Análisis encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Análisis no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<FootAnalysisResponseDTO>> getFootAnalysisById(@PathVariable Long id) {
        try {
            FootAnalysisResponseDTO result = footAnalysisService.getFootAnalysisById(id);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al obtener análisis de pisada ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener análisis de pisada ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Obtener análisis por sesión clínica",
            description = "Retorna el análisis de pisada asociado a una sesión clínica (relación 1:1). Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"foot-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Análisis encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "No existe análisis para esta sesión", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<FootAnalysisResponseDTO>> getFootAnalysisBySessionId(
            @PathVariable Long sessionId) {
        try {
            FootAnalysisResponseDTO result = footAnalysisService.getFootAnalysisBySessionId(sessionId);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al obtener análisis por sesión ID={}: {}", sessionId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener análisis por sesión ID={}", sessionId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UPDATE_FOOT_ANALYSIS')")
    @Operation(summary = "Actualizar análisis de pisada",
            description = "Actualiza los datos de un análisis existente. Solo si la sesión está ABIERTA. Requiere permiso UPDATE_FOOT_ANALYSIS.",
            tags = {"foot-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Análisis actualizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Análisis no encontrado o sesión no está OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<FootAnalysisResponseDTO>> updateFootAnalysis(
            @PathVariable Long id,
            @RequestBody FootAnalysisUpdateRequestDTO request) {
        try {
            FootAnalysisResponseDTO updated = footAnalysisService.updateFootAnalysis(id, request);
            return ok(ApiUtil.buildSuccessResponse(updated, "Análisis de pisada actualizado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al actualizar análisis de pisada ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar análisis de pisada ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_FOOT_ANALYSIS')")
    @Operation(summary = "Eliminar análisis de pisada (lógico)",
            description = "Eliminación lógica de un análisis. Solo si la sesión está ABIERTA. Requiere permiso DELETE_FOOT_ANALYSIS.",
            tags = {"foot-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Análisis eliminado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Análisis no encontrado o sesión no está OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deleteFootAnalysis(@PathVariable Long id) {
        try {
            footAnalysisService.deleteFootAnalysis(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Análisis de pisada eliminado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar análisis de pisada ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar análisis de pisada ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
