/*
 * File:    Renderer.java
 * Package: renderer
 * Author:  Zachary Gill
 */

package renderer;

import org.j3d.loaders.stl.STLFileReader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Renders the uploaded STL file.
 */
public class Renderer
{
    
    //Fields
    
    /**
     * The STL file reader.
     */
    private STLFileReader reader;
    
    
    //Constructors
    
    /**
     * Constructs a Renderer object.
     *
     * @param stl The stl file to Render.
     */
    public Renderer(File stl)
    {
        try {
            reader = new STLFileReader(stl);
        } catch (IOException | org.j3d.loaders.InvalidFormatException e) {
            e.printStackTrace();
        }
        
        
        if (reader != null) {
            System.out.println(Arrays.toString(reader.getNumOfFacets()));
            System.out.println(reader.getNumOfObjects());
            System.out.println(Arrays.toString(reader.getObjectNames()));
            System.out.println(reader.getParsingMessages());
        }
    }
    
    
    //Methods
    
    
}
