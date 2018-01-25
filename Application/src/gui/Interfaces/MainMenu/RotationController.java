package gui.Interfaces.MainMenu;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.net.URL;

/**
 * The controller for the Rotation tab.
 */
public class RotationController
{
    
    // FXML
    
    /**
     * The FXML elements of the tab.
     */
    public ScrollBar scrollBar;

    
    //Static Fields
    
    /**
     * The singleton instance fo the RotationController.
     */
    public static RotationController controller;
    
    
    //Static Methods
    
    /**
     * Creates the Rotation tab.
     *
     * @return The Rotation tab.
     */
    public static Tab setup() {
        try {
            URL fxml = RotationController.class.getResource("Rotation.fxml");
            Tab tab = FXMLLoader.load(fxml);
            
            return tab;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    /**
     * Initializes the RotationController.
     */
    public void initialize()
    {
    
    }
    
}
