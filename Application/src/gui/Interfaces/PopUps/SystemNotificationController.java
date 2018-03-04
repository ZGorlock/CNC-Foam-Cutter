package gui.Interfaces.PopUps;

import gui.Interfaces.MainMenu.GcodeController;
import gui.Interfaces.MainMenu.MenuController;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * The controller for the System Notification popup.
 */
public class SystemNotificationController
{
    
    // FXML
    public Label errorType;
    
    // Fields
    private String error;
    
    // Flags
    private boolean fullstop;
    
    // Instance
    public static SystemNotificationController controller;
    
    public void raise(String error, boolean fullstop)
    {
        this.error = error;
        errorType.setText(error);
        this.fullstop = fullstop;
    }
    
    public void initialize()
    {
        controller = this;
        errorType.setText(error);
    }
    
    public void ok(ActionEvent actionEvent)
    {
        // For full stops
        if (fullstop) {
            MenuController.controller.backToStartUpScreen();
            GcodeController.controller.resetUI();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            
        } else {
            // For any other notification
            MenuController.paused = false;
            MenuController.controller.initiateResume(actionEvent);
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        }
    }
    
}
