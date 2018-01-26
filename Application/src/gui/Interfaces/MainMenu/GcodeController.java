package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Interfaces.Greeting.GreetingController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import main.Main;
import slicer.Slicer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GcodeController {
    
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
            APIgrbl apIgrbl = new APIgrbl(getGcodeFile(file));
            //new Thread(apIgrbl).start();     //<--- comment out if no arduino TODO
        }
    }
    
    public void slice(File model)
    {
        if (model.getAbsolutePath().endsWith(".gcode")) {
            return;
        }
        
        Slicer slicer = new Slicer(model.getAbsolutePath(), Main.main.architecture);
        slicer.slice("--gcode-flavor mach3");
    }
    
    private String getGcodeFile(File model)
    {
        return model.getAbsolutePath().substring(0, model.getAbsolutePath().lastIndexOf('.')) + ".gcode";
    }
}
