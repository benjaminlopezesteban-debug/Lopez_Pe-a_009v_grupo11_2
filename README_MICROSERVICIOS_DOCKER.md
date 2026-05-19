# Gestion Archivo - Arquitectura de Microservicios con Docker

Este documento describe la separacion academica del monolito `gestionArchivo` en 10 microservicios Spring Boot independientes. El proyecto monolitico original se mantiene intacto; la nueva solucion vive en la carpeta `microservicios/`.

## Arquitectura

La solucion usa microservicios independientes con persistencia propia. Cada servicio tiene:

- Proyecto Spring Boot propio.
- `pom.xml` propio.
- `Dockerfile` propio.
- `application.properties` propio.
- Controller, service, repository, model/entity y DTOs propios.
- Validaciones con Jakarta Validation.
- Manejo de excepciones con `GlobalExceptionHandler`.
- Seguridad basica con Spring Security y HTTP Basic.
- Logs con `Logger`.
- Endpoints REST bajo `/api/v1`.
- Conexion solo a su base PostgreSQL.

No se usa Kubernetes, Eureka ni API Gateway. La comunicacion entre servicios se realiza por REST usando `RestTemplate` y nombres de servicios Docker.

## Microservicios

| Microservicio | Puerto | Base PostgreSQL | Endpoint principal |
| --- | ---: | --- | --- |
| `paciente-service` | 8081 | `paciente_db` | `/api/v1/pacientes` |
| `ficha-clinica-service` | 8082 | `ficha_clinica_db` | `/api/v1/fichas-clinicas` |
| `reserva-atencion-service` | 8083 | `reserva_atencion_db` | `/api/v1/reservas-atencion` |
| `expediente-hospitalizacion-service` | 8084 | `expediente_hospitalizacion_db` | `/api/v1/expedientes-hospitalizacion` |
| `administrativo-service` | 8085 | `administrativo_db` | `/api/v1/administrativos` |
| `estante-service` | 8086 | `estante_db` | `/api/v1/estantes` |
| `prestamo-service` | 8087 | `prestamo_db` | `/api/v1/prestamos` |
| `auditoria-service` | 8088 | `auditoria_db` | `/api/v1/auditorias` |
| `registro-archivado-service` | 8089 | `registro_archivado_db` | `/api/v1/registros-archivados` |
| `registro-ingreso-archivo-service` | 8090 | `registro_ingreso_archivo_db` | `/api/v1/registros-ingreso-archivo` |

## Puertos de Bases de Datos

| Contenedor | Puerto local | Puerto interno |
| --- | ---: | ---: |
| `paciente-db` | 5433 | 5432 |
| `ficha-clinica-db` | 5434 | 5432 |
| `reserva-atencion-db` | 5435 | 5432 |
| `expediente-hospitalizacion-db` | 5436 | 5432 |
| `administrativo-db` | 5437 | 5432 |
| `estante-db` | 5438 | 5432 |
| `prestamo-db` | 5439 | 5432 |
| `auditoria-db` | 5440 | 5432 |
| `registro-archivado-db` | 5441 | 5432 |
| `registro-ingreso-archivo-db` | 5442 | 5432 |

Credenciales por defecto:

