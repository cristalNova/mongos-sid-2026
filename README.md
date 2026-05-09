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

## Estado actual de la aplicación

La aplicación está **completamente funcional** con todos los 14 requerimientos implementados y verificados. A continuación se describe el estado de cada capa:

### Persistencia políglota
- **PostgreSQL (Supabase):** tablas `APP_USER`, `ROLE`, `PERMISSION`, `USER_ROLE`, `ROLE_PERMISSION`, `USER_TRAINER` y `RECOMMENDATION`. Hibernate crea y actualiza el esquema automáticamente (`ddl-auto=update`).
- **MongoDB (Atlas):** colecciones `exercises`, `routines`, `progress_records` y `events` con documentos embebidos.
- **Carga de `.env`:** `DotEnvPostProcessor` (registrado en `META-INF/spring.factories`) inyecta las variables del archivo `.env` en el `Environment` de Spring **antes** de que HikariCP y MongoClient se inicialicen, resolviendo el error `claims to not accept jdbcUrl, ${DB_URL}`.

### Inicialización automática de datos
`DataInitializer` siembra automáticamente ambas bases al primer arranque (o si detecta datos incompletos):

**PostgreSQL:** 1 admin · 3 entrenadores · 8 usuarios · asignaciones trainer→estudiante · 7 recomendaciones
**MongoDB:** 15 ejercicios · 6 rutinas (4 públicas de trainers + 2 privadas de usuarios) · 18 registros de progreso distribuidos en 6 semanas · 4 eventos con suscripciones precargadas

### Correcciones técnicas aplicadas
| Problema | Solución |
|----------|---------|
| `${DB_URL}` no se resolvía en Spring Boot 4.x | `DotEnvPostProcessor` + `META-INF/spring.factories` |
| `LazyInitializationException` en lista de usuarios | `@EntityGraph(roles)` en `AppUserRepository.findAll()` |
| `MultipleBagFetchException` al cargar roles + trainers | Métodos separados: `findAll()` carga solo `roles`; `findWithTrainersById()` carga solo `trainers` |

---

## Ejecución local

### Requisitos

- Java 17
- Maven 3.8+

### 1. Clonar el repositorio

```bash
git clone https://github.com/cristalNova/mongos-sid-2026.git
cd mongos-sid-2026
```

### 2. Configurar variables de entorno

Copia `.env.example` a `.env` y rellena con las credenciales del proyecto:

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

> El archivo `.env` es cargado automáticamente por `DotEnvPostProcessor`. **No es necesario exportar las variables manualmente** antes de ejecutar.

### 3. Ejecutar

```bash
mvn spring-boot:run
```

La aplicación arranca en: **`http://localhost:8080/exercise`**

La primera ejecución contra una base vacía tarda unos segundos más porque `DataInitializer` siembra todos los datos de prueba.

### 4. Compilar WAR para despliegue en Tomcat

```bash
mvn clean package
```

Artefacto generado: `target/exercise-0.0.1-SNAPSHOT.war`

---

## Acceso al servidor desplegado

La aplicación está desplegada en Apache Tomcat (requiere ZeroTier **`93afae59635dc904`**):

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

Creadas automáticamente por `DataInitializer` al primer arranque:

### Administrador

| Email | Contraseña | Rol | Permisos |
|-------|-----------|-----|---------|
| `admin@fitcampus.co` | `admin123` | ADMIN | VIEW_USERS · MANAGE_USERS · MANAGE_ROLES · MANAGE_PERMISSIONS · ASSIGN_TRAINER |

### Entrenadores

| Nombre | Email | Contraseña | Estudiantes asignados |
|--------|-------|-----------|----------------------|
| Leidy Cardona Restrepo | `leidy.cardona@fitcampus.co` | `trainer1` | Camilo, Valentina, Sebastián |
| Mauricio Giraldo Patiño | `mauricio.giraldo@fitcampus.co` | `trainer2` | Daniela, Andrés, Juliana |
| Tatiana Salazar Ríos | `tatiana.salazar@fitcampus.co` | `trainer3` | Felipe, Mariana |

### Usuarios

