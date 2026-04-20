package com.fisioterapiakinevid.kinevid.rest.model.dto.p;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/04/2026
 * DTO de entrada para creación y actualización de permisos.
 */
@Data
public class PermissionRequestDTO {

    @NotBlank(message = "El nombre del permiso es requerido")
    @Size(max = 60, message = "El nombre del permiso no puede superar los 60 caracteres")
    @Pattern(
            regexp = "^[A-Z][A-Z0-9_]*$",
            message = "El nombre del permiso debe estar en MAYÚSCULAS y solo puede contener letras, números y guiones bajos (ej: CREATE_USER)"
    )
    private String name;

    @NotBlank(message = "La descripción del permiso es requerida")
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;
}

