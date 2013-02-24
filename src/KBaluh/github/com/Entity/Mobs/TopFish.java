package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;

import java.awt.Image;

public class TopFish extends Mob {

    public TopFish(int x, int y) {
        super(x, y, 1, Team.TeamTwo);
        setDir(Direction.LEFT);
        setSpeed(4);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public Image getImage() {
        return Art.topFish;
    }

    @Override
    public int getScores() {
        return 10;
    }
}
