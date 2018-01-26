/*
 * File:    MachineSelector.java
 * Package: main
 * Author:  Zachary Gill
 */

package utils;

import gui.Debugger;

public final class MachineDetector
{
    
    private static Machine machine;
    
    
    public enum Machine {
        CNC,
        HOTWIRE
    }
    
    
    public static void detectMachine()
    {
        //TODO
        machine = Machine.CNC;
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
