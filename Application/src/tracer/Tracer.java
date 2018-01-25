/*
 * File:    Environment.java
 * Package: tracer.main
 * Author:  Zachary Gill
 */

package tracer;

import javafx.embed.swing.SwingNode;
import tracer.camera.Camera;
import tracer.math.matrix.Matrix3;
import tracer.math.vector.Vector;
import tracer.objects.base.BaseObject;
import tracer.objects.base.ObjectInterface;
import tracer.objects.base.simple.Edge;
import tracer.objects.base.simple.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Main Environment.
 */
public class Tracer
{
    
    //Constants
    
    /**
     * The number of frames to render per second.
     */
    public static final int FPS = 60;
    
    /**
     * The dimensions of the Window.
     */
    public static final int screenX = 1080;
    public static final int screenY = 640;
    public static final int screenZ = 480;
    
    /**
     * The border from the edge of the Window.
     */
    public static final int screenBorder = 0;
    
    /**
     * The min and max coordinate values to render.
     */
    public static double xMin = -100.0;
    public static double xMax = 100.0;
    public static double yMin = -100.0;
    public static double yMax = 100.0;
    public static double zMin = -100.0;
    public static double zMax = 100.0;
    
    /**
     * Color constants for the Tracer.
     */
    public static Color backgroundColor = new Color(230, 230, 230);
    
    /**
     * A flag indicating whether or not to display the Tracer demo.
     */
    public static boolean displayDemo = true;
    
    
    //Static Fields
    
    /**
     * The singleton instance of the Tracer.
     */
    private static Tracer instance;
    
    
    //Fields
    
    /**
     * The SwingNode containing the Tracer.
     */
    public SwingNode node;
    
    /**
     * The view of the window.
     */
    public JPanel renderPanel;
    
    /**
     * The transformation matrix for pitch and yaw.
     */
    public Matrix3 transform;
    
    /**
     * The list of Objects to be rendered in the Environment.
     */
    private List<ObjectInterface> objects = new ArrayList<>();
    
    /**
     * The coordinates to center the Environment at.
     */
    public Vector origin = new Vector(0, 0, 0);
    
    
    //Constructors
    
    /**
     * The private constructor for a Tracer.
     *
     * @param node The SwingNode containing the Tracer.
     */
    private Tracer(SwingNode node)
    {
        this.node = node;
    }
    
    
    // Static Methods
    
    /**
     * The setup method of the Tracer.
     *
     * @param node The SwingNode containing the Tracer.
     * @return The new Tracer instance or null.
     */
    public static Tracer setup(SwingNode node) {
        
        //initialize the Tracer
        if (instance != null) {
            return null;
        }
        instance = new Tracer(node);
        
    
        //add cameras
        Camera camera = new Camera();
        Camera.setActiveCamera(0);
        
        
        //add objects
        instance.objects.clear();
        instance.createObjects();
        
        
        //panel to display render results
        instance.renderPanel = new JPanel() {
            private AtomicBoolean running = new AtomicBoolean(false);
            
            public void paintComponent(Graphics g) {
                if (running.compareAndSet(false, true)) {
    
                    List<BaseObject> preparedBases = new ArrayList<>();
                    try {
                        for (ObjectInterface object : instance.objects) {
                            preparedBases.addAll(object.prepare());
                        }
                    } catch (ConcurrentModificationException ignored) {
                        running.set(false);
                        return;
                    }
                    
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(backgroundColor);
                    g2.fillRect(0, 0, screenX, screenY);
                    BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    
    
                    for (BaseObject preparedBase : preparedBases) {
                        preparedBase.render(g2);
                    }
    
    
                    g2.drawImage(img, 0, 0, null);
                    running.set(false);
                }
            }
        };
        node.setContent(instance.renderPanel);
        
        Timer renderTimer = new Timer();
        renderTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                instance.renderPanel.repaint();
            }
        }, 0, (int)(1000 / (double)FPS));
        
        return instance;
    }
    
    /**
     * Creates objects in the Environment.
     */
    private void createObjects()
    {
        if (!displayDemo) {
            return;
        }
        
        //axes
        objects.add(new Edge(Color.BLACK,
                new Vector(-2, 0, 0),
                new Vector(2, 0, 0)));
        objects.add(new Edge(Color.BLACK,
                new Vector(0, -2, 0),
                new Vector(0, 2, 0)));
        objects.add(new Edge(Color.BLACK,
                new Vector(0, 0, -2),
                new Vector(0, 0, 2)));
        
        //animation
        TimerTask traceTask = new TimerTask()
        {
            double phi = 0.0;
            double theta = 0.0;
            double rho = 2.0;
            
            @Override
            public void run()
            {
                phi += .01556;
                theta += .01256;
                
                double x = rho * Math.sin(phi) * Math.cos(theta);
                double y = rho * Math.cos(phi);
                double z = rho * Math.sin(phi) * Math.sin(theta);
                objects.add(new Vertex(Color.RED, new Vector(x, y, z)));
            }
        };
        Timer traceTimer = new Timer();
        traceTimer.scheduleAtFixedRate(traceTask, 0, 10);
    }
    
    /**
     * Handles movement of the camera.
     *
     * @param deltaX The movement in the x direction.
     * @param deltaY The movement in the y direction.
     */
    public void handleCameraControl(double deltaX, double deltaY)
    {
        Camera c = Camera.getActiveCameraView();
        c.handleMovement(deltaX, deltaY);
    }
    
    /**
     * Handles zooming of the camera.
     *
     * @param deltaZ The zoom amount.
     */
    public static void handleCameraZoom(double deltaZ)
    {
        Camera c = Camera.getActiveCameraView();
        c.handleZoom(deltaZ);
    }
    
    
    //Getter
    
    /**
     * Returns the origin for the Tracer instance.
     *
     * @return The origin for the Tracer instance.
     */
    public static Vector getOrigin()
    {
        return instance.origin;
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
        instance.objects.add(new Vertex(Color.RED, new Vector(x, y, z)));
    }
    
    /**
     * Adds an Object to the Environment at runtime.
     *
     * @param object The Object to add to the Environment.
     */
    public static void addObject(ObjectInterface object)
    {
        instance.objects.add(object);
    }
    
    /**
     * Removes an Object from the Environment at runtime.
     *
     * @param object The Object to remove from the Environment.
     */
    public static void removeObject(ObjectInterface object)
    {
        instance.objects.remove(object);
    }
    
}