```text
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

## Seguridad

Se implemento seguridad basica simple con Spring Security:

- `GET /api/v1/**` queda publico para pruebas y comunicacion entre microservicios.
- `POST`, `PUT` y `DELETE` requieren HTTP Basic.

Credenciales por defecto:

```text
Usuario: admin
Password: admin123
```

Ejemplo:

```bash
curl -u admin:admin123 -X POST http://localhost:8081/api/v1/pacientes \
  -H "Content-Type: application/json" \
  -d '{"numRut":"11111111-1","pnombre":"Ana","snombre":"Maria","papellido":"Perez","sapellido":"Soto","fechaNaci":"1990-01-10","direccion":"Av. Central 123","nacionalidad":"Chilena"}'
```

## Comandos Docker

Levantar todo:

```bash
docker compose up --build -d
```

Ver contenedores:

```bash
docker ps
```

Ver logs:

```bash
docker compose logs -f
```

Detener contenedores:

```bash
docker compose down
```

Detener y eliminar volumenes:

```bash
docker compose down -v
```

Levantar con pgAdmin:

```bash
docker compose --profile pgadmin up --build -d
```

pgAdmin queda disponible en:

```text
http://localhost:5050
admin@gestionarchivo.cl / admin123
```

## Comunicacion REST Entre Servicios

Cada servicio conserva solo los identificadores externos en su base de datos. Las validaciones cruzadas se hacen por REST.

Ejemplos obligatorios implementados:

| Servicio origen | Consulta a |
| --- | --- |
| `ficha-clinica-service` | `paciente-service` y `estante-service` |
| `reserva-atencion-service` | `paciente-service` |
| `expediente-hospitalizacion-service` | `reserva-atencion-service` |
| `prestamo-service` | `administrativo-service` y `ficha-clinica-service` |
| `auditoria-service` | `administrativo-service` y `ficha-clinica-service` |
| `registro-archivado-service` | `administrativo-service`, `ficha-clinica-service` y `expediente-hospitalizacion-service` |
| `registro-ingreso-archivo-service` | `administrativo-service` y `expediente-hospitalizacion-service` |

Ejemplos de URLs internas Docker:

```text
http://paciente-service:8081/api/v1/pacientes/{id}
http://ficha-clinica-service:8082/api/v1/fichas-clinicas/{folio}
http://estante-service:8086/api/v1/estantes/{id}
```

## Prueba Rapida de Flujo

Crear paciente:

```bash
curl -u admin:admin123 -X POST http://localhost:8081/api/v1/pacientes \
  -H "Content-Type: application/json" \
  -d '{"numRut":"11111111-1","pnombre":"Ana","snombre":"Maria","papellido":"Perez","sapellido":"Soto","fechaNaci":"1990-01-10","direccion":"Av. Central 123","nacionalidad":"Chilena"}'
```

Crear estante:

```bash
curl -u admin:admin123 -X POST http://localhost:8086/api/v1/estantes \
  -H "Content-Type: application/json" \
  -d '{"numEstante":1,"numBodega":1,"ubicacion":"Bodega A"}'
```

Crear ficha clinica. Este request valida por REST que existan el paciente `1` y el estante `1`:

```bash
curl -u admin:admin123 -X POST http://localhost:8082/api/v1/fichas-clinicas \
  -H "Content-Type: application/json" \
  -d '{"folioFicha":"FC-001","fechaCreacion":"2026-05-17","idPaciente":1,"idEstante":1}'
```

Crear reserva de atencion. Este request valida el paciente:

```bash
curl -u admin:admin123 -X POST http://localhost:8083/api/v1/reservas-atencion \
  -H "Content-Type: application/json" \
  -d '{"fechaReservada":"2026-06-01","horaReservada":"10:30:00","especialidad":"Cardiologia","profesional":"Dra. Rojas","idPaciente":1}'
```

Crear expediente. Este request valida la reserva `1`:

```bash
curl -u admin:admin123 -X POST http://localhost:8084/api/v1/expedientes-hospitalizacion \
  -H "Content-Type: application/json" \
  -d '{"codExpediente":"EXP-001","rutPaciente":"11111111-1","digitalizacion":true,"idReservaAtencion":1}'
```

Crear administrativo:

```bash
curl -u admin:admin123 -X POST http://localhost:8085/api/v1/administrativos \
  -H "Content-Type: application/json" \
  -d '{"rut":"22222222-2","pnombre":"Carlos","snombre":"Andres","papellido":"Diaz","sapellido":"Mora","fechaNaci":"1985-04-20","fechaContrato":"2020-03-01","email":"carlos.diaz@hospital.cl","cargo":"Archivista"}'
```

Crear prestamo. Este request valida administrativo y ficha clinica:

```bash
curl -u admin:admin123 -X POST http://localhost:8087/api/v1/prestamos \
  -H "Content-Type: application/json" \
  -d '{"idAdministrativo":1,"folioFicha":"FC-001","fechaPrestamo":"2026-05-17","fechaDevolucion":null,"estado":"PRESTADO"}'
```

Crear auditoria:

```bash
curl -u admin:admin123 -X POST http://localhost:8088/api/v1/auditorias \
  -H "Content-Type: application/json" \
  -d '{"idAdministrativo":1,"folioFicha":"FC-001","fechaAuditoria":"2026-05-17T12:00:00","accion":"REVISION","detalle":"Revision documental"}'
```

Crear registro archivado:

```bash
curl -u admin:admin123 -X POST http://localhost:8089/api/v1/registros-archivados \
  -H "Content-Type: application/json" \
  -d '{"folioFicha":"FC-001","idAdministrativo":1,"idExpediente":1,"fechaArchivado":"2026-05-17T13:00:00","observacion":"Archivado inicial"}'
```

Crear registro de ingreso a archivo:

```bash
curl -u admin:admin123 -X POST http://localhost:8090/api/v1/registros-ingreso-archivo \
  -H "Content-Type: application/json" \
  -d '{"idExpediente":1,"idAdministrativo":1,"fechaIngreso":"2026-05-17T14:00:00","observacion":"Ingreso a archivo central"}'
```

## Manejo de Errores

Cada microservicio maneja errores con `GlobalExceptionHandler`, `ResourceNotFoundException`, `BadRequestException` y `ErrorResponseDTO`.

Formato:

```json
{
  "timestamp": "2026-05-17T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Recurso no encontrado",
  "path": "/api/v1/..."
}
```

## Explicacion Para Defensa Tecnica

La separacion se realizo por dominio funcional. Cada microservicio es dueno de sus datos y no comparte tablas ni entidades JPA con otros servicios. Las relaciones del monolito, como `FichaClinica -> Paciente` o `Prestamo -> Administrativo`, se transformaron en referencias por identificador (`idPaciente`, `folioFicha`, `idAdministrativo`) y validaciones REST.

Esto permite defender que:

- Hay independencia de despliegue por servicio.
- Hay independencia de persistencia por base de datos.
- No existen joins ni `@ManyToOne` entre servicios.
- La integracion se hace mediante contratos HTTP.
- La seguridad basica protege operaciones de escritura.
- Los errores tienen formato uniforme.
- Los logs permiten rastrear creaciones, busquedas, actualizaciones, eliminaciones y llamadas REST.

La solucion es simple a proposito: evita infraestructura adicional no solicitada y se enfoca en demostrar microservicios, persistencia independiente, REST, validaciones, seguridad, excepciones y Docker.
