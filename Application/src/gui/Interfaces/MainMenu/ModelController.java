package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Interfaces.Greeting.InputController;
import javafx.scene.control.Label;
import jdk.internal.util.xml.impl.Input;
import renderer.Renderer;

import java.io.File;
import java.util.ArrayList;

import static gui.Interfaces.Greeting.GreetingController.getFileNames;

public class ModelController {

    //FXML
    public Label fileName;
    public Label fileSize;
    public Label fileDesc;
    public Label filePercent;
    public Label studentNID;
    
    public static ModelController controller;

    public static Renderer renderer;

    public void initialize()
    {
        controller = this;
        APIgrbl apIgrbl = new APIgrbl();

        ArrayList<String> fileNames = getFileNames();
        for(String str : fileNames)
        {
            File file = new File(str);
            setFileName(file.getName());
            calculateFileSize(file);
            setStudentNid(InputController.getNidFromText());
            setDesc(InputController.getDescFromText());
        }

        setPercentage(Double.toString(APIgrbl.grbl.getPercentage()) + "%");

        // model = new File(GreetingController.getFileNames().get(0));
        // renderer = new Renderer(model);
    }

    public static void calculateFileSize(File file)
    {
        double size = file.length();
        int i = 0;

        while(size / 100 > 10)
        {
            size /= 1024;
            i++;
        }
        String bytes = "";
        switch(i){
            case 0:
                bytes = "B";
                break;
            case 1:
                bytes = "KB";
                break;
            case 2:
                bytes = "MB";
                break;
            default: bytes = "B";
                break;
        }
        setFileSize(Double.toString(size) + bytes);
    }
    
    public static void setFileName(String fileName) {
        controller.fileName.setText(fileName);
    }
    
    public static void setDesc(String desc) {
        controller.fileDesc.setText(desc);
    }
    
    public static void setFileSize(String fileSize) {
        controller.fileSize.setText(fileSize);
    }
    
    public static void setPercentage(String percentage) {
        controller.filePercent.setText(percentage);
    }
    
    public static void setStudentNid(String studentNid) {
        controller.studentNID.setText(studentNid);
    }
    
    

    
}
