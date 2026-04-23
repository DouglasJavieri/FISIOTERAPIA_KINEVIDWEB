# 📋 KINEVID APP — Plan Maestro de Desarrollo
> **Stack:** Angular 13 · Spring Boot · PostgreSQL  
> **Fecha de referencia:** Abril 2026  
> **Autor:** Douglas Cristhian Javieri Vino

---

## 🗂️ ÍNDICE

1. [Estado actual del sistema](#1-estado-actual-del-sistema)
2. [Diseño completo de base de datos](#2-diseño-completo-de-base-de-datos)
3. [Flujo general del módulo de pacientes](#3-flujo-general-del-módulo-de-pacientes)
4. [Fases de desarrollo](#4-fases-de-desarrollo)
5. [Módulos del sistema](#5-módulos-del-sistema)
6. [Permisos y roles por módulo](#6-permisos-y-roles-por-módulo)
7. [Estrategia de almacenamiento de imágenes](#7-estrategia-de-almacenamiento-de-imágenes)

---

## 1. Estado actual del sistema

### ✅ Ya implementado (Backend + Frontend)

| Módulo | Backend | Frontend |
|---|---|---|
| Autenticación (JWT) | ✅ | ✅ |
| Usuarios | ✅ | ✅ |
| Roles y Permisos | ✅ | ✅ |
| Empleados | ✅ | ✅ |
| Pacientes (CRUD) | ✅ | 🔄 En progreso |
| Servicios (CRUD) | ✅ | 🔄 En progreso |

### 🔄 Pendiente por desarrollar

- Historia Clínica (sesiones por paciente)
- Análisis de imagen (pronación/supinación del pie)
- Generación de reportes PDF
- Módulo de citas (futuro)

---

## 2. Diseño completo de base de datos

### 2.1 Tablas ya existentes

```
user → role (user_role) → permission (role_permission)
employee ← user
patient
medical_service
```

### 2.2 Tablas nuevas a crear

---

#### 📌 Tabla: `clinical_session` (Historia Clínica / Sesión)

Cada vez que un paciente asiste a consulta se crea un registro de sesión.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Generado por secuencia `SEQ_CLINICAL_SESSION_ID` |
| patient_id | BIGINT | FK → patient.id, NOT NULL | Paciente al que pertenece |
| employee_id | BIGINT | FK → employee.id, NOT NULL | Fisioterapeuta que atiende |
| session_date | DATE | NOT NULL | Fecha de la sesión |
| session_number | INTEGER | NOT NULL | Número de sesión del paciente (1, 2, 3…) |
| reason_for_consultation | VARCHAR(500) | NOT NULL | Motivo de consulta |
| relevant_background | TEXT | NULL | Antecedentes relevantes |
| kinesiological_evaluation | TEXT | NULL | Evaluación kinesiológica |
| treatment_applied | TEXT | NULL | Tratamiento aplicado en la sesión |
| observations | TEXT | NULL | Observaciones del profesional |
| evolution | TEXT | NULL | Evolución del paciente |
| has_image_analysis | BOOLEAN | NOT NULL, DEFAULT FALSE | Si tiene análisis de imagen asociado |
| session_status | VARCHAR(30) | NOT NULL | OPEN / CLOSED / CANCELLED |
| created_at | TIMESTAMP | NOT NULL | Auditoría |
| updated_at | TIMESTAMP | NOT NULL | Auditoría |
| created_by | VARCHAR(80) | NOT NULL | Auditoría |
| updated_by | VARCHAR(80) | NOT NULL | Auditoría |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Eliminación lógica |

---

#### 📌 Tabla: `session_service` (Servicios de la sesión)

Relación N:M entre una sesión y los servicios aplicados.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Generado por secuencia `SEQ_SESSION_SERVICE_ID` |
| clinical_session_id | BIGINT | FK → clinical_session.id, NOT NULL | Sesión clínica |
| medical_service_id | BIGINT | FK → medical_service.id, NOT NULL | Servicio aplicado |
| quantity | INTEGER | NOT NULL, DEFAULT 1 | Cantidad de aplicaciones |
| notes | VARCHAR(300) | NULL | Notas específicas del servicio |

**Constraint único:** `(clinical_session_id, medical_service_id)`

---

#### 📌 Tabla: `image_analysis` (Análisis de Imagen)

Vinculado a una sesión clínica, contiene el análisis de pronación/supinación.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Generado por secuencia `SEQ_IMAGE_ANALYSIS_ID` |
| clinical_session_id | BIGINT | FK → clinical_session.id, NOT NULL | Sesión clínica a la que pertenece |
| analysis_date | TIMESTAMP | NOT NULL | Fecha/hora del análisis |
| foot_side | VARCHAR(10) | NOT NULL | LEFT / RIGHT / BOTH |
| diagnosis | VARCHAR(50) | NOT NULL | NORMAL / PRONATION / SUPINATION |
| angle_left | DECIMAL(5,2) | NULL | Ángulo medido pie izquierdo |
| angle_right | DECIMAL(5,2) | NULL | Ángulo medido pie derecho |
| notes | TEXT | NULL | Notas del análisis |
| report_generated | BOOLEAN | NOT NULL, DEFAULT FALSE | Si se generó reporte PDF |
| report_url | VARCHAR(500) | NULL | URL del reporte generado |
| created_at | TIMESTAMP | NOT NULL | Auditoría |
| updated_at | TIMESTAMP | NOT NULL | Auditoría |
| created_by | VARCHAR(80) | NOT NULL | Auditoría |
| updated_by | VARCHAR(80) | NOT NULL | Auditoría |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Eliminación lógica |

---

#### 📌 Tabla: `analysis_photo` (Fotos del análisis)

Hasta 6 fotos por análisis, con trazos y anotaciones guardados como JSON.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Generado por secuencia `SEQ_ANALYSIS_PHOTO_ID` |
| image_analysis_id | BIGINT | FK → image_analysis.id, NOT NULL | Análisis al que pertenece |
| photo_order | INTEGER | NOT NULL | Orden de la foto (1 a 6) |
| photo_url | VARCHAR(500) | NOT NULL | URL de almacenamiento (Google Drive / S3) |
| storage_file_id | VARCHAR(200) | NOT NULL | ID del archivo en el proveedor externo |
| annotations_json | TEXT | NULL | JSON con trazos y ángulos marcados en la imagen |
| is_selected | BOOLEAN | NOT NULL, DEFAULT FALSE | Si esta foto fue seleccionada para mostrar al paciente |
| created_at | TIMESTAMP | NOT NULL | Auditoría |

**Constraint único:** `(image_analysis_id, photo_order)`

---

### 2.3 Diagrama de relaciones (ERD simplificado)

```
user ──────────────── employee
 │                       │
 └── user_role           └──────────────────────┐
      │                                         │
     role                                       ▼
      │                              clinical_session
     role_permission                  │        │
      │                               │        │
    permission                    patient   employee
                                       │
                              session_service
                                   │
                             medical_service

clinical_session ──── image_analysis ──── analysis_photo
```

---

## 3. Flujo general del módulo de pacientes

### 3.1 Primera visita de un paciente

```
[1] RECEPCIÓN
    └── Buscar paciente por nombre o CI
         │
         ├── ENCONTRADO → abrir perfil del paciente
         │
         └── NO ENCONTRADO
              └── [2] REGISTRAR NUEVO PACIENTE
                       • Datos personales (nombre, CI, género, fecha de nacimiento)
                       • Datos de contacto (teléfono, email, dirección)
                       • Datos médicos (tipo de sangre, ocupación)
                       • Contacto de emergencia
                       • Notas adicionales
                       └── Guardar → Estado: ACTIVE

[3] VER PERFIL DEL PACIENTE
    └── Ver lista de todas sus sesiones clínicas (con fecha y estado)
         └── [4] CREAR NUEVA SESIÓN CLÍNICA
                  • Fecha (automática: hoy)
                  • Motivo de consulta
                  • Antecedentes relevantes
                  • Seleccionar fisioterapeuta
                  └── Guardar sesión → Estado: OPEN

[5] DURANTE LA CONSULTA
    └── Editar la sesión activa
         • Evaluación kinesiológica
         • Seleccionar servicios a aplicar (multiselect)
         • Tratamiento aplicado
         • Observaciones
         └── ¿Requiere análisis de imagen?
              │
              ├── NO → Guardar y cerrar sesión → Estado: CLOSED
              │
              └── SÍ → [6] MÓDULO ANÁLISIS DE IMAGEN
                           • Tomar hasta 6 fotos
                           • Seleccionar foto principal
                           • Trazar líneas verticales (máx. 2 trazos)
                           • Marcar ángulos
                           • Diagnóstico: NORMAL / PRONATION / SUPINATION
                           • Notas del análisis
                           └── Guardar análisis
                                └── [7] GENERAR REPORTE PDF
                                         • Datos del paciente
                                         • Datos de la sesión
                                         • Servicios aplicados
                                         • Imágenes con trazos
                                         • Diagnóstico y observaciones
                                         └── Guardar URL del reporte
                                              └── Cerrar sesión → CLOSED
```

### 3.2 Visitas siguientes

```
[1] Buscar paciente (nombre / CI)
    └── Abrir perfil
         └── Ver historial de sesiones anteriores (fecha, servicio, terapeuta)
              └── Crear nueva sesión para la consulta actual
                   └── (mismo flujo desde paso [4])
```

---

## 4. Fases de desarrollo

### 🔵 FASE 1 — Backend: Pacientes y Servicios (Completada ✅)

- [x] Entidad `Patient` con enums (Gender, BloodType, PatientStatus)
- [x] Entidad `MedicalService` con enums (ServiceCategory, ServiceStatus)
- [x] CRUD completo con paginación para ambas entidades
- [x] Permisos registrados en DataLoader
- [x] Roles ROOT, ADMIN, FISIOTERAPEUTA configurados

---

### 🔵 FASE 2 — Frontend: Módulo Servicios (En progreso 🔄)

**Pantalla de Servicios** (`/pages/management-pacient/service`)

Estructura de carpetas (convención del proyecto):
```
service/
├── service.component.ts          ← Lista paginada (componente principal)
├── service.component.html
├── service.component.scss
├── service.module.ts
├── service-routing.module.ts
├── service.util.ts               ← Constantes de columnas y mapeos
├── add-service/
│   ├── add-service.component.ts
│   ├── add-service.component.html
│   └── add-service.component.scss
└── update-service/
    ├── update-service.component.ts
    ├── update-service.component.html
    └── update-service.component.scss
```

> ⚠️ **Nota:** La carpeta `service-list/` que se creó por error debe ser eliminada.
> El paginado va directamente en `service.component.ts` (sin subcarpeta),
> igual que en el módulo de empleados.

**Funcionalidades a implementar:**
- [ ] Tabla paginada con filtro por estado y categoría
- [ ] Modal/formulario Agregar Servicio
- [ ] Modal/formulario Editar Servicio
- [ ] Cambiar estado (ACTIVE / INACTIVE)
- [ ] Eliminar (lógico)
- [ ] Servicios del core Angular: `MedicalServiceService`, modelos, rutas

---

### 🔵 FASE 3 — Frontend: Módulo Pacientes (Siguiente)

**Pantalla de Pacientes** (`/pages/management-pacient/patient`)

```
patient/
├── patient.component.ts          ← Lista paginada
├── patient.component.html
├── patient.component.scss
├── patient.module.ts
├── patient-routing.module.ts
├── patient.util.ts
├── add-patient/
│   ├── add-patient.component.ts
│   ├── add-patient.component.html
│   └── add-patient.component.scss
└── update-patient/
    ├── update-patient.component.ts
    ├── update-patient.component.html
    └── update-patient.component.scss
```

**Funcionalidades:**
- [ ] Tabla paginada con búsqueda por nombre/CI y filtro por estado
- [ ] Formulario Registrar Paciente (stepper o formulario dividido en secciones)
- [ ] Formulario Editar Paciente
- [ ] Cambiar estado del paciente
- [ ] Eliminar (lógico)
- [ ] Botón "Ver Historia Clínica" → navega al módulo de historial

---

### 🔵 FASE 4 — Backend: Historia Clínica

**Nuevas entidades y APIs:**
- [ ] Entidad `ClinicalSession` + DTO + Repository + Service + Controller
- [ ] Entidad `SessionService` (relación sesión ↔ servicio)
- [ ] Endpoints REST:
  - `POST /api/clinical-session/create`
  - `GET /api/clinical-session/patient/{patientId}` (paginado)
  - `GET /api/clinical-session/{id}`
  - `PUT /api/clinical-session/update/{id}`
  - `PATCH /api/clinical-session/{id}/status`
  - `DELETE /api/clinical-session/delete/{id}`
  - `POST /api/clinical-session/{id}/services` (asignar servicios)
- [ ] Permisos nuevos en DataLoader:
  - `CREATE_CLINICAL_SESSION`, `VIEW_CLINICAL_SESSION`
  - `UPDATE_CLINICAL_SESSION`, `DELETE_CLINICAL_SESSION`
  - `LIST_CLINICAL_SESSION`, `MANAGE_SESSION_SERVICES`

---

### 🔵 FASE 5 — Frontend: Historia Clínica

**Pantalla Historia Clínica** (`/pages/management-pacient/patient/clinical-history`)

```
clinical-history/
├── clinical-history.component.ts   ← Lista de sesiones del paciente
├── clinical-history.component.html
├── clinical-history.component.scss
├── add-session/
│   └── ...                          ← Crear nueva sesión
└── view-session/
    └── ...                          ← Ver/editar sesión existente
```

**Flujo en pantalla:**
1. Desde el perfil del paciente → click "Historia Clínica"
2. Pantalla muestra: cabecera con datos del paciente + lista de sesiones (fecha, motivo, fisioterapeuta, estado)
3. Botón "Nueva Sesión" → abre formulario
4. Click en una sesión → ver detalle completo (editable si está OPEN)
5. En el detalle → multiselect de servicios aplicados
6. Si aplica → botón "Ir a Análisis de Imagen"

---

### 🔵 FASE 6 — Backend: Análisis de Imagen

**Nuevas entidades y APIs:**
- [ ] Entidad `ImageAnalysis` + DTO + Repository + Service + Controller
- [ ] Entidad `AnalysisPhoto` + DTO + Repository + Service + Controller
- [ ] Integración con proveedor de almacenamiento (Google Drive API o AWS S3)
- [ ] Endpoints REST:
  - `POST /api/image-analysis/create`
  - `GET /api/image-analysis/session/{sessionId}`
  - `GET /api/image-analysis/{id}`
  - `PUT /api/image-analysis/update/{id}`
  - `POST /api/image-analysis/{id}/photos` (subir foto)
  - `DELETE /api/image-analysis/{id}/photos/{photoId}`
  - `PATCH /api/image-analysis/{id}/photos/{photoId}/select`
  - `POST /api/image-analysis/{id}/photos/{photoId}/annotations` (guardar trazos JSON)
- [ ] Permisos: `CREATE_IMAGE_ANALYSIS`, `VIEW_IMAGE_ANALYSIS`, `MANAGE_PHOTOS`, `GENERATE_REPORT`

---

### 🔵 FASE 7 — Frontend: Análisis de Imagen

**Pantalla Análisis de Imagen**

```
image-analysis/
├── image-analysis.component.ts
├── image-analysis.component.html    ← Grilla de 6 fotos + canvas con trazos
├── image-analysis.component.scss
├── photo-viewer/                    ← Canvas interactivo (trazos + ángulos)
│   └── ...
└── report-preview/                  ← Vista previa antes de generar PDF
    └── ...
```

**Funcionalidades:**
- [ ] Área de cámara o carga de imágenes (hasta 6 fotos)
- [ ] Grilla de miniaturas para seleccionar foto activa
- [ ] Canvas HTML5 sobre la foto seleccionada:
  - Hasta 2 trazos verticales
  - Herramienta para marcar ángulos con texto
  - Modo de diagnóstico visual (NORMAL / PRONATION / SUPINATION)
- [ ] Serialización de anotaciones a JSON y guardado en backend
- [ ] Botón "Generar Reporte" → llama al endpoint de PDF

---

### 🔵 FASE 8 — Backend: Generación de Reportes PDF

**Librería sugerida:** `iText 7` (Spring Boot)

- [ ] Servicio `ReportService` → genera PDF con:
  - Datos del paciente (nombre, CI, edad, género, fecha)
  - Datos del fisioterapeuta
  - Servicios aplicados en la sesión
  - Historia clínica de la sesión (evaluación, tratamiento, observaciones, evolución)
  - Imágenes seleccionadas con trazos (si aplica)
  - Diagnóstico: NORMAL / PRONATION / SUPINATION
  - Fecha de generación y firma
- [ ] El PDF se guarda en almacenamiento externo y se registra la URL en `image_analysis.report_url`
- [ ] Endpoint: `GET /api/image-analysis/{id}/report`

---

### 🔵 FASE 9 — Frontend: Descarga y Visualización de Reportes

- [ ] Botón "Descargar Reporte PDF" en la sesión
- [ ] Vista previa del reporte en modal (iframe o visor de PDF)
- [ ] Historial de reportes generados para el paciente

---

## 5. Módulos del sistema

### Sidebar de navegación (por pantallas)

```
📦 KineVid App
│
├── 🏠 Inicio (home)
│
├── 👥 Gestión de Usuarios
│   ├── Usuarios
│   ├── Roles
│   └── Permisos
│
├── 👨‍💼 Empleados
│
└── 🏥 Pacientes y Servicios  [management-pacient]
    ├── 📋 Servicios del Consultorio
    └── 🧑‍⚕️ Pacientes
          └── (→ Historia Clínica)
                └── (→ Análisis de Imagen)
```

---

## 6. Permisos y roles por módulo

### Permisos existentes

| Módulo | Permisos |
|---|---|
| Usuarios | `CREATE_USER`, `VIEW_USER`, `UPDATE_USER`, `DELETE_USER`, `LIST_USER`, `CHANGE_USER_STATUS` |
| Roles | `CREATE_ROLE`, `READ_ROLE`, `UPDATE_ROLE`, `DELETE_ROLE`, `LIST_ROLE`, `CHANGE_ROLE_STATUS` |
| Permisos | `CREATE_PERMISSION`, `READ_PERMISSION`, `UPDATE_PERMISSION`, `DELETE_PERMISSION`, `LIST_PERMISSION`, `CHANGE_PERMISSION_STATUS`, `ASSIGN_PERMISSION_TO_ROLE`, `REMOVE_PERMISSION_FROM_ROLE` |
| Empleados | `CREATE_EMPLOYEE`, `VIEW_EMPLOYEE`, `UPDATE_EMPLOYEE`, `DELETE_EMPLOYEE`, `LIST_EMPLOYEE`, `CHANGE_EMPLOYEE_STATUS`, `ASSIGN_USER_TO_EMPLOYEE`, `REMOVE_USER_FROM_EMPLOYEE` |
| Pacientes | `CREATE_PATIENT`, `VIEW_PATIENT`, `UPDATE_PATIENT`, `DELETE_PATIENT`, `LIST_PATIENT`, `CHANGE_PATIENT_STATUS` |
| Servicios | `CREATE_SERVICE`, `VIEW_SERVICE`, `UPDATE_SERVICE`, `DELETE_SERVICE`, `LIST_SERVICE`, `CHANGE_SERVICE_STATUS` |

### Permisos a agregar (Fases 4-8)

| Módulo | Permisos nuevos |
|---|---|
| Historia Clínica | `CREATE_CLINICAL_SESSION`, `VIEW_CLINICAL_SESSION`, `UPDATE_CLINICAL_SESSION`, `DELETE_CLINICAL_SESSION`, `LIST_CLINICAL_SESSION`, `MANAGE_SESSION_SERVICES` |
| Análisis de Imagen | `CREATE_IMAGE_ANALYSIS`, `VIEW_IMAGE_ANALYSIS`, `UPDATE_IMAGE_ANALYSIS`, `DELETE_IMAGE_ANALYSIS`, `MANAGE_PHOTOS`, `ANNOTATE_PHOTO` |
| Reportes | `GENERATE_REPORT`, `VIEW_REPORT`, `DOWNLOAD_REPORT` |

### Asignación de permisos por rol

| Permiso | ROOT | ADMIN | FISIOTERAPEUTA |
|---|---|---|---|
| Gestión de usuarios/roles/permisos | ✅ | ✅ | ❌ |
| Gestión de empleados | ✅ | ✅ | ❌ |
| Pacientes (CRUD completo) | ✅ | ✅ | ✅ |
| Servicios (solo ver y listar) | ✅ | ✅ | ✅ (VIEW + LIST) |
| Servicios (crear/editar/eliminar) | ✅ | ✅ | ❌ |
| Historia Clínica | ✅ | ✅ | ✅ |
| Análisis de Imagen | ✅ | ✅ | ✅ |
| Reportes | ✅ | ✅ | ✅ |

---

## 7. Estrategia de almacenamiento de imágenes

### Recomendación: **Google Cloud Storage (GCS)** o **AWS S3**

Para producción, se recomienda **AWS S3** o **Google Cloud Storage** por las siguientes razones:

| Criterio | Google Drive | AWS S3 / GCS |
|---|---|---|
| API oficial para backend Java | ✅ (pero limitada) | ✅ SDK nativo |
| Acceso por URL directa | ❌ (requiere auth) | ✅ URLs firmadas o públicas |
| Control de permisos por archivo | ❌ Limitado | ✅ Fine-grained |
| Escalabilidad en producción | ❌ Límite 15GB gratuito | ✅ Ilimitado |
| Integración con Spring Boot | ⚠️ Compleja | ✅ `spring-cloud-aws` o SDK |
| Costo | Gratis hasta 15GB | Muy bajo (~$0.023/GB/mes S3) |
| Generación de thumbnails | ❌ | ✅ AWS Lambda / GCS Cloud Functions |

### Estrategia de carpetas en S3/GCS

```
kinevid-app/
└── patients/
    └── {patient_id}/
        └── sessions/
            └── {session_id}/
                └── analysis/
                    └── {analysis_id}/
                        ├── photo_1.jpg
                        ├── photo_2.jpg
                        ├── ...
                        └── report_{fecha}.pdf
```

### Implementación en Spring Boot

```xml
<!-- Dependencia en pom.xml -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.25.x</version>
</dependency>
```

- El backend sube la imagen y devuelve la URL pública o firmada
- La URL se almacena en `analysis_photo.photo_url`
- El frontend muestra la imagen directamente desde esa URL
- Las URLs firmadas expiran en N horas para mayor seguridad

### Alternativa económica para MVP: **Cloudinary**

Si se quiere empezar rápido con un plan gratuito generoso:
- 25GB de almacenamiento gratis
- Transformaciones de imagen automáticas
- SDK Java disponible
- URLs públicas estables

---

## 📅 Orden de ejecución recomendado

```
✅ FASE 1  → Backend Pacientes + Servicios          (COMPLETADO)
🔄 FASE 2  → Frontend Módulo Servicios              (EN PROGRESO)
⏳ FASE 3  → Frontend Módulo Pacientes
⏳ FASE 4  → Backend Historia Clínica
⏳ FASE 5  → Frontend Historia Clínica
⏳ FASE 6  → Backend Análisis de Imagen
⏳ FASE 7  → Frontend Análisis de Imagen (Canvas)
⏳ FASE 8  → Backend Generación PDF
⏳ FASE 9  → Frontend Descarga Reportes
```

---

## 🔗 Integración entre módulos (visión global)

```
PACIENTE
  │
  ├── Datos personales (patient)
  │
  ├── HISTORIA CLÍNICA (clinical_session) [1..N sesiones]
  │    ├── Servicios aplicados (session_service → medical_service)
  │    ├── Evaluación + tratamiento + evolución
  │    └── ANÁLISIS DE IMAGEN (image_analysis) [0..1 por sesión]
  │         ├── Fotos (analysis_photo) [1..6 fotos]
  │         │    └── Anotaciones JSON (trazos + ángulos)
  │         └── Reporte PDF (URL almacenada en S3/GCS)
  │
  └── Estado del paciente: ACTIVE / INACTIVE / DISCHARGE
```

---

*Documento generado el 22/04/2026 — Revisión 1.0*

