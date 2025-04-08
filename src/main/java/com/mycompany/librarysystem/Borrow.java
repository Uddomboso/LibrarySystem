/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.librarysystem;

/**
 *
 * @author asus
 */
;
import java.sql.Date;
import java.sql.*;
import java.util.*;

public class Borrow {
    private int borrowId;
    private int memberId;
    private int bookId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;

    public Borrow(int borrowId, int memberId, int bookId, Date borrowDate, Date dueDate, Date returnDate) {
        this.borrowId = borrowId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public static void showBorrowStatus(Connection conn, int memberId) {
        System.out.println("\n--- Your Borrowed Books ---");
        String query = "SELECT b.title, b.author, br.duedate, br.returndate FROM borrow br JOIN book b ON br.bookid = b.bookid WHERE br.memberid = ? AND br.returndate IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                Date due = rs.getDate("duedate");
                System.out.print(title + " by " + author + " - Due: " + due);
                if (due.before(new java.sql.Date(System.currentTimeMillis()))) {
                    System.out.println(" [RETURN OVERDUE!]");
                } else {
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error showing borrow status: " + e.getMessage());
        }
    }

    public static void borrowBook(Connection conn, Scanner sc, int memberId) {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine().toLowerCase(); // Convert input to lowercase

        String search = "SELECT * FROM book WHERE LOWER(title) = ? AND availabilityStatus = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(search)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int bookId = rs.getInt("bookid");
                String insert = "INSERT INTO borrow (memberid, bookid, borrowdate, duedate) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))";
                try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                    insertStmt.setInt(1, memberId);
                    insertStmt.setInt(2, bookId);
                    insertStmt.executeUpdate();
                }

                try (PreparedStatement update = conn.prepareStatement("UPDATE book SET availabilityStatus = FALSE WHERE bookid = ?")) {
                    update.setInt(1, bookId);
                    update.executeUpdate();
                }

                System.out.println("Book borrowed successfully.");
            } else {
                System.out.println("Book not available.");
            }
        } catch (SQLException e) {
            System.err.println("Error borrowing book: " + e.getMessage());
        }
    }

    public static void showAvailableBooks(Connection conn) {
        System.out.println("\n--- Available Books ---");
        String query = "SELECT bookid, title, author FROM book WHERE availabilityStatus = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            System.out.println("+------------+--------------------------------------+------------------+");
            System.out.println("| Book ID    | Title                                | Author           |");
            System.out.println("+------------+--------------------------------------+------------------+");
            while (rs.next()) {
                int bookId = rs.getInt("bookid");
                String title = rs.getString("title");
                String author = rs.getString("author");
                System.out.printf("| %-10d | %-35s | %-16s |\n", bookId, title, author);
            }
            System.out.println("+------------+--------------------------------------+------------------+");
        } catch (SQLException e) {
            System.err.println("Error fetching available books: " + e.getMessage());
        }
    }

    public static void donateBook(Connection conn, Scanner sc, int memberId) {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Book Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Book Category: ");
        String categoryName = sc.nextLine();

        String categoryQuery = "SELECT categoryid FROM category WHERE LOWER(categoryname) = ?";
        int categoryId;
        try (PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery)) {
            categoryStmt.setString(1, categoryName.toLowerCase());
            ResultSet categoryRs = categoryStmt.executeQuery();
            if (categoryRs.next()) {
                categoryId = categoryRs.getInt("categoryid");
            } else {
                System.out.println("Category not found!");
                return; 
            }
        } catch (SQLException e) {
            System.err.println("Error fetching category ID: " + e.getMessage());
            return;
        }

        String insertBookQuery = "INSERT INTO book (title, author, categoryid, availabilityStatus) VALUES (?, ?, ?, TRUE)";
        try (PreparedStatement stmt = conn.prepareStatement(insertBookQuery)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, categoryId);
            stmt.executeUpdate();
            System.out.println("Book donated successfully.");
        } catch (SQLException e) {
            System.err.println("Error donating book: " + e.getMessage());
        }
    }

    public static void returnBook(Connection conn, Scanner sc, int memberId) {
        String query = "SELECT br.borrowid, b.title FROM borrow br JOIN book b ON br.bookid = b.bookid WHERE br.memberid = ? AND br.returndate IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            List<Integer> borrowIds = new ArrayList<>();
            int i = 1;
            while (rs.next()) {
                borrowIds.add(rs.getInt("borrowid"));
                System.out.println(i + ". " + rs.getString("title"));
                i++;
            }

            if (borrowIds.isEmpty()) {
                System.out.println("No books to return.");
                return;
            }

            System.out.print("Choose book number to return: ");
            int choice = sc.nextInt();
            sc.nextLine();

            int borrowId = borrowIds.get(choice - 1);
            String updateBorrow = "UPDATE borrow SET returndate = CURDATE() WHERE borrowid = ?";
            String updateBook = "UPDATE book SET availabilityStatus = TRUE WHERE bookid = (SELECT bookid FROM borrow WHERE borrowid = ?)";

            try (PreparedStatement up1 = conn.prepareStatement(updateBorrow);
                 PreparedStatement up2 = conn.prepareStatement(updateBook)) {
                up1.setInt(1, borrowId);
                up2.setInt(1, borrowId);
                up1.executeUpdate();
                up2.executeUpdate();
                System.out.println("Book returned.");
            }

        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
        }

    }
