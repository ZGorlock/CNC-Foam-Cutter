/*
 * File:    RotationControllerTest.java
 * Package: gui.interfaces.main
 * Author:  Zachary Gill
 */

package gui.interfaces.main;

import javafx.scene.image.Image;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class RotationControllerTest
{
    
    @Test
    public void testIsValidAngle() throws Exception
    {
        Assert.assertEquals(true, RotationController.isValidAngle("180"));
        Assert.assertEquals(false, RotationController.isValidAngle("180.5"));
        Assert.assertEquals(false, RotationController.isValidAngle("-1"));
        Assert.assertEquals(false, RotationController.isValidAngle("361"));
        Assert.assertEquals(false, RotationController.isValidAngle("degree"));
    }
    
    @Test
    public void testFormatDegree() throws Exception
    {
        Assert.assertEquals("180°", RotationController.formatDegree(180));
    }
    
    @Test
    public void testGenerateQueueHelper() throws Exception
    {
        RotationController sut = new RotationController();
        RotationController.controller = sut;
        sut.rotationStep = RotationController.MIN_ROTATION_DEGREE;
    
        Image i1 = Mockito.mock(Image.class);
        Image i2 = Mockito.mock(Image.class);
        Image i3 = Mockito.mock(Image.class);
        Image i4 = Mockito.mock(Image.class);
        
        List<Image> profiles = new ArrayList<>();
        profiles.add(i1);
        profiles.add(i2);
        profiles.add(i3);
        profiles.add(i4);
    
        sut.rotationProfileMap = new HashMap<>();
        sut.rotationProfileMap.put(profiles.get(0), 75);
        sut.rotationProfileMap.put(profiles.get(1), 130);
        sut.rotationProfileMap.put(profiles.get(2), 55);
        sut.rotationProfileMap.put(profiles.get(3), 100);
    
        sut.gcodeTraceFileMap = new HashMap<>();
        sut.gcodeTraceFileMap.put(profiles.get(0), "a");
        sut.gcodeTraceFileMap.put(profiles.get(1), "b");
        sut.gcodeTraceFileMap.put(profiles.get(2), "c");
        sut.gcodeTraceFileMap.put(profiles.get(3), "d");
        
        Assert.assertTrue(RotationController.generateQueueHelper(profiles));
        int j = 0;
        for (int i = j; i < 75; i++) {
            Assert.assertEquals("a", RotationController.queue.get(j));
            j++;
        }
        for (int i = j; i < 130; i++) {
            Assert.assertEquals("b", RotationController.queue.get(j));
            j++;
        }
        for (int i = j; i < 55; i++) {
            Assert.assertEquals("c", RotationController.queue.get(j));
            j++;
        }
        for (int i = j; i < 100; i++) {
            Assert.assertEquals("d", RotationController.queue.get(j));
            j++;
        }
    }
    
}
