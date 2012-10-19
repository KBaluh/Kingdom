package KBaluh.github.com.Entity.Decorations;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:41
 */
public class Bubble extends Entity {

    private Image imageSmall = Art.bubbleSmall;
    private Image imageMiddle = Art.bubbleMiddle;
    private Image imageBig = Art.bubbleLarge;
    private Image image;

    private int x;
    private int y;
    private int speed;
    private int dy;

    public BubbleType type = BubbleType.Small;

    public Bubble(int x, int y, int speed, BubbleType type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.type = type;

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