| Nombre | Email | Contraseña | Progreso pre-cargado |
|--------|-------|-----------|---------------------|
| Camilo Vargas Peñaloza | `camilo.vargas@gmail.com` | `user1` | 6 registros (6 semanas) |
| Valentina Torres Cárdenas | `valentina.torres@gmail.com` | `user2` | 4 registros |
| Sebastián Morales Agudelo | `sebastian.morales@gmail.com` | `user3` | 4 registros |
| Daniela Ramírez Herrera | `daniela.ramirez@gmail.com` | `user4` | 2 registros |
| Andrés López Bermúdez | `andres.lopez@gmail.com` | `user5` | 2 registros |
| Juliana Castro Quintero | `juliana.castro@gmail.com` | `user6` | — |
| Felipe Gómez Acosta | `felipe.gomez@gmail.com` | `user7` | — |
| Mariana Díaz Londoño | `mariana.diaz@gmail.com` | `user8` | — |

---

## Guía de pruebas — los 14 requerimientos

> **URL base local:** `http://localhost:8080/exercise`
> Todos los pasos suponen partir desde la pantalla de **login** (`/login`).

---

### REQ 1 — Login con cuenta institucional

**Usuario:** cualquiera de la tabla anterior

1. Ir a `http://localhost:8080/exercise/login`
2. Ingresar email y contraseña → clic en **Log in**
3. El sistema redirige al **dashboard** según el rol del usuario
4. Para registrar una cuenta nueva: clic en **Sign up** en la pantalla de login, completar el formulario (nombre, email, contraseña, edad, peso, altura) → la cuenta se crea con rol **USER**

---

### REQ 2 — Crear y editar rutinas eligiendo ejercicios predefinidos

**Usuario recomendado:** `camilo.vargas@gmail.com` / `user1`

1. Dashboard → **Rutinas** → **+ Nueva**
2. Completar nombre, tipo (cardio / fuerza / movilidad / mixto), dificultad y marcar como **Privada**
3. Guardar → aparece en la lista de **Mis rutinas**
4. Clic en **Ejercicios** en la rutina recién creada → aparece el catálogo completo de 15 ejercicios
5. Seleccionar un ejercicio del desplegable → **Agregar** → el ejercicio queda embebido en la rutina
6. Para editar la rutina: clic en **Editar** → cambiar nombre o visibilidad → **Actualizar**

---

### REQ 3 — Base de datos de ejercicios con videos demostrativos

**Usuario recomendado:** cualquiera (Admin para crear/editar)

1. Dashboard → **Ejercicios**
2. Se listan los 15 ejercicios pre-cargados con tipo, dificultad y duración
3. Como **Admin**, clic en **Editar** en cualquier ejercicio → ver/modificar descripción, tipo y dificultad
4. Clic en **Soportes visuales** → aparecen los videos asociados; con **Admin** se puede agregar una URL nueva (tipo VIDEO) y verificar que aparece en la lista

---

### REQ 4 — Registrar progreso diario o semanal

**Usuario recomendado:** `camilo.vargas@gmail.com` / `user1`

1. Dashboard → **Mi progreso** → **+ Nuevo registro**
2. Seleccionar la rutina **"Mi rutina de piernas"** del desplegable
3. Completar: ejercicio (`Sentadilla`), fecha (hoy), hora, series (4), repeticiones (12), peso (65 kg), notas
4. **Guardar registro** → el nuevo registro aparece al tope del historial
5. Se puede **Editar** o **Eliminar** cualquier registro desde la misma lista

---

### REQ 5 — Entrenador visualiza rutinas y progreso de sus estudiantes

**Usuario:** `leidy.cardona@fitcampus.co` / `trainer1`

1. Iniciar sesión como **Leidy Cardona**
2. Topbar → **Mis estudiantes** → aparecen Camilo, Valentina y Sebastián con sus datos físicos
3. Clic en **Ver progreso** junto a **Camilo Vargas** → se despliegan sus 6 registros históricos con ejercicio, fecha, series, reps y peso
4. Repetir con los otros estudiantes (Valentina tiene 4 registros, Sebastián tiene 4)

---

### REQ 6 — Entrenadores generan recomendaciones

**Enviar (Entrenador):** `leidy.cardona@fitcampus.co` / `trainer1`

1. Topbar → **Mis estudiantes** → clic en **Recomendar** junto a cualquier estudiante
2. El formulario abre con el destinatario pre-seleccionado
3. Escribir el mensaje → **Enviar recomendación**
4. Topbar → **Recomendaciones** → pestaña **Ver enviadas** → aparece el mensaje recién creado

