/*
 * File:    MachineSelector.java
 * Package: main
 * Author:  Zachary Gill
 */

package utils;

public final class MachineDetector
{
    
    private static Machine machine;
    
    
    public enum Machine {
        CNC,
        HOTWIRE
    }
    
    
    public static void detectMachine()
    {
        //TODO actually detect which machine is being used
        machine = Machine.HOTWIRE;
    }
    
    
    public static Machine getMachineType()
    {
        if (machine == null) {
            detectMachine();
        }
    
        return machine;
    }
    
    public static boolean isCncMachine()
    {
        return getMachineType() == Machine.CNC;
    }
    
    public static boolean isHotWireMachine()
    {
        return getMachineType() == Machine.HOTWIRE;
    }
    
}
