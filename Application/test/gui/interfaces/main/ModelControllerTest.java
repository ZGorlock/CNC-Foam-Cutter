/*
 * File:    ModelControllerTest.java
 * Package: gui.interfaces.main
 * Author:  Zachary Gill
 */

package gui.interfaces.main;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class ModelControllerTest
{
    
    @Test
    public void testSetFileName() throws Exception
    {
        //fake a JavaFX runtime environment
        new JFXPanel();
            Platform.runLater(() -> {
        });
        
        ModelController sut = new ModelController();
        ModelController.controller = sut;
        
        sut.fileName = new Label();
        sut.fileName.setText("");
        
        ModelController.setFileName(null);
        Assert.assertEquals("", sut.fileName.getText());
    
        ModelController.setFileName("An example file name.");
        Assert.assertEquals("An example file name.", sut.fileName.getText());
        
        ModelController.setFileName("This is an example filename that is really long and wont fit on the line.");
        Assert.assertEquals("This is an example filename\nthat is really long and\nwont fit on the line.", sut.fileName.getText());
    
        ModelController.setFileName("Thisisanexamplefilenamethatisreallylongandwontfitontheline.");
        Assert.assertEquals("Thisisanexamplefilenamethat\nisreallylongandwontfitonthe\nline.", sut.fileName.getText());
    }
    
    @Test
    public void testSetDesc() throws Exception
    {
        //fake a JavaFX runtime environment
        new JFXPanel();
        Platform.runLater(() -> {
        });
        
        ModelController sut = new ModelController();
        ModelController.controller = sut;
        
        sut.fileDesc = new Label();
        sut.fileDesc.setText("");
        
        ModelController.setDesc(null);
        Assert.assertEquals("", sut.fileDesc.getText());
        
        ModelController.setDesc("An example description.");
        Assert.assertEquals("An example description.", sut.fileDesc.getText());
        
        ModelController.setDesc("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        Assert.assertEquals("Lorem ipsum dolor sit amet,\nconsectetur adipiscing\nelit, sed do eiusmod tempor\nincididunt ut labore et\ndolore magna aliqua.", sut.fileDesc.getText());
        
        ModelController.setDesc("Loremipsumdolorsitametconsecteturadipiscingelitseddoeiusmodtemporincididuntutlaboreetdoloremagnaaliqua.");
        Assert.assertEquals("Loremipsumdolorsitametconse\ncteturadipiscingelitseddoei\nusmodtemporincididuntutlabo\nreetdoloremagnaaliqua.", sut.fileDesc.getText());
    }
    
    @Test
    public void testCalculateFileSize() throws Exception
    {
        File file1 = new File("resources\\gcode\\modifierTest\\expected.gcode");
        File file2 = new File("resources\\cadfiles\\can.stl");
        File file3 = new File("resources\\cadfiles\\Birds_and_flowers.stl");
    
        Assert.assertEquals(ModelController.calculateFileSize(file1), "245.00 B");
        Assert.assertEquals(ModelController.calculateFileSize(file2), "472.45 KB");
        Assert.assertEquals(ModelController.calculateFileSize(file3), "78.06 MB");
    }
    
}
