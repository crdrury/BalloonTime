import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class BalloonPanel extends InteractivePanel {
    public BalloonFrame frame;
    boolean gameRunning = true;                                     // Killswitch for endgame state
    ArrayList<Balloon> balloon = new ArrayList<Balloon>();                // The balloons, obviously
    float countDown = 0f;                                           // Timer counting down to next balloon spawn
    float maxRadius = 150;                                          // Balloons grow to this radius, then stop
    float squashBy = 10;                                            // When clicked, a balloon's radius is reduced by this many pixels
    float pixelAllowance = 5;                                       // Count a click even if you "miss" by this many pixels
    int score = 0;                                                  // Increases while no balloons are touching, based on the total combined balloon size
    int spawnTries = 0, maxTries = 10;                              // Controls how many times to try spawning a balloon before giving up
    Color stringColor = new Color(255, 255, 255, 100);
    Random rand = new Random();
    Image skyImage;
    Image[] balloonCoverImage = new Image[3];
    Image balloonBlankImage;
    Image balloonEyeImage;
    Point mousePos = new Point(0, 0);
    int panelHeight;

    float eyeMax = .15f;

    // Double buffering variables
    Image offscreenImage;
    Graphics2D offscreenGraphics;

    public BalloonPanel(BalloonFrame frame) {
        this.frame = frame;

        skyImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("Sky.png"));
        for (int i = 0; i < balloonCoverImage.length; i++) {
            balloonCoverImage[i] = Toolkit.getDefaultToolkit().createImage(getClass().getResource("BalloonCover" + i + ".png"));
        }
        balloonBlankImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("BalloonBlank.png"));
        balloonEyeImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("BalloonEyes.png"));

        panelHeight = frame.height;
    }

    public void mousePressed(MouseEvent e) {
        if (!gameRunning) {
            frame.setView(0);
        } else {

            // Store mouse position
            int x = e.getX();
            int y = e.getY();

            // Check if the click was within the pixel allowance of each balloon and if so, decrease the radius of that balloon
            for (Balloon b : new ArrayList<Balloon>(balloon)) {
                if (distSquared(x, y, b.x, b.correctedY()) <= Math.pow(b.radius + pixelAllowance, 2)) {
                    b.radius -= squashBy;
                    if (b.radius <= 0)
                        balloon.remove(b);
                }
            }
        }
    }

    // Returns a random int between min and max
    public int randomRange(int min, int max) {
        return rand.nextInt(max - min) + min;
    }

    // Returns a random float between min and max
    public float randomFloat(float min, float max) {
        return rand.nextFloat() * (max - min) + min;
    }

    // Comparable distance between two points, cutting out a call to Math.sqrt to save computing time
    public float distSquared(float x1, float y1, float x2, float y2) {
        float xDist = x1 - x2;
        float yDist = y1 - y2;
        return (xDist * xDist) + (yDist * yDist);
    }

    // Spawns a new balloon that's not right next to any other balloon
    public void spawnBalloon() {
        // Creates a test balloon at a random location onscreen with a random color
        Balloon newBalloon = new Balloon(randomRange((int) maxRadius, getWidth() - (int) maxRadius), randomRange((int) maxRadius, getHeight() - (int) maxRadius), randomFloat(0.01f, 0.1f), new Color(randomRange(0, 255), randomRange(0, 255), randomRange(0, 255)));

        // Check if this balloon is unfairly close to another balloon
        boolean tooClose = false;
        float scaleSquared = 1.5f;
        for (Balloon b : balloon) {
            if (distSquared(b.x, b.correctedY(), newBalloon.x, newBalloon.correctedY()) < maxRadius * maxRadius * scaleSquared) {
                tooClose = true;
                break;
            }
        }

        // If it's too close to another balloon, try again. If not, add this balloon and carry on
        if (tooClose) {
            if (spawnTries < maxTries) {
                spawnTries++;
                spawnBalloon();
            }
            else
                spawnTries = 0;
        } else {
            balloon.add(newBalloon);
            spawnTries = 0;
        }
    }

    // When two balloons have touched, change the other balloons to the background color
    public void hideOtherBalloons(int b1, int b2) {
        for (int i = 0; i < balloon.size(); i++) {
            if (i != b1 && i != b2) {
                balloon.get(i).color = Color.cyan;
            }
        }
    }

    public void run() {
        while (gameRunning) {
            if (countDown <= 0) {
                // New balloon and reset timer
                spawnBalloon();
                countDown = rand.nextInt(50) + 50;
            } else {
                for (int i = 0; i < balloon.size(); i++) {
                    // Update balloon position and size
                    Balloon b = balloon.get(i);
                    b.bobTimer += .05f;
                    if (b.radius < maxRadius)
                        b.radius += b.inflateRate;

                    // If two balloons are touching, end the game
                    if (i < balloon.size() - 1) {
                        for (int j = i + 1; j < balloon.size(); j++) {
                            Balloon b2 = balloon.get(j);
                            if (distSquared(b.x, b.correctedY(), b2.x, b2.correctedY()) <= Math.pow((b.radius + b2.radius), 2)) {
                                gameRunning = false;
                                hideOtherBalloons(i, j);
                            }
                        }
                    }

                    // Increase the score by each balloon's size
                    score += b.radius;
                }

                //Count down to next balloon spawn
                countDown--;
            }

            repaint();
            try {
                Thread.sleep(16);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void paint(Graphics g) {
        if (offscreenGraphics == null) {
            offscreenImage = createImage(getWidth(), getHeight());
            offscreenGraphics = (Graphics2D)offscreenImage.getGraphics();
            offscreenGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            offscreenGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

        offscreenGraphics.setColor(Color.black);
        offscreenGraphics.fillRect(0, 0, getWidth(), getHeight());
        offscreenGraphics.drawImage(skyImage, 0, 0, null);

        for (Balloon b : new ArrayList<Balloon>(balloon)) {
            Rectangle rect = new Rectangle((int)(b.x - b.radius), (int)(b.correctedY() - b.radius), (int)(b.radius * 2), (int)(b.radius * 2));

            offscreenGraphics.setColor(stringColor);
            offscreenGraphics.drawLine((int)b.x, (int)b.correctedY(), b.tetherPointX, panelHeight);

            offscreenGraphics.setColor(b.color);
            offscreenGraphics.fillOval(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
            offscreenGraphics.drawImage(balloonBlankImage, rect.x, rect.y, rect.width, rect.height, null);
            int eyeIndex = (int)((b.radius / maxRadius) * balloonCoverImage.length);
            if (eyeIndex >= balloonCoverImage.length)
                eyeIndex = balloonCoverImage.length - 1;
            offscreenGraphics.drawImage(balloonCoverImage[eyeIndex], rect.x, rect.y, rect.width, rect.height, null);

            // Eye movement
            if (eyeIndex < 2) {
                Point eyePos;
                float xDif = mousePos.x - b.x;
                float yDif = mousePos.y - b.correctedY();
                float hypSquared = (xDif * xDif) + (yDif * yDif);
                float eyeMaxAdjusted = eyeMax * b.radius;
                if (hypSquared <= eyeMaxAdjusted * eyeMaxAdjusted) {
                    eyePos = new Point((int)(mousePos.x - b.x), (int)(mousePos.y - b.correctedY()));
                } else {
                    double div = eyeMaxAdjusted / Math.sqrt(hypSquared);
                    eyePos = new Point((int) (xDif * div), (int) (yDif * div));
                }

                offscreenGraphics.drawImage(balloonEyeImage, rect.x + eyePos.x, rect.y + eyePos.y, rect.width, rect.height, null);
            }
        }

        offscreenGraphics.setColor(Color.black);
        offscreenGraphics.drawString("Score: " + score, 10, getHeight()-50);

        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(offscreenImage, 0, 0, null);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        mousePos = e.getPoint();
    }

    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
    }
}