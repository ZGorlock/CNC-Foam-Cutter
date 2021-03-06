/*
 * File:    GreetingController.java
 * Package: gui.interfaces.greeting
 * Author:  Nicolas Lopez
 */

package gui.interfaces.greeting;

import gui.interfaces.help.HelpController;
import gui.interfaces.main.GcodeController;
import gui.interfaces.popup.SystemNotificationController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import utils.MachineDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
     * The Slic3r console.
     */
    public TextArea slicerConsole;
    
    /**
     * The select a file prompt.
     */
    private String prompt;
    
    
    //Fields
    
    /**
     * The list of files that were uploaded.
     */
    private List<String> fileNames = new ArrayList<>();
    
    /**
     * A flag indicating whether a file has been chosen or not.
     */
    private boolean chosen;
    
    /**
     * The console output from the slicing process.
     */
    public final List<String> slicerOutput = new ArrayList<>();
    
    /**
     * The timer for updating the slicer console.
     */
    public Timer slicerConsoleTimer;
    
    /**
     * A flag indicating whether the slicing process has finished or not.
     */
    public boolean slicingDone;
    
    /**
     * A flag indicating whether the slicing process was successful or not.
     */
    public boolean slicingSuccess;
    
    /**
     * A flag indicating whether the slicing process is in progress or not.
     */
    public boolean slicingInProgress;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static GreetingController controller;
    
    
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
        
        slicerOutput.clear();
        slicingDone = false;
        slicingInProgress = false;
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
        if (slicingInProgress) {
            return;
        }
        
        File file = null;
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
            
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());
            if (selectedFiles != null) {
                if (selectedFiles.size() > 1) {
                    handleMultipleFiles(selectedFiles);
                    return;
                }
                
                file = selectedFiles.get(0);
            }
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
        boolean success = false;
        if (file != null) {
            if (file.isDirectory()) {
                if (MachineDetector.isCncMachine()) {
                    SystemNotificationController.throwNotification("Please select a single model or gcode file!", false, false);
                    return;
                }
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (!badExtension(f)) {
                            success = true;
                            fileNames.add(f.getAbsolutePath());
                        }
                    }
                }
            } else {
                if (!badExtension(file)) {
                    // this constructs all the file names
                    if (MachineDetector.isCncMachine()) {
                        fileNames.clear();
                    }
                    fileNames.add(file.getAbsolutePath());
                    success = true;
                }
            }
            
            if (success) {
                chosen = true;
                textFieldPath.setText(file.getAbsolutePath());
                handleUploadedAnimation();
            }
        }
    }
    
    /**
     * Handles multiple files being chosen
     *
     * @param files The list of files being uploaded.
     */
    private void handleMultipleFiles(List<File> files)
    {
        if (files.isEmpty()) {
            return;
        }
        
        if (MachineDetector.isCncMachine()) {
            SystemNotificationController.throwNotification("Please select a single model or gcode file!", false, false);
            return;
        }
        
        boolean success = false;
        
        for (File f : files) {
            if (badExtension(f)) {
                continue;
            }
            success = true;
            fileNames.add(f.getAbsolutePath());
        }
        
        if (success) {
            chosen = true;
            textFieldPath.setText(files.get(0).getParentFile().getAbsolutePath());
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
        if (slicingInProgress) {
            return;
        }
        
        if (!chosen) { // must upload a file to continue
            chooseButton.setStyle(" -fx-background-color: #BEBFC3;" +
                    " -fx-background-radius: 6;" +
                    " -fx-position: relative;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, .8, 0, 0);");
            return;
        }
        
        if (!getModel().isEmpty()) {
            File model = new File(getModel());
            slicerConsole.setVisible(true);
            slicerOutput.add("Slicing your model into gcode...");
            slicerOutput.add("");
            slicingInProgress = true;
            uploadButton.setDisable(true);
            updateSlicerConsole(actionEvent);
            
            Timer slicingTimer = new Timer();
            slicingTimer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    GcodeController.slice(model); //Error is handled internally
                }
            }, 0);
            
        } else {
            nextStage(actionEvent);
        }
    }
    
    /**
     * Updates the slicer console.
     *
     * @param actionEvent The event that triggered the handler that started this update thread.
     */
    private void updateSlicerConsole(ActionEvent actionEvent)
    {
        TimerTask updateSlicerConsole = new TimerTask()
        {
            private int state;
            
            @Override
            public void run()
            {
                if (slicerOutput.size() != state) {
                    state = slicerOutput.size();
                    
                    StringBuilder slicerOutputBuilder = new StringBuilder();
                    for (int i = 0; i < slicerOutput.size(); i++) {
                        slicerOutputBuilder.append(slicerOutput.get(i));
                        if (i != slicerOutput.size() - 1) {
                            slicerOutputBuilder.append("\n");
                        }
                    }
                    
                    slicerConsole.textProperty().set(slicerOutputBuilder.toString());
                    slicerConsole.appendText("");
                    slicerConsole.setScrollTop(Double.MAX_VALUE);
                }
                
                if (slicingDone) {
                    slicerConsoleTimer.cancel();
                    if (slicingSuccess) {
                        Platform.runLater(() -> nextStage(actionEvent));
                    }
                }
            }
        };
        slicerConsoleTimer = new Timer();
        slicerConsoleTimer.scheduleAtFixedRate(updateSlicerConsole, 0, 50);
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
            
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            
        } catch (Exception e) {
            System.err.println("There was an error loading Menu.fxml!");
            e.printStackTrace();
            Main.killApplication();
        }
    }
    
    /**
     * Opens the user help window.
     *
     * @param actionEvent The event that triggered the call.
     */
    public void help(MouseEvent actionEvent)
    {
        HelpController.help();
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
        if (slicingInProgress) {
            return;
        }
        
        final Dragboard db = dragEvent.getDragboard();
        if (db.getFiles().size() > 1) {
            handleMultipleFiles(db.getFiles());
        } else {
            handleFile(db.getFiles().get(0));
        }
    }
    
    /**
     * Handles a clipboard paste event.
     *
     * @param keyEvent The event that triggered the handler.
     */
    public void checkPaste(KeyEvent keyEvent)
    {
        if (slicingInProgress) {
            return;
        }
        
        if (textFieldPath.getText().compareTo(prompt) != 0 && !chosen) {
            String path = textFieldPath.getText();
            File file = new File(textFieldPath.getText());
            if (file.isDirectory()) {
                handleFile(file);
            }
            if (!badExtension(file)) {
                handleFile(file);
            }
        }
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
        if (controller.fileNames.size() == 1 && controller.fileNames.get(0).toLowerCase().endsWith(".stl")) {
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
        if (controller.fileNames.size() == 1 && controller.fileNames.get(0).toLowerCase().endsWith(".gcode")) {
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
            if (e.toLowerCase().endsWith(".gcode")) {
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
    public static void setFileNames(List<String> filenames)
    {
        controller.fileNames = filenames;
    }
    
    
    //Functions
    
    /**
     * Determines if a file is of a valid file type.
     *
     * @param file The file.
     * @return Whether the file is of a valid file type or not.
     */
    public static boolean badExtension(File file)
    {
        if (file == null || !file.exists()) {
            return true;
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
            ext = !allowed.contains(extension.toLowerCase());
        } else {
            ext = true;
        }
        return ext;
    }
    
}