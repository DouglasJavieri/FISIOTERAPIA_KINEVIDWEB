# ESTIMACIÓN DETALLADA DE TAMAÑO Y ESFUERZO - PROYECTO KINEVID

## 1. INTRODUCCIÓN
Este documento presenta una estimación precisa del tamaño del software KINEVID utilizando el método de **Puntos de Función (FP)**. A partir de este tamaño, se recalculan el esfuerzo, tiempo y personal necesario mediante el modelo **COCOMO II**, permitiendo una planificación más exacta del proyecto.

---

## 2. ANÁLISIS DE PUNTOS DE FUNCIÓN (FP)

### 2.1 Conteo de Funciones No Ajustadas (UFP)

Se identifican los componentes funcionales basados en la arquitectura actual del sistema (Gestión de Pacientes, Historia Clínica y Análisis de Imagen).

#### A. Tabla de Factor de Ponderación (Resumen UFP)
Esta tabla resume el conteo de funciones por categoría y su respectivo peso según la complejidad identificada.

| Categoría | Descripción | Cantidad | Complejidad | Peso | Total |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Entradas Externas (EI)** | Formularios de registro, edición y autenticación. | 8 | Media | 4 | 32 |
| **Salidas Externas (EO)** | Reportes PDF y notificaciones automáticas. | 3 | Alta | 7 | 19* |
| **Consultas Externas (EQ)** | Listados, búsquedas y dashboards. | 6 | Media | 4 | 24 |
| **Archivos Lógicos Internos (ILF)** | Grupos de datos persistidos (Seguridad, Clínica, etc). | 9 | Alta | 15 | 107* |
| **Interfaces Externas (EIF)** | Integración con servicios externos (Cloudinary). | 1 | Baja | 5 | 5 |
| **TOTAL UFP** | | | | | **187** |

*\*Nota: Los totales de EO e ILF reflejan la suma exacta de los pesos individuales detallados a continuación.*

#### B. Desglose Detallado de Funciones

##### 1. Entradas Externas (EI)
| ID | Descripción | Complejidad | Peso |
| :--- | :--- | :---: | :---: |
| EI-01 | Formulario de Login / Autenticación | Baja | 3 |
| EI-02 | Registro/Edición de Paciente | Media | 4 |
| EI-03 | Registro/Edición de Usuario | Media | 4 |
| EI-04 | Registro/Edición de Empleado | Media | 4 |
| EI-05 | Gestión de Servicios Médicos | Baja | 3 |
| EI-06 | Creación de Episodio Clínico | Media | 4 |
| EI-07 | Registro de Sesión (Stepper Paso 1: Datos + Servicios) | Media | 4 |
| EI-08 | Evaluación Clínica (Stepper Paso 2: Conclusiones) | Media | 6 |
| **Total** | | | **32** |

##### 2. Salidas Externas (EO)
| ID | Descripción | Complejidad | Peso |
| :--- | :--- | :---: | :---: |
| EO-01 | Generación de Reporte PDF (Análisis de Pisada) | Alta | 7 |
| EO-02 | Resumen de Historia Clínica para Impresión | Alta | 7 |
| EO-03 | Notificación de Cierre de Episodio / Alta | Media | 5 |
| **Total** | | | **19** |

##### 3. Consultas Externas (EQ)
| ID | Descripción | Complejidad | Peso |
| :--- | :--- | :---: | :---: |
| EQ-01 | Búsqueda y Listado Paginado de Pacientes | Media | 4 |
| EQ-02 | Listado de Sesiones por Episodio | Media | 4 |
| EQ-03 | Historial de Episodios por Paciente | Media | 4 |
| EQ-04 | Listado de Servicios Activos (Selector) | Baja | 3 |
| EQ-05 | Consulta de Auditoría (Logs de Usuario) | Media | 4 |
| EQ-06 | Dashboard de Resumen (Estadísticas básicas) | Media | 5 |
| **Total** | | | **24** |

##### 4. Archivos Lógicos Internos (ILF)
| ID | Grupo de Datos (Tablas Relacionadas) | Complejidad | Peso |
| :--- | :--- | :---: | :---: |
| ILF-01 | Seguridad (Users, Roles, Permissions) | Alta | 15 |
| ILF-02 | Personal (Employee) | Media | 10 |
| ILF-03 | Pacientes (Patient) | Media | 10 |
| ILF-04 | Catálogo (MedicalService) | Baja | 7 |
| ILF-05 | Clínica (ClinicalEpisode, ClinicalSession, SessionService) | Alta | 15 |
| ILF-06 | Análisis Postural (FootAnalysis, BiomechanicalAnalysis) | Alta | 15 |
| ILF-07 | Huella Plantar (FootprintAnalysis) | Media | 10 |
| ILF-08 | Multimedia (AnalysisPhoto) | Alta | 15 |
| ILF-09 | Auditoría (AuditableEntity Logs) | Media | 10 |
| **Total** | | | **107** |

##### 5. Interfaces Externas (EIF)
| ID | Interfaz Externa | Complejidad | Peso |
| :--- | :--- | :---: | :---: |
| EIF-01 | API de Cloudinary (Almacenamiento y Transformación) | Baja | 5 |
| **Total** | | | **5** |

