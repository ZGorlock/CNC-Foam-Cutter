/*
 * File:    DetectGrbl.java
 * Package: grbl
 * Author:  Nicolas Lopez
 */

package grbl;

import utils.CmdLine;
import utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Detects the machine type through grbl.
 */
public class DetectGrbl
{
    
    //Fields
    
    /**
     *  The Machine type to be set by detect.py.
     */
    private String type;
    
    
    //Constructors
    
    /**
     * The default constructor for DetectGrbl.
     */
    public DetectGrbl(){
        detectMachine();
    }
    
    
    //Methods
    
    /**
     * Detects which machine is connected.
     */
    private void detectMachine()
    {
        Process process = null;

        // The command needed to run detect.py
        String command = "\" \"";

        while (process == null) {

            process = CmdLine.executeCmdAsThread("python " + Constants.GRBL_DIRECTORY + "runDetect.py");
            if (process != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line = "";
                while (line != null && line.isEmpty()) {
                    try {
                        line = r.readLine();

                        if (line != null && !line.isEmpty()) {
                            if (line.equals("Traceback (most recent call last):")) {
                                this.type = null;
                            } else {
                                this.type = line;
                            }
                        }
                    }catch (IOException e)
                    {
                        System.out.println("Error detecting machine");
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("Error attempting to run detect.py! Reattempting...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
    
    
    //Getters
    
    /**
     * Returns the detected machine.
     *
     * @return The detected machine.
     */
    public String getType()
    {
        return this.type;
    }

}
