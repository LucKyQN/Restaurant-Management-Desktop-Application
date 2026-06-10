# Restaurant Management System — Ngoi Do

A Java Desktop Application for managing restaurant operations including table reservations, order management, billing, staff management, and revenue reporting.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (SE) |
| UI Framework | Java Swing / AWT |
| Database | Microsoft SQL Server |
| DB Connectivity | JDBC (Microsoft JDBC Driver) |
| Build/IDE | IntelliJ IDEA |
| Web (Landing Page) | HTML5, CSS3, Vanilla JavaScript |

---

## Architecture

The project follows a **Layered Architecture** with the **DAO (Data Access Object) Pattern**:

```
Presentation Layer  →  GUI (Swing Forms)
Business Layer      →  Service
Data Access Layer   →  DAO (JDBC queries)
Data Model          →  Entity (POJO)
Database            →  Microsoft SQL Server
```

- **Entity**: Plain Java objects representing database tables.
- **DAO**: Handles all SQL queries and database interactions via JDBC.
- **Service**: Contains business logic, called by GUI forms.
- **GUI**: Swing-based forms rendered per user role after login.
- **connectDatabase**: Singleton `ConnectDB` class managing the JDBC connection.

---

## Features by Role

### Admin / Quan ly (Manager)
- Full dashboard with revenue overview
- Manage staff accounts (create, update, deactivate)
- Manage menu items and pricing tiers
- View and export revenue reports by date range
- Manage promotions and discount programs
- Configure restaurant settings

### Thu ngan (Cashier)
- View active tables and their orders
- Process payments and generate invoices
- Apply promotions/discounts to bills
- Print end-of-day summary report
- Manage customer information

### Le Tan (Receptionist)
- Create and manage table reservations (`PhieuDatBan`)
- View real-time table layout and availability
- Assign customers to tables
- Open table groups for a reservation

### Phuc vu (Waiter)
- View assigned tables
- Take orders and add items to table orders
- Update or cancel order items
- Notify kitchen of order status
- Request table transfer or table merge

---

## Setup Guide

### Prerequisites

- JDK 11 or higher
- Microsoft SQL Server (local instance on port `1433`)
- IntelliJ IDEA
- Microsoft JDBC Driver for SQL Server (included in `lib/`)

### 1. Database Setup

1. Open SQL Server Management Studio (SSMS).
2. Create a new database named `QuanLyNhaHang`.
3. Run the SQL script (located in the `script/` folder if available) to create tables and seed initial data.
4. Ensure SQL Server is running and the `sa` account is enabled with the correct password.

The default connection is configured in `ConnectDB.java`:

```
URL:      jdbc:sqlserver://localhost:1433;databaseName=QuanLyNhaHang
Username: sql_sever_username
Password: sql_server_password
```

> To change credentials, edit `src/connectDatabase/ConnectDB.java`.

### 2. Run in IntelliJ IDEA

1. Open IntelliJ IDEA and select **Open** > choose the project root folder.
2. Go to **File > Project Structure > Libraries** and confirm the JDBC driver in `lib/` is added.
3. Set the **SDK** to JDK 11+.
4. Open `src/GUI/FrmDangNhap.java`.
5. Run the `main` method in `FrmDangNhap` to launch the application.

---

## Project Structure

```
QuanLyDatBanNhaHang/
├── src/
│   ├── Entity/             # POJO classes (BanAn, HoaDon, NhanVien, MonAn, KhachHang, ...)
│   ├── DAO/                # Data Access Objects (HoaDonDAO, NhanVienDAO, PhieuDatBanDAO, ...)
│   ├── GUI/                # Swing Forms (FrmDangNhap, FrmDashBoard, FrmThuNgan, FrmLeTan, ...)
│   ├── Model/              # Intermediate model classes
│   ├── Service/            # Business logic layer
│   ├── connectDatabase/    # ConnectDB (Singleton JDBC connection)
│   └── image/              # Images used in the UI
├── lib/                    # External JAR libraries (JDBC driver, etc.)
├── img/                    # Images for the web landing page
├── index.html              # Restaurant landing page
├── style.css               # Landing page styles
├── script.js               # Landing page scripts
├── config.properties       # Application configuration (restaurant name)
└── README.md
```

---

## Configuration

`config.properties` stores application-level settings:

```properties
res.name=Nha hang Ngoi Do
```

---

## Landing Page

A static web landing page (`index.html`) is included to showcase the restaurant's menu and allow customers to make reservations online. It is separate from the Desktop App and requires no backend to run — simply open in any browser.

---

## License

This project is developed for educational purposes.
