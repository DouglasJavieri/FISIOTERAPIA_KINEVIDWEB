package com.fisioterapiakinevid.kinevid.rest.model.dto.svc;

import com.fisioterapiakinevid.kinevid.rest.model.entity.svc.MedicalService;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceResponseDTO {

    private Long id;
    private String name;
    private String description;
    private ServiceCategory category;
    private String categoryDescription;
    private Integer durationMinutes;
    private BigDecimal price;
    private ServiceStatus status;

    public MedicalServiceResponseDTO(MedicalService service) {
        this.id                  = service.getId();
        this.name                = service.getName();
        this.description         = service.getDescription();
        this.category            = service.getCategory();
        this.categoryDescription = service.getCategory() != null ? service.getCategory().getDescription() : null;
        this.durationMinutes     = service.getDurationMinutes();
        this.price               = service.getPrice();
        this.status              = service.getStatus();
    }
}


