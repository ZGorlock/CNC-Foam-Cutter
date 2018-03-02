package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main GUI launcher for our Application.
 */
public class Gui extends Application
{
    
    //Static Fields
    
    /**
     * Whether or not the Application is in debug mode.
     */
    public static boolean debug = false;

    
    //Static Methods
    
    public static void main(String[] args) {
        launch(args);
    }

    
    //Methods
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        if (!debug) {
            Parent root = FXMLLoader.load(getClass().getResource("Interfaces/Greeting/Input.fxml"));
            primaryStage.setTitle("3D CNC Foam Cutter");
            primaryStage.setScene(new Scene(root, 800, 800));
            primaryStage.show();

        } else { //Debug Mode
            Parent root = FXMLLoader.load(getClass().getResource("Interfaces/MainMenu/Menu.fxml"));
            primaryStage.setTitle("3D CNC Foam Cutter");
            primaryStage.setScene(new Scene(root, 1280, 960));
            primaryStage.show();
        }

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }


}
