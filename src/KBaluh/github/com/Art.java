package KBaluh.github.com;

import java.awt.*;

/**
 * User: KBaluh
 * Date time: 19.10.12 11:41
 */
public class Art {

    private static ResLoader resLoader = new ResLoader();

    // Backgrounds
    public static final Image background = resLoader.loadImage("res/background.jpg");
    public static final Image background2 = resLoader.loadImage("res/background2.jpg");
    public static final Image background3 = resLoader.loadImage("res/background3.jpg");

    // Decorations
    public static final Image bubbleSmall = resLoader.loadImage("res/bubble_small.png");
    public static final Image bubbleMiddle = resLoader.loadImage("res/bubble_middle.png");
    public static final Image bubbleLarge = resLoader.loadImage("res/bubble_large.png");

    // Bullets
    public static final Image rocketBulletLeft = resLoader.loadImage("res/RocketBulletLeft.png");
    public static final Image rocketBulletRight = resLoader.loadImage("res/RocketBulletRight.png");
    public static final Image rocketBulletExplosion1 = resLoader.loadImage("res/expl1.png");
    public static final Image rocketBulletExplosion2 = resLoader.loadImage("res/expl2.png");
    public static final Image rocketBulletExplosion3 = resLoader.loadImage("res/expl3.png");
    public static final Image rocketBulletExplosion4 = resLoader.loadImage("res/expl4.png");
    public static final Image rocketBulletExplosion5 = resLoader.loadImage("res/expl5.png");

    // Mobs
    public static final Image playerLeft = resLoader.loadImage("res/player_left.png");
    public static final Image playerRight = resLoader.loadImage("res/player_right.png");

    public static final Image hunterFishLeft = resLoader.loadImage("res/hunterFishLeft.png");
    public static final Image hunterFishRight = resLoader.loadImage("res/hunterFishRight.png");

    public static final Image greenFishLeft = resLoader.loadImage("res/greenFishLeft.png");
    public static final Image greenFishRight = resLoader.loadImage("res/greenFishRight.png");

    public static final Image topFish = resLoader.loadImage("res/TopFish.png");

    // Support items
    public static final Image medicineChest = resLoader.loadImage("res/MedicineChest.png");
    public static final Image seaMine = resLoader.loadImage("res/SeaMine.png");
}
