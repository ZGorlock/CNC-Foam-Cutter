/*
 * File:    Gui.java
 * Package: gui
 * Author:  Nicolas Lopez
 */

package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Main;

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
    
    
    //Methods
    
    /**
     * Starts the GUI.
     *
     * @param primaryStage The primary stage of the Application.
     */
    @Override
    public void start(Stage primaryStage)
    {
        if (!debug) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("interfaces/greeting/Input.fxml"));
                primaryStage.setTitle("3D CNC Foam Cutter");
                primaryStage.setScene(new Scene(root, 800, 800));
                primaryStage.show();
            } catch (Exception e) {
                System.err.println("There was an error loading Input.fxml!");
                e.printStackTrace();
                Main.killApplication();
            }
            
        } else { //Debug Mode
            try {
                Parent root = FXMLLoader.load(getClass().getResource("interfaces/main/Menu.fxml"));
                primaryStage.setTitle("3D CNC Foam Cutter");
                primaryStage.setScene(new Scene(root, 1280, 960));
                primaryStage.show();
            } catch (Exception e) {
                System.err.println("There was an error loading Menu.fxml!");
                e.printStackTrace();
                Main.killApplication();
            }
        }
    
        primaryStage.setOnCloseRequest(t -> Main.killApplication());
    }
    
}
