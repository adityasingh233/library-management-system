
package lmscode2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:bbaulibrary.db";
    private static Connection connection = null;

    public static Connection getConnection() throws Exception {
        if (connection == null|| connection.isClosed()) {  //singleton design pattern
        return DriverManager.getConnection(URL);
    }
    return connection;
}

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // Books Table
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "book_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, author TEXT, status TEXT DEFAULT 'AVAILABLE')"); // AVAILABLE or ISSUED

            // Students Table
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, roll_number TEXT)");

            // Transactions Table (Expanded for detailed tracking)
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "book_id INTEGER, student_id INTEGER, issuer_name TEXT, " +
                    "issue_datetime DATETIME, expected_return_date DATE, " +
                    "actual_return_date DATE, fine_amount REAL DEFAULT 0.0, " +
                    "FOREIGN KEY(book_id) REFERENCES books(book_id), " +
                    "FOREIGN KEY(student_id) REFERENCES students(student_id))");
                    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}