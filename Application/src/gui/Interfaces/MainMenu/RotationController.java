package gui.Interfaces.MainMenu;

import gui.Interfaces.Greeting.GreetingController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import utils.GcodeTracer;

import java.awt.event.MouseEvent;
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
    private ScrollPane sp;
    private boolean firstScroll = true;
    
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
        index = 0;
        renderImages();
        textFieldDegrees.setPromptText("Enter degrees...");
    }

    private void renderImages()
    {
        sp = new ScrollPane();
        sp.setPrefSize(600, 340);
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setStyle("-fx-padding: 40px;");
        for (int i = 0; i < 10; i++)
        {
            Image image = new Image("file:src/gui/images/logo.PNG");
            ImageView pic = new ImageView(image);
            pic.setPreserveRatio(true);
            pic.setId(String.valueOf(i));

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
            String padding = "          ";
            String symbol = "°";
            Double d = (360 / 10) * 1.0;
            String deg = String.format("%.2f",d); // TODO change 10 to size
            Text text = new Text(padding + deg + symbol);

            vbox.getChildren().add(text);
            hbox.getChildren().add(vbox);
        }

        sp.setContent(hbox);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHmax(9.0); // set to the size of the arraylist aka gcodeTraces to keep track of current one

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

        // Handle valid input
        if(!isNumeric(input)) return;

        Double d = Double.parseDouble(input);

        HBox temp = (HBox)sp.getContent();

        temp.getChildren().get(index).setScaleX(1.18);
        temp.getChildren().get(index).setScaleY(1.18);

        VBox vbox = (VBox) temp.getChildren().get(index);
        Text text = (Text)vbox.getChildren().get(1);

        String deg = String.format("%.2f",d);
        String padding = "          ";
        String symbol = "°";
        text.setText(padding + deg + symbol);
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
        fileName.setText(im.impl_getUrl() + " " + iv.getId());

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
