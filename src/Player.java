import java.awt.*;
import javax.swing.ImageIcon;

public class Player {
    public int x, y;
    public int width, height;
    public int speed;
    public int originalSpeed; // Base speed for resetting after boost
    public long speedBoostEndTime; // Time when speed boost expires
    public boolean canTeleport = true; // Prevents infinite teleport loops
    public int smokeBombCount = 0; // Inventory for smoke bombs
    public boolean isCaught = false;
    public Color color;

    public enum Role {
        SEEKER, HIDER
    }

    public Role role;
    public boolean isBot;

    public ImageIcon idleImg;
    public ImageIcon moveImg;
    public boolean isMoving = false;

    private static final String[] CHAR_ASSETS = {
            "assets/boy1.gif", "assets/boy2.gif", "assets/boy3.gif",
            "assets/girl1.gif", "assets/girl2.gif", "assets/girl3.gif"
    };

    public Player(int startX, int startY, int size, int speed) {
        this.x = startX;
        this.y = startY;
        this.width = size;
        this.height = size;
        this.speed = speed;
        this.originalSpeed = speed; // Initialize original speed
        this.color = Color.RED; // Default color
        this.isBot = false; // Default to human player

        // Assign random character image
        String assetPath = CHAR_ASSETS[(int) (Math.random() * CHAR_ASSETS.length)];
        setCharacter(assetPath);
    }

    public void setCharacter(String gifPath) {
        // Load GIF for movement
        this.moveImg = new ImageIcon(gifPath);

        // Load PNG for idle (replace .gif with .png)
        String pngPath = gifPath.replace(".gif", ".png");
        this.idleImg = new ImageIcon(pngPath);
    }

    public void setRole(Role role) {
        this.role = role;
        if (role == Role.SEEKER) {
            this.color = Color.RED;
        } else {
            this.color = Color.GREEN; // Hiders are Green
        }
    }

    public void draw(Graphics g) {
        ImageIcon currentImg = isMoving ? moveImg : idleImg;

        if (currentImg != null) {
            g.drawImage(currentImg.getImage(), x, y, width, height, null);
        } else {
            // Fallback to square if image fails
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }

        // Draw colored border to indicate role - REMOVED per user request
        // g.setColor(color);
        // g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Helper to get bounds for a hypothetical next position
    public Rectangle getBounds(int nextX, int nextY) {
        return new Rectangle(nextX, nextY, width, height);
    }
}
