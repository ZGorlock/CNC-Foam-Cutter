package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        String normal = "Interfaces/Greeting/Input.fxml";
        String debug = "Interfaces/MainMenu/Menu.fxml";

        Parent root = FXMLLoader.load(getClass().getResource(normal));
        primaryStage.setTitle("3D CNC Foam Cutter");
        primaryStage.setScene(new Scene(root,800, 600));
        primaryStage.show();
    }


}
