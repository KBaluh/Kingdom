package KBaluh.github.com.Weapons;

import KBaluh.github.com.Entity.Bullets.RocketBullet;
import KBaluh.github.com.Entity.Mobs.Mob;

/**
 * Author: KBaluh
 * Date: 16.10.12:20:03
 */
public class RocketGun extends Weapon {

    private static int shootDelay = 30;

    public RocketGun(Mob mob) {
        super(mob, shootDelay);
    }

    public void useWeapon() {
        if (canShoot) {
            canShoot = false;
            int offsetX = 65;
            int offsetY = 55;
            RocketBullet bullet = new RocketBullet(mob.getX() + offsetX,
                    mob.getY() + offsetY,
                    mob.getDir(), mob.getTeam());
            mob.level.addEntity(bullet);
        }
    }
}
