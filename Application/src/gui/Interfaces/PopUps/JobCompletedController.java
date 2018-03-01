package gui.Interfaces.PopUps;

import gui.Interfaces.MainMenu.MenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static main.Main.startTime;

public class JobCompletedController {

    public Label timeCompleted;
    public static JobCompletedController controller;

    public void initialize()
    {
        controller = this;
    }

    public void setTimeCompleted()
    {
        long timeEnded = System.currentTimeMillis();
        long time = timeEnded - startTime;

        String totalTime = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));

        timeCompleted.setText(totalTime);
    }

    public void Ok(ActionEvent actionEvent)
    {
        // Call upon the MenuController to bring you back to the input screen
        MenuController.controller.backToStartUpScreen();

        // Hide the current window
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
}
