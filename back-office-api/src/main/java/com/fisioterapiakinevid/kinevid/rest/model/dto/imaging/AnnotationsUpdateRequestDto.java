package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para guardar o actualizar las anotaciones (trazos y ángulos)
 * dibujadas sobre una foto del análisis de pisada.
 * El JSON de anotaciones es generado por el canvas HTML5 del frontend.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationsUpdateRequestDto {

    @NotBlank(message = "Las anotaciones no pueden estar vacías")
    private String annotationsJson;
}
