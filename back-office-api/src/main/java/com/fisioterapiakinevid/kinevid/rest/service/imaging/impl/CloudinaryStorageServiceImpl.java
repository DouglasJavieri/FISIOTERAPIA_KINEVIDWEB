package com.fisioterapiakinevid.kinevid.rest.service.imaging.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.StorageResult;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Implementación de StorageService usando Cloudinary como proveedor.
 * Sube imágenes organizadas por carpetas jerárquicas y devuelve
 * la URL pública y el public_id para eliminación futura.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryStorageServiceImpl implements StorageService {

    private final Cloudinary cloudinary;

    @Override
    public StorageResult upload(MultipartFile file, String folderPath) throws OperationException {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",          folderPath,
                            "resource_type",   "image",
                            "use_filename",    true,
                            "unique_filename", true
                    )
            );

            String url    = (String) result.get("secure_url");
            String fileId = (String) result.get("public_id");

            log.info("Imagen subida a Cloudinary — fileId={}, folder={}", fileId, folderPath);
            return StorageResult.builder()
                    .url(url)
                    .fileId(fileId)
                    .build();

        } catch (Exception e) {
            log.error("Error al subir imagen a Cloudinary — folder={}: {}", folderPath, e.getMessage());
            throw new OperationException("No se pudo subir la imagen al servidor de almacenamiento. Intente nuevamente.");
        }
    }

    @Override
    public void delete(String storageFileId) throws OperationException {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    storageFileId,
                    ObjectUtils.asMap("resource_type", "image")
            );

            String status = (String) result.get("result");
            if (!"ok".equalsIgnoreCase(status)) {
                log.warn("Cloudinary reportó estado '{}' al eliminar fileId={}", status, storageFileId);
            } else {
                log.info("Imagen eliminada de Cloudinary — fileId={}", storageFileId);
            }

        } catch (Exception e) {
            log.error("Error al eliminar imagen de Cloudinary — fileId={}: {}", storageFileId, e.getMessage());
            throw new OperationException("No se pudo eliminar la imagen del servidor de almacenamiento.");
        }
    }
}
