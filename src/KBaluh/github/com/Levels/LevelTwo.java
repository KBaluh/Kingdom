package KBaluh.github.com.Levels;

import KBaluh.github.com.Entity.Spawners.BubbleSpawner;
import KBaluh.github.com.Entity.Spawners.HunterFishSpawner;
import KBaluh.github.com.Entity.Spawners.SupportItemSpawner;
import KBaluh.github.com.GameScreen;

import java.awt.*;

/**
 * Author: KBaluh
 * Date: 20.10.12:15:15
 */
public class LevelTwo extends StartupLevel {
    public LevelTwo(GameScreen screen) {
        super(screen);
    }

    @Override
    protected void paintPanel(Graphics g) {
        super.paintPanel(g);
        int x = 10;
        int y = 30;
        g.drawString("Level 2", x, y);
    }

    @Override
    protected void initSpawners() {
        addSpawner(new BubbleSpawner());
        addSpawner(new HunterFishSpawner());
        addSpawner(new SupportItemSpawner());
    }

    @Override
    protected void initPlayer() {
        super.initPlayer();
    }

    @Override
    protected void initVictoryCondition() {
        victoryCondition.initCondition(10, 10);
    }

    @Override
    protected void victory() {
        super.victory();
    }
}
