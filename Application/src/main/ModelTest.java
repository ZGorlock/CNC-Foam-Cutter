package main;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class ModelTest extends Application {
    
    private static final String MODEL = "resources/cadfiles/CylinderHead-binary.stl";
    private static final double MODEL_SCALE = 1;
    
    private static final int VIEWPORT_SIZE = 800;
    
    private static final Color lightColor = Color.WHITE;
    private static final Color modelColor = Color.rgb(48,  48, 48);
    private static final Color fillColor = Color.rgb(192, 192, 192);
    private static final Color ambientColor = Color.rgb(28, 28, 28);
    
    private Group root;
    
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    
    
    
    
    
    @Override
    public void start(Stage primaryStage) {
    }
    
    public static void main(String[] args) {
        launch(args); //TODO move to JPanel
    }
}