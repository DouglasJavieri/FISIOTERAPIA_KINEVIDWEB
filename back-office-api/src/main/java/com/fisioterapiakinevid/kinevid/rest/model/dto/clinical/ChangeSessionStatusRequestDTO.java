package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSessionStatusRequestDTO {
    @NotNull(message = "El estado es requerido")
    private SessionStatus status;
}