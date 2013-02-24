package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Team;

import java.awt.*;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:39
 */
public abstract class Bullet extends Entity {

    protected int dx;
    protected int dy;

    private int speed;
    private Direction dir;

    private static int ticks = 1;
    private int tickCount = ticks;

    private Team team;

    private float damage;

    private boolean isHit = false;

    public Bullet(int x, int y, int speed, Direction dir, float damage, Team team) {
        setX(x);
        setY(y);
        this.speed = speed;
        this.dir = dir;
        this.team = team;
        this.damage = damage;
    }

    public void hit() {
        isHit = true;
    }

    public boolean isHit() {
        return isHit;
    }

    public float getDamage() {
        if (isHit) {
            return 0;
        }
        return damage;
    }

    public void tick() {
        if (isHit) {
            return;
        }

        if (tickCount == 0) {
            switch(dir) {
                case LEFT:
                    dx += -speed;
                    break;
                case RIGHT:
                    dx += speed;
                    break;
                case UP:
                    dy -= speed;
                    break;
                case DOWN:
                    dy += speed;
                    break;
            }

            setX(getX() + dx);
            setY(getY() + dy);

            tickCount = ticks;
        } else {
            tickCount--;
        }
    }

    public Team getTeam() {
        return team;
    }

    public abstract Image getImage();
}
