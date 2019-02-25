import java.awt.*;

public class Balloon {
    float x, y, radius, inflateRate;
    float bobDist = 2;
    float bobTimer;
    Color color;

    public Balloon(int x, int y, float inflateRate, Color color) {
        this.x = x;
        this.y = y;
        this.inflateRate = inflateRate;
        this.color = color;
        this.radius = 0.1f;
    }

    // Returns the y value including a sine wave to simulate bobbing up and down
    public float correctedY() {
        return y + (float)Math.sin(bobTimer) * bobDist;
    }
}