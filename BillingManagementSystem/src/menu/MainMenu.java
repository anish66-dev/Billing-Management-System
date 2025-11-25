package menu;
public class MainMenu {
    public static void main(String[] args) {
        try (java.util.Scanner sc = new java.util.Scanner(System.in)) {
			System.out.println("Welcome to the Billing System App");
			System.out.print("Are you admin or user: ");
			String choice = sc.nextLine().toLowerCase();
			switch (choice) {
			    case "admin" -> new AdminMenu().showMenu();
			    case "user" -> new CustomerMenu().showMenu();
			    default -> System.out.println("Error");
			}
		}
    }
}

