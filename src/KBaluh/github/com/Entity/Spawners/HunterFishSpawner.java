package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Mobs.HunterFish;

import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:17
 */
public class HunterFishSpawner extends Spawner {

    private static int baseInterval = 110;
    private int interval = baseInterval;

    public HunterFishSpawner() {
        super(0, 0, EntityLayer.General);
    }

    public Entity getEntity() {
        Random random = new Random();
        int y = Math.abs(random.nextInt(level.getScreenHeight() - 100));
        int x = level.getScreenWidth() + 10;
        return new HunterFish(x, y, Direction.LEFT);
    }

    public void afterSpawn() {
        generateSpawnInterval(baseInterval, interval / 2);
        System.out.println(getInterval());
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }
}
