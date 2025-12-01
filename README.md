# Proyecto Transversal – Calidad de Software
## Automatización OpenCart (Java + Selenium + POM)

Este proyecto automatiza casos de prueba funcionales sobre la tienda demo de OpenCart:

> http://opencart.abstracta.us/

Usa **Java**, **Selenium WebDriver**, **TestNG**, **Maven**, **Apache POI** y el patrón **Page Object Model (POM)**. 
Los datos de prueba se leen desde archivos Excel y los resultados se registran en un archivo de logs en Excel.

---

Equipo de revisión:

Maria Fanny Giraldo - A00399948
John Hortua - A00373357
Santiago Velasquez - A00372725

---

## 1. Tecnologías y herramientas

- Java 17+
- Maven
- Selenium WebDriver 4.x
- TestNG
- Apache POI (lectura/escritura de Excel)
- Patrón Page Object Model (POM)
- Navegador: Google Chrome

---

## 2. Estructura del proyecto

```text
src/
  main/
    java/
      com.opencart.pages/   # Clases POM
        BasePage.java
        HomePage.java
        RegisterPage.java
        LoginPage.java
        ProductPage.java
        CartPage.java

      com.opencart.utils/   # Utilidades
        ExcelUtil.java      # Lectura/escritura Excel (Apache POI)
        WaitUtil.java       # WebDriverWait explícito

    resources/
      inputData.xlsx        # Datos de entrada (usuarios, login, productos)
      Logs.xlsx             # Logs de ejecución (registro, login, carrito, verificación)

  test/
    java/
      com.opencart.test/
        BaseTest.java           # Setup/tearDown, creación de WebDriver
        RegisterTest.java       # Caso 1: Registro de usuario
        LoginTest.java          # Caso 2: Login (éxito / error)
        SearchAndCartTest.java  # Caso 3: Búsqueda + agregado al carrito
        CartVerificationTest.java # Caso 4: Verificación de productos en el carrito
```

---

## 3. Datos de entrada (inputData.xlsx)

Archivo: `src/main/resources/inputData.xlsx`

### 3.1. Hoja `RegistroUsuarios`

Usada por `RegisterTest`.

Columnas (fila 1):

1. `FirstName`  
2. `LastName`  
3. `Email`  
4. `Telephone`  
5. `Password`  

Desde la fila 2 en adelante se definen usuarios a registrar.

---

### 3.2. Hoja `DataLogin`

Usada por `LoginTest`.

Columnas (fila 1):

1. `Email`  
2. `Password`  
3. `ExpectedResult` (`SUCCESS` / `ERROR`)  

Cada fila representa un escenario:

- `SUCCESS` → espera login exitoso (sin mensaje de error).
- `ERROR`   → espera mensaje de advertencia de credenciales inválidas.

---

### 3.3. Hoja `ProductosBusqueda`

Usada por `SearchAndCartTest` y `CartVerificationTest`.

Columnas (fila 1):

1. `Categoria`  
2. `SubCategoria`  
3. `Producto`    (texto del link del producto en la tienda, ej. `iMac`, `MacBook`)  
4. `Cantidad`    (puede ser `1`, `2` o `1.0`, `2.0` desde Excel)  

Cada fila define un producto a buscar y agregar al carrito.

---

## 4. Logs de ejecución (Logs.xlsx)

Archivo: `src/main/resources/Logs.xlsx`  

Se usa un **solo archivo de log** con varias hojas:

### 4.1. Hoja `Registro`

Escrita por `RegisterTest`.

Columnas recomendadas:

1. `FirstName`  
2. `LastName`  
3. `Email`  
4. `Telephone`  
5. `Password`  
6. `Estado` (`EXITOSO` / `FALLIDO`)  
7. `Mensaje` (mensaje de éxito o texto de error)

---

### 4.2. Hoja de Login (por ejemplo `Login`)

Escrita por `LoginTest`.

Columnas sugeridas:

1. `Email`  
2. `Password`  
3. `ExpectedResult` (`SUCCESS` / `ERROR`)  
4. `Estado` (`EXITOSO` / `FALLIDO`)  
5. `Mensaje` (warning de error o “Login exitoso”)

---

### 4.3. Hoja `carrito`

Escrita por `SearchAndCartTest`.

Columnas:

1. `Categoria`  
2. `SubCategoria`  
3. `Producto`  
4. `Cantidad`  
5. `Estado` (`AGREGADO` / `ERROR`)  
6. `Mensaje` (texto del éxito o error)

---

### 4.4. Hoja `VerificacionCarrito`

Escrita por `CartVerificationTest`.

Columnas:

1. `FechaHora`  
2. `Producto`  
3. `CantidadEsperada`  
4. `CantidadReal`  
5. `Estado` (`EXITOSO` / `FALLIDO`)  
6. `Mensaje` (detalle del resultado de la verificación)

---

## 5. Casos de prueba implementados

