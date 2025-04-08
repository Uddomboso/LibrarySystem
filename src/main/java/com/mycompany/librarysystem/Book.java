/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.librarysystem;

/**
 *
 * @author asus
 */
import java.sql.*;
import java.util.*;

public class Book {
    public static void showAvailableBooks(Connection conn) {
        System.out.println("\n--- Available Books ---");
        String query = "SELECT * FROM book WHERE availabilityStatus = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Title: " + rs.getString("title") + " | Author: " + rs.getString("author"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
    }

    public static void donateBook(Connection conn, Scanner sc) {
        System.out.println("\n--- Donate a Book ---");
        System.out.print("Book Title: ");
        String title = sc.nextLine();
        System.out.print("Author: ");
        String author = sc.nextLine();
        System.out.print("Category ID: ");
        int categoryId = sc.nextInt();
        sc.nextLine();
        System.out.print("ISBN: ");
        String isbn = sc.nextLine();

        String insert = "INSERT INTO book (title, author, categoryid, isbn, availabilityStatus) VALUES (?, ?, ?, ?, TRUE)";
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, categoryId);
            stmt.setString(4, isbn);
            stmt.executeUpdate();
            System.out.println("Book donated successfully!");
        } catch (SQLException e) {
            System.err.println("Error donating book: " + e.getMessage());
        }
    }
}
