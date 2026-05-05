# JWT en gestionArchivo

La autenticacion esta disponible en `/api/v1/auth/**`. El resto de endpoints, incluido `/api/v1/pacientes/**`, requiere un token JWT en el header `Authorization`.

## Credenciales de prueba

Las credenciales configuradas en `src/main/resources/application.properties` son:

```properties
app.auth.username=admin
app.auth.password=admin123
```

Puedes cambiarlas en ese archivo o sobrescribirlas con variables de entorno al ejecutar la aplicacion.

## Login en Postman

1. Levanta la aplicacion.
2. Crea una request `POST`.
3. Usa la URL:

```text
http://localhost:8081/api/v1/auth/login
```

4. En `Body -> raw -> JSON`, envia:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

5. La respuesta incluye un token:

```json
{
  "token": "...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "username": "admin",
  "valid": true
}
```

## Validar sesion/token

1. Crea una request `GET`.
2. Usa la URL:

```text
http://localhost:8081/api/v1/auth/validate
```

3. Agrega el header:

```text
Authorization: Bearer <token>
```

Si el token es valido, respondera con `valid: true`. Si falta, esta mal formado o expiro, respondera `401` con JSON de error.

## Probar endpoints protegidos

Para llamar pacientes, agrega el mismo header:

```text
Authorization: Bearer <token>
```

Ejemplo:

```text
GET http://localhost:8081/api/v1/pacientes
```

Sin token, la API respondera `401 Unauthorized`.

## Expiracion y secreto

Configuracion actual:

```properties
security.jwt.secret=gestionArchivo-super-secret-key-change-me-2026
security.jwt.issuer=gestionArchivo
security.jwt.expiration-ms=3600000
```

`security.jwt.expiration-ms=3600000` equivale a 1 hora. En produccion cambia `security.jwt.secret` por un valor largo y privado.
