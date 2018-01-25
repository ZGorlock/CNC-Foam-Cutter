package gui.Interfaces.MainMenu;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import tracer.Tracer;
import tracer.math.Delta;

import java.io.IOException;
import java.net.URL;

/**
 * The controller for the Trace tab.
 */
public class TraceController
{
    
    //FXML
    
    /**
     * The FXML elements of the tab.
     */
    public Label grblX;
    public Label grblY;
    public Label grblZ;
    public Label grblStatus;
    public SwingNode swingNodeTrace;
    
    
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
        updateCoordinates();
    }
    
    /**
     * Starts the coordinate monitoring thread.
     */
    private void updateCoordinates()
    {
        BackgroundProcessUI taskX = new BackgroundProcessUI(0);
        BackgroundProcessUI taskY = new BackgroundProcessUI(1);
        BackgroundProcessUI taskZ = new BackgroundProcessUI(2);
        BackgroundProcessUI taskStatus = new BackgroundProcessUI(3);

        grblStatus.textProperty().bind(taskStatus.messageProperty());
        grblX.textProperty().bind(taskX.messageProperty());
        grblY.textProperty().bind(taskY.messageProperty());
        grblZ.textProperty().bind(taskZ.messageProperty());

        new Thread(taskStatus).start();
        new Thread(taskX).start();
        new Thread(taskY).start();
        new Thread(taskZ).start();
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
