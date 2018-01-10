package gui.Interfaces.MainMenu;

import gui.Interfaces.Greeting.GreetingController;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class MenuController{

    public TabPane TPane;
    private File model = null;
    
    private ModelController modelController;
    private GcodeController gcodeController;
    private TraceController traceController;
    private RotationController rotationController;
    
    public void initialize()
    {
        // We assume it is the router system

        boolean isHotWire = false;
        String fxmlToLoad = "Trace.fxml";
        if(isHotWire)
        {
            fxmlToLoad = "Rotation.fxml";
        }

       // We add a tab dynamically depending on which fxml file we will load
        try {

            Tab tabThird = (FXMLLoader.load(this.getClass().getResource(fxmlToLoad)));
            TPane.getTabs().add(tabThird);

            Pane pane = (Pane)tabThird.getContent();
            SwingNode swingNode = (SwingNode) pane.getChildren().get(0);
            JPanel panel = new JPanel();
            swingNode.setContent(panel);

        }catch (IOException e)
        {
            e.printStackTrace();
        }

        /*
        model = new File(GreetingController.getFileNames().get(0));

        modelController = new ModelController();
        gcodeController = new GcodeController();
        traceController = new TraceController();
        rotationController = new RotationController();
        
        modelController.init(model);
        gcodeController.init(model);
        traceController.init();
        rotationController.init();
        */
    }
    
    
    public File getModel()
    {
        return model;
    }
    
    
    
    
}
