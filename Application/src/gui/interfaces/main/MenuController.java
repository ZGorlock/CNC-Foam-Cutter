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

import java.net.URL;

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
    public Button goldButton;
    
    /**
     * The container for the Pause/Resume button.
     */
    public HBox hBox;
    
    /**
     * The Pause/Resume button.
     */
    private Button greyButton;
    
    
    //Static Fields
    
    /**
     * A flag indicating whether the state is paused or not.
     */
    public static boolean paused = false;
    
    /**
     * A flag indicating whether the state is stopped or not.
     */
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
     * Handles the Print button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void print(ActionEvent actionEvent)
    {
        if (greyButton == null) {
            if (GcodeController.startGrbl()) {
    
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
                hBox.getChildren().add(greyButton);
    
                goldButton.setText("STOP");
                goldButton.setOnAction(this::stop);
            } else {
                //TODO throw error
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
        Parent root;
        try {
            URL fxml = SystemNotificationController.class.getResource("../popup/SystemNotification.fxml");
            root = FXMLLoader.load(fxml);
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
            stage.setOnCloseRequest(t -> Main.killApplication());
            
            stopped = true;
            
            // Set notification
            SystemNotificationController.controller.raise("Full Stop", true);
            
        } catch (Exception e) {
            System.err.println("There was an error loading SystemNotification.fxml!");
            e.printStackTrace();
            Main.killApplication();
        }
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
            (goldButton).getScene().getWindow().hide();
            
        } catch (Exception e) {
            System.err.println("There was an error loading Input.fxml!");
            e.printStackTrace();
            Main.killApplication();
        }
    }
    
}
