import javax.swing.*;
import java.awt.*;

public class BalloonFrame extends JFrame {
    int width = 1600, height = 900;                         // Frame resolution
    public static final int TITLE_SCREEN = 0;
    public static final int GAME_SCREEN = 1;
    InteractivePanel currentPanel = null;

    public BalloonFrame() {
        // Frame setup stuff
        super("Balloons");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the frame on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);

        setView(0);

        setVisible(true);

    }

    // Swap between title and game screens
    public void setView(int view) {
        if (currentPanel != null)
            remove(currentPanel);

        InteractivePanel panel = null;

        if (view == TITLE_SCREEN) {
            panel = new TitlePanel(this);
        } else if (view == GAME_SCREEN) {
            panel = new BalloonPanel(this);
        }


        if (panel != null) {
            add(panel, BorderLayout.CENTER);
            panel.setSize(getSize());
            panel.addMouseListener(panel);
            panel.addMouseMotionListener(panel);
            panel.updateThread.start();
            currentPanel = panel;
            update(getGraphics());
        }
    }

    public static void main(String[] args) {
        new BalloonFrame();
    }
}