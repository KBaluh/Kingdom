package KBaluh.github.com.Entity.Decorations;

import KBaluh.github.com.Entity.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:41
 */
public class Bubble extends Entity {

    private Image imageSmall = new ImageIcon("res/bubble_small.png").getImage();
    private Image imageMiddle = new ImageIcon("res/bubble_middle.png").getImage();
    private Image imageBig = new ImageIcon("res/bubble_large.png").getImage();
    private Image image;

    private int x;
    private int y;
    private int speed;
    private int dy;

    public Bubble(int x, int y, int speed, BubbleType type) {
        this.x = x;
        this.y = y;
        this.speed = speed;

        if (type == BubbleType.Big) {
            image = imageBig;
        } else if (type == BubbleType.Middle) {
            image = imageMiddle;
        } else {
            image = imageSmall;
        }
    }

    public void tick() {
        speed += dy;
        y -= speed;
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void onKeyUp(KeyEvent e) {
    }

    public void onKeyDown(KeyEvent e) {
    }
}
