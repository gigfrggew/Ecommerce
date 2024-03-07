package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class Item {
    String itemName;
    double itemPrice;

    public Item(String itemName, double itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }
}

class Mycart {
    String itemName;
    double itemPrice;

    public Mycart(String itemName, double itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }
}

class Order {
    String itemName;
    double itemPrice;

    public Order(String itemName, double itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }
}

class Payment {
    String address;
    String paymentMethod;

    public Payment(String address, String paymentMethod) {
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    public void processPayment(double totalCost) {
        System.out.println("\nPayment Details:");
        System.out.println("Address: " + address);
        System.out.println("Total Cost: Rs" + totalCost);
        System.out.println("Payment Method: " + paymentMethod);

        if (paymentMethod.equalsIgnoreCase("Online Payment")) {
            System.out.println("Processing online payment...");
            // Add online payment logic here
            System.out.println("Online payment successful. Thank you for your order!");
        } else if (paymentMethod.equalsIgnoreCase("Cash on Delivery")) {
            System.out.println("Please have exact cash ready for the delivery. Thank you for your order!");
        } else {
            System.out.println("Invalid payment method. Payment failed.");
        }
    }

    public double calculateTotalCost(ArrayList<? extends Object> items) {
        double totalCost = 0;
        for (Object item : items) {
            if (item instanceof Mycart) {
                totalCost += ((Mycart) item).itemPrice;
            } else if (item instanceof Order) {
                totalCost += ((Order) item).itemPrice;
            }
        }
        return totalCost;
    }

    public void addToOrder(Item selectedItem, ArrayList<Order> orderList) {
        // Add logic to create an Order and add it to orderList
        Order orderedItem = new Order(selectedItem.itemName, selectedItem.itemPrice);
        orderList.add(orderedItem);
        System.out.println("Item added to your order: " + selectedItem.itemName);
    }

    public void addToShoppingCart(Item selectedItem, ArrayList<Mycart> shoppingCart) {
        // Add logic to create a Mycart item and add it to shoppingCart
        Mycart cartItem = new Mycart(selectedItem.itemName, selectedItem.itemPrice);
        shoppingCart.add(cartItem);
        System.out.println("Item added to your shopping cart: " + selectedItem.itemName);
    }

    // Add a method to save order details to the database
    public void saveOrderToDatabase(ArrayList<Order> orderList, String username) {
        // JDBC connection parameters
        String jdbcUrl = "jdbc:mysql://localhost:3306/your_database_name";
        String dbUsername = "your_username";
        String dbPassword = "your_password";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            // Prepare a SQL statement to insert order details into the database
            String insertOrderSQL = "INSERT INTO orders (item_name, item_price, username) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrderSQL)) {
                // Loop through orderList and insert each order into the database
                for (Order orderedItem : orderList) {
                    preparedStatement.setString(1, orderedItem.itemName);
                    preparedStatement.setDouble(2, orderedItem.itemPrice);
                    preparedStatement.setString(3, username);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class ProductDetails {
    public static void displayDetails(ArrayList<Order> orderList, String address, String paymentMethod) {
        System.out.println("\nOrdered Item Details:");
        for (Order orderedItem : orderList) {
            System.out.println("Name: " + orderedItem.itemName + " Rs" + orderedItem.itemPrice);
        }

        System.out.println("\nDelivery Address: " + address);
        System.out.println("Payment Method: " + paymentMethod);
    }
}

public class Ecommerce {
    // Example: Create some sample items and add them to the item page
    static ArrayList<Item> itemPage = new ArrayList<>();
    static {
        itemPage.add(new Item("Redmi 10", 18000));
        itemPage.add(new Item("realme 12", 17000));
        itemPage.add(new Item("oppo F12", 12000));
        itemPage.add(new Item("samsung F22", 13000));
        itemPage.add(new Item("IQ100", 10000));
    }

    // Scanner for user input
    static Scanner scanner = new Scanner(System.in);

    // Example: Create a sample shopping cart
    static ArrayList<Mycart> shoppingCart = new ArrayList<>();

    // Example: Create a sample order list
    static ArrayList<Order> orderList = new ArrayList<>();

    // Example: Create an instance of Payment
    static Payment payment = new Payment("", "");

    // Example: Create an instance of User
    static User user;

    public static void main(String[] args) {
        // Take user login details
        takeUserLoginDetails();

        // Allow the user to select an item
        Item selectedItem = selectItem();

        // Ask the user whether to add the selected item to the cart or order
        System.out.println("1. Add to Order");
        System.out.println("2. Add to Shopping Cart");
        System.out.print("Enter your choice: ");
        int userChoice = scanner.nextInt();

        switch (userChoice) {
            case 1:
                // Add to order
                payment.addToOrder(selectedItem, orderList);

                // Save order details to the database
                payment.saveOrderToDatabase(orderList, user.username);
                break;

            case 2:
                // Add to shopping cart
                payment.addToShoppingCart(selectedItem, shoppingCart);
                System.out.println("Item added to your shopping cart: " + selectedItem.itemName);

                // Ask the user whether to view the cart
                System.out.print("Do you want to view your shopping cart? (Enter 'yes' or 'no'): ");
                String viewCartChoice = scanner.next();

                if (viewCartChoice.equalsIgnoreCase("yes")) {
                    payment.viewShoppingCart(shoppingCart);

                    // Ask the user whether to order the item from the cart
                    System.out.print("Do you want to order this item from the cart? (Enter 'yes' or 'no'): ");
                    String orderItemChoice = scanner.next();

                    if (orderItemChoice.equalsIgnoreCase("yes")) {
                        payment.addToOrder(selectedItem, orderList);
                        System.out.println("Item ordered successfully from the cart: " + selectedItem.itemName);

                        // Ask the user for address and payment details
                        takeAddressAndPaymentDetails();

                        // Display ordered item details using ProductDetails class
                        ProductDetails.displayDetails(orderList, payment.address, payment.paymentMethod);

                        // Process the payment
                        double totalCost = payment.calculateTotalCost(orderList);
                        payment.processPayment(totalCost);

                        // Save order details to the database
                        payment.saveOrderToDatabase(orderList, user.username);
                    } else {
                        System.out.println("Thank you for shopping!");
                    }
                } else {
                    System.out.println("Thank you for shopping!");
                }
                break;
            default:
                System.out.println("Invalid choice");
        }

        // Close the scanner
        scanner.close();
    }

    static void takeUserLoginDetails() {
        System.out.println("Enter User Login Details:");
        System.out.print("Username: ");
        String username = scanner.next();
        System.out.print("Password: ");
        String password = scanner.next();

        // Create User instance
        user = new User(username, password);
        System.out.println("User logged in successfully!");
    }

    static Item selectItem() {
        System.out.println("Item Page:");
        for (int i = 0; i < itemPage.size(); i++) {
            System.out.println((i + 1) + ". " + itemPage.get(i).itemName + " Rs" + itemPage.get(i).itemPrice);
        }

        int choice = -1;

        while (choice < 0 || choice > itemPage.size()) {
            System.out.print("Select an item (1-" + itemPage.size() + "): ");
            try {
                choice = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric choice.");
            }
        }

        return itemPage.get(choice - 1);
    }

    static void takeAddressAndPaymentDetails() {
        System.out.print("\nEnter your address for delivery: ");
        scanner.nextLine(); // Consume the newline character
        payment.address = scanner.nextLine();

        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Online Payment");
        System.out.println("2. Cash on Delivery");
        System.out.print("Enter your choice: ");
        int paymentChoice = scanner.nextInt();

        if (paymentChoice == 1) {
            payment.paymentMethod = "Online Payment";
        } else if (paymentChoice == 2) {
            payment.paymentMethod = "Cash on Delivery";
        } else {
            System.out.println("Invalid choice. Payment failed.");
            System.exit(1);
        }

        System.out.println("Address and Payment details saved!");
    }
}
