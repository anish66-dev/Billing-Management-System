# Billing Management System

A Java-based billing application developed using Eclipse, Java Swing, and MySQL. The system manages products, categories, admin users, carts, and billing history, with data stored in a MySQL database. The project includes both SQL scripts and CSV files to help easily set up the database.

---

## Features
- Manage categories and products
- Admin authentication
- Add items to cart
- Apply coupons and calculate final amount
- View billing history
- MySQL database-backed storage
- Includes sample data through SQL and CSV

---

## Technologies Used
- Java (JDK 8 or later)
- Eclipse IDE
- JDBC and MySQL Connector
- MySQL Database
- CSV-based data import

---

## Project Structure
Billing-Management-System/
- ├── src/
- ├── lib/
- ├── database/
- │   ├── SQL_TableCreate.sql
- │   ├── Category.csv
- │   ├── Products.csv
- │   └── Admin.csv
- └── README.md


---

## Database Setup

### 1. Import the SQL file
Open MySQL Workbench or phpMyAdmin and run the file:

This creates the database and all required tables.

### 2. Import sample data
Upload or import the CSV files located in the `database` folder:
- Category.csv
- Products.csv
- Admin.csv

These contain example data used by the application.

---

## Running the Project
1. Open the project in Eclipse  
2. Make sure MySQL server is running  
3. Update your database connection settings inside the Java code:

```java
String url = "jdbc:mysql://localhost:3306/billing_system";
String user = "root";
String password = "your_mysql_password";
```
4. Run the main Java file

## Requirements
- Eclipse IDE
- Java JDK (8 or later)
- MySQL Server
- MySQL Connector/J (JAR file)

