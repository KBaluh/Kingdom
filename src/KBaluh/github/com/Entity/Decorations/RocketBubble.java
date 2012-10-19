package KBaluh.github.com.Entity.Decorations;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Author: kostya
 * Date: 19.10.12:0:30
 */
public class RocketBubble extends Bubble {

    private Random random = new Random();
    private static int bubbleSpeed = 4;
    private int timeLife = 2;
    private int currentTimeLife = 0;

    private int freezeTime = 2;
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
