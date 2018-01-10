package gui.Interfaces.MainMenu;

import gui.Interfaces.Greeting.GreetingController;

import java.io.File;

public class MenuController {
    
    
    private File model = null;
    
    private ModelController modelController;
    private GcodeController gcodeController;
    private TraceController traceController;
    private RotationController rotationController;
    
    
    public void init()
    {
        model = new File(GreetingController.getFileNames().get(0));
        
        modelController = new ModelController();
        gcodeController = new GcodeController();
        traceController = new TraceController();
        rotationController = new RotationController();
        
        modelController.init(model);
        gcodeController.init(model);
        traceController.init();
        rotationController.init();
    }
    
    
    public File getModel()
    {
        return model;
    }
    
    
    
    
}
