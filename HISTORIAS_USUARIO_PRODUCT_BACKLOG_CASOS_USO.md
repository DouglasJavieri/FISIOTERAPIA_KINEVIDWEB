# KINEVID — Historias de Usuario, Product Backlog y Casos de Uso
> **Documento Profesional de Requisitos**  
> **Fecha:** Mayo 21, 2026  
> **Versión:** 1.0  
> **Preparado para:** Sistema de Gestión Clínica Integral de Fisioterapia  

---

## 📑 ÍNDICE

1. [Actores del Sistema](#1-actores-del-sistema)
2. [Historias de Usuario por Módulo](#2-historias-de-usuario-por-módulo)
3. [Product Backlog Profesional](#3-product-backlog-profesional)
4. [Casos de Uso por Módulo](#4-casos-de-uso-por-módulo)
5. [Caso de Uso General del Sistema](#5-caso-de-uso-general-del-sistema)

---

## 1. Actores del Sistema

### 1.1 Actores Principales

| Actor | Descripción | Responsabilidades |
|-------|-------------|-------------------|
| **ROOT (Superadministrador)** | Usuario con acceso total al sistema | Gestión de todo el sistema, permisos, roles, usuarios, empleados, auditoría |
| **ADMIN (Administrador)** | Gestor administrativo del consultorio | Gestión de usuarios, empleados, servicios, configuración del sistema |
| **FISIOTERAPEUTA (Profesional)** | Profesional de salud especializado | Gestión de pacientes, episodios, sesiones clínicas, análisis de imagen, reportes |
| **RECEPCIONISTA** | Personal de recepción y atención | Registro de pacientes, lista de servicios, creación de sesiones básicas |
| **PACIENTE** | Usuario final del sistema (futuro) | Acceso a su historial, reportes, seguimiento |

### 1.2 Actores Secundarios

| Actor | Descripción |
|-------|-------------|
| **Cloudinary (Proveedor Externo)** | Servicio de almacenamiento de imágenes y PDFs |
| **Base de Datos PostgreSQL** | Persistencia de datos del sistema |
| **Sistema de Auditoría** | Registro de acciones y cambios |

---

## 2. Historias de Usuario por Módulo

### 2.1 MÓDULO: AUTENTICACIÓN Y GESTIÓN DE USUARIOS

#### HU-AUTH-001: Autenticación con JWT
```
Como CUALQUIER USUARIO
Quiero iniciar sesión con mis credenciales
Para acceder al sistema de manera segura

Criterios de Aceptación:
✓ El sistema valida usuario y contraseña contra la BD
✓ Si válidos: genera access token (10 min) + refresh token (7 días)
✓ Tokens se envían en headers HTTP Authorization
✓ El usuario obtiene acceso según sus permisos
✓ Si inválidos: retorna error 401 Unauthorized

Tareas:
- POST /api/auth/login → valida credenciales → retorna tokens
- POST /api/auth/refresh → válida refresh token → retorna nuevo access token
- POST /api/auth/logout → invalida sesión
```

#### HU-AUTH-002: Gestión de Usuarios (CRUD)
```
Como ADMIN o ROOT
Quiero crear, ver, actualizar y eliminar usuarios del sistema
Para mantener el control de acceso y permisos

Criterios de Aceptación:
✓ Puedo crear usuario con email, nombre, contraseña, rol
✓ Los datos se validan (email único, contraseña fuerte)
✓ Puedo ver listado paginado de usuarios
✓ Puedo editar datos del usuario (excepto ID)
✓ Puedo cambiar estado de usuario (ACTIVE / INACTIVE / DISABLED)
✓ Puedo eliminar lógicamente usuario

Tareas:
- POST /api/users/create → crear usuario
- GET /api/users/list → listar usuarios paginado
- GET /api/users/{id} → obtener detalles
- PUT /api/users/update/{id} → actualizar usuario
- PATCH /api/users/{id}/status → cambiar estado
- DELETE /api/users/delete/{id} → eliminar lógico
```

#### HU-AUTH-003: Asignación de Roles
```
Como ROOT o ADMIN
Quiero asignar roles a usuarios
Para controlar qué módulos pueden acceder

Criterios de Aceptación:
✓ Un usuario puede tener múltiples roles
✓ Cambios de rol son efectivos inmediatamente
✓ Se registra en auditoría quién y cuándo cambió roles
✓ ROOT puede asignar cualquier rol
✓ ADMIN solo puede asignar roles menores

Tareas:
- POST /api/users/{userId}/roles/{roleId} → asignar rol
- DELETE /api/users/{userId}/roles/{roleId} → remover rol
- GET /api/users/{userId}/roles → listar roles del usuario
```

#### HU-AUTH-004: Permisos Granulares
```
Como ROOT
Quiero definir permisos específicos por módulo
Para tener control fino sobre qué puede hacer cada rol

Criterios de Aceptación:
✓ Puedo crear nuevos permisos (código único)
✓ Puedo asignar permisos a roles
✓ Los permisos se validan en cada acción
✓ Los permisos nuevos se sincronizan con roles automáticamente

Tareas:
- POST /api/permissions/create → crear permiso
- POST /api/roles/{roleId}/permissions/{permissionId} → asignar
- GET /api/roles/{roleId}/permissions → listar permisos
- Validación en interceptor HTTP (hasPermission)
```

---

### 2.2 MÓDULO: GESTIÓN DE EMPLEADOS

#### HU-EMP-001: Registro de Empleados
```
Como ADMIN o ROOT
Quiero registrar empleados del consultorio
Para tener su información centralizada

Criterios de Aceptación:
✓ Puedo registrar empleado con datos personales
✓ Puedo asignar un usuario existente al empleado
✓ El empleado puede tener múltiples especialidades
✓ El estado del empleado afecta su disponibilidad
✓ Se permite editar y eliminar lógicamente

Tareas:
- POST /api/employee/create → crear empleado
- GET /api/employee/list → listar empleados
- GET /api/employee/active-list → listar activos (sin paginar)
- PUT /api/employee/update/{id} → actualizar
- PATCH /api/employee/{id}/status → cambiar estado
```

#### HU-EMP-002: Asignación de Usuarios a Empleados
```
Como ADMIN
Quiero vincular un usuario del sistema con un empleado
Para que el profesional pueda acceder con sus credenciales

Criterios de Aceptación:
✓ Un empleado tiene máximo un usuario asignado
✓ Un usuario puede estar asignado a máximo un empleado
✓ El usuario hereda permisos según el rol del empleado
✓ Se puede desasignar usuario de empleado

Tareas:
- POST /api/employee/{empId}/assign-user/{userId}
- DELETE /api/employee/{empId}/remove-user
- GET /api/employee/{empId}/assigned-user
```

#### HU-EMP-003: Lista Activa de Fisioterapeutas
```
Como FISIOTERAPEUTA o RECEPCIONISTA
Quiero ver la lista de fisioterapeutas activos
Para seleccionar quién atenderá al paciente en la sesión

Criterios de Aceptación:
✓ Se muestra lista sin paginación
✓ Solo empleados con estado ACTIVE aparecen
✓ Se muestran nombre completo y especialidad
✓ La lista se actualiza automáticamente

Tareas:
- GET /api/employee/active-list → retorna List<EmployeeResponse>
```

---

### 2.3 MÓDULO: GESTIÓN DE PACIENTES

#### HU-PAC-001: Registro de Pacientes
```
Como RECEPCIONISTA o FISIOTERAPEUTA
Quiero registrar un nuevo paciente en el sistema
Para iniciar su historial clínico

Criterios de Aceptación:
✓ Puedo ingresar datos personales (nombre, cédula, edad, género)
✓ Puedo ingresar contacto (teléfono, email)
✓ Puedo ingresar grupo sanguíneo y alergias
✓ El paciente comienza con estado ACTIVE
✓ Se crea automáticamente el Episodio 1

Tareas:
- POST /api/patient/create → crear paciente
- GET /api/patient/list → listar paginado
- GET /api/patient/{id} → obtener perfil completo
```

#### HU-PAC-002: Búsqueda de Pacientes Existentes
```
Como RECEPCIONISTA
Quiero buscar un paciente por nombre o cédula
Para verificar si ya existe en el sistema

Criterios de Aceptación:
✓ Búsqueda por nombre (parcial o completo)
✓ Búsqueda por cédula (exacta)
✓ Búsqueda por estado (ACTIVE, INACTIVE, DISCHARGE)
✓ Resultados paginados
✓ Si existe y está DISCHARGE: muestro botón "Reactivar"

Tareas:
- GET /api/patient/search?query=...&status=...
- GET /api/patient/list → con filtros
```

#### HU-PAC-003: Edición de Datos de Paciente
```
Como FISIOTERAPEUTA o ADMIN
Quiero editar los datos de un paciente existente
Para mantener información actualizada

Criterios de Aceptación:
✓ Puedo editar datos personales
✓ Puedo cambiar grupo sanguíneo y alergias
✓ Puedo actualizar contacto
✓ Cambios quedan registrados en auditoría
✓ Si paciente es DISCHARGE, no puedo editar (solo ver)

Tareas:
- PUT /api/patient/update/{id} → actualizar paciente
- PATCH /api/patient/{id}/status → cambiar estado
```

#### HU-PAC-004: Reactivación de Pacientes Dados de Alta
```
Como FISIOTERAPEUTA
Quiero reactivar un paciente que estuvo de alta (DISCHARGE)
Para comenzar una nueva etapa de tratamiento

Criterios de Aceptación:
✓ Solo pacientes DISCHARGE pueden reactivarse
✓ Al reactivar: estado → ACTIVE
✓ Se crea automáticamente un nuevo episodio
✓ El paciente vuelve a disponibilidad para sesiones

Tareas:
- POST /api/patient/{id}/reactivate
- POST /api/clinical-episode/{patientId}/reactivate
```

#### HU-PAC-005: Perfil de Paciente con Episodios
```
Como FISIOTERAPEUTA
Quiero ver el perfil completo del paciente con su historial
Para tener contexto de su tratamiento

Criterios de Aceptación:
✓ Muestro datos personales del paciente
✓ Muestro episodio ACTIVE actual
✓ Muestro sesiones del episodio activo
✓ Muestro episodios cerrados (solo lectura, colapsados)
✓ Acceso rápido a crear nueva sesión
✓ Acceso rápido a sesiones activas

Tareas:
- GET /api/patient/{id} → retorna datos completos + episodios
- Componente: patient-profile.component.ts
```

---

### 2.4 MÓDULO: GESTIÓN DE SERVICIOS MÉDICOS

#### HU-SVC-001: Registro de Servicios Médicos
```
Como ADMIN
Quiero registrar servicios médicos que ofrece el consultorio
Para que los fisioterapeutas puedan aplicarlos en sesiones

Criterios de Aceptación:
✓ Puedo crear servicio con nombre y descripción
✓ Asigno categoría (THERAPY, POSTURAL_ANALYSIS, ASSESSMENT, etc.)
✓ Ingreso precio unitario
✓ Defino estado (ACTIVE / INACTIVE)
✓ Se puede editar y eliminar lógicamente

Tareas:
- POST /api/medical-service/create → crear servicio
- GET /api/medical-service/list → listar servicios
- PUT /api/medical-service/update/{id} → actualizar
- PATCH /api/medical-service/{id}/status → cambiar estado
```

#### HU-SVC-002: Búsqueda y Filtrado de Servicios
```
Como FISIOTERAPEUTA o RECEPCIONISTA
Quiero ver los servicios disponibles
Para seleccionar los que aplicaré en la sesión

Criterios de Aceptación:
✓ Puedo filtrar por categoría
✓ Puedo filtrar por estado (ACTIVE / INACTIVE)
✓ Se muestran en lista paginada
✓ Muestro precio unitario
✓ Búsqueda por nombre

Tareas:
- GET /api/medical-service/list?category=...&status=...
```

#### HU-SVC-003: Servicios Especiales - Análisis de Pisada
```
Como SISTEMA
Quiero identificar el servicio "Análisis de Pisada"
Para activar el módulo de análisis biomecánico

Criterios de Aceptación:
✓ Existe servicio con nombre "Análisis de Pisada"
✓ Tiene categoría POSTURAL_ANALYSIS
✓ Si se selecciona este servicio: activo módulo de imagen
✓ Botón "Ir a Análisis de Imagen" se muestra solo con este servicio

Tareas:
- Identificar servicio por nombre en SessionListComponent
```

---

### 2.5 MÓDULO: EPISODIOS CLÍNICOS

#### HU-EPI-001: Creación de Episodios Clínicos
```
Como SISTEMA (automático) / FISIOTERAPEUTA (manual)
Quiero crear un episodio clínico para agrupar sesiones
Para organizar la atención por etapas de tratamiento

Criterios de Aceptación:
✓ Episodio se crea automáticamente al registrar paciente nuevo
✓ Episodio se crea automáticamente al reactivar paciente
✓ Puedo crear episodio manualmente (solo 1 ACTIVE por paciente)
✓ Ingreso: motivo de admisión, fecha inicio
✓ Estado por defecto: ACTIVE
✓ Se numera secuencialmente (Episodio 1, 2, 3...)

Tareas:
- POST /api/clinical-episode/create
- Trigger automático en creación de paciente
- Trigger automático al reactivar paciente
```

#### HU-EPI-002: Cierre de Episodio (Alta Médica)
```
Como FISIOTERAPEUTA
Quiero cerrar un episodio clínico cuando el paciente se da de alta
Para registrar el fin de esa etapa de tratamiento

Criterios de Aceptación:
✓ Solo episodios ACTIVE pueden cerrarse
✓ Ingreso motivo de alta
✓ Episodio → CLOSED
✓ Automáticamente: Paciente → DISCHARGE
✓ El fisioterapeuta puede reactivar paciente después

Tareas:
- PATCH /api/clinical-episode/{id}/close
- Cambiar automáticamente estado paciente
```

#### HU-EPI-003: Historial de Episodios
```
Como FISIOTERAPEUTA
Quiero ver todos los episodios (activos y cerrados) del paciente
Para revisar su historial completo de tratamientos

Criterios de Aceptación:
✓ Muestro episodio ACTIVE expandido (con sesiones)
✓ Episodios CLOSED colapsados (solo lectura)
✓ Puedo expandir para ver sesiones históricas
✓ Acceso a análisis y reportes de episodios cerrados

Tareas:
- GET /api/clinical-episode/patient/{patientId}
- GET /api/clinical-episode/list → lista global paginada
- Componente: episode-list.component.ts
```

---

### 2.6 MÓDULO: SESIONES CLÍNICAS

#### HU-SES-001: Creación de Nueva Sesión
```
Como FISIOTERAPEUTA
Quiero crear una nueva sesión clínica dentro de un episodio
Para registrar la atención de un paciente

Criterios de Aceptación:
✓ Sesión se crea dentro del episodio ACTIVE
✓ Se genera automáticamente: número de sesión (1, 2, 3...)
✓ Fecha: automática (hoy)
✓ Ingreso: motivo de consulta
✓ Selecciono: fisioterapeuta responsable
✓ Estado por defecto: OPEN
✓ Se puede editar mientras esté OPEN

Tareas:
- POST /api/clinical-session/create
- GET /api/clinical-session/episode/{episodeId}
- Componente: clinical-session.component.ts (stepper paso 1)
```

#### HU-SES-002: Asignación de Servicios a Sesión
```
Como FISIOTERAPEUTA
Quiero asignar los servicios médicos aplicados en la sesión
Para documentar el tratamiento realizado

Criterios de Aceptación:
✓ Selecciono múltiples servicios (multiselect)
✓ Cada servicio: cantidad, precio unitario, notas
✓ Puedo agregar o quitar servicios mientras sesión esté OPEN
✓ Tabla de servicios aplicados actualizada en tiempo real
✓ Auto-completo de precio al seleccionar servicio

Tareas:
- POST /api/clinical-session/{id}/services → agregar/actualizar (upsert)
- GET /api/clinical-session/{id}/services → listar servicios
- DELETE /api/clinical-session/services/{serviceId}
```

#### HU-SES-003: Evaluación Clínica
```
Como FISIOTERAPEUTA
Quiero registrar la evaluación clínica del paciente
Para documentar el estado y evolución

Criterios de Aceptación:
✓ Ingreso: antecedentes relevantes
✓ Ingreso: evaluación kinesiológica
✓ Ingreso: tratamiento aplicado
✓ Ingreso: observaciones
✓ Ingreso: evolución
✓ Todo registrable en paso 2 del stepper

Tareas:
- PUT /api/clinical-session/update/{id}
- Campos: kinesiologicalEvaluation, treatmentApplied, observations, evolution
```

#### HU-SES-004: Cierre de Sesión
```
Como FISIOTERAPEUTA
Quiero cerrar una sesión cuando finalice la atención
Para marcar como completada

Criterios de Aceptación:
✓ Solo sesiones OPEN pueden cerrarse
✓ Puedo cerrar a CLOSED o CANCELLED
✓ Si tiene "Análisis de Pisada": debo generar reporte primero
✓ Sesión cerrada: sin ediciones adicionales
✓ Sesión cancelada: sin efectos en facturación

Tareas:
- PATCH /api/clinical-session/{id}/status → cambiar a CLOSED / CANCELLED
```

#### HU-SES-005: Edición de Sesión Abierta
```
Como FISIOTERAPEUTA
Quiero editar una sesión mientras esté OPEN
Para correcciones o actualización de datos

Criterios de Aceptación:
✓ Sesión OPEN: todos los campos editables
✓ Sesión CLOSED / CANCELLED: solo lectura
✓ Cambios en auditoría (quién, cuándo)
✓ Validaciones en cada cambio

Tareas:
- PUT /api/clinical-session/update/{id}
- Validar estado antes de permitir edición
```

---

### 2.7 MÓDULO: ANÁLISIS DE IMAGEN Y PISADA

#### HU-IMG-001: Captura de Fotos
```
Como FISIOTERAPEUTA
Quiero capturar fotos del paciente durante el análisis de pisada
Para análisis biomecánico y generación de reporte

Criterios de Aceptación:
✓ Puedo capturar hasta 6 fotos
✓ Origen: cámara o subida de archivos
✓ Preview de miniaturas
✓ Puedo eliminar y reemplazar fotos
✓ Fotos se suben a Cloudinary

Tareas:
- Componente: photo-gallery.component.ts
- POST /api/foot-analysis/{analysisId}/photos (multipart)
- DELETE /api/foot-analysis/{analysisId}/photos/{photoId}
```

#### HU-IMG-002: Análisis de Trazos en Canvas
```
Como FISIOTERAPEUTA
Quiero dibujar trazos sobre las fotos para marcar puntos de referencia
Para calcular ángulos y realizar análisis biomecánico

Criterios de Aceptación:
✓ Canvas HTML5 interactivo
✓ Dibujo de línea vertical para pie izquierdo
✓ Dibujo de línea vertical para pie derecho
✓ Marcación de puntos de ángulo
✓ Cálculo automático de ángulos internos y externos (grados)
✓ Guardado de anotaciones en JSON

Tareas:
- Componente: photo-canvas.component.ts
- PUT /api/foot-analysis/{analysisId}/photos/{photoId}/annotations
```

#### HU-IMG-003: Análisis Biomecánico por Pie
```
Como FISIOTERAPEUTA
Quiero registrar datos biomecánicos específicos de cada pie
Para documentar hallazgos clínicos

Criterios de Aceptación:
✓ Análisis por pie: LEFT y RIGHT
✓ Regla Maleolo Tibial (dropdown)
✓ Desgaste de calzado (texto)
✓ Palpación tibial (texto)
✓ Marcha (dropdown: NORMAL/PRONADOR/SUPINADOR/MIXTO)
✓ Se guarda en tabla biomechanical_analysis

Tareas:
- Componente: biomechanical-form.component.ts
- POST /api/foot-analysis/{analysisId}/biomechanical
```

#### HU-IMG-004: Evaluación de Huella Plantar Genérica
```
Como FISIOTERAPEUTA
Quiero clasificar el tipo de huella plantar del paciente
Para diagnóstico e indicaciones de tratamiento

Criterios de Aceptación:
✓ Selecciono UNA clasificación que aplica al paciente
✓ Opciones: ÍNDICE_NORMAL, ÍNDICE_PIE_PLANO, ÍNDICE_PIE_CAVO, PIE_PLANO, PIE_NORMAL, PIE_CAVO, etc.
✓ Se guarda una única vez en footprint_analysis
✓ Notas adicionales opcionales
✓ Aplica a toda la sesión (no por pie separado)

Tareas:
- Componente: footprint-form.component.ts
- POST /api/foot-analysis/{analysisId}/footprint
```

#### HU-IMG-005: Resumen y Diagnóstico
```
Como FISIOTERAPEUTA
Quiero completar el resumen general del análisis de pisada
Para sintetizar hallazgos y emitir diagnóstico

Criterios de Aceptación:
✓ Antecedentes relevantes (texto)
✓ Evaluación kinésica (texto)
✓ Observaciones (texto)
✓ Diagnóstico (radio: NORMAL/PRONACIÓN/SUPINACIÓN)
✓ Distancia Intermaleolar (número)
✓ Distancia Intercondílea (número)
✓ Seleccionar fotos para incluir en reporte

Tareas:
- Componente: foot-analysis.component.ts (paso 5)
- PUT /api/foot-analysis/update/{id}
```

---

### 2.8 MÓDULO: GENERACIÓN DE REPORTES PDF

#### HU-RPT-001: Generación de Reporte PDF
```
Como FISIOTERAPEUTA
Quiero generar un reporte PDF del análisis de pisada
Para documentación y entrega al paciente

Criterios de Aceptación:
✓ PDF contiene: datos paciente, fecha, profesional, episodio/sesión
✓ Incluye: servicios aplicados, evaluación clínica
✓ Incluye: datos análisis de pisada, fotos seleccionadas con trazos
✓ Incluye: diagnóstico, medidas, tipo de huella
✓ Se sube automáticamente a Cloudinary
✓ Se guarda URL en BD (NO el PDF)
✓ Se puede regenerar múltiples veces

Tareas:
- POST /api/foot-analysis/{id}/report/generate
- ReportService.generateFootAnalysisReport(Long analysisId)
- Dependencia: iText 7 en pom.xml
```

#### HU-RPT-002: Descarga de Reporte PDF
```
Como FISIOTERAPEUTA o PACIENTE
Quiero descargar el reporte PDF generado
Para distribución y archivos

Criterios de Aceptación:
✓ Botón "Descargar Reporte" en sesión con análisis
✓ Link desde URL de Cloudinary
✓ Descarga bajo demanda (sin restricciones)
✓ Se puede descargar múltiples veces
✓ Historial de descargas disponible

Tareas:
- GET /api/foot-analysis/{id}/report → retorna URL
- Componente: descarga desde Cloudinary
```

---

## 3. Product Backlog Profesional

### Estructura de Priorización

| Prioridad | Criterios |
|-----------|-----------|
| **CRITICAL** | Bloquea otras funcionalidades, impacto en múltiples módulos |
| **HIGH** | Funcionalidad esencial del módulo |
| **MEDIUM** | Funcionalidad secundaria pero importante |
| **LOW** | Mejora o funcionalidad futura |

### Product Backlog Ordenado

#### FASE 1: INFRAESTRUCTURA BASE (COMPLETADA)

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-001 | Autenticación JWT + Refresh Token | Autenticación | CRITICAL | 3 días | ✅ HECHO |
| PB-002 | CRUD de Usuarios | Usuarios | CRITICAL | 3 días | ✅ HECHO |
| PB-003 | Gestión de Roles y Permisos RBAC | Usuarios | CRITICAL | 3 días | ✅ HECHO |
| PB-004 | DataLoader: seed de permisos y roles | Sistema | CRITICAL | 1 día | ✅ HECHO |
| PB-005 | Sincronización automática de permisos | Sistema | HIGH | 1 día | ✅ HECHO |

#### FASE 2: GESTIÓN DE RECURSOS (COMPLETADA)

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-006 | CRUD de Empleados | Empleados | CRITICAL | 2 días | ✅ HECHO |
| PB-007 | Asignación Usuario ↔ Empleado | Empleados | HIGH | 1 día | ✅ HECHO |
| PB-008 | Lista activa de Fisioterapeutas | Empleados | HIGH | 1 día | ✅ HECHO |
| PB-009 | CRUD de Servicios Médicos | Servicios | CRITICAL | 2 días | ✅ HECHO |
| PB-010 | Filtrado y búsqueda de Servicios | Servicios | HIGH | 1 día | ✅ HECHO |

#### FASE 3: GESTIÓN DE PACIENTES (COMPLETADA)

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-011 | CRUD de Pacientes | Pacientes | CRITICAL | 2 días | ✅ HECHO |
| PB-012 | Búsqueda por nombre/cédula | Pacientes | HIGH | 1 día | ✅ HECHO |
| PB-013 | Filtrado por estado | Pacientes | HIGH | 1 día | ✅ HECHO |
| PB-014 | Perfil de Paciente | Frontend | CRITICAL | 1 día | ✅ HECHO |
| PB-015 | Cambio de estado de Paciente | Pacientes | HIGH | 1 día | ✅ HECHO |

#### FASE 4: HISTORIA CLÍNICA - EPISODIOS (COMPLETADA)

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-016 | Creación automática Episodio 1 | Episodios | CRITICAL | 1 día | ✅ HECHO |
| PB-017 | CRUD de Episodios Clínicos | Episodios | CRITICAL | 2 días | ✅ HECHO |
| PB-018 | Cierre de Episodio (Alta Médica) | Episodios | HIGH | 1 día | ✅ HECHO |
| PB-019 | Reactivación de Paciente | Episodios | HIGH | 1 día | ✅ HECHO |
| PB-020 | Historial de Episodios | Episodios | CRITICAL | 1 día | ✅ HECHO |

#### FASE 5: HISTORIA CLÍNICA - SESIONES (COMPLETADA)

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-021 | CRUD de Sesiones Clínicas | Sesiones | CRITICAL | 2 días | ✅ HECHO |
| PB-022 | Asignación de Servicios a Sesión (N:M) | Sesiones | CRITICAL | 2 días | ✅ HECHO |
| PB-023 | Registro de Evaluación Clínica | Sesiones | CRITICAL | 1 día | ✅ HECHO |
| PB-024 | Cambio de estado de Sesión | Sesiones | HIGH | 1 día | ✅ HECHO |
| PB-025 | Edición de Sesión OPEN | Sesiones | HIGH | 1 día | ✅ HECHO |
| PB-026 | Stepper 2 pasos (Datos + Evaluación) | Frontend | CRITICAL | 2 días | ✅ HECHO |

#### FASE 6: ANÁLISIS DE IMAGEN Y PISADA (PRÓXIMA)

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-027 | Backend: Entidades Análisis de Pisada | Análisis | CRITICAL | 3 días | ⏳ PRÓXIMA |
| PB-028 | Backend: Endpoints REST Análisis | Análisis | CRITICAL | 2 días | ⏳ PRÓXIMA |
| PB-029 | Backend: Almacenamiento en Cloudinary | Análisis | HIGH | 1 día | ⏳ PRÓXIMA |
| PB-030 | Frontend: Módulo Imaging | Análisis | CRITICAL | 4 días | ⏳ PRÓXIMA |
| PB-031 | Frontend: Photo Gallery Component | Análisis | CRITICAL | 1 día | ⏳ PRÓXIMA |
| PB-032 | Frontend: Canvas HTML5 para Trazos | Análisis | CRITICAL | 2 días | ⏳ PRÓXIMA |
| PB-033 | Frontend: Formulario Biomecánico | Análisis | CRITICAL | 1 día | ⏳ PRÓXIMA |
| PB-034 | Frontend: Evaluación Huella Plantar | Análisis | CRITICAL | 1 día | ⏳ PRÓXIMA |
| PB-035 | Frontend: Integración con Stepper | Análisis | HIGH | 1 día | ⏳ PRÓXIMA |

#### FASE 7: GENERACIÓN DE REPORTES PDF

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-036 | Backend: Generación PDF (iText 7) | Reportes | CRITICAL | 2 días | ⏳ PENDIENTE |
| PB-037 | Backend: Endpoints Generación/Descarga | Reportes | CRITICAL | 1 día | ⏳ PENDIENTE |
| PB-038 | Frontend: Botón Descargar PDF | Reportes | HIGH | 1 día | ⏳ PENDIENTE |
| PB-039 | Frontend: Vista Previa PDF | Reportes | MEDIUM | 1 día | ⏳ PENDIENTE |

#### FASE 8: MEJORAS Y OPTIMIZACIONES

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-040 | Migración a Flyway (control de versiones BD) | Sistema | MEDIUM | 1 día | ⏳ PENDIENTE |
| PB-041 | Dashboard de estadísticas | Frontend | LOW | 3 días | ⏳ PENDIENTE |
| PB-042 | Notificaciones en tiempo real | Sistema | MEDIUM | 2 días | ⏳ PENDIENTE |
| PB-043 | Exportación de datos a Excel | Reportes | MEDIUM | 1 día | ⏳ PENDIENTE |
| PB-044 | Auditoría detallada de cambios | Sistema | MEDIUM | 2 días | ⏳ PENDIENTE |

#### FASE 9: FUTURO - MODULACIÓN A MICROSERVICIOS

| ID | Historia | Módulo | Prioridad | Estimación | Estado |
|----|----------|--------|-----------|------------|--------|
| PB-045 | Extracción: Microservicio de Imaging | Arquitectura | LOW | 5 días | ⏳ FUTURO |
| PB-046 | Extracción: Microservicio de Reportes | Arquitectura | LOW | 3 días | ⏳ FUTURO |
| PB-047 | Integración con EHR externo | Integraciones | LOW | 5 días | ⏳ FUTURO |
| PB-048 | Portal del Paciente (acceso web) | Frontend | LOW | 5 días | ⏳ FUTURO |

---

## 4. Casos de Uso por Módulo

### 4.1 CASOS DE USO: AUTENTICACIÓN Y USUARIOS

#### CU-001: Iniciar Sesión
```
Título: Iniciar Sesión en el Sistema
Actor Primario: Usuario (cualquier rol)
Precondiciones: Usuario registrado en BD con credenciales válidas
Postcondiciones: Usuario autenticado, tokens generados

Flujo Principal:
1. Usuario accede a pantalla de login
2. Ingresa email y contraseña
3. Sistema valida contra BD
4. Si válidos:
   a. Genera access token (10 min) + refresh token (7 días)
   b. Retorna tokens en response
   c. Cliente almacena tokens en localStorage
   d. Redirige a dashboard
5. Si inválidos:
   a. Retorna error 401 Unauthorized
   b. Muestra mensaje de error

Flujos Alternativos:
FA-1: Contraseña olvidada
- Usuario solicita recuperación de contraseña
- Sistema envía correo con link temporal
- Usuario establece nueva contraseña

FA-2: Token expirado
- Access token expira
- Cliente usa refresh token
- Sistema genera nuevo access token
- Operación se reintenta automáticamente

Validaciones:
- Email existe en BD
- Contraseña válida (hasheada)
- Usuario estado != DISABLED
- Rol asignado
```

#### CU-002: Crear Usuario
```
Título: Crear Nuevo Usuario en el Sistema
Actor Primario: ADMIN o ROOT
Precondiciones: Usuario autenticado con permiso CREATE_USER
Postcondiciones: Nuevo usuario creado, roles asignados

Flujo Principal:
1. ADMIN accede a módulo Usuarios
2. Click en "Nuevo Usuario"
3. Completa formulario: email, nombre, contraseña, rol
4. Valida:
   - Email único (no existe)
   - Contraseña fuerte (min 8 chars, mayúscula, número)
5. Asigna rol (ADMIN, FISIOTERAPEUTA, RECEPCIONISTA)
6. Guarda usuario
7. Muestra confirmación
8. Sistema envía email bienvenida (futuro)

Flujos Alternativos:
FA-1: Email duplicado
- Sistema rechaza
- Muestra error: "Email ya existe"

FA-2: Validación de contraseña
- Contraseña débil
- Sistema muestra requisitos

Validaciones:
- Email formato válido
- Contraseña cumple políticas
- Rol existe y es válido
- Auditoría: quién creó, cuándo
```

#### CU-003: Asignar Permisos a Rol
```
Título: Asignar Permisos a Rol Específico
Actor Primario: ROOT
Precondiciones: Rol existe, Permiso existe
Postcondiciones: Permiso asignado a rol, sincronizado

Flujo Principal:
1. ROOT accede a Módulo Roles
2. Selecciona rol (ej: FISIOTERAPEUTA)
3. Accede a tab "Permisos"
4. Lista permisos disponibles (checkboxes)
5. Marca permisos a asignar
6. Click "Guardar"
7. Sistema asigna permisos
8. Sincroniza con usuarios de ese rol (automático)
9. Muestra confirmación

Flujos Alternativos:
FA-1: Rol no modificable (ROOT)
- Solo ROOT puede ver opciones completas
- ADMIN ve opciones limitadas

FA-2: Permiso ya asignado
- Checkbox ya está marcado
- Puede desmarcarse para remover

Validaciones:
- Rol != ROOT (solo otros)
- Permisos válidos
- Auditoría de cambios
```

---

### 4.2 CASOS DE USO: GESTIÓN DE EMPLEADOS

#### CU-004: Registrar Empleado
```
Título: Registrar Nuevo Empleado del Consultorio
Actor Primario: ADMIN o ROOT
Precondiciones: ADMIN autenticado
Postcondiciones: Empleado registrado, con usuario opcional

Flujo Principal:
1. ADMIN accede a Módulo Empleados
2. Click "Nuevo Empleado"
3. Completa datos:
   - Nombre completo
   - Cédula (única)
   - Especialidad (ej: Fisioterapia, Masoterapia)
   - Teléfono
   - Email
4. Selecciona estado: ACTIVE / INACTIVE
5. Opcionalmente: Asigna usuario existente
6. Guarda empleado
7. Muestra confirmación

Flujos Alternativos:
FA-1: Cédula duplicada
- Sistema rechaza
- Empleado ya existe

FA-2: Sin usuario asignado (todavía)
- Empleado registrado sin credenciales
- Se asigna usuario después (en otra operación)

Validaciones:
- Datos completos
- Cédula única
- Email único
```

#### CU-005: Asignar Usuario a Empleado
```
Título: Vincular Usuario del Sistema con Empleado
Actor Primario: ADMIN
Precondiciones: Usuario existe, Empleado existe
Postcondiciones: Vinculación establecida, 1:1

Flujo Principal:
1. ADMIN accede a formulario de Empleado
2. Tab "Usuario Asignado"
3. Dropdown: selecciona usuario (sin empleado aún)
4. Click "Asignar"
5. Sistema crea relación
6. Usuario hereda permisos del rol del empleado
7. Muestra confirmación

Restricciones:
- 1 usuario = máximo 1 empleado
- 1 empleado = máximo 1 usuario
- Si empleado ya tiene usuario: opción de cambiar

Flujos Alternativos:
FA-1: Usuario ya asignado
- Muestra: "¿Desasignar usuario anterior?"
- Opción para cambiar

FA-2: Sin usuarios disponibles
- Mensaje: "Cree un usuario primero"

Validaciones:
- Usuario y empleado existen
- No hay conflictos de asignación
```

---

### 4.3 CASOS DE USO: GESTIÓN DE PACIENTES

#### CU-006: Registrar Nuevo Paciente
```
Título: Registrar Nuevo Paciente en el Sistema
Actor Primario: RECEPCIONISTA o FISIOTERAPEUTA
Precondiciones: RECEPCIONISTA autenticado, permiso CREATE_PATIENT
Postcondiciones: Paciente registrado, Episodio 1 creado automáticamente

Flujo Principal:
1. RECEPCIONISTA accede a Módulo Pacientes
2. Click "Registrar Nuevo Paciente"
3. Completa datos en formulario (4 secciones):
   
   SECCIÓN 1: Datos Personales
   - Nombre completo *
   - Cédula (única) *
   - Fecha nacimiento *
   - Género (M/F) *
   - Teléfono
   - Email
   
   SECCIÓN 2: Datos Médicos
   - Grupo sanguíneo (O, A, B, AB)
   - Alergias (texto)
   - Enfermedades preexistentes
   
   SECCIÓN 3: Contacto Emergencia
   - Nombre
   - Relación
   - Teléfono
   
   SECCIÓN 4: Observaciones
   - Notas adicionales

4. Valida datos
5. Guarda paciente
6. Sistema automáticamente:
   - Estado paciente → ACTIVE
   - Crea Episodio 1 (numero=1, reason_for_admission=...)
   - Episodio estado → ACTIVE
7. Redirige a perfil del paciente
8. Muestra confirmación

Flujos Alternativos:
FA-1: Cédula duplicada
- Sistema advierte: "Paciente ya existe"
- Opción para verlo o crear nuevo

FA-2: Email duplicado
- Se permite (múltiples pacientes mismo email familiar)

FA-3: Datos incompletos
- Validación de campos obligatorios
- Marca errores en rojo

Validaciones:
- Campos obligatorios: Nombre, Cédula, Género, Fecha nac
- Cédula única
- Email formato válido
- Auditoría: creador, fecha
```

#### CU-007: Buscar Paciente Existente
```
Título: Buscar Paciente en el Sistema
Actor Primario: RECEPCIONISTA
Precondiciones: RECEPCIONISTA autenticado
Postcondiciones: Lista de pacientes resultantes mostrada

Flujo Principal:
1. RECEPCIONISTA accede a Módulo Pacientes
2. Ve lista con buscador
3. Ingresa criterio:
   - Por nombre (parcial)
   - Por cédula (exacta)
4. Selecciona filtro estado: ACTIVE / INACTIVE / DISCHARGE
5. Click "Buscar"
6. Sistema retorna resultados paginados
7. RECEPCIONISTA selecciona paciente
8. Se abre perfil del paciente

Si paciente está DISCHARGE:
- Botón destacado: "Reactivar Paciente"
- Click abre confirmación
- Si confirma → Paciente ACTIVE + Episodio N creado

Flujos Alternativos:
FA-1: Sin resultados
- Muestra: "No hay pacientes con ese criterio"
- Opción: "¿Crear nuevo paciente?"

FA-2: Múltiples coincidencias
- Tabla paginada (5, 10, 25 por página)
- Ordenable por columna

Validaciones:
- Búsqueda no vacía
- Al menos 1 carácter para nombre
```

#### CU-008: Ver Perfil de Paciente
```
Título: Ver Perfil Completo del Paciente
Actor Primario: FISIOTERAPEUTA
Precondiciones: Paciente existe, FISIOTERAPEUTA autenticado
Postcondiciones: Perfil mostrado con histórico

Flujo Principal:
1. FISIOTERAPEUTA navega a paciente (búsqueda o episodios)
2. Se abre vista de perfil (componente patient-profile):

   CARD SUPERIOR:
   - Foto (avatar)
   - Nombre, Cédula, Edad
   - Grupo sanguíneo, Alergias
   - Estado (badge color)

   SECCIÓN EPISODIOS (Acordeón):
   
   EPISODIO ACTUAL (ACTIVE) - expandido por defecto:
   - Número episodio, fechas
   - Motivo de admisión
   - Sesiones listadas:
     * Número, Fecha, Motivo, Terapeuta, Estado
     * Links rápidos: Ver/Editar, Cambiar estado
   - Botón: "Nueva Sesión"
   - Botón: "Acceso Rápido a Sesiones Activas"
   
   EPISODIOS CERRADOS (colapsados):
   - Episodio 1 (Mar 2024 - Jun 2024) [CLOSED]
   - Click para expandir y ver historial
   - Solo lectura (sin editar)
   - Reportes disponibles para descargar

3. Puede hacer acciones desde aquí:
   - Crear nueva sesión
   - Editar sesión abierta
   - Ver historial
   - Descargar reporte

Validaciones:
- Paciente existe
- Solo rol con permiso VIEW_PATIENT puede acceder
- Datos auditables
```

---

### 4.4 CASOS DE USO: EPISODIOS Y SESIONES CLÍNICAS

#### CU-009: Crear Sesión Clínica
```
Título: Crear Nueva Sesión Clínica dentro del Episodio
Actor Primario: FISIOTERAPEUTA
Precondiciones: Episodio ACTIVE existe, FISIOTERAPEUTA autenticado
Postcondiciones: Sesión creada en estado OPEN

Flujo Principal:
1. FISIOTERAPEUTA accede a Episodio ACTIVE
2. Click "Nueva Sesión"
3. Redirige a formulario con MAT-STEPPER (2 pasos)

   PASO 1: DATOS BÁSICOS + SERVICIOS
   ├─ Fecha (auto: hoy, editable)
   ├─ Motivo de consulta * (texto)
   ├─ Seleccionar Fisioterapeuta * (dropdown active-list)
   └─ Seleccionar Servicios * (multiselect)
       └─ Tabla en tiempo real:
           * Nombre servicio
           * Cantidad
           * Precio unitario (auto-completa)
           * Notas
           * Botón Eliminar
       └─ Si se selecciona "Análisis de Pisada":
           └─ Botón AZUL: "Ir a Análisis de Imagen"
               → REDIRIGE A PANTALLA SEPARADA (5 sub-pasos)
               → Vuelve a Paso 1 cuando completa

4. Valida Paso 1
5. Sistema crea sesión (BD):
   - episode_id = actual
   - session_number = máximo + 1
   - status = OPEN
   - created_by = usuario autenticado
   - has_foot_analysis = false (default)

6. Si TODO OK: Botón "Siguiente"
7. Avanza a PASO 2

   PASO 2: EVALUACIÓN CLÍNICA
   ├─ Antecedentes relevantes (texto)
   ├─ Evaluación kinesiológica (texto)
   ├─ Tratamiento aplicado (texto)
   ├─ Observaciones (texto)
   └─ Evolución (texto)
   └─ Botón "Cerrar Sesión" (estado → CLOSED)

8. Click "Cerrar Sesión"
9. Validaciones:
   - Si tiene Análisis de Pisada: reporte generado?
   - Todos campos Paso 2 completos?
10. Si TODO OK: Sesión → CLOSED
11. Redirige a lista de sesiones

Flujos Alternativos:
FA-1: Sin servicios seleccionados
- Advertencia pero se puede continuar
- Sesión sin servicios aplicados

FA-2: Análisis de Pisada → Reporte no generado
- Advertencia: "Por favor genere reporte antes de cerrar"
- No permite cerrar

FA-3: Usuario cancela en Paso 1
- Opción: "Volver" o "Cancelar"
- Si cancela: sesión NO se guarda

FA-4: Selecciona "Análisis de Pisada"
- Click en botón "Ir a Análisis"
- REDIRIGE a pantalla separada (/imaging)
- Completa 5 sub-pasos
- Click "Volver a Sesión"
- Retorna a Paso 1 (sesión actualizada, has_foot_analysis=true)

Validaciones:
- Motivo consulta no vacío
- Fisioterapeuta seleccionado
- Al menos 1 servicio
- Fecha válida
```

#### CU-010: Editar Sesión Abierta
```
Título: Editar Datos de Sesión mientras esté OPEN
Actor Primario: FISIOTERAPEUTA
Precondiciones: Sesión estado = OPEN
Postcondiciones: Cambios guardados

Flujo Principal:
1. FISIOTERAPEUTA accede a sesión OPEN
2. Abre formulario (misma estructura: 2 pasos)
3. PASO 1: Puede editar
   - Fecha
   - Motivo
   - Fisioterapeuta
   - Servicios (agregar/quitar)
4. PASO 2: Puede editar
   - Evaluación clínica
   - Tratamiento
   - Observaciones
   - Evolución
5. Guarda cambios
6. Sistema actualiza en BD
7. Auditoría registra quién, cuándo, qué cambió

Restricciones:
- Solo si status = OPEN
- Si status = CLOSED / CANCELLED: solo lectura (formularios deshabilitados)

Validaciones:
- Validar datos como en creación
- No borrar sesión (solo cambiar datos)
```

#### CU-011: Cambiar Estado de Sesión
```
Título: Cambiar Estado de Sesión (OPEN → CLOSED/CANCELLED)
Actor Primario: FISIOTERAPEUTA
Precondiciones: Sesión status = OPEN
Postcondiciones: Sesión status = CLOSED o CANCELLED

Flujo Principal:
1. FISIOTERAPEUTA accede a lista de sesiones
2. Fila de sesión OPEN con opción "Cambiar estado"
3. Click abre confirmación:
   - "¿Cerrar sesión?" → CLOSED
   - "¿Cancelar sesión?" → CANCELLED
4. Si selecciona "Cerrar":
   - Validaciones:
     * Si tiene Análisis de Pisada: ¿Reporte generado?
     * ¿Todos datos Paso 2 completos?
   - Si TODO OK: estado → CLOSED
   - Si falta reporte: Alerta → "Genere reporte primero"
5. Si selecciona "Cancelar":
   - Estado → CANCELLED
   - Sin validaciones adicionales
6. Muestra confirmación
7. Lista se actualiza

Tabla con colores por estado:
- OPEN: Azul (editable)
- CLOSED: Verde (completada)
- CANCELLED: Rojo (no aplicada)

Validaciones:
- Solo sesiones OPEN pueden cambiar
- Sesiones CLOSED/CANCELLED: botón deshabilitado
```

---

### 4.5 CASOS DE USO: ANÁLISIS DE IMAGEN

#### CU-012: Análisis de Pisada Completo (5 Sub-pasos)
```
Título: Realizar Análisis Biomecánico Completo de Pisada
Actor Primario: FISIOTERAPEUTA
Precondiciones: 
- Sesión OPEN con servicio "Análisis de Pisada" seleccionado
- Acceso a pantalla separada (/imaging)
Postcondiciones: Análisis guardado, opcionalmente con PDF generado

Flujo Principal:

==========================================
SUB-PASO 1: CAPTURA DE FOTOS
==========================================
1. FISIOTERAPEUTA en pantalla Imaging
2. Stepper muestra "Sub-paso 1 de 5"
3. Opciones:
   - Botón "Capturar con Cámara"
   - Botón "Subir Archivos"
4. Captura/Sube hasta 6 fotos
5. Preview en miniaturas
6. Puede eliminar/reemplazar
7. Click "Siguiente"

Validaciones:
- Mínimo 1 foto
- Máximo 6 fotos
- Formato válido (JPG, PNG)

==========================================
SUB-PASO 2: VISUALIZACIÓN EN CANVAS
==========================================
1. Stepper: "Sub-paso 2 de 5"
2. Miniatura de fotos en parte superior
3. Click en miniatura → amplía en canvas central
4. Canvas preparado para trazos (siguiente sub-paso)
5. Muestra foto actual (1/6)
6. Botón "Siguiente" para ir a sub-paso 3

==========================================
SUB-PASO 3: ANÁLISIS DE TRAZOS (CANVAS HTML5)
==========================================
1. Stepper: "Sub-paso 3 de 5"
2. Canvas interactivo con foto ampliada
3. Interfaz de dibujo:
   - Botón "Dibujar Trazo PIE IZQUIERDO"
   - Botón "Dibujar Trazo PIE DERECHO"
   - Botón "Marcar Ángulo"
   - Botón "Borrar Trazo"

4. Flujo de dibujo:
   a. Click "Dibujar Trazo PIE IZQUIERDO"
   b. Click en imagen: punto inicial
   c. Click en imagen: punto final → dibuja línea
   d. Sistema pregunta: "¿Dónde va el ángulo interno?"
   e. FISIOTERAPEUTA click: punto de ángulo
   f. Sistema calcula:
      - Ángulo interno (grados)
      - Ángulo externo (grados)
      - Dibuja triángulo referencia
   g. Valores se muestran en tiempo real

5. Repite para PIE DERECHO

6. Muestra resumen:
   - Pie Izquierdo: Ángulo interno, externo
   - Pie Derecho: Ángulo interno, externo
   - Botón "Guardar Trazos"

7. Sistema guarda annotations_json en BD

==========================================
SUB-PASO 4: ANÁLISIS BIOMECÁNICO + HUELLA PLANTAR
==========================================
1. Stepper: "Sub-paso 4 de 5"
2. Dos secciones:

   SECCIÓN A: ANÁLISIS BIOMECÁNICO (POR PIE)
   
   PIE IZQUIERDO:
   - Regla Maleolo Tibial * (dropdown)
     * NORMAL / VARO / VALGO / OTRO
   - Desgaste de Calzado (texto)
   - Palpación de la Tibia (texto)
   - Marcha * (dropdown)
     * NORMAL / PRONADOR / SUPINADOR / MIXTO
   
   PIE DERECHO: (misma estructura)
   
   SECCIÓN B: EVALUACIÓN DE HUELLA PLANTAR (⭐ GENÉRICA - UNA SOLA VEZ)
   
   Seleccione el tipo de huella que aplica al paciente:
   ◯ Índice Normal
   ◯ Índice Pie Plano
   ◯ Índice Pie Cavo
   ◯ Pie Plano
   ◯ Pie Plano-Normal
   ◯ Pie Normal
   ◯ Pie Normal-Cavo
   ◯ Pie Cavo
   ◯ Pie Cavo Fuerte
   ◯ Pie Cavo Extremo
   
   Notas Adicionales (texto, opcional)

3. Valida campos requeridos
4. Click "Siguiente"

==========================================
SUB-PASO 5: RESUMEN + GENERAR PDF
==========================================
1. Stepper: "Sub-paso 5 de 5 (RESUMEN)"
2. Formulario con campos resumen:
   - Antecedentes Relevantes * (textarea)
   - Evaluación Kinésica * (textarea)
   - Observaciones (textarea)
   - Diagnóstico * (radio)
     * NORMAL / PRONACIÓN / SUPINACIÓN
   - Distancia Intermaleolar (cm) (número)
   - Distancia Intercondílea (cm) (número)

3. Resumen visual:
   - Fotos seleccionadas: X/6
   - Biomecánico: LEFT ✓ RIGHT ✓
   - Huella Plantar: [Tipo seleccionado] ✓

4. Dos botones:
   a. "Generar Reporte PDF"
      - Llamada a backend
      - Crea PDF con:
        * Datos paciente + sesión
        * Fotos seleccionadas + trazos
        * Datos biomecánicos
        * Tipo de huella
        * Diagnóstico
      - Sube PDF a Cloudinary
      - Retorna URL
      - Guarda en BD: foot_analysis.report_url
      - Muestra: "PDF generado ✓"
   
   b. "Volver a Sesión"
      - Guarda análisis completo
      - has_foot_analysis = true
      - Redirige a Paso 1 del stepper original
      - Sesión continúa abierta para Paso 2

Validaciones:
- Campos obligatorios completos
- Mínimo 1 foto para reporte
- Diagnóstico seleccionado
- Antecedentes no vacío

Flujos Alternativos:
FA-1: Usuario cancela
- Opción "Volver a Sesión sin guardar"
- Análisis pendiente

FA-2: PDF ya generado
- Muestra: "Reporte ya existe"
- Opción: "¿Regenerar?"
- Sobrescribe report_url
```

---

## 5. Caso de Uso General del Sistema

### CU-GEN-001: Flujo Completo de Atención de Paciente (De Inicio a Fin)

```
Título: Flujo Completo de Atención Clínica desde Primer Contacto hasta Alta
Actor Primario: RECEPCIONISTA, FISIOTERAPEUTA
Precondiciones: Sistema funcionando, usuarios autenticados
Postcondiciones: Paciente con episodio cerrado, reportes generados

╔════════════════════════════════════════════════════════════════════════════════╗
║                    FLUJO GENERAL DEL SISTEMA KINEVID                          ║
╚════════════════════════════════════════════════════════════════════════════════╝

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 1: RECEPCIÓN INICIAL DEL PACIENTE                                       │
└──────────────────────────────────────────────────────────────────────────────┘

1. RECEPCIONISTA recibe paciente
2. Busca en sistema por nombre/cédula
3. Dos escenarios:

   ESCENARIO A: NUEVO PACIENTE
   ├─ Click "Registrar Nuevo Paciente"
   ├─ Completa formulario (4 secciones):
   │  ├─ Datos personales (nombre, cédula, edad, género)
   │  ├─ Datos médicos (grupo sanguíneo, alergias)
   │  ├─ Contacto emergencia
   │  └─ Observaciones
   ├─ Sistema crea:
   │  ├─ Paciente (ACTIVE)
   │  └─ Episodio 1 (automático, ACTIVE)
   └─ Abre perfil del paciente
   
   ESCENARIO B: PACIENTE EXISTENTE
   ├─ Si ACTIVE:
   │  └─ Abre perfil (continúa flujo)
   └─ Si DISCHARGE (fue de alta):
      ├─ Botón: "Reactivar Paciente"
      ├─ Click → crea Episodio N (nuevo)
      └─ Paciente ACTIVE

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 2: PREPARACIÓN DE SESIÓN                                                │
└──────────────────────────────────────────────────────────────────────────────┘

4. RECEPCIONISTA ve perfil con Episodio ACTIVE
5. Botón: "Nueva Sesión"
6. Redirige a formulario SESIÓN (MAT-STEPPER, 2 pasos)

   PASO 1: DATOS BÁSICOS + SERVICIOS
   ├─ Fecha (hoy)
   ├─ Motivo de consulta (ej: "Control post-traumático")
   ├─ Fisioterapeuta responsable
   ├─ MULTISELECT SERVICIOS:
   │  ├─ Servicio 1: "Masaje Terapéutico" → Cantidad 2 → Precio $50 c/u
   │  ├─ Servicio 2: "Terapia Física" → Cantidad 1 → Precio $80
   │  └─ ⭐ ESPECIAL: "Análisis de Pisada" (categoría POSTURAL_ANALYSIS)
   │     └─ Si se selecciona:
   │        └─ Botón AZUL: "Ir a Análisis de Imagen"
   │           (Este paso es OPCIONAL - solo si requiere análisis)
   │
   └─ Botón "Siguiente"

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 3: ANÁLISIS DE PISADA (OPCIONAL - SI SELECCIONÓ EL SERVICIO)           │
└──────────────────────────────────────────────────────────────────────────────┘

7. Si seleccionó "Análisis de Pisada":
   └─ Click botón azul → REDIRIGE A PANTALLA SEPARADA
      
      (/management-pacient/episodes/:episodeId/sessions/:sessionId/imaging)
      
      SUCESOR COMPLETO (5 SUB-PASOS):
      
      SUB-PASO 1: Captura 1-6 fotos (cámara o subida)
      ├─ Foto 1 ✓
      ├─ Foto 2 ✓
      ├─ Foto 3 ✓
      ├─ Foto 4 ✓
      ├─ Foto 5 ✓
      └─ Foto 6 ✓
      
      SUB-PASO 2: Visualización en canvas
      └─ Selecciona cada foto para análisis
      
      SUB-PASO 3: Trazos y Ángulos (Canvas HTML5)
      ├─ Dibuja línea vertical PIE IZQUIERDO
      ├─ Marca ángulo interno: 15.5°, externo: 22.3°
      ├─ Dibuja línea vertical PIE DERECHO
      ├─ Marca ángulo interno: 18.7°, externo: 25.1°
      └─ Guarda annotations_json
      
      SUB-PASO 4: Análisis Biomecánico + Huella Plantar
      ├─ Pie Izquierdo:
      │  ├─ Regla Maleolo Tibial: NORMAL
      │  ├─ Desgaste calzado: "Suela más desgastada en borde externo"
      │  ├─ Palpación tibia: "Normal, sin dolor"
      │  └─ Marcha: PRONADOR
      ├─ Pie Derecho:
      │  ├─ Regla Maleolo Tibial: VALGO
      │  ├─ Desgaste calzado: "Desgaste interno"
      │  ├─ Palpación tibia: "Tensión medial"
      │  └─ Marcha: PRONADOR
      └─ Huella Plantar (⭐ UNA SOLA CLASIFICACIÓN):
         ├─ Selecciona: "Pie Plano"
         └─ Notas: "Paciente sedentario, requiere plantillas"
      
      SUB-PASO 5: Resumen + Generar PDF
      ├─ Antecedentes: "Paciente con antecedente de fascitis plantar..."
      ├─ Evaluación: "Presente pronación bilateral..."
      ├─ Observaciones: "Paciente refiere dolor matinal..."
      ├─ Diagnóstico: PRONATION (radio)
      ├─ Distancia Intermaleolar: 12.5 cm
      ├─ Distancia Intercondílea: 18.3 cm
      ├─ Fotos para reporte: 3/6 seleccionadas ✓
      └─ BOTÓN: "Generar Reporte PDF"
         ├─ Backend genera PDF:
         │  ├─ Encabezado: Logo, Fecha, N° Reporte
         │  ├─ Datos paciente + profesional
         │  ├─ Episodio + Sesión
         │  ├─ Servicios aplicados
         │  ├─ Historia clínica de la sesión
         │  ├─ Fotos + trazos visualizados
         │  ├─ Datos biomecánicos
         │  ├─ Tipo de huella plantar
         │  ├─ Diagnóstico
         │  └─ Pie de página para firma
         ├─ Sube a Cloudinary
         ├─ Retorna URL
         └─ Guarda en BD: foot_analysis.report_url
      
      BOTÓN: "Volver a Sesión"
      └─ Retorna a PASO 1 del stepper
         └─ Sesión actualizada: has_foot_analysis = TRUE

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 4: EVALUACIÓN CLÍNICA                                                   │
└──────────────────────────────────────────────────────────────────────────────┘

8. FISIOTERAPEUTA completa PASO 2 del stepper:
   
   PASO 2: EVALUACIÓN CLÍNICA + CIERRE
   ├─ Antecedentes Relevantes: "..."
   ├─ Evaluación Kinesiológica: "..."
   ├─ Tratamiento Aplicado: "..."
   ├─ Observaciones: "..."
   ├─ Evolución: "..."
   └─ BOTÓN: "Cerrar Sesión"

9. Validaciones antes de cerrar:
   ├─ Si tiene "Análisis de Pisada":
   │  ├─ ¿PDF generado? (debe existir report_url)
   │  └─ Si NO: Alerta → "Genere PDF en Análisis"
   ├─ Si NO tiene análisis:
   │  └─ Cierra directamente
   └─ Sesión estado → CLOSED

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 5: CONTINUIDAD DE TRATAMIENTO (SESIONES POSTERIORES)                    │
└──────────────────────────────────────────────────────────────────────────────┘

10. En días posteriores, RECEPCIONISTA continúa ciclo:
    ├─ Busca paciente (ya existe, ACTIVE)
    ├─ Abre perfil
    ├─ Ve Episodio ACTIVE con sesiones anteriores (CLOSED)
    ├─ Botón: "Nueva Sesión"
    ├─ Repite Pasos 1-2 (análisis opcional en cada sesión)
    ├─ Sesión 2 creada
    ├─ Sesión 3 creada
    └─ ... tantas como requiera el tratamiento

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 6: ALTA MÉDICA / CIERRE DE EPISODIO                                     │
└──────────────────────────────────────────────────────────────────────────────┘

11. Cuando tratamiento finaliza:
    ├─ FISIOTERAPEUTA accede a perfil del paciente
    ├─ Ve Episodio ACTIVE con todas las sesiones (CLOSED)
    ├─ Botón: "Cerrar Episodio (Alta Médica)"
    ├─ Modal de confirmación:
    │  └─ Ingresa motivo de alta: "Paciente sin dolor, recuperación completa"
    ├─ Click "Confirmar"
    ├─ Sistema:
    │  ├─ Episodio estado → CLOSED
    │  ├─ Paciente estado → DISCHARGE (automático)
    │  └─ Muestra confirmación
    └─ Episodio ahora aparece en historial (colapsado, solo lectura)

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 7: HISTORIAL Y REPORTES                                                 │
└──────────────────────────────────────────────────────────────────────────────┘

12. Historial completo disponible:
    ├─ FISIOTERAPEUTA accede a perfil del paciente
    ├─ Ve episodios cerrados en acordeón
    ├─ Expande Episodio 1 (Mar-Jun 2024)
    ├─ Ve todas las sesiones históricas (CLOSED)
    ├─ Cada sesión con análisis:
    │  ├─ Datos clínicos
    │  ├─ Servicios aplicados
    │  ├─ Si tiene análisis de pisada:
    │  │  └─ Botón: "Descargar Reporte PDF"
    │  │     └─ URL desde Cloudinary
    │  └─ Fecha de generación
    └─ Historial completo para referencia futura

┌──────────────────────────────────────────────────────────────────────────────┐
│ FASE 8: REACTIVACIÓN (SI PACIENTE REGRESA)                                   │
└──────────────────────────────────────────────────────────────────────────────┘

13. Meses después, paciente regresa:
    ├─ RECEPCIONISTA busca paciente
    ├─ Sistema muestra: DISCHARGE
    ├─ Botón destacado: "Reactivar Paciente"
    ├─ Click → confirmación
    ├─ Sistema:
    │  ├─ Paciente estado → ACTIVE
    │  ├─ Crea Episodio 2 (automático)
    │  │  └─ Motivo: "Reapertura de caso"
    │  └─ Episodio 2 estado → ACTIVE
    ├─ Abre perfil actualizado
    ├─ FISIOTERAPEUTA comienza nueva sesión
    ├─ Ciclo repite (Fases 2-7)
    └─ Episodio 1 sigue disponible en historial

╔════════════════════════════════════════════════════════════════════════════════╗
║                            ACTORES Y PERMISOS                                 ║
╚════════════════════════════════════════════════════════════════════════════════╝

┌──────────────────────────┬─────────────────────────────────────────────────┐
│ ROL                      │ ACCIONES EN FLUJO GENERAL                       │
├──────────────────────────┼─────────────────────────────────────────────────┤
│ RECEPCIONISTA            │ • Registrar paciente                            │
│                          │ • Buscar paciente                               │
│                          │ • Ver lista de servicios                        │
│                          │ • Crear sesión (solo Paso 1)                    │
│                          │                                                 │
│ FISIOTERAPEUTA           │ • Ver perfil de paciente                        │
│                          │ • Crear/editar sesión completa (Paso 1 + 2)    │
│                          │ • Acceder a análisis de pisada (5 sub-pasos)    │
│                          │ • Cerrar episodio (alta médica)                 │
│                          │ • Generar/descargar reportes PDF                │
│                          │ • Ver historial                                 │
│                          │                                                 │
│ ADMIN                    │ • Todo lo anterior +                            │
│                          │ • Crear/editar servicios médicos                │
│                          │ • Registrar empleados                           │
│                          │ • Asignar roles a usuarios                      │
│                          │                                                 │
│ ROOT                     │ • Control total del sistema                     │
│                          │ • Gestión de permisos y roles                   │
└──────────────────────────┴─────────────────────────────────────────────────┘

╔════════════════════════════════════════════════════════════════════════════════╗
║                        DECISIONES CLAVE DEL SISTEMA                           ║
╚════════════════════════════════════════════════════════════════════════════════╝

1. ✅ Episodios agrupan sesiones de una etapa de tratamiento
   └─ Permite diferenciar cuando paciente regresa (Episodio 1, 2, 3...)

2. ✅ Sesiones OPEN mientras se atiende, CLOSED al finalizar
   └─ Solo se pueden editar sesiones OPEN

3. ✅ Análisis de Pisada es OPCIONAL
   └─ Flujo funciona sin análisis
   └─ Si se selecciona: pantalla separada con 5 sub-pasos

4. ✅ Reportes PDF generados bajo demanda
   └─ No se guardan archivos en BD
   └─ Solo URL en BD (Cloudinary)
   └─ Se pueden descargar múltiples veces

5. ✅ Evaluación de Huella Plantar GENÉRICA
   └─ Una sola clasificación por análisis (no por pie)
   └─ Más simple y coherente

6. ✅ RECEPCIONISTA crea sesión (Paso 1)
   └─ FISIOTERAPEUTA completa (Paso 2)
   └─ Flujo colaborativo

7. ✅ Alta Médica = Cierre Episodio + Paciente DISCHARGE
   └─ Paciente puede reactivarse después
   └─ Se crea automáticamente nuevo episodio

Validaciones de Negocio:
- Solo 1 episodio ACTIVE por paciente a la vez
- Solo 1 usuario por empleado
- Sesiones numeradas dentro del episodio (no global)
- Auditoría en todas las acciones (quién, cuándo, qué)
- Eliminación siempre lógica (no física)
```

---

## Resumen Ejecutivo

### Módulos Completados
✅ Autenticación y Usuarios (5 historias)
✅ Gestión de Empleados (3 historias)
✅ Gestión de Pacientes (5 historias)
✅ Gestión de Servicios (2 historias)
✅ Episodios Clínicos (3 historias)
✅ Sesiones Clínicas (5 historias)
✅ Análisis de Imagen (5 historias)
✅ Reportes PDF (2 historias)

### Total de Historias de Usuario: 30+
### Total de Casos de Uso: 12+ (modulares)
### Total de Items en Product Backlog: 48 items

### Próximas Fases
🔵 Fase 6: Backend Análisis de Pisada (PRÓXIMA)
🔵 Fase 6B: Frontend Módulo Imaging (PRÓXIMA)
🔵 Fase 7-8: Generación y Descarga de Reportes (PENDIENTE)

---

*Documento preparado: 21 de Mayo, 2026*  
*Versión: 1.0*  
*Estado: Documento Profesional Completo*

