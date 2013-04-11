package KBaluh.github.com.Weapons;

import KBaluh.github.com.Entity.Bullets.RocketBullet;
import KBaluh.github.com.Entity.Bullets.RocketBulletChaotic;
import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Mobs.Mob;

public class RocketGunChaotic extends RocketGun {

    public RocketGunChaotic(Mob mob) {
        super(mob);
    }

    @Override
    public void useWeapon() {
        if (canShoot) {
            canShoot = false;

            int offsetX = 0;
            int offsetY = -(mob.getImageHeight() / 2);

            if (mob.getDir() == Direction.LEFT) {
                offsetX += (mob.getImageWidth() / 2);
            }

            RocketBullet bullet = new RocketBulletChaotic(mob.getX() - offsetX,
                    mob.getY() - offsetY,
                    mob.getDir(), mob.getTeam());
            bullet.setX(bullet.getX() + bullet.getImageWidth());
            mob.level.addEntity(bullet);
        }
    }
}
