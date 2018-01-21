package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import javafx.concurrent.Task;
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

public class TraceController {

    //FXML
    public Label grblX;
    public Label grblY;
    public Label grblZ;
    public Label grblStatus;
    public SwingNode swingNodeTrace;
    
    public static TraceController controller;
    
    
    public void initialize()
    {
        controller = this;
        Tracer.setup(swingNodeTrace);
        updateCoordinates();
    }

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
    
    public static Tab setup()
    {
        try {
            Tab tabThird = (FXMLLoader.load(TraceController.class.getResource("Trace.fxml")));
        
            BorderPane pane = (BorderPane) tabThird.getContent();
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
                Tracer.handleCameraControl(mouseEvent.getSceneX() - delta.x, mouseEvent.getSceneY() - delta.y);
                delta.x = mouseEvent.getSceneX();
                delta.y = mouseEvent.getSceneY();
            });
            node.setOnMouseEntered(mouseEvent -> node.setCursor(Cursor.HAND));
            node.setOnScroll(event -> Tracer.handleCameraZoom(event.getDeltaY()));
            
            return tabThird;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void addTrace(double x, double y, double z)
    {
        Tracer.addTrace(x, y, z);
    }
}
