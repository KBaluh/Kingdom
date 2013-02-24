package KBaluh.github.com.Levels;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Bullets.Bullet;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Mobs.Mob;
import KBaluh.github.com.Entity.Mobs.Player;
import KBaluh.github.com.Entity.Spawners.BubbleSpawner;
import KBaluh.github.com.Entity.Spawners.HunterFishSpawner;
import KBaluh.github.com.Entity.Spawners.Spawner;
import KBaluh.github.com.Entity.Spawners.SupportItemSpawner;
import KBaluh.github.com.Entity.SupportItems.MedicineChest;
import KBaluh.github.com.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:37
 */
public class StartupLevel extends Level {

    /**
     * Image for paint background
     */
    private Image background = Art.background;

    /**
     * Entities on back layer
     */
    private List<Entity> entitiesBack = new ArrayList<Entity>();

    /**
     * Entities on center layer
     */
    private List<Entity> entities = new ArrayList<Entity>();

    /**
     * Entities on pop layer
     */
    private List<Entity> entitiesPop = new ArrayList<Entity>();

    /**
     * All spawners on level
     */
    private List<Spawner> spawners = new ArrayList<Spawner>();

    /**
     * User player
     */
    private Player player;

    /**
     * Count for current level maximum fish skipped
     */
    private int maxFishSkipped = 10;

    /**
     * Need kill mobs, for next level
     */
    private int maxKills = 5;

    /**
     * Current battle on level
     */
    private int battleNumber = 1;

    /**
     * Maximum battles on level
     */
    private static final int maxBattles = 3;

    /**
     * Victory conditions for level
     */
    protected VictoryCondition victoryCondition = new VictoryCondition();

    /**
     * Text to show on battle number
     */
    private String battleShowText = "";

    /**
     * Max time life to show battle text
     */
    private final int battleMaxShowTime = 120;

    /**
     * Current battle show time life
     */
    private int battleShowTimeLife = battleMaxShowTime;

    /**
     * Constructor
     * @param gameScreen - game screen is JPanel
     */
    public StartupLevel(GameScreen gameScreen) {
        super(gameScreen);
        initVictoryCondition();
        initPlayer();
        initSpawners();
        showBattleText();
    }

    /**
     * Level done after 3 battles on level
     * @return level is done
     */
    public boolean levelIsDone() {
        return (battleNumber > 3);
    }

    /**
     * Clear all entities
     */
    public void levelStop() {
        spawners.clear();
        entities.clear();
        entitiesBack.clear();
        entitiesPop.clear();
    }

    /**
     * Spawner layer
     * @param spawner - Entity spawner
     */
    public void addSpawner(Spawner spawner) {
        spawners.add(spawner);
        spawner.init(this);
    }

    /**
     * Add entity on entities list and init entity level
     * @param entity - see Entity
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.init(this);
    }

    /**
     * Add entity on back layer
     * @param entity - Entity
     */
    public void addEntityBack(Entity entity) {
        entitiesBack.add(entity);
        entity.init(this);
    }

    /**
     * Add entity on pop layer
     * @param entity - Entity
     */
    public void addEntityPop(Entity entity) {
        entitiesPop.add(entity);
        entity.init(this);
    }

    /**
     * Mark to remove entity
     * @param entity - entity from entities
     */
    public void removeEntity(Entity entity) {
        entity.remove();
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        player.onKeyDown(e);
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        player.onKeyUp(e);
    }

    @Override
    public void paint(Graphics g) {
        // Paint background layer
        g.drawImage(background, 0, 0, null);

        for (int i = 0; i < entitiesBack.size(); ++i) {
            Entity entity = entitiesBack.get(i);
            paintEntity(g, entity);
        }

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            paintEntity(g, entity);
        }

        for (int i = 0; i < entitiesPop.size(); ++i) {
            Entity entity = entitiesPop.get(i);
            paintEntity(g, entity);
        }

        // Paint information panel
        paintPanel(g);

