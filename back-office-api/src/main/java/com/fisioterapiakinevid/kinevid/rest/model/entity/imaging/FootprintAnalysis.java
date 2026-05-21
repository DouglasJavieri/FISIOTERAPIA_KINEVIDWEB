package com.fisioterapiakinevid.kinevid.rest.model.entity.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootprintType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Valoración de Hernández Corvo — Clasificación de huella plantar.
 * Registro GENÉRICO: se crea UNA SOLA VEZ por análisis de pisada.
 * El fisioterapeuta selecciona únicamente UNA clasificación del {@link FootprintType}
 * que representa al paciente en general. El tipo seleccionado se incluye
 * directamente en el reporte PDF generado.
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
@Table(name = "footprint_analysis",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_footprint_analysis_foot_analysis",
                columnNames = {"foot_analysis_id"}
        ))
public class FootprintAnalysis extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_FOOTPRINT_ANALYSIS_ID_GENERATOR", sequenceName = "SEQ_FOOTPRINT_ANALYSIS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FOOTPRINT_ANALYSIS_ID_GENERATOR")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foot_analysis_id", nullable = false)
    private FootAnalysis footAnalysis;

    @Enumerated(EnumType.STRING)
    @Column(name = "footprint_type", length = 50, nullable = false)
    private FootprintType footprintType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
