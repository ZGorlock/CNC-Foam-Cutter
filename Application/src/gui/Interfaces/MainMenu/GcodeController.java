package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Interfaces.Greeting.GreetingController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
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

public class GcodeController {

    // FXML variables
    public TextField textFieldCommand;
    public TextArea textAreaResponse;
    public TextArea textAreaCodeSent;
    public VBox vBox;

    // UI-dependent variables
    public static List<String> commandBlock = new ArrayList<>();
    public static String commandBlockText = "";

    public static List<String> codeBlock = new ArrayList<>();
    public static String codeBlockText = "";

    // This
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
    
    public void initialize()
    {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(GreetingController.getModel());
        controller = this;
        for(String str : fileNames)
        {
            File file = new File(str);
            slice(file);
            APIgrbl apIgrbl = new APIgrbl("can.gcode"); //todo change to file.getName() instead of can.gcode
            new Thread(apIgrbl).start();     //<--- comment out if no arduino TODO
        }

        textFieldCommand.setPromptText("Send Command...");

        textAreaResponse = new TextArea();
        textAreaCodeSent = new TextArea();

        vBox.getChildren().add(0,textAreaResponse);
        vBox.getChildren().add(0,textAreaCodeSent);

        updateUI();
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
        if(textFieldCommand.getText().compareTo(textFieldCommand.getPromptText()) == 0){ return; }
        String userCommand = textFieldCommand.getText();
        APIgrbl.grbl.sendRequest(userCommand);
        commandBlock.add('>'+ userCommand);
    }

    private void updateUI()
    {
        updateUICommand();
        updateUICodeCurrentlyProcessed();
    }

    private void updateUICodeCurrentlyProcessed()
    {
        TimerTask updateUICodeSent = new TimerTask() {
            private int state;

            @Override
            public void run() {
                if (codeBlock.size() != state)
                {
                    state = codeBlock.size();
                    codeBlockText = "";

                    for (int i = 0; i < 10; i++) {
                        codeBlockText += System.lineSeparator();
                    }

                    for (String code : codeBlock) {
                        codeBlockText += code + System.lineSeparator();
                    }
                    textAreaCodeSent.textProperty().set(codeBlockText);
                    textAreaCodeSent.setScrollTop(1000000);
                }
            }
        };
        Timer t2 = new Timer();
        t2.scheduleAtFixedRate(updateUICodeSent,0, 100);
    }

    private void updateUICommand()
    {
        TimerTask updateUI = new TimerTask() {
            private int l;

            @Override
            public void run() {
                if (commandBlock.size() != l)
                {
                    l = commandBlock.size();
                    commandBlockText = "";

                    for (int i = 0; i < 10; i++) {
                        commandBlockText += System.lineSeparator();
                    }

                    for (String command : commandBlock) {
                        commandBlockText += command + System.lineSeparator();
                    }
                    textAreaResponse.textProperty().set(commandBlockText);
                    textAreaResponse.setScrollTop(1000000);
                }
            }
        };
        Timer t = new Timer();
        t.scheduleAtFixedRate(updateUI,0, 100);
    }

}
