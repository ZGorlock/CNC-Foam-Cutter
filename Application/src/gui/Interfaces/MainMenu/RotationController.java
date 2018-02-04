package gui.Interfaces.MainMenu;

import gui.Interfaces.Greeting.GreetingController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.GcodeTracer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * The controller for the Rotation tab.
 */
public class RotationController
{
    
    // FXML
    
    /**
     * The FXML elements of the tab.
     */
    public VBox vBox;
    public Label fileName;
    public TextField textFieldDegrees;

    // UI components
    /**
     * UI components that are created through code instead of xml
     */
    private ScrollPane sp;

    
    //Static Fields
    
    /**
     * The singleton instance fo the RotationController.
     */
    public static RotationController controller;
    
    
    //Fields
    
    /**
     * The list of BufferedImages corresponding to the uploaded gcode files.
     */
    public List<BufferedImage> gcodeTraces;

    /**
     *  index of current image being viewed
     */
    private int index;

    /**
     * Variable to determine if the first scroll is to the first element
     */
    private boolean firstScroll = true;

    //Static Methods
    
    /**
     * Creates the Rotation tab.
     *
     * @return The Rotation tab.
     */
    public static Tab setup() {
        try {
            URL fxml = RotationController.class.getResource("Rotation.fxml");
            Tab tab = FXMLLoader.load(fxml);
            
            return tab;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //Methods
    
    /**
     * Initializes the RotationController.
     */
    public void initialize()
    {
        //produce gcode traces to display to the user
        GcodeTracer gcodeTracer = new GcodeTracer();
        gcodeTraces = gcodeTracer.traceGcodeSet(GreetingController.getSlices());

        // Init index
        index = 0;

        // Set prompt text
        textFieldDegrees.setPromptText("Enter degrees...");

        renderImages();
    }

    private void renderImages()
    {
        // Create display area
        sp = new ScrollPane();
        sp.setPrefSize(600, 340);
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setStyle("-fx-padding: 40px;");

        int size = 10; //TODO change to size of array

        // Add images as a row
        for (int i = 0; i < size; i++)
        {
            Image image = new Image("file:src/gui/images/logo.PNG");
            ImageView pic = new ImageView(image);
            pic.setPreserveRatio(true);
            pic.setId(String.valueOf(i));

            // Let images be selected
            pic.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {

                    int newIndex = Integer.parseInt(pic.getId());
                    if(newIndex == 0 && firstScroll)
                        handleSPAnimation();
                    else
                        slowScrollToImage(sp,newIndex);
                }
            });

            VBox vbox = new VBox();
            vbox.getChildren().add(pic);

            // Initialize all evenly spaced degrees
            Double d = (360 / size) * 1.0;

            // Setting the new degree
            Text text = new Text(formatDegree(d));

            // Add to the parent
            vbox.getChildren().add(text);
            hbox.getChildren().add(vbox);
        }

        // Adding to the scrollpane
        sp.setContent(hbox);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHmax((size - 1)*1.0); // TODO set to the size of the arraylist aka gcodeTraces to keep track of current one

        // Change the view when something is selected.
        sp.hvalueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {

                index = new_val.intValue();
                firstScroll = false;
                handleSPAnimation();
            }
        });

        vBox.getChildren().add(0,sp);

        /* TODO once there are images to render
        for (int i = 0; i < gcodeTraces.size(); i++)
        {
            Image image = SwingFXUtils.toFXImage(gcodeTraces.get(i), null );
            ImageView pic = new ImageView(image);
            hbox.getChildren().add(pic);
        }
        */
    }

    public void queueRotation()
    {
        String input = textFieldDegrees.getText();
        textFieldDegrees.setText("");

        // Handle invalid input
        if(!isNumeric(input)) return;

        Double d = Double.parseDouble(input);

        // Set new selected value
        HBox temp = (HBox)sp.getContent();

        temp.getChildren().get(index).setScaleX(1.18);
        temp.getChildren().get(index).setScaleY(1.18);

        VBox vbox = (VBox) temp.getChildren().get(index);
        Text text = (Text)vbox.getChildren().get(1);

        text.setText(formatDegree(d));
    }

    private String formatDegree(Double d)
    {
        // String formatting
        String deg = String.format("%.2f",d);
        String padding = "          ";
        String symbol = "°";
        return padding + deg + symbol;
    }

    private static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private void handleSPAnimation()
    {
        HBox temp = (HBox)sp.getContent();

        temp.getChildren().get(index).setScaleX(1.18);
        temp.getChildren().get(index).setScaleY(1.18);

        VBox vbox = (VBox) temp.getChildren().get(index);

        ImageView iv = (ImageView)vbox.getChildren().get(0);
        Image im = iv.getImage();
        fileName.setText("File Selected: "+im.impl_getUrl() + " " + iv.getId());

        // Prevent index out of bounds and return other images to normal size
        if(index + 1 < temp.getChildren().size())
        {
            temp.getChildren().get(index + 1).setScaleX(1);
            temp.getChildren().get(index + 1).setScaleY(1);
        }

        if(index - 1 >= 0)
        {
            temp.getChildren().get(index - 1).setScaleX(1);
            temp.getChildren().get(index - 1).setScaleY(1);
        }
    }

    private static void slowScrollToImage(ScrollPane scrollPane, int value) {
        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(0.8),
                        new KeyValue(scrollPane.hvalueProperty(), value)));
        animation.play();
    }
}
