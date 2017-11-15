package utils;/*
 * File:    Constants.java
 * Package: PACKAGE_NAME
 * Author:  Zachary Gill
 */

import java.io.File;

/**
 * Contains constants for the application.
 */
public class Constants
{
    //SYSTEM
    
    /**
     * The string used to indicate a x64 architecture.
     */
    public static final String ARCHITECTURE_x64 = "x64";
    
    /**
     * The string used to indicate a x86 architecture.
     */
    public static final String ARCHITECTURE_x86 = "x86";
    

    //SLIC3R
    
    /**
     * The path to the Slic3r library directory.
     */
    public static final String SLIC3R_DIRECTORY = "lib" + File.separator + "Slic3r" + File.separator;
    
    /**
     * The name of the Slic3r executable.
     */
    public static final String SLIC3R_EXECUTABLE_FILENAME = "slic3r-console.exe";


    //JAVA3D
    
    /**
     * The path to the Java3D library directory.
     */
    public static final String JAVA3D_DIRECTORY = "lib" + File.separator + "Java3D" + File.separator;
    
    /**
     * The base filename for the Java3D installer.
     */
    public static final String JAVA3D_FILENAME_BASE = "java3d-1_5_1-windows";
    
    /**
     * The filename suffix for the Java3D installer for a 64-bit system.
     */
    public static final String JAVA3D_FILENAME_SUFFIX_64 = "-amd64.exe";
    
    /**
     * The filename suffix for the Java3D installer for a 32-bit system.
     */
    public static final String JAVA3D_FILENAME_SUFFIX_86 = "-i586.exe";

    // GRBL - subject to change

    /**
     * The filename for the input testing path
     */
    private static final String INPUT_TEST_PATH = "C:\\Users\\nicol\\Desktop\\CNC-Foam-Cutter\\Application\\src\\grbl\\tests\\input\\";

    /**
     * The filepath for g code source
     */
    private static final String GSRC_PATH = "C:\\Users\\nicol\\Desktop\\CNC-Foam-Cutter\\Application\\src\\grbl\\gsrc\\";

    /**
     * The filepath for the temp folder to write to
     */
    private static final String TEMP_PATH = "C:\\Users\\nicol\\Desktop\\CNC-Foam-Cutter\\Application\\src\\grbl\\temp\\";

    /**
     * The port used for ARDUINO UNO
     */
    private static final String PORT = "COM3";


    public static String getInputTestPath()
    {
        return INPUT_TEST_PATH;
    }

    public  static String getPort()
    {
        return PORT;
    }

    public static String getGsrc()
    {
        return GSRC_PATH;
    }

    public static String getTemp()
    {
        return TEMP_PATH;
    }
}
