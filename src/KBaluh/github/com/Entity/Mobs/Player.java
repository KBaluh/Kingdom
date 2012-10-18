package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Entity.Direction;
import KBaluh.github.com.Entity.Team;
import KBaluh.github.com.Weapons.RocketGun;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Author: kostya
 * Date: 16.10.12:18:57
 */
public class Player extends Mob {

    private Image imageLeft = new ImageIcon("res/player_left.png").getImage();
    private Image imageRight = new ImageIcon("res/player_right.png").getImage();
    private Image image = imageRight;

    private static final float hp = 100;

    public Player(int x, int y) {
        super(x, y, hp, Team.TeamOne);
        RocketGun weapon = new RocketGun(this);
        setWeapon(weapon);
        setDir(Direction.RIGHT);
    }

    public Image getImage() {
        return image;
    }

    public void tick() {
        if (!isLive()) {
            return;
        }

        if (level.canMove(getX() + dx, getY() + dy, getImageWidth(), getImageHeight())) {
            setX(getX() + dx);
            setY(getY() + dy);
        }
        weapon.tick();
    }

    public void onKeyUp(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_W :
                dy = 0;
                break;

            case KeyEvent.VK_UP :
                dy = 0;
                break;

            case KeyEvent.VK_S :
                dy = 0;
                break;

            case KeyEvent.VK_DOWN:
                dy = 0;
                break;

            case KeyEvent.VK_D :
                dx = 0;
                break;

            case KeyEvent.VK_RIGHT:
                dx = 0;
                break;

            case KeyEvent.VK_A :
                dx = 0;
                break;

            case KeyEvent.VK_LEFT :
                dx = 0;
                break;
        }
    }

    public void onKeyDown(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int ctrl = 17;

        switch (keyCode) {
            case KeyEvent.VK_W :
                dy = -speed;
                break;

            case KeyEvent.VK_UP :
                dy = -speed;
                break;

            case KeyEvent.VK_S :
                dy = speed;
                break;

            case KeyEvent.VK_DOWN :
                dy = speed;
                break;

            case KeyEvent.VK_D :
                dx = speed;
                image = imageRight;
                setDir(Direction.RIGHT);
                break;

            case KeyEvent.VK_RIGHT :
                dx = speed;
                image = imageRight;
                setDir(Direction.RIGHT);
                break;

            case KeyEvent.VK_A :
                dx = -speed;
                image = imageLeft;
                setDir(Direction.LEFT);
                break;

            case KeyEvent.VK_LEFT :
                dx = -speed;
                image = imageLeft;
                setDir(Direction.LEFT);
                break;

            case KeyEvent.VK_SPACE:
                weapon.useWeapon();
                break;
        }

        if (keyCode == ctrl) {
            weapon.useWeapon();
        }
    }
}
