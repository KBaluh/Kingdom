package KBaluh.github.com.Entity.SupportItems;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Levels.Level;

import java.awt.event.KeyEvent;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:52
 */
public abstract class SupportItem extends Entity {

    protected Level level;
    private int speed;

    private boolean bonusReceived = false;
    private int bonus;

    public SupportItem(Level level, int x, int y, int speed, int bonus) {
        this.level = level;
        this.speed = speed;
        this.bonus = bonus;
        setX(x);
        setY(y);
    }

    public void tick() {
        setY(getY() - speed);
    }

    public void onKeyDown(KeyEvent e) {
    }

    public void onKeyUp(KeyEvent e) {
    }

    public int getBonus() {
        if (bonusReceived) {
            return 0;
        }
        return bonus;
    }
}
