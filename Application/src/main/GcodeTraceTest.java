/*
 * File:    GcodeTraceTest.java
 * Package: main
 * Author:  Zachary Gill
 */

package main;

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

public class GcodeTraceTest
{
    
    public static final int IMAGE_SIZE = 300;
    public static final int IMAGE_BORDER = IMAGE_SIZE / 20;
    public static final int IMAGE_MIDDLE = IMAGE_SIZE / 2;
    
    
    private static double traceX = IMAGE_MIDDLE;
    private static double traceY = IMAGE_BORDER;
    
    
    public static void main(String args[]) {
        String gcodeFile = "resources/jedicut files/sample wing/GCode_by_Jedicut_1628279641.txt";
    
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(gcodeFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        BufferedImage trace = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = trace.createGraphics();
    
        //initialize image
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, IMAGE_SIZE - 1, IMAGE_SIZE - 1);
        g2.setColor(Color.GRAY);
        g2.drawLine(IMAGE_BORDER, IMAGE_BORDER, IMAGE_SIZE - IMAGE_BORDER, IMAGE_BORDER);
        g2.drawLine(IMAGE_MIDDLE, IMAGE_BORDER, IMAGE_MIDDLE, IMAGE_SIZE - IMAGE_BORDER);
    
        //draw grid
        g2.setColor(new Color(220, 220, 220));
        for (int i = 1; i <= (IMAGE_MIDDLE - IMAGE_BORDER) / 10; i++ ) {
            g2.drawLine(IMAGE_MIDDLE + (10 * i), IMAGE_BORDER, IMAGE_MIDDLE + (10 * i), IMAGE_SIZE - IMAGE_BORDER);
            g2.drawLine(IMAGE_MIDDLE - (10 * i), IMAGE_BORDER, IMAGE_MIDDLE - (10 * i), IMAGE_SIZE - IMAGE_BORDER);
        }
        for (int i = 1; i <= (IMAGE_SIZE - (2 * IMAGE_BORDER)) / 10; i++ ) {
            g2.drawLine(IMAGE_BORDER, IMAGE_BORDER + (10 * i), IMAGE_SIZE - IMAGE_BORDER, IMAGE_BORDER + (10 * i));
        }
    
        traceX = IMAGE_BORDER;
        traceY = IMAGE_MIDDLE;
        Pattern g1Pattern = Pattern.compile("G1\\sX(?<x>-?\\d*\\.?\\d*)\\sY(?<y>-?\\d*\\.?\\d*).*");
        for (String line : lines) {
            Matcher g1Matcher = g1Pattern.matcher(line);
            if (g1Matcher.matches()) {
                double x = Double.valueOf(g1Matcher.group("x"));
                double y = Double.valueOf(g1Matcher.group("y"));
                moveTrace(g2, x, y);
            }
        }
        
        try {
            ImageIO.write(trace, "png", new File("resources/jedicut files/sample wing/GCode_by_Jedicut_1628279641.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    private static void moveTrace(Graphics2D g2, double x, double y)
    {
        g2.setColor(Color.BLACK);
        g2.drawLine((int) traceY, (int) traceX, (int) (traceY + y), (int) (traceX + x));
        traceX += x;
        traceY += y;
    }
    
}
