# Persistencia políglota: PostgreSQL + MongoDB

Este documento explica la estrategia de bases de datos del proyecto FitCampus, por qué se eligió cada motor para cada dominio y cómo se puede observar su interacción al usar la aplicación.

---

## ¿Por qué dos bases de datos?

El proyecto combina dos patrones de datos con necesidades muy distintas:

| Dominio | Características | Motor elegido |
|---------|----------------|---------------|
| Autenticación y autorización | Estructura fija, relaciones estrictas (usuarios ↔ roles ↔ permisos), integridad referencial crítica | **PostgreSQL** |
| Contenido fitness | Documentos variables (ejercicios con videos, rutinas con listas de ejercicios embebidos, eventos con subscripciones), esquema flexible | **MongoDB** |

---

## Qué vive en PostgreSQL (Supabase)

Las siguientes tablas se gestionan con **Spring Data JPA + Hibernate**:

```
APP_USER          → datos del usuario (nombre, email, edad, peso, altura, password hash)
ROLE              → roles del sistema (USER, TRAINER, ADMIN)
PERMISSION        → permisos atómicos (VIEW_USERS, MANAGE_USERS, …)
USER_ROLE         → tabla de unión usuario ↔ rol (M:N)
ROLE_PERMISSION   → tabla de unión rol ↔ permiso (M:N)
USER_TRAINER      → tabla de unión usuario ↔ su(s) entrenador(es) (M:N auto-referencial)
RECOMMENDATION    → recomendaciones de entrenador a usuario (sender_id, receiver_id, message, date)
```

**Por qué PostgreSQL aquí:**
Las relaciones entre usuarios, roles y permisos requieren transacciones ACID y restricciones de integridad. Un usuario siempre debe tener al menos un rol; un permiso eliminado debe desaparecer de todos los roles. PostgreSQL garantiza esto de forma nativa.

### Verificar en Supabase

