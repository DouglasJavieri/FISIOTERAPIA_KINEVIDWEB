package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para crear una nueva sesión clínica dentro de un episodio.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalSessionRequestDTO {

    @NotNull(message = "El ID del episodio es requerido")
    private Long episodeId;

    @NotNull(message = "El ID del fisioterapeuta es requerido")
    private Long employeeId;

    private LocalDate sessionDate; // Si null, se usa la fecha actual

    @NotBlank(message = "El motivo de consulta es requerido")
    private String reasonForConsultation;

    private String relevantBackground;
}

