package gui.Interfaces.MainMenu;

import javafx.scene.control.Label;
import tracer.Tracer;

public class TraceController {

    //FXML
    public Label grblX;
    public Label grblY;
    public Label grblZ;
    public Label grblA;

    public static Tracer tracer;
    
    public void initialize()
    {
        grblX.setText("X: 1.0");
        grblY.setText("Y: 2.0");
        grblZ.setText("Z: 3.0");
        grblA.setText("Acceleration: 55.0");
        tracer = new Tracer();
    }
    
    public static void addTrace(double x, double y, double z)
    {
        tracer.addTrace(x, y, z);
    }
    
}
