package generalFun;
import java.util.List;

public class GeneralClasses {
    public static void viewProducts() {
        String sql = "SELECT * FROM products";
        List<Product> products = DBHelper.queryList(sql, null, rs -> new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("descrip"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                rs.getString("cate")
        ));

        System.out.println("\nAvailable Products:");
        System.out.println("ID | Name | Price | Quantity");
        for (Product p : products) {
            System.out.println(p.toString());
        }
    }
}

