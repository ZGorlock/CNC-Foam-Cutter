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
import gui.Interfaces.MainMenu.ModelController;

import javax.jws.WebParam;

public class InputController {

    public TextField nid;
    public TextArea desc;

    private static String nidText;
    private static String descText;

    public void initialize()
    {
        nid.setOnMouseClicked(e -> nid.setText(""));
        desc.setOnMouseClicked(e -> desc.setText(""));
        nid.setPromptText("Please input your NID...");
        desc.setPromptText("Brief Description here...");
    }

    public void next(ActionEvent actionEvent) {

        setInput();

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
    }

    public static String getNidFromText()
    {
        return nidText;
    }

    public static String getDescFromText()
    {
        return descText;
    }
}
