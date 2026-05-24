package com.fisioterapiakinevid.kinevid.rest.model.entity.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.DiagnosisType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Contenedor principal del análisis de pisada vinculado a una sesión clínica.
 * Relación 1:1 con ClinicalSession — máximo un análisis por sesión.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "foot_analysis", uniqueConstraints = @UniqueConstraint(name = "UK_foot_analysis_session", columnNames = {"clinical_session_id"}))
public class FootAnalysis extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_FOOT_ANALYSIS_ID_GENERATOR", sequenceName = "SEQ_FOOT_ANALYSIS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FOOT_ANALYSIS_ID_GENERATOR")
    private Long id;

    @Basic
    @Column(name = "analysis_date", nullable = false)
    private LocalDateTime analysisDate;

    @Basic
    @Column(name = "relevant_background", columnDefinition = "TEXT")
    private String relevantBackground;

    @Basic
    @Column(name = "kinesiological_evaluation", columnDefinition = "TEXT")
    private String kinesiologicalEvaluation;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "diagnosis", length = 50, nullable = false)
    private DiagnosisType diagnosis;

    @Basic
    @Column(name = "distance_intermaleolar", precision = 5, scale = 2)
    private BigDecimal distanceIntermaleolar;

    @Basic
    @Column(name = "distance_intercondylar", precision = 5, scale = 2)
    private BigDecimal distanceIntercondylar;

    @Basic
    @Column(name = "angle_left_internal", precision = 5, scale = 2)
    private BigDecimal angleLeftInternal;

    @Basic
    @Column(name = "angle_left_external", precision = 5, scale = 2)
    private BigDecimal angleLeftExternal;

    @Basic
    @Column(name = "angle_right_internal", precision = 5, scale = 2)
    private BigDecimal angleRightInternal;

    @Basic
    @Column(name = "angle_right_external", precision = 5, scale = 2)
    private BigDecimal angleRightExternal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_session_id", nullable = false)
    private ClinicalSession clinicalSession;
}
