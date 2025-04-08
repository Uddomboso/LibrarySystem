DATABASE SETUP

CREATE DATABASE library;
USE library;

CREATE TABLE category (
    categoryid INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(900) NOT NULL
);

CREATE TABLE book (
    bookid INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    author VARCHAR(100),
    categoryid INT,
    isbn VARCHAR(20) UNIQUE,
    availabilityStatus BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categoryid) REFERENCES category(categoryid)
);

CREATE TABLE member (
    memberId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT
);

ALTER TABLE member
DROP COLUMN email,
DROP COLUMN phone,
DROP COLUMN address;

ALTER TABLE Member
ADD COLUMN username VARCHAR(100) UNIQUE NOT NULL,
ADD COLUMN password VARCHAR(255) NOT NULL;



CREATE TABLE borrow (
    borrowid INT AUTO_INCREMENT PRIMARY KEY,
    memberid INT,
    bookid INT,
    borrowdate DATE NOT NULL,
    duedate DATE NOT NULL,
    returndate DATE,
    FOREIGN KEY (memberid) REFERENCES Member(memberid),
    FOREIGN KEY (bookid) REFERENCES book(bookid)
);


INSERT INTO category (name) VALUES
('Fiction'), ('Dystopian'), ('Science'), ('Programming'),
('Fantasy'), ('Adventure'), ('Biography'), ('Self-Help');


INSERT INTO book (title, author, categoryid, isbn, availabilityStatus) VALUES
('To Kill a Mockingbird', 'Harper Lee', 1, '9780060935467', true),
('1984', 'George Orwell', 2, '9780451524935', true),
('A Brief History of Time', 'Stephen Hawking', 3, '9780553380163', true),
('The Great Gatsby', 'F. Scott Fitzgerald', 1, '9780743273565', true),
('Clean Code', 'Robert C. Martin', 4, '9780132350884', true),
('The Hobbit', 'J.R.R. Tolkien', 5, '9780547928227', true),
('The Alchemist', 'Paulo Coelho', 6, '9780061122415', true),
('Becoming', 'Michelle Obama', 7, '9781524763138', true),
('Atomic Habits', 'James Clear', 8, '9780735211292', true),
('The Subtle Art of Not Giving a F*ck', 'Mark Manson', 8, '9780062457714', true);


SHOW TABLES FROM library;
select * from category

