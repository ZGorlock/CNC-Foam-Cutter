/*
 * File:    Debug.java
 * Package: main
 * Author:  Zachary Gill
 */

package main;

import gui.Gui;
import gui.interfaces.greeting.GreetingController;
import gui.interfaces.main.GcodeController;
import javafx.application.Application;
import renderer.Renderer;

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
    public static final String gcodeVersion = "resources\\gcode\\can.gcode";

    /**
     * Sample upload stl file for Debugging.
     */
    public static final String stlVersion = "resources\\cadfiles\\CylinderHead-binary.stl";
    
    
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
        
        GreetingController g = new GreetingController();
        g.setup();
        GcodeController.gcodeFile = gcodeVersion;
        
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(gcodeVersion); //set the files here
        filenames.add("resources\\gcode\\test.gcode");
        filenames.add("resources\\gcode\\time.gcode");

        GreetingController.setFileNames(filenames);
    
        Renderer.foamWidth = 10;
        Renderer.foamLength = 10;
        Renderer.foamHeight = 10;
        
        Gui.debug = true;
        Application.launch(Gui.class, args);
    }
    
}
