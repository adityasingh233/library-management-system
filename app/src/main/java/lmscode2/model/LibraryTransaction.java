package lmscode2.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LibraryTransaction {
    private int transactionId;
    private int bookId;
    private String bookTitle;
    private String studentName;
    private String issuerName;
    private LocalDateTime issueDateTime;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    
    private static final double DAILY_FINE_RATE = 5.0; // ₹5 per day

    public LibraryTransaction(int transactionId, String bookTitle, String studentName, 
                              String issuerName, LocalDateTime issueDateTime, LocalDate expectedReturnDate) {
        this.transactionId = transactionId;
        this.bookTitle = bookTitle;
        this.studentName = studentName;
        this.issuerName = issuerName;
        this.issueDateTime = issueDateTime;
        this.expectedReturnDate = expectedReturnDate;
    }

    public double calculateFine(LocalDate returnDate) {
        if (returnDate.isAfter(expectedReturnDate)) {
            long overdueDays = ChronoUnit.DAYS.between(expectedReturnDate, returnDate);
            return overdueDays * DAILY_FINE_RATE;
        }
        return 0.0;
    }

    // Getters
    public int getTransactionId() { return transactionId; }
    public String getBookTitle() { return bookTitle; }
    public String getStudentName() { return studentName; }
    public String getIssuerName() { return issuerName; }
    public LocalDateTime getIssueDateTime() { return issueDateTime; }
    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
}