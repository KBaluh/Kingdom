package KBaluh.github.com.Entity.SupportItems;

import KBaluh.github.com.Art;
import KBaluh.github.com.Levels.Level;

import javax.swing.*;
import java.awt.*;

/**
 * User: KBaluh
 * Date time: 17.10.12 14:56
 */
public class MedicineChest extends SupportItem {

    private Image image = Art.medicineChest;

    private static int speed = 3;

    private static int bonus = 75;

    public MedicineChest(int x, int y) {
        super(x, y, speed, bonus);
    }

    public Image getImage() {
        return image;
    }
}
