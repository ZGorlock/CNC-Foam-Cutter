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
import renderer.Renderer;
import utils.*;

import java.io.*;
import java.util.*;

/**
 * Facilitates the grbl  process.
 */
public class APIgrbl extends Thread
{
    
    //Constants
    
    /**
     * The default number of recent time remaining calculations to store.
     */
    public static final int DEFAULT_TIME_REMAINING_HISTORY_COUNT = 5;
    
    
    //Static Fields
    
    /**
     * The instance of the grbl processor.
     */
    public static APIgrbl grbl;
    
    /**
     * The number of recent time remaining calculations to store.
     */
    public static int timeRemainingHistoryCount;
    
    /**
     * A flag indicating whether the adjusted gcode moves outside of the bounds of the machine or not.
     */
    public static boolean outOfBounds = false;
    
    
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
    private final List<Double> timeRemainingHistory = new ArrayList<>();
    
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

    /**
     * Variable to check if we've started streaming
     */
    private boolean startedStreaming = false;
    
    
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
            
            return adjustGcode();
            
        } else {
            commands.add("G21"); //set units to millimeters
            commands.add("G91"); //use relative positioning
    
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
    
                //commands.add("G0 Y" + String.valueOf(ModelController.maxYTravelHotwire - (Renderer.foamHeight * Renderer.MILLIMETERS_IN_INCH)));
                commands.addAll(m.getCommands());
                //commands.add("G28 X Y"); //TODO this need to be checked
                commands.add("G0 Z10.0"); //TODO + String.format("%.3f", (RotationController.controller.rotationStep / RotationController.minimumRotationDegree * RotationController.millimetersPerStep)));
                totalProgress += GcodeProgressCalculator.calculateFileProgressUnits(commands);
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
            startedStreaming = true;
            while (i < commands.size()) {

                // Wait for machine to finish
//                if(MachineDetector.isCncMachine())
//                {
                    while (getStatus().equals("Run")) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignore) {
                        }
                        queryStatus();
                    }
//                }
                // read every 127 characters and create a file with them for stream.py to use
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
                StringBuilder sb = new StringBuilder();
        
                int charsUsed = 0;

                if (profileImages.containsKey(i)) {
                    ModelController.setCurrentProfileImage(profileImages.get(i));

                    File profileGcode = new File(RotationController.controller.gcodeTraceFileMap.get(profileImages.get(i)));
                            Platform.runLater(() -> ModelController.setFileName(profileGcode.getName()));
                            Platform.runLater(() -> ModelController.setFileSize(ModelController.calculateFileSize(profileGcode)));
                }

                if (Main.development && Main.bypassArduinoForTracer && MachineDetector.isCncMachine()) {
                    TracerGcodeBypass.traceGcodeCommand(commands.get(i), true);
                }
