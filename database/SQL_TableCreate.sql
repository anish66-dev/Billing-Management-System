CREATE DATABASE billing_system;
USE billing_system;

CREATE TABLE category (	
	cate VARCHAR(100) PRIMARY KEY
);

CREATE TABLE products (
	id INT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
    descrip VARCHAR(255) default 'No Description',
	price DOUBLE NOT NULL,
	quantity INT NOT NULL,
	cate VARCHAR(100) NOT NULL, 
    FOREIGN KEY(cate) references category(cate)
    );

CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    quantity INT,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE coupons (
    coupon_code VARCHAR(20) PRIMARY KEY,
    discount_percent DECIMAL(5,2) NOT NULL,
    expiry_date DATE
);

CREATE TABLE bill_history (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0,
    final_amount DECIMAL(10,2) NOT NULL,
    purchase_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