public static void searchBooks(Connection conn, Scanner sc) {
    System.out.println("\n--- Search Options ---");
    System.out.println("1. Search by Category");
    System.out.println("2. Search by Keyword (Title)");
    System.out.print("Choose: ");
    int choice = sc.nextInt();
    sc.nextLine(); 

    switch (choice) {
        case 1 ->
            searchByCategory(conn, sc);
        case 2 ->
            searchByTitle(conn, sc);
        default -> System.out.println("Invalid option.");
    }
}

private static void searchByCategory(Connection conn, Scanner sc) {
    System.out.println("\n--- Available Categories ---");
    String categoryQuery = "SELECT categoryid, name FROM category ORDER BY name DESC"; 
    try (PreparedStatement stmt = conn.prepareStatement(categoryQuery)) {
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt("categoryid") + ". " + rs.getString("name"));
        }
        System.out.print("\nChoose Category: ");
        int categoryId = sc.nextInt();
        sc.nextLine(); 

        String booksInCategoryQuery = "SELECT bookid, title, author FROM book WHERE categoryid = ? AND availabilityStatus = TRUE";
        try (PreparedStatement bookStmt = conn.prepareStatement(booksInCategoryQuery)) {
            bookStmt.setInt(1, categoryId);
            ResultSet bookRs = bookStmt.executeQuery();
            if (!bookRs.next()) {
                System.out.println("No books found in this category.");
            } else {
                do {
                    System.out.println(bookRs.getInt("bookid") + ". " + bookRs.getString("title") + " by " + bookRs.getString("author"));
                } while (bookRs.next());
            }
        }
    } catch (SQLException e) {
        System.err.println("Search error: " + e.getMessage());
    }
}

private static void searchByTitle(Connection conn, Scanner sc) {
    System.out.print("Enter Book Title: ");
    String title = sc.nextLine().toLowerCase();

    String searchQuery = "SELECT bookid, title, author FROM book WHERE LOWER(title) LIKE ? AND availabilityStatus = TRUE";
    try (PreparedStatement stmt = conn.prepareStatement(searchQuery)) {
        stmt.setString(1, "%" + title + "%");
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            System.out.println("No books found with the title containing \"" + title + "\".");
        } else {
            do {
                System.out.println(rs.getInt("bookid") + ". " + rs.getString("title") + " by " + rs.getString("author"));
            } while (rs.next());
        }
    } catch (SQLException e) {
        System.err.println("Search error: " + e.getMessage());
    }
}



}




