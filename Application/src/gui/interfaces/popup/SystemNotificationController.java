/*
 * File:    SystemNotificationController.java
 * Package: gui.interfaces.popup
 * Author:  Nicolas Lopez
 */

package gui.interfaces.popup;

import gui.interfaces.main.MenuController;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.Main;

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
     * @param error    The error.
     * @param fullstop Whether or not to perform a full stop.
     */
    public void raise(String error, boolean fullstop)
    {
        this.error = error;
        errorType.setText(error);
        this.fullstop = fullstop;
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

            btnOk.setText("Are you sure?");

            btnOk.setOnMousePressed(event -> {
                Main.resetApplication();

                MenuController.controller.backToStartUpScreen();
                ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            });


            
        } else {
            // For any other notification
            MenuController.paused = false;
            MenuController.controller.initiateResume(actionEvent);
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        }
    }
    
    
    //Static Methods
    
    
    
}
