/*
 * File:    Debug.java
 * Package: main
 * Author:  Zachary Gill
 */

package main;

import gui.Gui;
import gui.Interfaces.Greeting.GreetingController;
import javafx.application.Application;

import java.util.ArrayList;

/**
 * The launching class for our Application in debug mode.
 */
public class Debug extends Main
{
    
    //Constants
    
    /**
     * Sample upload files for Debugging.
     */
    public static final String gcodeVersion = "resources\\gcode\\Birds_and_flowers.gcode";
    public static final String stlVersion = "resources\\cadfiles\\CylinderHead-binary.stl";
    
    
    //Methods
    
    /**
     * The Main method.
     *
     * @param args Arguments to the Main method.
     */
    public static void main(String [] args)
    {
        Debug debug = new Debug();
        if (!debug.init()) {
            return;
        }
        Main.main = debug;
    
        GreetingController g = new GreetingController();
        g.setup();
        
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(stlVersion); //set the files here
        GreetingController.setFileNames(filenames);
        
        Gui.debug = true;
        Application.launch(Gui.class, args);
    }
    
}
