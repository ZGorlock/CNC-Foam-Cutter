package gui.Interfaces.MainMenu;

import gui.Interfaces.Greeting.GreetingController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.GcodeTracer;
import javafx.scene.control.ScrollPane;

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
        textFieldDegrees.setOnMouseClicked(e -> textFieldDegrees.setText(""));
        textFieldDegrees.setPromptText("Enter degrees...");
    }

    private void renderImages()
    {
        sp = new ScrollPane();
        sp.setPrefSize(600, 270);
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setStyle("-fx-padding: 40px;");
        int size = 1;
        for (int i = 0; i < 10; i++)
        {
            Image image = new Image("file:src/gui/images/logo.PNG");
            ImageView pic = new ImageView(image);
            size = (int)pic.getFitWidth();
            pic.setPreserveRatio(true);
            hbox.getChildren().add(pic);
        }

        sp.setContent(hbox);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHmax(9.0); // set to the size of the arraylist aka gcodeTraces to keep track of current one

        sp.hvalueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {

                index = new_val.intValue();
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
        //Todo send instructions
    }

    private void handleSPAnimation()
    {
        HBox temp = (HBox)sp.getContent();
        temp.getChildren().get(index).setScaleX(1.18);
        temp.getChildren().get(index).setScaleY(1.18);

        ImageView iv = (ImageView) temp.getChildren().get(index);
        Image im = iv.getImage();
        fileName.setText(im.impl_getUrl());

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
    
}
