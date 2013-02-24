package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Entity.Bullets.SeaMineTop;

public class TopFishMine extends TopFish {

    private boolean shooting = false;

    public TopFishMine(int x, int y) {
        super(x, y);
    }

    @Override
    public void tick() {
        super.tick();

        if (shooting) {
            return;
        }

        int x = getX();
        int px = level.getPlayerX();

        if (x <= px) {
            int dc = getImageWidth()/2;
            int posX = getX() + dc;
            int posY = getImageHeight();

            SeaMineTop bullet = new SeaMineTop(posX, posY);
            level.addEntity(bullet);

            shooting = true;
        }
    }
}
