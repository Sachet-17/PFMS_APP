package gui;

import database.DatabaseInitializer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import managers.BudgetManager;
import managers.TransactionManager;
import managers.UserManager;
import models.Transaction;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import java.util.stream.Collectors;


import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;



public class PFMSApp extends Application {

    private final UserManager userManager = new UserManager();
    private final TransactionManager transactionManager = new TransactionManager();
    private final BudgetManager budgetManager = new BudgetManager();

    private int loggedInUserId = -1;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private TableView<Transaction> transactionTable;
    private PieChart pieChart;
    private Label summaryLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Personal Finance Management System");
        DatabaseInitializer.initializeDatabase();
        primaryStage.setScene(new Scene(createLoginScreen(primaryStage), 800, 600));
        primaryStage.show();
        
        primaryStage.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                primaryStage.setFullScreen(false); // Exit full-screen on Esc key
            }
        });

    }

    private VBox createLoginScreen(Stage stage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        // Instruction section
        Label instructionLabel = new Label(
            "Welcome to the Personal Finance Management System (PFMS)!\n\n"
            + "With this application, you can:\n"
            + "- Manage your personal income and expenses.\n"
            + "- Set budgets and track expenses in real-time.\n"
            + "- View detailed transaction history.\n"
            + "- Analyze spending patterns with a pie chart.\n\n"
            + "Please log in or register to start managing your finances!"
        );
        instructionLabel.setWrapText(true);
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: #555555;");
        instructionLabel.setMaxWidth(600);

        // Login/Registration form
        Label titleLabel = new Label("Login to PFMS");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (userManager.loginUser(username, password)) {
                loggedInUserId = userManager.getUserId(username);
                Scene dashboardScene = createDashboardScene(stage);
                stage.setScene(dashboardScene);
                stage.setFullScreen(true); // Enable full-screen mode
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });


        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (userManager.registerUser(username, password)) {
                messageLabel.setText("Registration successful. Please log in.");
            } else {
                messageLabel.setText("Registration failed. Username may already exist.");
            }
        });

        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Add all components to layout
        layout.getChildren().addAll(instructionLabel, titleLabel, usernameField, passwordField, buttonBox, messageLabel);
        return layout;
    }

    
    
   // private ListView<String> budgetSectionListView;
    

    private TableView<Map.Entry<String, Double>> budgetTable;

    private VBox createBudgetSection() {
        VBox budgetSection = new VBox(10);
        budgetSection.setPadding(new Insets(10));
        budgetSection.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");

        Label titleLabel = new Label("Current Budgets");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Initialize budgetTable as a class-level variable
        budgetTable = new TableView<>();
        budgetTable.setEditable(false);
        budgetTable.setPrefHeight(300);
        budgetTable.setPrefWidth(300);

        // Category Column
        TableColumn<Map.Entry<String, Double>, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey())
        );
        categoryColumn.setPrefWidth(150);

        // Budget Column
        TableColumn<Map.Entry<String, Double>, String> budgetColumn = new TableColumn<>("Budget");
        budgetColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", cellData.getValue().getValue()))
        );
        budgetColumn.setPrefWidth(150);

        budgetTable.getColumns().addAll(categoryColumn, budgetColumn);

        // Populate the table with budget data
        refreshBudgetTable(budgetTable);

        budgetSection.getChildren().addAll(titleLabel, budgetTable);
        return budgetSection;
    }




    private void refreshBudgetTable(TableView<Map.Entry<String, Double>> budgetTable) {
        Map<String, Double> budgets = budgetManager.getBudgets(loggedInUserId);
        ObservableList<Map.Entry<String, Double>> budgetData = FXCollections.observableArrayList(budgets.entrySet());
        budgetTable.setItems(budgetData);
    }


    
    private VBox createProfileSection() {
        VBox profileSection = new VBox(10);
        profileSection.setPadding(new Insets(10));
        profileSection.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-color: white;");

        Label titleLabel = new Label("Profile Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Fetch user details
        String username = userManager.getUsernameById(loggedInUserId); 
        Label usernameLabel = new Label("Username: " + username);

        Label userIdLabel = new Label("User ID: " + loggedInUserId);

        profileSection.getChildren().addAll(titleLabel, usernameLabel, userIdLabel);
        return profileSection;
    }


    private Scene createDashboardScene(Stage stage) {
        BorderPane root = new BorderPane();
        VBox dashboard = new VBox(15);
        dashboard.setPadding(new Insets(20));

        Label dashboardLabel = new Label("Dashboard");
        dashboardLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        dashboard.getChildren().add(dashboardLabel);

        // Profile Section
        VBox profileSection = createProfileSection();
        dashboard.getChildren().add(profileSection);

        // Summary Section
        summaryLabel = new Label();
        updateSummary();
        dashboard.getChildren().add(summaryLabel);

        // Charts Section (Bar Chart and Pie Chart side by side)
        HBox chartsSection = new HBox(15);
        chartsSection.setPadding(new Insets(10));
        barChart = createBarChart();
        pieChart = createPieChart();
        HBox.setHgrow(barChart, Priority.ALWAYS); // Allow resizing of the Bar Chart
        HBox.setHgrow(pieChart, Priority.ALWAYS); // Allow resizing of the Pie Chart
        chartsSection.getChildren().addAll(barChart, pieChart);
        dashboard.getChildren().add(chartsSection);

        // Tables Section (Transaction Table and Budget Section side by side)
        HBox tablesSection = new HBox(15);
        tablesSection.setPadding(new Insets(10));

        // Create Transaction Table
        transactionTable = createTransactionTable();
        transactionTable.setPrefHeight(300); // Set table height
        transactionTable.setPrefWidth(600); // Set default width

        // Wrap Transaction Table in ScrollPane for scrolling
        ScrollPane transactionScrollPane = new ScrollPane(transactionTable);
        transactionScrollPane.setFitToWidth(true);
        transactionScrollPane.setFitToHeight(true);

        // Create Budget Section
        VBox budgetSection = createBudgetSection();
        budgetSection.setPrefHeight(300); // Match height of transaction table
        budgetSection.setPrefWidth(300); // Set default width

        // Wrap Budget Section in ScrollPane for scrolling
        ScrollPane budgetScrollPane = new ScrollPane(budgetSection);
        budgetScrollPane.setFitToWidth(true);
        budgetScrollPane.setFitToHeight(true);

        // Allow dynamic resizing of both sections
        HBox.setHgrow(transactionScrollPane, Priority.ALWAYS);
        HBox.setHgrow(budgetScrollPane, Priority.ALWAYS);

        tablesSection.getChildren().addAll(transactionScrollPane, budgetScrollPane);
        VBox.setVgrow(tablesSection, Priority.ALWAYS);
        dashboard.getChildren().add(tablesSection);

        // Budget Management Form
        VBox budgetManagerForm = createBudgetManagerForm();
        dashboard.getChildren().add(budgetManagerForm);

        // Add Transaction Form
        HBox addTransactionForm = createAddTransactionForm();
        dashboard.getChildren().add(addTransactionForm);

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> stage.setScene(new Scene(createLoginScreen(stage), 800, 600)));
        dashboard.getChildren().add(logoutButton);

        root.setCenter(dashboard);
        return new Scene(root, 1200, 800); 
    }




    
    private void openEditTransactionForm(Transaction transaction) {
        Stage editStage = new Stage();
        editStage.setTitle("Edit Transaction");

        VBox form = new VBox(10);
        form.setPadding(new Insets(10));

        TextField dateField = new TextField(transaction.getDate());
        TextField descriptionField = new TextField(transaction.getDescription());
        TextField amountField = new TextField(String.valueOf(transaction.getAmount()));
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Expense", "Income");
        typeBox.setValue(transaction.getType());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String date = dateField.getText();
            String description = descriptionField.getText();
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid amount. Please enter a valid number.");
                return;
            }
            String type = typeBox.getValue();

            transaction.setDate(date);
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setType(type);

            transactionManager.addTransaction(loggedInUserId, transaction);
            refreshTransactions();
            refreshBudgets();
            editStage.close();
        });

        form.getChildren().addAll(new Label("Date:"), dateField, new Label("Description:"), descriptionField,
                new Label("Amount:"), amountField, new Label("Type:"), typeBox, saveButton);

        Scene scene = new Scene(form, 400, 300);
        editStage.setScene(scene);
        editStage.show();
    }

    

    private TableView<Transaction> createTransactionTable() {
    	
        TableView<Transaction> tableView = new TableView<>();
        tableView.setPrefHeight(300); // Adjust the height
        tableView.setPrefWidth(600); // Adjust the width
        tableView.setEditable(false);
        transactions = FXCollections.observableArrayList();
        tableView.setItems(transactions);

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Transaction, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    openEditTransactionForm(transaction);
                });

                deleteButton.setOnAction(e -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    transactionManager.deleteTransaction(transaction.getId());
                    refreshTransactions();
                    refreshBudgets();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        tableView.getColumns().addAll(dateColumn, descriptionColumn, amountColumn, typeColumn, actionColumn);
        refreshTransactions();
        return tableView;
    }


    private HBox createAddTransactionForm() {
        HBox form = new HBox(10);
        form.setPadding(new Insets(10));

        TextField dateField = new TextField();
        dateField.setPromptText("Date (MM/DD/YYYY)");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Expense", "Income");
        typeBox.setPromptText("Type");

        Button addButton = new Button("Add Transaction");
        addButton.setOnAction(e -> {
            String date = dateField.getText();
            String description = descriptionField.getText();
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid amount. Please enter a valid number.");
                return;
            }
            String type = typeBox.getValue();
            if (date.isEmpty() || description.isEmpty() || type == null) {
                showAlert("Please fill all fields.");
                return;
            }
            Transaction transaction = new Transaction(0, date, description, amount, type);
            transactionManager.addTransaction(loggedInUserId, transaction);
            refreshTransactions();
            refreshBudgets();
            dateField.clear();
            descriptionField.clear();
            amountField.clear();
            typeBox.setValue(null);
        });

        form.getChildren().addAll(dateField, descriptionField, amountField, typeBox, addButton);
        return form;
    }

    private VBox createBudgetManagerForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");

        Label titleLabel = new Label("Manage Budgets");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField budgetAmountField = new TextField();
        budgetAmountField.setPromptText("Budget Amount");

        Button setBudgetButton = new Button("Set Budget");
        setBudgetButton.setOnAction(e -> {
            String category = categoryField.getText();
            double amount;
            try {
                amount = Double.parseDouble(budgetAmountField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid amount. Please enter a valid number.");
                return;
            }
            if (category.isEmpty()) {
                showAlert("Category cannot be empty.");
                return;
            }
            budgetManager.setBudget(loggedInUserId, category, amount);
            categoryField.clear();
            budgetAmountField.clear();

            // Correctly refresh the budget list
            refreshBudgetTable(budgetTable);

        });


        form.getChildren().addAll(titleLabel, categoryField, budgetAmountField, setBudgetButton);
        return form;
    }
    
    private BarChart<String, Number> barChart;

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount ($)");

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Income vs Expenses");

        refreshBarChart();
        return barChart;
    }
    
    private void refreshBarChart() {
        barChart.getData().clear(); // Clear old data

        // Group transactions by month
        Map<String, Double> incomeByMonth = transactionManager.getTransactions(loggedInUserId).stream()
            .filter(t -> "Income".equals(t.getType())) // Filter for income
            .collect(Collectors.groupingBy(
                t -> t.getDate().substring(0, 7), // Extract "YYYY-MM" from date
                Collectors.summingDouble(Transaction::getAmount)
            ));

        Map<String, Double> expenseByMonth = transactionManager.getTransactions(loggedInUserId).stream()
            .filter(t -> "Expense".equals(t.getType())) // Filter for expenses
            .collect(Collectors.groupingBy(
                t -> t.getDate().substring(0, 7), // Extract "YYYY-MM" from date
                Collectors.summingDouble(Transaction::getAmount)
            ));

        // Create income and expense series
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        // Populate series with data
        incomeByMonth.forEach((month, totalIncome) -> incomeSeries.getData().add(new XYChart.Data<>(month, totalIncome)));
        expenseByMonth.forEach((month, totalExpense) -> expenseSeries.getData().add(new XYChart.Data<>(month, totalExpense)));

        // Add series to bar chart
        barChart.getData().addAll(incomeSeries, expenseSeries);
    }




    private PieChart createPieChart() {
        PieChart pieChart = new PieChart();
        refreshPieChart(pieChart);
        return pieChart;
    }

    private void refreshTransactions() {
        transactions.setAll(transactionManager.getTransactions(loggedInUserId));
        refreshPieChart(pieChart); 
        refreshBarChart();         
        updateSummary();           
    }


    private void refreshPieChart(PieChart pieChart) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        Map<String, Double> categoryTotals = transactionManager.getTransactions(loggedInUserId).stream()
                .filter(t -> "Expense".equals(t.getType())) 
                .collect(Collectors.groupingBy(Transaction::getDescription, Collectors.summingDouble(Transaction::getAmount)));

        categoryTotals.forEach((category, total) -> pieData.add(new PieChart.Data(category, total)));

        pieChart.setData(pieData);
        pieChart.getData().forEach(data -> {
            Tooltip.install(data.getNode(), new Tooltip(
                    String.format("%s: $%.2f", data.getName(), data.getPieValue())
            ));
            data.getNode().setOnMouseEntered(e -> data.getNode().setStyle("-fx-scale: 1.1; -fx-cursor: hand;"));
            data.getNode().setOnMouseExited(e -> data.getNode().setStyle("-fx-scale: 1.0;"));
        });
    }


    private void updateSummary() {
        double totalIncome = transactions.stream()
                .filter(t -> "Income".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> "Expense".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double remainingBudget = totalIncome - totalExpenses;

        summaryLabel.setText(String.format("Total Income: $%.2f | Total Expenses: $%.2f | Remaining Budget: $%.2f",
                totalIncome, totalExpenses, remainingBudget));
    }
    
    

    private final Set<String> alertedCategories = new HashSet<>();

    
    private void refreshBudgets() {
    	 barChart.getData().clear(); // Clear old data

    	    // Group transactions by month for Income
    	    Map<String, Double> incomeByMonth = transactionManager.getTransactions(loggedInUserId).stream()
    	        .filter(t -> "Income".equals(t.getType())) // Filter only Income
    	        .collect(Collectors.groupingBy(
    	            t -> t.getDate().substring(0, 7), // Extract "YYYY-MM" from the date
    	            Collectors.summingDouble(Transaction::getAmount) // Sum amounts
    	        ));

    	    // Group transactions by month for Expenses
    	    Map<String, Double> expenseByMonth = transactionManager.getTransactions(loggedInUserId).stream()
    	        .filter(t -> "Expense".equals(t.getType())) // Filter only Expenses
    	        .collect(Collectors.groupingBy(
    	            t -> t.getDate().substring(0, 7), // Extract "YYYY-MM" from the date
    	            Collectors.summingDouble(Transaction::getAmount) // Sum amounts
    	        ));

    	    // Create series for Income and Expenses
    	    XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
    	    incomeSeries.setName("Income");

    	    XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
    	    expenseSeries.setName("Expense");

    	    // Populate Income series
    	    incomeByMonth.forEach((month, totalIncome) -> 
    	        incomeSeries.getData().add(new XYChart.Data<>(month, totalIncome))
    	    );

    	    // Populate Expense series
    	    expenseByMonth.forEach((month, totalExpense) -> 
    	        expenseSeries.getData().add(new XYChart.Data<>(month, totalExpense))
    	    );

    	    // Add series to the Bar Chart
    	    barChart.getData().addAll(incomeSeries, expenseSeries);
    }




    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
