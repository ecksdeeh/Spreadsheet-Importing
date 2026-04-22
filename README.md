# Spreadsheet Importer

A Java-based Spring Boot web application that allows users to upload an Excel (`.xlsx`) file through a browser and import the data into a MySQL database.

---

## Requirements

### Option A — Standard (Java + Maven)
- [Java JDK 21+](https://adoptium.net/) — download the Eclipse Adoptium JDK
- [MySQL 5.7+](https://dev.mysql.com/downloads/installer/)
- Maven — either install separately or use VS Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

### Option B — Docker (Recommended)
- [Rancher Desktop](https://rancherdesktop.io/) — select **dockerd (moby)** as the container engine during setup
- [MySQL 5.7+](https://dev.mysql.com/downloads/installer/) — your existing local MySQL instance is used

> **Note:** Docker Desktop can also be used in place of Rancher Desktop if it installs successfully on your machine.

---

## Database Setup

Run the following in MySQL Workbench or any MySQL client:

```sql
CREATE DATABASE spreadsheet_importer;
USE spreadsheet_importer;

CREATE TABLE IF NOT EXISTS avb_closeout (
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

### Standard Setup
1. Copy the example properties file:
```
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

2. Open `src/main/resources/application.properties` and fill in your credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spreadsheet_importer
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

### Docker Setup
Create a `.env` file in the project root:
```
DB_NAME=spreadsheet_importer
DB_USER=root
DB_PASSWORD=your_password_here
```

> The Docker setup uses `host.docker.internal` to connect to your local MySQL instance. No separate database container is needed.

---

## Running the Application

### Option A — Standard (Java + Maven)

**Using Maven directly:**
```bash
mvn spring-boot:run
```

**Using VS Code:**
1. Open the project folder in VS Code
2. Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-boot-dev-pack)
3. Run in the terminal:
```bash
mvn spring-boot:run
```

Open your browser and go to `http://localhost:8080`

---

### Option B — Docker

1. Make sure Rancher Desktop is open and fully started (green status)

2. Make sure your `.env` file is created (see Configuration above)

3. In the project root, run:
```bash
docker compose up --build
```

4. Open your browser and go to `http://localhost:8080`

**Useful Docker commands:**
```bash
# Stop the container
docker compose down

# Rebuild after code changes
docker compose up --build

# View live logs
docker compose logs -f
```

---

## Usage

1. Start the application using either method above
2. Open your browser and navigate to `http://localhost:8080`
3. Click **Choose File** and select an `.xlsx` spreadsheet
4. Click **Import**
5. The page will display one of the following:
   - ✅ Green banner — all records imported successfully
   - ⚠️ Yellow banner — some records imported with row-level errors listed
   - ❌ Red banner — import failed with error details

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
- Headers may be split across two consecutive rows (merged cell format is supported)

---

## Troubleshooting

**`javac` not recognized**
Add the JDK bin directory to your system PATH. In PowerShell as Administrator:
```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot", "Machine")
[System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot\bin", "Machine")
```
Restart your terminal after running these commands.

**`mvn` not recognized**
Use the Maven binary bundled with the VS Code Extension Pack for Java:
```powershell
& "C:\Users\YOUR_USERNAME\.vscode\extensions\vscjava.vscode-maven-0.45.2\maven\bin\mvn.cmd" spring-boot:run
```

**`docker compose` not recognized**
Make sure Rancher Desktop is open and fully started before running Docker commands. If using an older Docker installation, try `docker-compose` (with hyphen) instead.

**`no main manifest attribute, in app.jar`**
The Spring Boot Maven plugin is missing from `pom.xml`. Add the following inside `<project>`:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```
Then rebuild with `docker compose up --build`.

**`Failed to obtain JDBC Connection`**
- Confirm MySQL is running
- Verify the database and table exist in MySQL Workbench
- Check that your credentials in `application.properties` or `.env` are correct

---

## Project Structure

```
spreadsheet-importer/
├── src/
│   └── main/
│       ├── java/importer/
│       │   ├── SpreadsheetImporterApplication.java
│       │   ├── UploadController.java
│       │   ├── ExcelService.java
│       │   ├── DatabaseService.java
│       │   └── ImportResult.java
│       └── resources/
│           ├── templates/
│           │   └── index.html
│           ├── application.properties         ← not committed, create from .example
│           └── application.properties.example
├── database.sql
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env                                       ← not committed, create manually
├── .gitignore
└── README.md
```