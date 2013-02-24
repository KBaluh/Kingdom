package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Mobs.TopFish;

public class TopFishSpawner extends Spawner {

    private int baseInterval = 900;
    private int interval = baseInterval;

    public TopFishSpawner() {
        super(0, 0, EntityLayer.General);
    }

    @Override
    public Entity getEntity() {
        return new TopFish(level.getScreenWidth(), 5);
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
}
