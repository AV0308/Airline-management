# ✈ Airline Management System

A Java Swing desktop application for managing airline reservations, passengers, flights, payments, and cancellations — backed by MySQL.

---

## 📁 Project Structure

```
AirlineManagementSystem/
├── database_setup.sql                          ← Run this first
├── README.md
└── src/
    └── airline/
        └── management/
            └── system/
                ├── DBConnection.java           ← MySQL singleton connector
                ├── Login.java                  ← Login screen
                ├── Mainframe.java              ← Main window with menu bar
                ├── Flight_Info.java            ← Add flight details
                ├── Add_Customer.java           ← Register a passenger
                ├── Journey_Details.java        ← Add reservation / journey
                ├── Payment_Details.java        ← Record payment
                ├── Cancel.java                 ← Cancel a reservation
                ├── Passenger_List.java         ← View / search all passengers
                ├── Flight_List.java            ← View / search / delete flights
                └── Print_Ticket.java           ← Fetch & print boarding pass
```

---

## 🛠 Requirements

| Component | Version |
|-----------|---------|
| Java JDK  | 8+      |
| NetBeans IDE | 8.2+ |
| MySQL     | 5.7 / 8.x |
| MySQL Connector/J | 8.x (`mysql-connector-j-8.x.jar`) |

---

## ⚙ Setup Steps

### 1. Create the Database
Open MySQL Workbench (or the CLI) and run:
```
mysql -u root -p < database_setup.sql
```
This creates the `airlinedb` database, all tables, and seeds sample data.

### 2. Configure the DB Password
Open `DBConnection.java` and set your MySQL root password:
```java
private static final String PASSWORD = "your_mysql_password";
```

### 3. Add the MySQL JDBC Driver
- Download `mysql-connector-j-8.x.jar` from https://dev.mysql.com/downloads/connector/j/
- In NetBeans: Right-click the project → Properties → Libraries → Add JAR/Folder → select the connector JAR.

### 4. Run the Project
- Set `Login.java` as the Main Class.
- Press **F6** to run.

### 5. Default Login Credentials
| Username | Password  |
|----------|-----------|
| admin    | admin123  |

---

## 🖥 Application Flow

```
Login
  └── Mainframe (Menu Bar)
        ├── AIRLINE SYSTEM
        │     ├── FLIGHT INFO          → Add new flights
        │     ├── ADD CUSTOMER DETAILS → Register passengers (auto PNR)
        │     ├── JOURNEY DETAILS      → Book a journey (auto Ticket ID)
        │     ├── PAYMENT DETAILS      → Record payment
        │     └── CANCELLATION         → Cancel a reservation
        ├── TICKET
        │     └── PRINT TICKET         → Fetch & print boarding pass
        ├── LIST
        │     ├── PASSENGER LIST       → Search & view all passengers
        │     └── FLIGHT LIST          → Search, view & delete flights
        └── MISC
              ├── LOGOUT
              └── EXIT
```

---

## 🗄 Database Schema

| Table          | Key Columns |
|----------------|-------------|
| `login`        | username (PK), password |
| `flight`       | f_code (PK), f_name, src, dst |
| `sector`       | flight_code (FK), capacity, class_code, class_name |
| `passenger`    | pnr_no (PK), name, address, nationality, gender, ph_no, passport_no, fl_code (FK) |
| `reservation`  | ticket_id (PK), pnr_no (FK), f_code (FK), jny_date, jny_time, src, dst |
| `payment`      | pnr_no (FK), ph_no, cheque_no, card_no, paid_amt, pay_date |
| `cancellation` | cancellation_no (PK), pnr_no, cancellation_date, fli_code |

---

## ✅ Features

- Secure login with DB authentication
- Auto-generated PNR numbers and Ticket IDs
- Full CRUD on flights and passengers
- Journey booking with date/time validation
- Multi-mode payment recording (Cash / Card / Cheque / UPI)
- One-click ticket cancellation with audit log
- Searchable passenger and flight lists (JTable)
- Formatted boarding pass with print support
