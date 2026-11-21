# 🗄️ Hardware Nexus - Esquema Oficial de Base de Datos (EV3 - DSY1104)
# Conforme a la Rúbrica: USUARIO, BOLETA, DETALLE_BOLETA, PRODUCTO, CATEGORIA

DROP DATABASE IF EXISTS hardware_nexus_db;
CREATE DATABASE hardware_nexus_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hardware_nexus_db;

-- 1. Tabla: roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

INSERT INTO roles (nombre) VALUES 
('ADMIN'),
('VENDEDOR'),
('CLIENTE');

-- 2. Tabla: usuarios (Representa USUARIO de la rúbrica)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    rol_id BIGINT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. Tabla: categorias (Representa CATEGORIA de la rúbrica)
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    icono VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- 4. Tabla: productos (Representa PRODUCTO de la rúbrica)
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen_url VARCHAR(500),
    categoria_id BIGINT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 5. Tabla: boletas (Representa BOLETA de la rúbrica)
CREATE TABLE boletas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_boleta VARCHAR(50) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12,2) NOT NULL,
    iva DECIMAL(12,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'COMPLETADO',
    CONSTRAINT fk_boleta_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6. Tabla: detalle_boletas (Representa DETALLE_BOLETA de la rúbrica)
CREATE TABLE detalle_boletas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    boleta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_detalle_boleta FOREIGN KEY (boleta_id) REFERENCES boletas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
