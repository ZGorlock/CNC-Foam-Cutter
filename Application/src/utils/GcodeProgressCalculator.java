/*
 * File:    GcodeProgressCalculator.java
 * Package: utils
 * Author:  Zachary Gill
 */

package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Provides utilities for calculating the distance of gcode instructions.
 */
public class GcodeProgressCalculator
{
    
    //Functions
    
    /**
     * Calculates the total distance travelled by a gcode file.
     *
     * @param gcodeFile The gcode file to calculate the distance for.
     * @return The total distance travelled by a gcode file, or -1 if there was an error.
     */
    public static double calculateFileProgressUnits(String gcodeFile)
    {
        File gcode = new File(gcodeFile);
        if (!gcode.exists()) {
            System.err.println("Cannot calculate gcode distance on file: " + gcode.getAbsolutePath() + ". File does not exist!");
            return -1;
        }
        
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(gcode.getAbsolutePath()));
        } catch (IOException e) {
            System.err.println("Cannot calculate gcode distance on file: " + gcode.getAbsolutePath() + ". File cannot be read!");
            return -1;
        }
        
        return calculateFileProgressUnits(lines);
    }
    
    /**
     * Calculates the total distance travelled by a gcode file.
     *
     * @param gcodeCommands The list of gcode instructions to calculate the distance for.
     * @return The total distance travelled by a gcode file, or -1 if there was an error.
     */
    public static double calculateFileProgressUnits(List<String> gcodeCommands)
    {
        double distance = 0.0;
        for (String gcodeCommand : gcodeCommands) {
            distance += calculateInstructionProgressUnits(gcodeCommand);
        }
        return distance;
    }
    
    /**
     * Calculates the distance travelled by a gcode instruction.
     *
     * @param instruction The gcode instruction to calculate the distance for.
     * @return The distance travelled by a gcode instruction.
     */
    public static double calculateInstructionProgressUnits(String instruction)
    {
        List<String> tokens = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(instruction);
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        
        if (tokens.size() > 0) {
            if (tokens.get(0).equals("G1") || tokens.get(0).equals("G0")) {
                double x = 0;
                double y = 0;
                double z = 0;
                double f = 0;
                
                try {
                    for (String token : tokens) {
                        if (token.startsWith("X")) {
                            x = Double.parseDouble(token.substring(1));
                        } else if (token.startsWith("Y")) {
                            y = Double.parseDouble(token.substring(1));
                        } else if (token.startsWith("Z")) {
                            z = Double.parseDouble(token.substring(1));
                        } else if (token.startsWith("F")) {
                            f = Double.parseDouble(token.substring(1));
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error calculating the gcode distance for instruction: " + instruction + ". Number is not formatted properly!");
                    return 0;
                }
    
                double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                if (f != 0) {
                    distance /= (f / 60); //divide by velocity in mm/sec
                }
                
                return distance;
            }
        }
        
        return 0;
    }
    
}
