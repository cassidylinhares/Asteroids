
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;

/**
 * Java Retro Asteroids - Lesson 11
 *
 * @author Adrian Balogh
 */
public class Asteroids extends Applet implements KeyListener, ActionListener {

    Image offscreen;
    Graphics offg;
    Spacecraft ship;
    gunBooster boost;
    ArrayList<Asteroid> rockList;
    ArrayList<Bullet> bulletList;
    ArrayList<Bullet2> boostBulletList;
    ArrayList<Debris> explosionList;
    Timer timer;
    boolean upKey, leftKey, rightKey, spaceKey;
    int score, level, numRock = 6;
    AudioClip laser, thruster, shipHit, asteroidHit;

    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    public void init() {
        this.setSize(900, 600);
        this.addKeyListener(this);
        ship = new Spacecraft();
        boost = new gunBooster();
        rockList = new ArrayList();
        bulletList = new ArrayList();
        boostBulletList = new ArrayList();
        explosionList = new ArrayList();
        timer = new Timer(20, this);
        offscreen = createImage(this.getWidth(), this.getHeight());
        offg = offscreen.getGraphics();

        for (int i = 0; i < 6; i++) {
            rockList.add(new Asteroid());
        }

        level = 1;

        laser = getAudioClip(getCodeBase(), "laser80.wav");
        thruster = getAudioClip(getCodeBase(), "thruster.wav");
        shipHit = getAudioClip(getCodeBase(), "explode1.wav");
        asteroidHit = getAudioClip(getCodeBase(), "explode0.wav");
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void actionPerformed(ActionEvent e) {
        respawnShip();
        keyCheck();
        levelUp();
        checkAsteroidDestruction();

        ship.updatePosition();
        boost.updatePosition();

        //update each rock position
        for (int i = 0; i < rockList.size(); i++) {
            rockList.get(i).updatePosition();
        }

        //update each bullet position
        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).updatePosition();
            if (bulletList.get(i).counter == 50 || bulletList.get(i).active == false) {
                bulletList.remove(i);
            }
        }

        //update boostBullet
        if (!boost.active) {
            if (boost.timer < 200) {
                boost.timer++;
                for (int i = 0; i < boostBulletList.size(); i++) {
                    boostBulletList.get(i).updatePosition();
                    if (boostBulletList.get(i).counter == 30 || boostBulletList.get(i).active == false) {
                        boostBulletList.remove(i);
                    }
                }
            } else {
                boost.resetBullet();
                boost.respawnBoost();
            }
        }
        
        //update each explosion
        for (int i = 0; i < explosionList.size(); i++) {
            explosionList.get(i).updatePosition();
            if (explosionList.get(i).counter == 10) {
                explosionList.remove(i);
            }
        }

