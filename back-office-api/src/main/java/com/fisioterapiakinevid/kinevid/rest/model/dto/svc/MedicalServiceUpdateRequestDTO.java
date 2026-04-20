package com.fisioterapiakinevid.kinevid.rest.model.dto.svc;

import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Data
public class MedicalServiceUpdateRequestDTO {

    @NotBlank(message = "El nombre del servicio es requerido")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @Size(max = 300, message = "La descripciÃ³n no puede superar los 300 caracteres")
    private String description;

    @NotNull(message = "La categorÃ­a del servicio es requerida")
    private ServiceCategory category;

    @Min(value = 1, message = "La duraciÃ³n debe ser al menos 1 minuto")
    private Integer durationMinutes;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio no puede tener mÃ¡s de 8 dÃ­gitos enteros y 2 decimales")
    private BigDecimal price;
}


