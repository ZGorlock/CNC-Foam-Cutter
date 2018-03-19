/*
 * File:    SlicerTest.java
 * Package: slicer
 * Author:  Zachary Gill
 */

package slicer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class SlicerTest
{
    
    @Test
    public void testIsSupportedModelFormat() throws Exception
    {
        Assert.assertEquals(true, Slicer.isSupportedModelFormat("stl"));
        Assert.assertEquals(false, Slicer.isSupportedModelFormat("obj"));
        Assert.assertEquals(false, Slicer.isSupportedModelFormat("gcode"));
    }
    
}
