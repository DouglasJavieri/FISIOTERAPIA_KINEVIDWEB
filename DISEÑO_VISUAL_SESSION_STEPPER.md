# 🎨 DISEÑO VISUAL: SessionFormComponent 2 Stepers

## 📐 LAYOUT PASO 1: "Información de sesión"

### Desktop (1920px+)

```
┌─────────────────────────────────────────────────────────────────┐
│                      NUEVA SESIÓN                               │
│  Gestión de Pacientes > Episodios > Sesiones > [Actual: Nueva]  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ PASO 1: INFORMACIÓN DE SESIÓN                  [1 de 2] ▶ paso 2 │
└─────────────────────────────────────────────────────────────────┘

┌───── SECCIÓN 1.1: Datos de la sesión ─────────────────────────┐
│                                                                 │
│  [Fisioterapeuta ▼]     [Fecha ▼]      [Servicio ▼]          │
│  ▲ Dra. María López     ▲ 12/05/2026   ▲ Rehabilitación      │
│  Select (required)      DatePicker     Select (required)      │
│  Error: Requerido.      Error: ...     Error: ...             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

┌───── SECCIÓN 1.2: Cantidad y precio ──────────────────────────┐
│                                                                 │
│  [Cantidad]             [Precio (Bs.)]  [Notas]              │
│  ▲ 1                    ▲ 150.00         ▲ Sesión inicial    │
│  min:1 max:99           min:0            (optional)           │
│  Error: ...             Error: ...        250 / 500           │
│                                                                 │
│                            [+ Agregar servicio] (botón accent) │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

┌───── SECCIÓN 1.3: Detalles de la consulta ────────────────────┐
│                                                                 │
│  [Motivo de consulta]           [Antecedentes relevantes]    │
│  ▼ Dolor lumbar crónico ...     ▼ Alergia a penicilina, ...  │
│     (textarea 4 rows)             (textarea 4 rows)           │
│  250 / 500                       450 / 1000                    │
│  Error: Requerido.              Error: ...                     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

┌───── SECCIÓN 1.4: Servicios registrados en esta sesión ───────┐
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │ Servicio         │ Categoría      │ Cant. │ Total Bs.  │  │
│  ├─────────────────────────────────────────────────────────┤  │
│  │ Rehabilitación   │ Rehabilitación │  1   │  150.00    │  │
│  │ Masaje         │ Masoterapia    │  1   │  80.00     │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ℹ️ ✓ Se ha detectado "Análisis Postural".                    │
│     Después de completar esta sesión, se activará el flujo    │
│     de captura de imágenes.                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

    [Cancelar]              [Siguiente paso →]
```

### Tablet (768px - 1919px)

```
┌─────────────────────────────────────────────┐
│      NUEVA SESIÓN                          │
│  Gestión... > [Estado navegación]          │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ PASO 1: INFORMACIÓN                 [1 de 2] │
└─────────────────────────────────────────────┘

┌───── SECCIÓN 1.1 ──────────────────────────┐
│                                             │
│  [Fisioterapeuta ▼]  [Fecha ▼]             │
│  Dra. María López    12/05/2026            │
│                                             │
│  [Servicio ▼]                              │
│  Rehabilitación                            │
│                                             │
└─────────────────────────────────────────────┘

┌───── SECCIÓN 1.2 ──────────────────────────┐
│  [Cantidad]         [Precio (Bs.)]         │
│  1                  150.00                 │
│                                             │
│  [Notas]                                   │
│  Sesión inicial                            │
│                                             │
│  [+ Agregar servicio]                      │
└─────────────────────────────────────────────┘

┌───── SECCIÓN 1.3 ──────────────────────────┐
│  [Motivo]                                   │
│  Dolor lumbar crónico...                   │
│  [Antecedentes]                            │
│  Alergia a penicilina...                   │
└─────────────────────────────────────────────┘

┌───── TABLA SERVICIOS ──────────────────────┐
│  Servicio | Categoría | Cantidad | Total   │
│  ......... scroll horizontal........       │
└─────────────────────────────────────────────┘

[Cancelar] [Siguiente →]
```

