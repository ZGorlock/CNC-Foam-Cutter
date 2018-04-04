/*
 * File:    ModelController.java
 * Package: gui.interfaces.main
 * Author:  Nicolas Lopez
 */

package gui.interfaces.main;

import grbl.APIgrbl;
import gui.interfaces.greeting.GreetingController;
import gui.interfaces.greeting.InputController;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import main.Main;
import renderer.Renderer;
import tracer.math.Delta;

import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The controller for the Model tab.
 */
public class ModelController
{
    
    //FXML Fields
    
    /**
     * The Swing node that holds the model rendering.
     */
    public SwingNode swingNodeModel;
    
    /**
     * The ImageView that holds the current gcode profile.
     */
    public ImageView profileImage;
    
    /**
     * The student nid output text field.
     */
    public Label studentNID;
    
    /**
     * The model description output text field.
     */
    public Label fileDesc;
    
    /**
     * The file name output text field.
     */
    public Label fileName;
    
    /**
     * The file size output text field.
     */
    public Label fileSize;
    
    /**
     * The completed percentage output text field.
     */
    public Label percentage;
    
    /**
     * The time remaining output text field.
     */
    public Label timeRemaining;
    
    
    //Constants
    
    /**
     * The maximum width of the foam block for the CNC machine, in inches.
     */
    public static final double MAX_WIDTH_CNC = 20;
    
    /**
     * The maximum length of the foam block for the CNC machine, in inches.
     */
    public static final double MAX_LENGTH_CNC = 40;
    
    /**
     * The maximum height of the foam block for the CNC machine, in inches.
     */
    public static final double MAX_HEIGHT_CNC = 10;
    
    /**
     * The maximum width of the foam block for the Hot Wire machine, in inches.
     */
    public static final double MAX_WIDTH_HOTWIRE = 66;
    
    /**
     * The maximum length of the foam block for the Hot Wire machine, in inches.
     */
    public static final double MAX_LENGTH_HOTWIRE = 66;
    
    /**
     * The maximum height of the foam block for the Hot Wire machine, in inches.
     */
    public static final double MAX_HEIGHT_HOTWIRE = 66;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static ModelController controller;
    
    /**
     * The instance of the Renderer.
     */
    public static Renderer renderer;
    
    
    //Fields
    
    /**
     * The timer for updating the percentage completed.
     */
    public Timer percentageTimer;
    
    /**
     * The timer for updating the time remaining.
     */
    public Timer timeRemainingTimer;
    
    
    //Constructors
    
    /**
     * Loads the Model tab.
     *
     * @return The Model tab, or null if there was an error.
     */
    public static Tab setup()
    {
        try {
            URL fxml = ModelController.class.getResource("Model.fxml");
            Tab tab = FXMLLoader.load(fxml);
            
            BorderPane pane = (BorderPane) tab.getContent();
            AnchorPane anchor = (AnchorPane) pane.getChildren().get(0);
            BorderPane anchorPane = (BorderPane) anchor.getChildren().get(0);
            StackPane stackPane = (StackPane) anchorPane.getChildren().get(0);
            SwingNode node = (SwingNode) stackPane.getChildren().get(0);
    
            final Delta delta = new Delta();
            node.setOnMousePressed(mouseEvent -> {
                delta.x = mouseEvent.getSceneX();
                delta.y = mouseEvent.getSceneY();
                Renderer.pauseModelAnimation();
                node.setCursor(Cursor.MOVE);
            });
            node.setOnMouseReleased(mouseEvent -> {
                Renderer.resumeModelAnimation();
                node.setCursor(Cursor.HAND);
            });
            node.setOnMouseDragged(mouseEvent -> {
                Renderer.handleCameraMovement(mouseEvent.getSceneX() - delta.x, mouseEvent.getSceneY() - delta.y);
                delta.x = mouseEvent.getSceneX();
                delta.y = mouseEvent.getSceneY();
            });
            node.setOnMouseEntered(mouseEvent -> node.setCursor(Cursor.HAND));
            
            return tab;
            
        } catch (Exception e) {
            System.err.println("There was an error loading Model.fxml!");
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    /**
     * Initializes the controller and sets up the Renderer.
     */
    public void initialize()
    {
        controller = this;
        renderer = Renderer.setup(swingNodeModel);
        
        File file = new File(GreetingController.getFilenames().get(0));
        if (file.exists()) {
            setFileName(file.getName());
        }
        
        setFileSize(calculateFileSize(file));
        setStudentNid(InputController.getNidFromText());
        setDesc(InputController.getDescFromText());
        updatePercentage();
        updateTime();
    }
    
    /**
     * Starts the percentage complete monitoring thread.
     */
    public void updatePercentage()
    {
        percentage.setText("0.00%");
        
        TimerTask updateCoordinates = new TimerTask()
        {
            private String state = "";
            
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    String percentComplete = APIgrbl.getPercentageComplete();
                    if (!percentComplete.equals(state)) {
                        percentage.textProperty().set(percentComplete);
                        state = percentComplete;
                    }
                    if (MenuController.controller != null && APIgrbl.grbl != null && APIgrbl.grbl.isDoneStreaming()) {
                        MenuController.controller.reset();
                        Main.resetApplication();
                    }
                });
            }
        };
        
