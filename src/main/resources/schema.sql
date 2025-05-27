-- Таблица обуви
CREATE TABLE shoes (
    id SERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    size REAL NOT NULL,
    color VARCHAR(30) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    in_stock BOOLEAN DEFAULT TRUE
);

-- Таблица сотрудников
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    personal_data JSONB NOT NULL,  -- Используем JSONB
    position VARCHAR(50) NOT NULL,
    access_level INT NOT NULL CHECK (access_level BETWEEN 1 AND 4),
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL
);

-- Таблица клиентов
CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

-- Таблица продаж
CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    shoe_id INT REFERENCES shoes(id),
    employee_id INT REFERENCES employees(id),
    client_id INT REFERENCES clients(id),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantity INT NOT NULL CHECK (quantity > 0),
    total_price NUMERIC(10,2) NOT NULL
);

-- Таблица прайс-листа
CREATE TABLE price_list (
    id SERIAL PRIMARY KEY,
    shoe_id INT REFERENCES shoes(id),
    current_price NUMERIC(10,2) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE
);