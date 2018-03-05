/*
 * File:    InputController.java
 * Package: gui.interfaces.greeting
 * Author:  Nicolas Lopez
 */

package gui.interfaces.greeting;

import gui.interfaces.main.ModelController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Main;
import renderer.Renderer;
import utils.MachineDetector;

/**
 * The controller for the Input page.
 */
public class InputController
{
    
    //FXML Fields
    
    /**
     * The student nid input field.
     */
    public TextField nid;
    
    /**
     * The model description input field.
     */
    public TextArea desc;
    
    /**
     * The foam width input field.
     */
    public TextField width;
    
    /**
     * The foam length input field.
     */
    public TextField length;
    
    /**
     * The foam height input field.
     */
    public TextField height;
    
    
    //Static Fields
    
    /**
     * The text from the student nid input field.
     */
    private static String nidText;
    
    /**
     * The text from the model description input field.
     */
    private static String descText;
    
    /**
     * The text from the foam width input field.
     */
    private static String widthText;
    
    /**
     * The text from the foam length input field.
     */
    private static String lengthText;
    
    /**
     * The text from the foam height input field.
     */
    private static String heightText;
    
    
    //Methods
    
    /**
     * Initializes the controller.
     */
    public void initialize()
    {
        nid.setPromptText("Please input your NID...");
        desc.setPromptText("Brief Description here...");
        width.setPromptText("Enter X...");
        length.setPromptText("Enter Y...");
        height.setPromptText("Enter Z...");

        desc.setWrapText(true);
    }
    
    /**
     * Handles the Next button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void next(ActionEvent actionEvent)
    {
        setInput();
        if (invalidInput()) {
            return;
        }
        
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("Greeting.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 1280, 960));
            stage.show();
            stage.setOnCloseRequest(t -> Main.killApplication());
            
            // Hide the current window
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (Exception e) {
            System.err.println("There was an error loading Greeting.fxml!");
            e.printStackTrace();
            Main.killApplication();
        }
    }
    
    /**
     * Copies the user input from the fields into memory.
     */
    private void setInput()
    {
        nidText = nid.getText();
        descText = desc.getText();
        
        widthText = width.getText();
        lengthText = length.getText();
        heightText = height.getText();
    }
    
    /**
     * Determines if the user input is invalid.
     *
     * @return Whether the user input is invalid or not.
     */
    private boolean invalidInput()
    {
        String invalidMsg = "Invalid Number Format";
        boolean invalid = false;
        
        resetHighlights();
        
        if (nidText.isEmpty()) {
            nid.getStyleClass().add("error");
            invalid = true;
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
                    if (Renderer.foamWidth <= 0) {
                        width.setPromptText("> 0");
                    } else if (Renderer.foamWidth > maxWidth) {
                        width.setPromptText("<= " + maxWidth);
                    }
                    width.clear();
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
                    if (Renderer.foamLength <= 0) {
                        length.setPromptText("> 0");
                    } else if (Renderer.foamLength > maxLength) {
                        length.setPromptText("<= " + maxLength);
                    }
                    length.clear();
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
                    if (Renderer.foamHeight <= 0) {
                        height.setPromptText("> 0");
                    } else if (Renderer.foamHeight > maxHeight) {
                        height.setPromptText("<= " + maxHeight);
                    }
                    height.clear();
                    height.getStyleClass().add("error");
                    invalid = true;
                }
                
            } catch (NumberFormatException e) {
                height.getStyleClass().add("error");
                height.setPromptText(invalidMsg);
                invalid = true;
            }
        }
        
        if (!invalid && descText.isEmpty()) {
            desc.setText("No description available.");
            descText = desc.getText();
        }
        
        return invalid;
    }
    
    /**
     * Resets the error highlighting for the input fields.
     */
    private void resetHighlights()
    {
        nid.getStyleClass().remove("error");
        width.getStyleClass().remove("error");
        length.getStyleClass().remove("error");
        height.getStyleClass().remove("error");
    }
    
    
    //Getters
    
    /**
     * Returns the student nid input text.
     *
     * @return The student nid input text.
     */
    public static String getNidFromText()
    {
        return nidText;
    }
    
    /**
     * Returns the model description input text.
     *
     * @return The model description input text.
     */
    public static String getDescFromText()
    {
        return descText;
    }
    
    /**
     * Returns the foam width input text.
     *
     * @return The foam width input text.
     */
    public static String getWidthFromText()
    {
        return widthText;
    }
    
    /**
     * Returns the foam length input text.
     *
     * @return The foam length input text.
     */
    public static String getLengthFromText()
    {
        return lengthText;
    }
    
    /**
     * Returns the foam height input text.
     *
     * @return The foam height input text.
     */
    public static String getHeightFromText()
    {
        return heightText;
    }
    
}
