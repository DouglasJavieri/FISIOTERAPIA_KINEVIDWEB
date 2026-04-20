package com.fisioterapiakinevid.kinevid.rest.controller.p;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.ChangePermissionStatusRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PagedPermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.p.PermissionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 03/03/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/permission")
@Tag(name = "permissions", description = "GestiÃ³n de permisos del sistema")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_PERMISSION')")
    @Operation(summary = "Crear un nuevo permiso",
            description = "Registra un nuevo permiso en el sistema. El nombre se normaliza a MAYÃšSCULAS. Requiere permiso CREATE_PERMISSION.",
            tags = {"permissions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permiso creado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "ValidaciÃ³n fallida o nombre duplicado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Fallo de autenticaciÃ³n", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CREATE_PERMISSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PermissionResponseDto>> createPermission(@Valid @RequestBody PermissionRequestDTO permissionDto) {
        try {
            PermissionResponseDto created = permissionService.createPermission(permissionDto);
            return ok(ApiUtil.buildResponseWithDefaults(created));
        } catch (OperationException e) {
            log.error("Error al crear permiso: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear permiso", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PERMISSION')")
    @Operation(summary = "Obtener permiso por ID",
            description = "Retorna los datos de un permiso activo por su ID. Requiere permiso READ_PERMISSION.",
            tags = {"permissions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permiso encontrado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Permiso no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Fallo de autenticaciÃ³n", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso READ_PERMISSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PermissionResponseDto>> getPermissionById(@PathVariable Long id) {
        try {
            PermissionResponseDto permission = permissionService.getPermissionById(id);
            return ok(ApiUtil.buildResponseWithDefaults(permission));
        } catch (OperationException e) {
            log.error("Error al obtener permiso con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener permiso con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('LIST_PERMISSION')")
    @Operation(summary = "Listar permisos con paginaciÃ³n",
            description = "Retorna permisos activos paginados. El filtro 'status' es opcional (ACTIVE, INACTIVE). Requiere permiso LIST_PERMISSION.",
            tags = {"permissions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Estado invÃ¡lido", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_PERMISSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<PagedPermissionResponseDto>>> getAllPermissions(@RequestParam("page") int page,
                                                                                            @RequestParam("size") int size,
                                                                                            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                                                                            @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir,
                                                                                            @RequestParam(required = false) String status) {
        try {
            Pageable pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<PagedPermissionResponseDto> usersPage = permissionService.findAllPermissions(status, pageable);

            return ok(ApiUtil.buildResponseWithDefaults(usersPage));
        } catch (OperationException e) {
            log.error("Error al listar permisos: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar permisos", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UPDATE_PERMISSION')")
    @Operation(summary = "Actualizar permiso",
            description = "Actualiza nombre y descripciÃ³n de un permiso existente. Requiere permiso UPDATE_PERMISSION.",
            tags = {"permissions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permiso actualizado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "ValidaciÃ³n fallida o nombre duplicado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_PERMISSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PermissionResponseDto>> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequestDTO permissionDto) {
        try {
            PermissionResponseDto updated = permissionService.updatePermission(id, permissionDto);
            return ok(ApiUtil.buildSuccessResponse(updated, "Permiso actualizado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al actualizar permiso con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar permiso con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_PERMISSION')")
    @Operation(summary = "Cambiar estado de permiso",
            description = "Cambia el estado entre ACTIVE e INACTIVE. Para eliminar use DELETE. No permite estado ELIMINATION. Requiere permiso UPDATE_PERMISSION.",
            tags = {"permissions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Estado invÃ¡lido o permiso no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_PERMISSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<PermissionResponseDto>> changeStatus(@PathVariable Long id, @Valid @RequestBody ChangePermissionStatusRequestDTO request) {
        try {
            PermissionResponseDto updated = permissionService.changeStatus(id, request.getStatus());
            return ok(ApiUtil.buildSuccessResponse(updated, "Estado del permiso cambiado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al cambiar estado del permiso con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del permiso con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_PERMISSION')")
    @Operation(summary = "Eliminar permiso (lÃ³gico)",
            description = "EliminaciÃ³n lÃ³gica del permiso. No permite eliminar permisos asignados a roles activos. Requiere permiso DELETE_PERMISSION.",
            tags = {"permissions"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permiso eliminado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Permiso no encontrado o en uso por roles", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso DELETE_PERMISSION", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Permiso eliminado exitosamente."));
        } catch (OperationException e) {
            log.error("Error al eliminar permiso con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar permiso con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
