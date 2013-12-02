package KBaluh.github.com.Entity.Mobs;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.PaintBehavior;

import java.awt.*;

public class PaintMobWithHp implements PaintBehavior {

    @Override
    public void paint(Graphics g, Entity entity) {
        if (g == null)
            return;
        if (entity == null)
            return;

        Mob mob = (Mob) entity;
        Image image = mob.getImage();
        if (image == null) {
            return;
        }

        g.drawImage(image, mob.getX(), mob.getY(), null);

        float maxHp = mob.getMaxHp();
        float hp = mob.getHp();
        boolean greenHp = (hp >= (maxHp * .8f));
        if (greenHp) {
            return;
        }

        boolean yellowHp = (!greenHp && hp >= maxHp * .30f);

        if (yellowHp) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }

        int x = mob.getX() + mob.getImageWidth() / 2;
        int y = mob.getY() + mob.getImageHeight();
        g.drawString("" + (int)mob.getHp(),x, y);
    }
}
