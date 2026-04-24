package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para abrir un nuevo episodio clínico.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalEpisodeRequestDTO {

    @NotNull(message = "El ID del paciente es requerido")
    private Long patientId;

    @NotBlank(message = "El motivo de ingreso es requerido")
    private String reasonForAdmission;

    private LocalDate startDate;
}

