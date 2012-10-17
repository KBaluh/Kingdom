package KBaluh.github.com.Levels;

import KBaluh.github.com.Entity.*;
import KBaluh.github.com.Entity.Decorations.Bubble;
import KBaluh.github.com.Entity.Decorations.BubbleType;
import KBaluh.github.com.Entity.Mobs.HunterFish;
import KBaluh.github.com.Entity.Mobs.Mob;
import KBaluh.github.com.Entity.Mobs.Player;
import KBaluh.github.com.GameScreen;
import KBaluh.github.com.Entity.Bullets.Bullet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:37
 */
public class StartupLevel extends Level {

    private Image background = new ImageIcon("res/background.jpg").getImage();

    private List<Entity> entities = new ArrayList<Entity>();
    private Thread spawnerTheard = new Thread(this);

    private int playerX = 300;
    private int playerY = 200;
    private Player player;

    private int maxFishSkips = 10;
    private int fishSkips = 0;

    public StartupLevel(GameScreen gameScreen) {
        super(gameScreen);

        player = new Player(this, playerX, playerY);
        addEntity(player);

        spawnerTheard.start();
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
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
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

        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            entity.tick();
        }
        player.tick();

        checkEntitiesBulletCollision();
        checkEntitiesPosition();
    }

    @Override
    public void run() {
        while (true) {
            Random random = new Random();
            try {
                Thread.sleep(random.nextInt(3000));
                addBubble();
                addHunterFish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addBubble() {
        Random random = new Random();
        int bubbleSpeed = random.nextInt(3) + 1;
        BubbleType bubbleType;
        if (bubbleSpeed == 1) {
            bubbleType = BubbleType.Small;
        } else
        if (bubbleSpeed == 2) {
            bubbleType = BubbleType.Middle;
        } else {
            bubbleType = BubbleType.Big;
        }
        entities.add(new Bubble(random.nextInt(800), random.nextInt(600), bubbleSpeed, bubbleType));
    }

    private void addHunterFish() {
        Random random = new Random();
        int y = random.nextInt(600);
        int x = gameScreen.getWidth() + 10;
        Entity entity = new HunterFish(this, x, y, Direction.LEFT);
        entities.add(entity);
    }

    private void checkEntitiesBulletCollision() {
        Entity entityMobToRemove = null;
        Entity entityBulletToRemove = null;

        List<Entity> removeEntity = new ArrayList<Entity>();

        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity instanceof Bullet) {
                Bullet bullet = (Bullet) entity;

                Iterator<Entity> mobIterator = entities.iterator();
                while (mobIterator.hasNext()) {
                    Entity entityMob = mobIterator.next();
                    if (entityMob instanceof Mob) {
                        Mob mob = (Mob) entityMob;
                        if (mob.getTeam() != bullet.getTeam()) {
                            if (mob.haveCollision(bullet)) {
                                mob.hurt(bullet.getDamage());
                                bullet.hit();
                                removeEntity.add(bullet);
                                if (!mob.isLive()) {
                                    if (entityMobToRemove instanceof Player == false) {
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

        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity != null) {
                int top = - 10;
                int bottom = getScreenHeight() + 10;
                int left = -10;
                int right = getScreenWidth() + 10;

                if (entity.getY() >= bottom || entity.getY() <= top) {
                    if (entity instanceof Player == false) {
                        if (entity instanceof Mob) {
                            fishSkips++;
                        }
                        removeList.add(entity);
                    }
                } else
                if (entity.getX() <= left || entity.getX() >= right) {
                    if (entity instanceof Player == false) {
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
        if (!player.isLive()) {
            return false;
        }
        if (fishSkips >= maxFishSkips) {
            return false;
        }
        return true;
    }
}
