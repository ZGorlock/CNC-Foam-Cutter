/*
 * File:    TraceController.java
 * Package: gui.interfaces.main
 * Author:  Nicolas Lopez
 */

package gui.interfaces.main;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import tracer.Tracer;
import tracer.math.Delta;

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
    
    //FXML Fields
    
    /**
     * The Swing node that holds the Tracer.
     */
    public SwingNode swingNodeTrace;
    
    /**
     * The grbl  x position output text field.
     */
    public Label grblX;
    
    /**
     * The grbl  y position output text field.
     */
    public Label grblY;
    
    /**
     * The grbl  z position output text field.
     */
    public Label grblZ;
    
    /**
     * The grbl  status output text field.
     */
    public Label grblStatus;
    
    
    //Static Fields
    
    /**
     * The instance fo the controller.
     */
    public static TraceController controller;
    
    /**
     * The instance of the Tracer.
     */
    public static Tracer tracer;
    
    /**
     * The coordinate values of grbl , set by APIgrbl.
     */
    public static List<String> coordinateBlock = new ArrayList<>();
    
    
    //Fields
    
    /**
     * The timer for updating the coordinates.
     */
    public Timer coordinateTimer;
    
    
    //Constructors
    
    /**
     * Loads the Trace tab.
     *
     * @return The Trace tab, or null if there was an error.
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
            
        } catch (Exception e) {
            System.err.println("There was an error loading Trace.fxml!");
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    /**
     * Initializes the controller and sets up the Tracer.
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
    public void updateCoordinates()
    {
        TimerTask updateCoordinates = new TimerTask()
        {
            private int state;
            
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    if (coordinateBlock.size() == 4) {
                        grblX.textProperty().set(coordinateBlock.get(0));
                        grblY.textProperty().set(coordinateBlock.get(1));
                        grblZ.textProperty().set(coordinateBlock.get(2));
                        grblStatus.textProperty().set(coordinateBlock.get(3));
                    }
                });
            }
        };
        
        coordinateTimer = new Timer();
        coordinateTimer.scheduleAtFixedRate(updateCoordinates, 0, 100);
    }
    
    /**
     * Resets the controller.
     */
    public void reset()
    {
        if (coordinateTimer != null) {
            coordinateTimer.purge();
            coordinateTimer.cancel();
        }
    }
    
    
    //Functions
    
    /**
     * Adds a new trace point to the Tracer.
     *
     * @param x The relative x movement of the trace.
     * @param y The relative y movement of the trace.
     * @param z The relative z movement of the trace.
     */
    public static void addTrace(double x, double y, double z)
    {
        Tracer.addTrace(x, y, z);
    }
    
}
