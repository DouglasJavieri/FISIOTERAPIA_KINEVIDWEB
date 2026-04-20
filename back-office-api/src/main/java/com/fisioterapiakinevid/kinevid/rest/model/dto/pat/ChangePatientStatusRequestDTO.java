package com.fisioterapiakinevid.kinevid.rest.model.dto.pat;

import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Data
public class ChangePatientStatusRequestDTO {

    @NotNull(message = "El estado es requerido")
    private PatientStatus status;
}


