package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Mobs.TopFishMine;

public class TopFishMineSpawner extends TopFishSpawner {

    @Override
    public Entity getEntity() {
        return new TopFishMine(level.getScreenWidth(), 5);
    }
}
