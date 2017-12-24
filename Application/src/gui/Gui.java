package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Gui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Greeting.fxml"));
        primaryStage.setTitle("Greeting screen");
        primaryStage.setScene(new Scene(root,1200,700));
        primaryStage.show();
    }


}
