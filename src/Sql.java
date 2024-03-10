import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class Sql {

    private static Connection connect;

    public static void connectingToSQL() {
        String host = "jdbc:mysql://127.0.0.1:3306/new_schema";
        String username = "root";
        String password = "kesem123";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection(host, username, password);
            System.out.println("Connection successful");
            showMenu();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteStatement(String id) {
        String sqlSelect = "SELECT name FROM new_schema.students WHERE id = ?";
        String sqlDelete = "DELETE FROM new_schema.students WHERE id = ?";
        try (PreparedStatement pstSelect = connect.prepareStatement(sqlSelect)) {
            pstSelect.setString(1, id);
            try (ResultSet result = pstSelect.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    System.out.println("Deleting: " + name);
                    try (PreparedStatement pstDelete = connect.prepareStatement(sqlDelete)) {
                        pstDelete.setString(1, id);
                        int rowsAffected = pstDelete.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Deleted successfully.");
                        } else {
                            System.out.println("No records found to delete.");
                        }
                    }
                } else {
                    System.out.println("No person found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            showMenu();
        }
    }

    public static void updateStatement(String newName, String id) {
        String sqlUpdate = "UPDATE new_schema.students SET name = ? WHERE id = ?";
        try (PreparedStatement pst = connect.prepareStatement(sqlUpdate)) {
            pst.setString(1, newName);
            pst.setString(2, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertStatement(String id, String name, String phone) {
        String sqlInsert = "INSERT INTO new_schema.students (id, name, phone) VALUES (?, ?, ?)";
        try (PreparedStatement pst = connect.prepareStatement(sqlInsert)) {
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, phone);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student Added Successfully.");
            } else {
                System.out.println("Failed to add student.");
            }
            showMenu();
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            e.printStackTrace();
            showMenu();
        }
    }

    public static void checkStudentById(String studentId) {
        // Adjust the SQL query to select all columns (*) from the students table for the given ID
        String sqlSelect = "SELECT * FROM new_schema.students WHERE id = ?";
        try (PreparedStatement statement = connect.prepareStatement(sqlSelect)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String id = result.getString("id");
                    String name = result.getString("name");
                    String phone = result.getString("phone");
                    System.out.println("ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Phone: " + phone);
                    showMenu();
                } else {
                    System.out.println("No data found.");
                    showMenu();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStudentById(String studentId, String name, String phone) {
        String sqlUpdate = "UPDATE new_schema.students SET name = ?, phone = ? WHERE id = ?";
        try (PreparedStatement statement = connect.prepareStatement(sqlUpdate)) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, studentId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student data updated successfully.");
            }
            showMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showMenu() {
        System.out.println("What you want to do?");
        System.out.println("1 - Add student.");
        System.out.println("2 - Check if user student.");
        System.out.println("3 - Delete student.");
        System.out.println("4 - Update student details.");

        Scanner scanner = new Scanner(System.in);
        int userInput = scanner.nextInt();
        switch (userInput) {
            case 1 -> addUser();
            case 2 -> checkUser();
            case 3 -> deleteUser();
            case 4 -> updateStudent();
        }
    }

    public static void addUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Student id:");
        String userId = scanner.nextLine();
        System.out.println("Student name:");
        String userName = scanner.nextLine();
        System.out.println("Student phone:");
        String userPhone = scanner.nextLine();
        Sql.insertStatement(userId, userName, userPhone);
    }

    public static void checkUser() {
        System.out.println("Please insert user ID for check.");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        checkStudentById(userInput);
    }

    public static void deleteUser() {
        System.out.println("Please insert user ID for delete.");
        Scanner scanner = new Scanner(System.in);
        String userId = scanner.nextLine();
        deleteStatement(userId);
    }

    public static void updateStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Student id:");
        String userId = scanner.nextLine();
        if (studentExists(userId)) {
            System.out.println("Updated student name:");
            String userName = scanner.nextLine();
            System.out.println("Updated student phone:");
            String userPhone = scanner.nextLine();
            updateStudentById(userId, userName, userPhone);
        } else {
            System.out.println("No data found");
            showMenu();
        }
    }


    private static boolean studentExists(String studentId) {
        String sqlCheck = "SELECT id FROM new_schema.students WHERE id = ?";
        try (PreparedStatement statement = connect.prepareStatement(sqlCheck)) {
            statement.setString(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Return true if there's at least one result
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
