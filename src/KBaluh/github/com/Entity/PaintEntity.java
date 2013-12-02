package KBaluh.github.com.Entity;

import java.awt.*;

public class PaintEntity implements PaintBehavior {

    @Override
    public void paint(Graphics g, Entity entity) {
        if (entity == null)
            return;
        Image image = entity.getImage();
        if (image == null)
            return;

        g.drawImage(image, entity.getX(), entity.getY(), null);
    }
}
