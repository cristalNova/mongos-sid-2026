# Aplicación de actividad física – Universidad Icesi

En el marco del bienestar universitario y el fomento de hábitos saludables, la Universidad Icesi busca fortalecer su oferta de espacios, actividades y programas relacionados con la actividad física. Actualmente, muchos estudiantes y colaboradores realizan rutinas de ejercicio en la universidad, pero no existe una plataforma centralizada que permita hacer seguimiento a su progreso, diseñar rutinas personalizadas o conectar a los entrenadores con los usuarios de forma eficiente.

Por esta razón, se requiere una aplicación web que permita a los estudiantes y colaboradores crear y registrar sus rutinas de entrenamiento, llevar el control de sus avances, recibir retroalimentación por parte de entrenadores certificados y estar informados sobre eventos, talleres o espacios disponibles en tiempo real. Esta plataforma busca integrar tecnología, salud y comunidad para promover una vida universitaria más activa y conectada.

## Requerimientos

1. Los usuarios deben poder iniciar sesión con su cuenta institucional.
2. Los usuarios deben poder crear y editar sus rutinas de ejercicio, eligiendo entre ejercicios predefinidos o agregando personalizados.
3. Debe haber una base de datos de ejercicios con nombre, tipo (cardio, fuerza, movilidad), descripción, duración, dificultad y videos demostrativos.
4. Los usuarios deben poder registrar su progreso diario o semanal (por ejemplo: repeticiones, tiempo, nivel de esfuerzo).
5. Los entrenadores deben poder visualizar las rutinas y el progreso de los estudiantes o colaboradores que tengan asignados.
6. Los entrenadores deben poder generar recomendaciones según el avance del usuario.
7. Los entrenadores deben poder subir rutinas prediseñadas para que los usuarios las consulten y adopten.
8. Debe existir una sección de eventos y espacios disponibles (por ejemplo: horarios del gimnasio, clases de yoga, torneos).
9. Debe existir un panel administrativo para gestionar los entrenadores, asignarlos a usuarios y administrar la base de datos de ejercicios y eventos.
10. Los usuarios deben poder consultar un historial de sus actividades, rutinas pasadas y métricas de rendimiento.
11. El sistema debe mostrar estadísticas básicas de rendimiento como gráficos de progreso semanal o mensual.
12. Se debe garantizar la seguridad de los datos y un adecuado control de acceso según el rol (usuario, entrenador, administrador).
13. La interfaz debe ser accesible y responsiva, adaptándose tanto a escritorio como a dispositivos móviles.
14. La plataforma debe permitir descargar reportes personales de progreso en PDF.

## Configuración de Bases de Datos

El proyecto utilizará dos bases de datos:

### MongoDB (NoSQL)
```
spring.data.mongodb.uri=mongodb+srv://springboot:springboot@mongosintenstivos.xge7qkg.mongodb.net/?appName=MongosIntenstivos
```

### PostgreSQL (SQL)
```
spring.datasource.url=jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
spring.datasource.username=postgres.qziqehhkxjsqfgotwoti
spring.datasource.password=rleblx8B6VQmsAgz
spring.datasource.driver-class-name=org.postgresql.Driver
```

## Estrategia de Persistencia

- **Autenticación**: Se manejará con SQL (PostgreSQL)
- **Entidades NoSQL (MongoDB)**: El resto de las entidades se manejarán como NoSQL

### Entidades en SQL/PostgreSQL

Las siguientes tablas/entidades se almacenarán en PostgreSQL:
- `USER_TRAINER`
- `APP_USER`
- `USER_ROLE`
- `ROLE`
- `RECOMMENDATION`
- `ROLE_PERMISSION`
- `PERMISSION`

### Colecciones en MongoDB

Las siguientes colecciones se almacenarán en MongoDB:

#### Colección `exercises`
```javascript
db.createCollection("exercises", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      title: "exercises",
      required: ["_id"],
      properties: {
        "_id": { bsonType: "objectId" },
        "exerciseName": { bsonType: "string" },
        "description": { bsonType: "string" },
        "type": { bsonType: "string" },
        "difficultyType": { bsonType: "string" },
        "duration": { bsonType: "double" },
        "visualSupports": { bsonType: "array", items: { bsonType: "object" } },
      },
    },
  },
});
```

#### Colección `progress_records`
```javascript
db.createCollection("progress_records", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      title: "progress_records",
      required: ["_id"],
      properties: {
        "_id": { bsonType: "objectId" },
        "userId": { bsonType: "double" },
        "userFirstName": { bsonType: "string" },
        "userLastName": { bsonType: "string" },
        "routineId": { bsonType: "objectId" },
        "routineName": { bsonType: "string" },
        "exerciseName": { bsonType: "string" },
        "date": { bsonType: "date" },
        "time": { bsonType: "string" },
        "series": { bsonType: "double" },
        "repetitions": { bsonType: "double" },
        "weight": { bsonType: "double" },
        "progressNotes": { bsonType: "string" },
        "equipmentUsed": { bsonType: "string" },
      },
    },
  },
});
```

#### Colección `events`
```javascript
db.createCollection("events", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      title: "events",
      required: ["_id"],
      properties: {
        "_id": { bsonType: "objectId" },
        "name": { bsonType: "string" },
        "date": { bsonType: "date" },
        "description": { bsonType: "string" },
        "physicalSpace": { 
          bsonType: "object", 
          title: "physicalSpace", 
          properties: { 
            "spaceId": { bsonType: "objectId" }, 
            "name": { bsonType: "string" }, 
            "location": { bsonType: "string" }, 
            "capacity": { bsonType: "double" }, 
          } 
        },
        "subscriptions": { bsonType: "array", items: { bsonType: "object" } },
      },
    },
  },
});
```

#### Colección `routines`
```javascript
db.createCollection("routines", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      title: "routines",
      required: ["_id"],
      properties: {
        "_id": { bsonType: "objectId" },
        "routineName": { bsonType: "string" },
        "visibility": { bsonType: "bool" },
        "type": { bsonType: "string" },
        "difficultyType": { bsonType: "string" },
        "ownerId": { bsonType: "double" },
        "ownerFirstName": { bsonType: "string" },
        "ownerLastName": { bsonType: "string" },
        "createdAt": { bsonType: "date" },
        "updatedAt": { bsonType: "date" },
        "exercises": { bsonType: "array", items: { bsonType: "object" } },
      },
    },
  },
});
```

## Tareas de Implementación

1. **Revisar las entidades en el proyecto** y ajustar según la estrategia de persistencia definida
2. **Modificar los repositorios** para que funcionen con las dos bases de datos:
    - Repositorios SQL para entidades de autenticación y roles
    - Repositorios MongoDB para ejercicios, progreso, eventos y rutinas
3. **Completar los servicios** para implementar toda la lógica de negocio requerida
4. **Implementar controladores** utilizando Server Side Rendering con Thymeleaf
5. **Desarrollar las vistas HTML** consistentes con el diseño actual del proyecto, manteniendo la coherencia visual y la experiencia de usuario

## Nota Técnica

La autenticación se manejará exclusivamente con SQL, mientras que el resto de las entidades operarán con MongoDB. Se debe garantizar la correcta configuración de múltiples fuentes de datos en Spring Boot para soportar ambos sistemas de persistencia de manera simultánea.
