package com.fisioterapiakinevid.kinevid.rest.controller.imaging;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootprintAnalysisRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootprintAnalysisResponseDto;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.FootprintAnalysisService;
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
 * Controlador REST para gestión de la Valoración de Hernández Corvo.
 * Endpoints anidados bajo /api/foot-analysis para mantener la jerarquía.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/foot-analysis")
@Tag(name = "footprint-analysis", description = "Valoración de Hernández Corvo — Clasificación de huella plantar")
@RequiredArgsConstructor
public class FootprintAnalysisController {

    private final FootprintAnalysisService footprintService;


    @PostMapping("/{footAnalysisId}/footprint")
    @PreAuthorize("hasAuthority('UPDATE_FOOT_ANALYSIS')")
    @Operation(summary = "Crear o actualizar la Valoración de Hernández Corvo",
            description = "Upsert: si ya existe una valoración para el análisis, se actualiza. " +
                          "Si fue eliminada, se reactiva. Si no existe, se crea. " +
                          "La sesión debe estar ABIERTA. Requiere permiso UPDATE_FOOT_ANALYSIS.",
            tags = {"footprint-analysis"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Valoración creada/actualizada exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validación, tipo inválido o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<FootprintAnalysisResponseDto>> saveOrUpdate(
            @PathVariable Long footAnalysisId,
            @Valid @RequestBody FootprintAnalysisRequestDto request) {
        try {
            FootprintAnalysisResponseDto result = footprintService.saveOrUpdate(footAnalysisId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(result, "Valoración de huella plantar guardada exitosamente."));
        } catch (OperationException e) {
            log.error("Error al guardar valoración Hernández Corvo (análisis ID={}): {}", footAnalysisId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar valoración Hernández Corvo (análisis ID={})", footAnalysisId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{footAnalysisId}/footprint")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Obtener la Valoración de Hernández Corvo de un análisis",
            description = "Retorna la valoración de huella plantar activa del análisis. " +
                          "Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"footprint-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valoración encontrada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Análisis no encontrado o sin valoración registrada", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<FootprintAnalysisResponseDto>> getByFootAnalysisId(
            @PathVariable Long footAnalysisId) {
        try {
            FootprintAnalysisResponseDto result = footprintService.getByFootAnalysisId(footAnalysisId);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al obtener valoración Hernández Corvo (análisis ID={}): {}", footAnalysisId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener valoración Hernández Corvo (análisis ID={})", footAnalysisId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/footprint/{id}")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Obtener valoración de huella plantar por ID",
            description = "Retorna el detalle de una valoración específica por su ID. " +
                          "Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"footprint-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valoración encontrada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Valoración no encontrada", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<FootprintAnalysisResponseDto>> getById(@PathVariable Long id) {
        try {
            FootprintAnalysisResponseDto result = footprintService.getById(id);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al obtener valoración Hernández Corvo ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener valoración Hernández Corvo ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/footprint/{id}")
    @PreAuthorize("hasAuthority('DELETE_FOOT_ANALYSIS')")
    @Operation(summary = "Eliminar valoración de huella plantar (lógico)",
            description = "Eliminación lógica de la Valoración de Hernández Corvo. " +
                          "La sesión debe estar ABIERTA. Requiere permiso DELETE_FOOT_ANALYSIS.",
            tags = {"footprint-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valoración eliminada exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "No encontrada o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> delete(@PathVariable Long id) {
        try {
            footprintService.delete(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Valoración de huella plantar eliminada exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar valoración Hernández Corvo ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar valoración Hernández Corvo ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
