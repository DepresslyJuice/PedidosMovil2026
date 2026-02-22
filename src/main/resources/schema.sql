-- Crear base de datos
CREATE DATABASE pedidomovil;

-- Conectar a la base de datos
\c pedidomovil;

-- Tabla users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Tabla productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DOUBLE PRECISION NOT NULL,
    stock INTEGER DEFAULT 0,
    descripcion TEXT
);

-- Tabla pedidos
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    direccion VARCHAR(500) NOT NULL,
    detalle_pedido TEXT NOT NULL,
    tipo_pago VARCHAR(50) NOT NULL,
    foto_url VARCHAR(500),
    latitud DOUBLE PRECISION NOT NULL,
    longitud DOUBLE PRECISION NOT NULL,
    fecha TIMESTAMP,
    estado VARCHAR(50) NOT NULL,
    total DOUBLE PRECISION NOT NULL,
    cliente_id BIGINT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES users(id)
);

-- Tabla intermedia pedido_productos
CREATE TABLE pedido_productos (
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    PRIMARY KEY (pedido_id, producto_id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Datos de ejemplo
INSERT INTO users (username, password, email, role) VALUES 
('cliente1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'cliente1@example.com', 'CLIENTE'),
('vendedor1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'vendedor1@example.com', 'VENDEDOR');

INSERT INTO productos (nombre, precio, stock, descripcion) VALUES 
('Laptop HP', 899.99, 15, 'Laptop HP 15.6 pulgadas, 8GB RAM, 256GB SSD'),
('Mouse Logitech', 25.50, 50, 'Mouse inalámbrico Logitech M185'),
('Teclado Mecánico', 75.00, 30, 'Teclado mecánico RGB retroiluminado');
