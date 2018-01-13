package gui.Interfaces.MainMenu;

import javafx.scene.control.Label;
import renderer.Renderer;

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
        
        // This can be generated from the file name
        //TODO these are just temporary
        setFileName("SuperAwesome.gcode");
        setDesc("This is a super awesome model");
        setFileSize("54MB");
        setPercentage("100% done");
        setStudentNid("XX99999");

        // model = new File(GreetingController.getFileNames().get(0));
        // renderer = new Renderer(model);
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
