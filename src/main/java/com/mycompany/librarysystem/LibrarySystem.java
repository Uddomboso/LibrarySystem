/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.librarysystem;

/**
 *
 * @author asus
 */
import java.sql.*;
import java.util.*;

public class LibrarySystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "29102001";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void main(String[] args) {
               try (Connection conn = getConnection(); Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n--- Library System ---");
                System.out.println("1. Login");
                System.out.println("2. Sign Up");
                System.out.println("3. Exit");
                System.out.print("Choose: ");
                int choice = sc.nextInt();
                sc.nextLine();

                Member user = null;

                switch (choice) {
                    case 1 -> user = Member.login(conn, sc);
                    case 2 -> user = Member.signup(conn, sc);
                    case 3 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }

                if (user != null) {
                    user.showDashboard(conn, sc);
                }
            }
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage() );
        }
}
}
