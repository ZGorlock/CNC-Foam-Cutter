/*
 * File:    GreetingController.java
 * Package: gui.interfaces.greeting
 * Author:  Nicolas Lopez
 */

package gui.interfaces.greeting;

import gui.interfaces.main.GcodeController;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import utils.MachineDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller for the Greeting page.
 */
public class GreetingController
{
    
    //FXML Fields
    
    /**
     * The text field for pasting a file path.
     */
    public TextField textFieldPath;
    
    /**
     * The Upload button.
     */
    public Button uploadButton;
    
    /**
     * The Choose File button for selecting a file.
     */
    public Button chooseButton;
    
    /**
     * The drop file here prompt.
     */
    public Label dropFileText;
    
    /**
     * The knights image.
     */
    public ImageView knights;
    
    /**
     * The select a file prompt.
     */
    private String prompt;
    
    
    //Fields
    
    /**
     * The list of files that were uploaded.
     */
    private ArrayList<String> fileNames = new ArrayList<>();
    
    /**
     * A flag indicating whether a file has been chosen or not.
     */
    private boolean chosen;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    private static GreetingController controller;
    
    
    //Methods
    
    /**
     * Initializes the controller.
     */
    public void initialize()
    {
        setup();
        
        chosen = false;
        prompt = textFieldPath.getText();
        fileNames.clear();
        
        knights.setOpacity(.5);
        uploadButton.setStyle(" -fx-background-color: #F2C313;" +
                " -fx-background-radius: 6;" +
                " -fx-position: relative;" +
                "-fx-opacity: .25;");
        textFieldPath.setOnMouseClicked(e -> textFieldPath.setText(""));
        textFieldPath.setPromptText("Paste path here...");
    }
    
    /**
     * Sets up the controller.
     */
    public void setup()
    {
        controller = this;
    }
    
    /**
     * Handles the Choose File button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void chooseFile(ActionEvent actionEvent)
    {
        File file;
        if (textFieldPath.getText().compareTo(prompt) != 0) {
            file = new File(textFieldPath.getText());
            if (badExtension(file)) {
                return;
            }
        } else {
            FileChooser fileChooser = new FileChooser();
            if (MachineDetector.isCncMachine()) {
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("3D models", "*.stl", "*.gcode");
                fileChooser.getExtensionFilters().add(extFilter);
            }
            fileChooser.setTitle("Select File(s)");
            file = fileChooser.showOpenDialog(new Stage());
        }
        handleFile(file);
    }
    
    /**
     * Handles a file upload.
     *
     * @param file The file being uploaded.
     */
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
        
        if (file != null) {
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
    
    /**
     * Performs the animation when a file is uploaded.
     */
    private void handleUploadedAnimation()
    {
        // Animations and Stylings
        knights.setOpacity(1.0);
        knights.setScaleX(1.18);
        knights.setScaleY(1.18);
        uploadButton.setStyle(" -fx-background-color: #F2C313;" +
                " -fx-background-radius: 6;" +
                " -fx-position: relative;");
        dropFileText.setText("");
    }
    
    /**
     * Handles the Upload button.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void upload(ActionEvent actionEvent)
    {
        if (!chosen) { // must upload a file to continue
            chooseButton.setStyle(" -fx-background-color: #BEBFC3;" +
                    " -fx-background-radius: 6;" +
                    " -fx-position: relative;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, .8, 0, 0);");
            return;
        }
        
        if (!getModel().isEmpty()) {
            File model = new File(getModel());
            //TODO error handling
//            SystemNotificationController notification = new SystemNotificationController();
//            notification.initialize();
//            notification.raise("Parsing your model...", false);
            GcodeController.slice(model);
        }
        
        nextStage(actionEvent);
    }
    
    /**
     * Moves to the Menu window.
     *
     * @param actionEvent The event that triggered the handler.
     */
    private void nextStage(ActionEvent actionEvent)
    {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../main/Menu.fxml"));
            Stage stage = new Stage();
            stage.setTitle("3D CNC Foam Cutter");
            stage.setScene(new Scene(root, 1280, 960));
            stage.show();
            stage.setOnCloseRequest(t -> Main.killApplication());
            
            // Hide the current window
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            
        } catch (Exception e) {
            System.err.println("There was an error loading Menu.fxml!");
            e.printStackTrace();
            Main.killApplication();
        }
    }
    
    /**
     * Handles a file drag event.
     *
     * @param dragEvent The event that triggered the handler.
     */
    public void dragOver(DragEvent dragEvent)
    {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }
    
    /**
     * Handles a file drop event.
     *
     * @param dragEvent The event that triggered the handler.
     */
    public void dropFile(DragEvent dragEvent)
    {
        final Dragboard db = dragEvent.getDragboard();
        File file = db.getFiles().get(0);
        if (badExtension(file)) {
            return;
        }
        handleFile(file);
    }
    
    /**
     * Handles a clipboard paste event.
     *
     * @param keyEvent The event that triggered the handler.
     */
    public void checkPaste(KeyEvent keyEvent)
    {
        if (textFieldPath.getText().compareTo(prompt) != 0 && !chosen) {
            File file = new File(textFieldPath.getText());
            if (!badExtension(file)) {
                handleFile(file);
            }
        }
    }
    
    /**
     * Determines if a file is of a valid file type.
     *
     * @param file The file.
     * @return Whether the file is of a valid file type or not.
     */
    private boolean badExtension(File file)
    {
        if (file == null) {
            return false;
        }
        
        List<String> allowed = new ArrayList<>();
        allowed.add("gcode");
        if (MachineDetector.isCncMachine()) {
            allowed.add("stl");
        }
        
        boolean ext;
        int i = file.getAbsolutePath().lastIndexOf('.');
        if (i > 0) {
            String extension = file.getAbsolutePath().substring(i + 1);
            ext = !allowed.contains(extension);
        } else {
            ext = true;
        }
        return ext;
    }
    
    
    //Getters
    
    /**
     * Returns the list of files that were uploaded.
     *
     * @return The list of files that were uploaded.
     */
    public static List<String> getFilenames()
    {
        return controller.fileNames;
    }
    
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