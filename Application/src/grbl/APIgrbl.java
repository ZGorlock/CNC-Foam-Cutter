/*
 * File:    APIgrbl.java
 * Package: grbl
 * Author:  Nicolas Lopez
 */

package grbl;

import gui.interfaces.main.GcodeController;
import gui.interfaces.main.MenuController;
import gui.interfaces.main.ModelController;
import gui.interfaces.main.TraceController;
import gui.interfaces.popup.SystemNotificationController;
import main.Main;
import utils.CmdLine;
import utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates the grbl  process.
 */
public class APIgrbl extends Thread
{
    
    //Constants
    
    /**
     * The number of recent time remaining calculations to store.
     */
    public static final int TIME_REMAINING_HISTORY_COUNT = 5;
    
    
    //Fields
    
    /**
     * The filename of the gcode file.
     */
    private String filename;
    
    /**
     * The list of gcode commands.
     */
    private List<String> commands;
    
    /**
     * A flag indicating whether grbl is done streaming or not.
     */
    private boolean doneStreaming = false;
    
    /**
     * The current progress of the printing process.
     */
    private double currentProgress = 0;
    
    /**
     * The total progress of the printing process.
     */
    private double totalProgress = 0;
    
    /**
     * The list of the recent time remaining calculations.
     */
    private final List<Double> timeRemainingHistory = new ArrayList<>(TIME_REMAINING_HISTORY_COUNT);
    
    /**
     * The x coordinate status field for grbl, displayed on the Tracer tab.
     */
    private double x = 0;
    
    /**
     * The y coordinate status field for grbl, displayed on the Tracer tab.
     */
    private double y = 0;
    
    /**
     * The z coordinate status field for grbl, displayed on the Tracer tab.
     */
    private double z = 0;
    
    /**
     * The status field for grbl, displayed on the Tracer tab.
     */
    private String status = "Off";
    
    /**
     * The buffer of user entered gcode commands.
     */
    private List<String> commandsFromUI = new ArrayList<>();
    
    
    //Static Fields
    
    /**
     * The instance of the grbl processor.
     */
    public static APIgrbl grbl;
    
    
    //Constructors
    
    /**
     * The constructor for the grbl processor.
     *
     * @param filename The filename of the gcode file to work on.
     */
    public APIgrbl(String filename)
    {
        grbl = this;
        this.filename = filename;
    }
    
    
    //Methods
    
    /**
     * Initializes the grbl processor.
     *
     * @return Whether the initialization was successful or not.
     */
    public boolean initialize()
    {
        // Modifies to gbrl acceptable gcode
        GcodeModifier m = new GcodeModifier(filename);
        if (!m.modify()) {
            System.err.println("An error occurred while running the GcodeModifier on file: " + filename);
            return false;
        }
        
        commands = m.getCommands();
//        totalProgress = GcodeProgressCalculator.calculateFileProgressUnits(commands);
        totalProgress = commands.size();
        currentProgress = 0;
        
        return true;
    }
    
    /**
     * Starts the grbl processor.
     */
    public void run()
    {
        //Begin streaming the gcode file
        partitionAndStream();
    }
    
    /**
     * Parses the gcode file into packets and streams them to the Arduino.
     */
    private void partitionAndStream()
    {
        try {
            int i = 0;
            while (i < commands.size()) {
                // read every 127 characters and create a file with them for stream.py to use
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Constants.GRBL_TEMP_DIRECTORY, "tempfile.txt")));
                StringBuilder sb = new StringBuilder();
        
                double packetProgressUnits = 0;
                int charsUsed = 0;
                while (charsUsed < 127) {
                    
                    String newCommand = null;
                    if (i < commands.size()) {
                        newCommand = commands.get(i);
                        i++;
                    }
            
                    if (newCommand != null) {
                        if ((newCommand.length() + charsUsed + 1) < 127) {
                            // append what will be written
                            sb.append(newCommand);
                            sb.append('\n');
                    
                            // update counts
                            charsUsed += newCommand.length() + 1;
//                            packetProgressUnits += GcodeProgressCalculator.calculateInstructionProgressUnits(newCommand);
                            packetProgressUnits++;
                        } else {
                            // end of 127 char limit
                            i--;
                            charsUsed = 127;
                        }
                    } else {
                        // end of file, stopping conditions
                        charsUsed = 127;
                    }
                }
                currentProgress += packetProgressUnits;
        
