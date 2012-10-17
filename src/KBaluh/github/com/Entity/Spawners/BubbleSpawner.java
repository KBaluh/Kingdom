package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Decorations.Bubble;
import KBaluh.github.com.Entity.Decorations.BubbleType;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Levels.Level;

import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:01
 */
public class BubbleSpawner extends Spawner {

    private static int spawnInterval = 40;

    public BubbleSpawner(Level level) {
        super(level, 0, 0, spawnInterval);
    }

    public Entity getEntity() {
        Random random = new Random();
        int bubbleSpeed = random.nextInt(3) + 1;
        BubbleType bubbleType;
        if (bubbleSpeed == 1) {
            bubbleType = BubbleType.Small;
        } else
        if (bubbleSpeed == 2) {
            bubbleType = BubbleType.Middle;
        } else {
            bubbleType = BubbleType.Big;
        }
        Bubble bubble = new Bubble(random.nextInt(level.getScreenWidth()),
                level.getScreenHeight(),
                bubbleSpeed, bubbleType);
        return bubble;
    }

    public void afterSpawn() {
        generateSpawnInterval(spawnInterval, 20);
    }
}
