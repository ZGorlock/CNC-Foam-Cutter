/*
 * File:    Environment.java
 * Package: tracer.main
 * Author:  Zachary Gill
 */

package tracer.main;

import tracer.camera.Camera;
import tracer.math.matrix.Matrix3;
import tracer.math.vector.Vector;
import tracer.objects.base.AbstractObject;
import tracer.objects.base.BaseObject;
import tracer.objects.base.ObjectInterface;
import tracer.objects.polyhedron.regular.platonic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The tracer.main Environment.
 */
public class Environment
{
    
    //Constants
    
    /**
     * The number of frames to render per second.
     */
    public static final int FPS = 60;
    
    /**
     * The dimensions of the Window.
     */
    public static final int screenX = 720;
    public static final int screenY = 720;
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
     * Creates tracer.objects in the Environment.
     */
    private static void createObjects()
    {
        //Cool Screensaver
    
        Octahedron diamond = new Octahedron(Environment.origin, new Color(255, 0, 0, 64), 1);
        diamond.addFrame(Color.BLACK);
        objects.add(diamond);

        Tetrahedron pyramid = new Tetrahedron(Environment.origin, new Color(255, 165, 0, 64), 1);
        pyramid.addFrame(Color.BLACK);
        objects.add(pyramid);

        Hexahedron x1 = new Hexahedron(Environment.origin, new Color(0, 255, 0, 64), 1);
        x1.addFrame(Color.BLACK);
        objects.add(x1);

        Dodecahedron d12 = new Dodecahedron(Environment.origin, new Color(0, 0, 255, 64), 1);
        d12.addFrame(Color.BLACK);
        objects.add(d12);

        Icosahedron d20 = new Icosahedron(Environment.origin, new Color(165, 0, 165, 64), 1);
        d20.addFrame(Color.BLACK);
        objects.add(d20);
        
        
        //Example 3
        AtomicBoolean cameraInMotion = new AtomicBoolean(false);
        final AtomicInteger[] loop = {new AtomicInteger(1)};
        for (int i = 0; i < 100; i++) {
            Color c =  new Color(0, 255, 0); //ColorUtility.getRandomColor()
            Hexahedron cube = new Hexahedron(Environment.origin, c, .1);
            tracer.objects.base.Frame frame = new tracer.objects.base.Frame(cube);
            Timer waiter = new Timer();
            waiter.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    cube.addRotationAnimation((Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI);
                    cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);
                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                    cube.setFrameColor(new Color(0, 0, 0, 0));
                    cube.addProcess(new Runnable() {
                        private int count = 0;

                        @Override
                        public void run()
                        {
                            count++;

                            if (count == 9) {
                                if (cameraInMotion.compareAndSet(false, true)) {
                                    if ((loop[0].compareAndSet(0, 1)) || (loop[0].compareAndSet(1, 2))) {
                                        Camera.getActiveCameraView().addFluidTransition(Math.PI / 4, Math.PI / 2, 0, 10000);
                                    } else if ((loop[0].compareAndSet(2, 3)) || (loop[0].compareAndSet(3, 0))) {
                                        Camera.getActiveCameraView().addFluidTransition(-Math.PI / 4, Math.PI / 2, 0, 10000);
                                    }
                                }
                            } else if (count == 20) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                double[] values = cube.movementAnimations.get(0);
                                cube.movementAnimations.remove(0);

                                cube.addMovementAnimation(values[0] * -20, values[1] * -20, values[2] * -20);

                            } else if (count == 21) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                cube.movementAnimations.remove(0);

                                cube.reposition(Environment.origin);
                                cube.registerFrame(frame);
                                if (cube.getDisplayMode() == AbstractObject.DisplayMode.EDGE) {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.FACE);
                                    cube.setFrameColor(Color.BLACK);
                                    cube.registerFrame(frame);
                                } else {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                                    cube.setFrameColor(new Color(0, 0, 0, 0));
                                }
                                cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);

                                cameraInMotion.set(false);
                                count = 0;
                            }
                        }
                    }, 1000);
                }
            }, 0);
            cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
            objects.add(cube);
        }

        for (int i = 0; i < 100; i++) {
            Color c = new Color(255, 165, 0); //ColorUtility.getRandomColor()
            Tetrahedron cube = new Tetrahedron(Environment.origin, c, .1);
            tracer.objects.base.Frame frame = new tracer.objects.base.Frame(cube);
            Timer waiter = new Timer();
            waiter.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    cube.addRotationAnimation((Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI);
                    cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);
                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                    cube.setFrameColor(new Color(0, 0, 0, 0));
                    cube.addProcess(new Runnable() {
                        private int count = 0;
                        private boolean loop = false;

                        @Override
                        public void run()
                        {
                            count++;

                            if (count == 20) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                double[] values = cube.movementAnimations.get(0);
                                cube.movementAnimations.remove(0);

                                cube.addMovementAnimation(values[0] * -20, values[1] * -20, values[2] * -20);

                            } else if (count == 21) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                cube.movementAnimations.remove(0);

                                cube.reposition(Environment.origin);
                                cube.registerFrame(frame);
                                if (cube.getDisplayMode() == AbstractObject.DisplayMode.EDGE) {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.FACE);
                                    cube.setFrameColor(Color.BLACK);
                                    cube.registerFrame(frame);
                                } else {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                                    cube.setFrameColor(new Color(0, 0, 0, 0));
                                }
                                cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);

                                count = 0;
                            }
                        }
                    }, 1000);
                }
            }, 0);
            cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
            objects.add(cube);
        }

        for (int i = 0; i < 100; i++) {
            Color c = new Color(255, 0, 0); //ColorUtility.getRandomColor()
            Octahedron cube = new Octahedron(Environment.origin, c, .1);
            tracer.objects.base.Frame frame = new tracer.objects.base.Frame(cube);
            Timer waiter = new Timer();
            waiter.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    cube.addRotationAnimation((Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI);
                    cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);
                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                    cube.setFrameColor(new Color(0, 0, 0, 0));
                    cube.addProcess(new Runnable() {
                        private int count = 0;
                        private boolean loop = false;

                        @Override
                        public void run()
                        {
                            count++;

                            if (count == 20) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                double[] values = cube.movementAnimations.get(0);
                                cube.movementAnimations.remove(0);

                                cube.addMovementAnimation(values[0] * -20, values[1] * -20, values[2] * -20);

                            } else if (count == 21) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                cube.movementAnimations.remove(0);

                                cube.reposition(Environment.origin);
                                cube.registerFrame(frame);
                                if (cube.getDisplayMode() == AbstractObject.DisplayMode.EDGE) {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.FACE);
                                    cube.setFrameColor(Color.BLACK);
                                    cube.registerFrame(frame);
                                } else {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                                    cube.setFrameColor(new Color(0, 0, 0, 0));
                                }
                                cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);

                                count = 0;
                            }
                        }
                    }, 1000);
                }
            }, 0);
            cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
            objects.add(cube);
        }

        for (int i = 0; i < 100; i++) {
            Color c = new Color(0, 0, 255); //ColorUtility.getRandomColor()
            Dodecahedron cube = new Dodecahedron(Environment.origin, c, .1);
            tracer.objects.base.Frame frame = new tracer.objects.base.Frame(cube);
            Timer waiter = new Timer();
            waiter.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    cube.addRotationAnimation((Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI);
                    cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);
                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                    cube.setFrameColor(new Color(0, 0, 0, 0));
                    cube.addProcess(new Runnable() {
                        private int count = 0;
                        private boolean loop = false;

                        @Override
                        public void run()
                        {
                            count++;

                            if (count == 20) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                double[] values = cube.movementAnimations.get(0);
                                cube.movementAnimations.remove(0);

                                cube.addMovementAnimation(values[0] * -20, values[1] * -20, values[2] * -20);

                            } else if (count == 21) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                cube.movementAnimations.remove(0);

                                cube.reposition(Environment.origin);
                                cube.registerFrame(frame);
                                if (cube.getDisplayMode() == AbstractObject.DisplayMode.EDGE) {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.FACE);
                                    cube.setFrameColor(Color.BLACK);
                                    cube.registerFrame(frame);
                                } else {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                                    cube.setFrameColor(new Color(0, 0, 0, 0));
                                }
                                cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);

                                count = 0;
                            }
                        }
                    }, 1000);
                }
            }, 0);
            cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
            objects.add(cube);
        }

        for (int i = 0; i < 100; i++) {
            Color c = new Color(165, 0, 165); //ColorUtility.getRandomColor()
            Icosahedron cube = new Icosahedron(Environment.origin, c, .1);
            tracer.objects.base.Frame frame = new tracer.objects.base.Frame(cube);
            Timer waiter = new Timer();
            waiter.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    cube.addRotationAnimation((Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI, (Math.random() * 2 * Math.PI) - Math.PI);
                    cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);
                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                    cube.setFrameColor(new Color(0, 0, 0, 0));
                    cube.addProcess(new Runnable() {
                        private int count = 0;
                        private boolean loop = false;

                        @Override
                        public void run()
                        {
                            count++;

                            if (count == 20) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                double[] values = cube.movementAnimations.get(0);
                                cube.movementAnimations.remove(0);

                                cube.addMovementAnimation(values[0] * -20, values[1] * -20, values[2] * -20);

                            } else if (count == 21) {
                                cube.animationTimers.get(1).purge();
                                cube.animationTimers.get(1).cancel();
                                cube.animationTimers.remove(1);

                                cube.movementAnimations.remove(0);

                                cube.reposition(Environment.origin);
                                cube.registerFrame(frame);
                                if (cube.getDisplayMode() == AbstractObject.DisplayMode.EDGE) {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.FACE);
                                    cube.setFrameColor(Color.BLACK);
                                    cube.registerFrame(frame);
                                } else {
                                    cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
                                    cube.setFrameColor(new Color(0, 0, 0, 0));
                                }
                                cube.addMovementAnimation(Math.random() - .5, Math.random() - .5, Math.random() - .5);

                                count = 0;
                            }
                        }
                    }, 1000);
                }
            }, 0);
            cube.setDisplayMode(AbstractObject.DisplayMode.EDGE);
            objects.add(cube);
        }
    }
    
    
    /**
     * The tracer.main method of of the program.
     *
     * @param args Arguments to the tracer.main method.
     */
    public static void main(String[] args) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
        
        
        //add KeyListener for tracer.main controls
        setupMainKeyListener();
    
    
        //add cameras
        Camera camera = new Camera();
        Camera camera2 = new Camera();
        camera2.setLocation(Math.PI * 3 / 4, Math.PI * 3 / 4, 20);
        Camera.setActiveCamera(0);
        
        
        //add tracer.objects
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
     * Adds the KeyListener for the Camera tracer.main environment controls.
     */
    private static void setupMainKeyListener()
    {
        Environment.frame.addKeyListener(new KeyListener()
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
