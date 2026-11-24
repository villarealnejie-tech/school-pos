-- =====================================================
--  School POS - MySQL schema + sample data
--  Run in MySQL Workbench: File > Open SQL Script,
--  then click the lightning-bolt (Execute) button.
-- =====================================================

DROP DATABASE IF EXISTS school_pos;
CREATE DATABASE school_pos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE school_pos;

-- ---------- Users (cashiers / admins) ----------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,              -- SHA-256 hex
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('ADMIN','CASHIER') NOT NULL DEFAULT 'CASHIER',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- Categories ----------
CREATE TABLE categories (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE
);

-- ---------- Products ----------
CREATE TABLE products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    barcode     VARCHAR(50)  NULL UNIQUE,
    name        VARCHAR(120) NOT NULL,
    category_id INT          NULL,
    price       DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    stock       INT           NOT NULL DEFAULT 0 CHECK (stock >= 0),
    active      TINYINT(1)    NOT NULL DEFAULT 1,     -- 0 = "deleted" (kept for sales history)
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- ---------- Sales (one row per transaction) ----------
CREATE TABLE sales (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    sale_date     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id       INT           NOT NULL,
    total         DECIMAL(10,2) NOT NULL,
    cash_given    DECIMAL(10,2) NOT NULL,
    change_amount DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_sales_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ---------- Sale line items ----------
CREATE TABLE sale_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    sale_id      INT           NOT NULL,
    product_id   INT           NOT NULL,
    product_name VARCHAR(120)  NOT NULL,              -- snapshot of the name at time of sale
    quantity     INT           NOT NULL CHECK (quantity > 0),
    unit_price   DECIMAL(10,2) NOT NULL,
    subtotal     DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_items_sale    FOREIGN KEY (sale_id)    REFERENCES sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_sales_date ON sales(sale_date);
CREATE INDEX idx_products_name ON products(name);

-- =====================================================
--  Sample data
-- =====================================================

-- Default logins:  admin / admin123    cashier / cashier123
INSERT INTO users (username, password_hash, full_name, role) VALUES
 ('admin',   SHA2('admin123', 256),   'Administrator', 'ADMIN'),
 ('cashier', SHA2('cashier123', 256), 'Cashier One',   'CASHIER');

INSERT INTO categories (name) VALUES
 ('Snacks'), ('Drinks'), ('School Supplies'), ('Canteen Meals');

INSERT INTO products (barcode, name, category_id, price, stock) VALUES
 ('1001', 'Piattos',               1, 20.00, 50),
 ('1002', 'Chocolate Bar',         1, 15.00, 40),
 ('1003', 'Bottled Water 500ml',   2, 15.00, 100),
 ('1004', 'Iced Tea',              2, 25.00, 60),
 ('1005', 'Notebook',              3, 35.00, 80),
 ('1006', 'Ballpen (Blue)',        3, 10.00, 150),
 ('1007', 'Pencil',                3,  8.00, 120),
 ('1008', 'Yellow Pad Paper',      3, 30.00, 70),
 ('1009', 'Chicken Adobo w/ Rice', 4, 55.00, 30),
 ('1010', 'Fried Rice',            4, 25.00, 30);

SELECT 'school_pos database created successfully' AS status;
