/*
 * File:    MachineDetector.java
 * Package: main
 * Author:  Zachary Gill
 */

package utils;

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
    
    
    //Static Methods
    
    /**
     * Detects which machine is connected.
     */
    public static void detectMachine()
    {
        machine = Machine.CNC;
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
