package com.fisioterapiakinevid.kinevid.rest.service.imaging;

import lombok.*;

/**
 * Resultado de una operación de subida al proveedor de almacenamiento.
 * Contiene la URL pública para mostrar la imagen y el ID del archivo
 * en el proveedor para poder eliminarlo posteriormente.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageResult {

    /**
     * URL pública del archivo en el proveedor (Cloudinary / S3).
     * Se guarda en {@code analysis_photo.photo_url}.
     */
    private String url;

    /**
     * ID único del archivo en el proveedor.
     * Se guarda en {@code analysis_photo.storage_file_id} para poder eliminarlo.
     */
    private String fileId;
}
