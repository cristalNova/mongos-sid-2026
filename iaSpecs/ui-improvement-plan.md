# Plan de mejora de UI — FitCampus

> **Contexto:** La app actualmente usa CSS básico con colores planos, sin librería de iconos, sin mensajes flash, sin búsqueda/filtro y con `confirm()` inline para eliminar. El objetivo es una UI más dinámica y con mejor UX **manteniendo SSR puro con Thymeleaf**. JavaScript permitido solo como mejora progresiva (vanilla JS, sin frameworks SPA).
>
> **Stack de referencia:** Spring Boot 4.0.3 · Thymeleaf · Spring Security · CSS custom properties · Chart.js (ya en uso) · HTML `<dialog>` nativo

---

## Fase 1 — Sistema de diseño (Design Tokens)

### 1.1 Variables CSS globales
- [ ] Crear `src/main/resources/static/css/app-vars.css` con custom properties:
  ```css
  :root {
    --clr-primary:     #9EB16F;   /* verde acción */
    --clr-primary-dk:  #7a9254;
    --clr-danger:      #ff6b6b;
    --clr-danger-dk:   #e85555;
    --clr-accent:      #5a3e8a;   /* púrpura */
    --clr-pink:        #F3C1D9;
    --clr-yellow:      #F5D76E;
    --clr-bg:          #FAF4E4;   /* fondo crema */
    --clr-surface:     #ffffff;
    --clr-surface-alt: #f8f1e3;
    --clr-text:        #2d2d2d;
    --clr-text-muted:  #777777;
    --clr-border:      #e0d9cc;

    --radius-sm:  8px;
    --radius-md:  12px;
    --radius-lg:  20px;
    --radius-pill: 999px;

    --shadow-sm:  0 1px 3px rgba(0,0,0,.08);
    --shadow-md:  0 4px 12px rgba(0,0,0,.10);
    --shadow-lg:  0 8px 24px rgba(0,0,0,.14);

    --space-xs:  0.25rem;
    --space-sm:  0.5rem;
    --space-md:  1rem;
    --space-lg:  1.5rem;
    --space-xl:  2rem;

    --transition: 150ms ease;
    --font:       'Montserrat', sans-serif;
  }
  ```
- [ ] Reemplazar todos los colores hardcodeados en `user-list.css` por variables
- [ ] Reemplazar todos los colores hardcodeados en `user-detail.css` por variables
- [ ] Reemplazar todos los colores hardcodeados en `topbar.css` por variables
- [ ] Reemplazar todos los colores hardcodeados en `dashboard.css` por variables
- [ ] Reemplazar todos los colores hardcodeados en `login.css` por variables
- [ ] Importar `app-vars.css` en todos los CSS existentes (`@import './app-vars.css'`)
- [ ] Eliminar estilos inline de color de las plantillas list (moverlos a clases CSS dedicadas: `.chip--success`, `.chip--danger`, `.chip--accent`, `.chip--info`)

### 1.2 Fragmento de iconos SVG
- [ ] Crear `src/main/resources/templates/fragments/icons.html` con definición de un sprite SVG `<symbol>` para:
  - `icon-home` — dashboard
  - `icon-users` — usuarios
  - `icon-dumbbell` — ejercicios
  - `icon-list` — rutinas
  - `icon-calendar` — eventos
  - `icon-chart` — progreso / estadísticas
  - `icon-star` — recomendaciones
  - `icon-shield` — roles / permisos
  - `icon-plus` — crear
  - `icon-edit` — editar
  - `icon-trash` — eliminar
  - `icon-check` — éxito
  - `icon-x` — error / cerrar
  - `icon-search` — búsqueda
  - `icon-logout` — cerrar sesión
  - `icon-arrow-left` — volver
- [ ] Añadir fragmento: `th:fragment="icon(name, size)"` que emite `<svg class="icon" ...><use href="#icon-{name}"/></svg>`
- [ ] Añadir en `<body>` del layout base (o en cada plantilla) el `th:replace` del sprite SVG
- [ ] Definir estilos de `.icon` en `app-vars.css`: `width: 1em; height: 1em; vertical-align: -0.125em; fill: currentColor`

---

## Fase 2 — Navegación y Layout global

