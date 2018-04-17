/*
 * File:    ConfigReader.java
 * Package: utils
 * Author:  Zachary Gill
 */

package utils;

import grbl.APIgrbl;
import gui.interfaces.main.GcodeController;
import gui.interfaces.main.ModelController;
import gui.interfaces.main.RotationController;
import tracer.Tracer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads and stores the configurations for the application.
 */
public class ConfigurationManager
{
    
    //Constants
    
    /**
     * The configuration file.
     */
    public static final String configFile = "configuration" + File.separator + "config.ini";
    
    
    //Static Fields
    
    /**
     * The map of settings.
     */
    public static final Map<String, String> settings = new HashMap<>();
    
    
    //Static Methods
    
    /**
     * Loads the settings file.
     */
    public static void loadSettings()
    {
        File config = new File(configFile);
        if (!config.exists()) {
            System.err.println("Could not read config.ini! File does not exist!");
            return;
        }
    
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(config.getAbsolutePath()));
        } catch (IOException e) {
            System.err.println("Could not read config.ini!");
            return;
        }
        
        Pattern pattern = Pattern.compile("(?<name>.*)\\s*=\\s*(?<value>.*)");
        for (String line : lines) {
            Matcher m = pattern.matcher(line);
            if (m.matches()) {
                String name = m.group("name");
                String value = m.group("value");
                settings.put(name, value);
            }
        }
        
        setSettings();
    }
    
    /**
     * Sets the settings in their respective classes.
     */
    public static void setSettings()
    {
        MachineDetector.cncArduino = getConfigAsString("CNC_ARDUINO", MachineDetector.DEFAULT_CNC_ARDUINO);
        MachineDetector.hotwireArduino = getConfigAsString("HOTWIRE_ARDUINO", MachineDetector.DEFAULT_HOTWIRE_ARDUINO);
    
        ModelController.maxWidthCnc = getConfigAsDouble("MAX_WIDTH_CNC", ModelController.DEFAULT_MAX_WIDTH_CNC);
        ModelController.maxLengthCnc = getConfigAsDouble("MAX_LENGTH_CNC", ModelController.DEFAULT_MAX_LENGTH_CNC);
        ModelController.maxHeightCnc = getConfigAsDouble("MAX_HEIGHT_CNC", ModelController.DEFAULT_MAX_HEIGHT_CNC);
        ModelController.maxWidthHotwire = getConfigAsDouble("MAX_WIDTH_HOTWIRE", ModelController.DEFAULT_MAX_WIDTH_HOTWIRE);
        ModelController.maxLengthHotwire = getConfigAsDouble("MAX_LENGTH_HOTWIRE", ModelController.DEFAULT_MAX_LENGTH_HOTWIRE);
        ModelController.maxHeightHotwire = getConfigAsDouble("MAX_HEIGHT_HOTWIRE", ModelController.DEFAULT_MAX_HEIGHT_HOTWIRE);
    
        ModelController.maxXTravelCnc = getConfigAsInt("MAX_X_TRAVEL_CNC", ModelController.DEFAULT_MAX_X_TRAVEL_CNC);
        ModelController.maxYTravelCnc = getConfigAsInt("MAX_Y_TRAVEL_CNC", ModelController.DEFAULT_MAX_Y_TRAVEL_CNC);
        ModelController.maxZTravelCnc = getConfigAsInt("MAX_Z_TRAVEL_CNC", ModelController.DEFAULT_MAX_Z_TRAVEL_CNC);
        ModelController.maxXTravelHotwire = getConfigAsInt("MAX_X_TRAVEL_CNC", ModelController.DEFAULT_MAX_X_TRAVEL_HOTWIRE);
        ModelController.maxYTravelHotwire = getConfigAsInt("MAX_X_TRAVEL_CNC", ModelController.DEFAULT_MAX_Y_TRAVEL_HOTWIRE);
        
        RotationController.minimumRotationDegree = getConfigAsDouble("MIN_ROTATION_DEGREE", RotationController.DEFAULT_MIN_ROTATION_DEGREE);
//       TODO RotationController.millimetersPerStep = getConfigAsDouble("MILLIMETERS_PER_STEP", RotationController.DEFAULT_MILLIMETERS_PER_STEP);
    
        APIgrbl.timeRemainingHistoryCount = getConfigAsInt("TIME_REMAINING_HISTORY_COUNT", APIgrbl.DEFAULT_TIME_REMAINING_HISTORY_COUNT);
        
        GcodeController.maxCodeHistory = getConfigAsInt("MAX_CODE_HISTORY", GcodeController.DEFAULT_MAX_CODE_HISTORY);
        Tracer.maxTraces = getConfigAsInt("MAX_TRACES", Tracer.DEFAULT_MAX_TRACES);
    }
    
    /**
     * Gets a configuration value as a string.
     *
     * @param name The name of the configuration.
     * @param def  The default value of the configuration.
     * @return The configuration value.
     */
    public static String getConfigAsString(String name, String def)
    {
        return settings.getOrDefault(name, def);
    }
    
    /**
     * Gets a configuration value as an int.
     *
     * @param name The name of the configuration.
     * @param def  The default value of the configuration.
     * @return The configuration value.
     */
    public static int getConfigAsInt(String name, int def)
    {
        if (settings.containsKey(name)) {
            return Integer.parseInt(settings.get(name));
        } else {
            return def;
        }
    }
    
    /**
     * Gets a configuration value as a double.
     *
     * @param name The name of the configuration.
     * @param def  The default value of the configuration.
     * @return The configuration value.
     */
    public static double getConfigAsDouble(String name, double def)
    {
        if (settings.containsKey(name)) {
            return Double.parseDouble(settings.get(name));
        } else {
            return def;
        }
    }
    
}
