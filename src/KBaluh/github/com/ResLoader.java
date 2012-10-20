package KBaluh.github.com;

import javax.swing.*;
import java.awt.*;

public class ResLoader {

    /**
     * Load image
     * @param patch - path for file, example : "res/background.jpg"
     * @return Image
     */
    public Image loadImage(String patch) {
        return new ImageIcon(getClass().getClassLoader().getResource(patch)).getImage();
    }
}
