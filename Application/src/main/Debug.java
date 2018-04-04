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
    public static final String gcodeVersion = "resources\\models\\bread loaf.gcode";

    /**
     * Sample upload stl file for Debugging.
     */
    public static final String stlVersion = "resources\\models\\bread loaf.stl";
    
    
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
    
        InputController i = new InputController();
        i.populateDefaultValues();
        
        GreetingController g = new GreetingController();
        g.setup();
        GcodeController.gcodeFile = gcodeVersion;
        
        ArrayList<String> filenames = new ArrayList<>();
        
        //CNC
        filenames.add(stlVersion); //set the files here
        
        //HotWire
//        filenames.add("resources\\profiles\\rotation demo\\rd01.gcode");
//        filenames.add("resources\\profiles\\rotation demo\\rd01 - Copy.gcode");
//        filenames.add("resources\\profiles\\rotation demo\\rd02.gcode");
//        filenames.add("resources\\profiles\\rotation demo\\rd02 - Copy.gcode");
//        filenames.add("resources\\profiles\\rotation demo\\rd03.gcode");
//        filenames.add("resources\\profiles\\rotation demo\\rd03 - Copy.gcode");


        GreetingController.setFileNames(filenames);
    
//        Renderer.foamWidth = 10;
//        Renderer.foamLength = 10;
//        Renderer.foamHeight = 10;
    
        Renderer.foamWidth = ModelController.MAX_WIDTH_CNC;
        Renderer.foamLength = ModelController.MAX_LENGTH_CNC;
        Renderer.foamHeight = ModelController.MAX_HEIGHT_CNC;
        
        Gui.debug = true;
        Application.launch(Gui.class, args);
    }
    
}
