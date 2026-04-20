package com.fisioterapiakinevid.kinevid.rest.model.dto.emp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 07/04/2026
 * DTO para asignar un usuario al empleado (acción independiente).
 */
@Data
public class AssignUserToEmployeeRequestDTO {

    @NotNull(message = "El ID del usuario es requerido")
    private Long userId;
}

