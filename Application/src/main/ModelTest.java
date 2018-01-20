package main;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

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
    
    
    private MeshView[] loadModel() {
        File file = new File(MODEL);
        
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(file);
        Mesh mesh = importer.getImport();
        
        return new MeshView[] { new MeshView(mesh) };
    }
    
    private Group renderModel() {
        MeshView[] meshViews = loadModel();
        
        for (MeshView meshView : meshViews) {
            PhongMaterial sample = new PhongMaterial(modelColor);
            sample.setDiffuseColor(modelColor);
            sample.setSpecularColor(lightColor);
            sample.setSpecularPower(8);
            meshView.setMaterial(sample);
            
            meshView.setScaleX(MODEL_SCALE);
            meshView.setScaleY(MODEL_SCALE);
            meshView.setScaleZ(MODEL_SCALE);
            meshView.getTransforms().setAll(new Rotate(0, Rotate.X_AXIS), new Rotate(0, Rotate.Y_AXIS), new Rotate(0, Rotate.Z_AXIS));
        }
    
        root = new Group(meshViews);
        root.getChildren().add(new AmbientLight(ambientColor));
        addLightSources(root);
        
        return root;
    }
    
    private void addLightSources(Group root)
    {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (i == 0 && j == 0 && k == 0) {
                        continue;
                    }
                    PointLight pointLight = new PointLight(lightColor);
                    pointLight.setTranslateX(1000 * i); //TODO set lights relative to max size of model
                    pointLight.setTranslateY(1000 * j);
                    pointLight.setTranslateZ(1000 * k);
                    root.getChildren().add(pointLight);
                }
            }
        }
    }
    
    private void addCamera(Scene scene) {
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(true);
        perspectiveCamera.getTransforms().addAll(rotateX, rotateY, rotateZ,
                new Translate(0, -100, -750)); //TODO get camera to center model
        
        perspectiveCamera.setFarClip(2000); //TODO
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFieldOfView(30.0);
    
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),  new KeyValue(rotateY.angleProperty(), 0)),
                new KeyFrame(Duration.seconds(15), new KeyValue(rotateY.angleProperty(), 360))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        scene.setCamera(perspectiveCamera);
    }
    
    @Override
    public void start(Stage primaryStage) {
        Group group = renderModel();
        
        Scene scene = new Scene(group, VIEWPORT_SIZE, VIEWPORT_SIZE, true);
        scene.setFill(fillColor);
        addCamera(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args); //TODO move to JPanel
    }
}