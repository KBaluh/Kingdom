package KBaluh.github.com.Entity.Decorations;

import java.util.Random;

/**
 * Author: KBaluh
 * Date: 19.10.12:0:30
 */
public class RocketBubble extends Bubble {

    private static final Random random = new Random();
    private static final int bubbleSpeed = 4;
    private int timeLife = 2;
    private int currentTimeLife = 0;

    private static final int freezeTime = 2;
    private int currentFreezeTime = 0;



    public RocketBubble(int x, int y, BubbleType type) {
        super(x, y + 8, bubbleSpeed, type);
        timeLife += random.nextInt(5);
    }

    public void tick() {
        if (++currentFreezeTime >= freezeTime) {
            super.tick();

            if (++currentTimeLife > timeLife) {
                remove();
            }
            currentFreezeTime = 0;
        }
    }
}
