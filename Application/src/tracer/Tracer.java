/*
 * File:    Tracer.java
 * Package: tracer
 * Author:  Zachary Gill
 */

package tracer;

import javafx.embed.swing.SwingNode;
import main.Main;
import renderer.Renderer;
import tracer.camera.Camera;
import tracer.math.matrix.Matrix3;
import tracer.math.vector.Vector;
import tracer.objects.base.AbstractObject;
import tracer.objects.base.BaseObject;
import tracer.objects.base.ObjectInterface;
import tracer.objects.base.polygon.Rectangle;
import tracer.objects.base.simple.Edge;

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
     * The x dimension of the Window.
     */
    public static final int screenX = 1080;
    
    /**
     * The y dimension of the Window.
     */
    public static final int screenY = 640;
    
    /**
     * The z dimension of the Window.
     */
    public static final int screenZ = 480;
    
    /**
     * The border from the edge of the Window.
     */
    public static final int screenBorder = 0;
    
    /**
     * Color constants for the Tracer.
     */
    public static final Color backgroundColor = new Color(230, 230, 230);
    
    /**
     * The maximum number of traces to display before old traces begin to disappear.
     */
    public static final int MAX_TRACES = 360;
    
    /**
     * Whether or not to display the trace demo.
     */
    public static final boolean traceDemo = false;
    
    
    //Static Fields
    
    /**
     * The singleton instance of the Tracer.
     */
    private static Tracer instance;
    
    /**
     * A flag indicating whether the Tracer is currently painting or not.
     */
    private static final AtomicBoolean running = new AtomicBoolean(false);
    
    /**
     * The last trace that was hit.
     */
    private static Vector lastTrace;
    
    /**
     * The list of traces currently being rendered.
     */
    private static List<Edge> traces = new ArrayList<>();
    
    /**
     * The time for rendering.
     */
    private static Timer renderTimer;
    
    /**
     * The timer for the trace demo.
     */
    private static Timer traceTimer;
    
    
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
    public static Tracer setup(SwingNode node)
    {
        //initialize the Tracer
        if (instance != null) {
            return null;
        }
        instance = new Tracer(node);
        
        //add cameras
        Camera camera = new Camera();
        camera.setLocation(Math.PI / 2, Math.PI, ((Math.max(Renderer.foamWidth, Renderer.foamWidth) + Renderer.foamHeight) * 2) * Renderer.MILLIMETERS_IN_INCH);
        Camera.setActiveCamera(0);
        
        //add objects
        instance.objects.clear();
        instance.createObjects();
        
        //panel to display render results
        instance.renderPanel = new JPanel()
        {
            public void paintComponent(Graphics g)
            {
                if (instance != null && running.compareAndSet(false, true)) {
                    
                    List<BaseObject> preparedBases = new ArrayList<>();
                    try {
                        if (instance == null) {
                            return;
                        }
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
        
        renderTimer = new Timer();
        renderTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                instance.renderPanel.repaint();
            }
        }, 0, (int) (1000 / (double) FPS));
        
        return instance;
    }
    
    /**
     * Creates objects in the Environment.
     */
    private void createObjects()
    {
        double w = (Renderer.foamWidth * Renderer.MILLIMETERS_IN_INCH) / 2;
        double l = (Renderer.foamLength * Renderer.MILLIMETERS_IN_INCH) / 2;
        double h = (Renderer.foamHeight * Renderer.MILLIMETERS_IN_INCH) / 2;
        
        Vector c1 = new Vector(-w, -h, -l);
        Vector c2 = new Vector(-w, -h, l);
        Vector c3 = new Vector(w, -h, -l);
        Vector c4 = new Vector(w, -h, l);
        Vector c5 = new Vector(-w, h, -l);
        Vector c6 = new Vector(-w, h, l);
        Vector c7 = new Vector(w, h, -l);
        Vector c8 = new Vector(w, h, l);
        
        Rectangle r1 = new Rectangle(Color.BLACK, c1, c5, c6, c2);
        Rectangle r2 = new Rectangle(Color.BLACK, c1, c5, c7, c3);
        Rectangle r3 = new Rectangle(Color.BLACK, c2, c6, c8, c4);
        Rectangle r4 = new Rectangle(Color.BLACK, c1, c2, c4, c3);
        Rectangle r5 = new Rectangle(Color.BLACK, c5, c6, c8, c7);
        Rectangle r6 = new Rectangle(Color.BLACK, c3, c4, c8, c7);
        
        r1.setDisplayMode(AbstractObject.DisplayMode.EDGE);
        r2.setDisplayMode(AbstractObject.DisplayMode.EDGE);
        r3.setDisplayMode(AbstractObject.DisplayMode.EDGE);
        r4.setDisplayMode(AbstractObject.DisplayMode.EDGE);
        r5.setDisplayMode(AbstractObject.DisplayMode.EDGE);
        r6.setDisplayMode(AbstractObject.DisplayMode.EDGE);
        
        objects.add(r1);
        objects.add(r2);
        objects.add(r3);
        objects.add(r4);
        objects.add(r5);
        objects.add(r6);
        
        if (traceDemo) {
            //animation
            TimerTask traceTask = new TimerTask()
            {
                double phi = 0.0;
                double theta = 0.0;
                double rho = ((Math.max(Renderer.foamWidth, Renderer.foamWidth) + Renderer.foamHeight) / 4) * Renderer.MILLIMETERS_IN_INCH;
                
                boolean startTrace = false;
                double lastX, lastY, lastZ;
                
                @Override
                public void run()
                {
                    phi += .02556;
                    theta += .02256;
                    
                    double x = rho * Math.sin(phi) * Math.cos(theta);
                    double y = rho * Math.cos(phi);
                    double z = rho * Math.sin(phi) * Math.sin(theta);
                    
                    if (!startTrace) {
                        setStartTrace(x, y, z);
                        startTrace = true;
                    } else {
                        addTrace(x - lastX, y - lastY, z - lastZ);
                    }
                    
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                }
            };
            traceTimer = new Timer();
            traceTimer.scheduleAtFixedRate(traceTask, 0, 20);
        }
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
    
    
    //Static Methods
    
    /**
     * Adds a new trace point to the Tracer.
     *
     * @param x The relative x movement of the trace.
     * @param y The relative y movement of the trace.
     * @param z The relative z movement of the trace.
     */
    public static synchronized void addTrace(double x, double y, double z)
    {
        if (lastTrace != null) {
            Vector trace = new Vector(x + lastTrace.getX(), y + lastTrace.getY(), z + lastTrace.getZ());
            Edge edge = new Edge(Color.RED, lastTrace, trace);
            
            traces.add(0, edge);
            addObject(edge);
            lastTrace = trace;
            
            if (traces.size() > MAX_TRACES) {
                removeObject(traces.get(MAX_TRACES - 1));
                traces.remove(MAX_TRACES - 1);
            }
            
            for (int i = 0; i < traces.size(); i++) {
                traces.get(i).setColor(new Color(1, 0, 0, 1 - (i / (float) MAX_TRACES)));
            }
        }
    }
    
    /**
     * Sets the starting trace point of the Tracer.
     *
     * @param x The starting x coordinate.
     * @param y The starting y coordinate.
     * @param z The starting z coordinate.
     */
    public static void setStartTrace(double x, double y, double z)
    {
        lastTrace = new Vector(x, y + (Renderer.foamHeight / 2 * Renderer.MILLIMETERS_IN_INCH), z);
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
    
    /**
     * Resets the Tracer.
     */
    public static void reset()
    {
        if (renderTimer != null) {
            renderTimer.purge();
            renderTimer.cancel();
        }
        if (traceTimer != null) {
            traceTimer.purge();
            traceTimer.cancel();
        }
        
        while (running.get()) {}
        instance = null;
    }
    
}