### 2.1 Topbar mejorada
- [ ] Añadir icono antes del texto en cada tab de `fragments/topbar.html`
- [ ] Implementar **estado activo**: añadir `th:classappend="${#request.requestURI.startsWith('/exercise/event') ? 'active' : ''}"` (o `th:attrappend`) en cada tab
- [ ] Añadir clase CSS `.tab.active` con borde inferior o fondo resaltado
- [ ] Incluir contador de notificaciones en el avatar (ej: recomendaciones no leídas) — badge CSS con `data-count`
- [ ] Mejorar `.user-box`: mostrar nombre completo, añadir dropdown con "Perfil" y "Cerrar sesión" (CSS `:focus-within` toggle, sin JS)

### 2.2 Fragmento de breadcrumbs
- [ ] Crear `src/main/resources/templates/fragments/breadcrumb.html`
  ```html
  <!-- th:fragment="breadcrumb(items)" -->
  <!-- items: lista de pares [label, href] -->
  ```
- [ ] Añadir estilos `.breadcrumb` en `user-detail.css`
- [ ] Usar en todas las páginas de detalle/formulario (form.html, exercises.html, stats.html, etc.)

### 2.3 Fragmento de mensajes flash
- [ ] Crear `src/main/resources/templates/fragments/flash.html`
  - Muestra `${flashSuccess}` con clase `.flash--success`
  - Muestra `${flashError}` con clase `.flash--error`
  - Auto-cierre: CSS `animation: fadeOut 4s forwards` con `animation-delay: 3s`
- [ ] Añadir en todos los controllers los atributos de modelo `flashSuccess` / `flashError` en operaciones CRUD (redirect con `RedirectAttributes.addFlashAttribute`)
- [ ] Incluir el fragmento flash en todas las plantillas list (y en el topbar fragment para cobertura global)
- [ ] Estilos: `.flash` como barra sticky debajo del topbar, con icono de check/x y botón de cierre

### 2.4 Dashboard mejorado
- [ ] Añadir icono SVG grande (100×100) a cada `.dashboard-card`
- [ ] Inyectar métricas de conteo en el controlador `DashboardController`: total usuarios, rutinas, eventos, registros de progreso
- [ ] Mostrar el conteo como número grande debajo del icono en cada card
- [ ] Añadir clase `.dashboard-card--primary` para la card de mayor relevancia (mayor prominencia visual)
- [ ] Hero card: cambiar texto genérico por saludo personalizado: "Bienvenido, {nombre}" con fecha actual

---

## Fase 3 — Páginas de lista

### 3.1 Barra de búsqueda
- [ ] Añadir formulario GET con `input[name=q]` y botón de búsqueda en el encabezado de cada lista:
  - `/user/list?q=`
  - `/exercise/list?q=`
  - `/routine/list?q=` y `/routine/mine?q=`
  - `/event/list?q=`
  - `/progress/list?q=`
  - `/recommendation/list?q=`
- [ ] Implementar filtrado por `q` en cada Service/Controller (filtro en memoria sobre lista ya cargada o via query de repositorio)
- [ ] Mantener el valor de `q` en el input tras la búsqueda (`th:value="${param.q}"`)
- [ ] Estilos: `.search-bar` con icono de lupa incrustado (CSS background-image o ::before)

### 3.2 Estado vacío ilustrado
- [ ] Crear `src/main/resources/templates/fragments/empty-state.html`
  - `th:fragment="emptyState(icon, message, actionHref, actionLabel)"`
  - Muestra icono SVG grande, mensaje descriptivo y botón de acción opcional
- [ ] Reemplazar todos los `<p th:if="${#lists.isEmpty(...)}">` por `th:replace="fragments/empty-state :: emptyState(...)"` en las 12 plantillas list

### 3.3 Modal de confirmación de eliminación (sin JS externo)
- [ ] Crear `src/main/resources/templates/fragments/confirm-modal.html`
  - Usar `<dialog>` nativo de HTML con `<form method="dialog">`
  - Botones: "Cancelar" (submit `dialog`) y "Eliminar" (submit el formulario POST real)
  - Activación: `<button onclick="document.getElementById('confirm-{id}').showModal()">Eliminar</button>`
  - Requiere ~5 líneas de vanilla JS o el método nativo `HTMLDialogElement.showModal()`
