package KBaluh.github.com.Entity.SupportItems;

import KBaluh.github.com.Entity.Entity;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:52
 */
public abstract class SupportItem extends Entity {

    private int speed;
    private int bonus;
    private boolean bonusReceived = false;

    public SupportItem(int x, int y, int speed, int bonus) {
        this.speed = speed;
        this.bonus = bonus;
        setX(x);
        setY(y);
    }

    public void tick() {
        setY(getY() - speed);
    }

    public int getBonus() {
        if (bonusReceived) {
            return 0;
        }
        return bonus;
    }
}
