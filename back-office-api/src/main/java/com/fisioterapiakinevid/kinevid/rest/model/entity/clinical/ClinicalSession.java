package com.fisioterapiakinevid.kinevid.rest.model.entity.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.entity.emp.Employee;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clinical_session")
public class ClinicalSession extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_CLINICAL_SESSION_ID_GENERATOR", sequenceName = "SEQ_CLINICAL_SESSION_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CLINICAL_SESSION_ID_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private ClinicalEpisode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_number", nullable = false)
    private Integer sessionNumber;

    @Column(name = "reason_for_consultation", length = 500, nullable = false)
    private String reasonForConsultation;

    @Column(name = "relevant_background", columnDefinition = "TEXT")
    private String relevantBackground;

    @Column(name = "kinesiological_evaluation", columnDefinition = "TEXT")
    private String kinesiologicalEvaluation;

    @Column(name = "actual_illness_history", length = 1024)
    private String actualIllnessHistory;

    @Column(name = "gait", length = 1024)
    private String gait;

    @Column(name = "functional_tests", length = 1024)
    private String functionalTests;

    @Column(name = "complementary_exams", length = 1024)
    private String complementaryExams;

    @Column(name = "kinesiological_diagnosis", length = 1024)
    private String kinesiologicalDiagnosis;

    @Column(name = "treatment_applied", columnDefinition = "TEXT")
    private String treatmentApplied;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "evolution", columnDefinition = "TEXT")
    private String evolution;

    @Builder.Default
    @Column(name = "has_image_analysis", nullable = false)
    private Boolean hasImageAnalysis = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", length = 30, nullable = false)
    private SessionStatus sessionStatus;
}

