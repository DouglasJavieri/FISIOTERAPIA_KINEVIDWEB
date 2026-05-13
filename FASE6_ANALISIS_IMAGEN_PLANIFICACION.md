# 📊 FASE 6 — Análisis de Imagen (Postura y Pisada)
## Planificación Profesional Detallada

> **Documento:** Planificación Arquitectónica — Módulo de Análisis Biomecánico de Pisada  
> **Fecha:** Mayo 2026  
> **Versión:** 1.0  
> **Preparado para:** Sistema KineVid — Clínica de Fisioterapia  

---

## 📑 ÍNDICE

1. [Análisis de Requisitos](#1-análisis-de-requisitos)
2. [Recomendaciones Arquitectónicas](#2-recomendaciones-arquitectónicas)
3. [Diseño de Base de Datos](#3-diseño-de-base-de-datos)
4. [Flujo de Negocio](#4-flujo-de-negocio)
5. [Pantallas y Componentes Frontend](#5-pantallas-y-componentes-frontend)
6. [Endpoints Backend](#6-endpoints-backend)
7. [Integración con Historia Clínica](#7-integración-con-historia-clínica)
8. [Impacto Mínimo en lo Existente](#8-impacto-mínimo-en-lo-existente)
9. [Hoja de Ruta Detallada](#9-hoja-de-ruta-detallada)

---

## 1. Análisis de Requisitos

### 1.1 Datos Específicos del Análisis de Pisada

El módulo capturará información **exclusivamente cuando se seleccione**:
- **Categoría de Servicio:** `POSTURAL_ANALYSIS`
- **Nombre de Servicio:** `Análisis de Pisada`

**Datos a registrar (nivel sesión clínica):**
```
Análisis de Pisada (FootAnalysis)
├── Antecedentes Relevantes (TEXT)
├── Evaluación Kinésica (TEXT)
├── Observaciones (TEXT)
├── Diagnóstico (VARCHAR 50) — NORMAL / PRONACIÓN / SUPINACIÓN
├── Distancia Intermaleolar (DECIMAL 5,2) — en cm
└── Distancia Intercondílea (DECIMAL 5,2) — en cm
```

### 1.2 Análisis Biomecánico (Tabla Relacionada)

Registro de múltiples evaluaciones **por pie** (izquierdo y derecho):

```
Análisis Biomecánico (BiomechanicalAnalysis)
├── Foot Analysis (relación 1:1)
├── Pie Izquierdo
│   ├── Regla Maleolo Tibial (VARCHAR 50) — NORMAL / VARO / VALGO / OTRO
│   ├── Desgaste de Calzado (TEXT)
│   ├── Palpación de la Tibia (TEXT)
│   └── Marcha (VARCHAR 50) — NORMAL / PRONADOR / SUPINADOR / MIXTO
└── Pie Derecho
    ├── Regla Maleolo Tibial (VARCHAR 50)
    ├── Desgaste de Calzado (TEXT)
    ├── Palpación de la Tibia (TEXT)
    └── Marcha (VARCHAR 50)
```

### 1.3 Evaluación de Huella Plantar (Tabla Relacionada) — ⭐ GENÉRICA

Clasificación del tipo de pie según **índice de huella** — **REGISTRADA UNA SOLA VEZ, NO POR PIE**:

```
Evaluación de Huella Plantar (FootPrintAnalysis)
├── Foot Analysis (relación 1:1)
├── Tipo de Huella (VARCHAR 50 - seleccionar UNO)
│   ├── ÍNDICE_NORMAL
│   ├── ÍNDICE_PIE_PLANO
│   ├── ÍNDICE_PIE_CAVO
│   ├── PIE_PLANO
│   ├── PIE_PLANO_NORMAL
│   ├── PIE_NORMAL
│   ├── PIE_NORMAL_CAVO
│   ├── PIE_CAVO
│   ├── PIE_CAVO_FUERTE
│   └── PIE_CAVO_EXTREMO
├── Notas Adicionales (TEXT - opcional)
└── Fecha de Evaluación (TIMESTAMP)
```

**✅ Cambio importante (Rev.2):** 
- ❌ **ANTES:** Se registraba POR PIE (izquierdo + derecho)
- ✅ **AHORA:** Se registra UNA SOLA VEZ de forma genérica (aplica al paciente/sesión general)
- 📊 **Ventaja:** Menos complejidad, más claridad, el fisioterapeuta evalúa el patrón general

**⚠️ Regla de negocio:** Solo UNO de estos tipos puede ser seleccionado.

### 1.4 Datos de Imagen y Anotaciones

```
Fotos y Trazos (AnalysisPhoto)
├── Hasta 6 fotos máximo
├── Por cada foto
│   ├── URL pública (Cloudinary)
│   ├── Anotaciones JSON
│   │   ├── Trazo 1 (Pie Izquierdo) — línea vertical + ángulo interno + externo
│   │   ├── Trazo 2 (Pie Derecho) — línea vertical + ángulo interno + externo
│   │   └── Coordenadas de los ángulos (x, y, grados)
│   └── Seleccionar para reporte (BOOLEAN)
└── Datos persistibles para el PDF
```

---

## 2. Recomendaciones Arquitectónicas

### 2.1 ✅ Decisión: Mantener Sesión Clínica como Contenedor

**Opción elegida:** El `Analysis de Pisada` será un **subelemento de la sesión clínica**, no una entidad independiente a nivel de paciente o episodio.

**Razones:**
- ✅ **Coherencia:** La sesión clínica ya contiene la mayoría de datos generales
- ✅ **Relación N:1:** Una sesión puede tener múltiples análisis (si el servicio se aplica múltiples veces)
- ✅ **Auditoría:** Se mantiene el contexto de quién, cuándo y por qué se hizo
- ✅ **Reutilización:** El flujo de Historia Clínica no cambia

**Impacto mínimo:** Solo se agrega un campo `has_foot_analysis` a `clinical_session` (ya existe como `has_image_analysis`)

### 2.2 ✅ Decisión: Tres Tablas Específicas para Análisis de Pisada

1. **`foot_analysis`** — Contenedor principal (datos generales del análisis)
2. **`biomechanical_analysis`** — Análisis biomecánico por pie
3. **`footprint_analysis`** — Evaluación de huella plantar por pie

**Ventajas:**
- ✅ Separación de responsabilidades (SoC)
- ✅ Facilita consultas específicas (ej: "pacientes con pie plano")
- ✅ Cumple con 3FN (evita redundancia)
- ✅ Permite reutilización en futuro para otros tipos de análisis

### 2.3 ✅ Decisión: Almacenamiento de Ángulos

Los **ángulos y trazos se guardarán:**
- En el JSON `annotations_json` de `analysis_photo` (para visualización en canvas)
- En tabla `foot_analysis` campos `angle_left_internal`, `angle_left_external`, `angle_right_internal`, `angle_right_external` (DECIMAL para reportes)

**Ventaja:** Se puede recuperar el cálculo del ángulo desde cualquiera de los dos lugares.

### 2.4 ✅ Decisión: No Se Requieren Tablas Adicionales

Con las 3 tablas propuestas (`foot_analysis`, `biomechanical_analysis`, `footprint_analysis`) + la existente `analysis_photo`:

- ✅ Se cubre toda la información requerida
- ✅ Estructura normalizada (3FN)
- ✅ Escalable para futuras extensiones

---

## 3. Diseño de Base de Datos

### 3.1 Tabla: `foot_analysis` ⭐ NUEVA

Contenedor principal del análisis de pisada vinculado a sesión clínica.

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_FOOT_ANALYSIS_ID` |
| clinical_session_id | BIGINT | FK → clinical_session.id, NOT NULL | Sesión a la que pertenece |
| analysis_date | TIMESTAMP | NOT NULL | Fecha/hora del análisis |
| relevant_background | TEXT | NULL | Antecedentes relevantes |
| kinesiological_evaluation | TEXT | NULL | Evaluación kinésica |
| observations | TEXT | NULL | Observaciones generales |
| diagnosis | VARCHAR(50) | NOT NULL | `NORMAL` / `PRONATION` / `SUPINATION` |
| distance_intermaleolar | DECIMAL(5,2) | NULL | Distancia intermaleolar (cm) |
| distance_intercondylar | DECIMAL(5,2) | NULL | Distancia intercondílea (cm) |
| angle_left_internal | DECIMAL(5,2) | NULL | Ángulo interno pie izquierdo |
| angle_left_external | DECIMAL(5,2) | NULL | Ángulo externo pie izquierdo |
| angle_right_internal | DECIMAL(5,2) | NULL | Ángulo interno pie derecho |
| angle_right_external | DECIMAL(5,2) | NULL | Ángulo externo pie derecho |
| report_generated | BOOLEAN | NOT NULL, DEFAULT FALSE | Si tiene PDF generado |
| report_url | VARCHAR(500) | NULL | URL del PDF (Cloudinary) |
| created_at | TIMESTAMP | NOT NULL | Auditoría |
| updated_at | TIMESTAMP | NOT NULL | Auditoría |
| created_by | VARCHAR(80) | NOT NULL | Auditoría |
| updated_by | VARCHAR(80) | NOT NULL | Auditoría |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Eliminación lógica |

**Constraints:**
- `UNIQUE (clinical_session_id)` — Una sesión tiene máximo un análisis de pisada
- `CHECK (diagnosis IN ('NORMAL', 'PRONATION', 'SUPINATION'))`

---

### 3.2 Tabla: `biomechanical_analysis` ⭐ NUEVA

Evaluación biomecánica por pie (derecho e izquierdo).

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_BIOMECHANICAL_ANALYSIS_ID` |
| foot_analysis_id | BIGINT | FK → foot_analysis.id, NOT NULL | Análisis padre |
| foot_side | VARCHAR(10) | NOT NULL | `LEFT` / `RIGHT` |
| tibial_malleolar_rule | VARCHAR(50) | NULL | `NORMAL` / `VARUS` / `VALGUS` / `OTHER` |
| shoe_wear | TEXT | NULL | Descripción del desgaste de calzado |
| tibia_palpation | TEXT | NULL | Hallazgos de palpación tibial |
| gait | VARCHAR(50) | NULL | `NORMAL` / `PRONATOR` / `SUPINATOR` / `MIXED` |
| created_at | TIMESTAMP | NOT NULL | Auditoría |
| updated_at | TIMESTAMP | NOT NULL | Auditoría |

**Constraints:**
- `UNIQUE (foot_analysis_id, foot_side)` — Máximo uno por pie
- `CHECK (foot_side IN ('LEFT', 'RIGHT'))`

---

### 3.3 Tabla: `footprint_analysis` ⭐ NUEVA

Clasificación de huella plantar (índices de Staheli).

| Columna | Tipo | Restricción | Descripción |
|---|---|---|---|
| id | BIGINT | PK, NOT NULL | Secuencia `SEQ_FOOTPRINT_ANALYSIS_ID` |
| foot_analysis_id | BIGINT | FK → foot_analysis.id, NOT NULL | Análisis padre |
| foot_side | VARCHAR(10) | NOT NULL | `LEFT` / `RIGHT` |
| index_normal | BOOLEAN | NOT NULL, DEFAULT FALSE | Índice normal |
| index_flat_foot | BOOLEAN | NOT NULL, DEFAULT FALSE | Índice pie plano |
| index_cavus_foot | BOOLEAN | NOT NULL, DEFAULT FALSE | Índice pie cavo |
| flat_foot | BOOLEAN | NOT NULL, DEFAULT FALSE | Pie plano |
| flat_foot_normal | BOOLEAN | NOT NULL, DEFAULT FALSE | Pie plano-normal |
| normal_foot | BOOLEAN | NOT NULL, DEFAULT FALSE | Pie normal |
| normal_cavus_foot | BOOLEAN | NOT NULL, DEFAULT FALSE | Pie normal-cavo |
| cavus_foot | BOOLEAN | NOT NULL, DEFAULT FALSE | Pie cavo |
| cavus_foot_strong | BOOLEAN | NOT NULL, DEFAULT FALSE | Pie cavo fuerte |
| cavus_foot_extreme | BOOLEAN | NOT NULL, DEFAULT FALSE | Pie cavo extremo |
| created_at | TIMESTAMP | NOT NULL | Auditoría |
| updated_at | TIMESTAMP | NOT NULL | Auditoría |

**Constraints:**
- `UNIQUE (foot_analysis_id, foot_side)`
- **Regla de negocio:** Exactamente UNO de los campos booleanos debe ser `true` (validado en servicio)

---

### 3.4 Modificación: `clinical_session` (existente)

Agregar campos de control:

| Columna Nueva | Tipo | Restricción | Descripción |
|---|---|---|---|
| has_foot_analysis | BOOLEAN | NOT NULL, DEFAULT FALSE | Si tiene análisis de pisada |
| has_image_annotations | BOOLEAN | NOT NULL, DEFAULT FALSE | Si las fotos tienen trazos |

---

### 3.5 ERD Actualizado

```
clinical_session
    │
    ├── session_service ──► medical_service
    │
    └── foot_analysis ◄────────────────────────┐
            │                                  │
            ├── analysis_photo               NUEVO
            │     └── annotations_json       
            │     (trazos + ángulos JSON)    
            │                                │
            ├── biomechanical_analysis ────┤
            │   ├── pie LEFT                │
            │   └── pie RIGHT               │
            │                                │
            └── footprint_analysis ────────┤
                ├── pie LEFT                 │
                └── pie RIGHT                │
```

---

## 4. Flujo de Negocio

### 4.1 Precondición: Servicio de "Análisis de Pisada"

El análisis de pisada **solo se activa** si en la sesión clínica se selecciona:
- Categoría: `POSTURAL_ANALYSIS`
- Nombre: `Análisis de Pisada`

En `clinical_session`, el campo `has_foot_analysis` cambia a `true`.

### 4.2 Flujo completo (Optimizado con Modal)

```
[SESIÓN CLÍNICA ABIERTA - PASO 1]
  └── Llenar: Motivo, Fisioterapeuta, Servicios
      └── ¿Se selecciona "Análisis de Pisada"?
          │
          ├─ NO → Avanzar a Paso 2 normalmente
          │
          └─ SÍ → Botón: "Ir a Análisis de Imagen"
               │
               └─ ABRE MODAL CON 5 SUB-PASOS:
                │
                ├─ SUB-PASO 1: CAPTURA DE IMÁGENES
                │  ├── Abrir cámara o subir fotos
                │  ├── Máximo 6 fotos
                │  ├── Preview miniaturas
                │  └── Guardar fotos temporalmente
                │    
                ├─ SUB-PASO 2: SELECCIONAR Y VISUALIZAR
                │  ├── Click en miniatura → imagen ampliada en canvas
                │  ├── Visualización clara para análisis
                │  └── Opciones de edición/reemplazo
                │    
                ├─ SUB-PASO 3: ANÁLISIS DE TRAZOS (Canvas)
                │  ├── Dibujar trazo vertical pie izquierdo (línea 1)
                │  ├── Marcar punto de ángulo → genera triángulo + ángulo interno/externo
                │  ├── Dibujar trazo vertical pie derecho (línea 2)
                │  ├── Marcar punto de ángulo → genera triángulo + ángulo interno/externo
                │  ├── Ver ángulos en tiempo real (grados)
                │  ├── Guardar anotaciones JSON
                │  └── Seleccionar fotos a guardar para reporte (max 6)
                │    
                ├─ SUB-PASO 4: ANÁLISIS BIOMECÁNICO + HUELLA PLANTAR
                │  ├── Datos por pie (LEFT/RIGHT)
                │  │   ├── Regla Maleolo Tibial
                │  │   ├── Desgaste de Calzado
                │  │   ├── Palpación Tibial
                │  │   └── Marcha
                │  │
                │  └── Evaluación de Huella Plantar
                │      └── Seleccionar SOLO UNO por pie
                │    
                ├─ SUB-PASO 5: RESUMEN + GENERAR PDF
                │  ├── Antecedentes, Evaluación, Observaciones
                │  ├── Diagnóstico (NORMAL / PRONACIÓN / SUPINACIÓN)
                │  ├── Distancia Intermaleolar / Intercondílea
                │  ├── Botón: "Generar Reporte PDF"
                │  │   └── Backend genera PDF con imágenes + datos
                │  │   └── Sube a Cloudinary → Retorna URL
                │  └── Botón: "Cerrar Modal" → Retorna a Paso 1
                │      (sesión actualizada con has_foot_analysis=true)
                │    
                └─ RETORNA A PASO 1:
                   ├── Sesión guardada con análisis
                   └── Avanzar a Paso 2 normalmente

[SESIÓN CLÍNICA - PASO 2]
  └── Llenar: Evaluación, Tratamiento, Observaciones
      └── Botón: Cerrar sesión → CLOSED
          (Si tiene análisis de pisada, puede generar/descargar PDF)
```

**Cambios clave:**
- ✅ Modal NO interrumpe el flujo principal del stepper
- ✅ Decisión de servicios se hace en Paso 1
- ✅ Análisis es **opcional** y **aislado**
- ✅ Historia clínica funciona con o sin análisis

### 4.3 Edición posterior

Si el usuario abre nuevamente la sesión (mientras siga OPEN), puede:
- ✅ Editar datos del análisis de pisada
- ✅ Agregar/reemplazar fotos (máximo 6)
- ✅ Regenerar trazos y ángulos
- ⚠️ Si ya se generó un PDF, se regenera con nuevos datos

---

## 5. Pantallas y Componentes Frontend

### 5.1 Ubicación en la Estructura

```
src/app/features/pages/management-pacient/
├── patient/
│   ├── clinical/
│   │   ├── session/
│   │   │   ├── clinical-session.component.ts    ← Stepper existente (agregar paso 4)
│   │   │   └── clinical-session.component.html
│   │   │
│   │   ├── imaging/                            ← NUEVO MÓDULO
│   │   │   ├── imaging.module.ts               ← NUEVO
│   │   │   ├── imaging-routing.module.ts       ← NUEVO
│   │   │   │
│   │   │   ├── foot-analysis/                 ← NUEVO
│   │   │   │   ├── foot-analysis.component.ts
│   │   │   │   ├── foot-analysis.component.html
│   │   │   │   └── foot-analysis.component.scss
│   │   │   │
│   │   │   ├── photo-gallery/                 ← NUEVO
│   │   │   │   ├── photo-gallery.component.ts
│   │   │   │   ├── photo-gallery.component.html
│   │   │   │   └── photo-gallery.component.scss
│   │   │   │
│   │   │   ├── photo-canvas/                  ← NUEVO
│   │   │   │   ├── photo-canvas.component.ts  ← Lógica de canvas + trazos
│   │   │   │   ├── photo-canvas.component.html
│   │   │   │   └── photo-canvas.component.scss
│   │   │   │
│   │   │   ├── biomechanical-form/            ← NUEVO
│   │   │   │   ├── biomechanical-form.component.ts
│   │   │   │   ├── biomechanical-form.component.html
│   │   │   │   └── biomechanical-form.component.scss
│   │   │   │
│   │   │   └── footprint-form/                ← NUEVO
│   │   │       ├── footprint-form.component.ts
│   │   │       ├── footprint-form.component.html
│   │   │       └── footprint-form.component.scss
│   │   │
│   │   └── report/                            ← NUEVO (Fase 8)
│   │       ├── report-preview.component.ts
│   │       ├── report-preview.component.html
│   │       └── report-preview.component.scss
```

### 5.2 Pantallas Detalladas

#### 📱 Pantalla 1: Galería de Fotos (Photo Gallery)

```
┌─────────────────────────────────────────┐
│  Análisis de Pisada — Captura de Imágenes
├─────────────────────────────────────────┤
│                                         │
│  [Botón: Capturar con cámara]          │
│  [Botón: Subir archivos]               │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │  Miniaturas de fotos (max 6)     │  │
│  │  ┌────┐ ┌────┐ ┌────┐          │  │
│  │  │ 1  │ │ 2  │ │ 3  │  ...    │  │
│  │  └────┘ └────┘ └────┘          │  │
│  │  ┌────┐ ┌────┐ ┌─────┐         │  │
│  │  │ 4  │ │ 5  │ │ +   │ (Añadir)│  │
│  │  └────┘ └────┘ └─────┘         │  │
│  └──────────────────────────────────┘  │
│                                         │
│  [Botón: Cancelar] [Botón: Siguiente]  │
└─────────────────────────────────────────┘
```

#### 📱 Pantalla 2: Canvas de Trazos (Photo Canvas)

```
┌────────────────────────────────────────────────┐
│ Análisis de Trazos — Foto [1/6]               │
├────────────────────────────────────────────────┤
│  [Miniatura 1] [Miniatura 2] [Miniatura 3]    │
├────────────────────────────────────────────────┤
│                                                │
│  ┌────────────────────────────────────────┐   │
│  │                                        │   │
│  │  [Imagen ampliada para análisis]       │   │
│  │                                        │   │
│  │  [Canvas con trazos dibujados]         │   │
│  │                                        │   │
│  └────────────────────────────────────────┘   │
│                                                │
│  ┌─────────────────────────────────────────┐  │
│  │ Pie Izquierdo                           │  │
│  │ ✓ Trazo vertical 1       [Borrar]      │  │
│  │ ✓ Ángulo interno: 15.5°                │  │
│  │ ✓ Ángulo externo: 22.3°                │  │
│  └─────────────────────────────────────────┘  │
│  ┌─────────────────────────────────────────┐  │
│  │ Pie Derecho                             │  │
│  │ ✓ Trazo vertical 2       [Borrar]      │  │
│  │ ✓ Ángulo interno: 18.7°                │  │
│  │ ✓ Ángulo externo: 25.1°                │  │
│  └─────────────────────────────────────────┘  │
│                                                │
│  [Cancelar] [Atrás] [Guardar] [Siguiente]    │
└────────────────────────────────────────────────┘
```

#### 📱 Pantalla 3: Formulario de Análisis Biomecánico

```
┌──────────────────────────────────────────┐
│ Análisis Biomecánico                     │
├──────────────────────────────────────────┤
│                                          │
│  PIE IZQUIERDO                           │
│  ┌──────────────────────────────────┐   │
│  │ Regla Maleolo Tibial *           │   │
│  │ [  Seleccione  ▼]                │   │
│  │ • NORMAL                         │   │
│  │ • VARO                           │   │
│  │ • VALGO                          │   │
│  │ • OTRO                           │   │
│  └──────────────────────────────────┘   │
│                                          │
│  Desgaste de Calzado                    │
│  [Texto libre.....................]     │
│                                          │
│  Palpación de la Tibia                  │
│  [Texto libre.....................]     │
│                                          │
│  Marcha *                                │
│  [  Seleccione  ▼]                       │
│  • NORMAL / PRONADOR / SUPINADOR / MIXTO │
│                                          │
│  ────────────────────────────────────    │
│  PIE DERECHO [igual estructura]         │
│                                          │
│  [Atrás] [Siguiente]                   │
└──────────────────────────────────────────┘
```

#### 📱 Pantalla 4: Evaluación de Huella Plantar

```
┌──────────────────────────────────────────┐
│ Evaluación de Huella Plantar             │
├──────────────────────────────────────────┤
│                                          │
│  PIE IZQUIERDO                           │
│  Seleccione UNO:                         │
│  ◯ Índice Normal                         │
│  ◯ Índice Pie Plano                      │
│  ◯ Índice Pie Cavo                       │
│  ◯ Pie Plano                             │
│  ◯ Pie Plano-Normal                      │
│  ◯ Pie Normal                            │
│  ◯ Pie Normal-Cavo                       │
│  ◯ Pie Cavo                              │
│  ◯ Pie Cavo Fuerte                       │
│  ◯ Pie Cavo Extremo                      │
│                                          │
│  ────────────────────────────────────    │
│  PIE DERECHO [igual estructura]         │
│                                          │
│  [Atrás] [Guardar Análisis]             │
└──────────────────────────────────────────┘
```

#### 📱 Pantalla 5: Resumen y Datos Generales

```
┌──────────────────────────────────────────┐
│ Resumen — Análisis de Pisada             │
├──────────────────────────────────────────┤
│                                          │
│  Antecedentes Relevantes                 │
│  [Texto libre.....................]      │
│                                          │
│  Evaluación Kinésica                     │
│  [Texto libre.....................]      │
│                                          │
│  Observaciones                           │
│  [Texto libre.....................]      │
│                                          │
│  Diagnóstico *                           │
│  ( ) NORMAL                              │
│  ( ) PRONACIÓN                           │
│  ( ) SUPINACIÓN                          │
│                                          │
│  Distancia Intermaleolar (cm)            │
│  [Número..........]                      │
│                                          │
│  Distancia Intercondílea (cm)            │
│  [Número..........]                      │
│                                          │
│  Fotos seleccionadas para reporte: 3/6  │
│                                          │
│  [Atrás] [Generar Reporte PDF]          │
└──────────────────────────────────────────┘
```

---

## 6. Endpoints Backend

### 6.1 Análisis de Pisada (CRUD)

```
POST   /api/foot-analysis/create
       {
         "clinicalSessionId": 123,
         "relevantBackground": "...",
         "kinesiologicalEvaluation": "...",
         "observations": "...",
         "diagnosis": "PRONATION",
         "distanceIntermaleolar": 12.5,
         "distanceIntercondylar": 18.3
       }

GET    /api/foot-analysis/{id}
       → FootAnalysisResponse

PUT    /api/foot-analysis/update/{id}
       {body similar a POST}

GET    /api/clinical-session/{sessionId}/foot-analysis
       → FootAnalysisResponse (si existe) o 404
```

### 6.2 Análisis Biomecánico

```
POST   /api/foot-analysis/{analysisId}/biomechanical
       {
         "footSide": "LEFT",
         "tibialMalleolarRule": "NORMAL",
         "shoeWear": "...",
         "tibiaPalpation": "...",
         "gait": "NORMAL"
       }

PUT    /api/foot-analysis/{analysisId}/biomechanical/{biomechId}
       {body}

GET    /api/foot-analysis/{analysisId}/biomechanical
       → List<BiomechanicalAnalysisResponse>
```

### 6.3 Evaluación de Huella Plantar

```
POST   /api/foot-analysis/{analysisId}/footprint
       {
         "footSide": "LEFT",
         "indexNormal": false,
         "indexFlatFoot": true,        // Solo 1 puede ser true
         "indexCavusFoot": false,
         ...
       }

PUT    /api/foot-analysis/{analysisId}/footprint/{footprintId}
       {body}

GET    /api/foot-analysis/{analysisId}/footprint
       → List<FootprintAnalysisResponse>
```

### 6.4 Fotos y Anotaciones (Existente, Adaptado)

```
POST   /api/foot-analysis/{analysisId}/photos
       multipart/form-data
       {
         "photo": <archivo>,
         "photoOrder": 1
       }

PUT    /api/foot-analysis/{analysisId}/photos/{photoId}/annotations
       {
         "annotationsJson": "{ trazos, ángulos... }"
       }

PATCH  /api/foot-analysis/{analysisId}/photos/{photoId}/select
       → Marcar foto para reporte

DELETE /api/foot-analysis/{analysisId}/photos/{photoId}
```

### 6.5 Generación de Reporte PDF

```
POST   /api/foot-analysis/{analysisId}/report/generate
       → Genera PDF con imágenes + datos + trazos
       → Sube a Cloudinary
       → Retorna URL

GET    /api/foot-analysis/{analysisId}/report
       → Devuelve URL del PDF (o 404 si no existe)
```

---

## 7. Integración con Historia Clínica

### 7.1 Modelo de Datos Unificado

**Sin cambiar nada en el flujo existente** (`clinical_session`, `episode`), se agrega:

```typescript
// clinical-session.model.ts
export interface ClinicalSessionResponse {
  id: number;
  episodeId: number;
  // ... campos existentes ...
  
  // NUEVO (campos de análisis de pisada)
  hasFootAnalysis: boolean;
  footAnalysis?: FootAnalysisResponse;
}

// foot-analysis.model.ts
export interface FootAnalysisResponse {
  id: number;
  clinicalSessionId: number;
  relevantBackground: string;
  kinesiologicalEvaluation: string;
  diagnosis: 'NORMAL' | 'PRONATION' | 'SUPINATION';
  distanceIntermaleolar: number;
  distanceIntercondylar: number;
  angleLeftInternal: number;
  angleLeftExternal: number;
  angleRightInternal: number;
  angleRightExternal: number;
  biomechanicalAnalysis: BiomechanicalAnalysisResponse[];
  footprintAnalysis: FootprintAnalysisResponse[];
  photos: AnalysisPhotoResponse[];
  reportUrl?: string;
}
```

### 7.2 Flujo en Stepper Existente (Rediseñado)

**Stepper optimizado: 2 pasos base + análisis postural en modal (NUEVO)**

```
PASO 1: Datos Básicos + Servicios
├── Fecha, Motivo, Fisioterapeuta
├── Multiselect de servicios
│   └── ✅ SI se selecciona "Análisis de Pisada"
│       └── Botón: "Ir a Análisis de Imagen" 🔵 NUEVO (abre modal)
│           └── Modal con 5 sub-pasos (no afecta stepper principal)
│               ├── Sub-paso 1: Captura de Fotos
│               ├── Sub-paso 2: Canvas de Trazos
│               ├── Sub-paso 3: Análisis Biomecánico
│               ├── Sub-paso 4: Evaluación de Huella Plantar
│               └── Sub-paso 5: Resumen + Generar PDF
│           └── Cierra modal → retorna a Paso 1
│               (sesión marcada con has_foot_analysis=true)

PASO 2: Evaluación Clínica + Cierre
├── [Antecedentes, Evaluación, Tratamiento, Observaciones, Evolución] ← existente
└── [Botón: Cerrar sesión → CLOSED]
```

**Ventajas:**
- ✅ Flujo normal (2 pasos) si NO hay análisis postural
- ✅ Decisión de servicios UPFRONT (paso 1)
- ✅ Análisis postural en modal aislado (no interrumpe stepper)
- ✅ UX más limpia y predecible

### 7.3 Impacto Mínimo

| Elemento | Cambio | Impacto |
|----------|--------|--------|
| Stepper | Agregar botón condicional | ❓ 2-3 líneas de código |
| Session Component | Validar `hasFootAnalysis` | ❓ 1-2 líneas |
| BD: clinical_session | Agregar `has_foot_analysis` | ✅ Migración Flyway (1 línea SQL) |
| Backend Service | Inyectar `FootAnalysisService` | ✅ 1 inyección |
| Frontend Module | Importar `ImagingModule` | ✅ 1 import en `clinical.module.ts` |

**→ TOTAL: Cambios mínimos, sin refactorización.**

---

## 8. Impacto Mínimo en lo Existente

### 8.1 Checklist de No Afectación

✅ **Pacientes (Fase 1-3):** Sin cambios  
✅ **Episodios Clínicos (Fase 4):** Sin cambios  
✅ **Sesiones (Fase 4):** Solo agrega campo booleano `has_foot_analysis`  
✅ **Servicios por sesión (Fase 5):** Sin cambios  
✅ **Historial clínica (Fase 5):** Sin cambios en lógica  

### 8.2 Cambios Requeridos

| Área | Cambio | Archivo | Líneas |
|------|--------|---------|--------|
| BD | Agregar columna | `clinical_session` | +1 |
| Backend | Inyectar servicio | `ClinicalSessionServiceImpl` | +1 |
| Frontend | Agregar botón | `clinical-session.component.html` | +3 |
| Frontend | Lógica botón | `clinical-session.component.ts` | +5 |

### 8.3 Rollback Fácil

Si no se requiere el módulo de análisis de imagen:
- No se usa el botón
- El sistema funciona normalmente (sesiones completas sin análisis)
- Solo hay una columna inactiva en BD

---

## 9. Hoja de Ruta Detallada

### Fase 6A — Backend: Entidades y Servicios (2-3 días)

```
[ 1 ] Crear enums: DiagnosisType, TibialMalleolarRule, Gait, FootSide
[ 2 ] Crear entidades:
      - FootAnalysis.java
      - BiomechanicalAnalysis.java
      - FootprintAnalysis.java
[ 3 ] Crear repositorios (extends JpaRepository)
[ 4 ] Crear servicios (CRUD + validaciones)
[ 5 ] Crear DTOs y Responses
[ 6 ] Crear controladores REST (endpoints)
[ 7 ] Agregar permisos en DataLoader
[ 8 ] Crear migración Flyway
       ALTER TABLE clinical_session ADD COLUMN has_foot_analysis BOOLEAN DEFAULT FALSE;
```

### Fase 7 — Backend: Generación de PDF (iText 7) (1-2 días)

```
[ 1 ] Agregar dependencia iText 7 en pom.xml
[ 2 ] Crear ReportService
       - Método: generateFootAnalysisReport(Long analysisId)
       - Inserta: logo, paciente, datos clínicos, fotos + trazos, diagnóstico
       - Sube PDF a Cloudinary (NO persiste en BD, solo URL)
[ 3 ] Crear endpoint POST /api/foot-analysis/{id}/report/generate
[ 4 ] Crear endpoint GET /api/foot-analysis/{id}/report
[ 5 ] Pruebas de generación
```

### Fase 8 — Frontend: Vista Previa y Descarga (1 día)

```
[ 1 ] Crear componente report-preview (opcional)
[ 2 ] Agregar botón "Generar / Descargar Reporte PDF" en análisis
[ 3 ] Link para descargar desde Cloudinary URL
[ 4 ] Indicador visual en tabla (si sesión tiene reporte)
[ 5 ] Historial de reportes por sesión
```

### Fase 6B — Frontend: Módulo de Imagen (3-4 días)

```
[ 1 ] Crear módulo ImagingModule
[ 2 ] Crear componentes:
      - photo-gallery.component
      - photo-canvas.component (canvas HTML5 + trazos)
      - biomechanical-form.component
      - footprint-form.component
      - foot-analysis.component (orquestador)
[ 3 ] Implementar canvas:
      - Dibujar líneas
      - Calcular ángulos
      - Serializar a JSON
[ 4 ] Crear servicios:
      - FootAnalysisService
      - FootAnalysisPhotoService
[ 5 ] Integrar en clinical-session.component
      - Agregar botón "Ir a Análisis" si se selecciona el servicio
```

### Fase 7 — Backend: Generación de PDF (1-2 días)

```
[ 1 ] Agregar dependencia iText 7 en pom.xml
[ 2 ] Crear ReportService
      - Método: generateFootAnalysisReport(Long analysisId)
      - Inserta: logo, paciente, datos clínicos, fotos + trazos, diagnóstico
[ 3 ] Crear endpoint POST /api/foot-analysis/{id}/report/generate
[ 4 ] Pruebas de generación
```

### Fase 8 — Frontend: Vista Previa y Descarga (1 día)

```
[ 1 ] Crear componente report-preview
[ 2 ] Agregar botón "Descargar Reporte PDF" en sesión
[ 3 ] Mostrar URL o link para descargar
[ 4 ] Indicador visual en tabla (si sesión tiene reporte)
```

---

## 📊 Resumen Ejecutivo

### Datos a Registrar

| Sección | Campos | Tipo de Datos |
|---------|--------|---------------|
| Análisis Principal | 6 campos | DECIMAL, VARCHAR, TEXT |
| Análisis Biomecánico | 4 campos × 2 pies = 8 | VARCHAR, TEXT |
| Huella Plantar | 10 campos × 2 pies = 20 | BOOLEAN |
| Fotos + Trazos | Hasta 6 fotos con JSON | BLOB, JSON |
| **Total** | **40+ datos** | **Bien estructurados** |

### Tablas Nuevas

| Tabla | Relación | Impacto |
|-------|----------|--------|
| `foot_analysis` | 1:1 con `clinical_session` | Principal |
| `biomechanical_analysis` | 1:2 (LEFT + RIGHT) | Secundaria |
| `footprint_analysis` | 1:2 (LEFT + RIGHT) | Secundaria |
| *`analysis_photo`* | *Ya existente* | *Reutilizada* |

### Cambios en Existentes

| Tabla | Cambio | Razón |
|-------|--------|-------|
| `clinical_session` | +1 columna boolean | Control de estado |

### Gestión de Reportes

| Aspecto | Decisión |
|--------|----------|
| Almacenamiento PDF | ❌ **NO** en BD, ✅ Solo URL en `foot_analysis.report_url` |
| Almacenamiento Físico | ✅ Cloudinary (proveedor externo) |
| Descarga | ✅ Bajo demanda desde URL Cloudinary |
| Regeneración | ✅ Permitida (sobrescribe en `report_url`) |
| Persistencia | ✅ Solo el link (URL), no el archivo |
| Eliminación Lógica | ✅ Si se borra análisis, se borra URL también |

### Pantallas Frontend

| Pantalla | Tipo | Propósito |
|----------|------|----------|
| Photo Gallery | Modal/Drawer | Capturar/subir fotos |
| Photo Canvas | Modal/Drawer | Dibujar trazos y ver ángulos |
| Biomechanical Form | Modal/Drawer | Datos por pie |
| Footprint Form | Modal/Drawer | Seleccionar clasificación |
| Resumen | Modal/Drawer | Datos generales + generar PDF |

### Integración Suave

✅ El flujo de Historia Clínica **NO se ve afectado**  
✅ El análisis de pisada es **completamente opcional**  
✅ Si se agrega el servicio → Se activa el módulo  
✅ Si NO se agrega → El sistema funciona normalmente  

---

## 🎯 Recomendación Final

**Opción Recomendada: Implementación Modular Integrada en Modal**

```
✅ VENTAJAS del diseño optimizado (2 pasos + modal):
  ├── UX más limpia: flujo normal sin análisis postural (2 pasos)
  ├── Decisión de servicios UPFRONT (paso 1)
  ├── Análisis aislado en modal (no interrumpe stepper)
  ├── Historia clínica funcional al 100% sin análisis
  ├── Reportes bajo demanda (sin persistencia de archivos)
  └── Bajo riesgo de ruptura en lo existente

SEMANA 1: Fase 6 Backend
  └─ Entidades + Servicios + API REST

SEMANA 2: Fase 6 Frontend (Modal integrado)
  └─ Módulos + Canvas + Integración en stepper

SEMANA 3: Fase 7-8 Reportes
  └─ PDF bajo demanda + Vista previa

RESULTADO: Sistema robusto, escalable y sin afectar Historia Clínica existente
```


---

**Documento preparado por:** Sistema de Planificación KineVid  
**Próximo paso:** Aprobación y comenzar Fase 6 (Backend)

---

**Nota de actualización — 11/05/2026:**
- ✅ Stepper optimizado: 2 pasos base + análisis postular en modal (no 3 pasos + 5 sub-pasos)
- ✅ Reportes: descarga bajo demanda desde Cloudinary (sin persistencia de archivos en BD)
- ✅ Historia clínica completamente funcional sin análisis de pisada  


