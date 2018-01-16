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
    
    
    //Static Fields
    
    /**
     * The SwingNode containing the Tracer.
     */
    public static SwingNode node;
    
    /**
     * The view of the window.
     */
    public static JPanel renderPanel;
    
    /**
     * The transformation matrix for pitch and yaw.
     */
    public static Matrix3 transform;
    
    /**
     * The list of Objects to be rendered in the Environment.
     */
    private static List<ObjectInterface> objects = new ArrayList<>();
    
    /**
     * The coordinates to center the Environment at.
     */
    public static Vector origin = new Vector(0, 0, 0);
    
    
    // Static Methods
    
    /**
     * Creates objects in the Environment.
     */
    private static void createObjects()
    {
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
     * The setup method of the Tracer
     */
    public static void setup(SwingNode node) {
        
        //initialize the Tracer
        if (Tracer.node != null) {
            return;
        }
        Tracer.node = node;
        
    
        //add cameras
        Camera camera = new Camera();
        Camera.setActiveCamera(0);
        
        
        //add objects
        objects.clear();
        createObjects();
        
        
        //panel to display render results
        renderPanel = new JPanel() {
            private AtomicBoolean running = new AtomicBoolean(false);
            
            public void paintComponent(Graphics g) {
                if (running.compareAndSet(false, true)) {
    
                    List<BaseObject> preparedBases = new ArrayList<>();
                    try {
                        for (ObjectInterface object : objects) {
                            preparedBases.addAll(object.prepare());
                        }
                    } catch (ConcurrentModificationException ignored) {
                        running.set(false);
                        return;
                    }
                    
                    preparedBases.sort((o1, o2) -> {
                        double d1 = o1.calculatePreparedDistance();
                        double d2 = o2.calculatePreparedDistance();
                        return Double.compare(d2, d1);
                    });
                    
                    
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(230, 230, 230));
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
        node.setContent(renderPanel);
        
        Timer renderTimer = new Timer();
        renderTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                renderPanel.repaint();
            }
        }, 0, (int)(1000 / (double)FPS));
    }
    
    /**
     * Handles movement of the camera.
     *
     * @param deltaX The movement in the x direction.
     * @param deltaY The movement in the y direction.
     */
    public static void handleCameraControl(double deltaX, double deltaY)
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
    
    
    //Functions
    
    /**
     * Adds a new trace point to the tracer.
     *
     * @param x
     * @param y
     * @param z
     */
    public static void addTrace(double x, double y, double z)
    {
        objects.add(new Vertex(Color.RED, new Vector(x, y, z)));
    }
    
    /**
     * Adds an Object to the Environment at runtime.
     *
     * @param object The Object to add to the Environment.
     */
    public static void addObject(ObjectInterface object)
    {
        objects.add(object);
    }
    
    /**
     * Removes an Object from the Environment at runtime.
     *
     * @param object The Object to remove from the Environment.
     */
    public static void removeObject(ObjectInterface object)
    {
        objects.remove(object);
    }
    
}
