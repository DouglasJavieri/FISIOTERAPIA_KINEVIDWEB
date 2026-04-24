package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para agregar o actualizar un servicio dentro de una sesión clínica.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 24/04/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SessionServiceRequestDTO {

    @NotNull(message = "El ID del servicio médico es requerido")
    private Long medicalServiceId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    private BigDecimal unitPrice;

    private String notes;
}

