package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import main.Main;
import slicer.Slicer;

import java.io.File;

public class GcodeController {
    
    
    public void init(File model)
    {
        slice(model);
        
        APIgrbl grbl = new APIgrbl();
        grbl.start(getGcodeFile(model));
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
