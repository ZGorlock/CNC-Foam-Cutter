/*
 * File:    TracerGcodeBypass.java
 * Package: utils
 * Author:  Zachary Gill
 */

package utils;

import gui.interfaces.main.TraceController;
import javafx.application.Platform;
import tracer.Tracer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Fakes the grbl position from gcode commands.
 */
public class TracerGcodeBypass
{
    
    //Static Fields
    
    /**
     * The virtual x position of the spindle.
     */
    static double posX = 0;
    
    /**
     * The virtual y position of the spindle.
     */
    static double posY = 0;
    
    /**
     * The virtual z position of the spindle.
     */
    static double posZ = 0;
    
    /**
     * The saved x position of the spindle.
     */
    static double saveX;
    
    /**
     * The saved y position of the spindle.
     */
    static double saveY;
    
    /**
     * The saved z position of the spindle.
     */
    static double saveZ;
    
    
    //Functions
    
    /**
     * Traces a gcode command.
     *
     * @param instruction         The command to trace.
     * @param absoluteCoordinates Whether or not to use absolute coordinates.
     */
    public static void traceGcodeCommand(String instruction, boolean absoluteCoordinates)
    {
        List<String> tokens = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(instruction);
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        
        
        if (tokens.size() > 0) {
            if (tokens.get(0).equals("G1")) {
                double x = saveX;
                double y = saveY;
                double z = saveZ;
                double f = 0;
                
                try {
                    for (String token : tokens) {
                        if (token.startsWith("X")) {
                            x = Double.parseDouble(token.substring(1));
                            saveX = x;
                        } else if (token.startsWith("Y")) {
                            y = Double.parseDouble(token.substring(1));
                            saveY = y;
                        } else if (token.startsWith("Z")) {
                            z = Double.parseDouble(token.substring(1));
                            saveZ = z;
                        } else if (token.startsWith("F")) {
                            f = Double.parseDouble(token.substring(1));
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error calculating the gcode distance for instruction: " + instruction + ". Number is not formatted properly!");
                    return;
                }
                
                if (absoluteCoordinates) {
                    posX = x;
                    posY = y;
                    posZ = z;
                } else {
                    posX += x;
                    posY += y;
                    posZ += z;
                }
                
                Tracer.addTrace(posX, posY, posZ);
                Platform.runLater(()->{
                    TraceController.controller.grblX.setText(String.format("%.2f", posX));
                    TraceController.controller.grblY.setText(String.format("%.2f", posY));
                    TraceController.controller.grblZ.setText(String.format("%.2f", posZ));
                    TraceController.controller.grblStatus.setText("Ok");
                });
            }
        }
    }
    
}
