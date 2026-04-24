package com.fisioterapiakinevid.kinevid.rest.model.entity.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.entity.pat.Patient;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.EpisodeStatus;
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
@Table(name = "clinical_episode")
public class ClinicalEpisode extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_CLINICAL_EPISODE_ID_GENERATOR", sequenceName = "SEQ_CLINICAL_EPISODE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CLINICAL_EPISODE_ID_GENERATOR")
    private Long id;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "reason_for_admission", length = 500, nullable = false)
    private String reasonForAdmission;

    @Column(name = "discharge_reason", length = 500)
    private String dischargeReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "episode_status", length = 20, nullable = false)
    private EpisodeStatus episodeStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "patient_id", nullable = false)
    private Patient patient;
}

