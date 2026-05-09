# FitCampus — Plataforma de Actividad Física Universitaria

Aplicación web para la gestión de rutinas de entrenamiento, seguimiento de progreso y coordinación entre usuarios y entrenadores en la Universidad Icesi.

## Integrantes

| Nombre                | Código    |
|-----------------------|-----------|
| Juan Pablo Serrano    | A00404067 |
| Felipe Calderón       | A00404998 |
| Samuel Navia          | A00405006 |
| Maria Cristina Angulo | A00404027 |
| Samuel Jose Rengifo   | A00404150 |


---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Spring Boot 4.0.0 · Java 17 · WAR |
| Persistencia relacional | PostgreSQL (Supabase) vía Spring Data JPA |
| Persistencia documental | MongoDB Atlas vía Spring Data MongoDB |
| Vistas | Thymeleaf + Spring Security Dialect |
| Seguridad | Spring Security · BCrypt · `@PreAuthorize` |
| PDF | OpenPDF 1.3.30 |

---

## Módulos implementados

| # | Requisito | Módulo / URL |
|---|-----------|-------------|
| 1 | Login con cuenta institucional | `/login` · `/user/signup` |
| 2 | Crear y editar rutinas con ejercicios predefinidos | `/routine/*` |
| 3 | Base de datos de ejercicios con videos demostrativos | `/exercise/*` |
| 4 | Registro de progreso diario/semanal | `/progress/*` |
| 5 | Entrenador visualiza progreso de sus estudiantes | `/user/my-students` · `/progress/student/{id}` |
| 6 | Entrenador genera recomendaciones | `/recommendation/*` |
| 7 | Entrenador sube rutinas prediseñadas adoptables | `/routine/list` → botón **Adoptar** |
| 8 | Sección de eventos y espacios disponibles | `/event/*` |
| 9 | Panel administrativo (entrenadores, ejercicios, eventos) | `/user/list` · `/exercise/*` · `/event/*` |
| 10 | Historial de actividades y métricas | `/progress/list` |
| 11 | Estadísticas con gráficos de progreso | `/progress/stats` |
| 12 | Control de acceso por rol | Permisos: `VIEW_USERS`, `MANAGE_USERS`, `MANAGE_ROLES`, `MANAGE_PERMISSIONS`, `ASSIGN_TRAINER` |
| 13 | Interfaz responsiva | Viewport meta en todas las vistas · CSS adaptativo |
| 14 | Descarga de reporte de progreso en PDF | `/progress/export/pdf` |

---

## Ejecución local

### Requisitos

- Java 17
- Maven 3.8+
- Acceso a red **ZeroTier `93afae59635dc904`** (solo si se usa la BD de producción) **o** credenciales propias en `.env`

### Configurar variables de entorno

Copia `.env.example` a `.env` y rellena con tus credenciales:

```bash
cp .env.example .env
```

```env
DB_URL=jdbc:postgresql://<host>:<port>/<database>?sslmode=require&prepareThreshold=0
DB_USERNAME=<usuario_supabase>
DB_PASSWORD=<contraseña_supabase>

MONGODB_URI=mongodb+srv://<user>:<password>@<cluster>.mongodb.net/<database>?appName=<appName>
MONGODB_DATABASE=exercise_app
```

