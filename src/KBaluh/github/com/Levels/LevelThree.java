package KBaluh.github.com.Levels;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Mobs.Player;
import KBaluh.github.com.Entity.Spawners.*;
import KBaluh.github.com.GameScreen;

import java.awt.*;

public class LevelThree extends StartupLevel {

    private static final Image background = Art.background3;

    public LevelThree(GameScreen screen) {
        super(screen);
        setBackground(background);
    }

    @Override
    protected void paintPanel(Graphics g) {
        super.paintPanel(g);
    }

    @Override
    protected void initSpawners() {
        addSpawner(new BubbleSpawner());

        SupportItemSpawner supportItemSpawner = new SupportItemSpawner();
        supportItemSpawner.setBaseInterval(850);
        addSpawner(supportItemSpawner);

        GreenFishSpawner greenFishSpawner = new GreenFishSpawner();
        greenFishSpawner.setBaseInterval(420);
        addSpawner(greenFishSpawner);
        addSpawner(new GreenFishSpawner());

        SeaMineSpawner seaMineSpawner = new SeaMineSpawner();
        seaMineSpawner.setBaseInterval(200);
        addSpawner(seaMineSpawner);

        addSpawner(new TopFishMineSpawner());
    }

    @Override
    protected void initPlayer() {
        super.initPlayer();
        Player player = getPlayer();
        player.setMaxHp(180);
        player.increaseHp(player.getMaxHp());
    }

    @Override
    protected void initVictoryCondition() {
        victoryCondition.initCondition(10, 12);
    }

    @Override
    public int getLevelNumber() {
        return 3;
    }
}
