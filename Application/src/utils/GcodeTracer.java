/*
 * File:    GcodeTracer.java
 * Package: utils
 * Author:  Zachary Gill
 */

package utils;

import gui.interfaces.main.ModelController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Traces a G-code file to make an image.
 */
public class GcodeTracer
{
    
    //Constants
    
    /**
     * The dimensions of the image border.
     */
    public static final int IMAGE_BORDER = 20;
    
    /**
     * The x dimension of the image.
     */
    public static final int IMAGE_SIZE_X = ModelController.maxXTravelHotwire + 2 * IMAGE_BORDER;
    
    /**
     * The y dimension of the image.
     */
    public static final int IMAGE_SIZE_Y = ModelController.maxYTravelHotwire + 2 * IMAGE_BORDER;
    
    /**
     * The scale to reduce the overall image by.
     */
    public static final int scaleFactor = 3;
    
    
    //Fields
    
    /**
     * The current x trace positions.
     */
    private double traceX = IMAGE_SIZE_X / 2;
    
    /**
     * The current y trace positions.
     */
    private double traceY = IMAGE_BORDER;
    
    
    //Methods
    
    /**
     * Traces a list of gcode files and produces a list of BufferedImages.
     *
     * @param gcodeFiles The list of gcode files to trace.
     * @return The list of BufferedImages produced from the gcode files.
     */
    public synchronized List<BufferedImage> traceGcodeSet(List<String> gcodeFiles)
    {
        List<BufferedImage> traces = new ArrayList<>();
        for (String gcode : gcodeFiles) {
            traces.add(traceGcode(gcode));
        }
        return traces;
    }
    
    /**
     * Traces the gcode for a file.
     *
     * @param gcode The gcode file to trace.
     * @return The BufferedImage that was created from the gcode.
     */
    static int n;
    private synchronized BufferedImage traceGcode(String gcode)
    {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(gcode));
        } catch (IOException e) {
            System.err.println("Error reading lines of gcode file:" + gcode + ".");
            e.printStackTrace();
        }
        BufferedImage trace = new BufferedImage(IMAGE_SIZE_X / scaleFactor, IMAGE_SIZE_Y / scaleFactor, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = trace.createGraphics();
        initializeImage(g2);
        
        traceX = IMAGE_BORDER;
        traceY = IMAGE_BORDER + (IMAGE_SIZE_X - 2 * IMAGE_BORDER) / 2;
        Pattern g1Pattern = Pattern.compile("G1\\sX(?<x>-?\\d*\\.?\\d*)\\sY(?<y>-?\\d*\\.?\\d*).*");
        for (String line : lines) {
            Matcher g1Matcher = g1Pattern.matcher(line);
            if (g1Matcher.matches()) {
                double x = Double.valueOf(g1Matcher.group("x"));
                double y = Double.valueOf(g1Matcher.group("y"));
                moveTrace(g2, x, y);
            }
        }
    
        n++;
        saveImage(trace, "JPG", new File(String.valueOf(n) + ".jpg"));
        
        return trace;
    }
    
    /**
     * Initializes the image for a gcode trace.
     *
     * @param g2 The graphics context to initialize.
     */
    private void initializeImage(Graphics2D g2)
    {
        //initialize image
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, IMAGE_SIZE_X / scaleFactor, IMAGE_SIZE_Y / scaleFactor);
//        g2.setColor(Color.BLACK);
//        g2.drawRect(0, 0, (IMAGE_SIZE_X / scaleFactor) - 1, (IMAGE_SIZE_Y / scaleFactor) - 1);
        g2.setColor(Color.DARK_GRAY);
        
        g2.drawLine(IMAGE_BORDER / scaleFactor, IMAGE_BORDER / scaleFactor, (IMAGE_SIZE_X - IMAGE_BORDER) / scaleFactor, IMAGE_BORDER / scaleFactor);
        g2.drawLine(IMAGE_BORDER / scaleFactor, (IMAGE_BORDER - 1) / scaleFactor, (IMAGE_SIZE_X - IMAGE_BORDER) / scaleFactor, (IMAGE_BORDER - 1) / scaleFactor);
        
        g2.drawLine((IMAGE_SIZE_X / 2 - 1) / scaleFactor, IMAGE_BORDER / scaleFactor, (IMAGE_SIZE_X / 2 - 1) / scaleFactor, (IMAGE_SIZE_Y - IMAGE_BORDER) / scaleFactor);
        g2.drawLine((IMAGE_SIZE_X / 2) / scaleFactor,     IMAGE_BORDER / scaleFactor, (IMAGE_SIZE_X / 2) / scaleFactor, (IMAGE_SIZE_Y - IMAGE_BORDER) / scaleFactor);
        g2.drawLine((IMAGE_SIZE_X / 2 + 1) / scaleFactor, IMAGE_BORDER / scaleFactor, (IMAGE_SIZE_X / 2 + 1) / scaleFactor, (IMAGE_SIZE_Y - IMAGE_BORDER) / scaleFactor);
        
        
        //draw grid
        g2.setColor(new Color(220, 220, 220));
        for (int i = 1; i <= ((IMAGE_SIZE_X - 2 * IMAGE_BORDER) / 2) / 25.4; i++) {
            g2.drawLine((((IMAGE_SIZE_X - 2 * IMAGE_BORDER) / 2) + IMAGE_BORDER + (int) (25.4 * i)) / scaleFactor, IMAGE_BORDER / scaleFactor, (((IMAGE_SIZE_X - 2 * IMAGE_BORDER)/ 2) + IMAGE_BORDER + (int) (25.4 * i)) / scaleFactor, (IMAGE_SIZE_Y - IMAGE_BORDER) / scaleFactor);
            g2.drawLine((((IMAGE_SIZE_X - 2 * IMAGE_BORDER) / 2) + IMAGE_BORDER - (int) (25.4 * i)) / scaleFactor, IMAGE_BORDER / scaleFactor, (((IMAGE_SIZE_X - 2 * IMAGE_BORDER) / 2) + IMAGE_BORDER - (int) (25.4 * i)) / scaleFactor, (IMAGE_SIZE_Y - IMAGE_BORDER) / scaleFactor);
        }
        for (int i = 1; i <= (IMAGE_SIZE_Y - 2 * IMAGE_BORDER) / 25.4; i++) {
            g2.drawLine(IMAGE_BORDER / scaleFactor, (IMAGE_BORDER + (int) (25.4 * i)) / scaleFactor, (IMAGE_SIZE_X - IMAGE_BORDER) / scaleFactor, (IMAGE_BORDER + (int) (25.4 * i)) / scaleFactor);
        }
    }
    
    /**
     * Performs a move from the gcode.
     *
     * @param g2 The graphics context.
     * @param x  The relative x movement.
     * @param y  The relative y movement.
     */
    private synchronized void moveTrace(Graphics2D g2, double x, double y)
    {
        g2.setColor(Color.BLACK);
        g2.drawLine((int) traceY / scaleFactor, (int) traceX / scaleFactor, (int) (traceY + y) / scaleFactor, (int) (traceX + x) / scaleFactor);
        traceX += x;
        traceY += y;
    }
    
    
    //Functions
    
    /**
     * Saves a BufferedImage to a file.
     *
     * @param img    The image to save.
     * @param type   The file type to create.
     * @param saveTo The file to save the image to.
     */
    public static void saveImage(BufferedImage img, String type, File saveTo)
    {
        try {
            ImageIO.write(img, type, saveTo);
        } catch (IOException e) {
            System.err.println("Error in trying to write the image");
            e.printStackTrace();
        }
    }
    
}
