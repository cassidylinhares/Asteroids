
import java.awt.Polygon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 100615025
 */
public class Bullet2 extends VectorSprite {
    

    public Bullet2(double x, double y, double a) {
        shape = new Polygon();
        shape.addPoint(0, 10);
        shape.addPoint(1, 0);
        shape.addPoint(0, -10);
        shape.addPoint(-1, 0);
        drawShape = new Polygon();
        drawShape.addPoint(0, 10);
        drawShape.addPoint(1, 0);
        drawShape.addPoint(0, -10);
        drawShape.addPoint(-1, 0);

        xposition = x;
        yposition = y;
        angle = a;
        THRUST = 25;

        xspeed = Math.cos(angle) * THRUST;
        yspeed = Math.sin(angle) * THRUST;

        ROTATION = 2;

        active = true;
        damage = 5;
    }

    public void updatePosition() {
        angle += ROTATION;
        super.updatePosition();
    }
    
    
}
