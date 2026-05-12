# Microservicio REST Gestion Archivo Clinico

Microservicio academico Spring Boot con CRUD REST, DTOs, validaciones Jakarta Validation, manejo global de errores y autenticacion JWT.

## Ejecutar

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/Mac:

```bash
./mvnw spring-boot:run
```

El proyecto usa el puerto `8081`.

## Login JWT en Postman

1. Crear una peticion `POST`.
2. URL: `http://localhost:8081/api/v1/auth/login`
3. Body -> raw -> JSON:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta esperada:

```json
{
  "token": "TOKEN_JWT",
  "type": "Bearer",
  "username": "admin",
  "role": "ADMIN"
}
```

Copiar el valor de `token`. En las demas peticiones usar:

```http
Authorization: Bearer TOKEN_JWT
```

## Ejemplos JSON

Crear paciente: `POST /api/v1/pacientes`

```json
{
  "numRut": "12345678-9",
  "pnombre": "Ana",
  "snombre": "Maria",
  "papellido": "Lopez",
  "sapellido": "Perez",
  "fechaNaci": "1990-05-20",
  "direccion": "Av. Salud 123",
  "nacionalidad": "Chilena"
}
```

Crear estante: `POST /api/v1/estantes`

```json
{
  "numEstante": 1,
  "numBodega": 2,
  "ubicacion": "Archivo central pasillo A"
}
```

Crear ficha clinica: `POST /api/v1/fichas-clinicas`

```json
{
  "folioFicha": "FC-2026-0001",
  "fechaCreacion": "2026-05-11",
  "idPaciente": 1,
  "idEstante": 1
}
```

Crear reserva atencion: `POST /api/v1/reservas-atencion`

```json
{
  "fechaReservada": "2026-05-15",
  "horaReservada": "09:30:00",
  "especialidad": "Medicina interna",
  "profesional": "Dra. Sofia Ramos",
  "idPaciente": 1
}
```

Crear expediente hospitalizacion: `POST /api/v1/expedientes-hospitalizacion`

```json
{
  "codExpediente": "EXP-2026-0001",
  "rutPaciente": "12345678-9",
  "digitalizacion": true,
  "idReservaAtencion": 1
}
```

## Rutas principales

Todas las rutas, excepto login y documentacion publica, requieren `Authorization: Bearer TOKEN_JWT`.

- `GET|POST /api/v1/pacientes`
- `GET|PUT|DELETE /api/v1/pacientes/{id}`
- `GET|POST /api/v1/fichas-clinicas`
- `GET|PUT|DELETE /api/v1/fichas-clinicas/{folio}`
- `GET|POST /api/v1/reservas-atencion`
- `GET|PUT|DELETE /api/v1/reservas-atencion/{id}`
- `GET|POST /api/v1/expedientes-hospitalizacion`
- `GET|PUT|DELETE /api/v1/expedientes-hospitalizacion/{id}`
- `GET|POST /api/v1/administrativos`
- `GET|PUT|DELETE /api/v1/administrativos/{id}`
- `GET|POST /api/v1/prestamos`
- `GET|PUT|DELETE /api/v1/prestamos/{id}`
- `GET|POST /api/v1/auditorias`
- `GET|PUT|DELETE /api/v1/auditorias/{id}`
- `GET|POST /api/v1/estantes`
- `GET|PUT|DELETE /api/v1/estantes/{id}`
- `GET|POST /api/v1/registros-archivados`
- `GET|PUT|DELETE /api/v1/registros-archivados/{id}`
- `GET|POST /api/v1/registros-ingreso-archivo`
- `GET|PUT|DELETE /api/v1/registros-ingreso-archivo/{id}`

## Probar compilacion

Windows:

```powershell
.\mvnw.cmd clean test
```

En este repositorio se corrigio un detalle del script `mvnw.cmd` para que funcione cuando la carpeta `.m2` local no es un enlace simbolico.
