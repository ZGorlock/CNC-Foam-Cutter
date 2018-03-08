package gui.interfaces.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gui.interfaces.greeting.GreetingController;
import gui.interfaces.main.*;
import javafx.scene.control.Tab;
import org.junit.jupiter.api.Test;

public class ControllerTests {
    public static void main(String [] args)
    {
        GcodeTest();
        MenuTest();
        ModelTest();
        RotationTest();
        TraceTest();
    }

    @Test
    private static void GcodeTest(){
        // not really sure how to test these... 
    }

    @Test
    private static void MenuTest(){
        // not really sure how to test these...
    }

    @Test
    private static void ModelTest(){
        Tab modelTest = ModelController.setup();
        assertNotNull(modelTest);
        //is that it? just the set up?

    }

    @Test
    private static void RotationTest(){
        Tab rotationTest = RotationController.setup();
        assertNotNull(rotationTest);
    }

    @Test
    private static void TraceTest(){
        Tab tracerTest = TraceController.setup();
        assertNotNull(tracerTest);
    }
}
