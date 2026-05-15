package lmscode2.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String status;

    public Book(int bookId, String title, String author, String status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    // Getters for TableView PropertyValueFactory
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getStatus() { return status; }
}