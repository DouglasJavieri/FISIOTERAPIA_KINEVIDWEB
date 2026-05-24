package com.fisioterapiakinevid.kinevid.rest.controller.imaging;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.BiomechanicalAnalysisRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.BiomechanicalAnalysisResponseDto;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.BiomechanicalAnalysisService;
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

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Controlador REST para gestión del análisis biomecánico por pie (LEFT / RIGHT).
 * Endpoints anidados bajo /api/foot-analysis para mantener la jerarquía.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/foot-analysis")
@Tag(name = "biomechanical-analysis", description = "Análisis biomecánico por pie dentro del análisis de pisada")
@RequiredArgsConstructor
public class BiomechanicalAnalysisController {

    private final BiomechanicalAnalysisService biomechanicalService;


    @PostMapping("/{footAnalysisId}/biomechanical")
    @PreAuthorize("hasAuthority('UPDATE_FOOT_ANALYSIS')")
    @Operation(summary = "Crear o actualizar análisis biomecánico de un pie",
            description = "Upsert: si ya existe un registro para el pie indicado (LEFT/RIGHT), se actualiza. " +
                          "Si fue eliminado, se reactiva. Si no existe, se crea. " +
                          "La sesión debe estar ABIERTA. Requiere permiso UPDATE_FOOT_ANALYSIS.",
            tags = {"biomechanical-analysis"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Análisis biomecánico creado/actualizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validación o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<BiomechanicalAnalysisResponseDto>> saveOrUpdate(
            @PathVariable Long footAnalysisId,
            @Valid @RequestBody BiomechanicalAnalysisRequestDto request) {
        try {
            BiomechanicalAnalysisResponseDto result = biomechanicalService.saveOrUpdate(footAnalysisId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(result, "Análisis biomecánico guardado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al guardar análisis biomecánico (análisis ID={}): {}", footAnalysisId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar análisis biomecánico (análisis ID={})", footAnalysisId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{footAnalysisId}/biomechanical")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Listar análisis biomecánicos de un análisis de pisada",
            description = "Retorna los análisis biomecánicos activos (máx 2: LEFT y RIGHT). " +
                          "Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"biomechanical-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Análisis de pisada no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<BiomechanicalAnalysisResponseDto>>> getByFootAnalysisId(
            @PathVariable Long footAnalysisId) {
        try {
            List<BiomechanicalAnalysisResponseDto> result = biomechanicalService.getByFootAnalysisId(footAnalysisId);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar análisis biomecánicos (análisis ID={}): {}", footAnalysisId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar análisis biomecánicos (análisis ID={})", footAnalysisId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/biomechanical/{id}")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Obtener análisis biomecánico por ID",
            description = "Retorna el detalle de un análisis biomecánico específico. " +
                          "Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"biomechanical-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Análisis biomecánico encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Análisis biomecánico no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<BiomechanicalAnalysisResponseDto>> getById(@PathVariable Long id) {
        try {
            BiomechanicalAnalysisResponseDto result = biomechanicalService.getById(id);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al obtener análisis biomecánico ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener análisis biomecánico ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/biomechanical/{id}")
    @PreAuthorize("hasAuthority('DELETE_FOOT_ANALYSIS')")
    @Operation(summary = "Eliminar análisis biomecánico (lógico)",
            description = "Eliminación lógica de un análisis biomecánico. " +
                          "La sesión debe estar ABIERTA. Requiere permiso DELETE_FOOT_ANALYSIS.",
            tags = {"biomechanical-analysis"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Análisis biomecánico eliminado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "No encontrado o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> delete(@PathVariable Long id) {
        try {
            biomechanicalService.delete(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Análisis biomecánico eliminado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar análisis biomecánico ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar análisis biomecánico ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
