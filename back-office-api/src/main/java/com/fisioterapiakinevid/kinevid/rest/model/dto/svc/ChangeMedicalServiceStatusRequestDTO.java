package com.fisioterapiakinevid.kinevid.rest.model.dto.svc;

import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Data
public class ChangeMedicalServiceStatusRequestDTO {

    @NotNull(message = "El estado es requerido")
    private ServiceStatus status;
}


