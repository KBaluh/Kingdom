package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Team;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:39
 */
public abstract class Bullet extends Entity {

    private int x;
    private int y;
    private int dx;
    private int dy;

    private int speed;
    private Direction dir;

    private static int ticks = 1;
    private int tickCount = ticks;

    private Team team;

    private float damage;

    private boolean isHit = false;

    public Bullet(int x, int y, int speed, Direction dir, float damage, Team team) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.dir = dir;
        this.team = team;
        this.damage = damage;
    }

    public void hit() {
        isHit = true;
    }

    public boolean isHis() {
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
            if (dir == Direction.LEFT) {
                dx += -speed;
            }
            if (dir == Direction.RIGHT) {
                dx += speed;
            }

            x += dx;
            y += dy;

            tickCount = ticks;
        } else {
            tickCount--;
        }
    }

    public Team getTeam() {
        return team;
    }

    public abstract Image getImage();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void onKeyUp(KeyEvent e) {
    }

    public void onKeyDown(KeyEvent e) {
    }
}
