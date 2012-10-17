package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Levels.Level;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * User: KBaluh
 * Date time: 17.10.12 16:25
 */
public class Explosion extends Entity {

    int currentFrame = 0;
    private IExplosion expolosionEntity;
    private Image image;

    private int ticks = 0;
    private int currentTick = 0;

    private Level level;

    private boolean isDone = false;

    public Explosion(Level level, IExplosion expolosionEntity, int x, int y) {
        this.expolosionEntity = expolosionEntity;
        this.level = level;
        setX(x);
        setY(y);

        ticks = expolosionEntity.getInterval();
    }

    public void tick() {
        if (isDone) {
            return;
        }

        if (currentTick >= ticks) {
            if (currentFrame < expolosionEntity.getFrames()) {
                image = expolosionEntity.getImageByFrame(currentFrame);
                currentFrame++;
                currentTick = 0;
            } else {
                isDone = true;
                level.needRemoveEntity(this);
                level.needRemoveEntity((Entity) expolosionEntity);
            }
        } else {
            currentTick++;
        }
    }

    public Image getImage() {
        if (isDone) {
            return null;
        }
        return image;
    }

    public void onKeyDown(KeyEvent e) {
    }

    public void onKeyUp(KeyEvent e) {
    }
}