**Ver recibidas (Usuario):** `camilo.vargas@gmail.com` / `user1`

1. Topbar → **Recomendaciones** → aparece la recomendación enviada por Leidy
2. El mensaje pre-cargado dice: *"Camilo, aumenta el peso en sentadilla gradualmente..."*

> También se puede enviar una recomendación desde el menú **Recomendaciones → + Nueva** seleccionando el destinatario manualmente.

---

### REQ 7 — Entrenadores suben rutinas prediseñadas; usuarios las adoptan

**Publicar rutina (Entrenador):** `leidy.cardona@fitcampus.co` / `trainer1`

1. Topbar → **Rutinas** → **+ Nueva**
2. Completar nombre, tipo, dificultad y marcar **Visibilidad: Pública** (checkbox activado)
3. Guardar → agregar ejercicios desde **Ejercicios** → la rutina queda disponible para todos

**Adoptar rutina (Usuario):** `valentina.torres@gmail.com` / `user2`

1. Topbar → **Rutinas** → pestaña **Públicas**
2. Se ven las 4 rutinas públicas creadas por los entrenadores (`Full Body Principiante`, `Cardio Intensivo`, `Fuerza Superior`, `Movilidad y Recuperación`)
3. Clic en **Adoptar** en cualquier rutina → confirmar el diálogo
4. Ir a **Mis rutinas** → aparece la copia privada con sufijo `(adoptada)` y todos los ejercicios incluidos

---

### REQ 8 — Eventos y espacios disponibles

**Usuario recomendado:** cualquiera (Admin para crear)

1. Dashboard → **Eventos**
2. Se listan 4 eventos pre-cargados con fecha, espacio físico y número de inscritos
3. Clic en **Inscribirse** → el usuario queda registrado como suscriptor del evento
4. Clic en **Desinscribirse** para revertir la inscripción

**Crear evento (Admin):**
1. Dashboard → **Eventos** → **+ Nuevo evento**
2. Completar nombre, descripción, fecha, nombre del espacio, ubicación y capacidad
3. Guardar → el evento aparece en la lista pública

**Marcar asistencia (Admin):**
1. En la lista de eventos, clic en **Asistencia** junto a un evento con inscritos → marcar presencia

---

### REQ 9 — Panel administrativo

**Usuario:** `admin@fitcampus.co` / `admin123`

**Gestión de usuarios:**
1. Dashboard → **Usuarios** → lista de los 12 usuarios con su rol (ADMIN · TRAINER · USER)
2. Clic en **Roles** junto a un usuario → asignar o quitar roles
3. Clic en **Assign Trainer** junto a un usuario con rol USER → seleccionar entrenador del **desplegable** (solo aparecen usuarios con rol TRAINER) → **Asignar**
4. Clic en **Edit** → modificar datos personales o contraseña

**Gestión de ejercicios y eventos:**
- Dashboard → **Ejercicios** → crear, editar, eliminar ejercicios y administrar sus soportes visuales
- Dashboard → **Eventos** → crear, editar, eliminar eventos; marcar asistencia

**Gestión de roles y permisos:**
- Dashboard → **Roles** → crear rol, asignar/quitar permisos con modal
- Dashboard → **Permisos** → crear, editar, eliminar permisos atómicos

---

### REQ 10 — Historial de actividades y métricas

**Usuario recomendado:** `camilo.vargas@gmail.com` / `user1`

1. Dashboard → **Mi progreso**
2. Se listan los 6 registros históricos ordenados, cada uno con ejercicio, rutina, fecha, series × reps y peso
3. Los registros cubren las últimas 6 semanas para mostrar evolución real
4. Clic en **Editar** para corregir un registro; clic en **Eliminar** para borrarlo
5. Los registros de otros usuarios (Valentina, Sebastián, etc.) solo son visibles para el propio usuario o para su entrenador asignado

---

### REQ 11 — Estadísticas de rendimiento con gráficos

**Usuario recomendado:** `camilo.vargas@gmail.com` / `user1`

