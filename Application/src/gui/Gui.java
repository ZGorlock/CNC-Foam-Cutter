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
        Parent root = FXMLLoader.load(getClass().getResource("Interfaces/Greeting/Greeting.fxml"));
        primaryStage.setTitle("3D CNC Foam Cutter");
        primaryStage.setScene(new Scene(root,1200,700));
        primaryStage.show();
    }


}