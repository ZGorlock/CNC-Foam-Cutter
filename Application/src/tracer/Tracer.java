/*
 * File:    Environment.java
 * Package: tracer.main
 * Author:  Zachary Gill
 */

package tracer;

import tracer.camera.Camera;
import tracer.math.matrix.Matrix3;
import tracer.math.vector.Vector;
import tracer.objects.base.AbstractObject;
import tracer.objects.base.BaseObject;
import tracer.objects.base.ObjectInterface;
import tracer.objects.base.simple.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    public static final int screenX = 2560;
    public static final int screenY = 1440;
    public static final int screenZ = 720;
    
    
    /**
     * The border from the edge of the Window.
     */
    public static final int screenBorder = 50;
    
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
     * The Frame of the Window.
     */
    public static JFrame frame;
    
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
    }
    
    
    /**
     * The Main method of of the program.
     *
     * @param args Arguments to the Main method.
     */
    public static void main(String[] args) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
        
        
        //add KeyListener for main controls
        setupMainKeyListener();
    
    
        //add cameras
        Camera camera = new Camera();
        Camera camera2 = new Camera();
        camera2.setLocation(Math.PI * 3 / 4, Math.PI * 3 / 4, 20);
        Camera.setActiveCamera(0);
        
        
        //add objects
        objects.clear();
        createObjects();
        
        
        // panel to display render results
        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                
                
                List<BaseObject> preparedBases = new ArrayList<>();
                for (ObjectInterface object : objects) {
                    preparedBases.addAll(object.prepare());
                }
    
                preparedBases.sort((o1, o2) -> {
                    double d1 = o1.calculatePreparedDistance();
                    double d2 = o2.calculatePreparedDistance();
                    return Double.compare(d2, d1);
                });
                
                for (BaseObject preparedBase : preparedBases) {
                    preparedBase.render(g2);
                }
                
                
                g2.drawImage(img, 0, 0, null);
            }
        };
        pane.add(renderPanel, BorderLayout.CENTER);
        
        
        frame.setSize(screenX, screenY);
        frame.setVisible(true);
        
        
        Timer renderTimer = new Timer();
        renderTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                renderPanel.repaint();
            }
        }, 0, 1000 / FPS);
    }
    
    /**
     * Adds the KeyListener for the Camera main environment controls.
     */
    private static void setupMainKeyListener()
    {
        Tracer.frame.addKeyListener(new KeyListener()
        {
            
            @Override
            public void keyTyped(KeyEvent e)
            {
            }
            
            @Override
            public void keyPressed(KeyEvent e)
            {
                int key = e.getKeyCode();
                
                if (key == KeyEvent.VK_DIVIDE) {
                    for (ObjectInterface object : objects) {
                        object.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                    }
                }
                if (key == KeyEvent.VK_MULTIPLY) {
                    for (ObjectInterface object : objects) {
                        object.setDisplayMode(AbstractObject.DisplayMode.VERTEX);
                    }
                }
                if (key == KeyEvent.VK_SUBTRACT) {
                    for (ObjectInterface object : objects) {
                        object.setDisplayMode(AbstractObject.DisplayMode.FACE);
                    }
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e)
            {
            }
            
        });
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
