/*
 * File:    JobCompletedController.java
 * Package: gui.interfaces.popup
 * Author:  Nicolas Lopez
 */

package gui.interfaces.popup;

import gui.interfaces.main.MenuController;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import main.Main;

/**
 * The controller for the Job Completed popup.
 */
public class JobCompletedController
{
    
    //FXML Fields
    
    /**
     * The completion time output label.
     */
    public Label timeCompleted;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static JobCompletedController controller;
    
    
    //Methods
    
    /**
     * Initializes the controller.
     */
    public void initialize()
    {
        controller = this;
    }
    
    /**
     * Sets the completion time.
     */
    public void setTimeCompleted()
    {
        double timeElapsed = (double)(System.currentTimeMillis() - Main.startTime) / 1000;
    
        long hours = (long) (timeElapsed / 3600);
        long minutes = (long) ((timeElapsed % 3600) / 60);
        long seconds = (long) (timeElapsed % 60);
        
        String totalTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeCompleted.setText(totalTime);
    }
    
    /**
     * Handles the OK button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void ok(ActionEvent actionEvent)
    {
        // Call upon the MenuController to bring you back to the input screen
        MenuController.controller.backToStartUpScreen();
        
        // Hide the current window
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
    
}
