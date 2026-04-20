package com.fisioterapiakinevid.kinevid.rest.model.entity.svc;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medical_service")
public class MedicalService extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_MEDICAL_SERVICE_ID_GENERATOR", sequenceName = "SEQ_MEDICAL_SERVICE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEDICAL_SERVICE_ID_GENERATOR")
    private Long id;

    @Basic
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Basic
    @Column(name = "description", length = 300)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    private ServiceCategory category;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_status", length = 30, nullable = false)
    private ServiceStatus status;
}


