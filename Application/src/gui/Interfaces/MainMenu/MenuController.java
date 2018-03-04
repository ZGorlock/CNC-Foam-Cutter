package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Gui;
import gui.Interfaces.PopUps.JobCompletedController;
import gui.Interfaces.PopUps.SystemNotificationController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import renderer.Renderer;
import tracer.Tracer;
import utils.MachineDetector;

import java.io.IOException;
import java.net.URL;

/**
 * The controller for the Menu.
 */
public class MenuController
{
    
    // FXML
    
    /**
     * The FXML elements of the menu.
     */
    public TabPane TPane;
    public Button goldButton;
    public HBox hbox;
    private Button greyButton;
    
    
    //Static Fields
    
    /**
     * A flag indicating whether the state is paused/stopped or not.
     */
    public static boolean paused = false;
    public static boolean stopped = false;
    
    /**
     *  Instance of the controller.
     */
    public static MenuController controller;
    
    
    //Methods
    
    /**
     * Initializes the MenuController and loads the tabs.
     */
    public void initialize()
    {
        controller = this;
        stopped = false;
        
        if (MachineDetector.isCncMachine()) {
            TPane.getTabs().add(ModelController.setup());
            TPane.getTabs().add(GcodeController.setup());
            TPane.getTabs().add(TraceController.setup());
            
            if (Gui.debug) {
                TPane.getTabs().add(RotationController.setup());
            }
            
        } else {
            TPane.getTabs().add(RotationController.setup());
            TPane.getTabs().add(ModelController.setup());
            TPane.getTabs().add(GcodeController.setup());
            
            if (Gui.debug) {
                TPane.getTabs().add(TraceController.setup());
            }
        }
    }
    
    /**
     * The EventHandler for the Print button.
     *
     * @param actionEvent The action event that triggered the handler.
     */
    public void print(ActionEvent actionEvent)
    {
        
        if (greyButton == null) {
            greyButton = new Button();
            
            greyButton.setStyle(" -fx-background-color: #BEBFC3;" +
                    " -fx-background-radius: 6;" +
                    " -fx-position: relative;");
            
            greyButton.setOnMouseClicked(e -> playPauseButtonClicked(actionEvent));
            
            greyButton.setOnMouseEntered(e -> greyButton.setStyle("-fx-text-fill: white; " +
                    "-fx-background-radius: 6; " +
                    "-fx-position: relative; -fx-background-color: #BEBFC3;"));
            greyButton.setOnMouseExited(e -> greyButton.setStyle("-fx-background-color: #BEBFC3; " +
                    "-fx-background-radius: 6; " +
                    "-fx-position: relative;"));
            
            greyButton.setText("Pause");
            hbox.getChildren().add(greyButton);
            
            goldButton.setText("STOP");
            goldButton.setOnAction(this::stop);
            
            GcodeController.startGrbl();
        }
    }
    
    /**
     * The EventHandler for the Pause/Resume button.
     */
    private void playPauseButtonClicked(ActionEvent actionEvent)
    {
        paused = !paused;
        
        greyButton.setStyle("-fx-background-color: #91918f; " +
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
     *
     * @param actionEvent The event that triggered the call.
     */
    public void initiatePause(ActionEvent actionEvent)
    {
        paused = true;
        
        //Pause Model Animation
        Renderer.pauseModelAnimation();
    }
    
    /**
     * Handles a Resume event.
     *
     * @param actionEvent The event that triggered the call.
     */
    public void initiateResume(ActionEvent actionEvent)
    {
        // Resume streaming
        APIgrbl.grbl.initiateResume();
        
        //Resume Model Animation
        Renderer.resumeModelAnimation();
    }
    
    /**
     * Handles a Stop event.
     *
     * @param actionEvent The event that triggered the call.
     */
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
            stage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });
            
            stopped = true;
            
            // Set notification
            SystemNotificationController.controller.raise("Full Stop", true);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles resetting the application
     * This method is called when the job is finished
     */
    public void reset()
    {
        stopped = true;
        
        Renderer.reset();
        Tracer.reset();
        
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../PopUps/JobCompleted.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 800, 600));
            
            JobCompletedController.controller.setTimeCompleted();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * This method resets the application back to the starting screen.
     * Is called by the jobCompletedController so both screens can be closed.
     */
    public void backToStartUpScreen()
    {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../Greeting/Input.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 800, 800));
            stage.show();
            stage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });
            
            // Hide the current window
            (goldButton).getScene().getWindow().hide();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