### 5.1. Registro de Usuario – `RegisterTest`

- Lee datos desde `RegistroUsuarios`.
- Navega al formulario de registro (`HomePage` → `RegisterPage`).
- Completa el formulario con POM.
- Verifica:
  - Si aparece alerta de error → registra `FALLIDO`.
  - Si no hay error → lee mensaje de éxito (título `Account`) y verifica que lo contenga → `EXITOSO`.
- Registra el resultado en `Logs.xlsx` → hoja `Registro`.
- Si el estado es `FALLIDO`, hace `Assert.fail` con el mensaje.

---

### 5.2. Inicio de Sesión – `LoginTest`

- DataProvider lee `DataLogin`.
- Por cada fila:
  - Abre página de login.
  - Ingresa `Email` y `Password`, hace click en `Login`.
  - Para `ExpectedResult == SUCCESS`:
    - Verifica que **no** haya mensaje de error (warning).
  - Para cualquier otro valor (`ERROR`):
    - Verifica que el warning esté presente.
- Escribe una fila de log con:
  - Email, Password, ExpectedResult, Estado (`EXITOSO`/`FALLIDO`), Mensaje.

---

### 5.3. Búsqueda y agregado al carrito – `SearchAndCartTest`

- Lee `ProductosBusqueda`.
- En un **solo test y una sola sesión**:
  - Para cada producto válido:
    - Navega al `baseUrl`.
    - Usa `HomePage.searchProduct(producto)` y `openProductFromResults(producto)`.
    - Usa `ProductPage`:
      - `selectFirstOptionIfAvailable()` si el producto tiene dropdowns.
      - `setQuantity(cantidad)` (normaliza valores tipo `1.0` a `1`).
      - `addToCart()`.
    - Espera mensaje de éxito (`.alert-success`) con `WaitUtil`.
    - Aserción `Assert.assertTrue(exito)`.
    - Loggea en hoja `carrito` de `Logs.xlsx`.

---

### 5.4. Verificación de productos en el carrito – `CartVerificationTest`

En un solo test:

1. Lee `ProductosBusqueda`.
2. Agrega productos al carrito en la misma sesión (mismo flujo que `SearchAndCartTest`).
3. Construye un mapa de productos esperados (`Producto -> Cantidad total`).
4. Navega a `Shopping Cart` (`/index.php?route=checkout/cart`).
5. Usa `CartPage.getCartItems()` para leer nombre y cantidad de cada fila.
6. Normaliza nombres y construye un mapa de productos reales.
7. Para cada producto esperado:
   - Verifica que exista en el carrito (`Assert.assertTrue`).
   - Compara cantidades (`Assert.assertEquals`).
   - Escribe una fila en `VerificacionCarrito` con el detalle.

---

## 6. Ejecución de pruebas

Desde la raíz del proyecto:

### 6.1. Ejecutar todos los tests

```bash
mvn test
```

### 6.2. Ejecutar un test específico

```bash
mvn -Dtest=RegisterTest test
mvn -Dtest=LoginTest test
mvn -Dtest=SearchAndCartTest test
mvn -Dtest=CartVerificationTest test
```

---

## 7. Estrategia de automatización (resumen)

- **Patrón POM**:
  - Cada página del sitio tiene su clase en `com.opencart.pages`.
  - `BasePage` centraliza helpers de Selenium y las esperas (`WaitUtil`).

- **Sincronización (esperas)**:
  - `WaitUtil` envuelve `WebDriverWait` + `ExpectedConditions`.
  - Se usa `esperavisible` y `esperaClickeable` para mensajes y botones clave.

- **Selectores**:
  - Uso de `By.id`, `By.cssSelector`, `By.xpath` y `By.linkText` según necesidad.
  - Se evita usar XPaths frágiles o índices absolutos cuando es posible.

- **Datos externos (Excel)**:
  - `ExcelUtil.leerDatos(...)` devuelve `List<String[]>` para DataProviders y ciclos.
  - `ExcelUtil.escribirLog(...)` escribe filas nuevas en `Logs.xlsx` (creando hoja si no existe).

- **Validaciones**:
  - `Assert.assertTrue / assertFalse / assertEquals` para validar:
    - Mensajes de éxito / error.
    - Presencia de warnings.
    - Existencia y cantidad de productos en el carrito.

---

## 8. Recomendaciones para la entrega

- Incluir en la carpeta de entrega:
  - Código fuente completo (con esta estructura).
  - `inputData.xlsx` y `Logs.xlsx`.
  - Evidencias (capturas) de:
    - Ejecuciones `mvn -Dtest=... test` con `BUILD SUCCESS`.
    - Pantallas de registro, login y carrito.
  - Documento (PDF/Word) explicando:
    - Casos de prueba.
    - Arquitectura (POM, utils, tests).
    - Cómo ejecutar el proyecto.
    - Cómo se usan los Excel y logs.
