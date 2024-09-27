# Inventory Management System for Board Games

## Project Overview

This project involves building a relational database system to manage an inventory of board games for a small gaming shop or a personal collection. The application will allow users to catalog games, track inventory levels, add new acquisitions, and manage sales transactions. The goal is to create a user-friendly system with robust backend support using PostgreSQL for the database and a simple web or desktop interface.

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

The **Board Game Inventory Management System** helps shop owners or board game enthusiasts track their collections efficiently. The system maintains data on board games, including their names, genres, price, available stock, and suppliers.

Users can:
- Add new board games to the inventory.
- Update existing inventory details.
- View current stock levels.
- Record sales of games.
- Generate inventory reports.

---

## Requirements

### Functional Requirements
1. Add, update, or remove board games.
2. Track stock levels and receive notifications when stock is low.
3. Record sales transactions with the customer's details.
4. View a game’s details (e.g., publisher, genre, price).
5. Generate inventory reports (total items, items below reorder threshold).

### Data Requirements
- Game details (name, genre, price, supplier)
- Supplier details (name, contact info)
- Stock details (quantity, reorder level)
- Sales details (date, customer, quantity sold)

---

## ER Model

The ER model for the Board Game Inventory System consists of the following entities:
- **Game** (GameID, Name, Genre, Price)
- **Supplier** (SupplierID, Name, Contact)
- **Stock** (StockID, GameID, Quantity, ReorderLevel)
- **Sale** (SaleID, Date, GameID, Quantity, CustomerName)

---

## Relational Model

Based on the ER model, the relational schema is as follows:

```sql
CREATE TABLE Game (
    GameID SERIAL PRIMARY KEY,
    Name VARCHAR(255),
    Genre VARCHAR(100),
    Price DECIMAL(10, 2)
);

CREATE TABLE Supplier (
    SupplierID SERIAL PRIMARY KEY,
    Name VARCHAR(255),
    Contact VARCHAR(100)
);

CREATE TABLE Stock (
    StockID SERIAL PRIMARY KEY,
    GameID INT REFERENCES Game(GameID),
    Quantity INT,
    ReorderLevel INT
);

CREATE TABLE Sale (
    SaleID SERIAL PRIMARY KEY,
    Date DATE,
    GameID INT REFERENCES Game(GameID),
    Quantity INT,
    CustomerName VARCHAR(255)
);
