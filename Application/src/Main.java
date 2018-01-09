/*
 * File:    Main.java
 * Package:
 * Author:  Zachary Gill
 */

import grbl.APIgrbl;
import gui.Interfaces.Greeting.GreetingController;
import javafx.application.Application;
import slicer.Slicer;
import utils.CmdLine;
import utils.Constants;
import gui.*;
import java.io.File;

public class Main
{
    
    //Static Fields
    
    /**
     * The name of the operating system.
     */
    public static String operatingSystem;
    
    /**
     * The architecture level of the operating system; either "x64" or "x86".
     */
    public static String architecture;
    
    /**
     * The Java runtime version.
     */
    public static String javaVersion;

    public static String port;
    
    //Methods
    
    /**
     * The tracer.main method.
     *
     * @param args Arguments to the tracer.main method.
     */
    public static void main(String [] args)
    {

        if (!init()) {
            return;
        }

        Application.launch(Gui.class, args);

        // Move to MainMenu
        String filename = GreetingController.getFileNames().get(0);

        File model = new File(filename);


//        Renderer renderer = new Renderer(model);
//        System.out.println();
        
        Slicer slicer = new Slicer(model.getAbsolutePath(),architecture);
        slicer.slice("--gcode-flavor mach3");
        System.out.println();

        APIgrbl grbl = new APIgrbl();
        grbl.start("Birds_and_flowers.gcode");
    }
    
    /**
     * Initializes the system properties and ensures the system is valid to run the application.
     *
     * @return Whether the system setup is valid or not.
     */
    private static boolean init()
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

        System.out.println(operatingSystem);
        System.out.println(architecture);
        System.out.println(javaVersion);
        
        return true;
    }
    
}
