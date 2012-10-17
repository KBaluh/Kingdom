package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;

import javax.swing.*;
import java.awt.*;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:43
 */
public class RocketBullet extends Bullet {

    private Image bulletLeft = new ImageIcon("res/RocketBulletLeft.png").getImage();
    private Image bulletRight = new ImageIcon("res/RocketBulletRight.png").getImage();
    private Image image = bulletLeft;

    private static int speed = 1;
    private Direction dir;

    private static final float damage = 14;

    public RocketBullet(int x, int y, Direction dir, Team team) {
        super(x, y, speed, dir, damage, team);
        this.dir = dir;
    }

    public Image getImage() {
        if (dir == Direction.LEFT) {
            image = bulletLeft;
        } else
        if (dir == Direction.RIGHT) {
            image = bulletRight;
        }
        return image;
    }
}
