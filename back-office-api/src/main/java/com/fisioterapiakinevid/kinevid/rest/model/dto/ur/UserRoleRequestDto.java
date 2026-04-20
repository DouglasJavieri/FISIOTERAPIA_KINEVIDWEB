package com.fisioterapiakinevid.kinevid.rest.model.dto.ur;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/04/2026
 * DTO de entrada para asignar un rol a un usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRequestDto {

    @NotNull(message = "El ID del usuario es requerido")
    private Long userId;

    @NotNull(message = "El ID del rol es requerido")
    private Long roleId;
}