1. Iniciar sesión en [supabase.com](https://supabase.com) con la cuenta del proyecto
2. Abrir el proyecto → **Table Editor**
3. Las 7 tablas anteriores son visibles con sus relaciones

Al registrar un usuario nuevo (`/exercise/user/signup`), aparecerá inmediatamente una fila nueva en `APP_USER` y en `USER_ROLE` con el rol por defecto.

---

## Qué vive en MongoDB (Atlas)

Las siguientes colecciones se gestionan con **Spring Data MongoDB**:

### `exercises`

```json
{
  "_id": ObjectId,
  "exerciseName": "Sentadilla",
  "description": "Ejercicio compuesto de piernas...",
  "type": "FUERZA",
  "difficultyType": "INTERMEDIO",
  "duration": 45.0,
  "visualSupports": [
    { "supportType": "VIDEO", "url": "https://..." }
  ]
}
```

La lista `visualSupports` es un **subdocumento embebido**: no tiene colección propia ni clave foránea. Puede crecer o variar sin alterar el esquema de la colección.

---

### `routines`

```json
{
  "_id": ObjectId,
  "routineName": "Rutina Full Body",
  "visibility": true,
  "type": "FUERZA",
  "difficultyType": "AVANZADO",
  "ownerId": 3,
  "ownerFirstName": "Leidy",
  "ownerLastName": "Cardona",
  "createdAt": ISODate,
  "updatedAt": ISODate,
  "exercises": [
    {
      "exerciseId": "...",
      "exerciseName": "Sentadilla",
      "type": "FUERZA",
      "difficultyType": "INTERMEDIO",
      "duration": 45.0
    }
  ]
}
```

`ownerId` es la **clave de unión entre bases**: referencia el `id` de un `APP_USER` en PostgreSQL. El nombre del dueño se desnormaliza en el documento para evitar joins cross-database en las vistas de listado.

---

### `progress_records`

```json
{
  "_id": ObjectId,
  "userId": 5,
  "userFirstName": "Camilo",
  "userLastName": "Vargas",
  "routineId": "...",
  "routineName": "Rutina Full Body",
  "exerciseName": "Sentadilla",
  "date": ISODate,
  "time": "08:30",
  "series": 4,
  "repetitions": 12,
  "weight": 60.0,
  "progressNotes": "Buena técnica",
  "equipmentUsed": "Barra olímpica"
}
```

Igual que en routines, `userId` es una referencia débil a PostgreSQL. Los campos de nombre se desnormalizan para que las consultas del entrenador sean eficientes sin necesitar acceder a la otra base.

---

### `events`

```json
{
  "_id": ObjectId,
  "name": "Clase de Yoga",
  "date": ISODate,
  "description": "Sesión de yoga para principiantes",
  "physicalSpace": {
    "name": "Gimnasio Principal",
    "location": "Bloque A, Piso 2",
    "capacity": 30.0
  },
  "subscriptions": [
    {
      "userId": 5,
      "userFirstName": "Camilo",
      "userLastName": "Vargas",
      "attendance": false
    }
  ]
}
```

`physicalSpace` y `subscriptions` son **documentos embebidos**: no existen como entidades independientes. Esto permite leer un evento completo (con su espacio y todos sus inscritos) en una sola operación de lectura, algo costoso en SQL con múltiples JOINs.

---

## Cómo interactúan las dos bases de datos

Las dos bases no se comunican entre sí directamente. La lógica de negocio en la capa de servicios actúa como puente:

```
ProgressRecordService.createProgressRecord()
    │
    ├── appUserRepository.findById(userId)          ← consulta PostgreSQL
    ├── routineMongoRepository.findById(routineId)  ← consulta MongoDB
    └── progressRecordMongoRepository.save(record)  ← escribe en MongoDB
```

```
RoutineService.createRoutine()
    │
    ├── appUserRepository.findById(ownerId)         ← consulta PostgreSQL (obtener nombre)
    └── routineMongoRepository.save(routine)        ← escribe en MongoDB
```

Este patrón de **referencia por ID + desnormalización de nombre** evita joins imposibles entre motores distintos, con la contrapartida de que si un usuario cambia su nombre, los documentos históricos conservan el nombre anterior (comportamiento aceptable para registros de auditoría).

---

## Cómo observar ambas bases en uso al navegar la aplicación

### Acción → qué base se toca

| Acción en la app | PostgreSQL | MongoDB |
|-----------------|-----------|---------|
| Registrar usuario (`/user/signup`) | ✅ INSERT en `APP_USER`, `USER_ROLE` | — |
| Iniciar sesión | ✅ SELECT `APP_USER` + roles + permisos | — |
| Crear ejercicio | — | ✅ INSERT en `exercises` |
| Agregar video a ejercicio | — | ✅ UPDATE `exercises` (push en array `visualSupports`) |
| Crear rutina | ✅ SELECT `APP_USER` (para obtener nombre) | ✅ INSERT en `routines` |
| Adoptar rutina pública | ✅ SELECT `APP_USER` | ✅ INSERT nueva rutina en `routines` |
| Registrar progreso | ✅ SELECT `APP_USER` | ✅ SELECT `routines` + INSERT `progress_records` |
| Ver estadísticas | — | ✅ SELECT `progress_records` (filtrado por userId) |
| Exportar PDF | — | ✅ SELECT `progress_records` |
| Crear evento + inscribirse | — | ✅ INSERT / UPDATE `events` |
| Enviar recomendación | ✅ INSERT en `recommendation` | — |
| Ver recomendaciones recibidas | ✅ SELECT `recommendation` JOIN `APP_USER` | — |
| Asignar entrenador | ✅ INSERT en `USER_TRAINER` | — |
| Ver progreso de estudiante | ✅ SELECT `APP_USER` | ✅ SELECT `progress_records` |

### Verificar en Atlas (MongoDB)

1. Ingresar a [cloud.mongodb.com](https://cloud.mongodb.com)
2. Cluster `MongosIntenstivos` → **Browse Collections** → base de datos `exercise_app`
3. Colecciones visibles: `exercises`, `routines`, `progress_records`, `events`

Cada acción listada arriba genera cambios observables en tiempo real desde el panel de Atlas usando **Atlas Data Explorer** o conectando con **MongoDB Compass** a la URI del cluster.

### Verificar en Supabase (PostgreSQL)

1. Ingresar a [supabase.com](https://supabase.com) → proyecto del equipo
2. **Table Editor** → seleccionar tabla `app_user`, `recommendation`, etc.
3. Los registros aparecen inmediatamente después de cada operación

---

## Configuración técnica de la doble conexión

### `application.properties`

```properties
# PostgreSQL
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

# MongoDB
spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=${MONGODB_DATABASE:exercise_app}
```

### `MongoConfig.java` — por qué existe

Spring Boot 4.0.0 tiene un bug por el que `MongoAutoConfiguration` no aplica `spring.data.mongodb.uri` al `MongoClientSettings` correctamente, lo que hace que la app intente conectarse a `localhost:27017` en lugar de Atlas. `MongoConfig` crea manualmente el bean `MongoClient` usando `MongoClients.create(uri)`, lo que activa el mecanismo `@ConditionalOnMissingBean` de la autoconfiguración y resuelve el problema:

```java
@Bean
public MongoClient mongoClient() {
    return MongoClients.create(mongoUri);  // usa el URI de .env directamente
}
```

### Auto-detección de repositorios

Spring Boot detecta automáticamente los repositorios por el tipo que extienden:

- `JpaRepository<T, ID>` → conecta con el `DataSource` configurado (PostgreSQL)
- `MongoRepository<T, ID>` → conecta con el `MongoClient` configurado (Atlas)

No se necesita `@EnableJpaRepositories` ni `@EnableMongoRepositories` de forma explícita.

---

## Datos de inicialización automática

Al primer arranque con una base de datos vacía, `DataInitializer` (implementa `ApplicationRunner`) siembra automáticamente:

**PostgreSQL:**
- 5 permisos (`VIEW_USERS`, `MANAGE_USERS`, `MANAGE_ROLES`, `MANAGE_PERMISSIONS`, `ASSIGN_TRAINER`)
- 3 roles (`USER`, `TRAINER`, `ADMIN`) con sus permisos asignados
- 1 usuario administrador (`admin@fitcampus.co` / `admin123`)

**MongoDB:**
- 5 ejercicios de muestra (Sentadilla, Plancha, Burpee, Trote, Flexión de pecho)
- 1 rutina pública de muestra con ejercicios embebidos
- 1 evento de muestra con espacio físico embebido
