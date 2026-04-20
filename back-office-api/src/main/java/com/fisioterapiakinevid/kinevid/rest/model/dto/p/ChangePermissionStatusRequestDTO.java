package com.fisioterapiakinevid.kinevid.rest.model.dto.p;

import com.fisioterapiakinevid.kinevid.rest.model.enums.p.PermissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/04/2026
 * DTO de entrada para el endpoint PATCH /{id}/status de permisos.
 */
@Data
public class ChangePermissionStatusRequestDTO {

    @NotNull(message = "El estado es requerido")
    private PermissionStatus status;
}