        paintBattleText(g);
    }

    /**
     * Get of player scores
     * @return player scores
     */
    public int getPlayerScores() {
        return player.getScores();
    }

    @Override
    public void tick() {
        if (victoryCondition.playerFail() || player.getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Game over, your scores: " + player.getScores());
            System.exit(1);
        }

        if (battleShowTimeLife >= 0) {
            --battleShowTimeLife;
            if (battleShowTimeLife == 0) {
                hideBattleText();
            }
        }

        if (victoryCondition.isVictory()) {
            victory();
        }

        for (int s = 0; s < spawners.size(); ++s) {
            Spawner spawner = spawners.get(s);
            spawner.tick();
        }

        tickDecorationLayer(entitiesBack);
        tickEntityLayer(entities);
        tickDecorationLayer(entitiesPop);
    }

    /**
     * @return Return level number
     */
    public int getLevelNumber() {
        return 1;
    }

    /**
     * Check if the player outside level
     * @param x - player coordinate x
     * @param y - player coordinate y
     * @param width - player width
     * @param height - player height
     * @return player can move
     */
    public boolean canMove(int x, int y, int width, int height) {
        return ((x >= 0 && x <= gameScreen.getWidth() - width) &&
                (y >= 0 && y <= gameScreen.getHeight() - height));
    }

    /**
     * Remove old and set new player on level.
     * Add player on entities list
     * @param player - new player
     */
    protected void setPlayer(Player player) {
        entities.remove(this.player);
        addEntity(player);
        this.player = player;
    }

    /**
     * Player of level
     * @return player
     */
    protected Player getPlayer() {
        return player;
    }

    /**
     * Event of level is victory
     */
    protected void victory() {
        if (battleNumber <= maxBattles) {
            maxKills = maxKills * 2;
            ++battleNumber;
            showBattleText();
        }

        for (int i = 0; i < spawners.size(); ++i) {
            if (spawners.get(i) instanceof HunterFishSpawner) {
                Spawner spawner = spawners.get(i);
                if (battleNumber == 2) {
                    spawner.setBaseInterval(spawner.getBaseInterval() - 20);
                    player.increaseHp(player.getMaxHp());
                } else
                if (battleNumber == 3) {
                    spawner.setBaseInterval(spawner.getBaseInterval() - 20);
                    player.increaseHp(player.getMaxHp());
                }
            }
        }

        victoryCondition.initCondition(maxFishSkipped, maxKills);
    }

    /**
     * Init spawners on level
     */
    protected void initSpawners() {
        addSpawner(new BubbleSpawner());
        addSpawner(new SupportItemSpawner());

        HunterFishSpawner hunterFishSpawner = new HunterFishSpawner();
        addSpawner(hunterFishSpawner);
    }

    /**
     * Init player for level
     */
    protected void initPlayer() {
        int playerX = 100;
        int playerY = 200;
        Player player = new Player(playerX, playerY);
        setPlayer(player);
    }

    /**
     * Init victory conditions for level
     */
    protected void initVictoryCondition() {
        victoryCondition.initCondition(maxFishSkipped, maxKills);
    }

    /**
     * Draw information panel after all entities draw
     * @param g - graphics for paint
     */
    protected void paintPanel(Graphics g) {
        int fontSize = 14;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g.setFont(font);

        Color color = Color.WHITE;
        g.setColor(color);

        g.drawString("Level: " + getLevelNumber() + ", Battle: " + battleNumber +
                ", Missed fish: " + victoryCondition.getFishSkipped() + "/" + victoryCondition.getMaxFishSkipped() +
                ", Killed fish: " + victoryCondition.getKillCount() + "/" + victoryCondition.getMaxKillCount() +
                ", Scores: " + player.getScores(), 10, 20);

        float maxHp = player.getMaxHp();
        float hp = player.getHp();

        if (hp <= (maxHp * 80 / 100)) {
            color = Color.YELLOW;
        }
        if (hp <= (maxHp * 30 / 100)) {
            color = Color.RED;
        }
        g.setColor(color);

        g.drawString("Player life: " + player.getHp(), 10, 35);
    }

    /**
     * Set a background fon
     * @param image - background image
     */
    protected void setBackground(Image image) {
        background = image;
    }

    /**
     * Check if the mob outside level
     * @param x - mob coordinate x
     * @param y - mob coordinate y
     * @param width - mob width
     * @param height - mob height
     * @return can move
     */
    private boolean canMoveMob(int x, int y, int width, int height) {
        int offsetX = -width;
        int offsetY = -height;
        return ((x >= offsetX && x <= gameScreen.getWidth() + width) &&
                (y >= offsetY && y <= gameScreen.getHeight()));
    }

    /**
     * Check is can move. If not - remove
     * @param entity - with entities list
     * @return result - true if can move, false is not
     */
    private boolean handleCanMove(Entity entity) {
        boolean result = true;
        if (!(entity instanceof Player)) {
            if (!canMoveMob(entity.getX(), entity.getY(),
                    entity.getImageWidth(), entity.getImageHeight())) {
                if (entity instanceof Mob) {
                    victoryCondition.addSkippedFish();
                }
                removeEntity(entity);
                result = false;
            }
        }
        return result;
    }

    /**
     * Check if entity is support item
     * @param entity - with entities list
     */
    private void handleSupportItem(Entity entity) {
        if (entity instanceof MedicineChest) {
            if (entity.haveCollision(player)) {
                player.increaseHp(((MedicineChest) entity).getBonus());
                removeEntity(entity);
            }
        }
    }

    /**
     * Handle Bullet collision
     * @param entity - with entities list
     */
    private void handleBulletCollision(Entity entity) {
        if (entity instanceof Bullet) {
            Bullet bullet = (Bullet) entity;

            for (int i = 0; i < entities.size(); i++) {
                Entity entityMob = entities.get(i);
                if (entityMob instanceof Mob) {
                    Mob mob = (Mob) entityMob;
                    if (mob.getTeam() == bullet.getTeam()) {
                        continue;
                    }

                    if (mob.haveCollision(bullet)) {
                        mob.hurt(bullet.getDamage());
                        bullet.hit();
                        removeEntity(bullet);

                        if (!mob.isLive()) {
                            if (!(mob instanceof Player)) {
                                player.addScores(mob.getScores());
                                victoryCondition.addKill();
                                removeEntity(mob);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Paint entity on level
     * @param g - Graphics
     * @param entity - Entity
     */
    private void paintEntity(Graphics g, Entity entity) {
        Image image = entity.getImage();
        if (image != null) {
            g.drawImage(image, entity.getX(), entity.getY(), null);
        }
    }

    /**
     * Tick entity layer
     * @param entityList - List<Entity>
     */
    private void tickEntityLayer(List<Entity> entityList) {
        for (int i = 0; i < entityList.size(); ++i) {
            Entity entity = entityList.get(i);
            if (entity.isRemoved())
            {
                entityList.remove(i);
                continue;
            }

            // Call entity tick
            entity.tick();

            handleCanMove(entity);
            handleBulletCollision(entity);
            handleSupportItem(entity);
        }
    }

    /**
     * Decoration layer is pop and back
     * @param entityList
     */
    private void tickDecorationLayer(List<Entity> entityList) {
        for (int i = 0; i < entityList.size(); ++i) {
            Entity entity = entityList.get(i);
            if (entity.isRemoved())
            {
                entityList.remove(i);
                continue;
            }

            entity.tick();
            handleCanMove(entity);
        }
    }

    /**
     * Set battle text
     */
    private void showBattleText() {
        battleShowTimeLife = battleMaxShowTime;
        battleShowText = "Level: " + getLevelNumber() + ", Battle: " + battleNumber;
    }

    /**
     * Set empty battle text
     */
    private void hideBattleText() {
        battleShowText = "";
    }

    private void paintBattleText(Graphics g) {
        int fontSize = 28;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g.setFont(font);
        g.setColor(Color.WHITE);
        Dimension dim = new Dimension(gameScreen.getWidth(), gameScreen.getHeight());
        g.drawString(battleShowText, dim.width/2 - 100, dim.height/2/2);
    }
}
