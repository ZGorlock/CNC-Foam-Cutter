package gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import javax.swing.text.html.ImageView;

public class GreetingController
{
    public Label fileUpload;
    public Label header;
    public Button upload;
    public Button print;
    public ImageView logo;

    public void uploadFile(ActionEvent actionEvent) {
        fileUpload.setText("File Uploaded!");
    }

    public void print(ActionEvent actionEvent) {
    }
}