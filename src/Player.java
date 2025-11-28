import java.awt.*;

public class Player {
    public int x, y;
    public int width, height;
    public int speed;
    public Color color;

    public Player(int startX, int startY, int size, int speed) {
        this.x = startX;
        this.y = startY;
        this.width = size;
        this.height = size;
        this.speed = speed;
        this.color = Color.RED; // Default color
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        // Optional: Draw a border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Helper to get bounds for a hypothetical next position
    public Rectangle getBounds(int nextX, int nextY) {
        return new Rectangle(nextX, nextY, width, height);
    }
}
