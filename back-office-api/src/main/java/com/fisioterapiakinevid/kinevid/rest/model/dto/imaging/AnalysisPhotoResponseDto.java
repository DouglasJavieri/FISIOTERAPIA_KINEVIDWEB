package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import lombok.*;

/**
 * DTO de respuesta para AnalysisPhoto.
 * Incluye la URL pública de Cloudinary, el orden, el estado de selección
 * y si tiene anotaciones guardadas.
 * Sin lógica de mapeo — el mapeo se realiza en el ServiceImpl.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisPhotoResponseDto {

    private Long id;
    private Long footAnalysisId;
    private Integer photoOrder;
    private String photoUrl;
    private String storageFileId;
    private String annotationsJson;
    private Boolean isSelected;
    private Boolean hasAnnotations;
}
