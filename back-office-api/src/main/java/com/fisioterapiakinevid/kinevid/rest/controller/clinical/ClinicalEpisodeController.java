package com.fisioterapiakinevid.kinevid.rest.controller.clinical;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalEpisodeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalEpisodeResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.CloseEpisodeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.EpisodeStatus;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.ClinicalEpisodeService;
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

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/clinical-episode")
@Tag(name = "clinical-episodes", description = "Gestión de episodios clínicos por paciente")
@RequiredArgsConstructor
public class ClinicalEpisodeController {

    private final ClinicalEpisodeService episodeService;


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_EPISODE')")
    @Operation(summary = "Abrir nuevo episodio clínico",
            description = "Crea un nuevo episodio clínico ACTIVO para un paciente. El paciente no debe tener un episodio activo. Requiere permiso CREATE_EPISODE.",
            tags = {"clinical-episodes"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Episodio creado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validación o regla de negocio", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_EPISODE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalEpisodeResponseDTO>> createEpisode(
            @Valid @RequestBody ClinicalEpisodeRequestDTO request) {
        try {
            ClinicalEpisodeResponseDTO created = episodeService.createEpisode(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(created, "Episodio clínico creado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al crear episodio: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear episodio", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/list")
    @PreAuthorize("hasAuthority('LIST_EPISODE')")
    @Operation(summary = "Listar todos los episodios (paginado con filtros)",
            description = "Lista global de episodios. Filtra por paciente y/o estado. Usada en la pantalla 'Episodios Clínicos' del menú. Requiere permiso LIST_EPISODE.",
            tags = {"clinical-episodes"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_EPISODE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<ClinicalEpisodeResponseDTO>>> getAllEpisodes(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sortBy", defaultValue = "episodeNumber") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir,
            @RequestParam(value = "patientId", required = false) Long patientId,
            @RequestParam(value = "status", required = false) EpisodeStatus status) {
        try {
            Pageable pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<ClinicalEpisodeResponseDTO> result = episodeService.getAllEpisodes(patientId, status, pageable);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar episodios: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar episodios", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('LIST_EPISODE')")
    @Operation(summary = "Listar episodios del paciente (paginado)",
            description = "Retorna todos los episodios clínicos de un paciente ordenados del más reciente al más antiguo. Requiere permiso LIST_EPISODE.",
            tags = {"clinical-episodes"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Paciente no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_EPISODE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<ClinicalEpisodeResponseDTO>>> getEpisodesByPatient(
            @PathVariable Long patientId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sortBy", defaultValue = "episodeNumber") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir) {
        try {
            Pageable pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<ClinicalEpisodeResponseDTO> result = episodeService.getEpisodesByPatientId(patientId, pageable);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar episodios del paciente ID={}: {}", patientId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar episodios del paciente ID={}", patientId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_EPISODE')")
    @Operation(summary = "Obtener episodio por ID",
            description = "Retorna el detalle de un episodio clínico. Requiere permiso VIEW_EPISODE.",
            tags = {"clinical-episodes"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Episodio encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Episodio no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_EPISODE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalEpisodeResponseDTO>> getEpisodeById(@PathVariable Long id) {
        try {
            ClinicalEpisodeResponseDTO episode = episodeService.getEpisodeById(id);
            return ok(ApiUtil.buildResponseWithDefaults(episode));
        } catch (OperationException e) {
            log.error("Error al obtener episodio ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener episodio ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAuthority('CLOSE_EPISODE')")
    @Operation(summary = "Cerrar episodio (dar de alta al paciente)",
            description = "Cierra el episodio activo y cambia el estado del paciente a DISCHARGE. Requiere permiso CLOSE_EPISODE.",
            tags = {"clinical-episodes"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Episodio cerrado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Episodio ya cerrado o no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CLOSE_EPISODE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalEpisodeResponseDTO>> closeEpisode(
            @PathVariable Long id,
            @Valid @RequestBody CloseEpisodeRequestDTO request) {
        try {
            ClinicalEpisodeResponseDTO closed = episodeService.closeEpisode(id, request);
            return ok(ApiUtil.buildSuccessResponse(closed, "Episodio cerrado. El paciente ha sido dado de alta."));
        } catch (OperationException e) {
            log.error("Error al cerrar episodio ID={}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cerrar episodio ID={}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/patient/{patientId}/reactivate")
    @PreAuthorize("hasAuthority('CREATE_EPISODE')")
    @Operation(summary = "Reactivar paciente y abrir nuevo episodio",
            description = "Reactiva un paciente en estado DISCHARGE y crea automáticamente un nuevo episodio clínico. Requiere permiso CREATE_EPISODE.",
            tags = {"clinical-episodes"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Paciente reactivado y episodio creado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "El paciente no está en estado ALTA o no fue encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_EPISODE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<ClinicalEpisodeResponseDTO>> reactivatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody ClinicalEpisodeRequestDTO request) {
        try {
            ClinicalEpisodeResponseDTO newEpisode = episodeService.reactivatePatient(patientId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(newEpisode, "Paciente reactivado y nuevo episodio clínico creado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al reactivar paciente ID={}: {}", patientId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al reactivar paciente ID={}", patientId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}