//                packetProgressUnits += GcodeProgressCalculator.calculateInstructionProgressUnits(commands.get(i));
        
                // create string to be printed
                String gcode = commands.get(i++) + '\n';
                System.out.println(gcode);
                bw.write(gcode);
                bw.close();
                
                currentProgress++;
        
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
    
                while (commandsFromUI.size() > 0) {
                    //create new process and add to the response for UI
                    handleRequest();
                }

                // execute stream.py with the file created, get input stream as a response
                Process process = null;
                while (process == null) {
                    process = CmdLine.executeCmdAsThread("python " + Constants.GRBL_DIRECTORY + "stream.py " + Constants.GRBL_TEMP_DIRECTORY + tempFile.getName() +"\n");
                    if (process != null) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
    
                        String line = "";
                        while (line.isEmpty()) {
                            line = r.readLine();
        
                            if (!line.isEmpty()) {
                                if (Main.development && Main.developmentLogging) {
                                    System.out.println(line);
                                }
                                //updateCoordinates(line); todo
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
        startedStreaming = false;
    }
    
    /**
     * Adjusts the gcode for the model.
     *
     * @return Whether the gcode is within the bounds of the machine or not.
     */
    public boolean adjustGcode()
    {
        outOfBounds = false;

        double xAdjustment = Renderer.xAdjustment;
        double yAdjustment = Renderer.yAdjustment;
        double zAdjustment = Renderer.zAdjustment;

        double xMax = 0;
        double yMax = 0;
        double zMax = 0;

        boolean absolute = false;

        for (int i = 0; i < commands.size(); i++) {
            String command = commands.get(i);

            List<String> tokens = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(command);
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }

            if (tokens.size() > 0) {
                if (tokens.get(0).equals("G90")) {
                    absolute = true;
                }

                if (tokens.get(0).equals("G1") || tokens.get(0).equals("G0")) {

                    double x = -1;
                    double y = -1;
                    double z = -1;
                    double f = -1;

                    try {
                        for (String token : tokens) {
                            if (token.startsWith("X")) {
                                x = Double.parseDouble(token.substring(1)) + xAdjustment;
                            } else if (token.startsWith("Y")) {
                                y = Double.parseDouble(token.substring(1)) + yAdjustment;
                            } else if (token.startsWith("Z")) {
                                z = Double.parseDouble(token.substring(1)) + zAdjustment;
                            } else if (token.startsWith("F")) {
                                f = Double.parseDouble(token.substring(1));
                            }
                        }

                        if (Math.abs(x) > xMax) {
                            xMax = Math.abs(x);
                        }
                        if (Math.abs(y) > yMax) {
                            yMax = Math.abs(y);
                        }
                        if (z > zMax) {
                            zMax = z;
                        }

                        StringBuilder newCommand = new StringBuilder(tokens.get(0)).append(" ");
                        if (x > -1) {
                            newCommand.append(String.format("X%.3f ", x));
                        }
                        if (y > -1) {
                            newCommand.append(String.format("Y%.3f ", y));
                        }
                        if (z > -1) {
                            newCommand.append(String.format("Z%.3f ", z));
                        }
                        if (f > -1) {
                            newCommand.append(String.format("F%.3f ", f));
                        }
                        commands.set(i, newCommand.toString());

                    } catch (NumberFormatException e) {
                        System.err.println("Error making adjustments to gcode instruction: " + command + ". Number is not formatted properly!");
                        SystemNotificationController.throwNotification("There was an error adjusting the gcode to fit the machine!", true, false);
                        return false;
                    }
                }
            }
        }

        if (absolute) {
            if (xMax > ModelController.maxXTravelCnc / 2.0 || yMax > ModelController.maxYTravelCnc / 2.0 || zMax > ModelController.maxZTravelCnc) {
                String travelMessage = String.format("The maximum travel distance is: +/- %.1f x, +/- %.1f y, 0->%.1f z\nBut your path takes you to: +/- %.1f x, +/- %.1f y, + %.1f z\nWhich is out of the bounds of the machine! Please adjust your model!", ModelController.maxXTravelCnc / 2.0, ModelController.maxYTravelCnc / 2.0, ModelController.maxZTravelCnc / 1.0, xMax, yMax, zMax);
                System.err.println("The path takes the machine out if its bounds!");
                SystemNotificationController.throwNotification(travelMessage, true, false, 400);
                outOfBounds = true;
                return false;
            }
        } else {
            String travelMessage = String.format("The maximum travel distance is: +/- %.1f x, +/- %.1f y, + %.1f z\nYour path is in relative coordinates!\nIt cannot be verified to be within the travel distance of the machine!\nProceed carefully!", ModelController.maxXTravelCnc / 2.0, ModelController.maxYTravelCnc / 2.0, ModelController.maxZTravelCnc / 1.0);
            SystemNotificationController.throwNotification(travelMessage, false, false, 400);
        }
        
        return true;
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
                if (!startedStreaming) {
                    while (commandsFromUI.size() > 0) {
                        //create new process and add to the response for UI
                        handleRequest();
                        
                        if (startedStreaming) {
                            break;
                        }
                    }
                }
            }
        };
        commandCheckingTimer = new Timer();
        commandCheckingTimer.scheduleAtFixedRate(checkCommands, 0, 100);
    }
    
    /**
     * Performs a user entered command.
     */
    private synchronized void handleRequest()
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
            //write the command to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCommand));
            bw.write(commandsFromUI.get(0));
            bw.close();
            
            if (Main.development && Main.developmentLogging) {
                System.out.println("> " + commandsFromUI.get(0));
            }
            
            // execute stream.py with the command being sent, get input stream as a response
            Process process = null;
            while (process == null) {
                process = CmdLine.executeCmdAsThread("python " + Constants.GRBL_DIRECTORY + "stream.py " + Constants.GRBL_TEMP_DIRECTORY + tempCommand.getName() + "\n");
                if (process != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
                    String line;
                    while (true) {
                        line = r.readLine();
                        if (line == null || line.isEmpty()) {
                            break;
                        }
                        
                        if (Main.development && Main.developmentLogging) {
                            System.out.println(line);
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
            System.err.println("There was an error writing a user entered command or reading grbl's response!");
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

        if (decomposed.length != 7) {
            return;
        }

        setStatus(decomposed[0].substring(1));
        String[] first = decomposed[1].split(":");
        setX(Double.parseDouble(first[1]));
        setY(Double.parseDouble(decomposed[2]));
        setZ(Double.parseDouble(decomposed[3]));

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

    private void queryStatus()
    {
        File tempDirectory = new File(Constants.GRBL_TEMP_DIRECTORY);
        File tempCommand = new File(tempDirectory, "tempQuery.txt");
        if (!tempDirectory.exists() || !tempCommand.exists()) {
            try {
                tempDirectory.mkdir();
                tempCommand.createNewFile();
            } catch (IOException e) {
                System.err.println("There was an error creating tempQuery.txt used for streaming instructions!");
                e.printStackTrace();
                SystemNotificationController.throwNotification("There was an error in grbl's status when querying it!", false, false);
                return;
            }
        }

        try {
            //write the command to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCommand));
            bw.write("?\n");
            bw.close();

            // execute stream.py with the command being sent, get input stream as a response
            Process process = null;
            while (process == null) {
                process = CmdLine.executeCmdAsThread("python " + Constants.GRBL_DIRECTORY + "stream.py " + Constants.GRBL_TEMP_DIRECTORY + tempCommand.getName() + "\n");
                if (process != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line;
                    while (true) {
                        line = r.readLine();
                        if (line == null || line.isEmpty()) {
                            break;
                        }

                        //  Parse line into coordinates
                        String[] decomposed = line.split(",");

                        if (decomposed.length != 7) {
                            return;
                        }

                        setStatus(decomposed[0].substring(1));

                        if (Main.development && Main.developmentLogging) {
                            System.out.println(line + " Status Query");
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
        } catch (IOException e) {
            System.err.println("There was an error writing a user entered command or reading grbl's response!");
            e.printStackTrace();
            SystemNotificationController.throwNotification("There was an error executing your command!", false, false);
            return;
        }
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
        if (grbl == null || grbl.totalProgress == 0 || grbl.currentProgress == 0) {
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
            if (grbl.timeRemainingHistory.size() > timeRemainingHistoryCount) {
                grbl.timeRemainingHistory.remove(timeRemainingHistoryCount);
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
