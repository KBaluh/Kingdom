package KBaluh.github.com.Entity.Bullets;

import java.awt.Image;

/**
 * User: KBaluh
 * Date time: 17.10.12 16:26
 */
public interface IExplosion {
    int getFrames();
    int getInterval();
    Image getImageByFrame(int frame);
}
