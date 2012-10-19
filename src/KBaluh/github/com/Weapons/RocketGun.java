package KBaluh.github.com.Weapons;

import KBaluh.github.com.Entity.Bullets.RocketBullet;
import KBaluh.github.com.Entity.Mobs.Mob;

/**
 * Author: KBaluh
 * Date: 16.10.12:20:03
 */
public class RocketGun extends Weapon {

    private static int shootDelay = 30;
    private int offsetX = 65;
    private int offsetY = 55;

    public RocketGun(Mob mob, int offsetX, int offsetY) {
        super(mob, shootDelay);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void useWeapon() {
        if (canShoot) {
            canShoot = false;

            RocketBullet bullet = new RocketBullet(mob.getX() + offsetX,
                    mob.getY() + offsetY,
                    mob.getDir(), mob.getTeam());
            mob.level.addEntity(bullet);
        }
    }
}
