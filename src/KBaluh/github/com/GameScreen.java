package KBaluh.github.com;

import KBaluh.github.com.Levels.Level;
import KBaluh.github.com.Levels.LevelManager;

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

    private LevelManager levelManager = new LevelManager(this);
    private Level level;

    public GameScreen() {
        level = levelManager.first();
        addKeyListener(new GameKeyAdapter());
        setFocusable(true);
        gameTimer.start();
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
        if (level.levelIsDone()) {
            level.levelStop();
            if (levelManager.isNext()) {
                level = levelManager.next();
            } else {
                gameTimer.stop();
                JOptionPane.showMessageDialog(null, "Game is done!");
                System.exit(1);
            }
        } else {
            level.tick();
        }
        repaint();
    }
}
