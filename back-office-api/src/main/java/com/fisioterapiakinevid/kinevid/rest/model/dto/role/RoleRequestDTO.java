package com.fisioterapiakinevid.kinevid.rest.model.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/04/2026
 * DTO de entrada para creación y actualización de roles.
 */
@Data
public class RoleRequestDTO {

    @NotBlank(message = "El nombre del rol es requerido")
    @Size(max = 75, message = "El nombre del rol no puede superar los 75 caracteres")
    @Pattern(
            regexp = "^[A-Z][A-Z0-9_]*$",
            message = "El nombre del rol debe estar en MAYÚSCULAS y solo puede contener letras, números y guiones bajos (ej: ROLE_MEDICO)"
    )
    private String name;

    @NotBlank(message = "La descripción del rol es requerida")
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;
}

