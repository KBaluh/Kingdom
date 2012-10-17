package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Mobs.HunterFish;
import KBaluh.github.com.Levels.Level;

import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:17
 */
public class HunterFishSpawner extends Spawner {

    private static int spawnInterval = 80;

    public HunterFishSpawner(Level level) {
        super(level, 0, 0, spawnInterval);
    }

    public Entity getEntity() {
        Random random = new Random();
        int y = Math.abs(random.nextInt(level.getScreenHeight() - 100));
        int x = level.getScreenWidth() + 10;
        return new HunterFish(level, x, y, Direction.LEFT);
    }

    public void afterSpawn() {
        generateSpawnInterval(spawnInterval, 40);
    }
}
