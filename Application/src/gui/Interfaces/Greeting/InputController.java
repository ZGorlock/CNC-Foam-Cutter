package gui.Interfaces.Greeting;

import gui.Interfaces.MainMenu.ModelController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import renderer.Renderer;
import utils.MachineDetector;

import java.io.IOException;

public class InputController {

    public TextField nid;
    public TextField length;
    public TextField width;
    public TextField height;
    public TextArea desc;

    private static String nidText;
    private static String descText;
    private static String heightText;
    private static String lengthText;
    private static String widthText;

    public void initialize()
    {
        nid.setPromptText("Please input your NID...");
        desc.setPromptText("Brief Description here...");
        width.setPromptText("Enter X...");
        length.setPromptText("Enter Y...");
        height.setPromptText("Enter Z...");
    }

    public void next(ActionEvent actionEvent) {

        setInput();

        if (invalidInput()) {
            return;
        }

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../Greeting/Greeting.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 1280, 960));
            stage.show();

            stage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });

            // Hide the current window
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setInput()
    {
        nidText = nid.getText();
        descText = desc.getText();

        widthText = width.getText();
        lengthText = length.getText();
        heightText = height.getText();
    }

    private boolean invalidInput()
    {
        String invalidMsg = "Invalid Number Format";
        boolean invalid = false;
        
        resetHighlights();

        if (nidText.isEmpty()) {
            nid.getStyleClass().add("error");
            invalid = true;
        }
        
        if (descText.isEmpty()) {
            desc.setText("No description available");
        }
        
        double maxWidth = MachineDetector.isCncMachine() ? ModelController.MAX_WIDTH_CNC : ModelController.MAX_WIDTH_HOTWIRE;
        double maxLength = MachineDetector.isCncMachine() ? ModelController.MAX_LENGTH_CNC : ModelController.MAX_LENGTH_HOTWIRE;
        double maxHeight = MachineDetector.isCncMachine() ? ModelController.MAX_HEIGHT_CNC : ModelController.MAX_WIDTH_HOTWIRE;
        
        if (widthText.isEmpty()) {
            width.getStyleClass().add("error");
            invalid = true;
            
        } else {
            try {
                Renderer.foamWidth = Double.parseDouble(widthText);
                
                if (Renderer.foamWidth <= 0 || Renderer.foamWidth > maxWidth) {
                    width.clear();
                    width.setPromptText("0 - " + maxWidth);
                    width.getStyleClass().add("error");
                    invalid = true;
                }
                
            } catch (NumberFormatException e) {
                width.getStyleClass().add("error");
                width.setPromptText(invalidMsg);
                invalid = true;
            }
        }
        
        if (lengthText.isEmpty()) {
            length.getStyleClass().add("error");
            invalid = true;
            
        } else {
            try {
                Renderer.foamLength = Double.parseDouble(lengthText);
    
                if (Renderer.foamLength <= 0 || Renderer.foamLength > maxLength) {
                    length.clear();
                    length.setPromptText("0 - " + maxLength);
                    length.getStyleClass().add("error");
                    invalid = true;
                }
                
            } catch (NumberFormatException e) {
                length.getStyleClass().add("error");
                length.setPromptText(invalidMsg);
                invalid = true;
            }
        }
        
        if (heightText.isEmpty()) {
            height.getStyleClass().add("error");
            invalid = true;
            
        } else {
            try {
                Renderer.foamHeight = Double.parseDouble(heightText);
    
                if (Renderer.foamHeight <= 0 || Renderer.foamHeight > maxHeight) {
                    height.clear();
                    height.setPromptText("0 - " + maxHeight);
                    height.getStyleClass().add("error");
                    invalid = true;
                }
                
            } catch (NumberFormatException e) {
                height.getStyleClass().add("error");
                height.setPromptText(invalidMsg);
                invalid = true;
            }
        }

        return invalid;
    }
    
    private void resetHighlights()
    {
        nid.getStyleClass().remove("error");
        width.getStyleClass().remove("error");
        length.getStyleClass().remove("error");
        height.getStyleClass().remove("error");
    }

    public static String getNidFromText()
    {
        return nidText;
    }

    public static String getDescFromText()
    {
        return descText;
    }

    public static String getLengthFromText() { return lengthText; }

    public static String getWidthFromText() { return widthText; }

    public static String getHeightFromText() { return heightText; }
}
