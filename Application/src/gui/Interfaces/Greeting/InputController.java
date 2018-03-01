package gui.Interfaces.Greeting;

import gui.Interfaces.MainMenu.ModelController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import renderer.Renderer;
import utils.MachineDetector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
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
        String redHighlight = "-fx-background-color:rgba(255,0,0,0.2);";
        String invalidMsg = "Invalid Number Format";
        boolean invalid = false;

        if(nidText.isEmpty())
        {
            nid.setStyle(redHighlight);
            invalid = true;
        }

        if(widthText.isEmpty())
        {
            width.setStyle(redHighlight);
            invalid = true;
        }

        if(lengthText.isEmpty())
        {
            length.setStyle(redHighlight);
            invalid = true;
        }

        if(heightText.isEmpty())
        {
            height.setStyle(redHighlight);
            invalid = true;
        }

        // First reset for emptyness
        if(invalid) return invalid;

        // Check number formats
        
        try {
            Renderer.foamWidth = Double.parseDouble(widthText);
        } catch (NumberFormatException e) {
            width.setStyle(redHighlight);
            width.setPromptText(invalidMsg);
            invalid = true;
        }

        try {
            Renderer.foamLength = Double.parseDouble(lengthText);
        } catch (NumberFormatException e) {
            length.setStyle(redHighlight);
            length.setPromptText(invalidMsg);
            invalid = true;
        }

        try {
            Renderer.foamHeight = Double.parseDouble(heightText);
        } catch (NumberFormatException e) {
            height.setStyle(redHighlight);
            height.setPromptText(invalidMsg);
            invalid = true;
        }

        // reset for bad number formats
        if(invalid) return invalid;
        
        if (MachineDetector.isCncMachine())
        {
            if (Renderer.foamWidth <= 0 || Renderer.foamWidth > ModelController.MAX_WIDTH_CNC){
                width.clear();
                width.setPromptText("0 - " + ModelController.MAX_WIDTH_CNC);
                width.setStyle(redHighlight);
                invalid = true;
            }

            if(Renderer.foamLength <= 0 || Renderer.foamLength > ModelController.MAX_LENGTH_CNC){
                length.clear();
                length.setPromptText("0 - " + ModelController.MAX_LENGTH_CNC);
                length.setStyle(redHighlight);
                invalid = true;
            }

            if(Renderer.foamHeight <= 0 || Renderer.foamHeight > ModelController.MAX_HEIGHT_CNC) {
                height.clear();
                height.setPromptText("0 - " + ModelController.MAX_HEIGHT_CNC);
                height.setStyle(redHighlight);
                invalid = true;
            }
        } else if (MachineDetector.isHotWireMachine())
        {
            if (Renderer.foamWidth <= 0 || Renderer.foamWidth > ModelController.MAX_WIDTH_HOTWIRE){
                width.clear();
                width.setPromptText("0 - " + ModelController.MAX_WIDTH_HOTWIRE);
                width.setStyle(redHighlight);
                invalid = true;
            }

            if(Renderer.foamLength <= 0 || Renderer.foamLength > ModelController.MAX_LENGTH_HOTWIRE){
                length.clear();
                length.setPromptText("0 - " + ModelController.MAX_LENGTH_HOTWIRE);
                length.setStyle(redHighlight);
                invalid = true;
            }

            if(Renderer.foamHeight <= 0 || Renderer.foamHeight > ModelController.MAX_HEIGHT_HOTWIRE) {
                height.clear();
                height.setPromptText("0 - " + ModelController.MAX_HEIGHT_HOTWIRE);
                height.setStyle(redHighlight);
                invalid = true;
            }
        } else {
            return true;
        }

        if(descText.isEmpty())
            desc.setText("No description available");

        return invalid;
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
