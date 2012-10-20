package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.SupportItems.IBonusReceiver;
import KBaluh.github.com.Entity.Team;
import KBaluh.github.com.Weapons.Weapon;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:29
 */
public abstract class Mob extends Entity implements IBonusReceiver {

    protected int dx;
    protected int dy;
    protected Weapon weapon;

    private Team team = Team.Neutral;
    private Direction dir = Direction.LEFT;
    private int speed = 3;
    private float maxHp;
    private float hp = 0;
    private boolean live = true;

    public Mob(int x, int y, float hp, Team team) {
        setX(x);
        setY(y);
        setTeam(team);
        this.maxHp = hp;
        this.hp = hp;
    }

    protected int getSpeed() {
        return speed;
    }

    protected void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
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

    /**
     * Method implements from IBonusReceiver.
     * Increase hp for mob.
     * @param hp - bonus hp
     */
    public void increaseHp(float hp) {
        if (isLive()) {
            this.hp += hp;
            if (this.hp > maxHp) {
                this.hp = maxHp;
            }
        }
    }

    public void onKeyDown(KeyEvent e) {
    }

    public void onKeyUp(KeyEvent e) {
    }

    public abstract Image getImage();
    public abstract int getScores();
}
