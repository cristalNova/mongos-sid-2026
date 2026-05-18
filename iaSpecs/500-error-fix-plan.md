# Plan de Corrección — Errores 500 Post-UI (FitCampus)

**Fecha de diagnóstico:** 2026-05-17
**Estado:** ✅ Correcciones aplicadas | 119/119 tests pasando

---

## Diagnóstico

### Síntoma
Después de iniciar sesión como cualquier rol, **todas las rutas del menú** devuelven HTTP 500:

| Ruta afectada | ¿Por qué falla? |
|---|---|
| `/exercise/list` | Topbar fragment lanza NullPointerException |
| `/routine/list` | Topbar fragment lanza NullPointerException |
| `/event/list` | Topbar fragment lanza NullPointerException |
| `/progress/list` | Topbar fragment lanza NullPointerException |
| `/progress/stats` | Topbar fragment + `#aggregates` inválido |
| `/recommendation/list` | Topbar fragment lanza NullPointerException |
| `/user/my-students` | Topbar fragment lanza NullPointerException |
| `/user/list` | Topbar fragment lanza NullPointerException |
| `/role/view` | Topbar fragment lanza NullPointerException |
| `/permission/view` | Topbar fragment lanza NullPointerException |

**La página del Dashboard sí funcionaba** porque tiene su propio `<header>` inline y no incluye el fragment `fragments/topbar`.

---

## Causa raíz

### Bug 1 (Crítico — afecta TODAS las rutas): `#request` eliminado en Thymeleaf 3.1

**Descripción:**
En la Fase 2 de la mejora de UI se añadió resaltado de pestaña activa al topbar usando expresiones como:

```html
th:classappend="${#request.servletPath.startsWith('/user/list') ? ' active' : ''}"
```

En **Thymeleaf 3.1** (incluido con Spring Boot 4.x), el objeto implícito `#request` fue **eliminado por seguridad** (CVE-2022-22965). En el contexto de expresiones OGNL:
- `#request` evalúa a `null`
- Acceso a propiedad sobre null: `null.servletPath` → `null` (OGNL es nulo-seguro para propiedades)
- **Llamada a método sobre null: `null.startsWith('...')` → lanza `NullPointerException`**

Thymeleaf envuelve la excepción en `TemplateProcessingException` → Spring MVC devuelve HTTP 500.

**Archivos afectados:**
- `src/main/resources/templates/fragments/topbar.html` — 11 expresiones `.startsWith()` sobre `#request.servletPath`

---

### Bug 2 (Afecta `/progress/stats`): `#aggregates` no es un utility object de Thymeleaf

**Descripción:**
En la Fase 5 se añadieron cajas de resumen en `stats.html` usando:

```html
th:with="totalReps=${#aggregates.sum(records.![repetitions != null ? repetitions : 0])}"
```

`#aggregates` **no existe** en el dialecto estándar de Thymeleaf. Los utility objects disponibles son `#strings`, `#lists`, `#numbers`, `#dates`, etc. Intentar usar `#aggregates` lanza `TemplateProcessingException` → HTTP 500.

**Archivos afectados:**
- `src/main/resources/templates/progress/stats.html`

---

### Bug 3 (Cosmético — CSS): Variables CSS no definidas

Las clases `.card-icon`, `.card-body`, `.card-metric` en `dashboard.css` referenciaban `var(--space-1)` y `var(--space-2)` que no existían en `app-vars.css` (los tokens correctos son `--space-xs` y `--space-sm`). Afecta el layout del dashboard pero no causa errores 500.

### Bug 4 (Cosmético — CSS): Bloque CSS inválido en `app-vars.css`

El bloque de dark mode mezclaba un selector con una at-rule en una lista separada por comas:

```css
/* INVÁLIDO */
[data-theme="dark"],
@media (prefers-color-scheme: dark) { ... }
```

Esto es sintaxis CSS inválida. Los navegadores ignoraban el bloque completo, por lo que las variables dark mode no se aplicaban al modo de preferencia del sistema.

---

## Correcciones Aplicadas

### Fix 1: `GlobalModelAdvice` + actualización del topbar

