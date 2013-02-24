package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Bullets.SeaMine;

import java.util.Random;

public class SeaMineSpawner extends Spawner {

    private int baseInterval = 250;
    private int interval = baseInterval;

    public SeaMineSpawner() {
        super(0, 0, EntityLayer.General);
    }

    @Override
    public Entity getEntity() {
        return getRandomEntity();
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    @Override
    public int getBaseInterval() {
        return baseInterval;
    }

    @Override
    public void setBaseInterval(int baseInterval) {
        this.baseInterval = baseInterval;
    }

    private Entity getRandomEntity() {
        Random random = new Random();
        int x = random.nextInt(level.getScreenWidth() - getImageWidth());
        return new SeaMine(x, level.getScreenHeight());
    }
}
