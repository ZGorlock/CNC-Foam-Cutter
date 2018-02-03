package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import tracer.Tracer;
import tracer.math.Delta;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The controller for the Trace tab.
 */
public class TraceController
{
    
    //FXML
    
    /**
     * The FXML elements of the tab.
     */
    public HBox hx;
    public HBox hy;
    public HBox hz;
    public HBox hStatus;
    public SwingNode swingNodeTrace;

    // UI-dependent variables
    public Label grblX;
    public Label grblY;
    public Label grblZ;
    public Label grblStatus;
    public static List<String> coordinateBlock = new ArrayList<>();


    //Static Fields
    
    /**
     * The singleton instance fo the TraceController.
     */
    public static TraceController controller;
    
    /**
     * The singleton instance of the Tracer.
     */
    public static Tracer tracer;
    
    
    //Static Methods
    
    /**
     * Creates the Trace tab.
     *
     * @return The Trace tab.
     */
    public static Tab setup()
    {
        try {
            URL fxml = TraceController.class.getResource("Trace.fxml");
            Tab tab = FXMLLoader.load(fxml);
            
            BorderPane pane = (BorderPane) tab.getContent();
            AnchorPane anchor = (AnchorPane) pane.getChildren().get(0);
            BorderPane anchorPane = (BorderPane) anchor.getChildren().get(0);
            SwingNode node = (SwingNode) anchorPane.getChildren().get(0);
            
            final Delta delta = new Delta();
            node.setOnMousePressed(mouseEvent -> {
                delta.x = mouseEvent.getSceneX();
                delta.y = mouseEvent.getSceneY();
                node.setCursor(Cursor.MOVE);
            });
            node.setOnMouseReleased(mouseEvent -> node.setCursor(Cursor.HAND));
            node.setOnMouseDragged(mouseEvent -> {
                tracer.handleCameraControl(mouseEvent.getSceneX() - delta.x, mouseEvent.getSceneY() - delta.y);
                delta.x = mouseEvent.getSceneX();
                delta.y = mouseEvent.getSceneY();
            });
            node.setOnMouseEntered(mouseEvent -> node.setCursor(Cursor.HAND));
            node.setOnScroll(event -> Tracer.handleCameraZoom(event.getDeltaY()));
            
            return tab;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    /**
     * Initializes the TraceController and sets up the Tracer.
     */
    public void initialize()
    {
        controller = this;
        tracer = Tracer.setup(swingNodeTrace);

        grblX = new Label();
        grblY = new Label();
        grblZ = new Label();
        grblStatus = new Label();

        hx.getChildren().add(grblX);
        hy.getChildren().add(grblY);
        hz.getChildren().add(grblZ);
        hStatus.getChildren().add(grblStatus);

        updateCoordinates();
    }
    
    /**
     * Starts the coordinate monitoring thread.
     */
    public void updateCoordinates()
    {
        TimerTask updateCoordinates = new TimerTask()
        {
            private int state;
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    if (coordinateBlock.size() > 0) {
                        grblX.textProperty().set(coordinateBlock.get(0));
                        grblY.textProperty().set(coordinateBlock.get(1));
                        grblZ.textProperty().set(coordinateBlock.get(2));
                        grblStatus.textProperty().set(coordinateBlock.get(3));
                    }
                });
            }
        };

        Timer t = new Timer();
        t.scheduleAtFixedRate(updateCoordinates,0, 100);
    }
    
    //Functions
    
    /**
     * Adds a new trace point to the Tracer.
     *
     * @param x The x coordinate of the trace.
     * @param y The y coordinate of the trace.
     * @param z The z coordinate of the trace.
     */
    public static void addTrace(double x, double y, double z)
    {
        Tracer.addTrace(x, y, z);
    }
}
