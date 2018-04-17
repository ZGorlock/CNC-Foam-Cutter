/*
 * File:    RotationControllerTest.java
 * Package: gui.interfaces.main
 * Author:  Zachary Gill
 */

package gui.interfaces.main;

import gui.interfaces.popup.SystemNotificationController;
import javafx.scene.image.Image;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemNotificationController.class})
public class RotationControllerTest
{
    
    @Test
    public void testIsValidAngle() throws Exception
    {
        Assert.assertTrue(RotationController.isValidStepCount("180"));
    
        PowerMockito.mockStatic(SystemNotificationController.class);
        
        Assert.assertFalse(RotationController.isValidStepCount("180.5"));
        Assert.assertFalse(RotationController.isValidStepCount("-1"));
        Assert.assertFalse(RotationController.isValidStepCount("361"));
        Assert.assertFalse(RotationController.isValidStepCount("degree"));
    }
    
    @Test
    public void testIsValidStepAngle() throws Exception
    {
        Assert.assertTrue(RotationController.isValidStepAngle("1"));
        Assert.assertTrue(RotationController.isValidStepAngle("90"));
        Assert.assertTrue(RotationController.isValidStepAngle("360"));
    
        PowerMockito.mockStatic(SystemNotificationController.class);
        
        Assert.assertFalse(RotationController.isValidStepAngle("71"));
        Assert.assertFalse(RotationController.isValidStepAngle("0"));
        Assert.assertFalse(RotationController.isValidStepAngle("-1"));
        Assert.assertFalse(RotationController.isValidStepAngle("361"));
        Assert.assertFalse(RotationController.isValidStepAngle("degree"));
    }
    
    @Test
    public void testFormatDegree() throws Exception
    {
        Assert.assertEquals("180.00°", RotationController.formatDegree(180.0));
    }
    
    @Test
    public void testGenerateQueueHelper() throws Exception
    {
        RotationController sut = new RotationController();
        RotationController.controller = sut;
        RotationController.rotationStep = (int) (360.0 / RotationController.DEFAULT_MIN_ROTATION_DEGREE);
    
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
        sut.rotationProfileMap.put(profiles.get(0), 45);
        sut.rotationProfileMap.put(profiles.get(1), 10);
        sut.rotationProfileMap.put(profiles.get(2), 15);
        sut.rotationProfileMap.put(profiles.get(3), 30);
    
        sut.gcodeTraceFileMap = new HashMap<>();
        sut.gcodeTraceFileMap.put(profiles.get(0), "a");
        sut.gcodeTraceFileMap.put(profiles.get(1), "b");
        sut.gcodeTraceFileMap.put(profiles.get(2), "c");
        sut.gcodeTraceFileMap.put(profiles.get(3), "d");
        
        RotationController.generateQueueHelper(profiles);
        int j = 0;
        for (int i = j; i < 55; i++) {
            Assert.assertEquals("a", RotationController.queue.get(j));
            j++;
        }
        for (int i = j; i < 10; i++) {
            Assert.assertEquals("b", RotationController.queue.get(j));
            j++;
        }
        for (int i = j; i < 15; i++) {
            Assert.assertEquals("c", RotationController.queue.get(j));
            j++;
        }
        for (int i = j; i < 30; i++) {
            Assert.assertEquals("d", RotationController.queue.get(j));
            j++;
        }
    
        PowerMockito.mockStatic(SystemNotificationController.class);
        
        RotationController.rotationStep = 4;
        RotationController.generateQueueHelper(profiles);
    
        sut.rotationProfileMap.put(profiles.get(0), 1);
        sut.rotationProfileMap.put(profiles.get(1), 1);
        sut.rotationProfileMap.put(profiles.get(2), 1);
        sut.rotationProfileMap.put(profiles.get(3), 1);
        RotationController.generateQueueHelper(profiles);
    }
    
}
