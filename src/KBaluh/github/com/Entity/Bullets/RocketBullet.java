package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:43
 */
public class RocketBullet extends Bullet implements IExplosion {

    private Image bulletLeft = new ImageIcon("res/RocketBulletLeft.png").getImage();
    private Image bulletRight = new ImageIcon("res/RocketBulletRight.png").getImage();
    private Image image = bulletLeft;

    private static int speed = 1;
    private Direction dir;

    private static final float damage = 14;

    private ArrayList<Image> explosions = new ArrayList<Image>();

    public RocketBullet(int x, int y, Direction dir, Team team) {
        super(x, y, speed, dir, damage, team);
        this.dir = dir;

        explosions.add(new ImageIcon("res/expl1.png").getImage());
        explosions.add(new ImageIcon("res/expl2.png").getImage());
        explosions.add(new ImageIcon("res/expl3.png").getImage());
        explosions.add(new ImageIcon("res/expl4.png").getImage());
        explosions.add(new ImageIcon("res/expl5.png").getImage());
    }

    public Image getImage() {
        if (isHis()) {
            return null;
        }
        if (dir == Direction.LEFT) {
            image = bulletLeft;
        } else
        if (dir == Direction.RIGHT) {
            image = bulletRight;
        }
        return image;
    }

    public int getFrames() {
        return 5;
    }

    public int getInterval() {
        return 3;
    }

    public Image getImageByFrame(int frame) {
        return explosions.get(frame);
    }
}
