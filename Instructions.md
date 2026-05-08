# Instrucciones de Ejecución – Proyecto Spring Boot

## 1. Requisitos del sistema

Antes de ejecutar la aplicación localmente, se debe cumplir con los siguientes requisitos:

* **Java:** versión 17
* **Maven:** (opcional, solo si se va a compilar desde código fuente)
* **Base de datos:** PostgreSQL.
* **Red ZeroTier:** se requiere tener acceso a la red ZeroTier **93afae59635dc904** tanto para conectarse a la base de datos remota como para acceder al servidor donde está desplegada la aplicación.

Para verificar la versión de Java instalada:

```bash
    java -version
```

---

## 2. Configuración de base de datos

El proyecto se encuentra configurado por defecto para conectarse a una base de datos PostgreSQL ubicada en el siguiente host (red ZeroTier):

```
10.147.19.38
```

Esta configuración está definida en el archivo:

```
application.properties
```

Ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://10.147.19.38:5432/exercise_app
spring.datasource.username=team_911
spring.datasource.password=911_team_911
```

En caso de que se desee utilizar una base de datos propia:

1. Se deben ejecutar los scripts disponibles en el directorio:

```
scripts/
```

2. Posteriormente, se debe modificar el archivo `application.properties` con los nuevos datos de conexión.

---

## 3. Acceso a la aplicación (servidor desplegado)

La aplicación ya se encuentra desplegada en un servidor Apache Tomcat ubicado en el siguiente computador (red ZeroTier):

```
10.147.19.34
```

Dado que el despliegue ya está realizado, **no es necesario compilar ni ejecutar nada**. Basta con tener acceso a la red ZeroTier **93afae59635dc904** y acceder directamente desde un navegador web o herramienta de prueba de APIs a la siguiente URL base:

```
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT
```

### 3.1 Reinicio del servidor Tomcat

En caso de que el servidor Tomcat se haya detenido, es posible iniciarlo nuevamente accediendo al computador remoto **(10.147.19.34)** y ejecutando los siguientes comandos:

```bash
cd ~/Documents/apache-tomcat-10.1.54/bin

./startup.sh
```

Una vez ejecutado, el servidor volverá a estar disponible en el puerto `8080` y la aplicación podrá ser accedida con normalidad.

### 3.2 Endpoints disponibles

#### Autenticación

```
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/login
```

#### Usuario

```
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/list
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/signup
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/edit/{id}
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/update/{id}
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/delete/{id}
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/{id}/roles
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/{id}/roles/add
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/{id}/roles/remove
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/{id}/trainer
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/user/{id}/trainer/assign
```

#### Rol

```
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/role/view
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/role/create
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/role/update/{id}
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/role/delete/{id}
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/role/assign-permission
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/role/remove-permission
```

#### Permiso

```
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/permission/view
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/permission/create
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/permission/edit/{id}
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/permission/update/{id}
http://10.147.19.34:8080/exercise-0.0.1-SNAPSHOT/permission/delete/{id}
```

---

## 4. Ejecución generando el archivo `.war` / `.jar` (desde código fuente)

En caso de contar con el código fuente del proyecto y querer generar el archivo ejecutable manualmente:

### 4.1 Ubicación en el proyecto

```bash
    cd proyecto/
```

### 4.2 Compilación

```bash
    mvn clean package
```

Este comando genera el archivo en el directorio `target/`.

### 4.3 Ejecución

```bash
    java -jar target/exercise-0.0.1-SNAPSHOT.jar
```

Opcionalmente, se puede definir un puerto:

```bash
    java -jar target/exercise-0.0.1-SNAPSHOT.jar --server.port=8080
```

---

## 5. Ejecución sin generar el `.jar` (modo desarrollo)

Es posible ejecutar el proyecto sin generar el `.jar`, directamente desde el código fuente utilizando Maven. Para esto, es necesario ubicarse en la raíz del proyecto (directorio donde se encuentra el archivo `pom.xml`).

### 5.1 Comando de ejecución

Si se tiene Maven instalado en el sistema:

```bash
    mvn spring-boot:run
```

Alternativamente, el proyecto incluye el wrapper de Maven (`mvnw` o `mvnw.cmd`), por lo que no es necesario tener Maven instalado. En este caso, se puede ejecutar:

En sistemas Linux o macOS:

```bash
    ./mvnw spring-boot:run
```

En sistemas Windows:

```bash
    mvnw.cmd spring-boot:run
```

Este comando:

* Compila el proyecto
* Resuelve dependencias
* Inicia el servidor embebido

---

## 6. Datos de prueba

Para el uso de la aplicación se recomiendan los siguientes datos los cuales se encuentran registrados en la base de datos de la aplicación:

| Usuario | Correo | Contraseña | Rol |
|---|---|---|---|
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
| Felipe Gómez Acosta | felipe.gomez@gmail.com | user7 | Usuario |
| Mariana Díaz Londoño | mariana.diaz@gmail.com | user8 | Usuario |
| Esteban Muñoz Villegas | esteban.munoz@gmail.com | user9 | Usuario |
| Manuela Sánchez Piedrahita | manuela.sanchez@gmail.com | user10 | Usuario |
| Tomás Ríos Betancur | tomas.rios@gmail.com | user11 | Usuario |
| Vanessa Herrera Aristizábal | vanessa.herrera@gmail.com | user12 | Usuario |
| David Mejía Correa | david.mejia@gmail.com | user13 | Usuario |
| Alejandra Benjumea Arango | alejandra.benjumea@gmail.com | user14 | Usuario |
| Nicolás Prado Echeverri | nicolas.prado@gmail.com | user15 | Usuario |
| Isabela Flórez Zuluaga | isabela.florez@gmail.com | user16 | Usuario |


---

## 7. Consideraciones finales

* Para acceder tanto a la base de datos remota como al servidor de despliegue, se requiere estar conectado a la red ZeroTier **93afae59635dc904**.
* El archivo `.jar` generado contiene todas las dependencias necesarias para su ejecución.
* Se recomienda verificar siempre la disponibilidad del puerto antes de ejecutar la aplicación en modo local.

---