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
    
    /**
     * The path to the Python library directory.
     */
    public static final String PYTHON_DIRECTORY = "lib" + File.separator + "Python" + File.separator;
    
    /**
     * The path to the Python library directory.
     */
    public static final String PYTHON_FILENAME = "python-3.6.4.exe";
    
}
