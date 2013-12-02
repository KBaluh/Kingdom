package KBaluh.github.com.Entity.SupportItems;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Mobs.Mob;
import KBaluh.github.com.Entity.PaintEntity;

import java.awt.*;

public class BubbleShield extends Mob {

    private Image image = Art.bubbleShield;
    private Mob mob;

    private boolean isActiveShield;

    private static float baseHp = 20;

    private int maxTimeLife = 400;
    private int timeLife = maxTimeLife;

    public BubbleShield(Mob mob) {
        super(0, 0, baseHp, mob.getTeam());
        this.mob = mob;
        setPaintBehavior(new PaintEntity());
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void tick() {
        int cx = mob.getX() + (mob.getImageWidth()/2);
        int cy = mob.getY() + (mob.getImageHeight()/2);
        cx -= getImageWidth()/2;
        cy -= getImageHeight()/2;

        setX(cx);
        setY(cy);

        if (--timeLife <=0) {
            remove();
        }
    }

    public void hurt(float damage) {
        super.hurt(damage);
        if (getHp() <= 0){
            remove();
            isActiveShield = false;
        }
    }

    public boolean isActive() {
        return isActiveShield;
    }

    public void activate() {
        if (level == null) {
            return;
        }

        timeLife = maxTimeLife;
        increaseHp(getMaxHp());
        level.addEntity(this);

        isActiveShield = true;
    }

    @Override
    public int getScores() {
        return 0;
    }
}
