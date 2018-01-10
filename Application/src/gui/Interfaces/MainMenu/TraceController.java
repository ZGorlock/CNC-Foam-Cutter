package gui.Interfaces.MainMenu;

import tracer.Tracer;

public class TraceController {
    
    public static Tracer tracer;
    
    public void init()
    {
        tracer = new Tracer();
    }
    
    public static void addTrace(double x, double y, double z)
    {
        tracer.addTrace(x, y, z);
    }
    
}
