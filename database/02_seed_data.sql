-- Datos semilla iniciales para pruebas y roles de evaluación
USE hardware_nexus_db;

-- 1. Usuarios con claves BCrypt (Hash para contraseña: 'Password123!')
-- Hash: $2a$10$e0MYzXyjpJS7Pd0RVvHwHeTmgVl3g1QcE79V1q0QhT66b7nO5uV6q
INSERT INTO usuarios (nombre, apellido, email, password, telefono, direccion, rol_id, activo) VALUES
('Administrador', 'Principal', 'admin@hardwarenexus.cl', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeTmgVl3g1QcE79V1q0QhT66b7nO5uV6q', '+56911112222', 'Av. Nexus 100, Santiago', 1, TRUE),
('Carlos', 'Ventas', 'vendedor@hardwarenexus.cl', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeTmgVl3g1QcE79V1q0QhT66b7nO5uV6q', '+56933334444', 'Av. Comercial 500, Santiago', 2, TRUE),
('Nicolas', 'Fuentes', 'cliente@hardwarenexus.cl', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeTmgVl3g1QcE79V1q0QhT66b7nO5uV6q', '+56955556666', 'Calle Gamer 404, Santiago', 3, TRUE);

-- 2. Categorías
INSERT INTO categorias (nombre, descripcion, icono, activo) VALUES
('Procesadores', 'CPUs Intel y AMD Ryzen para gaming y workstation', 'cpu', TRUE),
('Tarjetas Gráficas', 'GPUs NVIDIA GeForce RTX y AMD Radeon', 'gpu', TRUE),
('Placas Madre', 'Motherboards para chipsets Intel y AM5/AM4', 'motherboard', TRUE),
('Memorias RAM', 'Módulos DDR4 y DDR5 de alto rendimiento', 'ram', TRUE),
('Almacenamiento', 'Discos de estado sólido SSD M.2 NVMe y SATA', 'storage', TRUE);

-- 3. Productos del Catálogo Hardware Nexus
INSERT INTO productos (nombre, descripcion, precio, stock, imagen_url, categoria_id, activo) VALUES
('AMD Ryzen 7 7800X3D', 'El mejor procesador gaming con tecnología 3D V-Cache, 8 núcleos y 16 hilos hasta 5.0 GHz', 429990.00, 15, 'https://images.unsplash.com/photo-1555618568-d6215712f5a6?w=600', 1, TRUE),
('Intel Core i7 14700K', '20 núcleos (8P + 12E) hasta 5.6 GHz, ideal para gaming pesado y renderizado multitarea', 459990.00, 10, 'https://images.unsplash.com/photo-1555618568-d6215712f5a6?w=600', 1, TRUE),
('NVIDIA GeForce RTX 4070 Super 12GB', 'Arquitectura Ada Lovelace con DLSS 3.5 y Ray Tracing de última generación', 679990.00, 8, 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=600', 2, TRUE),
('ASUS ROG Strix B650-A Gaming WiFi', 'Placa madre AM5 con PCIe 5.0, disipadores VRM robustos y conectividad WiFi 6E', 249990.00, 12, 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600', 3, TRUE),
('Corsair Vengeance RGB DDR5 32GB (2x16GB) 6000MHz', 'Kit de memoria optimizado para AMD EXPO e Intel XMP con iluminación RGB dinámica', 139990.00, 20, 'https://images.unsplash.com/photo-1562976540-1502c2145186?w=600', 4, TRUE),
('Kingston Fury Renegade 1TB M.2 NVMe', 'Velocidades extremas de lectura hasta 7300 MB/s con disipador térmico de aluminio', 99990.00, 25, 'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=600', 5, TRUE);
