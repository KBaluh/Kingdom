package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;
import KBaluh.github.com.Levels.Level;
import KBaluh.github.com.Weapons.RocketGun;
import KBaluh.github.com.Weapons.Weapon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Author: KBaluh
 * Date: 16.10.12:21:34
 */
public class HunterFish extends Mob implements ActionListener {

    private Image imageLeft = new ImageIcon("res/hunterFishLeft.png").getImage();
    private Image imageRight = new ImageIcon("res/hunterFishRight.png").getImage();

    private final int fishSpeed = 2;

    private Timer shootTimer;

    private static final float hp = 20;

    public HunterFish(Level level, int x, int y, Direction dir) {
        super(level, x, y, hp, Team.TeamTwo);
        setDir(dir);
        speed = fishSpeed;

        Weapon weapon = new RocketGun(this);
        setWeapon(weapon);

        initShootTimer();
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
        int shootDelay = 500 + random.nextInt(6000);
        return shootDelay;
    }

    public Image getImage() {
        if (getDir() == Direction.LEFT) {
            return imageLeft;
        } else {
            return  imageRight;
        }
    }

    public void onKeyDown(KeyEvent e) {
    }

    public void onKeyUp(KeyEvent e) {
    }
}
