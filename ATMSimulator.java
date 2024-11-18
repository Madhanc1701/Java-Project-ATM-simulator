import java.sql.*;
import java.util.Scanner;

public class ATMSimulator {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/atm";
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = ""; // Replace with your database password

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to the ATM!");
            System.out.print("Enter your account number: ");
            String accountNumber = scanner.nextLine();

            System.out.print("Enter your PIN: ");
            int pin = scanner.nextInt();

            if (authenticateUser(connection, accountNumber, pin)) {
                System.out.println("Login successful!");

                while (true) {
                    System.out.println("\nATM Menu:");
                    System.out.println("1. Check Balance");
                    System.out.println("2. Deposit");
                    System.out.println("3. Withdraw");
                    System.out.println("4. Exit");
                    System.out.print("Choose an option: ");
                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            checkBalance(connection, accountNumber);
                            break;
                        case 2:
                            System.out.print("Enter amount to deposit: ");
                            double depositAmount = scanner.nextDouble();
                            deposit(connection, accountNumber, depositAmount);
                            break;
                        case 3:
                            System.out.print("Enter amount to withdraw: ");
                            double withdrawAmount = scanner.nextDouble();
                            withdraw(connection, accountNumber, withdrawAmount);
                            break;
                        case 4:
                            System.out.println("Thank you for using the ATM. Goodbye!");
                            return;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                }
            } else {
                System.out.println("Invalid account number or PIN.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean authenticateUser(Connection connection, String accountNumber, int pin) throws SQLException {
        String query = "SELECT * FROM atmsimulator WHERE account_number = ? AND pin = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setInt(2, pin);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static void checkBalance(Connection connection, String accountNumber) throws SQLException {
        String query = "SELECT balance FROM atmsimulator WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Your current balance is: " + resultSet.getDouble("balance"));
                }
            }
        }
    }

    private static void deposit(Connection connection, String accountNumber, double amount) throws SQLException {
        String query = "UPDATE atmsimulator SET balance = balance + ? WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, accountNumber);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Deposit successful! Amount deposited: " + amount);
            }
        }
    }

    private static void withdraw(Connection connection, String accountNumber, double amount) throws SQLException {
        String query = "SELECT balance FROM atmsimulator WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");
                    if (currentBalance >= amount) {
                        String updateQuery = "UPDATE atmsimulator SET balance = balance - ? WHERE account_number = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setDouble(1, amount);
                            updateStatement.setString(2, accountNumber);
                            int rowsUpdated = updateStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                System.out.println("Withdrawal successful! Amount withdrawn: " + amount);
                            }
                        }
                    } else {
                        System.out.println("Insufficient balance.");
                    }
                }
            }
        }
    }
}
