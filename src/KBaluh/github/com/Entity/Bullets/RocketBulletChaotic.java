package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;

public class RocketBulletChaotic extends RocketBullet {


    private int b = 1;
    private int c = 25;

    public RocketBulletChaotic(int x, int y, Direction dir, Team team) {
        super(x, y, dir, team);
    }

    @Override
    public void tick() {
        super.tick();

        int x = getX();
        int y = getY() + (int)Math.round(b * Math.cos(x / c));
        setY(y);
    }
}
