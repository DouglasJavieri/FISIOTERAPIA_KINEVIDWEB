package com.fisioterapiakinevid.kinevid.rest.controller.imaging;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.AnalysisPhotoResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.AnnotationsUpdateRequestDto;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.AnalysisPhotoService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Controlador REST para gestión de fotos del análisis de pisada.
 * El endpoint de subida usa multipart/form-data.
 * Los demás endpoints usan application/json.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/foot-analysis")
@Tag(name = "analysis-photos", description = "Gestión de fotos del análisis de pisada")
@RequiredArgsConstructor
public class AnalysisPhotoController {

    private final AnalysisPhotoService photoService;


    @PostMapping(value = "/{footAnalysisId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('MANAGE_ANALYSIS_PHOTOS')")
    @Operation(summary = "Subir foto al análisis",
            description = "Sube una imagen al análisis de pisada. Máximo 6 fotos por análisis. " +
                          "El orden (1-6) indica la posición de la foto. " +
                          "La sesión debe estar ABIERTA. Requiere permiso MANAGE_ANALYSIS_PHOTOS.",
            tags = {"analysis-photos"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Foto subida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Archivo vacío, límite alcanzado, orden inválido o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso MANAGE_ANALYSIS_PHOTOS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<AnalysisPhotoResponseDto>> uploadPhoto(
            @PathVariable Long footAnalysisId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("photoOrder") Integer photoOrder) {
        try {
            AnalysisPhotoResponseDto created = photoService.uploadPhoto(footAnalysisId, file, photoOrder);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(created, "Foto subida exitosamente."));
        } catch (OperationException e) {
            log.error("Error al subir foto al análisis ID={}: {}", footAnalysisId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al subir foto al análisis ID={}", footAnalysisId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{footAnalysisId}/photos")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Listar fotos del análisis",
            description = "Retorna todas las fotos activas de un análisis, ordenadas por posición. " +
                          "Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"analysis-photos"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Análisis no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<AnalysisPhotoResponseDto>>> getPhotosByAnalysisId(
            @PathVariable Long footAnalysisId) {
        try {
            List<AnalysisPhotoResponseDto> result = photoService.getPhotosByAnalysisId(footAnalysisId);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar fotos del análisis ID={}: {}", footAnalysisId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar fotos del análisis ID={}", footAnalysisId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/photos/{id}")
    @PreAuthorize("hasAuthority('VIEW_FOOT_ANALYSIS')")
    @Operation(summary = "Obtener foto por ID",
            description = "Retorna el detalle de una foto incluyendo sus anotaciones. " +
                          "Requiere permiso VIEW_FOOT_ANALYSIS.",
            tags = {"analysis-photos"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Foto encontrada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Foto no encontrada", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_FOOT_ANALYSIS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<AnalysisPhotoResponseDto>> getPhotoById(@PathVariable Long id) {
        try {
            AnalysisPhotoResponseDto result = photoService.getPhotoById(id);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al obtener foto ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener foto ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/photos/{id}/annotations")
    @PreAuthorize("hasAuthority('ANNOTATE_PHOTO')")
    @Operation(summary = "Guardar anotaciones de una foto",
            description = "Guarda o actualiza los trazos y ángulos dibujados en el canvas sobre la foto. " +
                          "La sesión debe estar ABIERTA. Requiere permiso ANNOTATE_PHOTO.",
            tags = {"analysis-photos"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Anotaciones guardadas exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Foto no encontrada o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso ANNOTATE_PHOTO", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<AnalysisPhotoResponseDto>> updateAnnotations(
            @PathVariable Long id,
            @Valid @RequestBody AnnotationsUpdateRequestDto request) {
        try {
            AnalysisPhotoResponseDto updated = photoService.updateAnnotations(id, request);
            return ok(ApiUtil.buildSuccessResponse(updated, "Anotaciones guardadas exitosamente."));
        } catch (OperationException e) {
            log.error("Error al guardar anotaciones de foto ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar anotaciones de foto ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/photos/{id}/select")
    @PreAuthorize("hasAuthority('MANAGE_ANALYSIS_PHOTOS')")
    @Operation(summary = "Alternar selección de foto para reporte",
            description = "Marca o desmarca una foto para incluirla en el reporte PDF. " +
                          "La sesión debe estar ABIERTA. Requiere permiso MANAGE_ANALYSIS_PHOTOS.",
            tags = {"analysis-photos"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selección actualizada", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Foto no encontrada o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso MANAGE_ANALYSIS_PHOTOS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<AnalysisPhotoResponseDto>> toggleSelection(@PathVariable Long id) {
        try {
            AnalysisPhotoResponseDto updated = photoService.toggleSelection(id);
            return ok(ApiUtil.buildSuccessResponse(updated, "Selección de foto actualizada."));
        } catch (OperationException e) {
            log.error("Error al alternar selección de foto ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al alternar selección de foto ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/photos/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ANALYSIS_PHOTOS')")
    @Operation(summary = "Eliminar foto del análisis",
            description = "Elimina la foto del proveedor de almacenamiento (Cloudinary) y aplica " +
                          "eliminación lógica en BD. La sesión debe estar ABIERTA. " +
                          "Requiere permiso MANAGE_ANALYSIS_PHOTOS.",
            tags = {"analysis-photos"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Foto eliminada exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Foto no encontrada o sesión no OPEN", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso MANAGE_ANALYSIS_PHOTOS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deletePhoto(@PathVariable Long id) {
        try {
            photoService.deletePhoto(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Foto eliminada exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar foto ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar foto ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