1. Dashboard → **Estadísticas** (o desde **Mi progreso** → botón **Estadísticas**)
2. Se muestran tres cajas de resumen: registros totales, repeticiones totales, series totales
3. **Gráfico de líneas** — repeticiones por sesión ordenadas cronológicamente
4. **Gráfico de barras** — peso levantado (kg) por sesión
5. Los gráficos se ven poblados con los 6 registros pre-cargados de Camilo (Valentina y Sebastián también tienen suficientes registros para ver tendencia)

> Los usuarios sin registros (Juliana, Felipe, Mariana) verán el mensaje *"No tienes registros aún"*.

---

### REQ 12 — Seguridad y control de acceso por rol

La seguridad se aplica a nivel de método con `@PreAuthorize`. Casos verificables:

| Acción | Rol requerido | Qué ocurre sin permiso |
|--------|--------------|------------------------|
| Ver lista de usuarios | `VIEW_USERS` | HTTP 403 |
| Editar/eliminar usuarios | `MANAGE_USERS` | Botones no aparecen en la vista |
| Gestionar roles | `MANAGE_ROLES` | Botones ocultos / HTTP 403 |
| Gestionar permisos | `MANAGE_PERMISSIONS` | Menú no visible |
| Asignar entrenador | `ASSIGN_TRAINER` | Botón no aparece |
| Ver mis estudiantes | `VIEW_USERS` (TRAINER) | Menú oculto para rol USER |

**Prueba concreta:**
1. Iniciar sesión como `camilo.vargas@gmail.com` / `user1`
2. Intentar acceder directamente a `http://localhost:8080/exercise/user/list` → respuesta **403 Forbidden**
3. El topbar no muestra los menús de **Usuarios**, **Roles** ni **Permisos**
4. Iniciar sesión como **Admin** → los mismos menús y rutas son accesibles

---

### REQ 13 — Interfaz responsiva

1. Abrir la aplicación en un navegador de escritorio → layout completo con topbar y tarjetas
2. Abrir las herramientas de desarrollador del navegador (F12) → activar vista de dispositivo móvil (ícono de móvil o `Ctrl+Shift+M`)
3. Todas las páginas tienen `<meta name="viewport" content="width=device-width, initial-scale=1.0">` — el contenido se adapta al ancho disponible
4. Probar en resoluciones de 375px (iPhone), 768px (tablet) y escritorio

---

### REQ 14 — Exportar reporte de progreso en PDF

**Usuario recomendado:** `camilo.vargas@gmail.com` / `user1` (tiene 6 registros)

1. Dashboard → **Mi progreso**
2. Clic en el botón rojo **Exportar PDF** (parte superior derecha)
3. El navegador descarga automáticamente `reporte-progreso.pdf`
4. El PDF contiene: encabezado con nombre del usuario, tabla con columnas Fecha · Ejercicio · Rutina · Series · Reps · Peso · Notas, y una fila por cada registro de progreso

> También se puede descargar desde **Estadísticas** → botón **Descargar PDF**.

---

## Arquitectura de bases de datos

Ver [PolyglotDatabases.md](PolyglotDatabases.md) para la explicación completa de la estrategia políglota PostgreSQL + MongoDB, la interacción entre ambas bases y cómo observarlas en uso.

---

## Estructura del proyecto

```
src/main/java/co/icesi/exercise/
├── config/          DotEnvPostProcessor, SecurityConfig, MongoConfig,
│                    DataInitializer
├── controller/      9 controladores MVC: User, Exercise, Routine, Event,
│                    Progress, Recommendation, Role, Permission, Login
├── dto/             AppUserDTO, RoleDTO, PermissionDTO
├── model/           Entidades JPA: AppUser, Role, Permission, Recommendation
│   └── nosql/       Documentos MongoDB: ExerciseDocument, RoutineDocument,
│                    ProgressRecordDocument, EventDocument + embebidos
├── repositories/    Repositorios JPA (con @EntityGraph donde aplica)
│   └── nosql/       Repositorios MongoDB
└── services/        9 servicios de negocio

src/main/resources/
├── application.properties      Variables como ${DB_URL}, ${MONGODB_URI}
├── META-INF/spring.factories   Registro de DotEnvPostProcessor
└── templates/                  Vistas Thymeleaf organizadas por módulo
    ├── dashboard/
    ├── exercise/
    ├── routine/
    ├── event/
    ├── progress/
    ├── recommendation/
    ├── user/
    ├── role/
    ├── permission/
    ├── login/
    └── fragments/
```
