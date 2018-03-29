/*
 * File:    APIgrbl.java
 * Package: grbl
 * Author:  Nicolas Lopez
 */

package grbl;

import gui.interfaces.main.*;
import gui.interfaces.popup.SystemNotificationController;
import javafx.application.Platform;
import javafx.scene.image.Image;
import main.Main;
import utils.CmdLine;
import utils.Constants;
import utils.TimeUtil;

import java.io.*;
import java.util.*;

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
    private String filename = null;
    
    /**
     * The list of profiles for the hot wire machine.
     */
    private List<String> profiles = null;
    
    /**
     * The list of gcode commands.
     */
    private List<String> commands;
    
    /**
     * The map from the index of the command that starts the profile and the image of that profile.
     */
    private Map<Integer, Image> profileImages;
    
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
    
    /**
     * The timer for checking user entered commands.
     */
    private Timer commandCheckingTimer = null;

    /**
     * The field to tell if it's in inches
     */

    private boolean inches = false;

    /**
     * The field to tell if it's in millimeters
     */
    private boolean mm = false;


    //Static Fields
    
    /**
     * The instance of the grbl processor.
     */
    public static APIgrbl grbl;
    
    
    //Constructors
    
    /**
     * The default no-argument constructor for the grbl processor.
     */
    public APIgrbl()
    {
        grbl = this;
        
        setupCommandChecking();
    }
    
    /**
     * The constructor for the grbl processor.
     *
     * @param filename The filename of the gcode file to work on.
     */
    public APIgrbl(String filename)
    {
        this();
        
        this.filename = filename;
        
        this.profiles = null;
    }
    
    /**
     * The constructor for the grbl processor.
     *
     * @param profiles The list of gcode profiles to work on.
     */
    public APIgrbl(List<String> profiles)
    {
        this();
        
        this.filename = null;
        
        this.profiles = new ArrayList<>();
        this.profiles.addAll(profiles);
    }
    
    
    //Methods
    
    /**
     * Initializes the grbl processor.
     *
     * @return Whether the initialization was successful or not.
     */
    public boolean initialize()
    {
        commands = new ArrayList<>();
        profileImages = new HashMap<>();
        totalProgress = 0;
        
        if (profiles == null) {
            // Modifies to gbrl acceptable gcode
            GcodeModifier m = new GcodeModifier(filename);
            if (!m.modify()) {
                System.err.println("An error occurred while running the GcodeModifier on file: " + filename);
                return false;
            }
    
            commands = m.getCommands();
            //totalProgress = GcodeProgressCalculator.calculateFileProgressUnits(commands);
            totalProgress = commands.size();
            currentProgress = 0;
            
        } else {
            for (String profile : profiles) {
                // Modifies to gbrl acceptable gcode
                GcodeModifier m = new GcodeModifier(profile);
                if (!m.modify()) {
                    System.err.println("An error occurred while running the GcodeModifier on file: " + profile);
                    return false;
                }
                
                if (!profileImages.containsValue(RotationController.controller.gcodeTraceMap.get(profile))) {
                    profileImages.put(commands.size(), RotationController.controller.gcodeTraceMap.get(profile));
                }
                commands.addAll(m.getCommands());
                commands.add("G28 X Y"); //TODO these need to be checked
                commands.add("G1 Z0.001 F7800.000"); //TODO this too (rotation RotationController.controller.rotationStep degrees)
                //totalProgress += GcodeProgressCalculator.calculateFileProgressUnits(commands);
            }
            totalProgress = commands.size();
            currentProgress = 0;
        }
        
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
        File tempDirectory = new File(Constants.GRBL_TEMP_DIRECTORY);
        File tempFile = new File(tempDirectory, "tempfile.txt");
        if (!tempDirectory.exists() || !tempFile.exists()) {
            try {
                tempDirectory.mkdir();
                tempFile.createNewFile();
            } catch (IOException e) {
                System.err.println("There was an error creating tempfile.txt used for streaming!");
                e.printStackTrace();
                SystemNotificationController.throwNotification("There was an error streaming to the machine!", true, false);
                return;
            }
        }
        
        try {
            int i = 0;
            while (i < commands.size()) {
                
                // read every 127 characters and create a file with them for stream.py to use
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
                StringBuilder sb = new StringBuilder();
        
                double packetProgressUnits = 0;
                int charsUsed = 0;
                while (charsUsed < 127) {
    
                    if (profileImages.containsKey(i)) {
                        ModelController.setCurrentProfileImage(profileImages.get(i));
        
                        File profileGcode = new File(RotationController.controller.gcodeTraceFileMap.get(profileImages.get(i)));
                                Platform.runLater(() -> ModelController.setFileName(profileGcode.getName()));
                                Platform.runLater(() -> ModelController.setFileSize(ModelController.calculateFileSize(profileGcode)));
                    }
    
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
        
                // Check for pause/resume
                if (MenuController.paused) {
                    synchronized (this) {
                        while (MenuController.paused) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                System.err.println("APIgrbl thread failed to sleep on machine pause.");
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
                    process = CmdLine.executeCmdAsThread("py " + Constants.GRBL_DIRECTORY + "stream.py " + Constants.GRBL_TEMP_DIRECTORY + tempFile.getName() +"\n");
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
            }
        } catch (IOException e) {
            System.err.println("There was an error writing tempfile.txt during streaming!");
            e.printStackTrace();
            SystemNotificationController.controller.raise("There was an error streaming to the machine!", false, false);
            return;
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
    private void setupCommandChecking()
    {
        TimerTask checkCommands = new TimerTask()
        {
            @Override
            public void run()
            {
                // check for commands from UI
                if (commandsFromUI.size() > 0) {
                    //create new process and add to the response for UI
                    handleRequest();
                }
            }
        };
        commandCheckingTimer = new Timer();
        commandCheckingTimer.scheduleAtFixedRate(checkCommands, 0, 100);
    }
    
    /**
     * Performs a user entered command.
     */
    private void handleRequest()
    {
        File tempDirectory = new File(Constants.GRBL_TEMP_DIRECTORY);
        File tempCommand = new File(tempDirectory, "tempCommand.txt");
        if (!tempDirectory.exists() || !tempCommand.exists()) {
            try {
                tempDirectory.mkdir();
                tempCommand.createNewFile();
            } catch (IOException e) {
                System.err.println("There was an error creating tempcommand.txt used for streaming user commands!");
                e.printStackTrace();
                SystemNotificationController.throwNotification("There was an error executing your command!", false, false);
                return;
            }
        }
        
        try {
            // execute stream.py with the command being sent, get input stream as a response
            Process process = null;
            while (process == null) {
                process = CmdLine.executeCmdAsThread("py -3 " + Constants.GRBL_DIRECTORY + "stream.py " + Constants.GRBL_TEMP_DIRECTORY + tempCommand.getName() + "\n");
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
            SystemNotificationController.throwNotification("There was an error executing your command!", false, false);
            return;
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
        //  Parse line into coordinates
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

        Platform.runLater(()->{
            TraceController.controller.grblX.setText(x);
            TraceController.controller.grblY.setText(y);
            TraceController.controller.grblZ.setText(z);
            TraceController.controller.grblStatus.setText(status);
            TraceController.addTrace(getCoordinateX(), getCoordinateY(), getCoordinateZ());
        });
    }
    
    /**
     * Handles a Resume event.
     */
    public synchronized void initiateResume()
    {
        notify();
    }
    
    /**
     * Resets the controller.
     */
    public void reset()
    {
        if (commandCheckingTimer != null) {
            commandCheckingTimer.purge();
            commandCheckingTimer.cancel();
        }
        
        this.doneStreaming = false;
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
            return "0.00 %";
        } else if (grbl.isDoneStreaming()) {
            return "100.00 %";
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
        if (MenuController.stopped || MenuController.paused) {
            return ModelController.controller.timeRemaining.getText();
        }
        if (Main.startTime == 0 || grbl == null || grbl.isDoneStreaming()) {
            return "00:00:00";
        } else {
            double timeElapsed = (double)(TimeUtil.currentTimeMillis() - Main.startTime) / 1000;
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
     * Sets the filename to be used for grbl.
     *
     * @param filename The filename to be used for grbl.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }
    
    /**
     * Sets the list of profiles for grbl.
     *
     * @param profiles The list of profiles for grbl.
     */
    public void setProfiles(List<String> profiles)
    {
        this.profiles = profiles;
    }

    /**
     * Sets the measurements to millimeters
     */
    public void setMetric(){ this.mm = true; this.inches = false; }

    /**
     * Sets the measurements to inches
     */
    public void setImperial(){ this.mm = false; this.inches = true; }
}