                // create string to be printed
                String gcode = sb.toString();
        
                bw.write(gcode);
                bw.close();
        
                // Update UI
                while (gcode.contains("\n\n")) {
                    gcode = gcode.replaceAll("\\n\\n", "\n");
                }
                if (!gcode.isEmpty()) {
                    GcodeController.codeBlock.add(gcode);
                }
        
                // Check for User Input
                checkForCommand();
        
                // Check for pause/resume
                if (MenuController.paused) {
                    synchronized (this) {
                        while (MenuController.paused) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // The current thread will be blocked until some else calls notify()
                        }
                    }
                }
        
                // Check for stopped condition
                if (MenuController.stopped) {
                    // Reset UI
                    currentProgress = 0;
                    return;
                }
        
                // execute stream.py with the file created, get input stream as a response
                Process process = null;
                while (process == null) {
                    process = CmdLine.executeCmdAsThread("py " + Constants.GRBL_DIRECTORY + "stream.py " + Constants.GRBL_TEMP_DIRECTORY + "tempfile.txt\n");
                    if (process != null) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
    
                        String line = "";
                        while (line.isEmpty()) {
                            line = r.readLine();
        
                            if (!line.isEmpty()) {
                                updateCoordinates(line);
                            }
                        }
                    } else {
                        System.err.println("Error attempting to run stream.py! Reattempting...");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
        
                // Check for User Input
                checkForCommand();
                
                //Sleep for Debugging
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("There was an error writing tempfile.txt during streaming!");
            e.printStackTrace();
//            SystemNotificationController.controller.raise("There was an error streaming to the machine!", true); TODO
        }
        
        // Reset UI
        doneStreaming = true;
    }
    
    /**
     * Queues a new user entered gcode command.
     *
     * @param command The command.
     */
    public void sendRequest(String command)
    {
        commandsFromUI.add(command);
    }
    
    /**
     * Checks for user entered commands to process.
     */
    private void checkForCommand()
    {
        // check for commands from UI
        if (commandsFromUI.size() > 0) {
            //create new process and add to the response for UI
            handleRequest();
        }
    }
    
    /**
     * Performs a user entered command.
     */
    private void handleRequest()
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Constants.GRBL_TEMP_DIRECTORY, "tempCommand.txt")));
            bw.write(commandsFromUI.get(0));
            bw.close();
        } catch (IOException e) {
            System.err.println("There was an error writing tempCommand.txt during streaming!");
            e.printStackTrace();
            return;
        }
        
        try {
            // execute stream.py with the command being sent, get input stream as a response
            Process process = null;
            while (process == null) {
                process = CmdLine.executeCmdAsThread("py -3 " + Constants.GRBL_DIRECTORY + "stream.py " + Constants.GRBL_TEMP_DIRECTORY + "tempCommand.txt\n");
                if (process != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
                    String line;
                    while (true) {
                        line = r.readLine();
                        if (line == null || line.isEmpty()) {
                            break;
                        }
                        GcodeController.commandBlock.add(' ' + line);
                    }
                } else {
                    System.err.println("Error attempting to run stream.py! Reattempting...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("There was an error reading grbl's response to a user entered command!");
            e.printStackTrace();
            SystemNotificationController.controller.raise("There was an error executing your command!", false);
        }
        
        commandsFromUI.remove(0);
    }
    
    /**
     * Updates the grbl status fields from grbl feedback.
     *
     * @param line The grbl feedback.
     */
    private void updateCoordinates(String line)
    {
        // Parse line into coordinates
        String[] decomposed = line.split(",");
        
        if (decomposed.length == 4) {
            setStatus(decomposed[0].substring(1));
            String[] first = decomposed[1].split(":");
            setX(Double.parseDouble(first[1]));
            setY(Double.parseDouble(decomposed[2]));
            setZ(Double.parseDouble(decomposed[3]));
        }
        
        String x = String.format("%.2f", getCoordinateX());
        String y = String.format("%.2f", getCoordinateY());
        String z = String.format("%.2f", getCoordinateZ());
        String status = getStatus();
        
        TraceController.coordinateBlock.clear();
        TraceController.coordinateBlock.add(0, x);
        TraceController.coordinateBlock.add(1, y);
        TraceController.coordinateBlock.add(2, z);
        TraceController.coordinateBlock.add(3, status);
    }
    
    /**
     * Handles a Resume event.
     */
    public synchronized void initiateResume()
    {
        notify();
    }
    
    
    //Getters
    
    /**
     * Returns the percentage completed string.
     *
     * @return The percentage completed string.
     */
    public static String getPercentageComplete()
    {
        if (MenuController.stopped) {
            return ModelController.controller.percentage.getText();
        }
        if (grbl == null) {
            return "0.00%";
        } else if (grbl.isDoneStreaming()) {
            return "100.00%";
        } else {
            double percentage = (grbl.currentProgress / grbl.totalProgress) * 100.0;
            return String.format("%.2f", percentage) + " %";
        }
    }
    
    /**
     * Returns the time remaining string.
     *
     * @return The time remaining string.
     */
    public static String getTimeRemaining()
    {
        if (MenuController.stopped) {
            return ModelController.controller.timeRemaining.getText();
        }
        if (Main.startTime == 0 || grbl == null || grbl.isDoneStreaming()) {
            return "00:00:00";
        } else {
            double timeElapsed = (double)(System.currentTimeMillis() - Main.startTime) / 1000;
            double timePerProgress = timeElapsed / grbl.currentProgress;
            double progressRemaining = grbl.totalProgress - grbl.currentProgress;
            double timeRemaining = (timePerProgress * progressRemaining);
    
            grbl.timeRemainingHistory.add(0, timeRemaining);
            if (grbl.timeRemainingHistory.size() > TIME_REMAINING_HISTORY_COUNT) {
                grbl.timeRemainingHistory.remove(TIME_REMAINING_HISTORY_COUNT);
            }
            
            double averageTimeRemaining = 0.0;
            for (double timeRemainingHistory : grbl.timeRemainingHistory) {
                averageTimeRemaining += timeRemainingHistory;
            }
            averageTimeRemaining /= grbl.timeRemainingHistory.size();
            
            long hours = (long) (averageTimeRemaining / 3600);
            long minutes = (long) ((averageTimeRemaining % 3600) / 60);
            long seconds = (long) (averageTimeRemaining % 60);
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
    
    /**
     * Returns the x coordinate status field for grbl.
     *
     * @return The x coordinate status field for grbl.
     */
    public double getCoordinateX()
    {
        return x;
    }
    
    /**
     * Returns the y coordinate status field for grbl.
     *
     * @return The y coordinate status field for grbl.
     */
    public double getCoordinateY()
    {
        return y;
    }
    
    /**
     * Returns the z coordinate status field for grbl.
     *
     * @return The z coordinate status field for grbl.
     */
    public double getCoordinateZ()
    {
        return z;
    }
    
    /**
     * Returns the status field for grbl.
     *
     * @return The status field for grbl.
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * Returns whether grbl is done streaming or not.
     *
     * @return Whether grbl is done streaming or not.
     */
    public boolean isDoneStreaming()
    {
        return doneStreaming;
    }
    
    
    //Setters
    
    /**
     * Sets the x coordinate status field for grbl.
     *
     * @param x The x coordinate status field for grbl.
     */
    private void setX(double x)
    {
        this.x = x;
    }
    
    /**
     * Sets the y coordinate status field for grbl.
     *
     * @param y The y coordinate status field for grbl.
     */
    private void setY(double y)
    {
        this.y = y;
    }
    
    /**
     * Sets the z coordinate status field for grbl.
     *
     * @param z The z coordinate status field for grbl.
     */
    private void setZ(double z)
    {
        this.z = z;
    }
    
    /**
     * Sets the status field for grbl.
     *
     * @param status The status field for grbl.
     */
    private void setStatus(String status)
    {
        this.status = status;
    }
    
    /**
     * Resets the flag which indicates whether grbl is done streaming or not.
     */
    public void resetStreaming()
    {
        this.doneStreaming = false;
    }
    
}
