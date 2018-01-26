package gui;

import gui.Interfaces.Greeting.GreetingController;

public class Debugger {

    private String fxml;
    private int width;
    private int height;
    Debugger(boolean mode)
    {
        if(mode)
        {
            width = 1280;
            height = 960;
            fxml = "Interfaces/MainMenu/Menu.fxml";
            GreetingController g = new GreetingController("resources/cadfiles/Birds_and_flowers.stl");
        }else{
            width = 800;
            height = 600;
            fxml = "Interfaces/Greeting/Input.fxml";
        }

    }
    public String getFXML() {
        return fxml;
    }
    public int getWidth(){return width;}
    public int getHeight(){return height;}
}
