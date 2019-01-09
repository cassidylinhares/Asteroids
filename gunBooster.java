
import java.awt.Polygon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class gunBooster extends VectorSprite {

    int spawnChance = 10000;
    double spawn;
    int timer;

    public gunBooster() {
        shape = new Polygon();
        shape.addPoint(-5, -5);
        shape.addPoint(5, -5);
        shape.addPoint(2, 0);
        shape.addPoint(5, 5);
        shape.addPoint(-5, 5);
        shape.addPoint(-2, 0);

        drawShape = new Polygon();
        drawShape.addPoint(-5, -5);
        drawShape.addPoint(5, -5);
        drawShape.addPoint(2, 0);
        drawShape.addPoint(5, 5);
        drawShape.addPoint(-5, 5);
        drawShape.addPoint(-2, 0);

        xposition = (Math.random() * 500) + 100;
        yposition = (Math.random() * 400) + 100;

        active = true;
        counter = 0;
        timer = 0;
        spawn = (int) (Math.random() * spawnChance);
        ROTATION = Math.random() / 2;
    }

    public void updatePosition() {
        angle += ROTATION;
        super.updatePosition();
    }

    public void respawnBoost() {
        active = true;
        xposition = (Math.random() * 500) + 100;
        yposition = (Math.random() * 400) + 100;
        counter = 0;
        spawn = (int) (Math.random() * spawnChance);
    }

    public void resetBullet() {
        timer = 0;
    }
}
