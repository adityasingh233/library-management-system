package lmscode2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lmscode2.util.DatabaseHelper;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseHelper.initializeDatabase(); 
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 1050, 500);
        
        primaryStage.setTitle("Central Library Management Interface");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}