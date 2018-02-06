package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Gui;
import gui.Interfaces.PopUps.SystemNotificationController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.MachineDetector;

import java.io.IOException;
import java.net.URL;

/**
 * The controller for the Menu.
 */
public class MenuController {

    // FXML
    
    /**
     * The FXML elements of the menu.
     */
    public TabPane TPane;
    public Button goldButton;
    public HBox hbox;
    private Button greyButton;

    //Fields
    
    /**
     * A flag indicating whether the state is paused or not.
     */
    public static boolean paused = false;
    public static MenuController controller;
    private ActionEvent event;
    
    //Methods
    
    /**
     * Initializes the MenuController and loads the tabs.
     */
    public void initialize()
    {
        controller = this;

        TPane.getTabs().add(ModelController.setup());
        TPane.getTabs().add(GcodeController.setup());
        
        if (Gui.debug) {
            TPane.getTabs().add(TraceController.setup());
            TPane.getTabs().add(RotationController.setup());
        } else {
            TPane.getTabs().add(MachineDetector.isCncMachine() ? TraceController.setup() : RotationController.setup());
        }
    }
    
    /**
     * The EventHandler for the Print button.
     *
     * @param actionEvent The action event that triggered the handler.
     */
    public void print(ActionEvent actionEvent){

        if (greyButton == null) {
            greyButton = new Button();

            greyButton.setStyle(" -fx-background-color: #BEBFC3;" +
                    " -fx-background-radius: 6;" +
                    " -fx-position: relative;");

            greyButton.setOnMouseClicked(e -> playPauseButtonClicked(actionEvent));
            
            greyButton.setOnMouseEntered(e -> greyButton.setStyle("-fx-text-fill: white; "+
                    "-fx-background-radius: 6; " +
                    "-fx-position: relative; -fx-background-color: #BEBFC3;"));
            greyButton.setOnMouseExited(e -> greyButton.setStyle("-fx-background-color: #BEBFC3; " +
                    "-fx-background-radius: 6; " +
                    "-fx-position: relative;"));

            greyButton.setText("Pause");
            hbox.getChildren().add(greyButton);

            goldButton.setText("STOP");
            goldButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stop(event);
                }
            });
            
        } else {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("../PopUps/JobCompleted.fxml"));
                Stage stage = new Stage();
                stage.setTitle("3D CNC Foam Cutter");
                stage.setScene(new Scene(root, 800, 600));
                stage.show();

                // Hide the current window
                ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * The EventHandler for the Pause/Resume button.
     */
    private void playPauseButtonClicked(ActionEvent actionEvent)
    {
        paused = !paused;

        greyButton.setStyle("-fx-background-color: #91918f; "+
                "-fx-background-radius: 6; " +
                "-fx-position: relative;");
        
        if (paused) {
            initiatePause(actionEvent);
            greyButton.setText("Resume");
        } else {
            greyButton.setText("Pause");
            initiateResume(actionEvent);
        }
    }
    
    /**
     * Handles a Pause event.
     */
    public void initiatePause(ActionEvent actionEvent)
    {
        // Hide the current window
        //((Node) (actionEvent.getSource())).getScene().getWindow().setOpacity(.5);
    }
    
    /**
     * Handles a Resume event.
     */
    public void initiateResume(ActionEvent actionEvent)
    {
        // Pause Model Animation
        

        // Pause streaming
        APIgrbl.grbl.initiateResume();

        // Set opacity back to normal
        //if(event == null) event = actionEvent;
        //((Node) (this.event.getSource())).getScene().getWindow().setOpacity(1.0);
    }

    private void stop(ActionEvent actionEvent)
    {
        Parent root;
        try {
            URL fxml = SystemNotificationController.class.getResource("../PopUps/SystemNotification.fxml");
            root = FXMLLoader.load(fxml);
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

            paused = true;
            event = actionEvent;
            // Hide the current window
            //((Node) (actionEvent.getSource())).getScene().getWindow().setOpacity(.5);

            // Set notification
            SystemNotificationController.controller.raise("Full Stop");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
