package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;

import java.awt.Image;

public class SeaMineTop extends Bullet {

    private static final int damage = 10;
    private static final int speed = 7;

    public SeaMineTop(int x, int y) {
        super(x, y, speed, Direction.DOWN, damage, Team.TeamTwo);
    }

    public void tick() {
        super.tick();
        dx = 0;
        dy = 0;
    }

    @Override
    public Image getImage() {
        return Art.seaMineTop;
    }

    @Override
    public void hit() {
        super.hit();
        level.addEntityPop(new Explosion(new RocketExplosion(), getX(), getY()));
    }
}
