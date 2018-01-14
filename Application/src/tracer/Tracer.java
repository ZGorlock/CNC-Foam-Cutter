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
import tracer.objects.base.simple.Vertex;

import javax.swing.*;
import java.awt.*;
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
    public static final int screenX = 200;
    public static final int screenY = 200;
    public static final int screenZ = 200;
    
    
    /**
     * The border from the edge of the Window.
     */
    public static final int screenBorder = 10;
    
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
    
//    /**
//     * The Frame of the Window.
//     */
//    public JFrame frame;
    
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
        for (int i = 0; i < 100000; i ++) {
            objects.add(new Vertex(Color.RED, new Vector(Math.random()  * (xMax * 2) - xMax, Math.random() * (yMax * 2) - yMax, Math.random() * (zMax * 2) - zMax)));
        }
    }
    
    
    /**
     * The Main method of of the program.
     */
    public static void setup(SwingNode node) {
//        frame = new JFrame();
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        Container pane = frame.getContentPane();
//        pane.setLayout(new BorderLayout());
//        frame.setFocusable(true);
//        frame.setFocusTraversalKeysEnabled(false);
        
        
        //add KeyListener for main controls
        setupMainKeyListener();
    
    
        //add cameras
        Camera camera = new Camera();
        Camera.setActiveCamera(0);
        
        
        //add objects
        objects.clear();
        createObjects();
        
        
        // panel to display render results
        renderPanel = new JPanel() {
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
        
        node.setContent(renderPanel);
//        pane.add(renderPanel, BorderLayout.CENTER);
//
//
//        frame.setSize(screenX, screenY);
//        frame.setVisible(true);
        
        
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
        //TODO make this work with the Pane
//        Tracer.frame.addKeyListener(new KeyListener()
//        {
//            private final Set<Integer> pressed = new HashSet<>();
//
//            @Override
//            public void keyTyped(KeyEvent e)
//            {
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e)
//            {
//                if (cameraId != activeCameraControl) {
//                    return;
//                }
//
//                pressed.add(e.getKeyCode());
//
//                double oldPhi = phi;
//                double oldTheta = theta;
//                double oldRho = rho;
//
//                for (Integer key : pressed) {
//                    if (key == KeyEvent.VK_W) {
//                        if (phi < (Math.PI - phiBoundary)) {
//                            if ((phi < (Math.PI / 2) - phiBoundary) && (phi + phiSpeed > (Math.PI / 2) - phiBoundary)) {
//                                phi = (Math.PI / 2) - phiBoundary;
//                            } else {
//                                phi += phiSpeed;
//                            }
//                        } else {
//                            phi = Math.PI - phiBoundary;
//                        }
//                    }
//                    if (key == KeyEvent.VK_S) {
//                        if (phi > phiBoundary) {
//                            if ((phi > (Math.PI / 2) - phiBoundary) && (phi - phiSpeed < (Math.PI / 2) - phiBoundary)) {
//                                phi = (Math.PI / 2) - phiBoundary;
//                            } else {
//                                phi -= phiSpeed;
//                            }
//                        } else {
//                            phi = phiBoundary;
//                        }
//                    }
//                    if (key == KeyEvent.VK_A) {
//                        theta += thetaSpeed;
//                        if (theta > 2 * Math.PI) {
//                            theta -= (2 * Math.PI);
//                        }
//                    }
//                    if (key == KeyEvent.VK_D) {
//                        theta -= thetaSpeed;
//                        if (theta < 0) {
//                            theta += (2 * Math.PI);
//                        }
//                    }
//                    if (key == KeyEvent.VK_Q) {
//                        rho -= zoomSpeed;
//                        if (rho < zoomSpeed) {
//                            rho = zoomSpeed;
//                        }
//                    }
//                    if (key == KeyEvent.VK_Z) {
//                        rho += zoomSpeed;
//                    }
//                }
//
//                if (phi != oldPhi || theta != oldTheta || rho != oldRho) {
//                    updateRequired = true;
//                }
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e)
//            {
//                pressed.remove(e.getKeyCode());
//            }
//
//        });
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
