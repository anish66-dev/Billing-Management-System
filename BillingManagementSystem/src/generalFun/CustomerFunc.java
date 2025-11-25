package generalFun;
import java.sql.*;
import java.util.*;
import menu.CustomerMenu;

public class CustomerFunc {
    private static String cate;
    private static final Scanner sc = new Scanner(System.in);

    public static void selectCate() {
        try {
            List<String> cats = DBHelper.queryList("SELECT cate FROM category", null, rs -> rs.getString("cate"));
            if (cats.isEmpty()) {
                System.out.println("No categories available.");
                return;
            }
            System.out.println("\nCategories are");
            for (int i = 0; i < cats.size(); i++) {
                System.out.println((i + 1) + ". " + cats.get(i));
            }
            System.out.println("Select Categaory: ");
            int sel = sc.nextInt();
            sc.nextLine();
            if (sel < 1 || sel > cats.size()) {
                System.out.println("Invalid selection!"); selectCate(); return;
            }
            cate = cats.get(sel - 1);
            viewProducts();
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static void viewProducts() {
        try {
            String sql = "SELECT * FROM products WHERE cate = ? and quantity >= 0";
            List<Product> products = DBHelper.queryList(sql, ps -> ps.setString(1, cate),
                    rs -> new Product(rs.getInt("id"), rs.getString("name"), rs.getString("descrip"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("cate")));

            System.out.println("\nAvailable Products:");
            System.out.println("ID | Name | Price | Quantity");
            for (Product p : products) {
                if (p.getQuantity() > 0) System.out.println(p);
            }

            System.out.println("Choose");
            System.out.println("1. See Product details");
            System.out.println("2. Return to main menu");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter Prod Id:");
                    int id = sc.nextInt();
                    sc.nextLine();
                    selectedProduct(id);
                }
                case 2 -> CustomerMenu.custMenu();
                default -> {
                    System.out.println("Invalid choice!"); viewProducts();
                }
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

	private static void selectedProduct(int id) { 
		selectedProduct(id, false); 
	}

    private static void selectedProduct(int id, boolean fromCart) {
        try {
            List<Product> products = DBHelper.queryList("SELECT * FROM products WHERE id = ?", ps -> ps.setInt(1, id),
                    rs -> new Product(rs.getInt("id"), rs.getString("name"), rs.getString("descrip"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("cate")));

            System.out.println("\nProducts Details:");
            System.out.println("Name | Price | Quantity | Description ");
            if (products.isEmpty()) {
                System.out.println("❌ Product not found!"); if (!fromCart) viewProducts(); return;
            }
            Product p = products.get(0);
            System.out.printf("%s | %.2f | %d | %s%n", p.getName(), p.getPrice(), p.getQuantity(), p.getDescrip());

            if (fromCart) { viewCart(); return; }

            System.out.println("Do you wish to add to cart: (y/n)"); String choice = sc.nextLine();
            switch (choice.toLowerCase()) {
                case "y" -> addToCart(p.getId());
                case "n" -> viewProducts();
                default -> selectedProduct(id, fromCart);
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static void searchProduct() {
        System.out.print("Enter product name: "); String name = sc.nextLine();
        try {
            List<Product> products = DBHelper.queryList("SELECT * FROM products WHERE name LIKE ?", ps -> ps.setString(1, "%" + name + "%"),
                    rs -> new Product(rs.getInt("id"), rs.getString("name"), rs.getString("descrip"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("cate")));

            boolean found = !products.isEmpty();
            System.out.println("\nSearch Results:"); System.out.println("ID | Name | Price | Qty");
            for (Product p : products) System.out.printf("%d | %s | %.2f | %d%n", p.getId(), p.getName(), p.getPrice(), p.getQuantity());

            System.out.println("Do you wish to get product details : (y/n)"); String choice = sc.nextLine();
            switch (choice.toLowerCase()) {
                case "y" -> {
                    if (found) {
                        System.out.print("Enter Prod Id: "); int id = Integer.parseInt(sc.nextLine());
                        selectedProduct(id, false);
                    } else { System.out.println("No products found!"); viewProducts(); }
                }
                case "n" -> viewProducts();
                default -> searchProduct();
            }
            if (!found) System.out.println("No products found!");
        } catch (DBException e) { e.printStackTrace(); }
    }

    private static void addToCart(int pid) {
        System.out.print("Enter quantity: "); int qty = Integer.parseInt(sc.nextLine());
        try (Connection conn = db.DBConnect.getConnection()) {
            PreparedStatement chk = conn.prepareStatement("SELECT name, quantity FROM products WHERE id=?");
            chk.setInt(1, pid);
            ResultSet rs = chk.executeQuery();
            if (rs.next()) {
                int available = rs.getInt("quantity"); String name = rs.getString("name");
                if (qty > 0 && qty <= available) {
                    PreparedStatement existing = conn.prepareStatement("SELECT quantity FROM cart WHERE product_id=?");
                    existing.setInt(1, pid);
                    ResultSet ex = existing.executeQuery();
                    if (ex.next()) {
                        int oldQty = ex.getInt("quantity"); PreparedStatement update = conn.prepareStatement("UPDATE cart SET quantity=? WHERE product_id=?");
                        update.setInt(1, oldQty + qty); update.setInt(2, pid); update.executeUpdate();
                    } else {
                        PreparedStatement insert = conn.prepareStatement("INSERT INTO cart (product_id, quantity) VALUES (?, ?)"); insert.setInt(1, pid); insert.setInt(2, qty); insert.executeUpdate();
                    }
                    System.out.println("✅ Added " + qty + " × " + name + " to cart.");
                } else { System.out.println("❌ Invalid quantity or not enough stock."); viewProducts();}
            } else { System.out.println("❌ Product not found!"); }
        } catch (SQLException e) { throw new DBException("Failed addToCart", e); }
    }

    public static void viewCart() {
        try {
            String sql = "SELECT c.product_id, p.name, p.price, c.quantity, (p.price * c.quantity) AS total FROM cart c JOIN products p ON c.product_id = p.id";
            List<CartLine> lines = DBHelper.queryList(sql, null, rs -> new CartLine(rs.getInt("product_id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("quantity"), rs.getDouble("total")));
            double grandTotal = 0;
            System.out.println("\n🛒 CART CONTENTS:"); System.out.println("ID | Name | Price | Qty | Total");
            if (lines.isEmpty()) { System.out.println("Cart is empty!"); return; }
            for (CartLine cl : lines) {
                System.out.printf("%d | %s | %.2f | %d | %.2f%n", cl.getProductId(), cl.getName(), cl.getPrice(), cl.getQuantity(), cl.getTotal());
                grandTotal += cl.getTotal();
            }
            System.out.printf("%n💰 Cart Total: ₹%.2f%n", grandTotal);
            System.out.println("\nChoice"); System.out.println("1. Product Details"); System.out.println("2. Purchase items in cart"); System.out.println("3. Remove Item from cart"); System.out.println("4. Clear Cart"); System.out.println("5. Return to main menu");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1 -> { System.out.print("Select Product ID: "); int id = Integer.parseInt(sc.nextLine()); selectedProduct(id, true); }
                case 2 -> buyProducts();
                case 3 -> removeFromCart();
                case 4 -> clearCart(true);
                case 5 -> CustomerMenu.custMenu();
                default -> viewCart();
            }
        } catch (DBException e) { e.printStackTrace(); }
    }

    protected static void removeFromCart() {
        System.out.print("Enter product ID: "); int pid = Integer.parseInt(sc.nextLine());
        System.out.print("Enter quantity: "); int rmqty = Integer.parseInt(sc.nextLine());
        try (Connection conn = db.DBConnect.getConnection()) {
            PreparedStatement chk = conn.prepareStatement("SELECT quantity FROM cart WHERE product_id=?"); chk.setInt(1, pid); ResultSet rs = chk.executeQuery();
            if (rs.next()) {
                int ctqty = rs.getInt("quantity"); if (rmqty <= ctqty) {
                    PreparedStatement update = conn.prepareStatement("UPDATE cart SET quantity=? WHERE product_id=?"); update.setInt(1, ctqty - rmqty); update.setInt(2, pid); update.executeUpdate();
                    PreparedStatement del = conn.prepareStatement("DELETE FROM cart WHERE quantity <= 0"); del.executeUpdate();
                    System.out.println("✅ Removed from cart."); viewCart();
                } else { System.out.println("You dont have that much in cart!"); viewCart(); }
            } else { System.out.println("That item is not in cart!"); viewCart(); }
        } catch (SQLException e) { 
        	throw new DBException("Failed removeFromCart", e); 
        }
    }

    public static void clearCart(boolean cart) {
        try { 
        	DBHelper.update("DELETE FROM cart", null); 
        	if (cart) System.out.println("🧺 Cart cleared successfully!"); 
        } catch (DBException e) { 
        	e.printStackTrace(); 
        }
    }

    protected static void buyProducts() {
        try (Connection conn = db.DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "SELECT c.product_id, p.name, p.price, c.quantity FROM cart c JOIN products p ON c.product_id = p.id";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            boolean empty = true; double total = 0;
            List<CartLine> lines = new ArrayList<>();
            while (rs.next()) {
                empty = false;
                int pid = rs.getInt("product_id"); int qty = rs.getInt("quantity"); double price = rs.getDouble("price"); total += qty * price;
                lines.add(new CartLine(pid, rs.getString("name"), price, qty, qty * price));
            }
            if (empty) { System.out.println("Cart is empty!"); conn.rollback(); return; }
            // check stock
            for (CartLine cl : lines) {
                PreparedStatement pst = conn.prepareStatement("SELECT quantity FROM products WHERE id=?"); pst.setInt(1, cl.getProductId()); ResultSet prs = pst.executeQuery();
                if (prs.next()) {
                    int stock = prs.getInt("quantity"); if (cl.getQuantity() > stock) { System.out.println("❌ Not enough stock for " + cl.getName()); conn.rollback(); return; }
                } else { System.out.println("❌ Product not found: " + cl.getProductId()); conn.rollback(); return; }
                PreparedStatement update = conn.prepareStatement("UPDATE products SET quantity = quantity - ? WHERE id = ?"); update.setInt(1, cl.getQuantity()); update.setInt(2, cl.getProductId()); update.executeUpdate();
            }
            PreparedStatement billPst = conn.prepareStatement("INSERT INTO bill_history (total_amount, discount_percent, final_amount) VALUES (?, ?, ?)"); billPst.setDouble(1, total);
            double discount = 0;
            System.out.print("Do you have valid coupon? (yes/no): "); String ans = sc.nextLine();
            if (ans.equalsIgnoreCase("yes")) {
                System.out.print("Enter coupon code: "); String code = sc.nextLine();
                PreparedStatement pst = conn.prepareStatement("SELECT discount_percent FROM coupons WHERE coupon_code = ? AND expiry_date >= CURDATE()"); pst.setString(1, code); ResultSet rs2 = pst.executeQuery();
                if (rs2.next()) { discount = rs2.getDouble("discount_percent"); System.out.println("Coupon applied! " + discount + "% discount."); }
                else { System.out.println("Invalid or expired coupon!"); }
            }
            double finalAmount = total - (total * discount / 100);
            billPst.setDouble(2, discount); billPst.setDouble(3, finalAmount); billPst.executeUpdate();
            conn.commit(); clearCart(false); System.out.printf("\n✅ Purchase complete! Total bill: ₹%.2f%n", finalAmount);
        } catch (SQLException e) { throw new DBException("Failed buyProducts", e); }
    }

    private static class CartLine {
        private final int productId;
        private final String name;
        private final double price;
        private final int quantity;
        private final double total;
        CartLine(int productId, String name, double price, int quantity, double total) {
            this.productId = productId; this.name = name; this.price = price; this.quantity = quantity; this.total = total;
        }
        public int getProductId() { 
        	return productId; 
        } 
        public String getName() { 
        	return name; 
        } 
        public double getPrice() { 
        	return price; 
        } 
        public int getQuantity() { 
        	return quantity; 
        } 
        public double getTotal() { 
        	return total; 
        }
    }
}
