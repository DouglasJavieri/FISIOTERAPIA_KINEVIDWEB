package com.fisioterapiakinevid.kinevid.rest.model.entity.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.entity.svc.MedicalService;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Tabla de relación N:M entre ClinicalSession y MedicalService.
 * Registra los servicios aplicados en cada sesión con cantidad, precio unitario y notas.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 24/04/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session_service",
        uniqueConstraints = @UniqueConstraint(name = "UK_session_service", columnNames = {"session_id", "medical_service_id"}))
public class SessionService extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_SESSION_SERVICE_ID_GENERATOR", sequenceName = "SEQ_SESSION_SERVICE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SESSION_SERVICE_ID_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ClinicalSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_service_id", nullable = false)
    private MedicalService medicalService;

    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "notes", length = 500)
    private String notes;
}

