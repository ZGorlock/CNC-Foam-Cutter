package gui.Interfaces.Greeting;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GreetingController
{
    public Button upload;
    public Button print;
    private static ArrayList<String> fileNames;

    
    public void uploadFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File(s)");
        File file = fileChooser.showOpenDialog(new Stage());

        if(file != null)
        {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    fileNames.add(f.getAbsolutePath());
                }
            } else {
                // this constructs all the file names
                fileNames.add(file.getAbsolutePath());
            }
        }
    }


    public void print(ActionEvent actionEvent) {
        // is it connected?
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../MainMenu/Menu.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 1200, 700));
            stage.show();

            // Hide the current window
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        

    }

    public static ArrayList<String> getFileNames() {
        return fileNames;
    }
    
}