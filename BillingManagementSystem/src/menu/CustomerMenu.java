package menu;
import generalFun.CustomerFunc;
public class CustomerMenu extends BaseMenu {
    @Override 
    public void showMenu() { 
    	custMenu(); 
    }
    public static void custMenu() {
        try (java.util.Scanner sc = new java.util.Scanner(System.in)) {
			while (true) {
			    System.out.println("\n=== CUSTOMER MENU ===");
			    System.out.println("1. View All Categories");
			    System.out.println("2. Search Product by Name");
			    System.out.println("3. View Cart");
			    System.out.println("4. Exit");
			    System.out.print("Enter choice: ");
			    int choice = Integer.parseInt(sc.nextLine());
			    switch (choice) {
			        case 1 -> CustomerFunc.selectCate();
			        case 2 -> CustomerFunc.searchProduct();
			        case 3 -> CustomerFunc.viewCart();
			        case 4 -> { System.out.println("Thank you for shopping!"); CustomerFunc.clearCart(false); System.exit(0); }
			        default -> System.out.println("Invalid choice!");
			    }
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
    }
}

