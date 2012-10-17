package KBaluh.github.com.Levels;

import KBaluh.github.com.Entity.Bullets.Bullet;
import KBaluh.github.com.Entity.Entity;
import KBaluh.github.com.Entity.Mobs.Mob;
import KBaluh.github.com.Entity.Mobs.Player;
import KBaluh.github.com.Entity.Spawners.BubbleSpawner;
import KBaluh.github.com.Entity.Spawners.HunterFishSpawner;
import KBaluh.github.com.Entity.Spawners.Spawner;
import KBaluh.github.com.Entity.Spawners.SupportItemSpawner;
import KBaluh.github.com.Entity.SupportItems.IBonusReceiver;
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

    private Image background = new ImageIcon("res/background.jpg").getImage();

    private List<Entity> entities = new ArrayList<Entity>();
    private Player player;

    private List<Spawner> spawners = new ArrayList<Spawner>();

    private int maxFishSkips = 10;
    private int fishSkips = 0;

    public StartupLevel(GameScreen gameScreen) {
        super(gameScreen);

        int playerX = 300;
        int playerY = 200;
        player = new Player(this, playerX, playerY);
        addEntity(player);

        spawners.add(new BubbleSpawner(this));
        spawners.add(new HunterFishSpawner(this));
        spawners.add(new SupportItemSpawner(this));
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
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
        g.drawImage(background, 0, 0, null);
        for (Entity entity : entities) {
            Image image = entity.getImage();
            g.drawImage(image, entity.getX(), entity.getY(), null);
        }
        g.drawImage(player.getImage(), player.getX(), player.getY(), null);

        g.setColor(Color.YELLOW);
        g.drawString("Entities: " + entities.size() +
                ", Player life: " + player.getHp() +
                ", Fish skips: " + fishSkips + "/" + maxFishSkips, 10, 15);
    }

    @Override
    public void tick() {
        if (!isGame()) {
            JOptionPane.showMessageDialog(null, "You lose!");
            System.exit(1);
        }

        player.tick();

        for (Spawner spawner : spawners) {
            spawner.tick();
        }

        for (Entity entity : entities) {
            entity.tick();
        }

        checkEntitiesSupportItemCollision();
        checkEntitiesBulletCollision();
        checkEntitiesPosition();
    }

    private void checkEntitiesSupportItemCollision() {
        List<Entity> itemsRemove = new ArrayList<Entity>();
        for (Entity bonusEntity : entities) {
            if (bonusEntity instanceof MedicineChest) {
                if (bonusEntity.haveCollision(player)) {
                    player.increaseHp(((MedicineChest) bonusEntity).getBonus());
                    itemsRemove.add(bonusEntity);
                    break;
                }
            }
        }
        for (Entity entity : itemsRemove) {
            removeEntity(entity);
        }
        itemsRemove.clear();
    }

    private void checkEntitiesBulletCollision() {
        List<Entity> removeEntity = new ArrayList<Entity>();

        for (Entity entity : entities) {
            if (entity instanceof Bullet) {
                Bullet bullet = (Bullet) entity;

                for (Entity entityMob : entities) {
                    if (entityMob instanceof Mob) {
                        Mob mob = (Mob) entityMob;
                        if (mob.getTeam() != bullet.getTeam()) {
                            if (mob.haveCollision(bullet)) {
                                mob.hurt(bullet.getDamage());
                                bullet.hit();
                                removeEntity.add(bullet);
                                if (!mob.isLive()) {
                                    if (!(mob instanceof Player)) {
                                        removeEntity.add(mob);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Entity entity : removeEntity) {
            removeEntity(entity);
        }
        removeEntity.clear();
    }

    private void checkEntitiesPosition() {
        List<Entity> removeList = new ArrayList<Entity>();

        for (Entity entity : entities) {
            if (entity != null) {
                int top = -10;
                int bottom = getScreenHeight() + 10;
                int left = -10;
                int right = getScreenWidth() + 10;

                if (entity.getY() >= bottom || entity.getY() <= top) {
                    if (!(entity instanceof Player)) {
                        if (entity instanceof Mob) {
                            fishSkips++;
                        }
                        removeList.add(entity);
                    }
                } else if (entity.getX() <= left || entity.getX() >= right) {
                    if (!(entity instanceof Player)) {
                        if (entity instanceof Mob) {
                            fishSkips++;
                        }
                        removeList.add(entity);
                    }
                }
            }
        }

        for (Entity entity : removeList) {
            removeEntity(entity);
        }
        removeList.clear();
    }

    private boolean isGame() {
        return (player.isLive() && (fishSkips <= maxFishSkips));
    }
}
