package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.SessionService;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO de respuesta para un servicio aplicado en una sesión clínica.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 24/04/2026
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionServiceResponseDTO {

    private Long id;
    private Long sessionId;
    private Long medicalServiceId;
    private String medicalServiceName;
    private String medicalServiceCategory;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String notes;

    public SessionServiceResponseDTO(SessionService ss) {
        this.id = ss.getId();
        this.sessionId = ss.getSession().getId();
        this.medicalServiceId = ss.getMedicalService().getId();
        this.medicalServiceName = ss.getMedicalService().getName();
        this.medicalServiceCategory = ss.getMedicalService().getCategory() != null
                ? ss.getMedicalService().getCategory().getDescription() : null;
        this.quantity = ss.getQuantity();
        this.unitPrice = ss.getUnitPrice();
        this.totalPrice = (ss.getUnitPrice() != null && ss.getQuantity() != null)
                ? ss.getUnitPrice().multiply(BigDecimal.valueOf(ss.getQuantity()))
                : null;
        this.notes = ss.getNotes();
    }
}

