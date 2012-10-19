package KBaluh.github.com.Levels;

import KBaluh.github.com.Art;
import KBaluh.github.com.Entity.Bullets.Bullet;
import KBaluh.github.com.Entity.Bullets.Explosion;
import KBaluh.github.com.Entity.Bullets.IExplosion;
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
import java.util.HashMap;
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

    private List<Entity> entitiesBack = new ArrayList<Entity>();

    /**
     * All entities on level
     */
    private List<Entity> entities = new ArrayList<Entity>();

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
     * Level victory conditions: max skips fish
     */
    private int maxFishSkips = 10;

    /**
     * Level victory condition: player skips fish count
     */
    private int fishSkips = 0;

    /**
     * Constructor
     * @param gameScreen - game screen is JPanel
     */
    public StartupLevel(GameScreen gameScreen) {
        super(gameScreen);

        int playerX = 300;
        int playerY = 200;
        player = new Player(playerX, playerY);
        addEntity(player);

        addSpawner(new BubbleSpawner());
        addSpawner(new HunterFishSpawner());
        addSpawner(new SupportItemSpawner());
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
    }

    /**
     * Add entity on pop layer
     * @param entity - Entity
     */
    public void addEntityPop(Entity entity) {
        entitiesPop.add(entity);
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
    }

    @Override
    public void tick() {
        if (!isGame()) {
            JOptionPane.showMessageDialog(null, "Game over, your scores: " + player.getScores());
            System.exit(1);
        }

        for (int s = 0; s < spawners.size(); ++s) {
            Spawner spawner = spawners.get(s);
            spawner.tick();
        }

        tickEntityLayer(entitiesBack);
        tickEntityLayer(entities);
        tickEntityLayer(entitiesPop);
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
                    fishSkips++;
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

            for (int j = 0; j < entities.size(); j++) {
                Entity entityMob = entities.get(j);
                if (entityMob instanceof Mob) {
                    Mob mob = (Mob) entityMob;
                    if (mob.getTeam() == bullet.getTeam()) {
                        continue;
                    }

                    if (mob.haveCollision(bullet)) {
                        mob.hurt(bullet.getDamage());
                        bullet.hit();
                        removeEntity(bullet);

                        if (bullet instanceof IExplosion) {
                            addEntity(new Explosion((IExplosion) bullet,
                                    bullet.getX(),
                                    bullet.getY()));
                        }

                        if (!mob.isLive()) {
                            if (!(mob instanceof Player)) {
                                player.addScores(mob.getScores());
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
     * Level condition to game.
     * @return if continue game - return true
     */
    private boolean isGame() {
        return (player.isLive() && (fishSkips <= maxFishSkips));
    }

    /**
     * Spawner layer
     * @param spawner - Entity spawner
     */
    private void addSpawner(Spawner spawner) {
        spawners.add(spawner);
        spawner.init(this);
    }

    /**
     * Draw information panel after all entities draw
     * @param g - graphics for paint
     */
    private void paintPanel(Graphics g) {
        g.setColor(Color.YELLOW);
        g.drawString("Entities: " + entities.size() +
                ", Player life: " + player.getHp() +
                ", Fish skips: " + fishSkips + "/" + maxFishSkips +
                ", Scores: " + player.getScores(), 10, 15);
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
}