- [ ] Reemplazar todos los `onclick="return confirm(...)"` en las 12 plantillas list por el nuevo modal
- [ ] Estilos del modal: backdrop semitransparente, card centrado con sombra, animación `scale(0.9) → scale(1)` en apertura

### 3.4 Ordenamiento de columnas
- [ ] Añadir parámetros `?sort=field&dir=asc|desc` en los controllers de lista
- [ ] Mostrar indicador visual (flechas ↑↓) en los encabezados de columna clicables
- [ ] Mantener el parámetro `q` al cambiar el orden

### 3.5 Paginación
- [ ] Añadir fragmento `fragments/pagination.html` con paginación estilo "anterior / 1 2 3 / siguiente"
- [ ] Convertir listados de usuarios y ejercicios a `Page<T>` (Spring Data `Pageable`)
- [ ] Tamaño de página por defecto: 20 elementos

---

## Fase 4 — Páginas de formulario

### 4.1 Inputs con label flotante
- [ ] Añadir clase `.field` como wrapper de cada `label + input`
- [ ] Implementar CSS floating label:
  ```css
  .field { position: relative; }
  .field label { position: absolute; top: 0.9rem; left: 1rem; transition: var(--transition); }
  .field input:focus ~ label,
  .field input:not(:placeholder-shown) ~ label { top: -0.5rem; font-size: 0.72rem; }
  ```
- [ ] Aplicar en todos los form templates (7 formularios: user, routine, exercise, event, progress, permission, recommendation)

### 4.2 Validación con feedback visual
- [ ] Añadir `@Valid` y `BindingResult` en los métodos POST de todos los controllers de formulario
- [ ] En las plantillas, usar `th:errorclass="'field--error'"` en el `.field` y `th:errors` para mostrar el mensaje
- [ ] Estilos `.field--error input` con borde rojo y `.field-error-msg` con texto rojo pequeño

### 4.3 `<datalist>` para campos de texto controlado
- [ ] Campo "Tipo" en rutinas y ejercicios: añadir `<datalist>` con opciones `fuerza, cardio, movilidad, mixto`
- [ ] Campo "Dificultad" en rutinas y ejercicios: `<datalist>` con `FÁCIL, MEDIO, DIFÍCIL`
- [ ] Campo "Tipo" en usuarios: `<datalist>` si aplica roles/categorías

### 4.4 Contador de caracteres en textarea
- [ ] Para todos los `<textarea>` (descripción de ejercicio, evento, notas de progreso, recomendación):
  ```html
  <textarea id="desc" maxlength="500" oninput="updateCount(this)"></textarea>
  <small class="char-count"><span id="desc-count">0</span>/500</small>
  ```
  ```js
  function updateCount(el){
    document.getElementById(el.id+'-count').textContent = el.value.length;
  }
  ```

---

## Fase 5 — Visualización de datos

### 5.1 Mejoras a charts en `progress/stats.html`
- [ ] Añadir **gradiente de relleno** en el gráfico de línea (usando `createLinearGradient` en Chart.js)
- [ ] Añadir tooltips personalizados con el nombre del ejercicio y fecha
- [ ] Hacer los gráficos responsivos al contenedor (`maintainAspectRatio: false`, `.chart-container` con `height: 300px`)
- [ ] Añadir un tercer gráfico de barras: "Sesiones por semana" (agrupación por semana del año)

### 5.2 Barra de porcentaje de asistencia
- [ ] En `event/attendance.html`: añadir barra de progreso visual para el porcentaje de asistencia confirmada
  ```html
  <div class="progress-bar">
    <div class="progress-bar__fill" th:style="'width:' + ${pct} + '%'"></div>
  </div>
  ```
- [ ] Estilo: fondo gris, relleno verde, transición CSS `width 0.6s ease`, texto del porcentaje superpuesto

### 5.3 Sparklines inline en lista de progreso
- [ ] En `progress/list.html`: para cada ejercicio con ≥2 registros, renderizar un `<svg>` de miniatura con `<polyline>` calculado en Thymeleaf (coordenadas X=índice, Y=peso normalizado entre 0 y 40px)
- [ ] El cálculo de puntos se realiza en el controller/service y se pasa como lista de strings `"x,y"`

---

## Fase 6 — Polish, responsividad y accesibilidad

