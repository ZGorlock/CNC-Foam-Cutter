package gui.Interfaces.MainMenu;

import gui.Gui;
import javafx.event.ActionEvent;
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
    public boolean paused = false;
    
    
    //Methods
    
    /**
     * Initializes the MenuController and loads the tabs.
     */
    public void initialize()
    {
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

            greyButton.setOnMouseClicked(e -> playPauseButtonClicked());
            
            greyButton.setOnMouseEntered(e -> greyButton.setStyle("-fx-text-fill: white; "+
                    "-fx-background-radius: 6; " +
                    "-fx-position: relative; -fx-background-color: #BEBFC3;"));
            greyButton.setOnMouseExited(e -> greyButton.setStyle("-fx-background-color: #BEBFC3; " +
                    "-fx-background-radius: 6; " +
                    "-fx-position: relative;"));

            greyButton.setText("Pause");
            hbox.getChildren().add(greyButton);
            goldButton.setText("STOP");
            
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
    private void playPauseButtonClicked()
    {
        greyButton.setStyle("-fx-background-color: #91918f; "+
                "-fx-background-radius: 6; " +
                "-fx-position: relative;");
        
        if (paused) {
            paused = false;
            greyButton.setText("Pause");
            initiatePause();
        } else {
            paused = true;
            greyButton.setText("Resume");
            initiateResume();
        }
    }
    
    /**
     * Handles a Pause event.
     */
    private void initiatePause()
    {
        //TODO handle the user clicking the pause button
    }
    
    /**
     * Handles a Resume event.
     */
    private void initiateResume()
    {
        //TODO handle the user clicking the resume button
    }
    
}
