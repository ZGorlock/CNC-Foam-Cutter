package gui.Interfaces.MainMenu;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tracer.Tracer;
import utils.MachineDetector;

import java.io.File;
import java.io.IOException;

public class MenuController {

    // FXML Components
    public TabPane TPane;
    public Button goldButton;
    public HBox hbox;
    private Button greyButton;

    // Model to Execute
    private File model = null;
    
    
    public void initialize()
    {
        String fxmlToLoad = MachineDetector.isCncMachine() ? "Trace.fxml" : "Rotation.fxml";

        // We add a tab dynamically depending on which fxml file we will load
        try {
            Tab tabThird = (FXMLLoader.load(this.getClass().getResource(fxmlToLoad)));
            TPane.getTabs().add(tabThird);

            BorderPane borderPane = (BorderPane) tabThird.getContent();
            Pane pane = (Pane)borderPane.getChildren().get(0);
            
            SwingNode swingNode = (SwingNode) pane.getChildren().get(0);
            Tracer.setup(swingNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    
    public boolean paused = false;
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
    
    private void initiatePause()
    {
        //TODO handle the user clicking the pause button
    }
    
    private void initiateResume()
    {
        //TODO handle the user clicking the resume button
    }

    public File getModel()
    {
        return model;
    }
    
}
