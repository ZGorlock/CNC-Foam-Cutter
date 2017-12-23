package gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class GreetingController
{
    public Label fileUpload;
    public void uploadFile(ActionEvent actionEvent) {
        fileUpload.setText("File Uploaded!");
    }
}