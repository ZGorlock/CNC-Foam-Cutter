/*
 * File:    Main.java
 * Package: main
 * Author:  Zachary Gill
 */

package main;

import grbl.APIgrbl;
import gui.Gui;
import gui.interfaces.main.GcodeController;
import gui.interfaces.main.ModelController;
import gui.interfaces.main.TraceController;
import javafx.application.Application;
import javafx.application.Platform;
import renderer.Renderer;
import tracer.Tracer;
import utils.CmdLine;
import utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The launching class for our Application.
 */
public class Main
{
    
    //Team
    
    //TODO get 3 judges
    //TODO update presentation powerpoint
    //TODO update Design Document
    //TODO conference paper
    
    //TODO add unit tests for Controller setup pieces
    //TODO Rotation...
    //TODO check for machine connection before even going to the input screen (new starting window?)
    
    
    //Zack
    
    //TODO reset path for hot wire profiles (it looks like this can be done with a single command: G28)
    //TODO handle if the sum of the profiles is not 360 (auto and user set) (do this in Print button)
    
    //TODO console text when uploading
    
    //TODO User notifications
    //  GcodeController line 332
    //  MenuController line 129
    //  APIgrbl lin 309, 340
    //  ---
    //  APIgrbl line 268 (how should we handle this)
    
    
    //Nick
    
    //TODO you cannot upload folders or multiple files (how are we going to do the Hot Wire profiles?)
    
    //TODO handle really long descriptions on the Model tab, maybe make a dynamically sized text area there instead of a label
    //TODO "are you sure" window for STOP button
    
    //TODO line 196 in RotationController
    
    
    
    //Static Fields
    
    /**
     * The singleton instance of the Main class.
     */
    public static Main main;
    
    /**
     * The start time of the printing process.
     */
    public static long startTime;
    
    /**
     * The current progress through the printing execution.
     */
    public static double progress = 0;
    
    /**
     * The total progress of the printing execution.
     */
    public static double totalProgressUnits = 0;
    
    
    //Fields
    
    /**
     * The name of the operating system.
     */
    public String operatingSystem;
    
    /**
     * The architecture level of the operating system; either "x64" or "x86".
     */
    public String architecture;
    
    /**
     * The Java runtime version.
     */
    public String javaVersion;
    
    /**
     * The Python runtime version.
     */
    public String pythonVersion;
    
    /**
     * The port connected to the Arduino UNO.
     */
    public String port;
    
    
    //Main Method
    
    /**
     * The Main method.
     *
     * @param args Arguments to the Main method.
     */
    public static void main(String[] args)
    {
        main = new Main();
        if (!main.init()) {
            return;
        }
        Application.launch(Gui.class, args);
    }
    
    
    //Methods
    
    /**
     * Initializes the system properties and ensures the system is valid to run the application.
     *
     * @return Whether the system setup is valid or not.
     */
    protected boolean init()
    {
        operatingSystem = System.getProperty("os.name").toUpperCase();
        
        if (!operatingSystem.contains("WIN")) {
            System.err.println("This application must be run on a Windows PC.");
            System.err.println("Please try again with a Windows PC.");
            return false;
        }
        
        architecture = (System.getenv("ProgramFiles(x86)") != null) ? Constants.ARCHITECTURE_x64 : Constants.ARCHITECTURE_x86;
        
        javaVersion = System.getProperty("java.runtime.version").toUpperCase(); // ie: 1.8.0_121-b13
        
        if (Double.parseDouble(javaVersion.substring(0, 3)) < 1.7) {
            System.err.println("This application must have a Java runtime environment of 1.7 or higher.");
            System.err.println("Please install a newer JRE and try again.");
            return false;
        }
        
        try {
            Class.forName("javax.media.j3d.J3DBuffer");
        } catch (final ClassNotFoundException ignored) {
            System.err.println("You must have Java3D installed on your system to use this application.");
            System.out.println("Attempting to install Java3D...");
            
            String installJava3D = Constants.JAVA3D_DIRECTORY + Constants.JAVA3D_FILENAME_BASE +
                    (architecture.equals(Constants.ARCHITECTURE_x64) ? Constants.JAVA3D_FILENAME_SUFFIX_64 : Constants.JAVA3D_FILENAME_SUFFIX_86);
            
            CmdLine.executeCmd(installJava3D, true);
            
            try {
                Class.forName("javax.media.j3d.J3DBuffer");
            } catch (final ClassNotFoundException ignored2) {
                System.out.println("Please install Java3D and try again.");
                return false;
            }
        }
        
        String pythonCheck = CmdLine.executeCmd("py -V");
        boolean installPython = false;
        if (pythonCheck.isEmpty() || pythonCheck.matches("'py' is not recognized .*")) {
            installPython = true;
        } else {
            Pattern p = Pattern.compile("Python\\s(?<version>.+)\\r\\n");
            Matcher m = p.matcher(pythonCheck);
            if (m.matches()) {
                pythonVersion = m.group("version");
                if (!pythonVersion.startsWith("3")) {
                    installPython = true;
                }
            } else {
                installPython = true;
            }
        }
        if (installPython) {
            System.err.println("You must have Python 3 installed on your system to use this application.");
            System.out.println("Attempting to install Python 3...");
            
            String pythonInstallCmd = Constants.PYTHON_DIRECTORY + Constants.PYTHON_FILENAME;
            CmdLine.executeCmd(pythonInstallCmd, true);
            
            pythonCheck = CmdLine.executeCmd("py -V");
            if (pythonCheck.isEmpty() || pythonCheck.matches("'py' is not recognized .*")) {
                System.out.println("Please install Python 3 and try again.");
                return false;
            } else {
                Pattern p = Pattern.compile("Python\\s(?<version>.+)\\r\\n");
                Matcher m = p.matcher(pythonCheck);
                if (m.matches()) {
                    pythonVersion = m.group("version");
                    if (!pythonVersion.startsWith("3")) {
                        System.out.println("Please install Python 3 and try again.");
                        return false;
                    }
                } else {
                    System.out.println("Please install Python 3 and try again.");
                    return false;
                }
            }
        }
        
        System.out.println("OS:           " + operatingSystem);
        System.out.println("Architecture: " + architecture);
        System.out.println("Java:         " + javaVersion);
        System.out.println("Python:       " + pythonVersion);
        
        return true;
    }
    
    
    //Static Methods
    
    /**
     * Resets the Application.
     */
    public static void resetApplication()
    {
        Renderer.reset();
        Tracer.reset();
        
        ModelController.controller.reset();
        TraceController.controller.reset();
        GcodeController.controller.reset();
        
        APIgrbl.grbl.resetStreaming();
        startTime = 0;
    }
    
    /**
     * Kills the Application.
     */
    public static void killApplication()
    {
        Platform.exit();
        System.exit(0);
    }
    
}
