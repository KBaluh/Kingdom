package KBaluh.github.com.Weapons;

import KBaluh.github.com.Entity.Bullets.RocketBullet;
import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Mobs.Mob;

/**
 * Author: KBaluh
 * Date: 16.10.12:20:03
 */
public class RocketGun extends Weapon {

    private static final int shootDelay = 30;

    public RocketGun(Mob mob) {
        super(mob, shootDelay);
    }

    public void useWeapon() {
        if (canShoot) {
            canShoot = false;

            int offsetX = 0;
            int offsetY = -(mob.getImageHeight() / 2);

            if (mob.getDir() == Direction.LEFT) {
                offsetX += (mob.getImageWidth() / 2);
            }

            RocketBullet bullet = new RocketBullet(mob.getX() - offsetX,
                    mob.getY() - offsetY,
                    mob.getDir(), mob.getTeam());
            bullet.setX(bullet.getX() + bullet.getImageWidth());
            mob.level.addEntity(bullet);
        }
    }
}
