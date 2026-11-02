# Mi Bienestar

Plataforma de bienestar universitario de la Universidad Icesi: rutinas, progreso, actividades y espacios deportivos.

| Carpeta | Contenido | Tecnologia |
|---|---|---|
| `backend/` | API REST | Java 17, Spring Boot 3.5, PostgreSQL |
| `frontend/` | Aplicacion web | React 19, TypeScript, Vite, Material UI |

## Puesta en marcha

Requisitos: Java 17, Node 20 o superior y una base de datos PostgreSQL.

```bash
cp .env.example .env   # luego llena los valores
```

```bash
cd backend && ./mvnw spring-boot:run   # http://localhost:8080/swagger-ui.html
```

```bash
cd frontend && npm install && npm run dev   # http://localhost:6573
```

Con Docker: `docker compose up --build` (aplicacion en http://localhost).

## Forma de trabajo

- `main` es la version estable y `dev` la rama de integracion.
- Cada historia sale de `dev` en una rama `feature/GMB-XX-descripcion`.
- Los commits siguen [Conventional Commits](https://www.conventionalcommits.org/es/): `feat(backend): agrega inscripciones`.
- Las credenciales van solo en `.env`. Nunca en el codigo.
- Los cambios de esquema se agregan como SQL en `backend/db/migrations/` y se aplican a mano en Supabase.
