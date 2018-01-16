package gui.Interfaces.MainMenu;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

import java.io.IOException;

public class RotationController {
    
    public void initialize()
    {
    
    }
    
    public static Tab setup() {
        try {
            return (FXMLLoader.load(RotationController.class.getResource("Rotation.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
