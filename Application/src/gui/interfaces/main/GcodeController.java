/*
 * File:    GcodeController.java
 * Package: gui.interfaces.main
 * Author:  Nicolas Lopez
 */

package gui.interfaces.main;

import grbl.APIgrbl;
import gui.interfaces.greeting.GreetingController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import main.Main;
import slicer.Slicer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The controller for the gcode tab.
 */
public class GcodeController
{
    
    //FXML Fields
    
    /**
     * The grbl  user command input field.
     */
    public TextField textFieldCommand;
    
    /**
     * The text area for the responses to user commands.
     */
    public TextArea textAreaResponse;
    
    /**
     * The text area for the gcode that has been streamed to the Arduino.
     */
    public TextArea textAreaCodeSent;
    
    /**
     * The Send button.
     */
    public Button sendButton;
    
    
    //Constants
    
    /**
     * The maximum history to display in the gcode window.
     */
    public static final int MAX_CODE_HISTORY = 500;
    
    
    //Static Fields
    
    /**
     * The instance of the controller.
     */
    public static GcodeController controller;
    
    /**
     * The gcode file.
     */
    public static String gcodeFile;
    
    /**
     * The list of user commands and responses.
     */
    public static List<String> commandBlock = new ArrayList<>();
    
    /**
     * The string buffer for the user command text area.
     */
    public static String commandBlockText = "";
    
    /**
     * The list of gcode commands that have been streamed.
     */
    public static List<String> codeBlock = new ArrayList<>();
    
    /**
     * The string buffer for the gcode command text area.
     */
    public static String codeBlockText = "";
    
    
    //Fields
    
    /**
     * The timer for updating the code block.
     */
    public Timer codeUpdateTimer;
    
    /**
     * The timer for updating the command block.
     */
    public Timer commandUpdateTimer;
    
    
    //Constructors
    
