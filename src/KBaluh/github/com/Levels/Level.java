package KBaluh.github.com.Levels;

import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.GameScreen;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:26
 */
public abstract class Level implements Runnable {

    public GameScreen gameScreen;

    public Level(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public int getScreenHeight() {
        return gameScreen.getHeight();
    }

    public int getScreenWidth() {
        return gameScreen.getWidth();
    }

    public abstract void addEntity(Entity entity);
    public abstract void removeEntity(Entity entity);

    public abstract void onKeyDown(KeyEvent e);
    public abstract void onKeyUp(KeyEvent e);
    public abstract void paint(Graphics g);
    public abstract void tick();
    public abstract void run();
}
