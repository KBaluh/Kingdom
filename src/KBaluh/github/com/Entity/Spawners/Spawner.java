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

    public Spawner(int x, int y, int spawnInterval) {
        this.spawnInterval = spawnInterval;
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

    private void spawn() {
        level.addEntity(getEntity());
        afterSpawn();
    }

    public void afterSpawn() {
    }
}
