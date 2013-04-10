package KBaluh.github.com.Entity.SupportItems;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Mobs.Mob;

import java.awt.*;

public class BubbleShield extends Mob {

    private Image image = Art.bubbleShield;
    private Mob mob;

    private boolean isActiveShield;

    private static float baseHp = 20;
    private float hp = baseHp;

    private int maxTimeLife = 400;
    private int timeLife = maxTimeLife;

    public BubbleShield(Mob mob) {
        super(0, 0, baseHp, mob.getTeam());
        this.mob = mob;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void tick() {
        setX(mob.getX());
        setY(mob.getY());

        if (--timeLife <=0) {
            remove();
        }
    }

    public void hurt(float damage) {
        hp -= damage;
        if (hp <= 0){
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
        hp = baseHp;
        level.addEntity(this);

        isActiveShield = true;
    }

    @Override
    public int getScores() {
        return 0;
    }
}
