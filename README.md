# Mars Spins — versión web (Spring Boot)

Proyecto Integrador de Estructura de Datos. Las vistas HTML ahora están conectadas a un
backend en Java con Spring Boot, que usa las mismas estructuras de la versión de consola:

| Estructura | Dónde vive | Para qué |
|---|---|---|
| Arreglo `ruleta[37]` | `servicio/RuletaService.java` | Casillas de la ruleta, acceso O(1) por índice |
| Pila (`Pila` + `Nodo`) | `modelo/`, usada en `servicio/BlackjackService.java` | Mazo de blackjack: `push` al barajar, `pop` al repartir |
| `TAD_CuentaUsuario` | `modelo/` | Saldo ficticio protegido (solo cambia con `realizarApuesta` / `registrarResultado`) |

## Cómo correrlo

Necesitas **Java 17 o superior**.

**IntelliJ IDEA:** File → Open → selecciona la carpeta del proyecto (la que tiene `pom.xml`).
Espera a que Maven descargue las dependencias y ejecuta `MarsSpinsApplication`.

**VS Code:** con el "Extension Pack for Java" instalado, abre la carpeta y dale Run a `MarsSpinsApplication`.

**Terminal (con Maven instalado):**
```
mvn spring-boot:run
```

Luego abre **http://localhost:8080** → te manda al login. Crea una cuenta en "Regístrate".

> Las cuentas se guardan en memoria: si apagas el servidor, se borran.

### Versión de consola (para el video)

Sigue existiendo en `consola/MarsSpinsConsola.java` y usa los mismos servicios.
Clic derecho → Run `MarsSpinsConsola.main()`.

## Estructura

```
src/main/java/com/marsspins/
├── MarsSpinsApplication.java   ← arranca el servidor
├── modelo/       Nodo, Pila, TAD_CuentaUsuario, PartidaBlackjack
├── servicio/     Lógica: CuentaService, RuletaService, BlackjackService
├── controlador/  Endpoints REST (/api/...) y manejo de errores
├── dto/          Lo que entra y sale como JSON
└── consola/      Demo original en consola
src/main/resources/static/      ← vistas (HTML, CSS, JS, imágenes)
└── js/           api.js (fetch compartido), blackjack.js, ruleta.js
```

## Cómo se conectan vistas y backend

1. La página hace `fetch('/api/...')` (ver `static/js/api.js`).
2. El **controlador** recibe la petición y llama al **servicio**.
3. El servicio usa la Pila / el Arreglo / el TAD y regresa un objeto.
4. Spring lo convierte en JSON y la página lo dibuja.

La cuenta logueada y la mano de blackjack en curso se guardan en la **sesión** del servidor
(`HttpSession`). Así, aunque recargues la página a media mano, la partida sigue.

Como toda la lógica está en el servidor, nadie puede ponerse saldo desde la consola del navegador.

## API

| Método | Ruta | Cuerpo | Respuesta |
|---|---|---|---|
| POST | `/api/registro` | `{usuario, correo, contrasena, confirmar}` | cuenta |
| POST | `/api/login` | `{usuario, contrasena}` | cuenta |
| POST | `/api/logout` | — | 204 |
| GET | `/api/cuenta` | — | `{id, nombre, saldo}` |
| GET | `/api/ruleta/casillas` | — | las 37 casillas del arreglo |
| POST | `/api/ruleta/girar` | `{color: "rojo"\|"negro", monto}` | resultado del giro |
| GET | `/api/blackjack/estado` | — | estado de la mesa |
| POST | `/api/blackjack/iniciar` | `{monto}` | estado de la mesa |
| POST | `/api/blackjack/pedir` | — | estado de la mesa |
| POST | `/api/blackjack/plantarse` | — | estado de la mesa |
| POST | `/api/blackjack/doblar` | — | estado de la mesa |

Errores: `400 {"error": "..."}` (reglas del juego) y `401` (sin sesión → el JS manda al login).

## Siguientes pasos posibles

- Guardar usuarios y saldo en **MySQL** con Spring Data JPA (y contraseñas cifradas con BCrypt).
- Tragamonedas y Dados (en el lobby aparecen como "Próximamente").
