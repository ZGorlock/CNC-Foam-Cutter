/*
 * File:    GcodeProgressCalculatorTest.java
 * Package: utils
 * Author:  Zachary Gill
 */

package utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class GcodeProgressCalculatorTest
{
    
    @Test
    public void testCalculateFileProgressUnits() throws Exception
    {
        Assert.assertEquals(838149.728, GcodeProgressCalculator.calculateFileProgressUnits("resources\\gcode\\can.gcode"), .01);
    }
    
    @Test
    public void testCalculateInstructionProgressUnits() throws Exception
    {
        Assert.assertEquals(3423.831, GcodeProgressCalculator.calculateInstructionProgressUnits("G1 X104.144 Y92.259 F2.43818"), .01);
    }
    
}
