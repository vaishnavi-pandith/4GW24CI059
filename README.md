# 4GW24CI059
# Contact Management System (Java)

The **Contact Management System** is a console-based Java application designed to efficiently manage personal or organizational contact information.  
It provides a **menu-driven interface** that allows users to perform all essential contact operations while ensuring **persistent storage using a file management system**.

This project demonstrates core Java programming concepts along with practical usage of **file handling**, **collections**, and **basic operating system interactions**.

---

## 📘 Project Overview

Managing contacts manually can be inefficient and error-prone.  
This application automates the process by allowing users to store, retrieve, update, and delete contact details in a structured and reliable manner.

All contact data is stored in a local file, ensuring that information remains available even after the program terminates and restarts.

---

## ✨ Key Features

- Menu-driven console interface for easy interaction  
- Add new contacts with complete details  
- View all saved contacts in a formatted table  
- Search contacts using:
  - Contact ID
  - Name
  - Phone number
  - Email address  
- Update existing contact information selectively  
- Delete contacts with user confirmation  
- Sort contacts alphabetically by name  
- **Automatic saving and loading of data using file storage**

---

## 🛠 Technologies and Concepts Used

- **Programming Language:** Java (JDK 17+)  
- **Core Java Concepts:**
  - Classes and Objects
  - Encapsulation
  - Exception Handling
  - Switch expressions
- **Data Structures:**
  - `ArrayList` for in-memory storage
- **File Management:**
  - Reading from and writing to text files
  - Persistent data storage using secondary memory
- **Operating System Concepts:**
  - Process execution
  - Memory management via JVM
  - File system interaction
  - Input/Output operations

---

## 📂 Project Structure


---

## ⚙️ How the File Management System Works

- On program startup:
  - The application checks for an existing contact database file
  - If found, contacts are loaded into memory
  - If not found, a new file is created automatically
- During execution:
  - Any add, update, or delete operation triggers an automatic save
- On program exit:
  - All data remains securely stored in the file

This approach ensures **data persistence** across multiple executions.

---

## 🚀 How to Compile and Run

### Prerequisites

- Java Development Kit (JDK) version 17 or higher installed
- Java properly configured in system PATH

Verify installation:
```bash
java -version
javac -version

