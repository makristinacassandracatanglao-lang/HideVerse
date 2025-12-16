import java.awt.*;
import java.util.Random;

public class Bot extends Player {
    private Random random;
    private int moveTimer = 0;
    private int currentDx = 0;
    private int currentDy = 0;
    private int hideTimer = 0; // Timer for when hider bots stop to hide
    private boolean isHiding = false;

    public Bot(int startX, int startY, int size, int speed) {
        super(startX, startY, size, speed);
        this.isBot = true;
        this.random = new Random();
        this.color = Color.BLUE; // Default bot color, will change based on role
    }

    @Override
    public void setRole(Role role) {
        super.setRole(role);
        // Bots have slightly different colors to distinguish from player
        if (role == Role.SEEKER) {
            this.color = new Color(139, 0, 0); // Dark Red
        } else {
            this.color = new Color(0, 100, 0); // Dark Green
        }
    }

    public void moveLogic(BaseGamePanel panel) {
        // Hider bots occasionally stop to hide
        if (role == Role.HIDER) {
            if (isHiding) {
                hideTimer--;
                if (hideTimer <= 0) {
                    isHiding = false;
                    moveTimer = 0; // Start moving again
                }
                this.isMoving = false; // Ensure animation stays stopped
                return; // Don't move while hiding
            } else {
                // Randomly decide to hide (5% chance per frame when not hiding)
                if (random.nextInt(100) < 5 && moveTimer == 0) {
                    isHiding = true;
                    hideTimer = 180 + random.nextInt(180); // Hide for 3-6 seconds
                    currentDx = 0;
                    currentDy = 0;
                    this.isMoving = false; // Explicitly stop animation
                    return;
                }
            }
        }

        // Simple AI: Move randomly for now
        // In a real implementation, Seeker would chase and Hiders would flee

        if (moveTimer > 0) {
            moveTimer--;
        } else {
            // Pick a new random direction
            int dir = random.nextInt(4);
            switch (dir) {
                case 0:
                    currentDx = 0;
                    currentDy = -speed;
                    break; // Up
                case 1:
                    currentDx = 0;
                    currentDy = speed;
                    break; // Down
                case 2:
                    currentDx = -speed;
                    currentDy = 0;
                    break; // Left
                case 3:
                    currentDx = speed;
                    currentDy = 0;
                    break; // Right
            }
            moveTimer = 20 + random.nextInt(40); // Move in this direction for 20-60 frames
        }

        // Attempt move
        int nextX = x + currentDx;
        int nextY = y + currentDy;

        if (!panel.checkCollision(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else {
            // Hit a wall, pick new direction immediately next frame
            moveTimer = 0;
        }

        // Update moving state
        this.isMoving = (currentDx != 0 || currentDy != 0);

        // Bots also interact with items (logic handled in BaseGamePanel)
    }
}
