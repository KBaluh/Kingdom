package KBaluh.github.com.Entity.Decorations;

public class DeathBubble extends Bubble {

    private int currentTimeLife = 0;
    private int currentFreezeTime = 0;

    public DeathBubble(int x, int y, BubbleType type) {
        super(x, y, 2, type);
    }

    @Override
    public void tick() {
        int freezeTime = 2;
        if (++currentFreezeTime >= freezeTime) {
            super.tick();

            int timeLife = 25;
            if (++currentTimeLife > timeLife) {
                remove();
            }
            currentFreezeTime = 0;
        }
    }
}
