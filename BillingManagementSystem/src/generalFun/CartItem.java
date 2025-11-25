package generalFun;
public class CartItem {
    private final int productId;
    private final int quantity;

    public CartItem(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
