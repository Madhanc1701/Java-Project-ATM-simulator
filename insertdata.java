import java.sql.*;

public class insertdata {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/atm";
        String username = "root";
        String password = "";

        String insertSQL = "INSERT INTO atmsimulator (account_number, pin, balance) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = connection.prepareStatement(insertSQL)) {

            // Set values for the placeholders
            stmt.setString(1, "9876543210");
            stmt.setString(2, "1234");
            stmt.setDouble(3, 100000.00);

            // Execute the insert command
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Record inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