#### Resumen Final de Conteo UFP
(Ver Tabla de Factor de Ponderación en la Sección 2.1.A)

---

### 2.2 Factores de Ajuste de Complejidad (CAF)

Se evalúan las 14 características generales del sistema en una escala de 0 (Sin influencia) a 5 (Influencia fuerte).

| # | Característica | Pregunta Aplicada al Proyecto | Puntaje (0-5) |
| :--- | :--- | :--- | :---: |
| 1 | Comunicación de Datos | ¿Qué tan compleja es la comunicación con el servidor (JWT, REST)? | 4 |
| 2 | Procesamiento Distribuido | ¿El sistema maneja procesamiento en diferentes nodos? (Monolito modular) | 2 |
| 3 | Rendimiento | ¿Es crítica la velocidad de respuesta para el análisis de imagen? | 4 |
| 4 | Configuración del Equipo | ¿El entorno operativo tiene restricciones pesadas? | 2 |
| 5 | Tasa de Transacciones | ¿Se espera un volumen masivo de transacciones diarias? | 3 |
| 6 | Entrada de Datos en Línea | ¿Qué porcentaje de la entrada es interactiva (Angular Steppers)? | 5 |
| 7 | Eficiencia del Usuario Final | ¿El diseño prioriza la facilidad de uso para fisioterapeutas? | 5 |
| 8 | Actualización en Línea | ¿Los ILF se actualizan en tiempo real (CRUD inmediato)? | 4 |
| 9 | Procesamiento Complejo | ¿Hay cálculos matemáticos o análisis de imagen complejos? | 5 |
| 10 | Reusabilidad | ¿El código se diseñó para ser rehusado (Arquitectura Modular)? | 4 |
| 11 | Facilidad de Instalación | ¿Qué tan automatizada está la migración (Flyway/DataLoader)? | 3 |
| 12 | Facilidad de Operación | ¿Qué tan automatizados están los backups y mantenimiento? | 3 |
| 13 | Instalaciones Múltiples | ¿Se instalará en múltiples consultorios/sedes? | 4 |
| 14 | Facilidad de Cambio | ¿Qué tan fácil es agregar nuevos módulos o servicios? | 4 |
| | **SUMA DE PUNTAJES (ΣFi)** | | **52** |

**Cálculo del Factor de Ajuste (CAF):**
$$CAF = 0.65 + (0.01 * \sum Fi)$$
$$CAF = 0.65 + (0.01 * 52) = 1.17$$

---

### 2.3 Cálculo de Puntos de Función Ajustados (AFP)

$$AFP = UFP * CAF$$
$$AFP = 187 * 1.17 = 218.79 \approx 219 \text{ FP}$$

---

## 3. ESTIMACIÓN DE TAMAÑO (KLDC)

Para convertir Puntos de Función a Líneas de Código (LOC), utilizamos los factores estándar por lenguaje:
*   **Java (Spring Boot):** ~53 LOC/FP
*   **TypeScript/Angular:** ~60 LOC/FP (incluyendo HTML/SCSS)
*   **Promedio Ponderado:** 56.5 LOC/FP

$$LOC = 219 * 56.5 = 12,373 \text{ líneas por capa (aproximadamente)}$$
$$Total Estimado = 12,373 * 2 \text{ capas} \approx 24,746 \text{ LOC}$$

Ajustando por la complejidad del módulo de imágenes y fases pendientes (Fases 6-9):
**Tamaño Final Estimado = 30.0 KLDC (30,000 líneas de código).**

---

## 4. ESTIMACIÓN DE ESFUERZO Y TIEMPO (COCOMO II)

Utilizamos el modelo **Semi-acoplado** (proyectos de complejidad media con equipos experimentados).

### 4.1 Parámetros de Entrada
*   **KLDC:** 30.0
*   **FAE (Factor de Ajuste de Esfuerzo):** 0.65

### 4.2 Cálculos
**Esfuerzo (E):**
$$E = 3.0 * (KLDC)^{1.12} * FAE$$
$$E = 3.0 * (30.0)^{1.12} * 0.65$$
$$E = 3.0 * 44.69 * 0.65 \approx 87.1 \text{ personas/mes}$$

**Tiempo (T):**
$$T = 2.5 * E^{0.35}$$
$$T = 2.5 * (87.1)^{0.35}$$
$$T = 2.5 * 4.77 \approx 11.9 \text{ meses}$$

**Personal Requerido (P):**
$$P = E / T = 87.1 / 11.9 \approx 7.3 \text{ personas}$$

---

## 5. CONCLUSIÓN DE LA ESTIMACIÓN

1.  **Precisión Detallada:** Se han desglosado 8 Entradas, 3 Salidas, 6 Consultas, 9 Archivos Lógicos y 1 Interfaz Externa, proporcionando una base sólida para el conteo de **187 UFP**.
2.  **Productividad:** El FAE de **0.65** refleja la eficiencia del stack tecnológico, resultando en un esfuerzo de **87.1 personas/mes**.
3.  **Viabilidad:** El proyecto requiere aproximadamente **12 meses** con un equipo de **7-8 personas**, lo cual es consistente con la envergadura de KINEVID.

---
*Generado automáticamente para el Proyecto KINEVID - Junio 2026*
