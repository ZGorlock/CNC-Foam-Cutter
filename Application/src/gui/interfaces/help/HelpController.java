/*
 * File:    HelpController.java
 * Package: gui.interfaces.help
 * Author:  Zachary Gill
 */

package gui.interfaces.help;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import main.Main;

/**
 * The controller for the Help popup.
 */
public class HelpController
{
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static HelpController controller;
    
    
    //Methods
    
    /**
     * Initializes the controller.
     */
    public void initialize()
    {
        controller = this;
    }
    
    /**
     * Handles the OK button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void ok(ActionEvent actionEvent)
    {
        // Hide the current window
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
    
    
    //Static Methods
    
    
    /**
     * Opens the user help window.
     */
    public static void help()
    {
        Platform.runLater(() -> {
            try {
                Region root = new HelpBrowser();
                Stage stage = new Stage();
                stage.setTitle("3D CNC Foam Cutter - User Help");
                stage.setScene(new Scene(root, 1080, 720));
                stage.show();
        
            } catch (Exception e) {
                System.err.println("There was an error loading Help.fxml!");
                e.printStackTrace();
                Main.killApplication();
            }
        });
    }
    
}
