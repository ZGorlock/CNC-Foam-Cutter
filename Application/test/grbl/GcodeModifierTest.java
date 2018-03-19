/*
 * File:    GcodeModifierTest.java
 * Package: grbl
 * Author:  Zachary Gill
 */

package grbl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class GcodeModifierTest
{
    
    @Test
    public void testGcodeModifier() throws Exception
    {
        GcodeModifier m = new GcodeModifier("resources\\gcode\\modifierTest\\test.gcode");
        m.modify();
        
        List<String> commands = m.getCommands();
    
        File expectedOutput = new File("resources\\gcode\\modifierTest\\expected.gcode");
        List<String> expectedCommands = Files.readAllLines(Paths.get(expectedOutput.getAbsolutePath()));
        
        for (int i = 0; i < expectedCommands.size(); i++) {
            Assert.assertEquals(expectedCommands.get(i), commands.get(i));
        }
    }
    
}
