package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Interfaces.Greeting.GreetingController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import main.Main;
import slicer.Slicer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GcodeController {

    public TextField textFieldCommand;
    public TextArea textAreaResponse;
    private ArrayList<String> requests;

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

        for(String str : fileNames)
        {
            File file = new File(str);
            slice(file);
            APIgrbl apIgrbl = new APIgrbl("can.gcode"); //todo change to file.getName() instead of can.gcode
            new Thread(apIgrbl).start();     //<--- comment out if no arduino TODO
        }

        textFieldCommand.setPromptText("Send Command...");
        requests = new ArrayList<>();
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
        if(textFieldCommand.getText() == textFieldCommand.getPromptText())
        {
            return;
        }

        RequestHandler request = new RequestHandler(textFieldCommand.getText());
        textAreaResponse.textProperty().bind(request.messageProperty());
        new Thread(request).start();
    }
}
