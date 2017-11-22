/*
 * File:    Tetrahedron.java
 * Package: objects.polyhedron.regular.platonic
 * Author:  Zachary Gill
 */

package tracer.objects.polyhedron.regular.platonic;

import tracer.math.vector.Vector;
import tracer.objects.base.AbstractObject;
import tracer.objects.base.polygon.Triangle;
import tracer.objects.polyhedron.regular.RegularPolyhedron;

import java.awt.*;

/**
 * Defines a Tetrahedron.
 */
public class Tetrahedron extends RegularPolyhedron
{
    
    //Constants
    
    /**
     * The number of faces of a Tetrahedron.
     */
    public static int TETRAHEDRON_FACES = 4;
    
    /**
     * The number of vertices of a Tetrahedron.
     */
    public static int TETRAHEDRON_VERTICES = 4;
    
    
    //Constructors
    
    /**
     * The constructor for an Tetrahedron.
     *
     * @param parent The parent of the Tetrahedron.
     * @param center The center point of the Tetrahedron.
     * @param color  The color of the Tetrahedron.
     * @param radius The radius of the bounding sphere of the Tetrahedron.
     */
    public Tetrahedron(AbstractObject parent, Vector center, Color color, double radius)
    {
        super(parent, center, color, TETRAHEDRON_FACES, radius);
    }
    
    /**
     * The constructor for an Tetrahedron.
     *
     * @param parent The parent of the Tetrahedron.
     * @param center The center point of the Tetrahedron.
     * @param radius The radius of the bounding sphere of the Tetrahedron.
     */
    public Tetrahedron(AbstractObject parent, Vector center, double radius)
    {
        this(null, center, Color.BLACK, radius);
    }
    
    /**
     * The constructor for an Tetrahedron.
     *
     * @param center The center point of the Tetrahedron.
     * @param color The color of the Tetrahedron.
     * @param radius The radius of the bounding sphere of the Tetrahedron.
     */
    public Tetrahedron(Vector center, Color color, double radius)
    {
        this(null, center, color, radius);
    }
    
    
    //Methods
    
    /**
     * Calculates the structure of the Tetrahedron.
     */
    @Override
    protected void calculate()
    {
        components.clear();
    
        Vector[] vertices = new Vector[TETRAHEDRON_VERTICES];
        int v = 0;
        for (int i : new int[] {-1, 1}) {
            for (int j : new int[]{-1, 1}) {
                vertices[v++] = new Vector(i, j, (i == j) ? 1 : -1).scale(radius).plus(center);
            }
        }
        
        new Triangle(this, color,
                vertices[2],
                vertices[1],
                vertices[0]
        );
        new Triangle(this, color,
                vertices[0],
                vertices[1],
                vertices[3]
        );
        new Triangle(this, color,
                vertices[0],
                vertices[3],
                vertices[2]
        );
        new Triangle(this, color,
                vertices[2],
                vertices[3],
                vertices[1]
        );
        
        setVisible(visible);
    }
    
}
