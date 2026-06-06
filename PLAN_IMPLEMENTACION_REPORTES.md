# 📋 Plan de Implementación de Reportes PDF - KineVid (Revisado)

Este documento detalla la estrategia para la generación de reportes bajo demanda en el proyecto KineVid. Los reportes se generarán dinámicamente cada vez que se soliciten, sin almacenar el archivo PDF ni su URL en la base de datos o en servicios externos como Cloudinary.

---

## 1. Análisis Comparativo: JasperReports vs iText 7

Se mantiene la recomendación de **iText 7** por las siguientes razones:

| Característica | JasperReports 5.6.0 | iText 7 |
| :--- | :--- | :--- |
| **Generación** | Basada en plantillas (.jrxml) | Programática (Java puro) |
| **Dinamismo** | Limitado con recursos externos | Total control sobre imágenes de Cloudinary |
| **Mantenimiento** | Requiere herramientas externas | Todo en el código fuente (IDE) |
| **Arquitectura** | Pesada para reportes simples | Ligera y modular |

### 🏆 Elección: iText 7
Dado que los reportes no se guardarán, iText 7 ofrece una mayor velocidad de generación en memoria, lo cual es crítico para una experiencia de usuario fluida al descargar archivos "al vuelo".

---

## 2. Estrategia de "Reportes bajo demanda" (Sin Persistencia)

### 2.1 Flujo de Generación
1.  **Petición:** El usuario pulsa el botón de reporte en el Frontend.
2.  **Backend:**
    *   Recupera datos de `FootAnalysis`, `ClinicalSession`, `Patient` y `AnalysisPhoto`.
    *   Descarga temporalmente las imágenes de Cloudinary (basado en `photo_url`).
    *   Construye el PDF en memoria utilizando iText 7.
3.  **Respuesta:** El Backend envía el flujo de bytes (`stream`) directamente al navegador con el `Content-Type: application/pdf`.
4.  **Frontend:** El navegador recibe el archivo y lo descarga o previsualiza.

### 2.2 Ventajas de NO guardar la URL
*   **Consistencia:** El reporte siempre refleja los datos más actuales de la base de datos.
*   **Ahorro de Almacenamiento:** No se ocupan créditos de Cloudinary ni espacio en BD para archivos estáticos que pueden quedar obsoletos si se edita el análisis.
*   **Seguridad:** Menos exposición de documentos PDF estáticos en la nube.

---

## 3. Plan de Acción Actualizado

### Paso 1: Dependencias
Agregar iText 7 al `pom.xml`. No se requieren cambios en la configuración de Cloudinary para el reporte, ya que solo usaremos las URLs de las fotos ya existentes.

### Paso 2: Servicio de Reportes (`ReportService`)
Desarrollar la lógica programática para el diseño del PDF:
*   **Encabezado:** Logo institucional y datos del centro.
*   **Sección Paciente:** Extraer datos de `Patient` (nombre, edad, CI).
*   **Sección Clínica:** Datos de `ClinicalSession` y `FootAnalysis`.
*   **Sección Biomecánica:** Tablas comparativas de pie izquierdo y derecho.
*   **Sección Fotos:** Dibujar las fotos seleccionadas (`isSelected=true`) junto con sus trazos y ángulos calculados.

### Paso 3: Endpoint de Generación
Implementar un endpoint que devuelva un `ResponseEntity<Resource>`:
*   `GET /api/foot-analysis/{id}/report/download`
*   No guarda nada en BD. Solo procesa y responde.

### Paso 4: Frontend (Angular)
*   Botón "Descargar Reporte" que invoque al endpoint.
*   Gestión del Blob en Angular para forzar la descarga del archivo con un nombre descriptivo (ej: `Reporte_Pisada_Juan_Perez.pdf`).

---

## 4. Análisis de Entidades (Confirmación de Campos)

Tras revisar las entidades en `back-office-api`, se confirma que:
*   `FootAnalysis`: Contiene antecedentes, evaluación kinésica, diagnóstico y ángulos (izq/der, int/ext). No tiene (ni necesita) `report_url`.
*   `AnalysisPhoto`: Contiene `photo_url` (Cloudinary), `annotations_json` (trazos para el canvas) y `isSelected`.
*   `ClinicalSession`: Provee el contexto de la visita (motivo, fecha, profesional).
*   `Patient`: Provee la información demográfica necesaria.

---
**Conclusión:** La arquitectura propuesta es 100% compatible con tu requerimiento de no persistir reportes. Todo se construye en tiempo real a partir de los datos existentes.