    /**
     * Loads the G-code tab.
     *
     * @return The G-code tab, or null if there was an error.
     */
    public static Tab setup()
    {
        try {
            URL fxml = GcodeController.class.getResource("G-code.fxml");
            Tab tab = FXMLLoader.load(fxml);
            
            return tab;
            
        } catch (Exception e) {
            System.err.println("There was an error loading G-code.fxml!");
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    /**
     * Initializes the controller.
     */
    public void initialize()
    {
        controller = this;
        
        textFieldCommand.setPromptText("Send Command...");
        textFieldCommand.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.compareTo(event.getCode()) == 0) {
                sendCommand(new ActionEvent());
            }
        });
        updateUI();
    }
    
    /**
     * Handles a user entered gcode command.
     *
     * @param actionEvent The event that triggered the handler.
     */
    public void sendCommand(ActionEvent actionEvent)
    {
        if (textFieldCommand.getText().equals(textFieldCommand.getPromptText()) || textFieldCommand.getText().isEmpty() || APIgrbl.grbl == null) {
            return;
        }
        
        String userCommand = textFieldCommand.getText();
        APIgrbl.grbl.sendRequest(userCommand);
        commandBlock.add('>' + userCommand);
        textFieldCommand.clear();
    }
    
    /**
     * Updates the UI.
     */
    private void updateUI()
    {
        updateUICommand();
        updateUICodeCurrentlyProcessed();
    }
    
    /**
     * Updates the gcode streaming history.
     */
    private void updateUICodeCurrentlyProcessed()
    {
        TimerTask updateUICodeSent = new TimerTask()
        {
            private int state;
            
            @Override
            public void run()
            {
                if (codeBlock.size() != state) {
                    state = codeBlock.size();
                    
                    StringBuilder codeBlockTextBuilder = new StringBuilder();
                    double height = textAreaCodeSent.getHeight() / (textAreaCodeSent.getFont().getSize() * 2);
                    
                    for (int i = 0; i < height - codeBlock.size() + 1; i++) {
                        codeBlockTextBuilder.append(System.lineSeparator());
                    }
                    for (int i = (codeBlock.size() > MAX_CODE_HISTORY ? codeBlock.size() - MAX_CODE_HISTORY : 0); i < codeBlock.size(); i++) {
                        if (i == codeBlock.size() - 1) {
                            codeBlockTextBuilder.append(codeBlock.get(i).substring(0, codeBlock.get(i).length() - 1));
                        } else {
                            codeBlockTextBuilder.append(codeBlock.get(i));
                        }
                    }
                    
                    codeBlockText = codeBlockTextBuilder.toString();
                    textAreaCodeSent.textProperty().set(codeBlockText);
                    textAreaCodeSent.appendText("");
                    textAreaCodeSent.setScrollTop(Double.MAX_VALUE);
                }
            }
        };
        codeUpdateTimer = new Timer();
        codeUpdateTimer.scheduleAtFixedRate(updateUICodeSent, 0, 250);
    }
    
    /**
     * Updates the user entered gcode command history.
     */
    private void updateUICommand()
    {
        TimerTask updateUI = new TimerTask()
        {
            private int l;
            
            @Override
            public void run()
            {
                if (commandBlock.size() != l) {
                    l = commandBlock.size();

                    StringBuilder commandBlockTextBuilder = new StringBuilder();
                    double height = textAreaResponse.getHeight() / (textAreaResponse.getFont().getSize() * 2);

                    for (int i = 0; i < height - commandBlock.size() + 1; i++) {
                        commandBlockTextBuilder.append(System.lineSeparator());
                    }
                    for (int i = (commandBlock.size() > MAX_CODE_HISTORY ? commandBlock.size() - MAX_CODE_HISTORY : 0); i < commandBlock.size(); i++) {
                        commandBlockTextBuilder.append(commandBlock.get(i));
                        if (i != commandBlock.size() - 1) {
                            commandBlockTextBuilder.append(System.lineSeparator());
                        }
                    }

                    commandBlockText = commandBlockTextBuilder.toString();
                    textAreaResponse.textProperty().set(commandBlockText);
                    textAreaResponse.appendText("");
                    textAreaResponse.setScrollTop(Double.MAX_VALUE);
                }
            }
        };
        commandUpdateTimer = new Timer();
        commandUpdateTimer.scheduleAtFixedRate(updateUI, 0, 100);
    }
    
    /**
     * Resets the controller.
     */
    public void reset()
    {
        if (codeUpdateTimer != null) {
            codeUpdateTimer.purge();
            codeUpdateTimer.cancel();
        }
        if (commandUpdateTimer != null) {
            commandUpdateTimer.purge();
            commandUpdateTimer.cancel();
        }
        
        commandBlock = new ArrayList<>();
        commandBlockText = "";
        codeBlock = new ArrayList<>();
        codeBlockText = "";
    }
    
    
    //Static Methods
    
    /**
     * Starts the grbl process.
     *
     * @return Whether grbl was successfully started or not.
     */
    public static boolean startGrbl()
    {
        if (gcodeFile == null || gcodeFile.isEmpty()) {
            String gcode = GreetingController.getGcode();
            String model = GreetingController.getModel();
            if (!model.isEmpty()) {
                File file = new File(model);
                slice(file);
            } else if (!gcode.isEmpty()) {
                gcodeFile = gcode;
            } else {
                //TODO handle multiple gcode files for Hot Wire
            }
        }
        
        APIgrbl apiGrbl = new APIgrbl(gcodeFile);
        if (!apiGrbl.initialize()) {
            System.err.println("Could not set up grbl!");
            return false;
        }
        apiGrbl.start();
        
        Main.startTime = System.currentTimeMillis();
        
        return true;
    }
    
    /**
     * Slices the uploaded model into gcode.
     *
     * @param model The uploaded model file.
     */
    public static void slice(File model)
    {
        if (model.getAbsolutePath().endsWith(".gcode")) {
            gcodeFile = model.getAbsolutePath();
        }
        
        //TODO error handling
        Slicer slicer = new Slicer(model.getAbsolutePath(), Main.main.architecture);
        slicer.slice("--gcode-flavor mach3");
        
        gcodeFile = model.getAbsolutePath().substring(0, model.getAbsolutePath().indexOf('.')) + ".gcode";
    }
    
}