### Mobile (<768px)

```
┌──────────────────────────┐
│  NUEVA SESIÓN           │
│  [Estado navegación]    │
└──────────────────────────┘

┌──────────────────────────┐
│ PASO 1              [1/2]│
└──────────────────────────┘

│ Fisioterapeuta ▼         │
│ Dra. María López         │
│ Error: Requerido        │

│ Fecha ▼                  │
│ 12/05/2026              │

│ Servicio ▼              │
│ Rehabilitación          │

│ Cantidad                │
│ 1                       │

│ Precio (Bs.)            │
│ 150.00                  │

│ Notas                   │
│ Sesión inicial          │

│ [+ Agregar servicio]    │

│ Motivo de consulta      │
│ Dolor lumbar crónico... │

│ Antecedentes            │
│ Alergia a penicilina... │

│ TABLA SERVICIOS ▼       │
│ [Scroll horizontal]     │

[Cancelar]
[Siguiente paso →]
```

---

## 📐 LAYOUT PASO 2: "Evaluación clínica"

### Desktop (1920px+)

```
┌─────────────────────────────────────────────────────────────────┐
│ PASO 2: EVALUACIÓN CLÍNICA                [2 de 2] ◄ paso 1   │
└─────────────────────────────────────────────────────────────────┘

┌─ FILA 1: Hallazgos iniciales ────────────────────────────────┐
│                                                                 │
│  [Evaluación       [Antecedente       [Marcha/Postura]       │
│   kinesiológica]   actual de enf.]                           │
│  ▼ Limitación...   ▼ Dolor agudo...   ▼ Marcha ...          │
│     (textarea)        (textarea)         (textarea)           │
│  [4 rows]          [4 rows]            [4 rows]              │
│  Error: ...        Error: ...          Error: ...            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

┌─ FILA 2: Pruebas y exámenes ─────────────────────────────────┐
│                                                                 │
│  [Pruebas          [Exámenes         [Diagnóstico           │
│   funcionales]     complementarios]   kinesiológico]         │
│  ▼ ROM...          ▼ Rayos X...      ▼ Contractura...       │
│     (textarea)        (textarea)        (textarea)           │
│  [4 rows]          [4 rows]           [4 rows]              │
│  Error: ...        Error: ...         Error: ...            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

┌─ FILA 3: Tratamiento y evolución ────────────────────────────┐
│                                                                 │
│  [Tratamiento      [Observaciones]    [Evolución del        │
│   aplicado]                            paciente]             │
│  ▼ Terapia...      ▼ Paciente...      ▼ Mejoró...          │
│     (textarea)        (textarea)        (textarea)           │
│  [4 rows]          [4 rows]           [4 rows]              │
│  Error: ...        Error: ...         Error: ...            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

    [◄ Anterior]    [Ver sesiones]    [✓ Guardar y finalizar]
```

### Tablet

```
┌────────────────────────────────────────┐
│ PASO 2: EVALUACIÓN CLÍNICA      [2/2] │
└────────────────────────────────────────┘

┌─ FILA 1 ───────────────────────────────┐
│ [Evaluación kinesiológica]             │
│ ............................           │
│ [Antecedente actual enfermedad]        │
│ ............................           │
│ [Marcha/Postura]                       │
│ ............................           │
└────────────────────────────────────────┘

┌─ FILA 2 ───────────────────────────────┐
│ [Pruebas funcionales]                  │
│ ............................           │
│ [Exámenes complementarios]             │
│ ............................           │
│ [Diagnóstico]                          │
│ ............................           │
└────────────────────────────────────────┘

┌─ FILA 3 ───────────────────────────────┐
│ [Tratamiento aplicado]                 │
│ ............................           │
│ [Observaciones]                        │
│ ............................           │
│ [Evolución]                            │
│ ............................           │
└────────────────────────────────────────┘

[◄ Anterior] [Ver sesiones] [✓ Guardar]
```

### Mobile

