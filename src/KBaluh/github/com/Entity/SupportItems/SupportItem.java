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

    public SupportItem(Level level, int x, int y, int speed) {
        this.level = level;
        this.speed = speed;
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
}
