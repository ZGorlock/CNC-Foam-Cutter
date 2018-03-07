/*
 * File:    RotationController.java
 * Package: gui.interfaces.main
 * Author:  Nicolas Lopez
 */

package gui.interfaces.main;

import gui.interfaces.greeting.GreetingController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.GcodeTracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The controller for the Rotation tab.
 */
public class RotationController
{
    
    //FXML Fields
    
    /**
     * The container for the scrolling list of gcode profiles.
     */
    public VBox vBox;
    
    /**
     * The file name label for the currently selected gcode profile.
     */
    public Label fileName;
    
    /**
     * The input field for setting degrees of rotation for the selected gcode profile.
     */
    public TextField textFieldDegrees;
    
    /**
     * The window for the scrolling list of gcode profiles.
     */
    private ScrollPane sp;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static RotationController controller;
    
    
    //Fields
    
    /**
     * The list of BufferedImages corresponding to the uploaded gcode files.
     */
    public List<BufferedImage> gcodeTraces;
    
    /**
     * The map between the Image in the UI and their filenames.
     */
    private Map<Image, String> gcodeTraceFileMap;
    
    /**
     * The queue of rotation degrees for each profile.
     */
    public List<Double> rotationQueue;
    
    /**
     * The index of the gcode profile current selected.
     */
    private int index;
    
    /**
     * A flag indicating whether or not the first scroll is to the first element.
     */
    private boolean firstScroll = true;
    
    
    //Constructors
    
    /**
     * Loads the Rotation tab.
     *
     * @return The Rotation tab, or null if there was an error.
     */
    public static Tab setup()
    {
        try {
            URL fxml = RotationController.class.getResource("Rotation.fxml");
            Tab tab = FXMLLoader.load(fxml);
            return tab;
            
        } catch (Exception e) {
            System.err.println("There was an error loading Rotation.fxml!");
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    /**
     * Initializes the controller.
     */
    public void initialize()
    {
        //produce gcode traces to display to the user
        GcodeTracer gcodeTracer = new GcodeTracer();
        gcodeTraceFileMap = new HashMap<>();
        gcodeTraces = gcodeTracer.traceGcodeSet(GreetingController.getSlices());
        rotationQueue = new ArrayList<>();
        
        // Init index
        index = 0;
        
        // Set prompt text
        textFieldDegrees.setPromptText("Enter degrees...");
        
        renderImages();
    }
    
    /**
     * Renders the gcode profiles in the UI.
     */
    private void renderImages()
    {
        // Create display area
        sp = new ScrollPane();
        sp.setPrefSize(600, 340);
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setStyle("-fx-padding: 40px; -fx-alignment: CENTER;");
        hbox.setAlignment(Pos.CENTER);
        
        // Add images as a row
        for (int i = 0; i < gcodeTraces.size(); i++) {
            Image image = SwingFXUtils.toFXImage(gcodeTraces.get(i), null);
            ImageView pic = new ImageView(image);
            gcodeTraceFileMap.put(image, new File(GreetingController.getSlices().get(i)).getName());
            
            pic.setPreserveRatio(true);
            pic.setId(String.valueOf(i));
            pic.setFitHeight(230);
            
            // Let images be selected
            pic.setOnMouseClicked(event -> {
                int newIndex = Integer.parseInt(pic.getId());
                if (newIndex == 0 && firstScroll) {
                    handleSPAnimation();
                } else {
                    slowScrollToImage(newIndex);
                }
            });
            
            VBox vbox = new VBox();
            vbox.getChildren().add(pic);
            // Initialize all evenly spaced degrees
            Double d = (360 / gcodeTraces.size()) * 1.0;
            rotationQueue.add(d);
            
            // Setting the new degree
            Text text = new Text(formatDegree(d));
            
            // Add to the parent
            vbox.getChildren().add(text);
            vbox.setAlignment(Pos.CENTER);
            HBox.setHgrow(vbox, Priority.ALWAYS);
            hbox.getChildren().add(vbox);
        }
        
        // Adding to the scrollpane
        if (gcodeTraces.size() <= 4) {
            sp.setFitToWidth(true);
        }
        sp.setContent(hbox);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHmax((gcodeTraces.size() - 1) * 1.0);
        
        // Change the view when something is selected.
        sp.hvalueProperty().addListener((ov, old_val, new_val) -> {
            index = new_val.intValue();
            firstScroll = false;
            handleSPAnimation();
        });
        
        vBox.getChildren().add(0, sp);
        vBox.setAlignment(Pos.CENTER);
    }
    
    /**
     * Sets the rotation slice for a gcode profile.
     */
    public void queueRotation()
    {
        String input = textFieldDegrees.getText();
        textFieldDegrees.setText("");
        
        // Handle invalid input
        if (!isValidAngle(input)) {
            return;
        }
        
        Double d = Double.parseDouble(input);
        rotationQueue.set(index, d);
        
        // Set new selected value
        HBox temp = (HBox) sp.getContent();
        
        temp.getChildren().get(index).setScaleX(1.18);
        temp.getChildren().get(index).setScaleY(1.18);
        
        VBox vbox = (VBox) temp.getChildren().get(index);
        Text text = (Text) vbox.getChildren().get(1);
        
        text.setText(formatDegree(d));
    }
    
    /**
     * Determines if an angle value is valid.
     *
     * @param str The angle value.
     * @return Whether the angle value is valid or not.
     */
    private boolean isValidAngle(String str)
    {
        try {
            double d = Double.parseDouble(str);
            if (d < 0 || d > 360) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Formats an angle string from an angle.
     *
     * @param d The angle.
     * @return The formatted angle string.
     */
    private String formatDegree(Double d)
    {
        // String formatting
        String deg = String.format("%.2f", d);
        String symbol = "°";
        return deg + symbol;
    }
    
    /**
     * Handles the animation of the scroll pane.
     */
    private void handleSPAnimation()
    {
        HBox temp = (HBox) sp.getContent();
        
        temp.getChildren().get(index).setScaleX(1.18);
        temp.getChildren().get(index).setScaleY(1.18);
        
        VBox vbox = (VBox) temp.getChildren().get(index);
        
        ImageView iv = (ImageView) vbox.getChildren().get(0);
        Image im = iv.getImage();
        fileName.setText("File Selected: " + gcodeTraceFileMap.get(im) + " - Profile #" + (Integer.valueOf(iv.getId()) + 1));
        
        // Prevent index out of bounds and return other images to normal size
        if (index + 1 < temp.getChildren().size()) {
            temp.getChildren().get(index + 1).setScaleX(1);
            temp.getChildren().get(index + 1).setScaleY(1);
        }
        
        if (index - 1 >= 0) {
            temp.getChildren().get(index - 1).setScaleX(1);
            temp.getChildren().get(index - 1).setScaleY(1);
        }
    }
    
    /**
     * Automatically scrolls to the selected gcode profile.
     *
     * @param value The index of the gcode profile to scroll to.
     */
    private void slowScrollToImage(int value)
    {
        double speed = .8;
        if (gcodeTraces.size() < 5) {
            speed = .4;
        }
        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(speed),
                        new KeyValue(sp.hvalueProperty(), value)));
        animation.play();
    }
    
}
