package gui.Interfaces.MainMenu;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import main.Main;
import slicer.Slicer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
        /*
        model = new File(GreetingController.getFileNames().get(0));
        slice(model);

        APIgrbl grbl = new APIgrbl();
        grbl.start(getGcodeFile(model));
        */
    }
    
    public void slice(File model)
    {
        Slicer slicer = new Slicer(model.getAbsolutePath(), Main.architecture);
        slicer.slice("--gcode-flavor mach3");
    }
    
    private String getGcodeFile(File model)
    {
        return model.getAbsolutePath().substring(0, model.getAbsolutePath().lastIndexOf('.')) + ".gcode";
    }
    
    
}
