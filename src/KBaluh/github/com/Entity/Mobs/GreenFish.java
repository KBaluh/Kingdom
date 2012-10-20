package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;
import KBaluh.github.com.Weapons.RocketGun;
import KBaluh.github.com.Weapons.Weapon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Author: KBaluh
 * Date: 20.10.12:18:19
 */
public class GreenFish extends Mob implements ActionListener {

    private Image imageLeft = Art.greenFishLeft;
    private Image imageRight = Art.greenFishRight;

    private final int fishSpeed = 2;

    private Timer shootTimer;

    private static final float hp = 35;

    private int scores = 14;

    public GreenFish(int x, int y, Direction dir) {
        super(x, y, hp, Team.TeamTwo);
        setDir(dir);
        setSpeed(fishSpeed);

        Weapon weapon = new RocketGun(this);
        setWeapon(weapon);

        initShootTimer();
    }

    public int getScores() {
        if (isRemoved()) {
            return 0;
        }
        return scores + (new Random().nextInt(scores / 2));
    }

    public void actionPerformed(ActionEvent e) {
        if (!isLive()) {
            shootTimer.stop();
            return;
        }
        weapon.useWeapon();

        shootTimer.setDelay(genShootTime());
    }

    /**
     * Первичная инициализая таймера для стрельбы
     */
    private void initShootTimer() {
        shootTimer = new Timer(genShootTime(), this);
        shootTimer.start();
    }

    /**
     * Генерирует время задержки, для след выстрела
     * @return
     */
    private int genShootTime() {
        Random random = new Random();
        int shootDelay = 750 + random.nextInt(4000);
        return shootDelay;
    }

    public Image getImage() {
        if (getDir() == Direction.LEFT) {
            return imageLeft;
        } else {
            return  imageRight;
        }
    }
}
