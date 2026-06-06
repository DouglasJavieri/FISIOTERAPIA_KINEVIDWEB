# EVALUACIÓN DE COSTOS Y BENEFICIOS - PROYECTO KINEVID

## 1. INTRODUCCIÓN

El presente documento detalla la evaluación económica del sistema **KINEVID**, una plataforma integral para la gestión de centros de fisioterapia. La evaluación se realiza mediante el modelo **COCOMO II** para la estimación de costos de desarrollo y los indicadores financieros **VAN (Valor Actual Neto)** y **TIR (Tasa Interna de Retorno)** para determinar la viabilidad y rentabilidad de la inversión.

KINEVID no solo busca automatizar procesos administrativos, sino también proporcionar herramientas especializadas como el análisis postural mediante imágenes, lo que representa un valor agregado significativo frente a sistemas de gestión genéricos.

## 2. ESTIMACIÓN DE COSTOS CON COCOMO II

Para el cálculo del esfuerzo y tiempo de desarrollo, se utiliza el modelo **COCOMO II Intermedio**, aplicando el modo de desarrollo **Semi-acoplado**, dado que el proyecto presenta una complejidad media y requiere la integración de módulos de procesamiento de imágenes y gestión clínica.

### 2.1 Estimación de Tamaño (KLDC)

Basado en el análisis de la base de código actual y las fases pendientes de implementación:

*   **Backend (Java/Spring Boot):** ~12,075 líneas.
*   **Frontend (Angular/TS/HTML/CSS):** ~15,310 líneas.
*   **Total actual:** 27,385 líneas.
*   **Estimación post-fases 7, 8 y 9:** ~32,000 líneas (32 KLDC).

| Lenguaje | KLDC Estimado |
| :--- | :--- |
| Java (Spring Boot) | 14.5 |
| TypeScript / HTML / SCSS | 17.5 |
| **Total (KLDC)** | **32.0** |

### 2.2 Factores de Ajuste de Esfuerzo (FAE)

Se evalúan los 15 conductores de costo para ajustar la estimación base:

| Conductor de Costo | Clasificación | Valor |
| :--- | :--- | :--- |
| **RELY** (Confiabilidad requerida) | Alta | 1.15 |
| **DATA** (Tamaño de la base de datos) | Nominal | 1.00 |
| **CPLX** (Complejidad del producto) | Alta (Procesamiento de imagen) | 1.15 |
| **TIME** (Restricción de tiempo de ejecución) | Nominal | 1.00 |
| **STOR** (Restricción de almacenamiento) | Nominal | 1.00 |
| **VIRT** (Volatilidad de la máquina virtual) | Baja | 0.87 |
| **TURN** (Tiempo de respuesta) | Nominal | 1.00 |
| **ACAP** (Capacidad del analista) | Alta | 0.86 |
| **AEXP** (Experiencia en aplicaciones) | Alta | 0.91 |
| **PCAP** (Capacidad del programador) | Alta | 0.86 |
| **VEXP** (Experiencia en el entorno virtual) | Nominal | 1.00 |
| **LEXP** (Experiencia en lenguajes) | Alta | 0.95 |
| **MODP** (Prácticas de programación) | Alta | 0.91 |
| **TOOL** (Uso de herramientas de software) | Alta | 0.91 |
| **SCED** (Plazo de desarrollo requerido) | Nominal | 1.00 |

**Cálculo del FAE:**
$$FAE = 1.15 * 1.00 * 1.15 * 1.00 * 1.00 * 0.87 * 1.00 * 0.86 * 0.91 * 0.86 * 1.00 * 0.95 * 0.91 * 0.91 * 1.00$$
$$FAE \approx 0.65$$

### 2.3 Cálculos de Esfuerzo, Tiempo y Personal

Utilizando los parámetros para el modo **Semi-acoplado** (a=3.0, b=1.12, c=2.5, d=0.35):

**Esfuerzo (E):**
$$E = a * (KLDC)^b * FAE$$
$$E = 3.0 * (32.0)^{1.12} * 0.65$$
$$E = 3.0 * 48.05 * 0.65 \approx 93.7 \text{ personas/mes}$$