        percentageTimer = new Timer();
        percentageTimer.scheduleAtFixedRate(updateCoordinates, 0, 100);
    }
    
    /**
     * Starts the time remaining monitoring thread.
     */
    public void updateTime()
    {
        timeRemaining.setText("00:00:00");
        
        TimerTask updateTime = new TimerTask()
        {
            private String state = "";
            
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    String remaining = APIgrbl.getTimeRemaining();
                    if (!remaining.equals(state)) {
                        timeRemaining.textProperty().set(remaining);
                        state = remaining;
                    }
                });
            }
        };
        
        timeRemainingTimer = new Timer();
        timeRemainingTimer.scheduleAtFixedRate(updateTime, 0, 100);
    }
    
    /**
     * Resets the controller.
     */
    public void reset()
    {
        if (percentageTimer != null) {
            percentageTimer.purge();
            percentageTimer.cancel();
        }
        if (timeRemainingTimer != null) {
            timeRemainingTimer.purge();
            timeRemainingTimer.cancel();
        }
    }
    
    
    //Setters
    
    /**
     * Sets the current gcode profile image.
     *
     * @param profile The gcode profile to display.
     */
    public static void setCurrentProfileImage(Image profile)
    {
        controller.swingNodeModel.setVisible(false);
        controller.profileImage.setVisible(true);
        controller.profileImage.setImage(profile);
    }
    
    /**
     * Sets the filename on the Model tab.
     *
     * @param fileName The filename.
     */
    public static void setFileName(String fileName)
    {
        if (fileName == null) {
            return;
        }
    
        StringBuilder sb = new StringBuilder(fileName);
        final int maxTextWidth = 28;
        int i = 0;
        while (i + maxTextWidth < sb.length() && (i = sb.lastIndexOf(" ", i + maxTextWidth)) != -1) {
            sb.replace(i, i + 1, "\n");
        }
        int j = 0;
        for (i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\n') {
                j = 0;
            } else {
                j++;
                if (j == maxTextWidth) {
                    sb.insert(i, "\n");
                    j = 0;
                    i--;
                }
            }
        }
        
        controller.fileName.setText(sb.toString());
    }
    
    /**
     * Sets the description on the Model tab.
     *
     * @param desc The description.
     */
    public static void setDesc(String desc)
    {
        if (desc == null) {
            return;
        }
        
        StringBuilder sb = new StringBuilder(desc);
        final int maxTextWidth = 28;
        int i = 0;
        while (i + maxTextWidth < sb.length() && (i = sb.lastIndexOf(" ", i + maxTextWidth)) != -1) {
            sb.replace(i, i + 1, "\n");
        }
        int j = 0;
        for (i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\n') {
                j = 0;
            } else {
                j++;
                if (j == maxTextWidth) {
                    sb.insert(i, "\n");
                    j = 0;
                    i--;
                }
            }
        }
        
        controller.fileDesc.setText(sb.toString());
    }
    
    /**
     * Sets the file size on the Model tab.
     *
     * @param fileSize The file size.
     */
    public static void setFileSize(String fileSize)
    {
        controller.fileSize.setText(fileSize);
    }
    
    /**
     * Sets the student NID on the Model tab.
     *
     * @param studentNid The student's NID.
     */
    public static void setStudentNid(String studentNid)
    {
        controller.studentNID.setText(studentNid);
    }
    
    
    //Functions
    
    /**
     * Calculates the filesize string for a file.
     *
     * @param file The file to measure.
     * @return The filesize string.
     */
    public static String calculateFileSize(File file)
    {
        double size = file.length();
        int i = 0;
        
        while (size / 100 > 10) {
            size /= 1024;
            i++;
        }
        
        return String.format("%.2f", size) +
                (i == 1 ? " KB" : (i == 2 ? " MB" : " B"));
    }
    
}
