package gui.Interfaces.Greeting;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import utils.MachineDetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GreetingController
{
    
    public TextField textFieldPath;
    public Button greyButton;
    public Button chooseButton;
    public Label dropFileText;
    public BorderPane contentPane;
    public ImageView knights;
    private String prompt;
    private boolean chosen;
    
    private ArrayList<String> fileNames = new ArrayList<>();
    
    private static GreetingController controller;
    
    public void setup()
    {
        controller = this;
    }

    public void initialize()
    {
        setup();
        
        chosen = false;
        prompt = textFieldPath.getText();
        fileNames.clear();

        knights.setOpacity(.5);
        greyButton.setStyle(" -fx-background-color: #BEBFC3;" +
                " -fx-background-radius: 6;" +
                " -fx-position: relative;" +
                "-fx-opacity: .25;");
        textFieldPath.setOnMouseClicked(e -> textFieldPath.setText(""));
        textFieldPath.setPromptText("Paste path here...");
    }
    
    public void chooseFile(ActionEvent actionEvent)
    {
        File file;
        if(textFieldPath.getText().compareTo(prompt) != 0)
        {
            file = new File(textFieldPath.getText());
            if(badExtension(file)) return;
        }else {
            FileChooser fileChooser = new FileChooser();
            if(MachineDetector.isCncMachine())
            {
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("3D models", "*.stl", "*.gcode");
                fileChooser.getExtensionFilters().add(extFilter);
            }
            fileChooser.setTitle("Select File(s)");
            file = fileChooser.showOpenDialog(new Stage());
        }
        handleFile(file);
    }

    private void handleFile(File file)
    {
        //Rotation Demo
//        controller.fileNames.clear();
//        try {
//            Files.list(Paths.get(new File("resources/jedicut files/rotation demo").getAbsolutePath())).forEach(e -> controller.fileNames.add(e.toString()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        chosen = true;
//        handleUploadedAnimation();
        
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
            handleUploadedAnimation();
        }
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

    /* Drag and Drop handling */
    public void dragOver(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void dropFile(DragEvent dragEvent) {
        final Dragboard db = dragEvent.getDragboard();
        File file = db.getFiles().get(0);
        if(badExtension(file)){ return; }
        handleFile(file);
    }

    /* Copy paste handling */
    public void checkPaste(KeyEvent keyEvent) {
        if(textFieldPath.getText().compareTo(prompt) != 0 && !chosen){
            File file = new File(textFieldPath.getText());
            if(!badExtension(file)){
                handleFile(file);
            }
        }
    }
    
    private boolean badExtension(File file)
    {
        String [] allowed ={"gcode","stl"};
        boolean ext = true;
        if(MachineDetector.isCncMachine() && file != null)
        {   // check the ending of the string for the extension
            String extension = "";
            int i = file.getAbsolutePath().lastIndexOf('.');

            if (i > 0) { extension = file.getAbsolutePath().substring(i + 1); }

            for(String s : allowed){
                if(extension.compareTo(s) == 0)
                {
                    ext = false;
                }
            }
        }
        return ext;
    }
    
    
    //Getters
    
    /**
     * Returns the STL model that was uploaded.
     *
     * @return The STL model file or an empty string if none.
     */
    public static String getModel()
    {
        if (controller.fileNames.size() == 1 && controller.fileNames.get(0).endsWith(".stl")) {
            return controller.fileNames.get(0);
        }
        return "";
    }
    
    /**
     * Returns the G-code files that was uploaded.
     *
     * @return The G-code file or an empty string if none.
     */
    public static String getGcode()
    {
        if (controller.fileNames.size() == 1 && controller.fileNames.get(0).endsWith(".gcode")) {
            return controller.fileNames.get(0);
        }
        return "";
    }
    
    /**
     * Returns the list of G-code files for the hot-wire slices that were uploaded.
     *
     * @return The List of G-code files for the hot-wire slices that were uploaded, or an empty list if none.
     */
    public static List<String> getSlices()
    {
        List<String> slices = new ArrayList<>();
        controller.fileNames.forEach(e -> {
            if (e.endsWith(".gcode")) {
                slices.add(e);
            }
        });
        return slices;
    }
    
    //Setters
    
    /**
     * Sets the list of uploaded files, for Debug mode.
     *
     * @param filenames The list of files.
     */
    public static void setFileNames(ArrayList<String> filenames)
    {
        controller.fileNames = filenames;
    }
    
}