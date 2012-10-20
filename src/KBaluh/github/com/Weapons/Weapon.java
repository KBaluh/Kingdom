package KBaluh.github.com.Weapons;

import KBaluh.github.com.Entity.Mobs.Mob;

/**
 * Author: KBaluh
 * Date: 16.10.12:19:28
 */
public abstract class Weapon {

    protected Mob mob;

    private int shootDelays = 30;
    private int shootDelay = 0;
    protected boolean canShoot = true;

    public Weapon(Mob owner, int shootDelay) {
        this.mob = owner;
        shootDelays = shootDelay;
        this.shootDelay = shootDelay;
    }

    public void tick() {
        if (!canShoot) {
            shootDelay--;
            if (shootDelay <= 0) {
                shootDelay = shootDelays;
                canShoot = true;
            }
        }
    }

    public abstract void useWeapon();
}