        checkCollisions();
    }

    public void checkAsteroidDestruction() {
        for (int i = 0; i < rockList.size(); i++) { //for each rock
            if (rockList.get(i).active == false) { //if rock is ded
                if (rockList.get(i).size > 1) { //add 2 new mini ones
                    rockList.add(new Asteroid(rockList.get(i).xposition + 5, rockList.get(i).yposition - 5, rockList.get(i).size - 1));
                    rockList.add(new Asteroid(rockList.get(i).xposition - 5, rockList.get(i).yposition + 5, rockList.get(i).size - 1));
                }
                rockList.remove(i);
            }
        }
    }

    public boolean collision(VectorSprite thing1, VectorSprite thing2) {
        int x, y;

        for (int i = 0; i < thing1.drawShape.npoints; i++) {
            x = thing1.drawShape.xpoints[i];
            y = thing1.drawShape.ypoints[i];

            if (thing2.drawShape.contains(x, y)) {
                return true;
            }
        }

        for (int i = 0; i < thing2.drawShape.npoints; i++) {
            x = thing2.drawShape.xpoints[i];
            y = thing2.drawShape.ypoints[i];

            if (thing1.drawShape.contains(x, y)) {
                return true;
            }
        }

        return false;
    }

    public void checkCollisions() {
        for (int i = 0; i < rockList.size(); i++) {
            double rnd;
            if (collision(ship, rockList.get(i)) && ship.active) {
                ship.hit();
                score -= 20;
                rnd = Math.random() * 5 + 5;
                for (int k = 0; k < rnd; k++) {
                    explosionList.add(new Debris(ship.xposition, ship.yposition));
                }
                shipHit.play();
            }

            for (int j = 0; j < bulletList.size(); j++) {
                if (collision(bulletList.get(j), rockList.get(i))) {
                    bulletList.get(j).active = false;
                    rockList.get(i).hp -= bulletList.get(j).damage;
                    if (rockList.get(i).hp <= 0) {
                        rockList.get(i).active = false;
                        score += 10;
                        rnd = Math.random() * 5 + 5;
                        for (int k = 0; k < rnd; k++) {
                            explosionList.add(new Debris(rockList.get(i).xposition,
                                    rockList.get(i).yposition));
                        }
                        rockList.get(i).resetHP();
                    }

                    asteroidHit.play();
                }
            }

            for (int j = 0; j < boostBulletList.size(); j++) {
                if (collision(boostBulletList.get(j), rockList.get(i))) {
                    boostBulletList.get(j).active = false;
                    rockList.get(i).hp -= boostBulletList.get(j).damage;
                    if (rockList.get(i).hp <= 0) {
                        rockList.get(i).active = false;
                        score += 10;
                        rnd = Math.random() * 5 + 5;
                        for (int k = 0; k < rnd; k++) {
                            explosionList.add(new Debris(rockList.get(i).xposition, rockList.get(i).yposition));
                        }
                        rockList.get(i).resetHP();
                    }

                    asteroidHit.play();
                }
            }
        }

        if (boost.active && collision(ship, boost)) {
            boost.active = false;
        }
    }

    public void respawnShip() {
        if (ship.active == false && ship.counter > 50 && isRespawnSafe()
                && ship.lives > 0) {
            ship.reset();
        }
    }

    public boolean isRespawnSafe() {
        double x, y, h;

        for (int i = 0; i < rockList.size(); i++) {
            x = rockList.get(i).xposition - 450;
            y = rockList.get(i).yposition - 300;
            h = Math.sqrt(x * x + y * y);

            if (h < 100) {
                return false;
            }
        }

        return true;
    }

    public void fireBullet() {
        if (ship.counter > 5 && ship.active) {
            bulletList.add(new Bullet(ship.drawShape.xpoints[0],
                    ship.drawShape.ypoints[0], ship.angle));
            ship.counter = 0;
            laser.play();
        }
    }

    public void fireBoostBullet() {
        if (ship.counter > 5 && ship.active && !boost.active) {
            boostBulletList.add(new Bullet2(ship.drawShape.xpoints[0],
                    ship.drawShape.ypoints[0], ship.angle));
            ship.counter = 0;
            laser.play();
        }

    }

    public void levelUp() {

        if (rockList.isEmpty()) {
            level++;
            numRock += 2;
            for (int i = 0; i < numRock; i++) {
                rockList.add(new Asteroid());
            }
            respawnShip();
        }
    }

    public void paint(Graphics g) {
        offg.setColor(Color.BLACK);
        offg.fillRect(0, 0, 900, 600);
        offg.setColor(Color.GREEN);

        if (ship.active) {
            ship.paint(offg);
        }

        if (boost.spawn == 1) {
            if (boost.active && boost.counter < 600) {
                boost.paint(offg);
            } else if (boost.active && boost.counter < 700 && boost.counter % 8 == 0) {
                boost.paint(offg);
            } else if (boost.active && boost.counter > 700) {
                boost.respawnBoost();
            }
        } else {
            boost.respawnBoost();
        }

        offg.setColor(Color.WHITE);
        for (int i = 0; i < rockList.size(); i++) {
            rockList.get(i).paint(offg);
        }

        for (int i = 0; i < boostBulletList.size(); i++) {
            boostBulletList.get(i).paint(offg);
        }

        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).paint(offg);
        }

        for (int i = 0; i < explosionList.size(); i++) {
            explosionList.get(i).paint(offg);
        }

        offg.drawString("Lives: " + ship.lives, 5, 15);
        offg.drawString("Score: " + score, 830, 15);
        offg.drawString("Level: " + level, 425, 15);
        offg.drawString("Timer: " + boost.timer, 425, 35);
        offg.drawString("Boost: " + boost.spawn, 425, 55);

        if (ship.lives == 0) {
            offg.drawString("Game Over - You Lose!  Good day, sir!", 380, 300);
        } else if (rockList.isEmpty()) {
            offg.drawString("You Win! ", 400, 300);
        }

        g.drawImage(offscreen, 0, 0, this);
        repaint();
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void keyCheck() {
        if (upKey) {
            ship.accelerate();
        }

        if (leftKey) {
            ship.rotateLeft();
        }

        if (rightKey) {
            ship.rotateRight();
        }

        if (spaceKey) {
            if (!boost.active) {
                fireBoostBullet();
            } else {
                fireBullet();
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKey = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKey = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upKey = true;
            thruster.loop();
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceKey = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKey = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKey = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upKey = false;
            thruster.stop();
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceKey = false;
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}
