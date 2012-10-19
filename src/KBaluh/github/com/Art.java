package KBaluh.github.com;

import javax.swing.*;
import java.awt.*;

/**
 * User: KBaluh
 * Date time: 19.10.12 11:41
 */
public class Art {

    // Backgrounds
    public static final Image background = loadImage("res/background.jpg");

    // Decorations
    public static final Image bubbleSmall = loadImage("res/bubble_small.png");
    public static final Image bubbleMiddle = loadImage("res/bubble_middle.png");
    public static final Image bubbleLarge = loadImage("res/bubble_large.png");

    // Bullets
    public static final Image rocketBulletLeft = loadImage("res/RocketBulletLeft.png");
    public static final Image rocketBulletRight = loadImage("res/RocketBulletRight.png");
    public static final Image rocketBulletExplosion1 = loadImage("res/expl1.png");
    public static final Image rocketBulletExplosion2 = loadImage("res/expl2.png");
    public static final Image rocketBulletExplosion3 = loadImage("res/expl3.png");
    public static final Image rocketBulletExplosion4 = loadImage("res/expl4.png");
    public static final Image rocketBulletExplosion5 = loadImage("res/expl5.png");

    // Mobs
    public static final Image playerLeft = loadImage("res/player_left.png");
    public static final Image playerRight = loadImage("res/player_right.png");

    public static final Image hunterFishLeft = loadImage("res/hunterFishLeft.png");
    public static final Image hunterFishRight = loadImage("res/hunterFishRight.png");

    // Support items
    public static final Image medicineChest = loadImage("res/MedicineChest.png");


    /**
     * Load image
     * @param patch - path for file, example : "res/background.jpg"
     * @return Image
     */
    public static Image loadImage(String patch) {
        return new ImageIcon(patch).getImage();
    }
}
