package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Entity;

import java.awt.*;

/**
 * User: KBaluh
 * Date time: 17.10.12 16:25
 */
public class Explosion extends Entity {

    int currentFrame = 0;
    private IExplosion explosionEntity;
    private Image image;

    private int ticks = 0;
    private int currentTick = 0;

    public Explosion(IExplosion explosionEntity, int x, int y) {
        this.explosionEntity = explosionEntity;

        final int offsetY = -60;

        int posY = y + offsetY;

        if (posY < 0) {
            posY = 0;
        }
        setX(x);
        setY(posY);

        ticks = explosionEntity.getInterval();
    }

    public void tick() {
        if (currentTick >= ticks) {
            if (currentFrame < explosionEntity.getFrames()) {
                image = explosionEntity.getImageByFrame(currentFrame);
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
}
