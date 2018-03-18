/*
 * File:    SystemNotificationController.java
 * Package: gui.interfaces.popup
 * Author:  Nicolas Lopez
 */

package gui.interfaces.popup;

import grbl.APIgrbl;
import gui.interfaces.main.MenuController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.Main;

import java.io.File;
import java.net.URL;

/**
 * The controller for the System Notification popup.
 */
public class SystemNotificationController
{
    
    //FXML Fields
    
    /**
     * The error type output field.
     */
    public Label errorType;

    /**
     * The button for this screen.
     */
    public Button btnOk;
    
    
    //Fields
    
    /**
     * The error.
     */
    private String error;
    
    /**
     * A flag indicating whether or not to perform a full stop.
     */
    private boolean fullstop;
    
    /**
     * A flag indicating whether or not to perform an "Are you sure?" check.
     */
    private boolean areYouSure;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static SystemNotificationController controller;
    
    
    //Methods
    
    /**
     * Initializes the controller
     */
    public void initialize()
    {
        controller = this;
        errorType.setText(error);
    }
    
    /**
     * Raises an error.
     *
     * @param error      The error.
     * @param fullstop   Whether or not to perform a full stop.
     * @param areYouSure Whether or not to perform the "Are you sure?" check.
     */
    public void raise(String error, boolean fullstop, boolean areYouSure)
    {
        this.error = error;
        errorType.setText(error);
        this.fullstop = fullstop;
        this.areYouSure = areYouSure;
    }
    
    /**
     * Handles the OK button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void ok(ActionEvent actionEvent)
    {
        // For full stops
        if (fullstop) {

            if (areYouSure) {
                btnOk.setText("Are you sure?");
    
                btnOk.setOnMousePressed(event -> {
                    MenuController.stopped = true;
                    Main.resetApplication();
        
                    MenuController.controller.backToStartUpScreen();
                    ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
                });
                
            } else {
                Main.resetApplication();
    
                MenuController.controller.backToStartUpScreen();
                ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            }
            
        } else {
            // For any other notification
            if (MenuController.paused && APIgrbl.grbl != null) {
                MenuController.paused = false;
                MenuController.controller.initiateResume(actionEvent);
            }
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        }
    }
    
    
    //Static Methods
    
    /**
     *  This method is used throughout the application to show any kind of message to the user,<br/>
     *  that be of an error, an exception, or just a regular notification, it is also used during full stops.<br/>
     *  Calling SystemNotificationController.throwNotification will show the popup with whatever custom message is<br/>
     *  inputted on the string field.
     *
     *  @param error      The error message to display.
     *  @param fullstop   Whether or not to perform a full stop.
     *  @param areYouSure Whether or not to perform an "are you sure" check before performing a full stop.
     */
    public static void throwNotification(String error, boolean fullstop, boolean areYouSure)
    {
        Platform.runLater(() -> {
            Parent root;
            try {
                File systemNotification = new File("src\\gui\\interfaces\\popup\\SystemNotification.fxml");
                URL fxml = systemNotification.toURI().toURL();
                root = FXMLLoader.load(fxml);
                Stage stage = new Stage();
                stage.setTitle("3D CNC Foam Cutter");
                stage.setScene(new Scene(root, 600, 300));
                stage.show();
    
                // Set notification
                SystemNotificationController.controller.raise(error, fullstop, areYouSure);

                if (areYouSure) {
                    stage.setOnCloseRequest(e-> {
                        MenuController.paused = false;
                        MenuController.controller.initiateResume(null);
                    });
                }
    
            } catch (Exception e) {
                System.err.println("There was an error loading SystemNotification.fxml!");
                e.printStackTrace();
                Main.killApplication();
            }
        });
    }
    
}
