# 💻 Hardware Nexus - Backend API (Evaluación 3 - DSY1104)

Backend oficial de la plataforma **Hardware Nexus**, desarrollado con **Java 21**, **Spring Boot 3.x / 4.x**, **Spring Security (RBAC con JWT)**, **MySQL** y documentación interactiva **Swagger/OpenAPI**.

---

## 🏛️ 1. Arquitectura y Roles del Sistema

El backend satisface los requerimientos de la Evaluación Parcial 3 de DSY1104, implementando control de acceso estricto según la rúbrica:

* **Administrador (`ADMIN`):** Acceso total al sistema (gestión de productos, categorías, pedidos, usuarios y métricas).
* **Vendedor (`VENDEDOR`):**
  * Visualización de lista y detalle de productos.
  * Visualización de lista y detalle de órdenes/boletas.
  * Restricción estricta: No tiene acceso a creación, edición ni administración general.
* **Cliente (`CLIENTE`):** Acceso al catálogo de la tienda, carrito de compras y consulta de sus propias boletas/órdenes.

---

## 🗄️ 2. Modelo de Base de Datos Relacional

Conforme a la figura y pauta oficial de evaluación:
* `USUARIO` (`usuarios` + `roles`)
* `CATEGORIA` (`categorias`)
* `PRODUCTO` (`productos`)
* `BOLETA` (`boletas`)
* `DETALLE_BOLETA` (`detalle_boletas`)

Los scripts de inicialización se encuentran en `./database/`:
- `01_schema_ev3.sql`: Definición de DDL, claves foráneas e integridad referencial.
- `02_seed_data.sql`: Datos semilla, catálogo de componentes y cuentas de prueba para cada rol.

---

## 🚀 3. Puesta en Marcha Local con Docker

Para levantar el servicio de base de datos MySQL 8:
```bash
docker compose up -d mysql-db
```

---

## 📌 4. Versionamiento de APIs
Todos los endpoints exponen rutas bajo el estándar versionado `/v1/...` exigido por la rúbrica:
* `/v1/auth/**`: Autenticación, login y registro (generación de Token JWT).
* `/v1/usuarios/**`: Gestión y consulta de perfiles y usuarios.
* `/v1/productos/**`: Catálogo e inventario.
* `/v1/boletas/**` / `/v1/pedidos/**`: Gestión y emisión de órdenes.
* `/swagger-ui/index.html`: Documentación interactiva de la API.
