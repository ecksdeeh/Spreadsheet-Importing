# Prompts Used — Spreadsheet Importer (Java)

A curated list of prompts used throughout the development of the Spreadsheet Importer project, from environment setup to a fully working application.

---

## Phase 1 — Environment Setup

1. "I am setting up a Java Spring Boot project on a new Windows machine. The `javac` compiler is not recognized in the terminal despite Java being present on the system. Diagnose the issue and walk me through configuring the JDK PATH and JAVA_HOME environment variables correctly."

2. "I have installed the Eclipse Adoptium JDK but the compiler is still not recognized after restarting the terminal. Provide a PowerShell command to locate the exact installation directory and manually apply the correct PATH configuration with administrator privileges."

3. "My project uses Maven as its build system but `mvn` is not recognized as a command. I have the VS Code Extension Pack for Java installed — explain how to locate and invoke the bundled Maven binary without a separate Maven installation."

---

## Phase 2 — Project Structure & Dependencies

4. "Audit the following Spring Boot project files and identify every structural issue preventing successful compilation. The project contains missing package declarations, missing imports, incorrect file extensions, and an incomplete pom.xml. Provide fully corrected versions of every file."

5. "The pom.xml is missing a dependency required to read `.xlsx` files using Apache POI's WorkbookFactory. Identify the missing artifact and add it at the correct version to match the existing POI dependency."

---

## Phase 3 — Database & Configuration

6. "Provide step-by-step instructions for installing MySQL on Windows, creating a new schema and table using MySQL Workbench, and configuring a Spring Boot `application.properties` file to establish a valid JDBC connection to that database."

7. "The application is throwing a JDBC connection failure on every row during import. Walk me through verifying that MySQL is running, the target schema exists, the table has been created, and the connection string in application.properties is correctly formed."

---

## Phase 4 — Business Logic & Requirements Alignment

8. "Review this Spring Boot codebase against the following requirements document. The implementation has the wrong table name, missing database columns, only processes the first sheet, assumes the header is always on row 0, and has incomplete error reporting. Rewrite all affected files to fully satisfy the requirements."

9. "The Excel file being imported has a complex multi-row header structure where required column names are split across two consecutive rows and several metadata rows precede the actual data. Update the header detection logic to handle this format dynamically without hardcoding row positions."

10. "The application is reporting Imported: 0 with no errors after a successful file upload. Add targeted diagnostic logging to determine whether the header detection is failing, metadata rows are being misidentified as data, or rows are being silently skipped during processing."

---

## Phase 5 — UI & Error Reporting

11. "Implement a three-state feedback system in the Thymeleaf template: a green success banner when all records import cleanly, a yellow warning banner listing per-row errors when import partially succeeds, and a red failure banner when no records are imported at all."

12. "The Thymeleaf template is throwing a SpringEL evaluation error caused by use of the `!` negation operator, and a NullPointerException on initial page load before any file has been submitted. Fix the template syntax and add null-safety guards to all conditional expressions."

---

## Phase 6 — Deployment Readiness

13. "Generate a professional README.md for this project that covers prerequisites, database setup, configuration steps, how to run the application, the expected input file format, and the full project structure."

14. "Create a `.gitignore` file that prevents credentials, build artifacts, and IDE configuration files from being committed to the repository. Also provide an `application.properties.example` file with placeholder values so collaborators know what to configure."