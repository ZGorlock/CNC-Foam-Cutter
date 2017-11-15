package slicer;/*
 * File:    Slicer.java
 * Package:
 * Author:  Zachary Gill
 */

import utils.CmdLine;
import utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Converts an STL file to G-Code.
 */
public class Slicer
{
    
    //Constants
    
    /**
     * The list of supported model file formats.
     */
    public static final String[] validFileExtensions = {"stl", "obj", "amf"}; //TODO ensure we can also render these formats
    
 
    //Fields
    
    /**
     * The path to the 3D model to slice.
     */
    private String model = "";
    private String architecture = "";
    
    //Constructors
    
    /**
     * Constructs a Slicer object.
     *
     * @param modelFile The path to the 3D model file.
     */
    public Slicer(String modelFile, String architectureMain)
    {
        model = modelFile;
        architecture = architectureMain;
    }
    
    
    //Methods
    
    /**
     * Slices the model and generates the corresponding g-code.
     *
     * @param arguments The arguments to send to Slic3r.
     * @return Whether the slicing was successful or not.
     */
    public boolean slice(String arguments)
    {
        File modelFile = new File(model);
        if (!modelFile.exists()) {
            System.err.println("The file: " + model + " does not exist.");
            return false;
        }
        if (!isSupportedModelFormat(model)) {
            System.err.println("The file: " + model + " is not of a supported model file type.");
            
            StringBuilder formatList = new StringBuilder();
            for (String fileExtension : validFileExtensions) {
                formatList.append((formatList.toString().isEmpty()) ? "" : ", ");
                formatList.append(fileExtension.toUpperCase());
            }
            System.err.println("Please use one of the following file formats: " + formatList.toString());
            
            return false;
        }
        
        String sliceModel = Constants.SLIC3R_DIRECTORY + "win-" + architecture + File.separator +
                Constants.SLIC3R_EXECUTABLE_FILENAME +
                " " + model +
                (arguments.isEmpty() ? "" : " " + arguments);
    
        Process process = CmdLine.executeCmdAsThread(sliceModel);
        if (process == null) {
            System.err.println("There was an error starting Slic3r.");
            return false;
        }
    
        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
    
        try {
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
        } catch (IOException ignored) {
            System.err.println("Lost communication with Slic3r.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Slices the model and generates the corresponding g-code.
     *
     * @return Whether the slicing was successful or not.
     */
    public boolean slice()
    {
        return slice("");
    }
    
    
    //Functions
    
    /**
     * Determines if a file is of a supported model file format.
     *
     * @param model The path of the file to test.
     * @return Whether the file is of a supported model file format or not.
     */
    public static boolean isSupportedModelFormat(String model)
    {
        for (String fileExtension : validFileExtensions) {
            if (model.toLowerCase().endsWith(fileExtension)) {
                return true;
            }
        }
        
        return false;
    }
    
}
