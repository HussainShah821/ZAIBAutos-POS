import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static final String URL = "jdbc:mysql://localhost:3306/zaibautos";
    private Connection connection;
    private String username;
    private String password;

    private DatabaseConnection(String username, String password) {
        this.username = username;
        this.password = password;
        try {
            connection = DriverManager.getConnection(URL, username, password);
            System.out.println("Connected to zaibautos database!");
            initializeSchema();
        } catch (SQLException e) {
            if (e.getSQLState().equals("42000")) { // Unknown database
                System.err.println("Database 'zaibautos' does not exist. Please create it and retry.");
            } else {
                System.err.println("Database connection failed: " + e.getMessage() + " [SQLState: " + e.getSQLState() + ", Error Code: " + e.getErrorCode() + "]");
            }
            connection = null;
        }
    }

    private void initializeSchema() {
        try (Statement stmt = connection.createStatement()) {
            // Create tables
            createTables(stmt);
            // Create indexes
            createIndexes(stmt);
            // Create triggers
            createTriggers(stmt);
            System.out.println("All tables, indexes, and triggers created successfully.");
        } catch (SQLException e) {
            System.err.println("Schema initialization failed: " + e.getMessage() + " [SQLState: " + e.getSQLState() + ", Error Code: " + e.getErrorCode() + "]");
        }
    }

    private void createTables(Statement stmt) throws SQLException {
        // Debug log table for trigger debugging
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS debug_log (" +
                "log_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "message VARCHAR(255), " +
                "log_time DATETIME)");

        // Users table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL, " +
                "PRIMARY KEY (username))");
        stmt.executeUpdate("INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin123')");

        // Customers table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customers (" +
                "customer_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "phone VARCHAR(20), " +
                "amount_remaining DECIMAL(10, 2) DEFAULT 0.00, " +
                "amount_paid DECIMAL(10, 2) DEFAULT 0.00, " +
                "total_sale DECIMAL(10, 2) DEFAULT 0.00)");

        // Products table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                "product_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "item_name VARCHAR(100) NOT NULL, " +
                "brand VARCHAR(100), " +
                "type VARCHAR(50), " +
                "uom VARCHAR(20), " +
                "stock INT NOT NULL DEFAULT 0)");

        // Customer Ledgers table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customer_ledgers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_id INT, " +
                "product_id INT, " +
                "date DATE NOT NULL, " +
                "item_name VARCHAR(100), " +
                "type VARCHAR(50), " +
                "uom VARCHAR(20), " +
                "qty INT NOT NULL, " +
                "price DECIMAL(10, 2), " +
                "total DECIMAL(10, 2), " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id), " +
                "FOREIGN KEY (product_id) REFERENCES products(product_id))");

        // Suppliers table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS suppliers (" +
                "supplier_id INT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "phone VARCHAR(20))");

        // Supplier Ledgers table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS supplier_ledgers (" +
                "bill_no INT AUTO_INCREMENT PRIMARY KEY, " +
                "supplier_id INT, " +
                "date DATE NOT NULL, " +
                "remarks VARCHAR(200), " +
                "debit DECIMAL(10, 2), " +
                "credit DECIMAL(10, 2), " +
                "balance DECIMAL(10, 2), " +
                "FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id))");

        // Sales table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sales (" +
                "s_no INT AUTO_INCREMENT PRIMARY KEY, " +
                "product_id INT, " +
                "product_name VARCHAR(100), " +
                "brand VARCHAR(100), " +
                "type VARCHAR(50), " +
                "uom VARCHAR(20), " +
                "quantity INT NOT NULL, " +
                "price DECIMAL(10, 2), " +
                "total DECIMAL(10, 2), " +
                "date DATE NOT NULL, " +
                "FOREIGN KEY (product_id) REFERENCES products(product_id))");

        // Expenditures table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS expenditures (" +
                "expenditure_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "date DATE NOT NULL, " +
                "category VARCHAR(50), " +
                "description VARCHAR(200), " +
                "amount DECIMAL(10, 2), " +
                "payment_type VARCHAR(50))");

        // Profits table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS profits (" +
                "profit_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "profit_date DATE NOT NULL, " +
                "amount DECIMAL(10, 2), " +
                "remarks VARCHAR(200))");

        // Sales Analysis table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sales_analysis (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "date DATE NOT NULL, " +
                "sales DECIMAL(10, 2) DEFAULT 0.00, " +
                "expenses DECIMAL(10, 2) DEFAULT 0.00)");
    }

    private void createIndexes(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_sales_date ON sales(date)");
        stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_expenditures_date ON expenditures(date)");
        stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_profits_date ON profits(profit_date)");
        stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_customer_ledgers_date ON customer_ledgers(date)");
        stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_sales_analysis_date ON sales_analysis(date)");
    }

    private void createTriggers(Statement stmt) throws SQLException {
        // Single-line triggers (no DELIMITER needed)
        stmt.executeUpdate("CREATE TRIGGER IF NOT EXISTS update_stock_after_sales_insert " +
                "AFTER INSERT ON sales FOR EACH ROW " +
                "UPDATE products SET stock = stock - NEW.quantity WHERE product_id = NEW.product_id");

        stmt.executeUpdate("CREATE TRIGGER IF NOT EXISTS update_stock_after_sales_delete " +
                "AFTER DELETE ON sales FOR EACH ROW " +
                "UPDATE products SET stock = stock + OLD.quantity WHERE product_id = OLD.product_id");

        // Multi-statement triggers (handled individually to avoid DELIMITER issues)
        try {
            stmt.executeUpdate("DROP TRIGGER IF EXISTS check_stock_before_sales_insert");
            stmt.executeUpdate("CREATE TRIGGER check_stock_before_sales_insert " +
                    "BEFORE INSERT ON sales " +
                    "FOR EACH ROW BEGIN " +
                    "    DECLARE current_stock INT; " +
                    "    SELECT stock INTO current_stock FROM products WHERE product_id = NEW.product_id; " +
                    "    IF current_stock IS NULL THEN " +
                    "        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid product_id in sales'; " +
                    "    END IF; " +
                    "    IF current_stock < NEW.quantity THEN " +
                    "        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock for sale'; " +
                    "    END IF; " +
                    "END");
        } catch (SQLException e) {
            System.err.println("Failed to create trigger check_stock_before_sales_insert: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
        }

        try {
            stmt.executeUpdate("DROP TRIGGER IF EXISTS update_sales_analysis");
            stmt.executeUpdate("CREATE TRIGGER update_sales_analysis " +
                    "AFTER INSERT ON sales " +
                    "FOR EACH ROW BEGIN " +
                    "    DECLARE existing_id INT; " +
                    "    SELECT id INTO existing_id FROM sales_analysis WHERE date = NEW.date LIMIT 1; " +
                    "    IF existing_id IS NOT NULL THEN " +
                    "        UPDATE sales_analysis SET sales = sales + NEW.total WHERE id = existing_id; " +
                    "    ELSE " +
                    "        INSERT INTO sales_analysis (date, sales, expenses) VALUES (NEW.date, NEW.total, 0); " +
                    "    END IF; " +
                    "END");
        } catch (SQLException e) {
            System.err.println("Failed to create trigger update_sales_analysis: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
        }

        try {
            stmt.executeUpdate("DROP TRIGGER IF EXISTS update_sales_analysis_expenses");
            stmt.executeUpdate("CREATE TRIGGER update_sales_analysis_expenses " +
                    "AFTER INSERT ON expenditures " +
                    "FOR EACH ROW BEGIN " +
                    "    DECLARE existing_id INT; " +
                    "    SELECT id INTO existing_id FROM sales_analysis WHERE date = NEW.date LIMIT 1; " +
                    "    IF existing_id IS NOT NULL THEN " +
                    "        UPDATE sales_analysis SET expenses = expenses + NEW.amount WHERE id = existing_id; " +
                    "    ELSE " +
                    "        INSERT INTO sales_analysis (date, sales, expenses) VALUES (NEW.date, 0, NEW.amount); " +
                    "    END IF; " +
                    "END");
        } catch (SQLException e) {
            System.err.println("Failed to create trigger update_sales_analysis_expenses: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
        }

        try {
            stmt.executeUpdate("DROP TRIGGER IF EXISTS update_profits");
            stmt.executeUpdate("CREATE TRIGGER update_profits " +
                    "AFTER INSERT ON sales_analysis " +
                    "FOR EACH ROW BEGIN " +
                    "    DECLARE existing_id INT; " +
                    "    SELECT profit_id INTO existing_id FROM profits WHERE profit_date = NEW.date AND remarks != 'Manual entry' LIMIT 1; " +
                    "    IF existing_id IS NOT NULL THEN " +
                    "        UPDATE profits SET amount = NEW.sales - NEW.expenses, remarks = 'Auto-calculated from sales and expenses' WHERE profit_id = existing_id; " +
                    "    ELSE " +
                    "        INSERT INTO profits (profit_date, amount, remarks) VALUES (NEW.date, NEW.sales - NEW.expenses, 'Auto-calculated from sales and expenses'); " +
                    "    END IF; " +
                    "END");
        } catch (SQLException e) {
            System.err.println("Failed to create trigger update_profits: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
        }

        try {
            stmt.executeUpdate("DROP TRIGGER IF EXISTS update_profits_after_update");
            stmt.executeUpdate("CREATE TRIGGER update_profits_after_update " +
                    "AFTER UPDATE ON sales_analysis " +
                    "FOR EACH ROW BEGIN " +
                    "    DECLARE existing_id INT; " +
                    "    SELECT profit_id INTO existing_id FROM profits WHERE profit_date = NEW.date AND remarks != 'Manual entry' LIMIT 1; " +
                    "    IF existing_id IS NOT NULL THEN " +
                    "        UPDATE profits SET amount = NEW.sales - NEW.expenses, remarks = 'Auto-calculated from sales and expenses' WHERE profit_id = existing_id; " +
                    "    ELSE " +
                    "        INSERT INTO profits (profit_date, amount, remarks) VALUES (NEW.date, NEW.sales - NEW.expenses, 'Auto-calculated from sales and expenses'); " +
                    "    END IF; " +
                    "END");
        } catch (SQLException e) {
            System.err.println("Failed to create trigger update_profits_after_update: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
        }
    }

    public static synchronized DatabaseConnection getInstance(String username, String password) {
        if (instance == null || !instance.username.equals(username) || !instance.password.equals(password)) {
            instance = new DatabaseConnection(username, password);
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, username, password);
                System.out.println("Reconnected to zaibautos database!");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Reconnection failed: " + e.getMessage() + " [SQLState: " + e.getSQLState() + ", Error Code: " + e.getErrorCode() + "]");
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // Method to debug stock update issues after sales insert
    public void debugStockUpdate(int productId, int quantity) {
        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate("UPDATE products SET stock = stock - " + quantity + " WHERE product_id = " + productId);
            if (rowsAffected == 0) {
                stmt.executeUpdate("INSERT INTO debug_log (message, log_time) VALUES ('Stock update failed for product_id " + productId + " in debugStockUpdate', NOW())");
            }
        } catch (SQLException e) {
            System.err.println("Debug stock update failed: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
        }
    }
}