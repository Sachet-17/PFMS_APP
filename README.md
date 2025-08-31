
# Personal Finance Management System (PFMS)

The Personal Finance Management System (PFMS) is a Java-based application that helps users manage their finances by tracking income and expenses, setting budgets, and analyzing spending patterns with interactive charts. This project is built using JavaFX for the GUI, SQLite for the database, and advanced Java concepts such as Streams and ORM.

## **Folder Structure**

```plaintext
PFMS_APP/
├── src/
│   ├── database/
│   │   ├── DatabaseConnection.java
│   │   ├── DatabaseInitializer.java
│   ├── gui/
│   │   ├── PFMSApp.java
│   ├── main/
│   │   ├── Main.java
│   ├── managers/
│   │   ├── UserManager.java
│   │   ├── TransactionManager.java
│   │   ├── BudgetManager.java
│   ├── models/
│       ├── Transaction.java
├── executables/
│   ├── javafx-sdk-17.0.13/lib/
│   ├── sqlite-jdbc-3.47.1.0.jar
├──
```

---

## **Prerequisites**

- Java SE Development Kit (JDK) version 18 or above.
- Eclipse IDE (or any Java IDE supporting JavaFX).
- JavaFX SDK 17 or higher.
- SQLite JDBC driver.

---

## **Setup Instructions**

### 1. Clone the Repository
Copy the project folder to your local machine.

### 2. Import Project into Eclipse
1. Open Eclipse IDE.
2. Go to `File` → `Import` → `Existing Projects into Workspace`.
3. Select the project folder (`PFMS_APP`) and click `Finish`.

### 3. Configure Build Path
1. Right-click on the project in the Eclipse Project Explorer and select `Properties`.
2. Navigate to `Java Build Path` → `Libraries`.
3. Click `Add JARs` and select:
   - `sqlite-jdbc-3.47.1.0.jar` (located in the `executables` folder).
4. Click `Apply and Close`.

### 4. Add JavaFX SDK to Build Path
1. In the `Properties` menu, go to `Java Build Path` → `Libraries`.
2. Click `Add JARs` and select:
3. Select the `all the javaFX JARS file` folder (located in the `executables/javafx-sdk-17.0.13/lib` folder).

### 5. Configure VM Arguments
1. Right-click on the `Main.java` file and select `Run As` → `Run Configurations`.
2. Go to the `Arguments` tab.
3. Under `VM arguments`, add:
   ```
    --module-path "PATH" --add-modules javafx.controls,javafx.fxml 
   ```
  (Replace `PATH` with the absolute path to the `javafx-sdk-17.0.13` folder.)

---

## **How to Run**

1. Open `Main.java` located in the `src/main` package.
2. Right-click and select `Run As` → `Java Application`.
3. The application should launch successfully.

---

## **Features**

- **User Registration and Login**: Secure authentication system with SQLite.
- **Transaction Management**: Add, view, edit, and delete financial transactions.
- **Budget Management**: Set budgets for different categories and track them in real-time.
- **Data Visualization**: Interactive pie and bar charts for financial analysis.
- **Database Integration**: All data is securely stored in a SQLite database.

---


## **Changes in the Approach**

Compared to the initial proposal, the following changes were made in the approach:

1. **User Interface Enhancements**:
   - The interface now includes full-screen mode with the ability to exit using the ESC key.
   - Dynamic resizing and better layout adjustments for tables and charts.

2. **Data Visualization**:
   - Added a **Bar Chart** to compare monthly income and expenses, providing a clearer visual representation of financial trends.
   - Introduced an **interactive Pie Chart** with tooltips for better insights into expense categories.

3. **Profile Information**:
   - A new feature to display user-specific details (username and ID) directly on the dashboard.

4. **Budget Management**:
   - Real-time updates to budgets and transaction summaries on data modification.
   - Alerts added for overspending in budgeted categories.

5. **Advanced Validation**:
   - Additional form validations to improve user experience and error prevention.

These changes were made to enhance usability, provide deeper insights, and align the project with modern standards for personal finance applications.

---

---

## **Differences in Coding Approach**

Compared to the original proposal, the following differences in coding approach were implemented:

1. **Database Management**:
   - **Proposal**: Planned to use Java Persistence API (JPA) for object-relational mapping and type safety.
   - **Implementation**: Switched to SQLite with JDBC for direct database interactions. This approach uses raw SQL queries for operations, giving more control but requiring manual handling of relationships and constraints.

2. **Transaction Handling**:
   - **Proposal**: Intended to rely on JPA’s advanced ORM features for transaction management.
   - **Implementation**: Transactions are handled manually with SQLite, using custom SQL queries for precise control over data.

3. **Data Visualization**:
   - **Proposal**: Focused on simple tables for data presentation.
   - **Implementation**: Introduced **interactive Pie Charts** for expense distribution and **Bar Charts** for monthly income vs. expenses using JavaFX.

4. **User Interface (UI)**:
   - **Proposal**: A static and basic UI was planned.
   - **Implementation**: The UI is dynamic and responsive, including full-screen mode, interactive elements, and real-time updates to data visualizations and summaries.

5. **Budget Management**:
   - **Proposal**: Budget management was lightly discussed.
   - **Implementation**: Added detailed budget management features:
     - Alerts for overspending in specific categories.
     - Real-time budget adjustments using SQL `ON CONFLICT` clauses for updates.

6. **Validation and Error Handling**:
   - **Proposal**: Planned to use standard JavaFX input validations.
   - **Implementation**: Included robust input validation and error messages for better user experience.

7. **Profile Management**:
   - **Proposal**: Did not include profile management.
   - **Implementation**: Added a profile section displaying the logged-in user's details (username and ID).

8. **Threading**:
   - **Proposal**: Considered using threading for complex processes.
   - **Implementation**: Simplified by processing everything synchronously, focusing on reducing overhead and complexity.

These differences demonstrate a shift toward simplicity, control, and real-time interactivity over abstraction and automation.

---


## **Troubleshooting**

- **Issue**: Application fails to launch.
  - **Solution**: Ensure JavaFX SDK and SQLite JAR are correctly added to the build path and VM arguments.

- **Issue**: "Module not found" error.
  - **Solution**: Verify the `--module-path` and `--add-modules` VM arguments.

---
