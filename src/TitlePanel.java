import java.awt.*;
import java.awt.event.MouseEvent;

public class TitlePanel extends InteractivePanel {
    Image background;
    Rectangle[] button;

    public TitlePanel(BalloonFrame frame) {
        this.frame = frame;

        background = Toolkit.getDefaultToolkit().createImage(getClass().getResource("TitleScreen.png"));

        button = new Rectangle[2];
        button[0] = new Rectangle(1136, 508, 330, 50);
        button[1] = new Rectangle(1128, 576, 222, 50);
    }

    public void run() {
        while (true) {
            repaint();

            try {
                Thread.sleep(33);
            } catch (Exception e) {
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        int mX = e.getX();
        int mY = e.getY();
        for (int i = 0; i < button.length; i++) {
            if (mX > button[i].x
                    && mX < button[i].x + button[i].width
                    && mY > button[i].y
                    && mY < button[i].y + button[i].height) {

                if (i == 0) {
                    frame.setView(1);
                } else if (i == 1) {
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
    }
}
