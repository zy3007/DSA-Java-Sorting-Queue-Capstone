import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class EzliciousOrderSystem {

    static Scanner input = new Scanner(System.in);

    static String[] menuCategory = {
        "THE MAIN EVENT (Burgers)", "THE MAIN EVENT (Burgers)", "THE MAIN EVENT (Burgers)",
        "EZ-BITES (Chicken & Wings)", "EZ-BITES (Chicken & Wings)", "EZ-BITES (Chicken & Wings)",
        "ON THE SIDE", "ON THE SIDE", "ON THE SIDE",
        "SIPS & SHAKES", "SIPS & SHAKES", "SIPS & SHAKES",
        "SWEET TREATS (Desserts)", "SWEET TREATS (Desserts)"
    };
    static String[] menuName = {
        "The Classic Ez-Burger", "The Double Smash", "The Spicy Volcano Burger",
        "Garlic Parm Wings (6 pcs)", "Sticky Sweet BBQ Pops", "Crunchy Chicken Tenders",
        "Loaded Bacon & Cheese Fries", "Golden Onion Rings", "Cheesy Mac Bites",
        "Creamy Vanilla Shake", "Double Choco Fudge Shake", "Ez-Lemon Iced Tea",
        "Warm Brownie Sundae", "Fried Apple Pie Bites"
    };
    static double[] menuPrice = {
        250.00, 350.00, 280.00,
        400.00, 300.00, 220.00,
        150.00, 120.00, 140.00,
        120.00, 140.00, 80.00,
        150.00, 100.00
    };

    static ArrayList<Integer> orderNumbers = new ArrayList<>();
    static ArrayList<String> customerNames = new ArrayList<>();
    static ArrayList<String> orderDetails = new ArrayList<>();
    static ArrayList<Double> orderTotals = new ArrayList<>();
    static ArrayList<String> orderStatus = new ArrayList<>();

    static Queue<Integer> prepQueue = new LinkedList<>();

    static int nextOrderNumber = 1001;

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            showMainMenu();
            int choice = readInt("Enter your choice (1-11): ", 1, 11);

            switch (choice) {
                case 1:  displayMenu(); break;
                case 2:  takeNewOrder(); break;
                case 3:  prepareNextOrder(); break;
                case 4:  peekNextOrder(); break;
                case 5:  displayQueue(); break;
                case 6:  checkQueueEmpty(); break;
                case 7:  displayAllOrders(); break;
                case 8:  displayPendingAndCompleted(); break;
                case 9:  searchOrder(); break;
                case 10: sortOrdersMenu(); break;
                case 11:
                    System.out.println("\nThank you for using the Ezlicious Order System. Goodbye!");
                    running = false;
                    break;
            }
        }
        input.close();
    }

    static void showMainMenu() {
        System.out.println("\n==============================================");
        System.out.println("   EZLICIOUS - ORDER & PREPARATION SYSTEM");
        System.out.println("==============================================");
        System.out.println(" 1. View Menu");
        System.out.println(" 2. Take New Order (adds to preparation queue)");
        System.out.println(" 3. Prepare Next Order (dequeue)");
        System.out.println(" 4. Peek Next Order in Queue");
        System.out.println(" 5. Display Preparation Queue");
        System.out.println(" 6. Check if Queue is Empty");
        System.out.println(" 7. Display All Orders");
        System.out.println(" 8. Display Pending and Completed Orders");
        System.out.println(" 9. Search Order");
        System.out.println("10. Sort Orders");
        System.out.println("11. Exit");
        System.out.println("----------------------------------------------");
    }

    static void displayMenu() {
        System.out.println("\n=========== EZLICIOUS MENU ===========");
        String currentCategory = "";
        for (int i = 0; i < menuName.length; i++) {
            if (!menuCategory[i].equals(currentCategory)) {
                currentCategory = menuCategory[i];
                System.out.println("\n** " + currentCategory + " **");
            }
            System.out.printf(" %2d. %-30s PHP %7.2f%n", (i + 1), menuName[i], menuPrice[i]);
        }
    }

    static void printOrder(int i) {
        System.out.printf("Order #%d | Customer: %s | Total: PHP %.2f | Status: %s%n",
                orderNumbers.get(i), customerNames.get(i), orderTotals.get(i), orderStatus.get(i));
        System.out.println("   Items: " + orderDetails.get(i));
    }

    static int printOrdersByStatus(String statusFilter) {
        int count = 0;
        for (int i = 0; i < orderNumbers.size(); i++) {
            if (statusFilter == null || orderStatus.get(i).equals(statusFilter)) {
                printOrder(i);
                count++;
            }
        }
        return count;
    }

    static void displayAllOrders() {
        System.out.println("\n----------- ALL ORDERS -----------");
        if (orderNumbers.isEmpty()) {
            System.out.println("No orders have been recorded yet.");
            return;
        }
        printOrdersByStatus(null);
    }

    static void displayPendingAndCompleted() {
        if (orderNumbers.isEmpty()) {
            System.out.println("\nNo orders have been recorded yet.");
            return;
        }

        System.out.println("\n----------- PENDING ORDERS -----------");
        if (printOrdersByStatus("Pending") == 0) {
            System.out.println("There are no pending orders.");
        }

        System.out.println("\n----------- COMPLETED ORDERS -----------");
        if (printOrdersByStatus("Completed") == 0) {
            System.out.println("There are no completed orders yet.");
        }
    }

    static void takeNewOrder() {
        System.out.println("\n----------- NEW ORDER -----------");
        String name = readNonEmptyString("Customer name: ");

        displayMenu();
        System.out.println("\nEnter the item number to add. Enter 0 when done.");

        String details = "";
        double total = 0;
        int itemCount = 0;

        while (true) {
            int itemChoice = readInt("Item number (0 to finish): ", 0, menuName.length);
            if (itemChoice == 0) {
                break;
            }
            int index = itemChoice - 1;
            int qty = readInt("Quantity for " + menuName[index] + " (1-50): ", 1, 50);

            double subtotal = menuPrice[index] * qty;
            total += subtotal;
            itemCount++;

            if (!details.isEmpty()) {
                details += ", ";
            }
            details += qty + "x " + menuName[index];

            System.out.printf("Added: %dx %s (PHP %.2f). Running total: PHP %.2f%n",
                    qty, menuName[index], subtotal, total);
        }

        if (itemCount == 0) {
            System.out.println("No items were selected. Order cancelled.");
            return;
        }

        int orderNo = nextOrderNumber++;
        orderNumbers.add(orderNo);
        customerNames.add(name);
        orderDetails.add(details);
        orderTotals.add(total);
        orderStatus.add("Pending");

        prepQueue.add(orderNo);

        System.out.println("\nOrder placed successfully!");
        System.out.printf("Order #%d for %s | Total: PHP %.2f%n", orderNo, name, total);
        System.out.println("Your order is #" + prepQueue.size() + " in the preparation queue.");
    }

    static int findIndexByOrderNumber(int orderNo) {
        for (int i = 0; i < orderNumbers.size(); i++) {
            if (orderNumbers.get(i) == orderNo) {
                return i;
            }
        }
        return -1;
    }

    static void prepareNextOrder() {
        System.out.println();
        if (prepQueue.isEmpty()) {
            System.out.println("The preparation queue is empty. No orders to prepare.");
            return;
        }

        int orderNo = prepQueue.poll();
        int index = findIndexByOrderNumber(orderNo);

        if (index == -1) {
            System.out.println("Error: order #" + orderNo + " could not be found in the records.");
            return;
        }

        orderStatus.set(index, "Completed");
        System.out.println("Now preparing and completing:");
        printOrder(index);
        System.out.println("Orders still waiting: " + prepQueue.size());
    }


    static void peekNextOrder() {
        System.out.println();
        if (prepQueue.isEmpty()) {
            System.out.println("The preparation queue is empty. Nothing to peek at.");
            return;
        }

        int orderNo = prepQueue.peek();
        int index = findIndexByOrderNumber(orderNo);
        System.out.println("Next order to be prepared:");
        if (index != -1) {
            printOrder(index);
        }
    }

    static void displayQueue() {
        System.out.println("\n----------- PREPARATION QUEUE (front to back) -----------");
        if (prepQueue.isEmpty()) {
            System.out.println("The preparation queue is empty.");
            return;
        }

        int position = 1;
        for (int orderNo : prepQueue) {
            int index = findIndexByOrderNumber(orderNo);
            if (index != -1) {
                System.out.printf("%d. Order #%d - %s - PHP %.2f%n",
                        position, orderNo, customerNames.get(index), orderTotals.get(index));
            }
            position++;
        }
        System.out.println("Total waiting: " + prepQueue.size());
    }

    static void checkQueueEmpty() {
        System.out.println();
        if (prepQueue.isEmpty()) {
            System.out.println("Yes, the preparation queue is empty.");
        } else {
            System.out.println("No, the queue is not empty. Orders waiting: " + prepQueue.size());
        }
    }

    static void searchOrder() {
        System.out.println("\n----------- SEARCH ORDER -----------");
        if (orderNumbers.isEmpty()) {
            System.out.println("There are no orders to search.");
            return;
        }

        System.out.println("1. Search by order number");
        System.out.println("2. Search by customer name");
        int choice = readInt("Choose (1-2): ", 1, 2);

        if (choice == 1) {
            int target = readInt("Enter order number: ", 1, Integer.MAX_VALUE);
            int index = findIndexByOrderNumber(target);
            if (index == -1) {
                System.out.println("No order found with number " + target + ".");
            } else {
                System.out.println("\nOrder found:");
                printOrder(index);
            }
        } else {
            String keyword = readNonEmptyString("Enter customer name (or part of it): ").toLowerCase();
            int found = 0;
            for (int i = 0; i < customerNames.size(); i++) {
                if (customerNames.get(i).toLowerCase().contains(keyword)) {
                    if (found == 0) {
                        System.out.println("\nMatching orders:");
                    }
                    printOrder(i);
                    found++;
                }
            }
            if (found == 0) {
                System.out.println("No orders found for \"" + keyword + "\".");
            }
        }
    }

    static void sortOrdersMenu() {
        System.out.println("\n----------- SORT ORDERS -----------");
        if (orderNumbers.size() < 2) {
            System.out.println("You need at least 2 orders to sort.");
            return;
        }

        System.out.println("1. Order number (Bubble Sort)");
        System.out.println("2. Customer name A-Z (Insertion Sort)");
        System.out.println("3. Total amount (Bubble Sort)");
        int choice = readInt("Sort by (1-3): ", 1, 3);

        boolean ascending = true;
        if (choice == 1 || choice == 3) {
            System.out.println("1. Ascending");
            System.out.println("2. Descending");
            ascending = readInt("Order (1-2): ", 1, 2) == 1;
        }

        System.out.println("\nBEFORE SORTING:");
        printOrdersByStatus(null);

        if (choice == 1) {
            bubbleSortByOrderNumber(ascending);
        } else if (choice == 2) {
            insertionSortByName();
        } else {
            bubbleSortByTotal(ascending);
        }

        System.out.println("\nAFTER SORTING:");
        printOrdersByStatus(null);
    }

    static void swapRecords(int i, int j) {
        int tempNo = orderNumbers.get(i);
        orderNumbers.set(i, orderNumbers.get(j));
        orderNumbers.set(j, tempNo);

        String tempName = customerNames.get(i);
        customerNames.set(i, customerNames.get(j));
        customerNames.set(j, tempName);

        String tempDetails = orderDetails.get(i);
        orderDetails.set(i, orderDetails.get(j));
        orderDetails.set(j, tempDetails);

        double tempTotal = orderTotals.get(i);
        orderTotals.set(i, orderTotals.get(j));
        orderTotals.set(j, tempTotal);

        String tempStatus = orderStatus.get(i);
        orderStatus.set(i, orderStatus.get(j));
        orderStatus.set(j, tempStatus);
    }

    static void bubbleSortByOrderNumber(boolean ascending) {
        int n = orderNumbers.size();
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int i = 0; i < n - 1 - pass; i++) {
                int a = orderNumbers.get(i);
                int b = orderNumbers.get(i + 1);
                boolean outOfOrder = ascending ? (a > b) : (a < b);
                if (outOfOrder) {
                    swapRecords(i, i + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    static void bubbleSortByTotal(boolean ascending) {
        int n = orderTotals.size();
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int i = 0; i < n - 1 - pass; i++) {
                double a = orderTotals.get(i);
                double b = orderTotals.get(i + 1);
                boolean outOfOrder = ascending ? (a > b) : (a < b);
                if (outOfOrder) {
                    swapRecords(i, i + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    static void insertionSortByName() {
        for (int i = 1; i < customerNames.size(); i++) {
            int j = i;
            while (j > 0 && customerNames.get(j - 1).compareToIgnoreCase(customerNames.get(j)) > 0) {
                swapRecords(j - 1, j);
                j--;
            }
        }
    }

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Invalid choice. Please enter a number from " + min
                        + " to " + (max == Integer.MAX_VALUE ? "the maximum" : String.valueOf(max)) + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }
}