### 6.1 Animaciones de entrada
- [ ] Añadir en `app-vars.css`:
  ```css
  @keyframes fadeInUp {
    from { opacity: 0; transform: translateY(12px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  .user-row { animation: fadeInUp 200ms ease both; }
  .user-row:nth-child(n) { animation-delay: calc(n * 30ms); }
  ```
- [ ] Aplicar `prefers-reduced-motion: reduce` para deshabilitar animaciones si el usuario lo prefiere

### 6.2 Dark mode
- [ ] Añadir en `app-vars.css` media query `@media (prefers-color-scheme: dark)` con versión oscura de todas las variables de color
- [ ] Añadir botón toggle en topbar que escribe `document.documentElement.dataset.theme = 'dark'` y persiste en `localStorage`
  ```js
  // ~8 líneas, inline en topbar.html
  const saved = localStorage.getItem('theme');
  if (saved) document.documentElement.dataset.theme = saved;
  ```
- [ ] Añadir selector `[data-theme="dark"]` en `app-vars.css` con las mismas variables en oscuro

### 6.3 Responsividad mejorada
- [ ] Auditar los 12 templates list en viewport 375px y 768px
- [ ] `.user-row` en móvil: colapsar a layout de 1 columna con sección `.actions` en fila horizontal con scroll
- [ ] Topbar en móvil: convertir tabs a menú hamburguesa colapsable (CSS checkbox toggle, sin JS)
- [ ] Forms en móvil: ancho 100%, sin `.card` centrado

### 6.4 Estilos de impresión
- [ ] En `progress/stats.html`: añadir `<link rel="stylesheet" media="print" href="/css/print.css">`
- [ ] Crear `print.css`: ocultar topbar, botones y barra de búsqueda; expandir contenido al 100%; mostrar tablas en lugar de charts si es posible

### 6.5 Accesibilidad
- [ ] Todos los botones de acción con iconos deben tener `aria-label`
- [ ] Formularios: asociar cada `<label>` con su input vía `for`/`id`
- [ ] Botón de eliminar en modal: `autofocus` para acceso por teclado
- [ ] Añadir `role="status"` al fragmento flash para lectores de pantalla
- [ ] Verificar contraste de texto en chips de color (mínimo WCAG AA 4.5:1)

---

## Verificación final

- [ ] Ejecutar `./mvnw test` — los 119 tests deben pasar sin cambios
- [ ] Probar cada página como: admin (todas las opciones visibles), trainer, usuario regular
- [ ] Verificar en Chrome DevTools: mobile 375px, tablet 768px, desktop 1280px
- [ ] Probar dark mode en macOS con "Apariencia oscura" activa
- [ ] Verificar que ningún `confirm()` inline quede en el código (búsqueda global en IDE)
- [ ] Lighthouse audit: objetivo Performance ≥80, Accessibility ≥90

---

## Archivos creados / modificados

| Acción | Ruta |
|--------|------|
| CREAR | `src/main/resources/static/css/app-vars.css` |
| CREAR | `src/main/resources/static/css/print.css` |
| CREAR | `src/main/resources/templates/fragments/icons.html` |
| CREAR | `src/main/resources/templates/fragments/flash.html` |
| CREAR | `src/main/resources/templates/fragments/breadcrumb.html` |
| CREAR | `src/main/resources/templates/fragments/empty-state.html` |
| CREAR | `src/main/resources/templates/fragments/confirm-modal.html` |
| CREAR | `src/main/resources/templates/fragments/pagination.html` |
| MODIFICAR | `src/main/resources/static/css/user-list.css` |
| MODIFICAR | `src/main/resources/static/css/user-detail.css` |
| MODIFICAR | `src/main/resources/static/css/topbar.css` |
| MODIFICAR | `src/main/resources/static/css/dashboard.css` |
| MODIFICAR | `src/main/resources/static/css/login.css` |
| MODIFICAR | `src/main/resources/templates/fragments/topbar.html` |
| MODIFICAR | `src/main/resources/templates/dashboard/index.html` |
| MODIFICAR | 12 plantillas de lista (user, routine×2, exercise, event, progress, recommendation, role, permission…) |
| MODIFICAR | 7 plantillas de formulario |
| MODIFICAR | `src/main/resources/templates/progress/stats.html` |
| MODIFICAR | `src/main/resources/templates/event/attendance.html` |
| MODIFICAR | Controllers con `RedirectAttributes` para flash messages y parámetros de búsqueda/orden |
