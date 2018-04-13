/*
 * File:    Renderer.java
 * Package: renderer
 * Author:  Zachary Gill
 */

package renderer;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import gui.interfaces.greeting.GreetingController;
import gui.interfaces.popup.SystemNotificationController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.scene.*;
import javafx.scene.effect.Light;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import tracer.camera.Camera;
import utils.MachineDetector;

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
     * The x rotation to apply to the model.
     */
    private static final Transform MODEL_ROTATE_X = new Rotate(-270, Rotate.X_AXIS);
    
    /**
     * The y rotation to apply to the model.
     */
    private static final Transform MODEL_ROTATE_Y = new Rotate(180, Rotate.Y_AXIS);
    
    /**
     * The z rotation to apply to the model.
     */
    private static final Transform MODEL_ROTATE_Z = new Rotate(-90, Rotate.Z_AXIS);
    
    /**
     * The z rotation to apply to the model for the constant rotation.
     */
    private static final Transform MODEL_ROTATE_CONSTANT_Z = new Rotate(0, Rotate.Z_AXIS);
    
    /**
     * The size of the view.
     */
    private static final int VIEWPORT_SIZE = 680;
    
    /**
     * The color constant for light.
     */
    private static final Color lightColor = Color.WHITE;
    
    /**
     * The color constant for ambient light.
     */
    private static final Color ambientColor = Color.rgb(28, 28, 28);
    
    /**
     * The color constant for the model.
     */
    private static final Color modelColor = Color.rgb(48, 48, 48);
    
    /**
     * The color constant for the foam border.
     */
    private static final Color borderColor = Color.rgb(128, 128, 128, .2);
    
    /**
     * The color constant for the background fill.
     */
    private static final Color fillColor = Color.rgb(192, 192, 192);
    
    /**
     * The number of millimeters in an inch.
     */
    public static final double MILLIMETERS_IN_INCH = 25.4;
    
    
    //Static Fields
    
    /**
     * The singleton instance of the Renderer.
     */
    private static Renderer instance;
    
    /**
     * The camera for the Renderer.
     */
    private static PerspectiveCamera perspectiveCamera;
    
    /**
     * The model rotation animation timeline.
     */
    private static Timeline timeline;
    
    /**
     * The width of the block of foam (in inches).
     */
    public static double foamWidth;
    
    /**
     * The length of the block of foam (in inches).
     */
    public static double foamLength;
    
    /**
     * The height of the block of foam (in inches).
     */
    public static double foamHeight;
    
    /**
     * The center point of the foam block.
     */
    public static Light.Point foamCenter = new Light.Point(0.0, 0.0, 0.0, new Color(0, 0, 0, 1));
    
    /**
     * The width of the model (in millimeters).
     */
    public static double modelWidth;
    
    /**
     * The length of the model (in millimeters).
     */
    public static double modelLength;
    
    /**
     * The height of the model (in millimeters).
     */
    public static double modelHeight;
    
    
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
     * The group holding the objects in the scene.
     */
    private Group group;
    
    /**
     * The STL model file to render.
     */
    private String model;
    
    
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
    public static Renderer setup(SwingNode node)
    {
        //initialize the Renderer
        if (instance != null || !MachineDetector.isCncMachine()) {
            return null;
        }
        instance = new Renderer(node);
        
        //render the model
        instance.model = GreetingController.getModel();
        
        if (!instance.model.isEmpty()) {
            Group group = instance.renderModel();
            if (group == null) {
                return null;
            }
            
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
        } else {
            System.err.println("Cannot render model without a model file!");
        }
        
        return null;
    }
    
    
    //Methods
    
    /**
     * Renders the STL model file.
     *
     * @return The Group containing the elements of the STL model.
     */
    private Group renderModel()
    {
        MeshView meshView = loadModel(model);
    
        modelWidth = meshView.getBoundsInLocal().getWidth();
        modelLength = meshView.getBoundsInLocal().getHeight();
        modelHeight = meshView.getBoundsInLocal().getDepth();
        
        if (modelWidth > foamWidth * MILLIMETERS_IN_INCH ||
                modelLength > foamLength * MILLIMETERS_IN_INCH ||
                modelHeight > foamHeight * MILLIMETERS_IN_INCH) {
            System.err.println("The model does not fit within the foam block!");
            SystemNotificationController.throwNotification("Your model must fit within the foam block!", true, false);
            return null;
        }
    
        //TODO these adjustments need to be made to the gcode as well
        double translateX = 0;//-modelWidth / 2;
        double translateY = 0;//modelLength / 2;
        double translateZ = 0;//(foamHeight * MILLIMETERS_IN_INCH / 2) - ((foamHeight * MILLIMETERS_IN_INCH) - modelHeight);
        Translate translation = new Translate(translateX, translateY, translateZ);
        
        foamCenter = new Light.Point(foamWidth / 2, foamLength / 2, foamHeight / 2, new Color(0, 0, 0, 1));
        ((Rotate) MODEL_ROTATE_X).pivotXProperty().bind(foamCenter.xProperty());
        ((Rotate) MODEL_ROTATE_Y).pivotYProperty().bind(foamCenter.yProperty());
        ((Rotate) MODEL_ROTATE_Z).pivotZProperty().bind(foamCenter.zProperty());
        
        PhongMaterial sample = new PhongMaterial(modelColor);
        sample.setDiffuseColor(modelColor);
        sample.setSpecularColor(lightColor);
        sample.setSpecularPower(5);
        meshView.setMaterial(sample);
        
        meshView.setScaleX(MODEL_SCALE);
        meshView.setScaleY(MODEL_SCALE);
        meshView.setScaleZ(MODEL_SCALE);
        meshView.getTransforms().setAll(translation);
        
        group = new Group();
        group.getChildren().add(meshView);
        group.getTransforms().setAll(MODEL_ROTATE_X, MODEL_ROTATE_Y, MODEL_ROTATE_Z, MODEL_ROTATE_CONSTANT_Z);

        root = new Group(group);
        root.getChildren().add(new AmbientLight(ambientColor));
        addBorder();
        addLightSources();
        
        return root;
    }
    
    /**
     * Loads the STL model file into memory.
     *
     * @param model The STL model file.
     * @return The MeshViews describing the model.
     */
    private MeshView loadModel(String model)
    {
        File file = new File(model);
        if (!file.exists()) {
            return new MeshView();
        }
        
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(file);
        Mesh mesh = importer.getImport();
        
        return new MeshView(mesh);
    }
    
    /**
     * Adds light sources to the model view.
     */
    private void addLightSources()
    {
        for (double i = -1; i <= 1; i += 2) {
            for (double j = -1; j <= 1; j += 2) {
                for (double k = -1; k <= 1; k += 2) {
                    if (i == 0 && j == 0 && k == 0) {
                        continue;
                    }
                    PointLight pointLight = new PointLight(lightColor);
                    pointLight.setTranslateX(i * foamLength * MODEL_SCALE * MILLIMETERS_IN_INCH);
                    pointLight.setTranslateY(j * foamHeight * MODEL_SCALE * MILLIMETERS_IN_INCH - ((foamHeight / 2) * MODEL_SCALE * MILLIMETERS_IN_INCH));
                    pointLight.setTranslateZ(k * foamWidth * MODEL_SCALE * MILLIMETERS_IN_INCH);
                    root.getChildren().add(pointLight);
                }
            }
        }
    }
    
    /**
     * Sets up the Camera for the Scene.
     */
    private void addCamera()
    {
        double translateX = 0;
        double translateY = 0;
        double translateZ = ((Math.max(foamWidth, foamLength) + foamHeight) * 1.5) * MODEL_SCALE * MILLIMETERS_IN_INCH;
        
        perspectiveCamera = new PerspectiveCamera(true);
        perspectiveCamera.getTransforms().addAll(new Translate(translateX, translateY, translateZ), new Rotate(180, Rotate.Y_AXIS));
        
        perspectiveCamera.setFarClip(5000);
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFieldOfView(45.0);
        
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(((Rotate) MODEL_ROTATE_CONSTANT_Z).angleProperty(), 360)),
                new KeyFrame(Duration.seconds(15), new KeyValue(((Rotate) MODEL_ROTATE_CONSTANT_Z).angleProperty(), 0))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        scene.setCamera(perspectiveCamera);
    }
    
    /**
     * Adds a foam border to the Scene.
     */
    private void addBorder()
    {
        Box border = new Box(foamWidth, foamLength, foamHeight);
        PhongMaterial sample = new PhongMaterial(borderColor);
        sample.setSpecularColor(borderColor);
        sample.setSpecularPower(1000);
        sample.setDiffuseColor(borderColor);
        border.setMaterial(sample);
        border.setDrawMode(DrawMode.LINE);
        
        border.setScaleX(MODEL_SCALE * MILLIMETERS_IN_INCH);
        border.setScaleY(MODEL_SCALE * MILLIMETERS_IN_INCH);
        border.setScaleZ(MODEL_SCALE * MILLIMETERS_IN_INCH);
        
        group.getChildren().add(border);
    }
    
    
    //Static Methods
    
    /**
     * Pauses the model animation.
     */
    public static void pauseModelAnimation()
    {
        if (timeline != null) {
            timeline.pause();
        }
    }
    
    /**
     * Resumes the model animation.
     */
    public static void resumeModelAnimation()
    {
        if (timeline != null) {
            timeline.play();
        }
    }
    
    /**
     * Handles movement of the camera.
     *
     * @param deltaX The movement in the x direction.
     * @param deltaY The movement in the y direction.
     */
    public static void handleCameraMovement(double deltaX, double deltaY)
    {
        ((Rotate) MODEL_ROTATE_X).setAngle(((Rotate) MODEL_ROTATE_X).getAngle() + (-.2 * deltaY));
        ((Rotate) MODEL_ROTATE_Z).setAngle(((Rotate) MODEL_ROTATE_Z).getAngle() + (-.2 * deltaX));
    }
    
    /**
     * Resets the Renderer.
     */
    public static void reset()
    {
        Camera.reset();
        
        instance = null;
    }
    
}
