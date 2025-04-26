CREATE DATABASE IF NOT EXISTS zaib_autos_db;
USE zaib_autos_db;
show databases;
CREATE TABLE Suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(45) NOT NULL,
    supplier_contact VARCHAR(45),
    supplier_address TEXT
);
CREATE TABLE Products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(45),
    stock_quantity INT DEFAULT 0,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE SET NULL
);
CREATE TABLE Stock_Audits (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    product_id INT,
    system_stock INT NOT NULL DEFAULT 0,
    actual_stock INT NOT NULL DEFAULT 0,
    discrepancy INT NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
);
CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_number VARCHAR(20),
    customer_name VARCHAR(45) NOT NULL,
    customer_contact INT,
    total_credit INT NOT NULL DEFAULT 0,
    amount_paid INT NOT NULL DEFAULT 0,
    amount_remaining INT NOT NULL DEFAULT 0
);
CREATE TABLE Sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('Cash', 'Credit', 'Online') NOT NULL DEFAULT 'Cash',
    total_sale INT,
    total_profit INT,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE SET NULL
);
CREATE TABLE Sale_Items (
    sale_item_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    sale_id INT,
    product_id INT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (sale_id) REFERENCES Sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
);
CREATE TABLE Returns (
    return_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    sale_item_id INT,
    product_id INT,
    quantity INT NOT NULL DEFAULT 1,
    reason TEXT DEFAULT null,
    return_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sale_item_id) REFERENCES Sale_Items(sale_item_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
);
CREATE TABLE Expenses (
    expense_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount INT NOT NULL DEFAULT 0,
    description TEXT
);
CREATE TABLE Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
	FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE CASCADE
);
CREATE TABLE Roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    role_name VARCHAR(45) NOT NULL
);
DESC sales;



