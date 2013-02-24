package KBaluh.github.com.Entity.Bullets;

import KBaluh.github.com.Art;

import java.awt.*;
import java.util.ArrayList;

public class RocketExplosion implements IExplosion {

    private ArrayList<Image> explosions = new ArrayList<Image>();

    public RocketExplosion() {
        explosions.add(Art.rocketBulletExplosion1);
        explosions.add(Art.rocketBulletExplosion2);
        explosions.add(Art.rocketBulletExplosion3);
        explosions.add(Art.rocketBulletExplosion4);
        explosions.add(Art.rocketBulletExplosion5);
    }

    public int getFrames() {
        return 5;
    }

    public int getInterval() {
        return 3;
    }

    public Image getImageByFrame(int frame) {
        return explosions.get(frame);
    }
}
