package gui.Interfaces.MainMenu;

import javafx.fxml.FXMLLoader;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import renderer.Renderer;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class ModelController {
    
    //TODO comments
    
    //FXML
    
    public Label fileName;
    public Label fileSize;
    public Label fileDesc;
    public Label filePercentage;
    public Label studentNID;
    public SubScene subSceneRenderer;
    
    
    //Static Fields
    
    public static ModelController controller;
    public static Renderer renderer;
    
    
    //Static Methods
    
    public static Tab setup()
    {
        try {
            URL fxml = ModelController.class.getResource("Model.fxml");
            Tab tab = (Tab) (FXMLLoader.load(fxml));
            
            return tab;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    public void initialize()
    {
        controller = this;
        renderer = Renderer.setup(subSceneRenderer);

        //TODO this needs to be moved to the GcodeController, get model from GreetingController
//        ArrayList<String> fileNames = getFileNames();
//        for(String str : fileNames)
//        {
//            File file = new File(str);
//
//            APIgrbl apIgrbl = new APIgrbl(file.getName());
//            new Thread(apIgrbl).start();     //<--- comment out if no arduino
//
//            setFileName(file.getName());
//            setFileSize(calculateFileSize(file));
//            setStudentNid(InputController.getNidFromText());
//            setDesc(InputController.getDescFromText());
//        }
//        updatePercentage();
    }
    
    
    //Setters
    
    public static void setFileName(String fileName) {
        controller.fileName.setText(fileName);
    }
    
    public static void setDesc(String desc) {
        controller.fileDesc.setText(desc);
    }
    
    public static void setFileSize(String fileSize) {
        controller.fileSize.setText(fileSize);
    }
    
    public void updatePercentage()
    {
        BackgroundProcessUI taskPercentage = new BackgroundProcessUI(4);
        filePercentage.textProperty().bind(taskPercentage.messageProperty());
        new Thread(taskPercentage).start();
    }
    
    public static void setStudentNid(String studentNid) {
        controller.studentNID.setText(studentNid);
    }
    
    
    //Functions
    
    public static String calculateFileSize(File file)
    {
        double size = file.length();
        int i = 0;
        
        while (size / 100 > 10) {
            size /= 1024;
            i++;
        }
    
        return String.valueOf(size) +
                (i == 1 ? "KB" : (i == 2 ? "MB" : "B"));
    }
    
}
