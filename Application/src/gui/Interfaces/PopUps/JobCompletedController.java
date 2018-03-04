package gui.Interfaces.PopUps;

import gui.Interfaces.MainMenu.MenuController;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import main.Main;

public class JobCompletedController
{
    
    public Label timeCompleted;
    public static JobCompletedController controller;
    
    public void initialize()
    {
        controller = this;
    }
    
    public void setTimeCompleted()
    {
        long timeEnded = System.currentTimeMillis();
        long time = timeEnded - Main.startTime;
        long timeInSeconds = time / 1000;
        
        long hours = time / 3600;
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;
        String totalTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        
        timeCompleted.setText(totalTime);
    }
    
    public void ok(ActionEvent actionEvent)
    {
        // Call upon the MenuController to bring you back to the input screen
        MenuController.controller.backToStartUpScreen();
        
        // Hide the current window
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
    
}
