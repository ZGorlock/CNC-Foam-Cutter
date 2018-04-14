/*
 * File:    Debug.java
 * Package: main
 * Author:  Zachary Gill
 */

package main;

import gui.Gui;
import gui.interfaces.greeting.GreetingController;
import gui.interfaces.greeting.InputController;
import gui.interfaces.main.GcodeController;
import gui.interfaces.main.ModelController;
import javafx.application.Application;
import renderer.Renderer;
import utils.MachineDetector;

import java.util.ArrayList;

/**
 * The launching class for our Application in debug mode.
 */
public class Debug extends Main
{
    
    //Constants
    
    /**
     * Sample upload gcode file for Debugging.
     */
    public static String gcodeVersion = "resources\\models\\bread loaf.gcode";

    /**
     * Sample upload stl file for Debugging.
     */
    public static String stlVersion = "resources\\models\\bread loaf.stl";
    
    
    //Methods
    
    /**
     * The Main method.
     *
     * @param args Arguments to the Main method.
     */
    public static void main(String[] args)
    {
        Debug debug = new Debug();
        if (!debug.init()) {
            return;
        }
        Main.main = debug;
        
        if (Main.demoMode) {
            gcodeVersion = "resources\\models\\CylinderHead-binary.gcode";
            stlVersion = "resources\\models\\CylinderHead-binary.stl";
        }
    
        InputController i = new InputController();
        i.populateDefaultValues();
        
        GreetingController g = new GreetingController();
        g.setup();
        GcodeController.gcodeFile = gcodeVersion;
        
        ArrayList<String> filenames = new ArrayList<>();
        
        if (MachineDetector.isCncMachine()) {
            filenames.add(stlVersion); //set the files here
            
        } else if (MachineDetector.isHotWireMachine()) {
            filenames.add("resources\\profiles\\rotation demo\\rd01.gcode");
            filenames.add("resources\\profiles\\rotation demo\\rd01 - Copy.gcode");
            filenames.add("resources\\profiles\\rotation demo\\rd02.gcode");
            filenames.add("resources\\profiles\\rotation demo\\rd02 - Copy.gcode");
            filenames.add("resources\\profiles\\rotation demo\\rd03.gcode");
            filenames.add("resources\\profiles\\rotation demo\\rd03 - Copy.gcode");
        }
        
        GreetingController.setFileNames(filenames);
        
        if (Main.demoMode) {
            Renderer.foamWidth = 8;
            Renderer.foamLength = 8;
            Renderer.foamHeight = 8;
            
        } else {
            Renderer.foamWidth = ModelController.DEFAULT_MAX_WIDTH_CNC;
            Renderer.foamLength = ModelController.DEFAULT_MAX_LENGTH_CNC;
            Renderer.foamHeight = ModelController.DEFAULT_MAX_HEIGHT_CNC;
    
            Renderer.modelWidth = 166;
            Renderer.modelLength = 472;
            Renderer.modelHeight = 81;
        }
        
        Gui.debug = true;
        Application.launch(Gui.class, args);
    }
    
}
