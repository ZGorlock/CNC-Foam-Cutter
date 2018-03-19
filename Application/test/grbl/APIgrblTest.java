/*
 * File:    APIgrblTest.java
 * Package: grbl
 * Author:  Zachary Gill
 */

package grbl;

import gui.interfaces.main.MenuController;
import gui.interfaces.main.ModelController;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import main.Main;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import utils.TimeUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MenuController.class, ModelController.class, TimeUtil.class})
public class APIgrblTest
{
    
    @Test
    public void testGetPercentageComplete() throws Exception
    {
        //fake a JavaFX runtime environment
        new JFXPanel();
        Platform.runLater(() -> {
        });
        
        MenuController.stopped = true;
        
        ModelController modelController = new ModelController();
        modelController.percentage = new Label();
        modelController.percentage.setText("84.21 %");
        ModelController.controller = modelController;
        Assert.assertEquals("84.21 %", APIgrbl.getPercentageComplete());
        
        MenuController.stopped = false;
        
        
        APIgrbl.grbl = null;
        Assert.assertEquals("0.00 %", APIgrbl.getPercentageComplete());
        
        APIgrbl grbl = new APIgrbl();
        APIgrbl.grbl = grbl;
        Whitebox.setInternalState(grbl, "doneStreaming", true);
        Assert.assertEquals("100.00 %", APIgrbl.getPercentageComplete());
        
    
        Whitebox.setInternalState(grbl, "doneStreaming", false);
        Whitebox.setInternalState(grbl, "currentProgress", 774);
        Whitebox.setInternalState(grbl, "totalProgress", 15742);
        Assert.assertEquals("4.92 %", APIgrbl.getPercentageComplete());
    }
    
    @Test
    public void testGetTimeRemaining() throws Exception
    {
        //fake a JavaFX runtime environment
        new JFXPanel();
        Platform.runLater(() -> {
        });
        
        MenuController.stopped = true;
        
        ModelController modelController = new ModelController();
        modelController.timeRemaining = new Label();
        modelController.timeRemaining.setText("00:08:44");
        ModelController.controller = modelController;
        Assert.assertEquals("00:08:44", APIgrbl.getTimeRemaining());
    
        MenuController.stopped = false;
        MenuController.paused = true;
        
        Assert.assertEquals("00:08:44", APIgrbl.getTimeRemaining());
        
        MenuController.paused = false;
        
        
        APIgrbl grbl = new APIgrbl();
        APIgrbl.grbl = grbl;
        
        Main.startTime = 0;
        Assert.assertEquals("00:00:00", APIgrbl.getTimeRemaining());
        
        Main.startTime = 1;
        APIgrbl.grbl = null;
        Assert.assertEquals("00:00:00", APIgrbl.getTimeRemaining());
        
        APIgrbl.grbl = grbl;
        Whitebox.setInternalState(grbl, "doneStreaming", true);
        Assert.assertEquals("00:00:00", APIgrbl.getTimeRemaining());
        
        Whitebox.setInternalState(grbl, "doneStreaming", false);
    
        
        PowerMockito.mockStatic(TimeUtil.class);
        PowerMockito.when(TimeUtil.currentTimeMillis()).thenReturn(5000001L);
    
        Whitebox.setInternalState(grbl, "currentProgress", 774);
        Whitebox.setInternalState(grbl, "totalProgress", 1513);
        Assert.assertEquals("01:19:33", APIgrbl.getTimeRemaining());
    }
    
}
