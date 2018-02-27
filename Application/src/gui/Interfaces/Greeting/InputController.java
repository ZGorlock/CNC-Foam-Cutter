package gui.Interfaces.Greeting;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

        if(invalidInput()) return;

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../Greeting/Greeting.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 1280, 960));
            stage.show();

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
        lengthText = length.getText();
        heightText = height.getText();
        widthText = width.getText();
    }

    private boolean invalidInput()
    {
        if(lengthText.isEmpty() || heightText.isEmpty() || widthText.isEmpty())
        {
            return true;
        }
        return false;
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
