# Project Specifications
## Project: Wine Cave Inventory Management System (Wineventory)

### 1. **Project Overview**
The **Wineventory** project aims to develop a relational database system that manages a wine cave's inventory, allowing users to track wine stocks, handle new acquisitions, and manage wine removals. The goal is to create an efficient, user-friendly system tailored to the needs of private or professional wine cave owners.

---

### 2. **Project Objectives**
The objectives of this project are to:
- Develop a relational database to store information about wines, suppliers, and inventory movements.
- Enable efficient stock management: view current levels, handle wine removal, and receive low-stock alerts.
- Track the history of inventory changes, including added and removed wines, with reasons (e.g., sold, gifted, consumed).
- Provide a clear and responsive user interface for managing daily tasks.

---

### 3. **Scope of Work**
The system will allow users to:
- Add new wines with detailed information such as name, varietal, vintage, price, and supplier.
- Remove wines from inventory with reasons (sold, gifted, consumed, etc.).
- Monitor real-time stock levels and receive alerts for low stock.
- View the full history of added and removed wines.
- To be able to view the stock levels and movements of all products across other stores within the chain.
- To be able to see all customer purchases in order to send them offers or predict their visit frequency, allowing for advanced stock preparation.

---

### 4. **Functional Specifications**
Key features of the application:
- **Wine Management:** Users can add new wines with specific details (name, varietal, vintage, price, supplier) and remove wines from inventory with reasons.
- **Stock Monitoring:** The system provides real-time views of available stock, updating as wines are added or removed.
- **Low Stock Alerts:** Alerts are generated when stock levels drop below a predefined threshold.
- **History Tracking:** A complete record of added and removed wines, including dates and reasons for changes.
- **Supplier Management:** Manage supplier details (name, contact info) for reference.

---

### 5. **Non-Functional Specifications**
- **Database:** PostgreSQL will be used for relational data management.
- **Performance:** The system must handle hundreds of wine entries without performance degradation.
- **Security:** Access to data should be secure, with the potential for role-based access control.
- **User Interface:** Simple and intuitive UI, either desktop-based (JavaFX) or web-based (HTML/CSS/Bootstrap).
- **Portability:** The system should be deployable on both local and remote servers.

---

### 6. **Constraints**
- **Required Technologies:** PostgreSQL for database management.
- **Recommended Languages:** Java for backend (using JDBC for database connections). JavaFX for desktop UI or HTML/CSS for web-based UI.
- **Scalability:** The system must be designed to allow future expansions, such as new features or an increase in data volume.

---

### 7. **Deliverables**
- **Project Specifications** (this document) in PDF format.
- **Conceptual Database Model** (UML schema).
- **Relational Database Model** based on the conceptual model.
- **SQL Scripts** for creating tables and integrity constraints.
- **Application Code** for the inventory management system.
- **User Documentation** for end users.
- **Installation Guide** for setting up the database and application.

---

### 8. **Timeline**
- **Project Specifications Submission:** October 13, 2024
- **Database Modeling:** TBD
- **Application Development:** To be carried out in parallel with database modeling.
- **Final Submission and Presentation:** January 24, 2025

---

### 9. **Responsibilities**
- Team members are each responsible for different aspects of the project (e.g., database design, UI development).
- Collaboration between team members is critical to ensure smooth integration of all components.

---

### 10. **Possible Extensions**
- Mobile app integration for on-the-go stock management.
- Advanced supplier management features (order history, delivery tracking).
- Sales analysis and graphical reports for popular wine categories.

---

### 11. **Appendices**
- **UML Diagram** of the conceptual schema.
- **SQL Scripts** for creating tables and setting integrity constraints.
- **Technical Documentation** explaining design decisions.
