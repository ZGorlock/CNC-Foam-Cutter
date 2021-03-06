/*
 * File:    Text.java
 * Package: tracer.objects.base.simple
 * Author:  Zachary Gill
 */

package tracer.objects.base.simple;

import tracer.camera.Camera;
import tracer.math.vector.Vector;
import tracer.objects.base.AbstractObject;
import tracer.objects.base.BaseObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a Text object.
 */
public class Text extends BaseObject
{
    
    //Fields
    
    /**
     * The text to display.
     */
    protected char[] text;
    
    
    //Constructor
    
    /**
     * The constructor for a Text.
     *
     * @param parent The parent of the Text.
     * @param color  The color of the Text.
     * @param v      The Vector defining the point of the Text.
     * @param text   The text to display.
     */
    public Text(AbstractObject parent, Color color, Vector v, String text)
    {
        super(parent, color, v, v);
        this.text = text.toCharArray();
    }
    
    /**
     * The constructor for a Text.
     *
     * @param parent The parent of the Text.
     * @param v      The Vector defining the point of the Text.
     * @param text   The text to display.
     */
    public Text(AbstractObject parent, Vector v, String text)
    {
        this(parent, Color.BLACK, v, text);
    }
    
    /**
     * The constructor for a Text.
     *
     * @param color  The color of the Text.
     * @param v      The Vector defining the point of the Text.
     * @param text   The text to display.
     */
    public Text(Color color, Vector v, String text)
    {
        this(null, color, v, text);
    }
    
    /**
     * The constructor for a Text.
     *
     * @param v      The Vector defining the point of the Text.
     * @param text   The text to display.
     */
    public Text(Vector v, String text)
    {
        this(null, Color.BLACK, v, text);
    }
    
    
    
    //Methods
    
    /**
     * Prepares the Text to be rendered.
     *
     * @return The list of BaseObjects that were prepared.
     */
    @Override
    public List<BaseObject> prepare()
    {
        List<BaseObject> preparedBases = new ArrayList<>();
        if (!visible) {
            return preparedBases;
        }
        
        prepared.clear();
        prepared.add(vertices[0].clone());
        
        performRotationTransformation(prepared);
        
        preparedBases.add(this);
        return preparedBases;
    }
    
    /**
     * Renders the Vertex on the screen.
     *
     * @param g2 The 2D Graphics entity.
     */
    @Override
    public void render(Graphics2D g2)
    {
        if (!visible || prepared.size() != 1) {
            return;
        }
        
        Camera.projectVectorToCamera(prepared);
        Camera.collapseVectorToViewport(prepared);
        
        if (!clippingEnabled || Camera.hasVectorInView(prepared, vertices)) {
            Camera.scaleVectorToScreen(prepared);
            
            g2.setColor(color);
            g2.drawChars(text, 0, text.length, (int) prepared.get(0).getX(), (int) prepared.get(0).getY());
        }
    }
    
}
