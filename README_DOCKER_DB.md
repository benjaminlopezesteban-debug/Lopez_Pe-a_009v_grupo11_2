# Docker para base de datos PostgreSQL

## REQUISITOS

- Docker Desktop instalado y corriendo.
- Proyecto abierto en VS Code.

## COMANDOS

Para levantar PostgreSQL y pgAdmin:

```powershell
docker compose up -d
```

Para verificar contenedores:

```powershell
docker ps
```

Para ver logs:

```powershell
docker compose logs -f
```

Para detener contenedores:

```powershell
docker compose down
```

Para borrar contenedores y volumen:

```powershell
docker compose down -v
```

Para correr Spring Boot en Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## PGADMIN

URL:

```text
http://localhost:8081
```

Login:

```text
Email: admin@gestionarchivo.cl
Password: admin123
```

Registrar servidor en pgAdmin:

```text
Name: gestion_archivo_db
Host: postgres
Port: 5432
Maintenance database: gestion_archivo
Username: admin
Password: admin123
```

Este proyecto usa Docker para levantar PostgreSQL. Spring Boot se conecta a PostgreSQL mediante `application.properties`. Las tablas se generan desde las entidades JPA usando `spring.jpa.hibernate.ddl-auto=update`.

La API Spring Boot queda configurada en `http://localhost:8080` para evitar conflicto con pgAdmin, que usa `http://localhost:8081`.
