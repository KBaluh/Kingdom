package KBaluh.github.com.Levels;

import KBaluh.github.com.Entity.Spawners.HunterFishSpawner;
import KBaluh.github.com.Entity.Spawners.SupportItemSpawner;
import KBaluh.github.com.GameScreen;

public class LevelOne extends BaseLevel {

    public LevelOne(GameScreen screen) {
        super(screen);
    }

    @Override
    protected void initSpawners() {
        super.initSpawners();
        addSpawner(new SupportItemSpawner());
        HunterFishSpawner hunterFishSpawner = new HunterFishSpawner();
        addSpawner(hunterFishSpawner);
    }
}
