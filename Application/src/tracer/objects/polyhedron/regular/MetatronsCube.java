/*
 * File:    MetatronsCube.java
 * Package: tracer.objects.polyhedron.regular
 * Author:  Zachary Gill
 */

package tracer.objects.polyhedron.regular;

import tracer.main.Environment;
import tracer.math.vector.Vector;
import tracer.objects.base.Object;
import tracer.objects.polyhedron.regular.platonic.*;
import tracer.utility.ColorUtility;

import java.awt.*;

public class MetatronsCube extends Object
{
    private double radius;
    
    public MetatronsCube(Vector center, Color color, double radius)
    {
        super(center, color);
        this.radius = radius;
        calculate();
    }
    
    @Override
    public void calculate()
    {
//        Octahedron diamond = new Octahedron(this, Environment.origin, new Color(255, 0, 0, 64), 1);
//        Tetrahedron pyramid = new Tetrahedron(this, Environment.origin, new Color(255, 165, 0, 64), 1);
//        Hexahedron x1 = new Hexahedron(this, Environment.origin, new Color(0, 255, 0, 64), 1);
//        Dodecahedron d12 = new Dodecahedron(this, Environment.origin, new Color(0, 0, 255, 64), 1);
//        Icosahedron d20 = new Icosahedron(this, Environment.origin, new Color(165, 0, 165, 64), 1);
    
        Octahedron diamond = new Octahedron(this, Environment.origin, ColorUtility.getRandomColor(64), radius);
        Tetrahedron pyramid = new Tetrahedron(this, Environment.origin, ColorUtility.getRandomColor(64), radius);
        Hexahedron x1 = new Hexahedron(this, Environment.origin, ColorUtility.getRandomColor(64), radius);
        Dodecahedron d12 = new Dodecahedron(this, Environment.origin, ColorUtility.getRandomColor(64), radius);
        Icosahedron d20 = new Icosahedron(this, Environment.origin, ColorUtility.getRandomColor(64), radius);
        
        addFrame(Color.BLACK);
    }
    
    
}
