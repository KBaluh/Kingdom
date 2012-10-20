package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Mobs.GreenFish;

import java.util.Random;

/**
 * Author: kostya
 * Date: 20.10.12:18:25
 */
public class GreenFishSpawner extends Spawner {

    private int baseInterval = 140;
    private int interval = baseInterval;

    public GreenFishSpawner() {
        super(0, 0, EntityLayer.General);
    }

    public Entity getEntity() {
        Random random = new Random();
        int y = Math.abs(random.nextInt(level.getScreenHeight() - 100));
        int x = level.getScreenWidth() + 10;
        return new GreenFish(x, y, Direction.LEFT);
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public int getBaseInterval() {
        return baseInterval;
    }

    public void setBaseInterval(int baseInterval) {
        this.baseInterval = baseInterval;
    }

    @Override
    public void afterSpawn() {
        generateSpawnInterval(baseInterval, baseInterval / 2);
    }
}