*Nota: Debido a la alta eficiencia (FAE < 1.0) y el uso de frameworks modernos, el esfuerzo se reduce significativamente.*

**Tiempo (T):**
$$T = c * E^d$$
$$T = 2.5 * (93.7)^{0.35}$$
$$T = 2.5 * 4.90 \approx 12.25 \text{ meses}$$

**Personal Promedio (P):**
$$P = E / T = 93.7 / 12.25 \approx 7.6 \text{ personas}$$

## 3. ESTIMACIÓN DE COSTOS TOTALES

Se desglosan los costos de inversión inicial y costos operativos anuales.

| Categoría | Concepto | Costo (USD) | Costo (BS) |
| :--- | :--- | :--- | :--- |
| **Inversión** | Desarrollo de Software (Estimado COCOMO) | $15,000 | 104,400 |
| **Inversión** | Equipamiento (Computadoras/Cámaras) | $2,000 | 13,920 |
| **Operativo** | Infraestructura Cloud (Cloudinary/VPS) | $500/año | 3,480 |
| **Operativo** | Mantenimiento y Soporte | $1,200/año | 8,352 |
| **TOTAL INVERSIÓN INICIAL** | | **$17,000** | **118,320** |

## 4. ANÁLISIS DE BENEFICIO (VAN Y TIR)

Para el cálculo de los beneficios, se proyectan los ahorros en tiempo de diagnóstico, incremento en la retención de pacientes y optimización de la agenda clínica.

### 4.1 Flujo de Caja Proyectado (5 años)

Se estima un crecimiento gradual a medida que el sistema se estabiliza y atrae más pacientes.

| Año | Flujo de Caja (BS) | Descripción |
| :--- | :---: | :--- |
| 0 | -118,320 | Inversión Inicial |
| 1 | 35,000 | Ahorro operativo y nuevos pacientes |
| 2 | 45,000 | Expansión de servicios (Análisis Postural) |
| 3 | 55,000 | Consolidación del mercado |
| 4 | 65,000 | Optimización máxima |
| 5 | 75,000 | Madurez del sistema |

### 4.2 Valor Actual Neto (VAN)

Utilizando una tasa de descuento (k) del 12% (0.12):

$$VAN = -118,320 + \frac{35,000}{(1.12)^1} + \frac{45,000}{(1.12)^2} + \frac{55,000}{(1.12)^3} + \frac{65,000}{(1.12)^4} + \frac{75,000}{(1.12)^5}$$
$$VAN = -118,320 + 31,250 + 35,873 + 39,148 + 41,308 + 42,557$$
$$VAN = 71,826 \text{ BS}$$

**Interpretación:** Como el **VAN > 0**, el proyecto es financieramente viable y genera un valor excedente de **71,826 BS** sobre la inversión inicial y el costo de capital.

### 4.3 Tasa Interna de Retorno (TIR)

La TIR es la tasa que hace que el VAN sea igual a cero.
Calculando mediante interpolación o software financiero:
**TIR \approx 28%**

**Interpretación:** Como la **TIR (28%) > Tasa de Descuento (12%)**, el proyecto es altamente rentable, ofreciendo un retorno muy superior al costo de oportunidad.

## 5. RELACIÓN COSTO-BENEFICIO

$$Relación C/B = \frac{\text{Valor Presente de los Beneficios}}{\text{Inversión Inicial}}$$
$$Relación C/B = \frac{190,136}{118,320} \approx 1.61$$

**Interpretación:** Por cada 1 boliviano invertido, se obtiene un beneficio de **1.61 bolivianos**.

## 6. CONCLUSIONES

1.  **Viabilidad Técnica:** El sistema KINEVID cuenta con una base sólida de código (~32k líneas proyectadas) y utiliza tecnologías modernas que reducen los riesgos de desarrollo.
2.  **Rentabilidad Económica:** Con un **VAN de 71,826 BS** y una **TIR del 28%**, el proyecto supera ampliamente los criterios de aceptación financiera.
3.  **Impacto en el Negocio:** La relación costo-beneficio de **1.61** confirma que la automatización y las herramientas de análisis clínico no solo pagan la inversión, sino que generan utilidades sostenibles a largo plazo para el centro de fisioterapia.
