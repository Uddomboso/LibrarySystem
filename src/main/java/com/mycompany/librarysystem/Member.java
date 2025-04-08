/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.librarysystem;

/**
 *
 * @author asus
 */

// Member.java
import java.sql.*;
import java.util.Scanner;

public class Member {
    private int memberId;
    private String username;

    public Member(int memberId, String username) {
        this.memberId = memberId;
        this.username = username;
    }

    public int getMemberId() {
        return memberId;
    }

    public static Member login(Connection conn, Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String query = "SELECT memberid FROM member WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int memberId = rs.getInt("memberid");
                return new Member(memberId, username);
            } else {
                System.out.println("Invalid credentials.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }
    }

    public static Member signup(Connection conn, Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String insert = "INSERT INTO member (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int memberId = rs.getInt(1);
                System.out.println("Signup successful.");
                return new Member(memberId, username);
            }
        } catch (SQLException e) {
            System.err.println("Signup error: " + e.getMessage());
        }
        return null;
    }

    public void showDashboard(Connection conn, Scanner sc) {
        while (true) {
            System.out.println("\n--- Member Dashboard ---");
            System.out.println("1. View Available Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Donate Book");
            System.out.println("4. Search Books");
            System.out.println("4. Return Book");
            System.out.println("6. Logout");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> Book.showAvailableBooks(conn);
                case 2 -> Borrow.borrowBook(conn, sc, memberId);
                case 3 -> Borrow.donateBook(conn, sc, memberId);
                case 4 -> Borrow.searchBooks(conn, sc);
                case 5 -> Borrow.returnBook(conn, sc, memberId);
                case 6 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}

