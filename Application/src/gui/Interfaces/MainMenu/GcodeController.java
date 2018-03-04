package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Interfaces.Greeting.GreetingController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import main.Main;
import slicer.Slicer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The controller for the Gcode tab.
 */
public class GcodeController
{
    
    // FXML variables
    public TextField textFieldCommand;
    public TextArea textAreaResponse;
    public TextArea textAreaCodeSent;
    public VBox vBox;
    public Button sendButton;
    
    // UI-dependent variables
    public static List<String> commandBlock = new ArrayList<>();
    public static String commandBlockText = "";
    
    public static List<String> codeBlock = new ArrayList<>();
    public static String codeBlockText = "";
    
    // This instance
    public static GcodeController controller;
    
    
    public static Tab setup()
    {
        try {
            URL fxml = GcodeController.class.getResource("G-code.fxml");
            Tab tab = FXMLLoader.load(fxml);
            
            return tab;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void resetUI()
    {
        commandBlock = new ArrayList<>();
        commandBlockText = "";
        codeBlock = new ArrayList<>();
        codeBlockText = "";
    }
    
    public void initialize()
    {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(GreetingController.getModel());
        controller = this;
        for (String str : fileNames) {
            File file = new File(str);
            slice(file);
            
        }
        
        textFieldCommand.setPromptText("Send Command...");
        textFieldCommand.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.compareTo(event.getCode()) == 0) {
                sendCommand(new ActionEvent());
            }
        });
        updateUI();
    }
    
    public static void startGrbl()
    {
        String file = "can.gcode";
        APIgrbl apIgrbl = new APIgrbl(file);
        
        Main.startTime = System.currentTimeMillis();
    }
    
    public void slice(File model)
    {
        if (model.getAbsolutePath().endsWith(".gcode")) {
            return;
        }
        
        Slicer slicer = new Slicer(model.getAbsolutePath(), Main.main.architecture);
        slicer.slice("--gcode-flavor mach3");
    }
    
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
    
    private void updateUI()
    {
        updateUICommand();
        updateUICodeCurrentlyProcessed();
    }
    
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
                    for (int i = 0; i < codeBlock.size(); i++) {
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
        Timer t2 = new Timer();
        t2.scheduleAtFixedRate(updateUICodeSent, 0, 100);
    }
    
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
                    for (int i = 0; i < commandBlock.size(); i++) {
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
        Timer t = new Timer();
        t.scheduleAtFixedRate(updateUI, 0, 100);
    }
    
}
