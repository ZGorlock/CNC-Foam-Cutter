/*
 * File:    MenuController.java
 * Package: gui.interfaces.main
 * Author:  Nicolas Lopez
 */

package gui.interfaces.main;

import grbl.APIgrbl;
import gui.Gui;
import gui.interfaces.popup.JobCompletedController;
import gui.interfaces.popup.SystemNotificationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.Main;
import renderer.Renderer;
import utils.MachineDetector;

/**
 * The controller for the Menu.
 */
public class MenuController
{
    
    //FXML Fields
    
    /**
     * The container holding the tabs of the main window.
     */
    public TabPane TPane;
    
    /**
     * The emergency Stop button.
     */
    public Button stopButton;
    
    /**
     * The container for the Pause/Resume button.
     */
    public HBox hBox;
    
    /**
     * The Pause/Resume button.
     */
    private Button playPauseButton;
    
    
    //Static Fields
    
    /**
     *  Instance of the controller.
     */
    public static MenuController controller;
    
    /**
     * A flag indicating whether the state is paused or not.
     */
    public static boolean paused = false;
    
    /**
     * A flag indicating whether the state is stopped or not.
     */
    public static boolean stopped = false;
    
    /**
     * Stores the amount of time that the machine was paused.
     */
    private static long pauseTime;
    
    
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
     * Handles the Print button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void print(ActionEvent actionEvent)
    {
        if (playPauseButton == null) {
            if (MachineDetector.isCncMachine()) {
                if (GcodeController.startGrbl()) {
        
                    playPauseButton = new Button();
                    playPauseButton.getStyleClass().add("buttonGold");
                    playPauseButton.setOnMouseClicked(e -> playPauseButtonClicked(actionEvent));
        
                    playPauseButton.setText("Pause");
                    hBox.getChildren().add(playPauseButton);
        
                    stopButton.setText("STOP");
                    stopButton.setOnAction(this::stop);
                } else {
                    SystemNotificationController.throwNotification("The process of communicating with the machine could not be started!", true, false);
                }
                
            } else if (MachineDetector.isHotWireMachine()) {
                double total = 0.0;
                for (double degree : RotationController.controller.rotationQueue) {
                    total += degree;
                }
                if (total == 360.0) {
                    //TODO handle first profile and set up queue
                } else {
                    SystemNotificationController.throwNotification("The sum of your profiles must add up to 360 degrees!!", false, false);
                }
            }
        }
    }
    
    /**
     * Handles the Pause/Resume button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    private void playPauseButtonClicked(ActionEvent actionEvent)
    {
        paused = !paused;
        
        if (paused) {
            initiatePause(actionEvent);
            playPauseButton.setText("Resume");
        } else {
            playPauseButton.setText("Pause");
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
        pauseTime = System.currentTimeMillis();
        
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
        if (pauseTime > 0) {
            long pauseDuration = System.currentTimeMillis() - pauseTime;
            Main.startTime += pauseDuration;
            pauseTime = 0;
        }
        
        //Resume streaming
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
        stopped = true;
        SystemNotificationController.throwNotification("Performing a Full Stop!", true, true);
    }
    
    /**
     * Resets the Application.<br/>
     * This method is called when the job is finished.
     */
    public void reset()
    {
        stopped = true;
        
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../popup/JobCompleted.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 800, 600));
            
            JobCompletedController.controller.setTimeCompleted();
            stage.show();
            
        } catch (Exception e) {
            System.err.println("There was an error loading JobCompleted.fxml!");
            e.printStackTrace();
            Main.killApplication();
        }
        
    }
    
    /**
     * This method resets the Application back to the starting screen.<br/>
     * Is called by the jobCompletedController so both screens can be closed.
     */
    public void backToStartUpScreen()
    {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../greeting/Input.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 800, 800));
            stage.show();
            stage.setOnCloseRequest(t -> Main.killApplication());
            
            // Hide the current window
            (stopButton).getScene().getWindow().hide();
            
        } catch (Exception e) {
            System.err.println("There was an error loading Input.fxml!");
            e.printStackTrace();
            Main.killApplication();
        }
    }
    
}
