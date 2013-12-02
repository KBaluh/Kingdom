package KBaluh.github.com.Entity;

import KBaluh.github.com.Levels.Level;

import java.awt.*;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:41
 */
public abstract class Entity {

    public Level level;

    private int x;
    private int y;

    private boolean removed = false;

    private PaintBehavior paintBehavior;

    public Entity() {
        paintBehavior = new PaintEntity();
    }

    public PaintBehavior getPaintBehavior() {
        if (paintBehavior == null) {
            paintBehavior = new EmptyPaint();
        }
        return paintBehavior;
    }

    public void setPaintBehavior(PaintBehavior paintBehavior) {
        this.paintBehavior = paintBehavior;
    }

    public void remove() {
        removed = true;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void init(Level level) {
        this.level = level;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getImageWidth() {
        int width = 0;
        Image image = getImage();
        if (image == null) {
            return width;
        } else {
            return image.getWidth(null);
        }
    }

    public int getImageHeight() {
        int height = 0;
        Image image = getImage();
        if (image == null) {
            return height;
        } else {
            return image.getHeight(null);
        }
    }

    Rectangle getRectangle() {
        return new Rectangle(getX(), getY(), getImageWidth(), getImageHeight());
    }

    public boolean haveCollision(Entity entity) {
        return getRectangle().intersects(entity.getRectangle());
    }

    public abstract Image getImage();
    public abstract void tick();

    public void paint(Graphics g) {
        getPaintBehavior().paint(g, this);
    }
}
