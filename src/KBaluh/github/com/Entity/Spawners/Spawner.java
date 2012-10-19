package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Entity;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 13:50
 */
public abstract class Spawner extends Entity {

    private int spawnInterval = 60;
    private int freezeTime = 0;
    private EntityLayer layer;

    public Spawner(int x, int y, int spawnInterval, EntityLayer layer) {
        this.spawnInterval = spawnInterval;
        this.layer = layer;
        setX(x);
        setY(y);
    }

    public abstract Entity getEntity();

    public void generateSpawnInterval(int start, int finish) {
        Random random = new Random();
        spawnInterval = start + random.nextInt(finish);
    }

    public void tick() {
        freezeTime++;
        if (freezeTime >= spawnInterval) {
            spawn();
            freezeTime = 0;
        }
    }

    public Image getImage() {
        return null;
    }

    public void onKeyDown(KeyEvent e) {
    }

    public void onKeyUp(KeyEvent e) {
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
    }
}
