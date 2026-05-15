package lmscode2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lmscode2.model.Book;
import lmscode2.model.LibraryTransaction;
import lmscode2.util.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LibrarianDashboardController {

    // Tables
    @FXML private TableView<Book> availableBooksTable;
    @FXML private TableColumn<Book, Integer> colBookId;
    @FXML private TableColumn<Book, String> colTitle, colAuthor;

    @FXML private TableView<LibraryTransaction> issuedBooksTable;
    @FXML private TableColumn<LibraryTransaction, Integer> colTransId;
    @FXML private TableColumn<LibraryTransaction, String> colIssuedTitle, colStudent, colIssuer;
    @FXML private TableColumn<LibraryTransaction, LocalDateTime> colIssueTime;
    @FXML private TableColumn<LibraryTransaction, LocalDate> colReturnDate;

    // Inputs
    @FXML private TextField bookIdInput, studentIdInput, librarianNameInput;
    @FXML private Label statusMessage;

    @FXML
    public void initialize() {
        // Initialize Available Books Columns
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        // Initialize Issued Books Columns
        colTransId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colIssuedTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colIssuer.setCellValueFactory(new PropertyValueFactory<>("issuerName"));
        colIssueTime.setCellValueFactory(new PropertyValueFactory<>("issueDateTime"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));

        loadTables();
    }

    @FXML
    public void handleIssueBook() {
        try (Connection conn = DatabaseHelper.getConnection()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDate expectedReturn = LocalDate.now().plusDays(14); // 14 day issue period

            String insertSQL = "INSERT INTO transactions (book_id, student_id, issuer_name, issue_datetime, expected_return_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);
            pstmt.setInt(1, Integer.parseInt(bookIdInput.getText()));
            pstmt.setInt(2, Integer.parseInt(studentIdInput.getText()));
            pstmt.setString(3, librarianNameInput.getText());
            pstmt.setString(4, now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(5, expectedReturn.toString());
            pstmt.executeUpdate();

            // Update Book Status
            String updateBook = "UPDATE books SET status = 'ISSUED' WHERE book_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateBook);
            updateStmt.setInt(1, Integer.parseInt(bookIdInput.getText()));
            updateStmt.executeUpdate();

            statusMessage.setText("Book Issued Successfully!");
            statusMessage.setStyle("-fx-text-fill: green;");
            loadTables();

        } catch (Exception e) {
            statusMessage.setText("Error issuing book: " + e.getMessage());
            statusMessage.setStyle("-fx-text-fill: red;");
        }
    }

    private void loadTables() {
        ObservableList<Book> availableList = FXCollections.observableArrayList();
        ObservableList<LibraryTransaction> issuedList = FXCollections.observableArrayList();

        try (Connection conn = DatabaseHelper.getConnection()) {
            // Load Available Books
            ResultSet rsBooks = conn.createStatement().executeQuery("SELECT * FROM books WHERE status = 'AVAILABLE'");
            while (rsBooks.next()) {
                availableList.add(new Book(rsBooks.getInt("book_id"), rsBooks.getString("title"), rsBooks.getString("author"), rsBooks.getString("status")));
            }
            availableBooksTable.setItems(availableList);

            // Load Issued Books (Join query to get names instead of just IDs)
            String query = "SELECT t.transaction_id, b.title, s.name as student_name, t.issuer_name, t.issue_datetime, t.expected_return_date " +
                           "FROM transactions t " +
                           "JOIN books b ON t.book_id = b.book_id " +
                           "JOIN students s ON t.student_id = s.student_id " +
                           "WHERE t.actual_return_date IS NULL";
                           
            ResultSet rsTrans = conn.createStatement().executeQuery(query);
            while (rsTrans.next()) {
                issuedList.add(new LibraryTransaction(
                        rsTrans.getInt("transaction_id"), rsTrans.getString("title"), rsTrans.getString("student_name"),
                        rsTrans.getString("issuer_name"), LocalDateTime.parse(rsTrans.getString("issue_datetime")),
                        LocalDate.parse(rsTrans.getString("expected_return_date"))
                ));
            }
            issuedBooksTable.setItems(issuedList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}