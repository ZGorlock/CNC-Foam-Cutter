package gui.Interfaces.MainMenu;

import grbl.APIgrbl;
import gui.Interfaces.Greeting.GreetingController;
import gui.Interfaces.Greeting.InputController;
import gui.Interfaces.PopUps.JobCompletedController;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import renderer.Renderer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static gui.Interfaces.MainMenu.MenuController.stopped;

/**
 * The controller for the Model tab.
 */
public class ModelController
{
    
    //FXML
    
    /**
     * The FXML elements of the tab.
     */
    public SwingNode swingNodeModel;
    public Label fileName;
    public Label fileSize;
    public Label fileDesc;
    public Label studentNID;
    public HBox hp;
    public HBox time;
    

    //UI-Dependent Components
    
    public Label filePercentage;
    public static String percentage = "";

    public Label timeRemaining;
    public static String timerem = "";
    
    
    //Constants
    
    /**
     * The maximum dimensions of the foam block, in inches.
     */
    public static final double MAX_WIDTH_CNC = 20;
    public static final double MAX_LENGTH_CNC = 40;
    public static final double MAX_HEIGHT_CNC = 10;
    public static final double MAX_WIDTH_HOTWIRE = 66;
    public static final double MAX_LENGTH_HOTWIRE = 66;
    public static final double MAX_HEIGHT_HOTWIRE = 66;

    
    //Static Fields
    
    /**
     * The singleton instance of the ModelController.
     */
    public static ModelController controller;
    
    /**
     * The singleton instance of the Renderer.
     */
    public static Renderer renderer;
    
    
    //Static Methods
    
    /**
     * Creates the Model tab.
     *
     * @return The Model tab.
     */
    public static Tab setup()
    {
        try {
            URL fxml = ModelController.class.getResource("Model.fxml");
            Tab tab = FXMLLoader.load(fxml);
            
            return tab;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Methods
    
    /**
     * Initializes the ModelController and sets up the Renderer.
     */
    public void initialize()
    {
        controller = this;
        renderer = Renderer.setup(swingNodeModel);
        
        File file = new File(GreetingController.getModel());
        setFileName(file.getName());
        setFileSize(calculateFileSize(file));
        setStudentNid(InputController.getNidFromText());
        setDesc(InputController.getDescFromText());
        updatePercentage();
        updateTime();
    }
    
    /**
     * Starts the percentage monitoring thread.
     */
    public void updatePercentage()
    {
        filePercentage = new Label();
        filePercentage.setText("0.00%");
        filePercentage.setAlignment(Pos.TOP_RIGHT);
        hp.getChildren().add(filePercentage);


        TimerTask updateCoordinates = new TimerTask()
        {
            private String state = "";
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    if(percentage != null & percentage.compareTo(state) != 0){
                        filePercentage.textProperty().set(percentage);
                        state = percentage;
                    }
                    if(MenuController.controller != null && APIgrbl.grbl != null && APIgrbl.grbl.isDoneStreaming())
                    {
                        MenuController.controller.reset();
                        GcodeController.controller.resetUI();
                        APIgrbl.grbl.resetStreaming();
                    }
                });
            }
        };

        Timer t = new Timer();
        t.scheduleAtFixedRate(updateCoordinates,0, 100);
    }

    public void updateTime()
    {
        timeRemaining = new Label();
        timeRemaining.setText("00:00:00");
        timeRemaining.setAlignment(Pos.TOP_RIGHT);
        time.getChildren().add(timeRemaining);

        TimerTask updateTime = new TimerTask()
        {
            private String state = "";
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    if(timerem.compareTo(state) != 0){
                        timeRemaining.textProperty().set(timerem);
                        state = timerem;
                    }
                });
            }
        };

        Timer t = new Timer();
        t.scheduleAtFixedRate(updateTime,0, 100);
    }
    
    
    //Setters
    
    /**
     * Sets the filename on the Model tab.
     *
     * @param fileName The filename.
     */
    public static void setFileName(String fileName) {
        controller.fileName.setText(fileName);
    }
    
    /**
     * Sets the description on the Model tab.
     *
     * @param desc The description.
     */
    public static void setDesc(String desc) {
        controller.fileDesc.setText(desc);
    }
    
    /**
     * Sets the file size on the Model tab.
     *
     * @param fileSize The file size.
     */
    public static void setFileSize(String fileSize) {
        controller.fileSize.setText(fileSize);
    }
    
    /**
     * Sets the student NID on the Model tab.
     *
     * @param studentNid The student's NID.
     */
    public static void setStudentNid(String studentNid) {
        controller.studentNID.setText(studentNid);
    }
    
    
    //Functions
    
    /**
     * Calculates the filesize string for a file.
     *
     * @param file The file to measure.
     * @return The filesize string.
     */
    public static String calculateFileSize(File file)
    {
        double size = file.length();
        int i = 0;
        
        while (size / 100 > 10) {
            size /= 1024;
            i++;
        }
    
        return String.format("%.2f",size) +
                (i == 1 ? "KB" : (i == 2 ? "MB" : "B"));
    }
}
