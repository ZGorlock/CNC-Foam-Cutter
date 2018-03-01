package gui.Interfaces.PopUps;

import gui.Interfaces.MainMenu.MenuController;
import gui.Interfaces.MainMenu.ModelController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

public class SystemNotificationController{

    public Label errorType;
    private String error;
    public static SystemNotificationController controller;
    private boolean fullstop;

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
        if(fullstop)
        {
            MenuController.controller.reset();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();

        }else
        {
            // For any other notification
            MenuController.paused = false;
            MenuController.controller.initiateResume(actionEvent);
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        }
    }
}
