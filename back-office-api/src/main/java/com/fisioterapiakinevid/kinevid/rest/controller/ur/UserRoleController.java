package com.fisioterapiakinevid.kinevid.rest.controller.ur;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.RoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.ur.UserRoleRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.ur.UserRoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.ur.UserRoleService;
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
 * @created 01/04/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/user-role")
@Tag(name = "user-role", description = "AsignaciÃ³n y remociÃ³n de roles a usuarios")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    @Operation(summary = "Listar roles de un usuario",
            description = "Retorna los roles activamente asignados a un usuario. Requiere permiso VIEW_USER.",
            tags = {"user-role"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_USER", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<List<RoleResponseDto>>> getRolesByUser(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        try {
            List<RoleResponseDto> roles = userRoleService.getRolesByUserId(userId);
            return ok(ApiUtil.buildResponseWithDefaults(roles));
        } catch (OperationException e) {
            log.error("Error al listar roles del usuario ID {}: {}", userId, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar roles del usuario ID {}", userId, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @Operation(summary = "Asignar rol a usuario",
            description = "Crea la asignaciÃ³n entre un rol y un usuario. Ambos deben existir y no estar eliminados. Requiere permiso UPDATE_USER.",
            tags = {"user-role"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol asignado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Usuario/Rol no encontrado o ya asignado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_USER", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<UserRoleResponseDto>> assignRoleToUser(
            @Valid @RequestBody UserRoleRequestDto request) {
        try {
            UserRoleResponseDto assigned = userRoleService.assignRoleToUser(request);
            return ok(ApiUtil.buildSuccessResponse(assigned, "Rol asignado al usuario exitosamente."));
        } catch (OperationException e) {
            log.error("Error al asignar rol a usuario: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al asignar rol a usuario", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove/{userId}/{roleId}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @Operation(summary = "Remover rol de usuario",
            description = "EliminaciÃ³n lÃ³gica de la asignaciÃ³n usuario-rol. Requiere permiso UPDATE_USER.",
            tags = {"user-role"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol removido del usuario exitosamente",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Usuario/Rol no encontrado o asignaciÃ³n no existe",
                            content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso UPDATE_USER",
                            content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> removeRoleFromUser(
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Parameter(description = "ID del rol") @PathVariable Long roleId) {
        try {
            userRoleService.removeRoleFromUser(userId, roleId);
            return ok(ApiUtil.buildSuccessResponse(true, "Rol removido del usuario exitosamente."));
        } catch (OperationException e) {
            log.error("Error al remover rol del usuario: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al remover rol del usuario", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}


