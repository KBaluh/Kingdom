package KBaluh.github.com.Levels;

import KBaluh.github.com.Entity.Mobs.Player;
import KBaluh.github.com.Entity.Spawners.BubbleSpawner;
import KBaluh.github.com.Entity.Spawners.GreenFishSpawner;
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
    }

    @Override
    protected void initSpawners() {
        addSpawner(new BubbleSpawner());

        SupportItemSpawner supportItemSpawner = new SupportItemSpawner();
        supportItemSpawner.setBaseInterval(900);
        addSpawner(supportItemSpawner);

        addSpawner(new HunterFishSpawner());

        GreenFishSpawner greenFishSpawner = new GreenFishSpawner();
        greenFishSpawner.setBaseInterval(400);
        addSpawner(greenFishSpawner);
    }

    @Override
    protected void initPlayer() {
        super.initPlayer();
        Player player = getPlayer();
        player.setMaxHp(150);
        player.increaseHp(player.getMaxHp());
    }

    @Override
    protected void initVictoryCondition() {
        victoryCondition.initCondition(10, 10);
    }

    @Override
    protected void victory() {
        super.victory();
    }

    @Override
    public int getLevelNumber() {
        return 2;
    }
}
