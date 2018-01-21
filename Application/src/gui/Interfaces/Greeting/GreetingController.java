package gui.Interfaces.Greeting;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GreetingController
{
    private static ArrayList<String> fileNames;
    public TextField textFieldPath;
    public Button greyButton;
    public Button chooseButton;
    public Label dropFileText;
    public BorderPane contentPane;
    public ImageView knights;
    private String prompt;
    private boolean chosen;

    public void initialize()
    {

        chosen = false;
        prompt = textFieldPath.getText();
        fileNames = new ArrayList<>();

        knights.setOpacity(.5);
        greyButton.setStyle(" -fx-background-color: #BEBFC3;" +
                " -fx-background-radius: 6;" +
                " -fx-position: relative;" +
                "-fx-opacity: .25;");
    }
    
    public void chooseFile(ActionEvent actionEvent)
    {
        File file;
        if(textFieldPath.getText().compareTo(prompt) != 0)
        {
            file = new File(textFieldPath.getText());
        }else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File(s)");
            file = fileChooser.showOpenDialog(new Stage());
        }
        handleFile(file);
    }

    private void handleFile(File file)
    {
        if(file != null)
        {
            textFieldPath.setText(file.getAbsolutePath());

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        fileNames.add(f.getAbsolutePath());
                    }
                }
            } else {
                // this constructs all the file names
                fileNames.add(file.getAbsolutePath());
            }
            chosen = true;
        }
        handleUploadedAnimation();
    }

    private void handleUploadedAnimation(){
        // Animations and Stylings
        knights.setOpacity(1.0);
        knights.setScaleX(1.18);
        knights.setScaleY(1.18);
        greyButton.setStyle(" -fx-background-color: #BEBFC3;" +
                " -fx-background-radius: 6;" +
                " -fx-position: relative;");
        dropFileText.setText("");
    }


    public void upload(ActionEvent actionEvent)
    {
        if(!chosen) // must upload a file to continue
        {
            chooseButton.setStyle(" -fx-background-color: #BEBFC3;" +
                    " -fx-background-radius: 6;" +
                    " -fx-position: relative;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, .8, 0, 0);");
            return;
        }
        nextStage(actionEvent);
    }

    private void nextStage(ActionEvent actionEvent)
    {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../MainMenu/Menu.fxml"));
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

    /* Drag and Drop handling */
    public void dragOver(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void dropFile(DragEvent dragEvent) {
        final Dragboard db = dragEvent.getDragboard();
        File file = db.getFiles().get(0);

        handleFile(file);
    }

    /* Copy paste handling */
    public void checkPaste(KeyEvent keyEvent) {
        if(textFieldPath.getText().compareTo(prompt) != 0 && !chosen){
            File file = new File(textFieldPath.getText());
            if(file != null){
                handleFile(file);
            }
        }
    }

    /* Getter */
    public static ArrayList<String> getFileNames() {
        return fileNames;
    }

}