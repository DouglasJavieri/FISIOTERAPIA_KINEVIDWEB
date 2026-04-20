package com.fisioterapiakinevid.kinevid.rest.model.dto.rp;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 * DTO de entrada para asignar/verificar permiso en un rol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionRequestDto {

    @NotNull(message = "El ID del rol es requerido")
    private Long roleId;

    @NotNull(message = "El ID del permiso es requerido")
    private Long permissionId;
}