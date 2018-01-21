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
import javafx.scene.layout.HBox;
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
        /*
        runInTaskButton.setOnAction(event -> {
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    for (int i=1; i<=10; i++) {
                        Thread.sleep(250);
                    }
                    return null;
                }
            };
            task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
            new Thread(task).start();
        });
        */
        setGrblX(APIgrbl.grbl.getCoordinateX());
        setGrblY(APIgrbl.grbl.getCoordinateY());
        setGrblZ(APIgrbl.grbl.getCoordinateZ());
        setGrblStatus(APIgrbl.grbl.getStatus());
    }
    
    public static Tab setup()
    {   // Tab tabThird = (FXMLLoader.load(TraceController.class.getResource("Trace.fxml"))); <--- this was causing an exception
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

    public static void setGrblX(double x) {
        controller.grblX.setText(String.format("%.2f", x));
    }
    
    public static void setGrblY(double y) {
        controller.grblY.setText(String.format("%.2f", y));
    }
    
    public static void setGrblZ(double z) {
        controller.grblZ.setText(String.format("%.2f", z));
    }
    
    public static void setGrblStatus(String status) {
        controller.grblStatus.setText(status);
    }
    
    
}
