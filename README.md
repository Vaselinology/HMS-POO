# Hotel Management System

![System Overview](welcome.png) 

## Table of Contents
- [Features](#-features)
- [Technologies](#-technologies)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Usage](#-usage)
- [Database Schema](#-database-schema)
- [Folder Structure](#-folder-structure)
- [Known Issues](#-known-issues)

## 🌟 Features

### Admin Modules
- **Room Management** - Add/edit/delete rooms
- **Employee Management** - Staff records and details
- **Customer Management** - Guest information and bookings
- **User Management** - System access control

### User Modules
- Room browsing and booking
- Booking history view

### Core Functionality
- Secure authentication
- Transaction-based operations
- Responsive Swing UI

## 💻 Technologies

**Backend:**
- Java 8+
- MySQL 5.7+
- JDBC for database connectivity

**Frontend:**
- Java Swing
- JCalendar for date pickers
- SwingX for enhanced components

## 📥 Installation

### Prerequisites
1. Java JDK 8 or later
2. MySQL Server 5.7+
3. MySQL Connector/J 8.0+

### Setup Steps
1. Clone the repository:
   ```
   [git clone https://github.com/yourusername/hotel-management-system.git](https://github.com/Vaselinology/HMS-POO/blob/main/README.md#-installation)
   ```
2. Import database:
   ```
   mysql -u root -p < database/HMS-DB.sql
   ```
3. Build and run:
   ```
   javac hms/WelcomeScreen.java
   java hms.WelcomeScreen
   ```
## ⚙️ Configuration

Edit src/hms/DatabaseConnection.java:
   ```
   public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hotelmanagementsystem";
    private static final String USER = "your_db_username";
    private static final String PASSWORD = "your_db_password";
    // ...
}
   ```
## 🖥️ Usage

### Admin Login
```
Launch application
Login with admin credentials (default: admin/admin123)
Access all management modules via dashboard
```
### User Flow
```
Register new account/log in
Browse available options
Make bookings
View booking history
```
## 🗃 Database Schema

### 1. `booking`
| Column     | Type        | Description                 |
|------------|-------------|-----------------------------|
| bookingid  | INT         | Primary Key, Auto Increment |
| userid     | INT         | Foreign Key → user.id       |
| roomid     | VARCHAR(10) | Foreign Key → room.roomnumber |
| checkin    | DATE        |                             |
| checkout   | DATE        |                             |

### 2. `customer`
| Column     | Type         | Description                |
|------------|--------------|----------------------------|
| bookingid  | INT          | Primary Key                |
| name       | VARCHAR(100) |                            |
| gender     | VARCHAR(10)  |                            |
| contact    | VARCHAR(15)  |                            |
| userid     | INT          | Foreign Key → user.id      |

### 3. `employee`
| Column     | Type         | Description                |
|------------|--------------|----------------------------|
| name       | VARCHAR(25)  |                            |
| age        | VARCHAR(10)  |                            |
| gender     | VARCHAR(15)  |                            |
| job        | VARCHAR(30)  |                            |
| salary     | VARCHAR(15)  |                            |
| phone      | VARCHAR(15)  |                            |
| email      | VARCHAR(40)  |                            |

### 4. `room`
| Column          | Type         | Description        |
|-----------------|--------------|--------------------|
| roomnumber      | VARCHAR(10)  | Primary Key        |
| availability    | VARCHAR(20)  | e.g., Available    |
| cleaning_status | VARCHAR(20)  | e.g., Clean/Dirty  |
| price           | VARCHAR(20)  | Room price         |
| bed_type        | VARCHAR(20)  | e.g., Single/Double|

### 5. `user`
| Column        | Type         | Description             |
|---------------|--------------|-------------------------|
| id            | INT          | Primary Key, Auto Increment |
| username      | VARCHAR(25)  |                         |
| password      | VARCHAR(25)  |                         |
| administrator | TINYINT(1)   | 1 = Admin, 0 = User     |

## 📂 Folder Structure
```
├───HMS
│   └───src
│       ├───hms
│       │   ├───admin (Admin management forms)
│       │   ├───user (User-facing forms)
│       │   ├───Login.java/signUp.java (Authentication)
│       │   └───WelcomeScreen.java
│       └───images
├───libraries
└───Database 
      ├───DB-Diagram.svg
      └───HMS-DB.sql
```
##🛑 Known Issues

- Password storage is not encrypted
- Limited input validation on some forms
- No backup/restore functionality
