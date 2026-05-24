package com.fisioterapiakinevid.kinevid.rest.model.entity.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootSide;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.GaitType;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.TibialMalleolarRule;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Análisis biomecánico por pie (LEFT / RIGHT) dentro de un análisis de pisada.
 * Máximo dos registros por análisis — uno por cada lado del pie.
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
@Table(name = "biomechanical_analysis",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_biomechanical_analysis_foot",
                columnNames = {"foot_analysis_id", "foot_side"}
        ))
public class BiomechanicalAnalysis extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_BIOMECHANICAL_ANALYSIS_ID_GENERATOR", sequenceName = "SEQ_BIOMECHANICAL_ANALYSIS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BIOMECHANICAL_ANALYSIS_ID_GENERATOR")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "foot_side", length = 10, nullable = false)
    private FootSide footSide;

    @Enumerated(EnumType.STRING)
    @Column(name = "tibial_malleolar_rule", length = 50)
    private TibialMalleolarRule tibialMalleolarRule;

    @Column(name = "shoe_wear", columnDefinition = "TEXT")
    private String shoeWear;

    @Column(name = "tibia_palpation", columnDefinition = "TEXT")
    private String tibiaPalpation;

    @Enumerated(EnumType.STRING)
    @Column(name = "gait", length = 50)
    private GaitType gait;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foot_analysis_id", nullable = false)
    private FootAnalysis footAnalysis;
}
