package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.SupportItems.MedicineChest;
import KBaluh.github.com.Levels.Level;

import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:58
 */
public class SupportItemSpawner extends Spawner {

    private static int spawnInterval = 3000;

    public SupportItemSpawner(Level level) {
        super(level, 0, 0, spawnInterval);
    }

    public Entity getEntity() {
        return getRandomEntity();
    }

    public void afterSpawn() {
        generateSpawnInterval(spawnInterval, spawnInterval * 2);
    }

    private Entity getRandomEntity() {
        Random random = new Random();
        int x = random.nextInt(level.getScreenWidth());
        return new MedicineChest(level, x, level.getScreenHeight());
    }

}
