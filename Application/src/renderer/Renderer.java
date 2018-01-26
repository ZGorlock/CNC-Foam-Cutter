/*
 * File:    Renderer.java
 * Package: renderer
 * Author:  Zachary Gill
 */

package renderer;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import gui.Interfaces.Greeting.GreetingController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;

/**
 * Renders the uploaded STL file.
 */
public class Renderer
{
    
    //Constants
    
    /**
     * The factor to scale the model by.
     */
    private static final double MODEL_SCALE = 1;
    
    /**
     * The size of the view.
     */
    private static final int VIEWPORT_SIZE = 680;
    
    /**
     * Color constants for rendering the model.
     */
    private static final Color lightColor = Color.WHITE;
    private static final Color ambientColor = Color.rgb(28, 28, 28);
    private static final Color modelColor = Color.rgb(48,  48, 48);
    private static final Color fillColor = Color.rgb(192, 192, 192);
    
    
    //Static Fields
    
    /**
     * The singleton instance of the Renderer.
     */
    private static Renderer instance;
    
    
    //Fields
    
    /**
     * The SwingNode containing the Renderer.
     */
    public SwingNode node;
    
    /**
     * The view of the model.
     */
    private Group root;
    
    /**
     * The scene containing the view of the model.
     */
    private Scene scene;
    
    /**
     * The STL model file to render.
     */
    private String model;
    
    /**
     * The rotation values for the Camera.
     */
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    
    
    //Constructors
    
    /**
     * The private constructor for a Renderer.
     *
     * @param node The SwingNode containing the Renderer.
     */
    private Renderer(SwingNode node)
    {
        this.node = node;
    }
    
    
    //Static Methods
    
    /**
     * The setup method of the Renderer.
     *
     * @param node The SwingNode containing the Renderer.
     * @return The new Renderer instance or null.
     */
    public static Renderer setup(SwingNode node) {
        
        //initialize the Renderer
        if (instance != null) {
            return null;
        }
        instance = new Renderer(node);
    
        
        //render the model
        instance.model = GreetingController.getModel();

        if (!instance.model.isEmpty()) {
            Group group = instance.renderModel();

            instance.scene = new Scene(group, VIEWPORT_SIZE, VIEWPORT_SIZE, true);
            instance.scene.setFill(fillColor);
            instance.addCamera();
    
            JPanel panel = new JPanel();
            JFXPanel jfxPanel = new JFXPanel();
            panel.add(jfxPanel);
            panel.setVisible(true);
            jfxPanel.setScene(instance.scene);
            node.setContent(panel);
            
            return instance;
        }
        
        return null;
    }
    
    
    //Methods
    
    /**
     * Renders the STL model file.
     *
     * @return The Group containing the elements of the STL model.
     */
    private Group renderModel() {
        MeshView[] meshViews = loadModel(model);
        
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
        addLightSources();
        
        return root;
    }
    
    /**
     * Loads the STL model file into memory.
     *
     * @param model The STL model file.
     * @return A list of MeshViews describing the model.
     */
    private MeshView[] loadModel(String model) {
        File file = new File(model);
        
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(file);
        Mesh mesh = importer.getImport();
        
        return new MeshView[] { new MeshView(mesh) };
    }
    
    /**
     * Adds light sources to the model view.
     */
    private void addLightSources()
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
    
    /**
     * Sets up the Camera for the Scene.
     */
    private void addCamera() {
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
    
}
