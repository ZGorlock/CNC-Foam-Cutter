package gui;

import gui.Interfaces.Greeting.GreetingController;

import java.nio.file.Paths;

public class Debugger {

    private String fxml;
    private int width;
    private int height;
    boolean rotation;
    public static Debugger debugger;
    Debugger(boolean mode, boolean rotation)
    {
        if(mode)
        {
            width = 1280;
            height = 960;
            fxml = "Interfaces/MainMenu/Menu.fxml";

            String thisPath = Paths.get("").toAbsolutePath().toString();

            String gcodeVersion = "\\resources\\gcode\\Birds_and_flowers.gcode";
            String stlVersion = "\\resources\\cadfiles\\Birds_and_flowers.stl";

            GreetingController g = new GreetingController(thisPath + stlVersion);
        }else
        {
            width = 800;
            height = 600;
            fxml = "Interfaces/Greeting/Input.fxml";
        }
        debugger = this;
        this.rotation = rotation;
    }
    public boolean getRotation(){return rotation;}
    public String getFXML() {
        return fxml;
    }
    public int getWidth(){return width;}
    public int getHeight(){return height;}
}
