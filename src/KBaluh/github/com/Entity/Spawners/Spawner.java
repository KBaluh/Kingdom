package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.EmptyPaint;
import KBaluh.github.com.Entity.Entity;

import java.awt.*;
import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 13:50
 */
public abstract class Spawner extends Entity {

    private int freezeTime = 0;
    private EntityLayer layer;

    public Spawner(int x, int y, EntityLayer layer) {
        this.layer = layer;
        setX(x);
        setY(y);
        setPaintBehavior(new EmptyPaint());
    }

    public abstract Entity getEntity();

    public void generateSpawnInterval(int start, int finish) {
        Random random = new Random();
        if (finish <= 0) {
            setInterval(start);
        } else {
            setInterval(start + random.nextInt(finish));
        }
    }

    public void tick() {
        freezeTime++;
        if (freezeTime >= getInterval()) {
            spawn();
            freezeTime = 0;
        }
    }

    public Image getImage() {
        return null;
    }

    public void spawn() {
        if (layer == EntityLayer.Back) {
            level.addEntityBack(getEntity());
        } else
        if (layer == EntityLayer.General) {
            level.addEntity(getEntity());
        } else
        if (layer == EntityLayer.Pop) {
            level.addEntityPop(getEntity());
        }
        afterSpawn();
    }

    public void afterSpawn() {
        generateSpawnInterval(getBaseInterval(), getBaseInterval() / 2);
    }

    public abstract int getInterval();
    public abstract void setInterval(int interval);
    public abstract int getBaseInterval();
    public abstract void setBaseInterval(int baseInterval);
}
