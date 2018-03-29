/*
 * File:    RotationController.java
 * Package: gui.interfaces.main
 * Author:  Nicolas Lopez
 */

package gui.interfaces.main;

import gui.interfaces.greeting.GreetingController;
import gui.interfaces.popup.SystemNotificationController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
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
    
    //Constants
    
    /**
     * The minimum degree of rotation that the machine can perform.
     */
    public static final int MIN_ROTATION_DEGREE = 1;
    
    
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
     * The button for setting degrees of rotation for the selected gcode profile.
     */
    public Button queueButton;
    
    /**
     * The input field for setting degrees of rotation per step during printing.
     */
    public TextField textFieldRotationStep;

    /**
     * The button for setting degrees of rotation for the selected gcode profile.
     */
    public Button rotationStepButton;

    /**
     * The window for the scrolling list of gcode profiles.
     */
    public ScrollPane sp;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static RotationController controller;
    
    /**
     * The queue of profiles to slice.
     */
    public static final List<String> queue = new ArrayList<>();
    
    
    //Fields
    
    /**
     * The list of BufferedImages corresponding to the uploaded gcode files.
     */
    public List<BufferedImage> gcodeTraces;
    
    /**
     * The map between the Image in the UI and their filenames.
     */
    public Map<Image, String> gcodeTraceFileMap;
    
    /**
     * The map between the filename of the profile and the generated BufferedImage.
     */
    public Map<String, Image> gcodeTraceMap;
    
    /**
     * The map between the Image in the UI and the rotation degrees for that profile.
     */
    public Map<Image, Integer> rotationProfileMap;
    
    /**
     * The rotation step to use during printing.
     */
    public int rotationStep;
    
    /**
     * The index of the gcode profile current selected.
     */
    private int index;
    
    /**
     * A flag indicating whether or not the first scroll is to the first element.
     */
    private boolean firstScroll = true;

    /**
     * A flag indicating whether or not we can drag elements, it is set to true before clicking print.
     */
    public boolean draggable = true;

    /**
     * The temporary field for the image that is going to be received when swapping images
     */
    private Image recieverImage;

    /**
     * The temporary field for the file that is going to be sent when swapping images
     */
    private String giverFile;

    /**
     * The temporary field for the file that is going to be received when swapping images
     */
    private String recieverFile;

    /**
     * The temporary field for the degrees of the image that is going to be sent when swapping images
     */
    private int giverDegrees;

    /**
     * The temporary field for the degrees of the image that is going to be received when swapping images
     */
    private int recieverDegrees;
    
    
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
        controller = this;
        
        //produce gcode traces to display to the user
        GcodeTracer gcodeTracer = new GcodeTracer();
        queue.clear();
        gcodeTraceFileMap = new HashMap<>();
        gcodeTraceMap = new HashMap<>();
        gcodeTraces = gcodeTracer.traceGcodeSet(GreetingController.getSlices());
        rotationProfileMap = new HashMap<>();
        
        // Init index
        index = 0;
        
        // Set prompt text
        textFieldDegrees.setPromptText("Enter degrees...");
        textFieldDegrees.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.compareTo(event.getCode()) == 0) {
                queueRotation();
            }
        });
    
        textFieldRotationStep.setPromptText("Enter step...");
        textFieldRotationStep.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.compareTo(event.getCode()) == 0) {
                setRotationStep();
            }
        });
        rotationStep = MIN_ROTATION_DEGREE;
        textFieldRotationStep.setText(String.valueOf(rotationStep));
        
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
        
        int d = 360 / gcodeTraces.size();
        int degreeGap = 360 - (d * gcodeTraces.size());
        
        ImageView firstPic = null;
        
        // Add images as a row
        for (int i = 0; i < gcodeTraces.size(); i++) {
            Image image = SwingFXUtils.toFXImage(gcodeTraces.get(i), null);
            ImageView pic = new ImageView(image);
            gcodeTraceFileMap.put(image, new File(GreetingController.getSlices().get(i)).getAbsolutePath());
            gcodeTraceMap.put(new File(GreetingController.getSlices().get(i)).getAbsolutePath(), image);
            
            if (i == 0) {
                firstPic = pic;
            }

            // Initialize all evenly spaced degrees
            if (degreeGap > 0) {
                rotationProfileMap.put(image, d + 1);
                degreeGap--;
            } else {
                rotationProfileMap.put(image, d);
            }

            pic.setPreserveRatio(true);
            pic.setId(String.valueOf(i));
            pic.setFitHeight(220);
            
            // Let images be selected
            pic.setOnMouseClicked(event -> {
                int newIndex = Integer.parseInt(pic.getId());
                textFieldDegrees.setText(String.valueOf(rotationProfileMap.get(pic.getImage())));
                if (newIndex == 0 && firstScroll) {
                    handleSPAnimation();
                } else {
                    slowScrollToImage(newIndex);
                }
            });

            pic.setOnDragDetected(event -> {
                if (draggable) {
                    Dragboard db = pic.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(pic.getImage());
                    db.setContent(content);
                    giverFile = gcodeTraceFileMap.get(pic.getImage());
                    giverDegrees = rotationProfileMap.get(pic.getImage());
                    event.consume();
                }
            });

            pic.setOnDragOver(event -> {
                if (draggable) {
                    if (event.getDragboard().hasImage()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
            });

            pic.setOnDragDropped(event -> {
                if (draggable) {
                    Image newPic = event.getDragboard().getImage();
                    recieverImage = pic.getImage();
    
                    recieverFile = gcodeTraceFileMap.get(recieverImage);
                    recieverDegrees = rotationProfileMap.get(recieverImage);
    
                    gcodeTraceFileMap.remove(pic.getImage());
                    rotationProfileMap.remove(pic.getImage());
    
                    pic.setImage(newPic);
    
                    gcodeTraceFileMap.put(pic.getImage(), giverFile);
                    gcodeTraceMap.put(giverFile, pic.getImage());
                    rotationProfileMap.put(pic.getImage(), giverDegrees);
    
                    HBox temp = (HBox) sp.getContent();
                    int newIndex = Integer.parseInt(pic.getId());
                    VBox box = (VBox) temp.getChildren().get(newIndex);
                    VBox vbox = (VBox) temp.getChildren().get(newIndex);
                    Text text = (Text) vbox.getChildren().get(1);
    
                    text.setText(formatDegree(giverDegrees));
                }
            });

            pic.setOnDragDone(event -> {
                if (draggable) {
                    gcodeTraceFileMap.remove(pic.getImage());
                    rotationProfileMap.remove(pic.getImage());
    
                    pic.setImage(recieverImage);
    
                    gcodeTraceFileMap.put(pic.getImage(), recieverFile);
                    gcodeTraceMap.put(recieverFile, pic.getImage());
                    rotationProfileMap.put(pic.getImage(), recieverDegrees);
    
                    HBox temp = (HBox) sp.getContent();
                    int newIndex = Integer.parseInt(pic.getId());
                    VBox box = (VBox) temp.getChildren().get(newIndex);
                    VBox vbox = (VBox) temp.getChildren().get(newIndex);
                    Text text = (Text) vbox.getChildren().get(1);
    
                    text.setText(formatDegree(recieverDegrees));
                }
            });

            VBox vbox = new VBox();
            vbox.getChildren().add(pic);
            
            // Setting the new degree
            Text text = new Text(formatDegree(rotationProfileMap.get(image)));
            
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
        
        Event.fireEvent(firstPic, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
        
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
        
        // Handle invalid input
        if (!isValidAngle(input)) {
            return;
        }
    
        HBox temp = (HBox) sp.getContent();
        VBox box = (VBox) temp.getChildren().get(index);
        
        Integer d = Integer.parseInt(input);
        rotationProfileMap.replace(((ImageView) box.getChildren().get(0)).getImage(), d);
        
        // Set new selected value
        temp.getChildren().get(index).setScaleX(1.18);
        temp.getChildren().get(index).setScaleY(1.18);
        
        VBox vbox = (VBox) temp.getChildren().get(index);
        Text text = (Text) vbox.getChildren().get(1);
        
        text.setText(formatDegree(d));
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
        if (im == null) {
            System.err.println("Image for profile at index: " + index + " is null");
            
        } else {
            StringBuilder sb = new StringBuilder("Profile #" + (Integer.valueOf(iv.getId()) + 1) + " - " + (new File(gcodeTraceFileMap.get(im))).getName());
            final int maxTextWidth = 36;
            int j = 0;
            for (int i = 0; i < sb.length(); i++) {
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
    
            fileName.setText(sb.toString());
        }
        
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
    
    /**
     * Sets the rotation step degree from the input field.
     */
    public void setRotationStep()
    {
        String rotationStepDegree = textFieldRotationStep.getText();
        if (isValidStepAngle(rotationStepDegree)) {
            rotationStep = Integer.parseInt(rotationStepDegree);
        }
    }
    
    /**
     * Resets the controller.
     */
    public void reset()
    {
        draggable = true;
    }
    
    
    //Static Methods
    
    /**
     * Determines if an angle value is valid.
     *
     * @param str The angle value.
     * @return Whether the angle value is valid or not.
     */
    public static boolean isValidAngle(String str)
    {
        try {
            int d = Integer.parseInt(str);
            if (d < 0 || d > 360) {
                SystemNotificationController.throwNotification("The angle must be between 0 and 360 degrees!", false, false);
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Determines if an angle value is a valid step angle.
     *
     * @param str The step angle value.
     * @return Whether the angle value step is valid or not.
     */
    public static boolean isValidStepAngle(String str)
    {
        try {
            int d = Integer.parseInt(str);
            if (d <= 0 || d > 360) {
                SystemNotificationController.throwNotification("The step angle must be between 1 and 360 degrees! (360 for no rotation)", false, false);
                return false;
            }
            if (360 % d != 0) {
                SystemNotificationController.throwNotification("The step angle must evenly divide into 360!", false, false);
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
    public static String formatDegree(Integer d)
    {
        // String formatting
        String deg = String.format("%d", d);
        String symbol = "°";
        return deg + symbol;
    }
    
    /**
     * Generates the rotation profile queue.
     *
     * @return Whether the queue was successfully generated or not.
     */
    public static boolean generateQueue()
    {
        List<Image> profiles = new ArrayList<>();
        
        HBox temp = (HBox) controller.sp.getContent();
        for (int i = 0; i < controller.gcodeTraces.size(); i++) {
            VBox box = (VBox) temp.getChildren().get(i);
            Image image = ((ImageView) box.getChildren().get(0)).getImage();
            profiles.add(image);
        }
        
        return generateQueueHelper(profiles);
    }
    
    /**
     * Generates the rotation profile queue.
     *
     * @param profiles The list of profile images in the queue.
     * @return Whether the queue was successfully generated or not.
     */
    public static boolean generateQueueHelper(List<Image> profiles)
    {
        queue.clear();
        
        for (Image image : profiles) {
            int degrees = controller.rotationProfileMap.get(image);
            if (degrees % controller.rotationStep != 0) {
                return false;
            }
        }
    
        for (Image image : profiles) {
            int degrees = controller.rotationProfileMap.get(image);
            int cycles = degrees / controller.rotationStep;
            for (int j = 0; j < cycles; j++) {
                queue.add(controller.gcodeTraceFileMap.get(image));
            }
        }
        
        return true;
    }
    
}
