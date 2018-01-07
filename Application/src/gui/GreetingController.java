package gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GreetingController
{
    public Label fileUpload;
    public Label header;
    public Button upload;
    public Button print;
    public ImageView logo;
    public ArrayList<String> fileNames;

    public void uploadFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File(s)");
        File file = fileChooser.showOpenDialog(new Stage());

        if(file != null)
        {
            openFile(file);
        }

        fileUpload.setText("File Uploaded!");
    }

    private void openFile(File file)
    {
        Desktop desktop = Desktop.getDesktop();

        try {
            desktop.open(file);
        } catch (IOException ex) {
            fileUpload.setText("File Not Found");
        }
    }

    public ArrayList<String> getFileNames()
    {
        return fileNames;
    }

    public void print(ActionEvent actionEvent) {
    }
}