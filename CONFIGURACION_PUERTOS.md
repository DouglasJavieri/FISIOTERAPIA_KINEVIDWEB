# 🔧 Configuración de Puertos - Fisioterapia Kinevid
## ✅ Cambios Realizados
Para evitar conflictos de puertos, se han actualizado las siguientes configuraciones:
### Backend (Spring Boot)
- **Puerto anterior**: 8080
- **Puerto nuevo**: **8090**
- **URL del API**: http://localhost:8090/api
### Frontend (Angular)
- **Puerto anterior**: 4200
- **Puerto nuevo**: **4300**
- **URL de la aplicación**: http://localhost:4300
---
## 📝 Archivos Modificados
### 1. Backend - application.properties
```properties
server.port=8090
kinevid.app.cors.allowed-origins=http://localhost:4300
```
### 2. Frontend - environment.ts
```typescript
apiUrl: 'http://localhost:8090/api'
```
### 3. Frontend - angular.json
```json
"options": {
  "port": 4300
}
```
---
## 🚀 Cómo Levantar el Proyecto
### Backend:
```bash
cd /f/fisioterapia-kinevid/FISIOTERAPIA_KINEVIDWEB/back-office-api
./mvnw.cmd spring-boot:run
```
### Frontend:
```bash
cd /f/fisioterapia-kinevid/FISIOTERAPIA_KINEVIDWEB/back-office-web/src/main/web
npm install
npm start
```
---
## 📋 URLs del Proyecto
| Servicio | URL |
|----------|-----|
| Frontend | http://localhost:4300 |
| Backend API | http://localhost:8090/api |
| Swagger UI | http://localhost:8090/swagger-ui.html |
| PostgreSQL | localhost:5432 |
**Fecha**: 2026-04-20
