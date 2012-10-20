package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.SupportItems.MedicineChest;

import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:58
 */
public class SupportItemSpawner extends Spawner {

    private static int baseInterval = 1500;
    private int interval = baseInterval;

    public SupportItemSpawner() {
        super(0, 0, EntityLayer.General);
    }

    public Entity getEntity() {
        return getRandomEntity();
    }

    private Entity getRandomEntity() {
        Random random = new Random();
        int x = random.nextInt(level.getScreenWidth());
        return new MedicineChest(x, level.getScreenHeight());
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
}
