# 📋 KINEVID APP — Plan Maestro de Desarrollo
> **Stack:** Angular 13 · Spring Boot 3 · Java 17 · PostgreSQL  
> **Fecha de última actualización:** Abril 2026 — Revisión 2.0  
> **Autor:** Douglas Cristhian Javieri Vino  
> **Arquitectura:** Monolito Modular (preparado para extracción futura a microservicios)

---

## 🗂️ ÍNDICE

1. [Estado actual del sistema](#1-estado-actual-del-sistema)
2. [Decisiones de arquitectura](#2-decisiones-de-arquitectura)
3. [Diseño completo de base de datos](#3-diseño-completo-de-base-de-datos)
4. [Flujo general del módulo de pacientes](#4-flujo-general-del-módulo-de-pacientes)
5. [Fases de desarrollo](#5-fases-de-desarrollo)
6. [Módulos del sistema](#6-módulos-del-sistema)
7. [Permisos y roles por módulo](#7-permisos-y-roles-por-módulo)
8. [Estrategia de almacenamiento de imágenes](#8-estrategia-de-almacenamiento-de-imágenes)

---

## 1. Estado actual del sistema

### ✅ Completado

| Módulo | Backend | Frontend |
|---|---|---|
| Autenticación (JWT + Refresh Token) | ✅ | ✅ |
| Usuarios (CRUD + estado) | ✅ | ✅ |
| Roles y Permisos (RBAC granular) | ✅ | ✅ |
| Empleados (CRUD + asignación de usuario) | ✅ | ✅ |
| Pacientes (CRUD + paginación + filtros) | ✅ | ✅ |
| Servicios del consultorio (CRUD + paginación) | ✅ | ✅ |

### 🔄 En desarrollo / Siguiente paso

| Fase | Descripción | Estado |
|---|---|---|
| FASE 4 | Backend: Episodios Clínicos + Historia Clínica | ⏳ Próxima |
| FASE 5 | Frontend: Episodios + Historia Clínica | ⏳ Pendiente |
| FASE 6 | Backend: Análisis de Imagen | ⏳ Pendiente |
| FASE 7 | Frontend: Análisis de Imagen (Canvas HTML5) | ⏳ Pendiente |
| FASE 8 | Backend: Generación de Reportes PDF (iText 7) | ⏳ Pendiente |
| FASE 9 | Frontend: Descarga y visualización de reportes | ⏳ Pendiente |

### 🏗️ Infraestructura implementada

- **DataLoader** con seed automático de permisos, roles ROOT/ADMIN/FISIOTERAPEUTA/**RECEPCIONISTA**
- `syncPermissionsToExistingFullAccessRoles()` → ADMIN y ROOT reciben nuevos permisos automáticamente al reiniciar
- **AuditableEntity** base con `createdDate`, `modifiedDate`, `createdBy`, `modifiedBy`, `deleted`
- **JWT** con access token (10 min) + refresh token (7 días)
- **CORS** configurado para `http://localhost:4200`
- `spring.jpa.hibernate.ddl-auto=update` (se migrará a **Flyway** en Fase 4)

---

## 2. Decisiones de arquitectura

### 2.1 Monolito Modular (decisión confirmada)

Se mantiene el **monolito modular** para todas las fases del plan. Bounded contexts definidos:

| Contexto | Paquete backend | Módulo Angular |
|---|---|---|
| IAM (auth, users, roles, perms) | `auth`, `role`, `p`, `ur`, `rp` | `management-user` |
| Staff (empleados) | `emp` | `management-user/employee` |
| Clinical Catalog (servicios) | `svc` | `management-pacient/medical-service` |
| Patient | `pat` | `management-pacient/patient` |
| Clinical (episodios + sesiones) | `clinical` | `management-pacient/patient/clinical` |
| Imaging (análisis + fotos) | `imaging` | `management-pacient/patient/clinical/imaging` |
| Reporting (PDF) | `reporting` | integrado en imaging |

> **Gatillos para extraer microservicios (futuro):** alta concurrencia en imágenes/PDF, equipos separados, o integración con sistemas externos de salud.

### 2.2 Gestión del esquema de BD

A partir de la Fase 4 se introduce **Flyway** para control de migraciones versionadas. Se cambia `ddl-auto=update` a `ddl-auto=validate`.

### 2.3 Almacenamiento de imágenes

**Cloudinary** para MVP (plan gratuito 25GB, SDK Java, URLs públicas estables). Interfaz `StorageService` abstraída para cambio futuro a AWS S3 / GCS sin modificar lógica de negocio.

---

## 3. Diseño completo de base de datos

### 3.1 Tablas existentes

```
users → user_role → role → role_permission → permission
employee ←── users
patient
medical_service
```

### 3.2 Tablas nuevas — Historia Clínica con Episodios

---

#### 📌 Tabla: `clinical_episode` ⭐ NUEVA

Un **episodio** agrupa todas las sesiones de una misma etapa de atención del paciente.  
Permite distinguir claramente cuando un paciente que tuvo alta médica regresa meses o años después.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_CLINICAL_EPISODE_ID` |
| patient_id | BIGINT | FK → patient.id, NOT NULL | Paciente al que pertenece |
| episode_number | INTEGER | NOT NULL | Número de episodio del paciente (1, 2, 3…) |
| start_date | DATE | NOT NULL | Fecha de inicio del episodio |
| end_date | DATE | NULL | Fecha de cierre (NULL = episodio activo) |
| reason_for_admission | VARCHAR(500) | NOT NULL | Motivo de ingreso al episodio |
| discharge_reason | VARCHAR(500) | NULL | Motivo del alta / cierre del episodio |
| episode_status | VARCHAR(20) | NOT NULL | `ACTIVE` / `CLOSED` |
| created_at | TIMESTAMP | NOT NULL | Auditoría |
| updated_at | TIMESTAMP | NOT NULL | Auditoría |
| created_by | VARCHAR(80) | NOT NULL | Auditoría |
| updated_by | VARCHAR(80) | NOT NULL | Auditoría |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Eliminación lógica |

**Constraints:**
- `UNIQUE (patient_id, episode_number)`
- Solo puede existir **un episodio ACTIVE** por paciente a la vez (validado en servicio)

---

#### 📌 Tabla: `clinical_session` (actualizada)

Cada sesión de consulta. Ahora pertenece a un **episodio** en lugar de directamente a un paciente.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_CLINICAL_SESSION_ID` |
| episode_id | BIGINT | FK → clinical_episode.id, NOT NULL | Episodio al que pertenece |
| employee_id | BIGINT | FK → employee.id, NOT NULL | Fisioterapeuta que atiende |
| session_date | DATE | NOT NULL | Fecha de la sesión |
| session_number | INTEGER | NOT NULL | Número de sesión **dentro del episodio** (1, 2, 3…) |
| reason_for_consultation | VARCHAR(500) | NOT NULL | Motivo de consulta de esta sesión |
| relevant_background | TEXT | NULL | Antecedentes relevantes |
| kinesiological_evaluation | TEXT | NULL | Evaluación kinesiológica |
| treatment_applied | TEXT | NULL | Tratamiento aplicado |
| observations | TEXT | NULL | Observaciones del profesional |
| evolution | TEXT | NULL | Evolución del paciente |
| has_image_analysis | BOOLEAN | NOT NULL, DEFAULT FALSE | Si tiene análisis de imagen |
| session_status | VARCHAR(30) | NOT NULL | `OPEN` / `CLOSED` / `CANCELLED` |
| created_at | TIMESTAMP | NOT NULL | Auditoría |
| updated_at | TIMESTAMP | NOT NULL | Auditoría |
| created_by | VARCHAR(80) | NOT NULL | Auditoría |
| updated_by | VARCHAR(80) | NOT NULL | Auditoría |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Eliminación lógica |

**Constraints:**
- `UNIQUE (episode_id, session_number)`
- `CHECK (session_number > 0)`

---

#### 📌 Tabla: `session_service` (sin cambios)

Relación N:M entre una sesión y los servicios aplicados.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_SESSION_SERVICE_ID` |
| clinical_session_id | BIGINT | FK → clinical_session.id, NOT NULL | Sesión clínica |
| medical_service_id | BIGINT | FK → medical_service.id, NOT NULL | Servicio aplicado |
| quantity | INTEGER | NOT NULL, DEFAULT 1 | Cantidad de aplicaciones |
| notes | VARCHAR(300) | NULL | Notas específicas del servicio |

**Constraint único:** `(clinical_session_id, medical_service_id)`

---

#### 📌 Tabla: `image_analysis` (sin cambios)

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_IMAGE_ANALYSIS_ID` |
| clinical_session_id | BIGINT | FK → clinical_session.id, NOT NULL | Sesión clínica |
| analysis_date | TIMESTAMP | NOT NULL | Fecha/hora del análisis |
| foot_side | VARCHAR(10) | NOT NULL | `LEFT` / `RIGHT` / `BOTH` |
| diagnosis | VARCHAR(50) | NOT NULL | `NORMAL` / `PRONATION` / `SUPINATION` |
| angle_left | DECIMAL(5,2) | NULL | Ángulo pie izquierdo |
| angle_right | DECIMAL(5,2) | NULL | Ángulo pie derecho |
| notes | TEXT | NULL | Notas del análisis |
| report_generated | BOOLEAN | NOT NULL, DEFAULT FALSE | Si se generó PDF |
| report_url | VARCHAR(500) | NULL | URL del reporte generado |
| created_at / updated_at / created_by / updated_by | — | NOT NULL | Auditoría |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Eliminación lógica |

---

#### 📌 Tabla: `analysis_photo` (sin cambios)

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_ANALYSIS_PHOTO_ID` |
| image_analysis_id | BIGINT | FK → image_analysis.id, NOT NULL | Análisis al que pertenece |
| photo_order | INTEGER | NOT NULL | Orden de foto (1 a 6) |
| photo_url | VARCHAR(500) | NOT NULL | URL pública (Cloudinary / S3) |
| storage_file_id | VARCHAR(200) | NOT NULL | ID del archivo en el proveedor |
| annotations_json | TEXT | NULL | JSON con trazos y ángulos |
| is_selected | BOOLEAN | NOT NULL, DEFAULT FALSE | Foto seleccionada para reporte |
| created_at | TIMESTAMP | NOT NULL | Auditoría |

**Constraints:**
- `UNIQUE (image_analysis_id, photo_order)`
- `CHECK (photo_order BETWEEN 1 AND 6)`

---

### 3.3 ERD completo actualizado

```
users ──────────────── employee
  │                       │
  └── user_role           │
       │                  │
      role                ▼
       │          clinical_session ◄──── clinical_episode ◄──── patient
      role_permission      │                                        │
       │                   ├── session_service ──► medical_service  │
    permission             └── image_analysis                       │
                                    │                               │
                              analysis_photo                        │
                                                         (episodio agrupa
                                                          sesiones de una
                                                          etapa de atención)
```

### 3.4 Flujo de estados del paciente vs episodios

```
Paciente: ACTIVE
  └── Episodio 1: ACTIVE
        └── Sesiones 1, 2, 3, 4 → CLOSED
              └── Análisis de imagen (si aplica)
  └── Episodio 1: CLOSED → paciente: DISCHARGE

Tiempo después → paciente regresa:
  └── Reactivar paciente → ACTIVE
  └── Episodio 2: ACTIVE (nuevo)
        └── Sesiones 1, 2, 3 (renumeradas en el episodio 2)
              └── Ve historial del Episodio 1 (solo lectura)
```

### 3.5 Reglas de negocio clave

| Regla | Descripción |
|---|---|
| Un episodio activo por paciente | No se puede abrir Episodio 2 si el Episodio 1 sigue ACTIVE |
| Cierre de episodio | Cerrar episodio cambia estado del paciente a DISCHARGE |
| Reapertura | Reactivar paciente DISCHARGE → crear nuevo episodio automáticamente |
| session_number | Se numera desde 1 dentro de cada episodio (no global) |
| Sesiones editables | Solo si `session_status = OPEN` |
| Historial preservado | Los episodios cerrados son de solo lectura en UI |

---

## 4. Flujo general del módulo de pacientes

### 4.1 Primera visita de un paciente

```
[1] RECEPCIÓN
    └── Buscar paciente por nombre o CI
         │
         ├── ENCONTRADO (DISCHARGE) → botón "Reactivar"
         │     └── Sistema crea Episodio N (nuevo) automáticamente
         │
         └── NO ENCONTRADO
              └── [2] REGISTRAR NUEVO PACIENTE
                       └── Guardar → Estado: ACTIVE
                            └── Sistema crea Episodio 1 automáticamente

[3] VER PERFIL DEL PACIENTE
    ├── Datos personales [editar]
    ├── Episodio actual (ACTIVE)
    │     └── Lista de sesiones del episodio (fecha, motivo, terapeuta, estado)
    │          └── [4] CREAR NUEVA SESIÓN
    │                   • Fecha (automática: hoy)
    │                   • Motivo de consulta
    │                   • Antecedentes relevantes
    │                   • Seleccionar fisioterapeuta
    │                   └── Guardar → Estado: OPEN
    └── Episodios anteriores (colapsados, solo lectura)
          └── Episodio 1 (Mar 2024 - Jun 2024)
               └── Sesiones históricas + análisis + reportes

[5] DURANTE LA CONSULTA
    └── Editar sesión OPEN
         • Evaluación kinesiológica
         • Seleccionar servicios (multiselect)
         • Tratamiento aplicado · Observaciones · Evolución
         └── ¿Requiere análisis de imagen?
              ├── NO → Cerrar sesión → CLOSED
              └── SÍ → [6] MÓDULO ANÁLISIS DE IMAGEN
                           └── [7] GENERAR REPORTE PDF
                                └── Cerrar sesión → CLOSED

[8] CERRAR EPISODIO (Alta Médica)
    └── Ingresar motivo de alta
        └── Episodio → CLOSED · Paciente → DISCHARGE
```

### 4.2 Visitas siguientes (paciente ya existe)

```
[1] Buscar paciente (nombre / CI) → abrirlo
    ├── Si ACTIVE con episodio activo → continuar flujo paso [4]
    └── Si DISCHARGE → botón "Reactivar" → Episodio nuevo → paso [4]
```

---

## 5. Fases de desarrollo

### ✅ FASE 1 — Backend: Pacientes y Servicios (COMPLETADA)
- Entidades `Patient`, `MedicalService` con enums y auditoría
- CRUD completo con paginación y búsqueda
- Permisos en DataLoader; roles ROOT, ADMIN, FISIOTERAPEUTA, RECEPCIONISTA

---

### ✅ FASE 2 — Frontend: Módulo Servicios (COMPLETADA)
- Lista paginada con filtros (estado, categoría)
- Formulario Agregar / Actualizar (3 columnas, responsivo)
- Cambiar estado y eliminar (lógico)
- Módulo en `management-pacient/medical-service/`

---

### ✅ FASE 3 — Frontend: Módulo Pacientes (COMPLETADA)
- Lista paginada con filtro por estado
- Formulario Agregar / Actualizar (4 secciones, 3 columnas, responsivo)
- Cambiar estado (ciclo ACTIVE → INACTIVE → DISCHARGE) y eliminar lógico
- Módulo en `management-pacient/patient/`

---

### 🔵 FASE 4 — Backend: Episodios Clínicos + Historia Clínica (PRÓXIMA)

**Migración de esquema:**
- [ ] Introducir **Flyway** (`V1__init_schema.sql` + migraciones incrementales)
- [ ] Cambiar `ddl-auto=update` → `ddl-auto=validate`

**Nuevas entidades:**
- [ ] `ClinicalEpisode` + enum `EpisodeStatus` (ACTIVE / CLOSED)
- [ ] `ClinicalSession` + enum `SessionStatus` (OPEN / CLOSED / CANCELLED)
- [ ] `SessionService` (relación N:M sesión ↔ servicio médico)

**Nuevos endpoints REST:**

*Episodios:*
- `POST   /api/clinical-episode/create`                           — abrir nuevo episodio
- `GET    /api/clinical-episode/patient/{patientId}`              — episodios del paciente (paginado)
- `GET    /api/clinical-episode/{id}`                             — detalle episodio
- `PATCH  /api/clinical-episode/{id}/close`                       — cerrar episodio (alta)
- `POST   /api/clinical-episode/patient/{patientId}/reactivate`   — reactivar paciente + nuevo episodio

*Sesiones:*
- `POST   /api/clinical-session/create`
- `GET    /api/clinical-session/episode/{episodeId}`              — sesiones del episodio (paginado)
- `GET    /api/clinical-session/{id}`
- `PUT    /api/clinical-session/update/{id}`
- `PATCH  /api/clinical-session/{id}/status`
- `DELETE /api/clinical-session/delete/{id}`

*Servicios de sesión:*
- `POST   /api/clinical-session/{id}/services`                    — asignar/reemplazar servicios
- `GET    /api/clinical-session/{id}/services`                    — listar servicios de la sesión

**Nuevos permisos en DataLoader:**
```
CREATE_EPISODE, VIEW_EPISODE, CLOSE_EPISODE, LIST_EPISODE
CREATE_CLINICAL_SESSION, VIEW_CLINICAL_SESSION
UPDATE_CLINICAL_SESSION, DELETE_CLINICAL_SESSION
LIST_CLINICAL_SESSION, MANAGE_SESSION_SERVICES
```

**Asignación a roles:**
- ROOT, ADMIN, FISIOTERAPEUTA: todos los permisos clínicos
- RECEPCIONISTA: `LIST_EPISODE`, `VIEW_EPISODE`, `CREATE_EPISODE` (puede abrir episodio/registrar visita)

---

### 🔵 FASE 5 — Frontend: Episodios + Historia Clínica

**Estructura de carpetas:**
```
patient/
└── clinical/
    ├── clinical.module.ts
    ├── clinical-routing.module.ts
    ├── episode-list/                     ← Lista de episodios del paciente
    │   ├── episode-list.component.ts
    │   ├── episode-list.component.html   ← Cabecera paciente + episodios (acordeón)
    │   └── episode-list.component.scss
    ├── add-episode/                      ← Abrir nuevo episodio / reactivar
    │   └── ...
    ├── session-list/                     ← Sesiones de un episodio
    │   ├── session-list.component.ts
    │   ├── session-list.component.html
    │   └── session-list.component.scss
    ├── add-session/                      ← Nueva sesión dentro del episodio
    │   └── ...
    └── view-session/                     ← Ver / editar sesión (+ servicios)
        └── ...
```

**Navegación Angular:**
```
/management-pacient/patients                → lista pacientes
/management-pacient/patients/:id/clinical   → episodios del paciente
/management-pacient/patients/:id/clinical/:episodeId/sessions          → sesiones
/management-pacient/patients/:id/clinical/:episodeId/sessions/:sessionId → detalle
```

**Pantalla Episode List:**
- Cabecera con datos del paciente (nombre, CI, edad, estado)
- Episodio activo resaltado, con botón "Nueva Sesión"
- Episodios cerrados en acordeón (colapsados, solo lectura)
- Botón "Reactivar" si paciente en DISCHARGE
- Botón "Dar de Alta (cerrar episodio)" en episodio activo

**Pantalla View Session:**
- Formulario editable (solo si `session_status = OPEN`)
- Sección evaluación + tratamiento + observaciones + evolución
- Multiselect de servicios aplicados (con cantidad y notas)
- Botón "Ir a Análisis de Imagen" (si aplica)
- Botón "Cerrar Sesión"

---

### 🔵 FASE 6 — Backend: Análisis de Imagen

**Nuevas entidades:**
- [ ] `ImageAnalysis` + enums `FootSide` (LEFT / RIGHT / BOTH), `Diagnosis` (NORMAL / PRONATION / SUPINATION)
- [ ] `AnalysisPhoto` con `annotations_json` (TEXT)
- [ ] `StorageService` interfaz + implementación Cloudinary

**Endpoints REST:**
- `POST   /api/image-analysis/create`
- `GET    /api/image-analysis/session/{sessionId}`
- `GET    /api/image-analysis/{id}`
- `PUT    /api/image-analysis/update/{id}`
- `POST   /api/image-analysis/{id}/photos`                    — subir foto (multipart)
- `DELETE /api/image-analysis/{id}/photos/{photoId}`
- `PATCH  /api/image-analysis/{id}/photos/{photoId}/select`   — marcar foto principal
- `PUT    /api/image-analysis/{id}/photos/{photoId}/annotations` — guardar trazos JSON

**Nuevos permisos:**
```
CREATE_IMAGE_ANALYSIS, VIEW_IMAGE_ANALYSIS
UPDATE_IMAGE_ANALYSIS, DELETE_IMAGE_ANALYSIS
MANAGE_PHOTOS, ANNOTATE_PHOTO
```

---

### 🔵 FASE 7 — Frontend: Análisis de Imagen (Canvas HTML5)

**Estructura:**
```
clinical/
└── imaging/
    ├── imaging.module.ts
    ├── imaging-routing.module.ts
    ├── image-analysis/
    │   ├── image-analysis.component.ts     ← Grilla 6 fotos + panel diagnóstico
    │   ├── image-analysis.component.html
    │   └── image-analysis.component.scss
    ├── photo-canvas/                        ← Canvas interactivo
    │   ├── photo-canvas.component.ts        ← Trazos + ángulos + anotaciones
    │   ├── photo-canvas.component.html
    │   └── photo-canvas.component.scss
    └── report-preview/                      ← Vista previa antes de generar PDF
        └── ...
```

**Funcionalidades Canvas:**
- Cargar/tomar foto (input file o cámara web si disponible)
- Grilla de miniaturas (hasta 6); click activa la foto en el canvas
- Hasta 2 trazos verticales con drag
- Herramienta de ángulo: marcar 3 puntos → calcula y muestra grados
- Serialización automática de anotaciones a JSON → guardado en backend
- Modo de diagnóstico visual: highlight por resultado (verde/naranja/rojo)

---

### 🔵 FASE 8 — Backend: Generación de Reportes PDF (iText 7)

**Implementación:**
- [ ] Dependencia `itext7-core` en `pom.xml`
- [ ] `ReportService.generateAnalysisReport(Long analysisId)` → devuelve `byte[]`
- [ ] Sube el PDF a Cloudinary (misma interfaz `StorageService`)
- [ ] Guarda URL en `image_analysis.report_url`

**Contenido del PDF:**
1. Encabezado: logo del consultorio, fecha, número de reporte
2. Datos del paciente (nombre, CI, edad, género)
3. Datos del fisioterapeuta
4. Episodio y número de sesión
5. Servicios aplicados en la sesión
6. Historia clínica de la sesión (evaluación, tratamiento, observaciones, evolución)
7. Imagen(es) seleccionada(s) con trazos y ángulos
8. Diagnóstico: NORMAL / PRONACIÓN / SUPINACIÓN
9. Pie de página: fecha de generación y espacio para firma

**Endpoint:**
- `GET /api/image-analysis/{id}/report` → genera (si no existe) o devuelve URL del PDF

---

### 🔵 FASE 9 — Frontend: Descarga y Visualización de Reportes

- [ ] Botón "Generar / Descargar Reporte PDF" en la sesión y en el análisis
- [ ] Vista previa del PDF en modal (iframe con URL de Cloudinary)
- [ ] Indicador visual en la tabla de sesiones si la sesión tiene reporte generado
- [ ] Historial de reportes por paciente (lista de URLs descargables)

---

## 6. Módulos del sistema

### Sidebar de navegación

```
📦 KineVid App
│
├── 🏠 Inicio
│
├── 👥 Gestión de Usuarios  [solo ADMIN / ROOT]
│   ├── Usuarios
│   ├── Empleados
│   ├── Roles
│   └── Permisos
│
└── 🏥 Gestión de Pacientes  [ADMIN / ROOT / FISIOTERAPEUTA / RECEPCIONISTA]
    ├── 📋 Servicios del Consultorio
    └── 🧑‍⚕️ Pacientes
          └── Perfil del Paciente
                └── 📂 Episodios Clínicos
                      └── 📝 Sesiones
                            └── 🦶 Análisis de Imagen
                                  └── 📄 Reporte PDF
```

---

## 7. Permisos y roles por módulo

### Permisos existentes (implementados)

| Módulo | Permisos |
|---|---|
| Usuarios | `CREATE_USER`, `VIEW_USER`, `UPDATE_USER`, `DELETE_USER`, `LIST_USER`, `CHANGE_USER_STATUS` |
| Roles | `CREATE_ROLE`, `READ_ROLE`, `UPDATE_ROLE`, `DELETE_ROLE`, `LIST_ROLE`, `CHANGE_ROLE_STATUS` |
| Permisos | `CREATE_PERMISSION`, `READ_PERMISSION`, `UPDATE_PERMISSION`, `DELETE_PERMISSION`, `LIST_PERMISSION`, `CHANGE_PERMISSION_STATUS`, `ASSIGN_PERMISSION_TO_ROLE`, `REMOVE_PERMISSION_FROM_ROLE` |
| Empleados | `CREATE_EMPLOYEE`, `VIEW_EMPLOYEE`, `UPDATE_EMPLOYEE`, `DELETE_EMPLOYEE`, `LIST_EMPLOYEE`, `CHANGE_EMPLOYEE_STATUS`, `ASSIGN_USER_TO_EMPLOYEE`, `REMOVE_USER_FROM_EMPLOYEE` |
| Pacientes | `CREATE_PATIENT`, `VIEW_PATIENT`, `UPDATE_PATIENT`, `DELETE_PATIENT`, `LIST_PATIENT`, `CHANGE_PATIENT_STATUS` |
| Servicios | `CREATE_SERVICE`, `VIEW_SERVICE`, `UPDATE_SERVICE`, `DELETE_SERVICE`, `LIST_SERVICE`, `CHANGE_SERVICE_STATUS` |

### Permisos a agregar — Fases 4-9

| Módulo | Permisos nuevos |
|---|---|
| Episodios Clínicos | `CREATE_EPISODE`, `VIEW_EPISODE`, `CLOSE_EPISODE`, `LIST_EPISODE` |
| Historia Clínica (sesiones) | `CREATE_CLINICAL_SESSION`, `VIEW_CLINICAL_SESSION`, `UPDATE_CLINICAL_SESSION`, `DELETE_CLINICAL_SESSION`, `LIST_CLINICAL_SESSION`, `MANAGE_SESSION_SERVICES` |
| Análisis de Imagen | `CREATE_IMAGE_ANALYSIS`, `VIEW_IMAGE_ANALYSIS`, `UPDATE_IMAGE_ANALYSIS`, `DELETE_IMAGE_ANALYSIS`, `MANAGE_PHOTOS`, `ANNOTATE_PHOTO` |
| Reportes | `GENERATE_REPORT`, `VIEW_REPORT`, `DOWNLOAD_REPORT` |

### Matriz de roles

| Área | ROOT | ADMIN | FISIOTERAPEUTA | RECEPCIONISTA |
|---|---|---|---|---|
| Usuarios / Roles / Permisos | ✅ | ✅ | ❌ | ❌ |
| Empleados | ✅ | ✅ | ❌ | ❌ |
| Pacientes — CRUD completo | ✅ | ✅ | ✅ | CREATE + VIEW + LIST |
| Servicios — ver y listar | ✅ | ✅ | ✅ | ✅ |
| Servicios — crear / editar / eliminar | ✅ | ✅ | ❌ | ❌ |
| Episodios — crear / ver / listar | ✅ | ✅ | ✅ | ✅ |
| Episodios — cerrar (alta médica) | ✅ | ✅ | ✅ | ❌ |
| Historia Clínica — CRUD sesiones | ✅ | ✅ | ✅ | ❌ |
| Análisis de Imagen | ✅ | ✅ | ✅ | ❌ |
| Reportes PDF | ✅ | ✅ | ✅ | ❌ |

---

## 8. Estrategia de almacenamiento de imágenes

### Decisión: Cloudinary (MVP)

| Criterio | Cloudinary | AWS S3 / GCS |
|---|---|---|
| Plan gratuito | ✅ 25 GB | ❌ Solo de pago |
| SDK Java | ✅ | ✅ |
| URLs públicas estables | ✅ | ✅ (firmadas o públicas) |
| Transformaciones automáticas | ✅ (thumbnails, resize) | ⚠️ Requiere Lambda |
| Configuración inicial | ✅ Muy simple | ⚠️ Más compleja |
| Escalabilidad producción | ⚠️ Limitada en free | ✅ Ilimitada |

> **Migración futura:** La interfaz `StorageService` abstrae la implementación.  
> Cambiar de Cloudinary a S3 no requiere modificar lógica de negocio.

### Estructura de carpetas en Cloudinary

```
kinevid/
└── patients/
    └── {patient_id}/
        └── episodes/
            └── {episode_id}/
                └── sessions/
                    └── {session_id}/
                        └── analysis/
                            └── {analysis_id}/
                                ├── photo_1.jpg
                                ├── photo_2.jpg
                                └── report_YYYYMMDD.pdf
```

### Dependencia Maven

```xml
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.36.0</version>
</dependency>
```

---

## 📅 Hoja de ruta actualizada

```
✅ FASE 1  → Backend: Pacientes + Servicios                    COMPLETADO
✅ FASE 2  → Frontend: Módulo Servicios                        COMPLETADO
✅ FASE 3  → Frontend: Módulo Pacientes                        COMPLETADO
🔵 FASE 4  → Backend: Episodios + Historia Clínica + Flyway    PRÓXIMA
🔵 FASE 5  → Frontend: Episodios + Sesiones + Servicios        PENDIENTE
🔵 FASE 6  → Backend: Análisis de Imagen + Cloudinary          PENDIENTE
🔵 FASE 7  → Frontend: Canvas HTML5 + Anotaciones              PENDIENTE
🔵 FASE 8  → Backend: Generación PDF (iText 7)                 PENDIENTE
🔵 FASE 9  → Frontend: Descarga + Vista previa de reportes     PENDIENTE
```

---

## 🔗 Integración entre módulos (visión global actualizada)

```
PACIENTE (patient)
  │
  ├── Datos personales [CRUD]
  │
  └── EPISODIOS CLÍNICOS (clinical_episode) [1..N por paciente]
       │
       ├── Episodio ACTIVE  ──────────────────────────────────────────┐
       │     └── SESIONES (clinical_session) [1..N por episodio]       │
       │           ├── Número de sesión (relativo al episodio)         │
       │           ├── Fisioterapeuta (employee)                       │
       │           ├── SERVICIOS APLICADOS (session_service)           │
       │           │     └── medical_service                           │
       │           ├── Evaluación · Tratamiento · Evolución            │
       │           └── ANÁLISIS DE IMAGEN (image_analysis) [0..1]      │
       │                 ├── FOTOS (analysis_photo) [1..6]             │
       │                 │     └── annotations_json (trazos + ángulos) │
       │                 └── REPORTE PDF → Cloudinary URL              │
       │                                                               │
       └── Episodios CLOSED (solo lectura) ◄─────────────────────────┘
             └── Historial completo preservado
```

---

*Plan actualizado el 23/04/2026 — Revisión 2.0*  
*Cambio principal: arquitectura de episodios clínicos para soporte de múltiples etapas de atención por paciente*
