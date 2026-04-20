package com.fisioterapiakinevid.kinevid.rest.controller.rp;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.rp.RolePermissionRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.rp.RolePermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.rp.RolePermissionService;
import com.fisioterapiakinevid.kinevid.rest.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/role-permission")
@Tag(name = "role-permission", description = "AsignaciÃ³n y remociÃ³n de permisos a roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION_TO_ROLE')")
    @Operation(summary = "Listar permisos de un rol",
            description = "Retorna los permisos activamente asignados a un rol. Requiere permiso ASSIGN_PERMISSION_TO_ROLE.",
            tags = {"role-permission"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de permisos obtenida", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Rol no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso ASSIGN_PERMISSION_TO_ROLE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<PermissionResponseDto>>> getPermissionsByRole(
            @Parameter(description = "ID del rol") @PathVariable Long roleId) {
        try {
            List<PermissionResponseDto> permissions = rolePermissionService.getPermissionsByRoleId(roleId);
            return ok(ApiUtil.buildResponseWithDefaults(permissions));
        } catch (OperationException e) {
            log.error("Error al listar permisos del rol ID {}: {}", roleId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar permisos del rol ID {}", roleId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    // â”€â”€â”€ ASSIGN â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION_TO_ROLE')")
    @Operation(summary = "Asignar permiso a rol",
            description = "Crea la asignaciÃ³n entre un permiso y un rol. Ambos deben existir y no estar eliminados. Requiere permiso ASSIGN_PERMISSION_TO_ROLE.",
            tags = {"role-permission"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permiso asignado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Rol/Permiso no encontrado o ya asignado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso ASSIGN_PERMISSION_TO_ROLE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<RolePermissionResponseDto>> assignPermissionToRole(
            @Valid @RequestBody RolePermissionRequestDto request) {
        try {
            RolePermissionResponseDto assigned = rolePermissionService.assignPermissionToRole(request);
            return ok(ApiUtil.buildSuccessResponse(assigned, "Permiso asignado al rol exitosamente."));
        } catch (OperationException e) {
            log.error("Error al asignar permiso a rol: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al asignar permiso a rol", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    // â”€â”€â”€ REMOVE â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @DeleteMapping("/remove/{roleId}/{permissionId}")
    @PreAuthorize("hasAuthority('REMOVE_PERMISSION_FROM_ROLE')")
    @Operation(summary = "Remover permiso de rol",
            description = "EliminaciÃ³n lÃ³gica de la asignaciÃ³n rol-permiso. Requiere permiso REMOVE_PERMISSION_FROM_ROLE.",
            tags = {"role-permission"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permiso removido del rol exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Rol/Permiso no encontrado o asignaciÃ³n no existe", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso REMOVE_PERMISSION_FROM_ROLE", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> removePermissionFromRole(
            @Parameter(description = "ID del rol") @PathVariable Long roleId,
            @Parameter(description = "ID del permiso") @PathVariable Long permissionId) {
        try {
            rolePermissionService.removePermissionFromRole(roleId, permissionId);
            return ok(ApiUtil.buildSuccessResponse(true, "Permiso removido del rol exitosamente."));
        } catch (OperationException e) {
            log.error("Error al remover permiso del rol: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al remover permiso del rol", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}
