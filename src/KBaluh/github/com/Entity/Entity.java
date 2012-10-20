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

    public void paint(Graphics g) {
    }

    public abstract Image getImage();
    public abstract void tick();
}