```
┌──────────────────────────┐
│ PASO 2               [2/2]│
└──────────────────────────┘

│ Evaluación           │
│ kinesiológica        │
│ .................    │

│ Antecedente actual   │
│ .................    │

│ Marcha/Postura       │
│ .................    │

│ Pruebas funcionales  │
│ .................    │

│ Exámenes            │
│ .................    │

│ Diagnóstico         │
│ .................    │

│ Tratamiento         │
│ .................    │

│ Observaciones       │
│ .................    │

│ Evolución           │
│ .................    │

[◄ Anterior]
[Ver sesiones]
[✓ Guardar y finalizar]
```

---

## 🔔 ALERTA: Análisis Postural Detectado

### Visual (Paso 1, Sección 1.4)

```
┌──────────────────────────────────────────────────────────┐
│ ℹ️  ✓ Se ha detectado "Análisis Postural".            │
│     Después de completar esta sesión, se activará el   │
│     flujo de captura de imágenes.                       │
└──────────────────────────────────────────────────────────┘

Estilos aplicados:
├─ background: #e3f2fd (azul claro - Material Blue 100)
├─ border-left: 4px solid #1976d2 (azul oscuro)
├─ border-radius: 4px
├─ padding: 12px 16px
├─ color: #0d47a1 (texto azul oscuro)
├─ font-size: 14px
├─ line-height: 1.4
└─ gap: 12px (ícono + texto)
```

### Condiciones de aparición

```
✓ Aparece SI:
  - Usuario selecciona servicio con category = "POSTURAL_ANALYSIS"
  - Se ejecuta: checkForPosturalAnalysisService()
  - hasPosturalAnalysisService = true
  - *ngIf="hasPosturalAnalysisService" == true

✗ Desaparece SI:
  - Usuario elimina el servicio de Análisis Postural
  - Se vuelve a ejecutar: checkForPosturalAnalysisService()
  - hasPosturalAnalysisService = false
  - *ngIf="hasPosturalAnalysisService" == false
```

---

## ⚙️ FLUJO DE INTERACCIÓN

### Flujo 1: Crear sesión SIN Análisis Postural

```
┌─────────────┐
│   INICIO    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│ PASO 1: Información de sesión       │
│ ┌─────────────────────────────────┐ │
│ │ 1. Seleccionar fisioterapeuta   │ │
│ │ 2. Seleccionar fecha (hoy)      │ │
│ │ 3. Seleccionar servicio:        │ │
│ │    → "Rehabilitación"           │ │
│ │ 4. Ingresar cantidad: 1         │ │
│ │ 5. Ingresar motivo de consulta  │ │
│ │ 6. Ingresar antecedentes        │ │
│ │ 7. Clic: "+ Agregar servicio"  │ │
│ │ 8. Verificar tabla de servicios │ │
│ │ 9. ❌ NO aparece alerta         │ │
│ └─────────────────────────────────┘ │
│           [Siguiente paso →]         │
└──────┬─────────────────────────────┬─┘
       │                             │
       ▼                             ▼
   Validación OK            Validación FAIL
   (todos campos)           (falta algo)
       │                             │
       ▼                             ▼
   [Siguiente →]              [Mostrar error]
       │                             │
       ▼                             └──────┘
┌──────────────────────────────┐
│ PASO 2: Evaluación clínica   │
│ 1. Ingresar evaluación       │
│ 2. Ingresar historia         │
│ 3. ... (completar campos)    │
│ [Guardar y finalizar]        │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────┐
│ ✅ Sesión creada │
│ (sin análisis)   │
└──────────────────┘
```

### Flujo 2: Crear sesión CON Análisis Postural 🆕

