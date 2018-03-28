/*
 * File:    MachineDetector.java
 * Package: utils
 * Author:  Zachary Gill
 */

package utils;

import grbl.DetectGrbl;
import main.Main;

/**
 * Handles machine detection.
 */
public final class MachineDetector
{
    
    //Enums
    
    /**
     * An enumeration of machine types.
     */
    public enum Machine
    {
        CNC,
        HOTWIRE,
        NONE
    }
    
    //Static Fields
    
    /**
     * The type of machine that is currently connected.
     */
    private static Machine machine;

    private static String cnc = "55735323835351017091";
    private static String hotwire;
    private static String test = "95530343235351D092F0";
    
    
    //Static Methods
    
    /**
     * Detects which machine is connected.
     */
    public static void detectMachine()
    {
        DetectGrbl detect = new DetectGrbl();
        String type = detect.getType();

        if (Main.development) {
            machine = Main.developmentMode;
        } else {
            if (type.compareTo(cnc) == 0) {
                machine = Machine.CNC;
            } else if(type.compareTo(hotwire) == 0){
                machine = Machine.HOTWIRE;
            }
        }
    }
    
    /**
     * Returns the type of machine connected to.
     *
     * @return The type of machine connected to.
     */
    public static Machine getMachineType()
    {
        if (machine == null) {
            detectMachine();
        }
        
        return machine;
    }
    
    /**
     * Returns whether the machine connected to is the CNC machine or not.
     *
     * @return Whether the machine connected to is the CNC machine or not.
     */
    public static boolean isCncMachine()
    {
        return getMachineType() == Machine.CNC;
    }
    
    /**
     * Returns whether the machine connected to is the Hot Wire machine or not.
     *
     * @return Whether the machine connected to is the Hot Wire machine or not.
     */
    public static boolean isHotWireMachine()
    {
        return getMachineType() == Machine.HOTWIRE;
    }
    
}
