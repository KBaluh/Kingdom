package KBaluh.github.com.Entity.Spawners;

import KBaluh.github.com.Entity.Decorations.Bubble;
import KBaluh.github.com.Entity.Decorations.BubbleType;
import KBaluh.github.com.Entity.Entity;

import java.awt.*;
import java.util.Random;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:01
 */
public class BubbleSpawner extends Spawner {

    private static int baseInterval = 20;
    private int interval = baseInterval;

    public BubbleSpawner() {
        super(0, 0, EntityLayer.Back);
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
        return new Bubble(random.nextInt(level.getScreenWidth()),
                level.getScreenHeight(),
                bubbleSpeed, bubbleType);
    }

    public void spawn() {
        Bubble bubble = (Bubble) getEntity();
        if (bubble.type == BubbleType.Small) {
            level.addEntityBack(bubble);
        } else
        if (bubble.type == BubbleType.Middle) {
            if (new Random().nextBoolean()) {
                level.addEntityPop(bubble);
            } else {
                level.addEntityBack(bubble);
            }
        } else
        if (bubble.type == BubbleType.Big) {
            level.addEntityPop(bubble);
        }
        afterSpawn();
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getBaseInterval() {
        return baseInterval;
    }

    public void setBaseInterval(int baseInterval) {
        this.baseInterval = baseInterval;
    }
}
