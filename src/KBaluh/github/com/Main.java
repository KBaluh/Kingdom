package KBaluh.github.com;

import javax.swing.*;

/**
 * User: KBaluh
 * Date time: 16.10.12 17:17
 */
public class Main {

    public static String GAME_TITLE = "Kingdom";
    public static int GAME_WIDTH = 800;
    public static int GAME_HEIGHT = 600;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(GAME_TITLE);
        frame.setSize(GAME_WIDTH, GAME_HEIGHT);
        frame.setResizable(false);
        GameScreen screen = new GameScreen();
        frame.add(screen);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.out.println("Unable to load Windows look and feel");
        }

        java.awt.Dimension dim = frame.getToolkit().getScreenSize();
        frame.setLocation(dim.width/2 - frame.getWidth()/2,
                dim.height/2 - frame.getHeight()/2);

        frame.setVisible(true);
    }
}
