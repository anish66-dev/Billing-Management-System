package generalFun;
public class Product {
    private final int id;
    private final String name;
    private final String descrip;
    private final double price;
    private final int quantity;
    private final String cate;

    public Product(int id, String name, String descrip, double price, int quantity, String cate) {
        this.id = id;
        this.name = name;
        this.descrip = descrip == null ? "No Description" : descrip;
        this.price = price;
        this.quantity = quantity;
        this.cate = cate;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescrip() { return descrip; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getCate() { return cate; }

    @Override
    public String toString() {
        return String.format("%d | %s | %.2f | %d", id, name, price, quantity);
    }
}
