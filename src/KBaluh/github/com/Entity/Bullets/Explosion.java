package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Entity;

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

    public Explosion(IExplosion expolosionEntity, int x, int y) {
        this.expolosionEntity = expolosionEntity;

        int offsetX = 0;
        int offsetY = -60;
        setX(x + offsetX);
        setY(y + offsetY);

        ticks = expolosionEntity.getInterval();
    }

    public void tick() {
        if (currentTick >= ticks) {
            if (currentFrame < expolosionEntity.getFrames()) {
                image = expolosionEntity.getImageByFrame(currentFrame);
                currentFrame++;
                currentTick = 0;
            } else {
                remove();
            }
        } else {
            currentTick++;
        }
    }

    public Image getImage() {
        return image;
    }

    public void onKeyDown(KeyEvent e) {
    }

    public void onKeyUp(KeyEvent e) {
    }
}
