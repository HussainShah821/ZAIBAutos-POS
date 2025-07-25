import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private ArrayList<Customer> customers;
    private ArrayList<Supplier> suppliers;
    private ArrayList<Product> products;
    private ArrayList<Expenditure> expenditures;
    private ArrayList<Sales> sales;
    private ArrayList<Profit> profits;
    private Map<Integer, List<List<Object>>> customerLedgers; // Key: customerId, Value: List of [date, item, brand, type, uom, qty, price, total]

    // Customer class
    public static class Customer {
        int id;
        String name;
        String phone;
        double balance; // Maps to amountRemaining
        double amountPaid;
        double creditLimit;

        Customer(int id, String name, String phone, double balance, double amountPaid, double creditLimit) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.balance = balance;
            this.amountPaid = amountPaid;
            this.creditLimit = creditLimit;
        }

        @Override
        public String toString() {
            return name + " (" + phone + ")";
        }
    }

    // Supplier class
    public static class Supplier {
        int id;
        String name;
        String phone;

        Supplier(int id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }

        @Override
        public String toString() {
            return name + " (" + phone + ")";
        }
    }

    // Product class
    public static class Product {
        int id;
        String name;
        String brand;
        double price;
        String type;
        String uom;
        int stock;

        Product(int id, String name, String brand, double price, String type, String uom, int stock) {
            this.id = id;
            this.name = name;
            this.brand = brand;
            this.price = price;
            this.type = type;
            this.uom = uom;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return name + " (" + brand + ")";
        }
    }

    // Expenditure class
    public static class Expenditure {
        int id;
        String date;
        String category;
        String description;
        double amount;
        String paymentType;

        Expenditure(int id, String date, String category, String description, double amount, String paymentType) {
            this.id = id;
            this.date = date;
            this.category = category;
            this.description = description;
            this.amount = amount;
            this.paymentType = paymentType;
        }
    }

    // Sales class (for SalesAnalysisFrame)
    public static class Sales {
        int id;
        String date;
        double sales;
        double expenses;
        double profit;
        double profitPercent;

        Sales(int id, String date, double sales, double expenses) {
            this.id = id;
            this.date = date;
            this.sales = sales;
            this.expenses = expenses;
            this.profit = sales - expenses;
            this.profitPercent = sales > 0 ? (this.profit / sales) * 100 : 0;
        }
    }

    // Profit class
    public static class Profit {
        int id;
        String date;
        double amount;
        String remarks;

        Profit(int id, String date, double amount, String remarks) {
            this.id = id;
            this.date = date;
            this.amount = amount;
            this.remarks = remarks;
        }
    }

    private DataManager() {
        customers = new ArrayList<>();
        suppliers = new ArrayList<>();
        products = new ArrayList<>();
        expenditures = new ArrayList<>();
        sales = new ArrayList<>();
        profits = new ArrayList<>();
        customerLedgers = new HashMap<>();

        // Sample data
        customers.add(new Customer(1, "Ali Khan", "500-1234567", 2000.0, 5000.0, 10000.0));
        customers.add(new Customer(2, "Sara Ahmed", "500-7654321", 500.0, 3000.0, 8000.0));
        customers.add(new Customer(3, "Bilal Raza", "500-9876543", 0.0, 7000.0, 15000.0));

        // Sample ledger entries for customers
        customerLedgers.put(1, new ArrayList<>());
        customerLedgers.get(1).add(List.of("21-05-2025", "Oil Filter", "Bosch", "Filter", "Unit", 2, 1500.0, 3000.0));

        customerLedgers.put(2, new ArrayList<>());
        customerLedgers.get(2).add(List.of("21-05-2025", "Brake Pads", "Toyota", "Brake", "Set", 1, 3000.0, 3000.0));

        customerLedgers.put(3, new ArrayList<>());
        customerLedgers.get(3).add(List.of("21-05-2025", "Air Filter", "Honda", "Filter", "Unit", 1, 1200.0, 1200.0));

        suppliers.add(new Supplier(1, "Auto Parts Ltd", "1112223334"));
        suppliers.add(new Supplier(2, "Motor Supplies", "4445556667"));
        products.add(new Product(1, "Oil Filter", "Bosch", 1500.0, "Filter", "Unit", 50));
        products.add(new Product(2, "Brake Pads", "Toyota", 3000.0, "Brake", "Set", 20));
        expenditures.add(new Expenditure(1, "21-05-2025", "Operational", "Office Rent", 10000.0, "Cash"));
        sales.add(new Sales(1, "21-05-2025", 3000.0, 1000.0));
        sales.add(new Sales(2, "15-05-2025", 2000.0, 700.0));
        profits.add(new Profit(1, "21-05-2025", 5000.0, "End of day profit"));

        // Database initialization
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaib_autos_pos_new?useSSL=false", "zaib_user", "ZaibPass2025");

            // Clear existing data (optional, for fresh start)
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM customers");
            stmt.executeUpdate("DELETE FROM customer_ledgers");
            stmt.executeUpdate("DELETE FROM suppliers");
            stmt.executeUpdate("DELETE FROM products");
            stmt.executeUpdate("DELETE FROM expenditures");
            stmt.executeUpdate("DELETE FROM sales");
            stmt.executeUpdate("DELETE FROM profits");
            stmt.close();

            // Insert customers
            String customerSql = "INSERT INTO customers (customer_id, name, phone, balance, amount_paid, credit_limit) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement customerStmt = conn.prepareStatement(customerSql);
            for (Customer c : customers) {
                customerStmt.setInt(1, c.id);
                customerStmt.setString(2, c.name);
                customerStmt.setString(3, c.phone);
                customerStmt.setDouble(4, c.balance);
                customerStmt.setDouble(5, c.amountPaid);
                customerStmt.setDouble(6, c.creditLimit);
                customerStmt.executeUpdate();
            }
            customerStmt.close();

            // Insert customer ledgers
            String ledgerSql = "INSERT INTO customer_ledgers (customer_id, date, item, brand, type, uom, qty, price, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ledgerStmt = conn.prepareStatement(ledgerSql);
            for (Map.Entry<Integer, List<List<Object>>> entry : customerLedgers.entrySet()) {
                int customerId = entry.getKey();
                List<List<Object>> ledgerEntries = entry.getValue();
                for (List<Object> ledgerEntry : ledgerEntries) {
                    ledgerStmt.setInt(1, customerId);
                    ledgerStmt.setString(2, (String) ledgerEntry.get(0)); // date
                    ledgerStmt.setString(3, (String) ledgerEntry.get(1)); // item
                    ledgerStmt.setString(4, (String) ledgerEntry.get(2)); // brand
                    ledgerStmt.setString(5, (String) ledgerEntry.get(3)); // type
                    ledgerStmt.setString(6, (String) ledgerEntry.get(4)); // uom
                    ledgerStmt.setInt(7, (Integer) ledgerEntry.get(5));   // qty
                    ledgerStmt.setDouble(8, (Double) ledgerEntry.get(6)); // price
                    ledgerStmt.setDouble(9, (Double) ledgerEntry.get(7)); // total
                    ledgerStmt.executeUpdate();
                }
            }
            ledgerStmt.close();

            // Insert suppliers
            String supplierSql = "INSERT INTO suppliers (supplier_id, name, phone) VALUES (?, ?, ?)";
            PreparedStatement supplierStmt = conn.prepareStatement(supplierSql);
            for (Supplier s : suppliers) {
                supplierStmt.setInt(1, s.id);
                supplierStmt.setString(2, s.name);
                supplierStmt.setString(3, s.phone);
                supplierStmt.executeUpdate();
            }
            supplierStmt.close();

            // Insert products
            String productSql = "INSERT INTO products (product_id, name, brand, price, type, uom, stock) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement productStmt = conn.prepareStatement(productSql);
            for (Product p : products) {
                productStmt.setInt(1, p.id);
                productStmt.setString(2, p.name);
                productStmt.setString(3, p.brand);
                productStmt.setDouble(4, p.price);
                productStmt.setString(5, p.type);
                productStmt.setString(6, p.uom);
                productStmt.setInt(7, p.stock);
                productStmt.executeUpdate();
            }
            productStmt.close();

            // Insert expenditures
            String expenditureSql = "INSERT INTO expenditures (expenditure_id, expenditure_date, category, description, amount, payment_type) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement expenditureStmt = conn.prepareStatement(expenditureSql);
            for (Expenditure e : expenditures) {
                expenditureStmt.setInt(1, e.id);
                expenditureStmt.setString(2, e.date);
                expenditureStmt.setString(3, e.category);
                expenditureStmt.setString(4, e.description);
                expenditureStmt.setDouble(5, e.amount);
                expenditureStmt.setString(6, e.paymentType);
                expenditureStmt.executeUpdate();
            }
            expenditureStmt.close();

            // Insert sales
            String salesSql = "INSERT INTO sales (sale_id, sale_date, sales_amount, expenses) VALUES (?, ?, ?, ?)";
            PreparedStatement salesStmt = conn.prepareStatement(salesSql);
            for (Sales s : sales) {
                salesStmt.setInt(1, s.id);
                salesStmt.setString(2, s.date);
                salesStmt.setDouble(3, s.sales);
                salesStmt.setDouble(4, s.expenses);
                salesStmt.executeUpdate();
            }
            salesStmt.close();

            // Insert profits
            String profitSql = "INSERT INTO profits (profit_id, profit_date, amount, remarks) VALUES (?, ?, ?, ?)";
            PreparedStatement profitStmt = conn.prepareStatement(profitSql);
            for (Profit p : profits) {
                profitStmt.setInt(1, p.id);
                profitStmt.setString(2, p.date);
                profitStmt.setDouble(3, p.amount);
                profitStmt.setString(4, p.remarks);
                profitStmt.executeUpdate();
            }
            profitStmt.close();

            conn.close();
        } catch (SQLException ex) {
            System.err.println("Database initialization error: " + ex.getMessage());
        }
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // Customer methods
    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    // Ledger methods
    public void addLedgerEntry(int customerId, String date, String item, String brand, String type, String uom, int qty, double price, double total) {
        List<List<Object>> ledger = customerLedgers.getOrDefault(customerId, new ArrayList<>());
        ledger.add(List.of(date, item, brand, type, uom, qty, price, total));
        customerLedgers.put(customerId, ledger);

        // Update customer's balance (amountRemaining)
        Customer customer = customers.stream().filter(c -> c.id == customerId).findFirst().orElse(null);
        if (customer != null) {
            customer.balance += total;
        }
    }

    public List<List<Object>> getCustomerLedger(int customerId) {
        return customerLedgers.getOrDefault(customerId, new ArrayList<>());
    }

    // Supplier methods
    public ArrayList<Supplier> getSuppliers() {
        return suppliers;
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    // Product methods
    public ArrayList<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    // Expenditure methods
    public ArrayList<Expenditure> getExpenditures() {
        return expenditures;
    }

    public void addExpenditure(Expenditure expenditure) {
        expenditures.add(expenditure);
    }

    public void removeExpenditure(Expenditure expenditure) {
        expenditures.remove(expenditure);
    }

    // Sales methods
    public ArrayList<Sales> getSales() {
        return sales;
    }

    public void addSale(Sales sale) {
        sales.add(sale);
    }

    public void clearSales() {
        sales.clear();
    }

    // Profit methods
    public ArrayList<Profit> getProfits() {
        return profits;
    }

    public void addProfit(Profit profit) {
        profits.add(profit);
    }

    public void removeProfit(Profit profit) {
        profits.remove(profit);
    }
}