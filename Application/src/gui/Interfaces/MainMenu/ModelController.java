package gui.Interfaces.MainMenu;

import renderer.Renderer;

import java.io.File;

public class ModelController {
    
    public static Renderer renderer;
    
    public void init(File model)
    {
        renderer = new Renderer(model);
    }
    
}
