package com.fisioterapiakinevid.kinevid.rest.model.entity.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Foto del análisis de pisada subida a Cloudinary.
 * Cada análisis puede tener un máximo de 6 fotos.
 * Almacena la URL pública, el ID en Cloudinary y las anotaciones
 * de trazos y ángulos dibujados sobre la imagen (canvas).
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
@Table(name = "analysis_photo",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_analysis_photo_order",
                columnNames = {"foot_analysis_id", "photo_order"}
        ))
public class AnalysisPhoto extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_ANALYSIS_PHOTO_ID_GENERATOR", sequenceName = "SEQ_ANALYSIS_PHOTO_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ANALYSIS_PHOTO_ID_GENERATOR")
    private Long id;

    @Column(name = "photo_order", nullable = false)
    private Integer photoOrder;

    @Column(name = "photo_url", length = 500, nullable = false)
    private String photoUrl;

    @Column(name = "storage_file_id", length = 200, nullable = false)
    private String storageFileId;

    @Column(name = "annotations_json", columnDefinition = "TEXT")
    private String annotationsJson;

    @Builder.Default
    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foot_analysis_id", nullable = false)
    private FootAnalysis footAnalysis;
}
