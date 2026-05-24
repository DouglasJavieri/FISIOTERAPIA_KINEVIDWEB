# 🎯 PRÓXIMOS PASOS: Fase 5.2 & Fase 6

**Documento:** Plan de implementación del módulo de Análisis Postural  
**Creado:** 12 de Mayo 2026  
**Versión:** 1.0  

---

## 📋 INDICE DE ESTE DOCUMENTO

1. [Fase 5.2 - Frontend: Detección y Modal](#fase-52---frontend-detección-y-modal)
2. [Fase 6 - Backend: Análisis de Imagen](#fase-6---backend-análisis-de-imagen)
3. [Cronograma estimado](#cronograma-estimado)
4. [Dependencias y bloqueadores](#dependencias-y-bloqueadores)
5. [Decisiones de diseño](#decisiones-de-diseño)

---

## Fase 5.2 - Frontend: Detección y Modal

### 📌 Objetivo
Agregar botón "Ir a Análisis de Imagen" en la alerta de Análisis Postural que abre un modal/drawer con el flujo de captura de fotos y análisis.

### 🎨 Ubicación UI
```
SessionFormComponent → Paso 1 → Sección 1.4 → ALERTA Análisis Postural
```

### Implementación

#### 1. Actualizar template HTML
```html
<!-- Antes: solo alerta -->
<div *ngIf="hasPosturalAnalysisService" class="postural-analysis-alert">
  <mat-icon>info</mat-icon>
  <span>✓ Se ha detectado "Análisis Postural". 
        Después de completar esta sesión, se activará el flujo de captura de imágenes.</span>
</div>

<!-- Después: alerta + botón -->
<div *ngIf="hasPosturalAnalysisService" class="postural-analysis-alert-box">
  <div class="postural-analysis-alert">
    <mat-icon>info</mat-icon>
    <span>✓ Se ha detectado "Análisis Postural". 
          Después de completar esta sesión, se activará el flujo de captura de imágenes.</span>
  </div>
  <button mat-flat-button color="primary" (click)="goToAnalysisFlow()">
    <mat-icon>image</mat-icon>
    Ir a Análisis de Imagen
  </button>
</div>
```

#### 2. Método en TypeScript
```typescript
goToAnalysisFlow(): void {
  if (!this.sessionId) {
    Notiflix.Report.failure(
      'Error', 
      'La sesión debe guardarse primero.', 
      'OK'
    );
    return;
  }
  
  // Opción A: Abrir modal (angular-material)
  const dialogRef = this.dialog.open(ImageAnalysisModalComponent, {
    width: '90vw',
    height: '90vh',
    data: { sessionId: this.sessionId }
  });
  
  // Opción B: Navegar a pantalla separada
  // this.router.navigate([...new route...]);
}
```

#### 3. Estilos SCSS
```scss
.postural-analysis-alert-box {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 16px 0;

  .postural-analysis-alert {
    // estilos existentes
  }

  button {
    align-self: flex-start; // Botón a la izquierda
    min-width: 200px;
  }
}

@media (max-width: 768px) {
  .postural-analysis-alert-box {
    button {
      width: 100%; // Full-width en mobile
      align-self: stretch;
    }
  }
}
```

### 🧩 Componentes necesarios

1. **ImageAnalysisModalComponent** (NUEVO)
   ```
   src/app/features/pages/management-pacient/clinical/image-analysis/
   ├── image-analysis-modal.component.ts
   ├── image-analysis-modal.component.html
   ├── image-analysis-modal.component.scss
   └── image-analysis.module.ts
   ```

### Estimado: 1-2 días

---

## Fase 6 - Backend: Análisis de Imagen

### 📌 Objetivo
Crear entidades, servicios y API REST para manejar análisis de imágenes postales con captura de fotos, anotaciones y análisis biomecánico.

### 🏗️ Entidades a crear

#### 1. ImageAnalysis (Principal)
```java
@Entity
@Table(name = "image_analysis")
public class ImageAnalysis extends AuditableEntity {
    
    @Id
    @SequenceGenerator(name = "seq_image_analysis", sequenceName = "SEQ_IMAGE_ANALYSIS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_image_analysis")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ClinicalSession session;
    
    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false)
    private AnalysisType analysisType; // FOOT_PRONATION, GAIT, POSTURE
    
    @Column(name = "pronation_left", length = 50)
    private String pronationLeft; // NEUTRAL, PRONATION, SUPINATION
    
    @Column(name = "pronation_right", length = 50)
    private String pronationRight; // NEUTRAL, PRONATION, SUPINATION
    
    @Column(name = "foot_angle_left")
    private Float footAngleLeft;
    
    @Column(name = "foot_angle_right")
    private Float footAngleRight;
    
    @Column(name = "biomechanical_observations", length = 2000)
    private String biomechanicalObservations;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", nullable = false)
    private AnalysisStatus analysisStatus; // DRAFT, COMPLETED, ARCHIVED
    
    @OneToMany(mappedBy = "imageAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisPhoto> photos = new ArrayList<>();
    
    // getters, setters, constructors
}
```

#### 2. AnalysisPhoto (Fotos)
```java
@Entity
@Table(name = "analysis_photo", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"image_analysis_id", "photo_order"}))
public class AnalysisPhoto extends AuditableEntity {
    
    @Id
    @SequenceGenerator(name = "seq_analysis_photo", sequenceName = "SEQ_ANALYSIS_PHOTO_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_analysis_photo")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_analysis_id", nullable = false)
    private ImageAnalysis imageAnalysis;
    
    @Column(name = "photo_order", nullable = false)
    private Integer photoOrder; // 1-6
    
    @Column(name = "photo_url", length = 500, nullable = false)
    private String photoUrl; // Cloudinary URL
    
    @Column(name = "storage_file_id", length = 200, nullable = false)
    private String storageFileId; // ID en Cloudinary
    
    @Column(name = "annotations_json", length = 5000)
    private String annotationsJson; // JSON con trazos, ángulos, etc.
    
    @Column(name = "is_selected")
    private Boolean isSelected = false; // Para seleccionar fotos en reporte
    
    @Column(name = "photo_side", length = 20)
    private String photoSide; // LEFT, RIGHT, FRONT, BACK
    
    // getters, setters, constructors
}
```

### 🛠️ Servicios a crear

#### 1. ImageAnalysisService
```typescript
// Interfaz TypeScript (Frontend)
export interface ImageAnalysisRequest {
  sessionId: number;
  analysisType: 'FOOT_PRONATION' | 'GAIT' | 'POSTURE';
  pronationLeft?: string;
  pronationRight?: string;
  footAngleLeft?: number;
  footAngleRight?: number;
  biomechanicalObservations?: string;
}

export interface AnalysisPhotoRequest {
  photoOrder: number;
  photoUrl: string;
  storageFileId: string;
  annotationsJson?: string;
  photoSide: 'LEFT' | 'RIGHT' | 'FRONT' | 'BACK';
}

export interface ImageAnalysisResponse extends ImageAnalysisRequest {
  id: number;
  analysisDate: string;
  analysisStatus: 'DRAFT' | 'COMPLETED' | 'ARCHIVED';
  photos: AnalysisPhotoResponse[];
}
```

#### 2. API REST Endpoints

```
// Crear análisis de imagen
POST /api/image-analysis/create
Body: {
  sessionId: number,
  analysisType: string
}
Response: ImageAnalysisResponse

// Obtener análisis
GET /api/image-analysis/{id}
Response: ImageAnalysisResponse

// Subir foto
POST /api/image-analysis/{id}/photo/upload
Multipart: photo (file)
Response: AnalysisPhotoResponse

// Actualizar análisis (finalizar)
PUT /api/image-analysis/{id}
Body: ImageAnalysisRequest
Response: ImageAnalysisResponse

// Cambiar estado
PATCH /api/image-analysis/{id}/status
Body: { status: string }
Response: ImageAnalysisResponse
```

### 📦 Dependencias necesarias

```xml
<!-- Cloudinary (Java SDK) -->
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.33.0</version>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### Estimado: 2-3 semanas

---

## Cronograma estimado

```
SEMANA 1 (Mayo 13-19):
├─ Lunes 13: Fase 5.2 - Frontend modal
├─ Martes 14-15: Testing Fase 5.2
├─ Miércoles 16: Iniciación Fase 6 - Entidades
├─ Jueves 17-18: Servicios backend Fase 6
└─ Viernes 19: QA Fase 6.1

SEMANA 2 (Mayo 20-26):
├─ Lunes 20: API REST endpoints Fase 6
├─ Martes 21-22: Integración Cloudinary
├─ Miércoles 23: Testing backend
├─ Jueves 24-25: Frontend - Canvas HTML5
└─ Viernes 26: QA integrada

SEMANA 3 (Mayo 27-02):
├─ Lunes 27: Canvas anotaciones
├─ Martes 28-29: Análisis biomecánico UI
├─ Miércoles 30: Testing UI-Backend
├─ Jueves 01: Refinamientos
└─ Viernes 02: Release candidate
```

---

## Dependencias y bloqueadores

### ✅ No hay bloqueadores actuales
- ✅ Cambio de 3 a 2 stepers completado
- ✅ Detección de Análisis Postural lista
- ✅ Backend preparado para nuevas entidades
- ✅ Cloudinary account configurado (según plan)

### ⚠️ Consideraciones

1. **Cloudinary API Key**
   - Requerida para subir fotos
   - Debe estar en `application.properties` (securizada)

2. **Almacenamiento de anotaciones (JSON)**
   - Decidir estructura de anotaciones
   - Trazos: coordenadas (x, y)
   - Ángulos: valor en grados
   - Labels: descripciones de puntos

3. **Performance de fotos**
   - 6 fotos × sesión → potencial problema
   - Considerar compresión en frontend
   - Cloudinary maneja redimensionamiento

---

## Decisiones de diseño

### 1. Modal vs Pantalla separada
```
✅ RECOMENDACIÓN: Modal (Dialog de Angular Material)
   - Flujo no interrumpe sesión principal
   - Usuario puede cancelar sin perder datos
   - Mejor UX para "dentro de sesión"
   - Reduce navegación
```

### 2. Subida de fotos
```
✅ RECOMENDACIÓN: Cloudinary (no guardar en servidor)
   - Plan gratuito: 25GB
   - URLs públicas estables
   - Transformaciones automáticas
   - Interfaz simple con SDK Java
   
🔸 ALTERNATIVA: AWS S3 (futuro)
   - Mayor control
   - Escalable a millones de usuarios
   - Costo por GB
```

### 3. Estructura de anotaciones
```json
✅ RECOMENDACIÓN: JSON flexible

{
  "annotations": [
    {
      "id": "line_1",
      "type": "line",
      "points": [{"x": 100, "y": 150}, {"x": 200, "y": 300}],
      "color": "#FF0000",
      "label": "Eje talón-dedo"
    },
    {
      "id": "angle_1",
      "type": "angle",
      "center": {"x": 150, "y": 225},
      "angle": 45.5,
      "label": "Pronación"
    }
  ]
}
```

### 4. Enums para categorización
```typescript
enum AnalysisType {
  FOOT_PRONATION,    // Análisis de pronación/supinación
  GAIT,             // Análisis de marcha
  POSTURE           // Análisis postural general
}

enum AnalysisStatus {
  DRAFT,            // En progreso
  COMPLETED,        // Finalizado
  ARCHIVED          // Archivado (solo lectura)
}

enum Pronation {
  NEUTRAL,          // Pronación normal
  PRONATION,        // Overpronación
  SUPINATION        // Supinación
}

enum PhotoSide {
  LEFT,
  RIGHT,
  FRONT,
  BACK
}
```

---

## 🎯 Success Criteria

### Fase 5.2 (Frontend Modal)
- [ ] Botón "Ir a Análisis de Imagen" funcional
- [ ] Modal se abre sin errores
- [ ] Modal se cierra correctamente
- [ ] Datos de sesión se conservan
- [ ] Testing en desktop + tablet + mobile

### Fase 6 (Backend)
- [ ] Entidades creadas en BD
- [ ] Endpoints REST funcionan (POST, GET, PUT, PATCH)
- [ ] Integración Cloudinary OK
- [ ] Búsqueda y filtrado de análisis
- [ ] Validaciones de integridad

### Fase 6B (Frontend Canvas)
- [ ] Canvas HTML5 dibuja líneas
- [ ] Canvas dibuja ángulos
- [ ] Anotaciones se guardan en JSON
- [ ] Frontend envía a backend
- [ ] Fotos se cargan a Cloudinary

---

## 📞 Recomendaciones finales

### Para Fase 5.2:
1. Usar `MatDialog` de Angular Material
2. Lazy load del componente modal
3. Validar `sessionId` antes de abrir

### Para Fase 6:
1. Crear tabla de migraciones Flyway
2. Tests unitarios para servicios
3. Documentación Swagger/OpenAPI
4. Validaciones en controllers

### Para overall:
1. Mantener retrospectiva al final de cada fase
2. Documentar cambios en cada commit
3. Hacer code review antes de merge
4. Agregar logs significativos

---

## 📚 Referencias

- Angular Material Dialog: https://material.angular.io/components/dialog/overview
- Cloudinary Java SDK: https://github.com/cloudinary/cloudinary_java
- HTML5 Canvas: https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API
- Java 17 records: https://docs.oracle.com/en/java/javase/17/language/records.html

---

**Documento preparado por:** Douglas Cristhian Javieri Vino  
**Aprobado:** Pendiente confirmación usuario  
**Fecha de inicio prevista:** 13 de Mayo 2026  
**Fecha de término prevista:** 02 de Junio 2026  


