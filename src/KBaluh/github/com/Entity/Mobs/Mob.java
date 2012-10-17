package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Team;
import KBaluh.github.com.Levels.Level;
import KBaluh.github.com.Weapons.Weapon;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:29
 */
public abstract class Mob extends Entity {

    public Level level;
    protected int dx;
    protected int dy;
    protected int speed = 3;
    protected Weapon weapon;
    private Team team = Team.Neutral;
    private Direction dir = Direction.LEFT;

    private float hp = 0;
    private boolean live = true;

    public Mob(Level level, int x, int y, float hp, Team team) {
        this.level = level;
        setX(x);
        setY(y);
        setTeam(team);
        this.hp = hp;
    }

    public float getHp() {
        return hp;
    }

    public void hurt(float damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            live = false;
        }
    }

    public boolean isLive() {
        return live;
    }

    public void setTeam(Team team) {
        if (team == null) {
            team = Team.Neutral;
        }

        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        if (dir != null) {
            this.dir = dir;
        }
    }

    public void tick() {
        if (!isLive()) {
            return;
        }

        if (weapon != null) {
            weapon.tick();
        }

        if (dir == Direction.LEFT) {
            setX(getX() + (-speed));
        } else
        if (dir == Direction.RIGHT) {
            setX(getX() + speed);
        }
    }

    public abstract Image getImage();
    public abstract void onKeyDown(KeyEvent e);
    public abstract void onKeyUp(KeyEvent e);
}
