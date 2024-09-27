# Wine Cave Inventory Management System

## Project Overview

This project involves building a relational database system to manage the inventory of a wine cave (cellar), where a user can track different types of wines, their quantities, suppliers, and sales transactions. The goal is to create a user-friendly system to keep track of wine stock, add new acquisitions, and manage sales, ensuring that the wine cave always has the right supply on hand.

---

## Table of Contents

1. [Project Description](#project-description)
2. [Requirements](#requirements)
3. [ER Model](#er-model)
4. [Relational Model](#relational-model)
5. [SQL Queries](#sql-queries)
6. [Features](#features)
7. [Technology Stack](#technology-stack)
8. [Known Bugs](#known-bugs)
9. [Conclusion](#conclusion)

---

## Project Description

The **Wine Cave Inventory Management System** is designed to assist wine cave owners in managing their stock efficiently. It maintains details about different wines, including their names, varietals, vintage, price, available stock, and suppliers. The system allows users to:
- Add new wines to the inventory.
- Update details of existing wines.
- Monitor stock levels and receive low-stock alerts.
- Track sales and generate detailed reports on the wine inventory.

---

## Requirements

### Functional Requirements
1. Add, update, or remove wines.
2. Track stock levels and receive notifications when stock is low.
3. Record sales transactions with customer details.
4. View detailed information about each wine (e.g., varietal, vintage, price, supplier).
5. Generate inventory reports (total stock, low-stock wines, most popular wines).

### Data Requirements
- Wine details (name, varietal, vintage, price, supplier)
- Supplier details (name, contact information)
- Stock details (quantity, reorder level)
- Sales details (date, customer, quantity sold)

---

## ER Model

The ER model for the Wine Cave Inventory System includes the following entities:
- **Wine** (WineID, Name, Varietal, Vintage, Price)
- **Supplier** (SupplierID, Name, Contact)
- **Stock** (StockID, WineID, Quantity, ReorderLevel)
- **Sale** (SaleID, Date, WineID, Quantity, CustomerName)

---

## Relational Model

Based on the ER model, the relational schema is as follows:

```sql
CREATE TABLE Wine (
    WineID SERIAL PRIMARY KEY,
    Name VARCHAR(255),
    Varietal VARCHAR(100),
    Vintage INT,
    Price DECIMAL(10, 2)
);

CREATE TABLE Supplier (
    SupplierID SERIAL PRIMARY KEY,
    Name VARCHAR(255),
    Contact VARCHAR(100)
);

CREATE TABLE Stock (
    StockID SERIAL PRIMARY KEY,
    WineID INT REFERENCES Wine(WineID),
    Quantity INT,
    ReorderLevel INT
);

CREATE TABLE Sale (
    SaleID SERIAL PRIMARY KEY,
    Date DATE,
    WineID INT REFERENCES Wine(WineID),
    Quantity INT,
    CustomerName VARCHAR(255)
);
```
---

## Features

### 1. **Add New Wines**
Users can add new wines to the inventory by entering details such as the wine's name, varietal, vintage, price, and supplier. This allows the system to keep track of all the different wines in the cave.

### 2. **Track Wine Stock**
The system displays real-time stock levels for each wine, making it easy for the user to see which wines are running low. The stock is updated automatically when new bottles are added or sales are recorded.

### 3. **Record Sales**
Sales transactions can be recorded in the system by selecting the wine sold, entering the quantity, and providing customer details. The stock levels are automatically updated after every sale.

### 4. **Low Stock Alerts**
The system generates alerts when the quantity of a wine falls below the designated reorder level, allowing the user to restock before running out.

### 5. **Generate Inventory Reports**
Users can generate inventory reports that show the total number of bottles in stock, wines that are running low, and sales trends over time. This provides a clear overview of the cave’s inventory.

---

## Technology Stack

- **Database:** PostgreSQL for managing and storing data.
- **Backend:** Java with JDBC for connecting to the database.
- **Frontend:** 
  - **Option 1:** Desktop application using **JavaFX** for a rich, responsive user interface.
  - **Option 2:** Web application using **HTML/CSS** and **Bootstrap** for a lightweight and mobile-friendly interface.

---

## Known Bugs


---

## Conclusion

The **Wine Cave Inventory Management System** is an efficient solution for managing the inventory of a wine cellar. It tracks the availability of each wine, records sales, and helps manage suppliers, ensuring that the user is always aware of the stock levels and can avoid running out of any wine. The system's ability to generate inventory reports and low-stock notifications ensures that the cave is always well-supplied with its most popular wines.

---

## Annexes

### 1. **Installation Guide**
To install the Wine Cave Inventory Management System:
- Install **PostgreSQL** and set up the database using the provided SQL script.
- Configure the connection settings in the backend code to connect to the database.
- For desktop users, run the **JavaFX** application by executing the main Java class.
- For web users, deploy the web application on a local or remote server.

### 2. **User Guide**
Once installed, the system offers the following options:
- **Add New Wines:** Navigate to the "Add Wine" section and fill out the wine's details.
- **View Stock Levels:** Go to the "Inventory" section to see real-time data on wine quantities.
- **Record Sales:** Enter the "Sales" section, select the wine sold, enter the quantity, and provide customer details.
- **Generate Reports:** Access the "Reports" section to view detailed information about stock levels, low-stock alerts, and sales.

---

## Future Improvements

- **Enhanced Reporting:** Adding more detailed analytics on sales trends and popular wine categories.
- **Supplier Management:** Creating more robust supplier management features, such as contact history and automatic reorder options.
- **Mobile Version:** Building a mobile-friendly version of the app for wine cave owners on the go, allowing for quick stock updates and sales entries via smartphones.
