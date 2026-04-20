package com.fisioterapiakinevid.kinevid.rest.controller.svc;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.svc.*;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.svc.MedicalServiceService;
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
@RequestMapping("/api/medical-service")
@Tag(name = "medical-services", description = "GestiÃ³n de servicios del consultorio de fisioterapia")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_SERVICE')")
    @Operation(summary = "Crear nuevo servicio",
            description = "Registra un nuevo servicio del consultorio. Requiere permiso CREATE_SERVICE.",
            tags = {"medical-services"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Servicio creado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validaciÃ³n o nombre duplicado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_SERVICE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<MedicalServiceResponseDTO>> createService(@Valid @RequestBody MedicalServiceRequestDTO request) {
        try {
            MedicalServiceResponseDTO created = medicalServiceService.createService(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiUtil.buildSuccessResponse(created, "Servicio creado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al crear servicio: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear servicio", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SERVICE')")
    @Operation(summary = "Obtener servicio por ID",
            description = "Retorna los datos de un servicio activo por su ID. Requiere permiso VIEW_SERVICE.",
            tags = {"medical-services"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Servicio encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Servicio no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_SERVICE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<MedicalServiceResponseDTO>> getServiceById(@PathVariable Long id) {
        try {
            MedicalServiceResponseDTO service = medicalServiceService.getServiceById(id);
            return ok(ApiUtil.buildResponseWithDefaults(service));
        } catch (OperationException e) {
            log.error("Error al obtener servicio con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener servicio con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('LIST_SERVICE')")
    @Operation(summary = "Listar servicios con paginaciÃ³n",
            description = "Retorna lista paginada de servicios. Filtra por estado o categorÃ­a. Requiere permiso LIST_SERVICE.",
            tags = {"medical-services"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_SERVICE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<MedicalServiceResponseDTO>>> getAllServices(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC") Sort.Direction sortDir,
            @RequestParam(value = "status", required = false) ServiceStatus status,
            @RequestParam(value = "category", required = false) ServiceCategory category) {
        try {
            Pageable pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<MedicalServiceResponseDTO> result = medicalServiceService.getAllServices(pageable, status, category);
            return ok(ApiUtil.buildResponseWithDefaults(result));
        } catch (OperationException e) {
            log.error("Error al listar servicios: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar servicios", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active-list")
    @PreAuthorize("hasAuthority('LIST_SERVICE')")
    @Operation(summary = "Listar servicios activos (sin paginaciÃ³n)",
            description = "Retorna todos los servicios activos para uso en selectores. Requiere permiso LIST_SERVICE.",
            tags = {"medical-services"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_SERVICE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<MedicalServiceResponseDTO>>> getActiveServicesListForSelector() {
        try {
            List<MedicalServiceResponseDTO> services = medicalServiceService.getActiveServicesList();
            return ok(ApiUtil.buildResponseWithDefaults(services));
        } catch (OperationException e) {
            log.error("Error al obtener lista de servicios activos: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener lista de servicios activos", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UPDATE_SERVICE')")
    @Operation(summary = "Actualizar servicio",
            description = "Actualiza los datos del servicio. Requiere permiso UPDATE_SERVICE.",
            tags = {"medical-services"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Servicio actualizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error de validaciÃ³n", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_SERVICE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<MedicalServiceResponseDTO>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody MedicalServiceUpdateRequestDTO request) {
        try {
            MedicalServiceResponseDTO updated = medicalServiceService.updateService(id, request);
            return ok(ApiUtil.buildSuccessResponse(updated, "Servicio actualizado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al actualizar servicio con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar servicio con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CHANGE_SERVICE_STATUS')")
    @Operation(summary = "Cambiar estado del servicio",
            description = "Cambia el estado entre ACTIVE e INACTIVE. Requiere permiso CHANGE_SERVICE_STATUS.",
            tags = {"medical-services"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado cambiado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Estado invÃ¡lido o servicio no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CHANGE_SERVICE_STATUS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<MedicalServiceResponseDTO>> changeServiceStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeMedicalServiceStatusRequestDTO request) {
        try {
            MedicalServiceResponseDTO updated = medicalServiceService.changeServiceStatus(id, request.getStatus());
            return ok(ApiUtil.buildSuccessResponse(updated, "Estado del servicio cambiado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al cambiar estado del servicio con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del servicio con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_SERVICE')")
    @Operation(summary = "Eliminar servicio (lÃ³gico)",
            description = "EliminaciÃ³n lÃ³gica: marca deleted=true y estado ELIMINATION. Requiere permiso DELETE_SERVICE.",
            tags = {"medical-services"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Servicio eliminado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Servicio no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_SERVICE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deleteService(@PathVariable Long id) {
        try {
            medicalServiceService.deleteService(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Servicio eliminado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar servicio con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar servicio con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}


