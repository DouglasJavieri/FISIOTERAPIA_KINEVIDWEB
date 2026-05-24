package com.fisioterapiakinevid.kinevid.rest.service.imaging;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrato de almacenamiento de archivos en proveedor externo.
 * Implementación actual: Cloudinary (MVP).
 * Abstracción preparada para migración futura a AWS S3 / GCS
 * sin modificar la lógica de negocio.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
public interface StorageService {

    /**
     * Sube un archivo al proveedor externo.
     *
     * @param file       Archivo a subir
     * @param folderPath Ruta de carpeta en el proveedor (ej: "kinevid/patients/1/sessions/3/analysis/5")
     * @return {@link StorageResult} con la URL pública y el ID del archivo en el proveedor
     */
    StorageResult upload(MultipartFile file, String folderPath) throws OperationException;

    /**
     * Elimina un archivo del proveedor externo usando su ID.
     *
     * @param storageFileId ID del archivo en el proveedor (devuelto al subir)
     */
    void delete(String storageFileId) throws OperationException;
}
