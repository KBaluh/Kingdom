package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Decorations.BubbleType;
import KBaluh.github.com.Entity.Decorations.RocketBubble;
import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;

import java.awt.*;
import java.util.Random;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:43
 */
public class RocketBullet extends Bullet {

    private Image bulletLeft = Art.rocketBulletLeft;
    private Image bulletRight = Art.rocketBulletRight;
    private Image image = bulletLeft;

    private static final int speed = 1;
    private Direction dir;

    private static final float damage = 14;

    private Random random = new Random();

    private static final int ticks = 1;
    private int currentTick = 0;

    public RocketBullet(int x, int y, Direction dir, Team team) {
        super(x, y, speed, dir, damage, team);
        this.dir = dir;
    }

    public void tick() {
        super.tick();
        if (currentTick <= ticks) {
            currentTick++;
        } else {
            int y = random.nextInt(30);
            RocketBubble bubble = new RocketBubble(getX(), getY() + y, BubbleType.Small);
            level.addEntity(bubble);
            currentTick = 0;
        }
    }

    @Override
    public void hit() {
        super.hit();
        Explosion explosion = new Explosion(new RocketExplosion(), getX(), getY());
        level.addEntity(explosion);
    }

    public Image getImage() {
        if (isHit()) {
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
}
