package menu;

import java.sql.*;
import db.DBConnect;
import generalFun.GeneralClasses;

public class AdminMenu extends BaseMenu {
    private static final java.util.Scanner sc = new java.util.Scanner(System.in);

    @Override
    public void showMenu() { adminLogin(); }

    private static void adminLogin() {
        System.out.print("Enter username: ");
        String user = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?")) {

            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n✅ Login successful!");
                    adminMenu(conn);
                } else {
                    System.out.println("\n❌ Invalid credentials!");
                }
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while trying to login. See details for debugging.");
            e.printStackTrace();
        }
    }

    private static void adminMenu(Connection conn) {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. View Products");
            System.out.println("2. Add (Product / Category)");
            System.out.println("3. Update Product");
            System.out.println("4. Remove Product");
            System.out.println("5. Remove Categories");
            System.out.println("6. View Bill Histroy");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> GeneralClasses.viewProducts();
                case 2 -> addMenu(conn);
                case 3 -> updateProduct(conn);
                case 4 -> removeProduct(conn);
                case 5 -> removeCategory(conn);
                case 6 -> viewBillHistory(conn);
                case 7 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void addMenu(Connection conn) {
        while (true) {
            System.out.println("\nAdd:");
            System.out.println("1. Add Product");
            System.out.println("2. Add Category");
            System.out.println("3. Return");
            System.out.print("Enter choice: ");
            int choice = readInt();
            switch (choice) {
                case 1 -> addProduct(conn);
                case 2 -> addCategory(conn);
                case 3 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void addProduct(Connection conn) {
        int id = -1;
        while (true) {
            System.out.print("Enter product ID: ");
            id = readInt();
            // check existence
            try (PreparedStatement chk = conn.prepareStatement("SELECT COUNT(*) FROM products WHERE id = ?")) {
                chk.setInt(1, id);
                try (ResultSet rs = chk.executeQuery()) {
                    rs.next();
                    int cnt = rs.getInt(1);
                    if (cnt > 0) {
                        System.out.println("❌ A product with that ID already exists. Please use a different ID or update the existing product.");
                        System.out.print("Do you want to try another ID? (y/n): ");
                        String c = readLine();
                        if (c.equalsIgnoreCase("y")) continue;
                        else return;
                    }
                    break;
                }
            } catch (SQLException e) {
                System.err.println("Error checking product ID. Aborting add operation.");
                e.printStackTrace();
                return;
            }
        }

        System.out.print("Enter product name: ");
        String name = readLine();

        System.out.print("Enter description (or leave blank): ");
        String descrip = readLine();
        if (descrip.isBlank()) descrip = "No Description";

        System.out.print("Enter price: ");
        double price = readDouble();

        System.out.print("Enter quantity: ");
        int qty = readInt();

        System.out.print("Enter category: ");
        String cate = readLine();

        String sql = "INSERT INTO products (id, name, descrip, price, quantity, cate) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, descrip);
            ps.setDouble(4, price);
            ps.setInt(5, qty);
            ps.setString(6, cate);
            ps.executeUpdate();
            System.out.println("✅ Product added successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to add product. Please check the values and try again.");
            e.printStackTrace();
        }
    }

    private static void addCategory(Connection conn) {
        System.out.print("Enter new category name: ");
        String cate = readLine();
        if (cate.isBlank()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        try (PreparedStatement chk = conn.prepareStatement("SELECT COUNT(*) FROM category WHERE cate = ?")) {
            chk.setString(1, cate);
            try (ResultSet rs = chk.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("❌ That category already exists.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking category existence.");
            e.printStackTrace();
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO category (cate) VALUES (?)")) {
            ps.setString(1, cate);
            ps.executeUpdate();
            System.out.println("✅ Category added successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to add category.");
            e.printStackTrace();
        }
    }

    private static void updateProduct(Connection conn) {
        System.out.print("Enter product ID: ");
        int id = readInt();

        try (PreparedStatement chk = conn.prepareStatement("SELECT COUNT(*) FROM products WHERE id = ?")) {
            chk.setInt(1, id);
            try (ResultSet rs = chk.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    System.out.println("❌ Product not found.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking product existence.");
            e.printStackTrace();
            return;
        }

        System.out.println("What do you want to update?");
        System.out.println("1. Update quantity");
        System.out.println("2. Update price");
        System.out.println("3. Update category");
        System.out.print("Enter choice: ");
        int ch = readInt();

        switch (ch) {
            case 1 -> {
                System.out.print("Enter new quantity: ");
                int newQty = readInt();
                try (PreparedStatement pst = conn.prepareStatement("UPDATE products SET quantity = ? WHERE id = ?")) {
                    pst.setInt(1, newQty);
                    pst.setInt(2, id);
                    int updated = pst.executeUpdate();
                    if (updated > 0) System.out.println("Product quantity updated successfully!");
                    else System.out.println("No rows updated.");
                } catch (SQLException e) {
                    System.err.println("Failed to update quantity.");
                    e.printStackTrace();
                }
            }
            case 2 -> {
                System.out.print("Enter new price: ");
                double newPrice = readDouble();
                try (PreparedStatement pst = conn.prepareStatement("UPDATE products SET price = ? WHERE id = ?")) {
                    pst.setDouble(1, newPrice);
                    pst.setInt(2, id);
                    int updated = pst.executeUpdate();
                    if (updated > 0) System.out.println("Product price updated successfully!");
                    else System.out.println("No rows updated.");
                } catch (SQLException e) {
                    System.err.println("Failed to update price.");
                    e.printStackTrace();
                }
            }
            case 3 -> {
                System.out.print("Enter new category: ");
                String newCate = readLine();
                if (newCate.isBlank()) {
                    System.out.println("Category cannot be empty.");
                    return;
                }
                try (PreparedStatement chkCate = conn.prepareStatement("SELECT COUNT(*) FROM category WHERE cate = ?")) {
                    chkCate.setString(1, newCate);
                    try (ResultSet rs = chkCate.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) == 0) {
                            System.out.println("❌ Category not found. Add it first or enter an existing category.");
                            return;
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Failed to validate category.");
                    e.printStackTrace();
                    return;
                }
                try (PreparedStatement pst = conn.prepareStatement("UPDATE products SET cate = ? WHERE id = ?")) {
                    pst.setString(1, newCate);
                    pst.setInt(2, id);
                    int updated = pst.executeUpdate();
                    if (updated > 0) System.out.println("Product category updated successfully!");
                    else System.out.println("No rows updated.");
                } catch (SQLException e) {
                    System.err.println("Failed to update category.");
                    e.printStackTrace();
                }
            }
            default -> System.out.println("Incorrect choice. Returning to admin menu.");
        }
    }

    private static void removeProduct(Connection conn) {
        System.out.print("Enter product ID to remove: ");
        int id = readInt();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("✅ Product removed!");
            else System.out.println("❌ No product found with that ID.");
        } catch (SQLException e) {
            System.err.println("Failed to remove product.");
            e.printStackTrace();
        }
    }

    private static void viewBillHistory(Connection conn) {
        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM bill_history ORDER BY purchase_datetime DESC");
             ResultSet rs = pst.executeQuery()) {

            System.out.printf("%-10s | %-25s | %-12s | %-12s | %-12s%n", "Bill ID", "Date & Time", "Total", "Discount", "Final");
            System.out.println("---------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d | %-25s | %-12.2f | %-12.2f | %-12.2f%n",
                        rs.getInt("bill_id"),
                        rs.getTimestamp("purchase_datetime"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount_percent"),
                        rs.getDouble("final_amount"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load bill history.");
            e.printStackTrace();
        }
    }

    private static int readInt() {
        while (true) {
            String line = sc.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter an integer: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            String line = sc.nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a decimal number: ");
            }
        }
    }

    private static String readLine() {
        return sc.nextLine().trim();
    }
    
    private static void removeCategory(Connection conn) {
        System.out.print("Enter category name to remove: ");
        String cate = readLine();

        if (cate.isBlank()) {
            System.out.println("❌ Category name cannot be empty.");
            return;
        }

        // Step 1: Check if category exists
        try (PreparedStatement chk = conn.prepareStatement("SELECT COUNT(*) FROM category WHERE cate = ?")) {
            chk.setString(1, cate);
            try (ResultSet rs = chk.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    System.out.println("❌ Category not found!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking category existence.");
            e.printStackTrace();
            return;
        }

        // Step 2: Check if any products belong to this category
        int productCount = 0;
        try (PreparedStatement countPs = conn.prepareStatement("SELECT COUNT(*) FROM products WHERE cate = ?")) {
            countPs.setString(1, cate);
            try (ResultSet rs = countPs.executeQuery()) {
                if (rs.next()) {
                    productCount = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting products in category.");
            e.printStackTrace();
            return;
        }

        // Step 3: If products exist, ask for confirmation
        if (productCount > 0) {
            System.out.println("⚠️  There are " + productCount + " products in this category.");
            System.out.print("Do you want to delete all products in this category as well? (y/n): ");
            String confirm = readLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("❌ Operation cancelled. Category not deleted.");
                return;
            }

            // Step 4: Delete products first
            try (PreparedStatement delProd = conn.prepareStatement("DELETE FROM products WHERE cate = ?")) {
                delProd.setString(1, cate);
                int removed = delProd.executeUpdate();
                System.out.println("🧺 Deleted " + removed + " products from this category.");
            } catch (SQLException e) {
                System.err.println("Error deleting products from category.");
                e.printStackTrace();
                return;
            }
        }

        // Step 5: Delete category itself
        try (PreparedStatement delCate = conn.prepareStatement("DELETE FROM category WHERE cate = ?")) {
            delCate.setString(1, cate);
            int rows = delCate.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Category '" + cate + "' removed successfully!");
            else
                System.out.println("❌ Category not found during deletion (possibly removed by another operation).");
        } catch (SQLException e) {
            System.err.println("Failed to remove category.");
            e.printStackTrace();
        }
    }

}