```
┌─────────────┐
│   INICIO    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│ PASO 1: Información de sesión       │
│ ┌─────────────────────────────────┐ │
│ │ 1. Seleccionar fisioterapeuta   │ │
│ │ 2. Seleccionar fecha (hoy)      │ │
│ │ 3. Seleccionar servicio:        │ │
│ │    → "Análisis Postural" ⭐     │ │
│ │ 4. Ingresar cantidad: 1         │ │
│ │ 5. Ingresar motivo de consulta  │ │
│ │ 6. Ingresar antecedentes        │ │
│ │ 7. Clic: "+ Agregar servicio"  │ │
│ │ 8. Verificar tabla              │ │
│ │ 9. ✅ APARECE ALERTA AZUL:      │ │
│ │    "Se ha detectado...         │ │
│ │     Análisis Postural"          │ │
│ └─────────────────────────────────┘ │
│           [Siguiente paso →]         │
└──────┬──────────────────────────────┘
       │
       ▼
   [Siguiente →]
       │
       ▼
┌────────────────────────────────────────┐
│ PASO 2: Evaluación clínica             │
│ (campos iguales a flujo anterior)      │
│ [Guardar y finalizar]                  │
└────┬─────────────────────────────────┬─┘
     │                                 │
     ▼                                 ▼
 Guardar OK                      Validación FAIL
     │                                 │
     ▼                                 └──────┘
┌─────────────────────────────────┐
│ ✅ Sesión creada CON análisis   │
│                                 │
│ Sistema detectó:                │
│ hasPosturalAnalysisService=true │
│                                 │
│ Próximo paso (Fase 6B):         │
│ → Abrir flujo de imágenes       │
│ → Captura de fotos              │
│ → Canvas HTML5                  │
│ → Análisis biomecánico          │
└─────────────────────────────────┘
```

---

## 🎯 VALIDACIONES VISUALES

### Validación 1: Falta fisioterapeuta

```
┌─────────────────────────────────────┐
│ [Fisioterapeuta ▼]                  │
│ [campo vacío]                       │
│ ⚠️ Error: La selección es requerida.│
└─────────────────────────────────────┘

[Siguiente paso →] ← DESHABILITADO (disabled=true)
```

### Validación 2: Falta servicio

```
┌─────────────────────────────────────┐
│ SECCIÓN 1.4: Servicios registrados  │
│                                     │
│ 📦 No hay servicios registrados.    │
│    Agregue uno usando el formulario │
│    anterior.                        │
└─────────────────────────────────────┘

[Siguiente paso →] ← DESHABILITADO
```

### Validación 3: Todo válido

```
✅ Fisioterapeuta: Seleccionado
✅ Fecha: 12/05/2026
✅ Motivo: "Dolor lumbar"
✅ Antecedentes: "Ninguno"
✅ Servicios: 1 agregado

[Siguiente paso →] ← HABILITADO (enabled)
```

---

## 📱 RESPONSIVE BREAKPOINTS

| Breakpoint | Ancho | Columnas | Aplicación |
|------------|-------|----------|-----------|
| Desktop | 1920px+ | 3 | Escritorio completo |
| Tablet | 768px - 1919px | 2 | iPad horizontal |
| Mobile | <768px | 1 | Teléfono + iPad vertical |

### Media Queries utilizadas

```scss
// Desktop (por defecto)
fxFlex="33.33"    // 3 columnas

// Tablet
fxFlex.lt-md="50"  // 2 columnas (< 960px)

// Mobile
fxFlex.lt-md="100" // 1 columna (< 960px)
```

---

## 🎨 PALETA DE COLORES

| Elemento | Color | Hex | Uso |
|----------|-------|-----|-----|
| Primario | Azul Índigo | #5c6bc0 | Títulos de sección |
| Secundario | Azul Material | #1976d2 | Alerta postural (borde) |
| Fondo Alerta | Azul 100 | #e3f2fd | Fondo alerta postural |
| Texto Alerta | Azul Oscuro | #0d47a1 | Texto alerta postural |
| Borde Alerta | Azul Oscuro | #1976d2 | Borde izq. alerta |
| Éxito | Verde | #2e7d32 | Estado OPEN (tabla) |
| Advertencia | Naranja | #f57c00 | Botones secundarios |
| Peligro | Rojo | #c62828 | Estado CANCELLED |

---

**Última actualización:** 12 de Mayo 2026  
**Versión:** 2.0 - SessionFormComponent

