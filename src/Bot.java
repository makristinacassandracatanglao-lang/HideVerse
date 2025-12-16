import java.awt.*;
import java.util.Random;

public class Bot extends Player {
    private Random random;
    private int moveTimer = 0;
    private int currentDx = 0;
    private int currentDy = 0;
    private int hideTimer = 0; //timer for when hider bots stop to hide
    private boolean isHiding = false;

    public Bot(int startX, int startY, int size, int speed) {
        super(startX, startY, size, speed);
        this.isBot = true;
        this.random = new Random();
        this.color = Color.BLUE;
    }

    @Override
    public void setRole(Role role) {
        super.setRole(role);
        if (role == Role.SEEKER) {
            this.color = new Color(139, 0, 0); //dark red
        } else {
            this.color = new Color(0, 100, 0); //dark green
        }
    }

    public void moveLogic(BaseGamePanel panel) {
        //hider bots occasionally stop to hide
        if (role == Role.HIDER) {
            if (isHiding) {
                hideTimer--;
                if (hideTimer <= 0) {
                    isHiding = false;
                    moveTimer = 0; //start moving again
                }
                this.isMoving = false; //ensure animation stays stopped
                return; //don't move while hiding
            } else {
                //randomly decide to hide (5% chance per frame when not hiding)
                if (random.nextInt(100) < 5 && moveTimer == 0) {
                    isHiding = true;
                    hideTimer = 180 + random.nextInt(180); //hide for 3-6 seconds
                    currentDx = 0;
                    currentDy = 0;
                    this.isMoving = false; //explicitly stop animation
                    return;
                }
            }
        }

        if (moveTimer > 0) {
            moveTimer--;
        } else {
            //pick a new random direction
            int dir = random.nextInt(4);
            switch (dir) {
                case 0:
                    currentDx = 0;
                    currentDy = -speed;
                    break; //up
                case 1:
                    currentDx = 0;
                    currentDy = speed;
                    break; //down
                case 2:
                    currentDx = -speed;
                    currentDy = 0;
                    break; //left
                case 3:
                    currentDx = speed;
                    currentDy = 0;
                    break; //right
            }
            moveTimer = 20 + random.nextInt(40);
        }

        //attempt move
        int nextX = x + currentDx;
        int nextY = y + currentDy;

        if (!panel.checkCollision(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else {
            //hit a wall, pick new direction immediately next frame
            moveTimer = 0;
        }

        //update moving state
        this.isMoving = (currentDx != 0 || currentDy != 0);

        // Bots also interact with items (logic handled in BaseGamePanel)
    }
}

