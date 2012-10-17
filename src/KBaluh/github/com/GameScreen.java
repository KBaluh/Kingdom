package KBaluh.github.com;

import KBaluh.github.com.Levels.Level;
import KBaluh.github.com.Levels.StartupLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:21
 */
public class GameScreen extends JPanel implements ActionListener {

    private class GameKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            level.onKeyDown(e);
        }

        public void keyReleased(KeyEvent e) {
            level.onKeyUp(e);
        }
    }

    private static int gameTickInterval = 1000/60;
    private Timer gameTimer = new Timer(gameTickInterval, this);
    private Level level = new StartupLevel(this);

    public GameScreen() {
        setFocusable(true);
        addKeyListener(new GameKeyAdapter());
        gameTimer.start();
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Отрисовка графики
     * @param g
     */
    public void paint(Graphics g) {
        level.paint(g);
    }

    /**
     * Реализация интерфейса ActionListener
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        level.tick();
        repaint();
    }
}
