package com.fisioterapiakinevid.kinevid.rest.controller.u;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.ChangeUserStatusRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.u.UserService;
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
 * @created 17/02/2026
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "users", description = "GestiÃ³n de usuarios del sistema")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Crear un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema. Requiere permiso CREATE_USER.",
            tags = {"users"},
            responses = {
                    @ApiResponse(description = "OperaciÃ³n satisfactorio", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Registro creado", responseCode = "201", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Fallo de autentificaciÃ³n", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Acceso Denegado", content = @Content(schema = @Schema(hidden = true))),
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDTO user) {
        try {
            UserResponseDto createdUser = userService.createUser(user);
            ResponseBody<UserResponseDto> response = ApiUtil.buildSuccessResponse(
                    createdUser, "Usuario creado exitosamente.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (OperationException e) {
            log.error("Error al crear usuario: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear usuario", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    @Operation(summary = "Obtener usuario por ID",
            description = "Retorna los datos de un usuario activo por su ID. Requiere permiso VIEW_USER.",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Usuario no encontrado o eliminado",
                            content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso VIEW_USER",
                            content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<UserResponseDto>> getUserById(
            @PathVariable Long id) {
        try {
            UserResponseDto user = userService.getUserById(id);
            return ok(ApiUtil.buildResponseWithDefaults(user));

        } catch (OperationException e) {
            log.error("Error al obtener usuario con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener usuario con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/list")
    @PreAuthorize("hasAuthority('LIST_USER')")
    @Operation(summary = "Listar usuarios con paginaciÃ³n",
            description = "Retorna una lista paginada de usuarios activos. Requiere permiso LIST_USER.",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso LIST_USER", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Page<UserResponseDto>>> getAllUsers(@RequestParam("page") int page,
                                                                           @RequestParam("size") int size,
                                                                           @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                                                           @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir) {
        try {
            Pageable pageable = ApiUtil.buildPageableWithSort(page, size, sortBy, sortDir);
            Page<UserResponseDto> usersPage = userService.getAllUsers(pageable);

            return ok(ApiUtil.buildResponseWithDefaults(usersPage));
        } catch (OperationException e) {
            log.error("Error al listar usuarios: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al listar usuarios", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @Operation(summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente. La contraseÃ±a es opcional: si no se envÃ­a, no se modifica. Requiere permiso UPDATE_USER.",
            tags = {"users"},
            responses = {
                    @ApiResponse(description = "Usuario actualizado con Ã©xito", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error en la validaciÃ³n de datos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Fallo de autentificaciÃ³n", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<UserResponseDto>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDTO user) {
        try {
            UserResponseDto updatedUser = userService.updateUser(id, user);
            return ok(ApiUtil.buildSuccessResponse(updatedUser, "Usuario actualizado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al actualizar usuario con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar usuario con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CHANGE_USER_STATUS')")
    @Operation(summary = "Cambiar estado de usuario",
            description = "Cambia el estado de un usuario entre ACTIVE e INACTIVE. " +
                    "Para eliminar use el endpoint DELETE. Requiere permiso CHANGE_USER_STATUS.",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Estado invÃ¡lido o usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Sin permiso CHANGE_USER_STATUS", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<UserResponseDto>> changeUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUserStatusRequestDTO request) {
        try {
            UserResponseDto updatedUser = userService.changeUserStatus(id, request.getStatus());
            return ok(ApiUtil.buildSuccessResponse(
                    updatedUser, "Estado del usuario cambiado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al cambiar estado del usuario con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del usuario con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    @Operation(summary = "Eliminar usuario (lÃ³gico)",
            description = "EliminaciÃ³n lÃ³gica: marca deleted=true y estado ELIMINATION. Requiere permiso DELETE_USER.",
            tags = {"users"},
            responses = {
                    @ApiResponse(description = "Usuario eliminado con Ã©xito", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error en la validaciÃ³n de datos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Fallo de autentificaciÃ³n", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Boolean>> deleteUser(
            @PathVariable(value = "id") Long id) {
        try {
            userService.deleteUser(id);
            return ok(ApiUtil.buildSuccessResponse(true, "Usuario eliminado exitosamente."));

        } catch (OperationException e) {
            log.error("Error al eliminar usuario con ID {}: {}", id, e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar usuario con ID {}", id, e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }
}

