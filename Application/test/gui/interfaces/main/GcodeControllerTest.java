/*
 * File:    GcodeControllerTest.java
 * Package: gui.interfaces.main
 * Author:  Zachary Gill
 */

package gui.interfaces.main;

import grbl.APIgrbl;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class GcodeControllerTest
{
    
    @Test
    public void testSendCommand() throws Exception
    {
        //fake a JavaFX runtime environment
        new JFXPanel();
        Platform.runLater(() -> {
        });
        
        GcodeController sut = new GcodeController();
        GcodeController.controller = sut;
    
        APIgrbl grbl = new APIgrbl();
        APIgrbl.grbl = grbl;
        
        TextField tf = sut.textFieldCommand = new TextField();
        tf.setPromptText("Enter text here...");
        tf.setText("Enter text here...");
        
        sut.sendCommand(null);
        Assert.assertEquals(0, GcodeController.commandBlock.size());

        tf.setText("");

        sut.sendCommand(null);
        Assert.assertEquals(0, GcodeController.commandBlock.size());

        APIgrbl.grbl = null;

        sut.sendCommand(null);
        Assert.assertEquals(0, GcodeController.commandBlock.size());

        APIgrbl.grbl = grbl;
        tf.setText("A command");

        sut.sendCommand(null);
        Assert.assertEquals(1, GcodeController.commandBlock.size());
        Assert.assertEquals(">A command", GcodeController.commandBlock.get(0));
    }
    
}
