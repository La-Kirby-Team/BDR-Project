# Wine Cave Inventory Management System
# Wineventory

## Project Overview

This project involves building a relational database system to manage the inventory of a wine cave (cellar), where a user can track different types of wines, their quantities, suppliers, and transactions. The goal is to create a user-friendly system to keep track of wine stock, add new acquisitions, and manage the removal of wines from the inventory, ensuring that the wine cave always has the right supply on hand.

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
- Remove wines from the inventory with reasons (e.g., sold, gifted, damaged).
- View the history of added and removed wines.

---

## Requirements

### Functional Requirements
1. Add, update, or remove wines.
2. Track stock levels and receive notifications when stock is low.
3. Remove wines from the inventory with a reason for removal (e.g., sold, gifted, damaged).
4. View detailed information about each wine (e.g., varietal, vintage, price, supplier).
5. View history of added and removed wines (date, reason, quantity).

### Data Requirements
- Wine details (name, varietal, vintage, price, supplier)
- Supplier details (name, contact information)
- Stock details (quantity, reorder level)
- Wine removal details (date, reason for removal, quantity removed)
- Wine addition details (date, supplier, quantity added)

---

## ER Model

The ER model for the Wine Cave Inventory System includes the following entities:
- **Wine** (WineID, Name, Varietal, Vintage, Price)
- **Supplier** (SupplierID, Name, Contact)
- **Stock** (StockID, WineID, Quantity, ReorderLevel)
- **Removal** (RemovalID, Date, WineID, Quantity, Reason)
- **Addition** (AdditionID, Date, WineID, Quantity, SupplierID)

---

## Relational Model

Based on the ER model, the relational schema is as follows:

[Insert Schema Here]
---

## Features

### 1. **Add New Wines**
Users can add new wines to the inventory by entering details such as the wine's name, varietal, vintage, price, and supplier. This allows the system to keep track of all the different wines in the cave.

### 2. **Track Wine Stock**
The system displays real-time stock levels for each wine, making it easy for the user to see which wines are running low. The stock is updated automatically when new bottles are added or removed from the inventory.

### 3. **Remove Wines**
Users can remove wines from the inventory, specifying the reason for removal (e.g., sold, gifted, damaged). This helps track why wines were removed and ensures stock levels are accurately reflected.

### 4. **Low Stock Alerts**
The system generates alerts when the quantity of a wine falls below the designated reorder level, allowing the user to restock before running out.

### 5. **View History**
Users can view the history of added and removed wines. This includes details such as the date, quantity, supplier for added wines, and reason for removal for removed wines (e.g., sold, gifted, damaged). This provides a clear audit trail of changes to the wine cave's inventory.

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

The **Wine Cave Inventory Management System** is an efficient solution for managing the inventory of a wine cellar. It tracks the availability of each wine, allows for adding and removing wines with a detailed reason, and helps manage suppliers. The system also allows users to view the history of inventory changes, ensuring full transparency and control over the wine cave's operations.

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
- **Remove Wines:** Enter the "Remove Wine" section, select the wine to be removed, specify the quantity, and provide a reason for the removal (e.g., sold, gifted, damaged).
- **View History:** Access the "History" section to view a detailed record of wines added and removed, including dates, reasons for removal, and supplier information for added wines.

---

## Future Improvements

- **Enhanced Reporting:** Adding more detailed analytics on the history of wine removals and additions.
- **Supplier Management:** Creating more robust supplier management features, such as contact history and automatic reorder options.
- **Mobile Version:** Building a mobile-friendly version of the app for wine cave owners on the go, allowing for quick stock updates and wine removals via smartphones.
