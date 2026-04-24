package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para cerrar un episodio clínico (dar de alta al paciente).
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CloseEpisodeRequestDTO {

    @NotBlank(message = "El motivo del alta es requerido")
    private String dischargeReason;
}

