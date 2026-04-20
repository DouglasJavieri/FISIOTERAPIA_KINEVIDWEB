# Guia de instalacion y clonacion en otro dispositivo

Este documento describe como clonar y ejecutar el proyecto `fisioterapia-Kinevid` en otro equipo.

## 1) Requisitos previos

Instala lo siguiente en el nuevo dispositivo:

- Git
- JDK 17 (el proyecto usa `java.version=17`)
- Node.js 16 LTS (recomendado para Angular 13)
- npm (incluido con Node.js)
- PostgreSQL (recomendado 13+)

Tambien verifica que estos puertos esten libres:

- `5432` (PostgreSQL)
- `8080` (Backend Spring Boot)
- `4200` (Frontend Angular)

## 2) Clonar repositorio

```powershell
git clone <URL_DEL_REPOSITORIO>
Set-Location fisioterapia-Kinevid\source
```

> Reemplaza `<URL_DEL_REPOSITORIO>` por la URL real (HTTPS o SSH).

## 3) Configurar base de datos (PostgreSQL)

El backend esta configurado para conectarse a:

- DB: `fisioterapia_kinevid`
- Host: `localhost`
- Puerto: `5432`
- Usuario: `postgres`

Crea la base de datos:

```powershell
psql -U postgres -h localhost -p 5432 -c "CREATE DATABASE fisioterapia_kinevid;"
```

Si usas credenciales distintas, actualiza `back-office-api/src/main/resources/application.properties`:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

## 4) Levantar Backend (Spring Boot)

```powershell
Set-Location .\back-office-api
.\mvnw.cmd spring-boot:run
```

Notas:

- El wrapper de Maven descarga Maven automaticamente (3.9.14).
- La API arranca por defecto en `http://localhost:8080`.

## 5) Levantar Frontend (Angular)

En otra terminal:

```powershell
Set-Location F:\fisioterapia-Kinevid\source\back-office-web\src\main\web
npm install
npm start
```

Notas:

- El frontend arranca por defecto en `http://localhost:4200`.
- La URL de API en desarrollo esta en `back-office-web/src/main/web/src/environments/environment.ts`:
  - `apiUrl: 'http://localhost:8080/api'`

## 6) Verificacion rapida

1. Abre `http://localhost:4200`.
2. Verifica que el frontend pueda consumir la API en `http://localhost:8080/api`.
3. Si no hay usuarios cargados, revisa los valores admin en `application.properties`:
   - `kinevid.app.admin.username`
   - `kinevid.app.admin.password`

## 7) Problemas comunes

- **Error de conexion a DB**: revisa que PostgreSQL este iniciado y que usuario/password sean correctos.
- **Puerto ocupado**: cambia `server.port` (backend) o ejecuta Angular en otro puerto.
- **CORS**: revisa `kinevid.app.cors.allowed-origins` en `application.properties`.
- **Version de Node incompatible**: usa Node 16 LTS para Angular 13.
- **Cache de npm**: si hay conflictos, elimina `node_modules` y reinstala.

## 8) Seguridad recomendada

Antes de usar en otro entorno:

- Cambia passwords por defecto.
- No subas secretos reales al repositorio.
- Usa variables de entorno para credenciales sensibles.

## 9) Sobre la garantia de funcionamiento

No es posible garantizar al 100% que funcionara en cualquier dispositivo, porque depende del entorno (sistema operativo, permisos, versiones, red y configuracion local).

Lo que si puedes garantizar operativamente es esto:

- Si el nuevo equipo cumple los requisitos de este documento.
- Si se respetan versiones compatibles (JDK 17, Node 16, PostgreSQL).
- Si la base de datos y variables de configuracion estan correctas.

Entonces la probabilidad de que funcione correctamente es muy alta.

