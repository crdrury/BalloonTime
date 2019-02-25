import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class InteractivePanel extends JPanel implements MouseListener, MouseMotionListener, Runnable {
    public BalloonFrame frame;
    public Thread updateThread = new Thread(this);

    public abstract void mouseClicked(MouseEvent e);
    public abstract void mouseReleased(MouseEvent e);
    public abstract void mousePressed(MouseEvent e);
    public abstract void mouseEntered(MouseEvent e);
    public abstract void mouseExited(MouseEvent e);
    public abstract void mouseMoved(MouseEvent e);
    public abstract void mouseDragged(MouseEvent e);
    public abstract void run();
}