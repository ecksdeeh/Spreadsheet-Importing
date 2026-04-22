# Spreadsheet Importer

A Java-based Spring Boot web application that allows users to upload an Excel (`.xlsx`) file through a browser and import the data into a MySQL database.

---

## Requirements

Before running this project, make sure you have the following installed:

- [Java JDK 21+](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi) — or use VS Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- [MySQL 5.7+](https://dev.mysql.com/downloads/installer/)

---

## Database Setup

1. Open MySQL Workbench (or any MySQL client) and run the following:

```sql
CREATE DATABASE spreadsheet_importer;
USE spreadsheet_importer;

CREATE TABLE avb_closeout (
  avb_sku        VARCHAR(255) PRIMARY KEY,
  closeout_id    VARCHAR(255) NOT NULL,
  linq_id        INT(10)      NOT NULL,
  avb_status     VARCHAR(255) NOT NULL,
  closeout_type  VARCHAR(255) NOT NULL,
  avb_brand      VARCHAR(255) NOT NULL
);
```

---

## Configuration

1. Copy the example properties file:

```
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

2. Open `src/main/resources/application.properties` and fill in your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spreadsheet_importer
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

---

## Running the Application

**Using Maven directly:**
```bash
mvn spring-boot:run
```

**Using VS Code:**
1. Open the project folder in VS Code
2. Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-boot-dev-pack)
3. Use the Spring Boot Dashboard in the sidebar to run the app, or run in the terminal:
```bash
mvn spring-boot:run
```

---

## Usage

1. Start the application
2. Open your browser and go to `http://localhost:8080`
3. Click **Choose File** and select an `.xlsx` spreadsheet
4. Click **Import**
5. The page will display how many records were imported, along with any row-level errors

---

## Input File Format

- Format: Excel (`.xlsx`)
- The file must contain the following column headers somewhere in the first 20 rows:

| Spreadsheet Column | Database Column |
|---|---|
| SKU | avb_sku |
| Brand | avb_brand |
| Product Linq ID | linq_id |
| CLOSEOUT_ID | closeout_id |
| CLOSEOUT_TYPE | closeout_type |
| Active Inactive Status-Default | avb_status |

- Column order does not matter
- Extra columns are ignored
- All sheets in the workbook are processed; sheets missing required headers are skipped

---

## Project Structure

```
src/
├── main/
│   ├── java/importer/
│   │   ├── SpreadsheetImporterApplication.java
│   │   ├── UploadController.java
│   │   ├── ExcelService.java
│   │   ├── DatabaseService.java
│   │   └── ImportResult.java
│   └── resources/
│       ├── templates/
│       │   └── index.html
│       ├── application.properties         ← not committed, create from .example
│       └── application.properties.example
```
