package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Interfaces.Greeting.GreetingController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import main.Main;
import slicer.Slicer;
import sun.misc.Request;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GcodeController {

    public TextField textFieldCommand;
    public TextArea textAreaResponse;
    public Label responseLabel;

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
        APIgrbl.grbl.sendRequest(textFieldCommand.getText());
    }

    private void updateUI()
    {
        RequestHandler request = new RequestHandler();
        responseLabel.textProperty().bind(request.messageProperty());
        new Thread(request).start();
    }
}
