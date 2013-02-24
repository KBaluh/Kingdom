package KBaluh.github.com.Entity.Decorations;

import KBaluh.github.com.Levels.Level;

import java.util.Random;

public class DeathBubbles {

    private Level level;
    private int x;
    private int y;

    public DeathBubbles(Level level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public void CreateBubbles() {
        if (level == null) {
            return;
        }

        int maxBubbles = 20;
        for (int i = 0; i < maxBubbles; ++i) {
            int dx = getRandX(x);
            int dy = getRandY(y);
            BubbleType type = getRandBubbleType();

            Bubble bubble = new DeathBubble(dx, dy, type);
            level.addEntityPop(bubble);
        }
    }

    private BubbleType getRandBubbleType() {
        Random random = new Random();
        int r = random.nextInt(2);
        BubbleType type;
        switch (r) {
            case 0:
                type = BubbleType.Small;
                break;
            case 1:
            default:
                type = BubbleType.Middle;
                break;
        }
        return type;
    }

    private int getRandX(int x) {
        Random rand = new Random();
        int dx = x;
        int rx = rand.nextInt(25);
        if (rand.nextBoolean()) {
            dx += rx;
        } else {
            dx -= rx;
        }
        return dx;
    }

    private int getRandY(int y) {
        Random rand = new Random();
        int dy = y;
        int ry = rand.nextInt(50);
        if (rand.nextBoolean()) {
            dy += ry;
        } else {
            dy -= dy;
        }
        return dy;
    }
}
