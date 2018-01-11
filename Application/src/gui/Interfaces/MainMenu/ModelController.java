package gui.Interfaces.MainMenu;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import renderer.Renderer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ModelController {

    //FXML
    public Label fileName;
    public Label fileSize;
    public Label fileDesc;
    public Label filePercent;
    public Label studentNID;

    public static Renderer renderer;

    public void initialize()
    {
        // This can be generated from the file name
        fileName.setText("SuperAwesome.gcode");
        fileDesc.setText("This is a super awesome model");
        fileSize.setText("54MB");
        filePercent.setText("100% done");
        studentNID.setText("XX99999");

        // model = new File(GreetingController.getFileNames().get(0));
        // renderer = new Renderer(model);
    }

    
}
