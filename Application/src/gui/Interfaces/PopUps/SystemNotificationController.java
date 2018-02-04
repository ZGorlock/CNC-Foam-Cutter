package gui.Interfaces.PopUps;

import gui.Interfaces.MainMenu.MenuController;
import gui.Interfaces.MainMenu.ModelController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SystemNotificationController{

    public Label errorType;
    private String error;
    public static SystemNotificationController controller;

    public void raise(String error)
    {
        this.error = error;
    }

    public void initialize()
    {
        controller = this;
        errorType.setText(error);
    }

    public void ok(ActionEvent actionEvent)
    {
        MenuController.paused = false;
        MenuController.controller.initiateResume(actionEvent);
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
}