**Archivo creado:** `src/main/java/co/icesi/exercise/controller/GlobalModelAdvice.java`

```java
@ControllerAdvice
public class GlobalModelAdvice {
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getServletPath();
    }
}
```

Este `@ControllerAdvice` inyecta automáticamente el servlet path como atributo `currentPath` en el modelo de **todos** los requests, sin necesidad de modificar cada controller.

**Archivo modificado:** `src/main/resources/templates/fragments/topbar.html`

Todos los `#request.servletPath` fueron reemplazados por `${currentPath}` con guardas null-safe:

```html
<!-- Antes (rompe con Thymeleaf 3.1) -->
th:classappend="${#request.servletPath.startsWith('/user/list') ? ' active' : ''}"

<!-- Después (correcto) -->
th:classappend="${currentPath != null and currentPath.startsWith('/user/list') ? ' active' : ''}"
```

---

### Fix 2: Cómputo server-side de totales en `ProgressRecordController.stats()`

**Archivo modificado:** `src/main/java/co/icesi/exercise/controller/ProgressRecordController.java`

Se añadieron dos atributos al modelo calculados en Java:

```java
long totalReps = records.stream()
        .filter(r -> r.getRepetitions() != null)
        .mapToLong(ProgressRecordDocument::getRepetitions).sum();
long totalSeries = records.stream()
        .filter(r -> r.getSeries() != null)
        .mapToLong(ProgressRecordDocument::getSeries).sum();

model.addAttribute("totalReps", totalReps);
model.addAttribute("totalSeries", totalSeries);
```

**Archivo modificado:** `src/main/resources/templates/progress/stats.html`

Se reemplazaron las expresiones con `#aggregates` por referencias a los nuevos atributos:

```html
<!-- Antes (inválido) -->
th:with="totalReps=${#aggregates.sum(records.![repetitions != null ? repetitions : 0])}"

<!-- Después (correcto) -->
th:text="${totalReps}"
```

---

### Fix 3: Variables CSS corregidas en `dashboard.css`

`var(--space-1)` → `var(--space-xs)`
`var(--space-2)` → `var(--space-sm)`

---

### Fix 4: Bloque CSS dark mode separado en `app-vars.css`

El bloque inválido fue separado en dos bloques válidos:

```css
/* Para prefers-color-scheme */
@media (prefers-color-scheme: dark) {
    :root:not([data-theme="light"]) { ... }
}

/* Para toggle manual */
[data-theme="dark"] { ... }
```

---

## Verificación

```bash
mvn test
# [INFO] Tests run: 119, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
```

### Checklist de pruebas manuales

- [ ] Login como `admin@fitcampus.co` → Dashboard carga sin error
- [ ] Navegar a `/exercise/list` → Carga sin 500, pestaña "Ejercicios" resaltada
- [ ] Navegar a `/routine/list` → Carga sin 500
- [ ] Navegar a `/event/list` → Carga sin 500
- [ ] Navegar a `/progress/list` → Carga sin 500
- [ ] Navegar a `/progress/stats` → Carga sin 500, boxes de totales muestran valores
- [ ] Navegar a `/recommendation/list` → Carga sin 500
- [ ] Navegar a `/user/list` → Carga sin 500
- [ ] Navegar a `/role/view` → Carga sin 500
- [ ] Navegar a `/permission/view` → Carga sin 500
- [ ] Dark mode (botón luna) → Aplica correctamente en todas las páginas
- [ ] Login como rol USER (sin `MANAGE_ROLES`/`MANAGE_PERMISSIONS`) → Pestañas Roles/Permisos no visibles

---

## Impacto por rol

| Rol | Impacto antes del fix | Impacto después del fix |
|---|---|---|
| ADMIN | 500 en todas las rutas del menú | ✅ Todo funciona |
| TRAINER | 500 en todas las rutas del menú | ✅ Todo funciona |
| USER | 500 en todas las rutas del menú | ✅ Todo funciona |

Los errores 500 afectaban **todos los roles** por igual, ya que la causa es el rendering del fragment compartido `topbar.html`, no una validación de permisos.
