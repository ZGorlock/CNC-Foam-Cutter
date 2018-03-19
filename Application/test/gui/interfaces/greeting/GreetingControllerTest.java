/*
 * File:    GreetingControllerTest.java
 * Package: gui.interfaces.greeting
 * Author:  Zachary Gill
 */

package gui.interfaces.greeting;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import utils.MachineDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MachineDetector.class})
public class GreetingControllerTest
{
    
    @Test
    public void testBadExtension() throws Exception
    {
        PowerMockito.mockStatic(MachineDetector.class);
        PowerMockito.when(MachineDetector.isCncMachine()).thenReturn(true);
    
        File file1 = new File("resources\\cadfiles\\can.stl");
        File file2 = new File("resources\\gcode\\Birds_and_flowers.gcode");
        File file3 = new File("resources\\jedicut files\\0184.dxf");
    
        Assert.assertEquals(false, GreetingController.badExtension(file1));
        Assert.assertEquals(false, GreetingController.badExtension(file2));
        Assert.assertEquals(true, GreetingController.badExtension(file3));
    
        
        PowerMockito.when(MachineDetector.isCncMachine()).thenReturn(false);
    
        Assert.assertEquals(true, GreetingController.badExtension(file1));
        Assert.assertEquals(false, GreetingController.badExtension(file2));
        Assert.assertEquals(true, GreetingController.badExtension(file3));
    }
    
    @Test
    public void testGetModel() throws Exception
    {
        GreetingController sut = new GreetingController();
        GreetingController.controller = sut;
    
        List<String> fileNames = new ArrayList<>();
        GreetingController.setFileNames(fileNames);
        
        Assert.assertEquals("", GreetingController.getModel());
    
        fileNames.add("resources\\cadfiles\\can.stl");
        Assert.assertEquals("resources\\cadfiles\\can.stl", GreetingController.getModel());
        
        fileNames.add("resources\\gcode\\Birds_and_flowers.gcode");
        Assert.assertEquals("", GreetingController.getModel());
    
        fileNames.remove("resources\\cadfiles\\can.stl");
        Assert.assertEquals("", GreetingController.getModel());
    }
    
    @Test
    public void testGetGcode() throws Exception
    {
        GreetingController sut = new GreetingController();
        GreetingController.controller = sut;
        
        List<String> fileNames = new ArrayList<>();
        GreetingController.setFileNames(fileNames);
        
        Assert.assertEquals("", GreetingController.getGcode());
        
        fileNames.add("resources\\gcode\\Birds_and_flowers.gcode");
        Assert.assertEquals("resources\\gcode\\Birds_and_flowers.gcode", GreetingController.getGcode());
        
        fileNames.add("resources\\cadfiles\\can.stl");
        Assert.assertEquals("", GreetingController.getGcode());
        
        fileNames.remove("resources\\gcode\\Birds_and_flowers.gcode");
        Assert.assertEquals("", GreetingController.getGcode());
    }
    
    @Test
    public void testGetSlices() throws Exception
    {
        GreetingController sut = new GreetingController();
        GreetingController.controller = sut;
        
        List<String> fileNames = new ArrayList<>();
        GreetingController.setFileNames(fileNames);
        
        Assert.assertEquals(0, GreetingController.getSlices().size());
        
        fileNames.add("resources\\gcode\\Birds_and_flowers.gcode");
        Assert.assertEquals(1, GreetingController.getSlices().size());
        
        fileNames.add("resources\\cadfiles\\can.stl");
        Assert.assertEquals(1, GreetingController.getSlices().size());

        fileNames.add("resources\\jedicut files\\rotation demo\\rd01.gcode");
        fileNames.add("resources\\jedicut files\\rotation demo\\rd01 - Copy.gcode");
        fileNames.add("resources\\jedicut files\\rotation demo\\rd02.gcode");
        fileNames.add("resources\\jedicut files\\rotation demo\\rd02 - Copy.gcode");
        fileNames.add("resources\\jedicut files\\rotation demo\\rd03.gcode");
        fileNames.add("resources\\jedicut files\\rotation demo\\rd03 - Copy.gcode");
        Assert.assertEquals(7, GreetingController.getSlices().size());
    }
    
}