### Iniciar en modo desarrollo

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/exercise`

### Compilar WAR para despliegue

```bash
mvn clean package
```

El artefacto se genera en `target/exercise-0.0.1-SNAPSHOT.war`.

---

## Acceso al servidor desplegado

La aplicación se encuentra desplegada en Apache Tomcat (requiere ZeroTier **`93afae59635dc904`**):

```
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT
```

Para reiniciar Tomcat en el servidor remoto:

```bash
cd ~/Documents/apache-tomcat-10.1.54/bin
./startup.sh
```

---

## Cuentas de prueba

La aplicación se auto-inicializa con datos de prueba al primer arranque (`DataInitializer`).

### Cuenta administrador (creada automáticamente)

| Campo | Valor |
|-------|-------|
| Email | `admin@fitcampus.co` |
| Contraseña | `admin123` |
| Rol | ADMIN |

### Cuentas de la base de datos de producción

| Nombre | Email | Contraseña | Rol |
|--------|-------|-----------|-----|
| Hernando Ospina Martínez | hernando.ospina@fitcampus.co | admin123 | Admin |
| Leidy Cardona Restrepo | leidy.cardona@fitcampus.co | trainer1 | Entrenador |
| Mauricio Giraldo Patiño | mauricio.giraldo@fitcampus.co | trainer2 | Entrenador |
| Tatiana Salazar Ríos | tatiana.salazar@fitcampus.co | trainer3 | Entrenador |
| Camilo Vargas Peñaloza | camilo.vargas@gmail.com | user1 | Usuario |
| Valentina Torres Cárdenas | valentina.torres@gmail.com | user2 | Usuario |
| Sebastián Morales Agudelo | sebastian.morales@gmail.com | user3 | Usuario |
| Daniela Ramírez Herrera | daniela.ramirez@gmail.com | user4 | Usuario |
| Andrés López Bermúdez | andres.lopez@gmail.com | user5 | Usuario |
| Juliana Castro Quintero | juliana.castro@gmail.com | user6 | Usuario |

---

## Guía de pruebas por funcionalidad

### 1. Login / Registro
1. Navegar a `/exercise/login`
2. Iniciar sesión con `admin@fitcampus.co` / `admin123`
3. Para crear un nuevo usuario: clic en **Sign up** desde la pantalla de login

### 2. Gestión de ejercicios (Admin)
1. Iniciar sesión como **Admin**
2. Dashboard → **Ejercicios** → **+ Nuevo ejercicio**
3. Completar nombre, tipo (`CARDIO` / `FUERZA` / `MOVILIDAD`), dificultad, duración y descripción
4. Guardar y luego entrar al ejercicio creado → **Soportes visuales** → agregar URL de video

### 3. Rutinas — crear y agregar ejercicios
1. Dashboard → **Rutinas** → **+ Nueva**
2. Completar nombre, tipo, dificultad y marcar visibilidad (pública/privada)
3. En la lista de rutinas → **Ejercicios** → agregar ejercicios del catálogo

### 4. Adoptar rutina de un entrenador
1. Iniciar sesión como **Usuario** (ej. `camilo.vargas@gmail.com` / `user1`)
2. Dashboard → **Rutinas** → pestaña **Públicas**
3. En cualquier rutina pública → clic en **Adoptar**
4. La rutina aparecerá en **Mis rutinas** (copia privada)

### 5. Registro de progreso
1. Dashboard → **Mi progreso** → **+ Nuevo registro**
2. Seleccionar una rutina propia, ingresar ejercicio, fecha, series, repeticiones y peso
3. El registro queda visible en el historial

### 6. Estadísticas y exportación PDF
1. Dashboard → **Estadísticas** (o desde Mi progreso → **Estadísticas**)
2. Se visualizan gráficos de repeticiones y peso por sesión (Chart.js)
3. Clic en **Descargar PDF** genera `reporte-progreso.pdf` con toda la tabla de registros

### 7. Flujo entrenador → estudiante

**Asignar entrenador a un usuario (Admin):**
1. Iniciar sesión como **Admin**
2. Usuarios → seleccionar usuario → **Asignar entrenador**
3. Elegir entrenador del desplegable (usuarios con rol TRAINER)

**Ver progreso de estudiantes (Entrenador):**
1. Iniciar sesión como **Entrenador** (ej. `leidy.cardona@fitcampus.co` / `trainer1`)
2. Topbar → **Mis estudiantes** → lista de usuarios asignados
3. Clic en **Ver progreso** para revisar los registros de ese estudiante

**Enviar recomendación (Entrenador):**
1. Desde **Mis estudiantes** → clic en **Recomendar** junto al estudiante
2. Escribir el mensaje → **Enviar recomendación**

**Ver recomendaciones recibidas (Usuario):**
1. Iniciar sesión como el usuario que recibió la recomendación
2. Topbar → **Recomendaciones**

### 8. Eventos
1. Dashboard → **Eventos** → **+ Nuevo evento** (Admin)
2. Completar nombre, fecha, descripción, espacio físico (nombre, ubicación, capacidad)
3. Cualquier usuario puede **Inscribirse** desde la lista de eventos
4. El Admin puede marcar **Asistencia** de los inscritos

### 9. Panel administrativo de roles y permisos
1. Iniciar sesión como **Admin**
2. Dashboard → **Roles** → crear/editar roles y asignar permisos
3. Dashboard → **Permisos** → crear/editar permisos disponibles
4. Dashboard → **Usuarios** → gestionar roles de cada usuario

---

## Arquitectura de bases de datos

Ver [PolyglotDatabases.md](PolyglotDatabases.md) para una explicación detallada de la estrategia de persistencia políglota PostgreSQL + MongoDB.

---

## Estructura del proyecto

```
src/main/java/co/icesi/exercise/
├── config/          SecurityConfig, MongoConfig, DataInitializer
├── controller/      8 controladores MVC (User, Exercise, Routine, Event,
│                    Progress, Recommendation, Role, Permission)
├── dto/             AppUserDTO, RoleDTO, PermissionDTO
├── model/           Entidades JPA (AppUser, Role, Permission, Recommendation)
│   └── nosql/       Documentos MongoDB (Exercise, Routine, ProgressRecord,
│                    Event + subdocumentos embebidos)
├── repositories/    Repositorios JPA
│   └── nosql/       Repositorios MongoDB
└── services/        Lógica de negocio (9 servicios)

src/main/resources/
├── application.properties
└── templates/       Vistas Thymeleaf organizadas por módulo
```
