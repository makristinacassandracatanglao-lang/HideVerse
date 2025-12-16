import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public abstract class BaseGamePanel extends JPanel {
    protected static final int TILE = 45;
    protected Player player;
    protected java.util.List<Shape> walls = new ArrayList<>();

    protected abstract void setupWalls();

    protected abstract void setupItems();

    //abstract methods for rendering layers
    protected abstract void drawMap(Graphics g);

    protected abstract void drawItems(Graphics g);

    protected void setupInput() {
        InputMap im = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("pressed W"), "up_pressed");
        im.put(KeyStroke.getKeyStroke("released W"), "up_released");
        im.put(KeyStroke.getKeyStroke("pressed S"), "down_pressed");
        im.put(KeyStroke.getKeyStroke("released S"), "down_released");
        im.put(KeyStroke.getKeyStroke("pressed A"), "left_pressed");
        im.put(KeyStroke.getKeyStroke("released A"), "left_released");
        im.put(KeyStroke.getKeyStroke("pressed D"), "right_pressed");
        im.put(KeyStroke.getKeyStroke("released D"), "right_released");

        am.put("up_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                upPressed = true;
            }
        });
        am.put("up_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                upPressed = false;
            }
        });
        am.put("down_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                downPressed = true;
            }
        });
        am.put("down_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                downPressed = false;
            }
        });
        am.put("left_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                leftPressed = true;
            }
        });
        am.put("left_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                leftPressed = false;
            }
        });
        am.put("right_pressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                rightPressed = true;
            }
        });
        am.put("right_released", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                rightPressed = false;
            }
        });

        //smoke Bomb Activation
        im.put(KeyStroke.getKeyStroke("F"), "use_item");
        am.put("use_item", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (player != null && player.smokeBombCount > 0 && player.role == Player.Role.HIDER) {
                    player.smokeBombCount--;
                    //create smoke effect at player's position
                    activeSmokeEffects.add(
                            new SmokeEffect(player.x + player.width / 2, player.y + player.height / 2, SMOKE_DURATION));
                    System.out.println("Smoke Bomb activated! Remaining: " + player.smokeBombCount);
                }
            }
        });
    }

    protected void movePlayer(int dx, int dy) {
        if (player == null || player.isCaught)
            return;

        //prevent seeker from moving during delay
        if (isSeekerWaiting && player.role == Player.Role.SEEKER)
            return;

        //check for collisions immediately after move (responsive tagging)
        checkPlayerCollisions();

        int nextX = player.x + dx;
        int nextY = player.y + dy;

        if (!checkCollision(nextX, nextY)) {
            player.x = nextX;
            player.y = nextY;
            checkItemCollision(player);
            repaint();
        } else {
            //System.out.println("Collision detected!");
        }
    }

    //item constants
    protected static final int SPEEDPAD_UP = 21;
    protected static final int SPEEDPAD_DOWN = 22;
    protected static final int SPEEDPAD_LEFT = 23;
    protected static final int SPEEDPAD_RIGHT = 24;
    protected static final int SPEEDPAD_SLANTU = 25;
    protected static final int SPEEDPAD_SLANTD = 26;

    protected static final int TELEPAD = 30;
    protected static final int SMOKEBOMB = 31;

    protected enum GameState {
        SELECTION, PLAYING
    }

    protected GameState gameState = GameState.SELECTION;
    protected javax.swing.Timer gameTimer;
    protected int rouletteIndex = 0;
    protected long rouletteTimer = 0;
    protected long rouletteDuration = 0;

    //game timer and win conditions
    protected long gameDuration = 180000; //default 3 mins (overridden by subclasses)
    protected long remainingTime = 180000;
    protected boolean isGameOver = false;
    protected String winnerMessage = "";

    //3-round system
    protected int currentRound = 1;
    protected int maxRounds = 3;
    protected Map<String, Integer> scores = new HashMap<>();
    protected java.util.List<String> roundResults = new ArrayList<>();
    protected boolean isRoundOver = false;
    protected long roundOverTimer = 0;

    //seeker delay
    protected long seekerDelay = 10000; //10 seconds
    protected boolean isSeekerWaiting = false;

    //input state
    protected boolean upPressed = false;
    protected boolean downPressed = false;
    protected boolean leftPressed = false;
    protected boolean rightPressed = false;

    protected java.util.List<GameItem> items = new ArrayList<>();
    protected java.util.List<Bot> bots = new ArrayList<>();

    //smoke bomb effect tracking
    protected static class SmokeEffect {
        public int x, y;
        public long endTime;

        public SmokeEffect(int x, int y, long duration) {
            this.x = x;
            this.y = y;
            this.endTime = System.currentTimeMillis() + duration;
        }
    }

    protected java.util.List<SmokeEffect> activeSmokeEffects = new ArrayList<>();
    protected static final int SMOKE_RADIUS = 150; //radius of smoke effect
    protected static final long SMOKE_DURATION = 5000; //5 seconds
    protected ImageIcon smokeImg; //smoke effect GIF

    protected String playerCharacterPath;

    public void setPlayerCharacter(String assetPath) {
        this.playerCharacterPath = assetPath;
        if (player != null) {
            player.setCharacter(assetPath);
            assignUniqueCharacters();
        }
    }

    protected void assignUniqueCharacters() {
        if (playerCharacterPath == null)
            return;

        //assign unique characters to bots
        java.util.List<String> availableChars = new ArrayList<>(Arrays.asList(
                "assets/boy1.gif", "assets/boy2.gif", "assets/boy3.gif",
                "assets/girl1.gif", "assets/girl2.gif", "assets/girl3.gif"));

        //remove player's character from pool
        availableChars.remove(playerCharacterPath);

        //shuffle remaining characters
        Collections.shuffle(availableChars);

        //assign to bots
        for (int i = 0; i < bots.size() && i < availableChars.size(); i++) {
            bots.get(i).setCharacter(availableChars.get(i));
        }
    }

    public BaseGamePanel() {
        setFocusable(true);

        //ensure focus when panel is shown
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                requestFocusInWindow();
                setupGame(); //initialize roles and bots when panel is shown
            }

            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {
            }

            public void ancestorMoved(javax.swing.event.AncestorEvent event) {
            }
        });

        setupInput();
        setupWalls();

        //load smoke effect GIF
        smokeImg = new ImageIcon("assets/smoke.gif");

        //game loop timer (60 FPS)
        gameTimer = new javax.swing.Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameState == GameState.PLAYING) {
                    updateGameLogic();
                    repaint();
                } else if (gameState == GameState.SELECTION) {
                    updateRoulette();
                    repaint();
                }
            }
        });
        gameTimer.start();
    }

    protected void setupGame() {
        if (player == null)
            return;

        bots.clear();
        items.clear(); //clear items for selection phase
        setupItems(); //repopulate items

        //reset game state
        remainingTime = gameDuration;
        isGameOver = false;

        winnerMessage = "";
        isSeekerWaiting = false; //ensure delay is off during selection

        //reset player status
        player.isCaught = false;
        player.speed = player.originalSpeed;
        player.canTeleport = true;
        player.smokeBombCount = 0;

        //create 5 bots
        for (int i = 0; i < 5; i++) {
            bots.add(new Bot(0, 0, 30, 5));
        }

        //position everyone in a circle for roulette
        int centerX = 315; 
        int centerY = 215; 
        int radius = 100;
        int totalPlayers = 6; //1 Human + 5 Bots

        //position human at index 0
        player.x = centerX + (int) (radius * Math.cos(0)) - player.width / 2;
        player.y = centerY + (int) (radius * Math.sin(0)) - player.height / 2;

        //position bots
        for (int i = 0; i < 5; i++) {
            double angle = (2 * Math.PI / totalPlayers) * (i + 1);
            Bot bot = bots.get(i);
            bot.x = centerX + (int) (radius * Math.cos(angle)) - bot.width / 2;
            bot.y = centerY + (int) (radius * Math.sin(angle)) - bot.height / 2;
        }

        assignUniqueCharacters();
        startRoulette();
    }

    protected boolean isSpinning = false;
    protected long showResultTime = 0;

    protected void startRoulette() {
        gameState = GameState.SELECTION;
        rouletteIndex = 0;
        rouletteTimer = System.currentTimeMillis();
        rouletteDuration = 3000 + new Random().nextInt(2000); //3-5 seconds spin
        isSpinning = true;
    }

    protected void updateRoulette() {
        long currentTime = System.currentTimeMillis();

        if (isSpinning) {
            //spin effect: change highlighted player every 100ms
            if (currentTime - rouletteTimer > 100) {
                rouletteIndex = (rouletteIndex + 1) % 6; //0 is player, 1-5 are bots
                rouletteTimer = currentTime;
                rouletteDuration -= 100;
            }

            if (rouletteDuration <= 0) {
                isSpinning = false;
                showResultTime = System.currentTimeMillis() + 2000; //show result for 2 seconds
            }
        } else {
            //waiting for result show time
            if (currentTime > showResultTime) {
                finishSelection();
            }
        }
    }

    protected void finishSelection() {
        gameState = GameState.PLAYING;

        //reset seeker delay
        seekerDelay = 10000;
        isSeekerWaiting = true;

        //rouletteIndex determines who is the Seeker
        //0 = Player, 1-5 = Bots

        if (rouletteIndex == 0) {
            player.setRole(Player.Role.SEEKER);
            System.out.println("You are the SEEKER!");
            for (Bot bot : bots)
                bot.setRole(Player.Role.HIDER);
        } else {
            player.setRole(Player.Role.HIDER);
            System.out.println("You are a HIDER!");
            for (int i = 0; i < bots.size(); i++) {
                if (i == rouletteIndex - 1) {
                    bots.get(i).setRole(Player.Role.SEEKER);
                } else {
                    bots.get(i).setRole(Player.Role.HIDER);
                }
            }
        }

        //move everyone to spawn points
        Point seekerSpawn = getSeekerSpawnPoint();

        if (player.role == Player.Role.SEEKER) {
            player.x = seekerSpawn.x;
            player.y = seekerSpawn.y;
        } else {
            Point pSpawn = getSafeSpawnPoint();
            player.x = pSpawn.x;
            player.y = pSpawn.y;
        }

        for (Bot bot : bots) {
            if (bot.role == Player.Role.SEEKER) {
                bot.x = seekerSpawn.x;
                bot.y = seekerSpawn.y;
            } else {
                Point bSpawn = getSafeSpawnPoint();
                bot.x = bSpawn.x;
                bot.y = bSpawn.y;
            }
        }
    }

    protected Point getSeekerSpawnPoint() {
        //default: spawn in the center
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        if (centerX == 0)
            centerX = 315;
        if (centerY == 0)
            centerY = 215;
        return new Point(centerX - 15, centerY - 15);
    }

    protected static class GameItem {
        public int x, y;
        public int type;

        public GameItem(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

    protected Point getSafeSpawnPoint() {
        Random rand = new Random();
        int maxAttempts = 100;

        for (int i = 0; i < maxAttempts; i++) {
            //random position within bounds (avoiding edges)
            int w = getWidth();
            int h = getHeight();

            //fallback if panel not yet sized
            if (w < 100)
                w = 630;
            if (h < 100)
                h = 430;

            int x = 50 + rand.nextInt(w - 100);
            int y = 50 + rand.nextInt(h - 100);

            //create a temporary rectangle for collision check
            Rectangle tempBounds = new Rectangle(x, y, 30, 30); 

            boolean collision = false;
            for (Shape wall : walls) {
                if (wall.intersects(tempBounds)) {
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                return new Point(x, y);
            }
        }

        //fallback if no safe spot found (center)
        return new Point(315, 215);
    }

    protected void checkPlayerCollisions() {
        if (player == null)
            return;

        //prevent tagging during seeker delay
        if (isSeekerWaiting)
            return;

        //define seeker and hiders list
        Player seeker = null;
        java.util.List<Player> hiders = new ArrayList<>();

        //identify human role
        if (player.role == Player.Role.SEEKER) {
            seeker = player;
        } else if (!player.isCaught) {
            hiders.add(player);
        }

        //identify bot roles
        for (Bot bot : bots) {
            if (bot.role == Player.Role.SEEKER) {
                seeker = bot;
            } else if (!bot.isCaught) {
                hiders.add(bot);
            }
        }

        if (seeker == null)
            return;

        Rectangle seekerBounds = seeker.getBounds();

        //check collisions
        for (Player hider : hiders) {
            if (seekerBounds.intersects(hider.getBounds())) {
                hider.isCaught = true;
                System.out.println("Hider caught!");

                Point safeSpot = getSafeSpawnPoint();
                hider.x = safeSpot.x;
                hider.y = safeSpot.y;
            }
        }
    }

    protected boolean checkCollision(int nextX, int nextY) {
        if (player == null)
            return false;

        Rectangle nextBounds = player.getBounds(nextX, nextY);

        //check against manual walls
        for (Shape wall : walls) {
            if (wall.intersects(nextBounds)) {
                return true;
            }
        }

        return false;
    }

    protected void updateGameLogic() {
        if (player == null)
            return;

        //handle player movement
        if (!player.isCaught && (!isSeekerWaiting || player.role != Player.Role.SEEKER)) {
            int dx = 0;
            int dy = 0;

            if (upPressed)
                dy -= player.speed;
            if (downPressed)
                dy += player.speed;
            if (leftPressed)
                dx -= player.speed;
            if (rightPressed)
                dx += player.speed;

            //update moving state
            player.isMoving = (dx != 0 || dy != 0);

            if (dx != 0 || dy != 0) {
                if (dx != 0 && dy != 0) {
                    double length = Math.sqrt(dx * dx + dy * dy);
                    dx = (int) Math.round((dx / length) * player.speed);
                    dy = (int) Math.round((dy / length) * player.speed);
                }
                movePlayer(dx, dy);
            }
        }

        //check for eliminations
        checkPlayerCollisions();

        //seeker delay logic
        if (isSeekerWaiting) {
            seekerDelay -= 16;
            if (seekerDelay <= 0) {
                seekerDelay = 0;
                isSeekerWaiting = false;
            }
        }

        //check round transition
        if (isRoundOver) {
            if (System.currentTimeMillis() > roundOverTimer) {
                startNextRound();
            }
            return; //stop game logic while round is over
        }

        //check win conditions
        if (!isGameOver) {
            // Decrement Timer only after seeker is released
            if (!isSeekerWaiting) {
                remainingTime -= 16;
            }

            if (remainingTime <= 0) {
                remainingTime = 0;
                endRound("HIDERS WIN! Time's Up!");
            }

            //check if all hiders are caught
            boolean allHidersCaught = true;
            int hiderCount = 0;

            //check human hider
            if (player.role == Player.Role.HIDER) {
                hiderCount++;
                if (!player.isCaught)
                    allHidersCaught = false;
            }

            //check bot hiders
            for (Bot bot : bots) {
                if (bot.role == Player.Role.HIDER) {
                    hiderCount++;
                    if (!bot.isCaught)
                        allHidersCaught = false;
                }
            }

            if (hiderCount > 0 && allHidersCaught) {
                endRound("SEEKER WINS! All Hiders Caught!");
            }
        }

        if (System.currentTimeMillis() > player.speedBoostEndTime && player.speed != player.originalSpeed) {
            player.speed = player.originalSpeed;
        }

        for (Bot bot : bots) {
            if (System.currentTimeMillis() > bot.speedBoostEndTime && bot.speed != bot.originalSpeed) {
                bot.speed = bot.originalSpeed;
            }
        }

        if (!player.canTeleport) {
            boolean onAnyTelepad = false;
            Rectangle playerBounds = player.getBounds();
            for (GameItem item : items) {
                if (item.type == TELEPAD) {
                    Rectangle itemBounds = new Rectangle(item.x - TILE / 2, item.y - TILE / 2, TILE, TILE);
                    if (itemBounds.intersects(playerBounds)) {
                        onAnyTelepad = true;
                        break;
                    }
                }
            }
            if (!onAnyTelepad) {
                player.canTeleport = true;
            }
        }

        activeSmokeEffects.removeIf(smoke -> System.currentTimeMillis() > smoke.endTime);

        for (Bot bot : bots) {
            if (!bot.isCaught) {
                //prevent seeker bot from moving during delay
                if (isSeekerWaiting && bot.role == Player.Role.SEEKER) {
                    continue;
                }
                bot.moveLogic(this);
                checkItemCollision(bot); 
            }
        }
    }

    protected void checkItemCollision(Player p) {
        if (p == null)
            return;

        Rectangle playerBounds = p.getBounds();
        GameItem collidedItem = null;

        //find collision
        for (GameItem item : items) {
            Rectangle itemBounds = new Rectangle(item.x - TILE / 2, item.y - TILE / 2, TILE, TILE);
            if (itemBounds.intersects(playerBounds)) {
                collidedItem = item;
                break;
            }
        }

        if (collidedItem != null) {
            handleItemEffect(p, collidedItem);
        }
    }

    protected void handleItemEffect(Player p, GameItem item) {
        switch (item.type) {
            case SPEEDPAD_UP:
            case SPEEDPAD_DOWN:
            case SPEEDPAD_LEFT:
            case SPEEDPAD_RIGHT:
            case SPEEDPAD_SLANTU:
            case SPEEDPAD_SLANTD:
                //speed boost logic: HIDER ONLY
                if (p.role == Player.Role.HIDER) {
                    p.speed = p.originalSpeed * 2;
                    p.speedBoostEndTime = System.currentTimeMillis() + 2000; //2 seconds
                }
                break;

            case TELEPAD:
                //teleport logic: SEEKER ONLY
                if (p.role == Player.Role.SEEKER) {
                    if (p.canTeleport) {
                        for (GameItem other : items) {
                            if (other.type == TELEPAD && other != item) {
                                p.x = other.x;
                                p.y = other.y;
                                p.canTeleport = false; //prevent immediate re-teleport
                                repaint();
                                break;
                            }
                        }
                    }
                }
                break;

            case SMOKEBOMB:
                //inventory logic: HIDER ONLY
                if (p.role == Player.Role.HIDER) {
                    p.smokeBombCount++;
                    items.remove(item);
                    System.out.println("Smoke Bomb acquired! Count: " + p.smokeBombCount);
                }
                break;
        }
    }

    protected void endRound(String winner) {
        isRoundOver = true;
        roundOverTimer = System.currentTimeMillis() + 5000; //5 seconds delay
        winnerMessage = winner;
        roundResults.add("Round " + currentRound + ": " + winner);

        //update scores
        if (winner.contains("Seeker")) {
            //seeker wins: seeker gets 1 point
            Player seeker = null;
            if (player.role == Player.Role.SEEKER)
                seeker = player;
            else {
                for (Bot bot : bots)
                    if (bot.role == Player.Role.SEEKER)
                        seeker = bot;
            }

            if (seeker != null) {
                String name = (seeker == player) ? "YOU" : "BOT"; //simplified name logic
                scores.put(name, scores.getOrDefault(name, 0) + 1);
            }
        } else {
            //hiders win: surviving hiders get 1 point
            if (player.role == Player.Role.HIDER && !player.isCaught) {
                scores.put("YOU", scores.getOrDefault("YOU", 0) + 1);
            }
            for (int i = 0; i < bots.size(); i++) {
                Bot bot = bots.get(i);
                if (bot.role == Player.Role.HIDER && !bot.isCaught) {
                    String name = "BOT " + (i + 1);
                    scores.put(name, scores.getOrDefault(name, 0) + 1);
                }
            }
        }
    }

    protected void startNextRound() {
        if (currentRound >= maxRounds) {
            isGameOver = true;
            isRoundOver = false;
            //calculate final winner
            return;
        }

        currentRound++;
        isRoundOver = false;
        setupGame(); //reset for next round
    }

    protected void drawSmokeOnMap(Graphics g) {
        for (SmokeEffect smoke : activeSmokeEffects) {
            if (smokeImg != null) {
                //draw smoke image centered at smoke.x, smoke.y with size SMOKE_RADIUS * 2
                int size = SMOKE_RADIUS * 2;
                g.drawImage(smokeImg.getImage(), smoke.x - SMOKE_RADIUS, smoke.y - SMOKE_RADIUS, size, size, null);
            } else {
                g.setColor(new Color(128, 128, 128, 150));
                g.fillOval(smoke.x - SMOKE_RADIUS, smoke.y - SMOKE_RADIUS, SMOKE_RADIUS * 2, SMOKE_RADIUS * 2);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //draw map (walls, background)
        drawMap(g);

        //draw items (below players)
        drawItems(g);

        //draw smoke on map
        drawSmokeOnMap(g);

        if (gameState == GameState.SELECTION) {
            //draw selection visuals
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String msg = isSpinning ? "Selecting Seeker..." : "Seeker Selected!";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, getWidth() / 2 - fm.stringWidth(msg) / 2, 50);

            //highlight current selection
            Player highlighted = (rouletteIndex == 0) ? player : bots.get(rouletteIndex - 1);
            g.setColor(Color.YELLOW);
            g.fillOval(highlighted.x - 5, highlighted.y - 5, highlighted.width + 10, highlighted.height + 10);

            //if result is shown, draw name/role
            if (!isSpinning) {
                g.setColor(Color.GREEN);
                String name = (rouletteIndex == 0) ? "YOU" : "BOT " + rouletteIndex;
                g.drawString(name, getWidth() / 2 - g.getFontMetrics().stringWidth(name) / 2, getHeight() / 2);
            }
        }

        //draw bots
        for (Bot bot : bots) {
            if (!bot.isCaught) {
                bot.draw(g);
            }
        }

        //draw player
        if (player != null && !player.isCaught) {
            player.draw(g);
        }

        //draw HUD (timer)
        if (gameState == GameState.PLAYING) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            long seconds = (remainingTime / 1000) % 60;
            long minutes = (remainingTime / 1000) / 60;
            String timeStr = String.format("Time: %02d:%02d", minutes, seconds);
            FontMetrics fm = g.getFontMetrics();
            int strW = fm.stringWidth(timeStr);
            g.drawString(timeStr, getWidth() / 2 - strW / 2, 25);

            //draw round info
            String roundStr = "Round: " + currentRound + "/" + maxRounds;
            g.drawString(roundStr, 20, 25);
        }

        //draw seeker delay countdown
        if (isSeekerWaiting && gameState == GameState.PLAYING) {
            //draw at the top, below the timer
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 40, getWidth(), 50);

            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String delayMsg = "Seeker Releasing in " + (seekerDelay / 1000 + 1) + "...";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(delayMsg, getWidth() / 2 - fm.stringWidth(delayMsg) / 2, 75);
        }

        //draw CAUGHT overlay
        if (player != null && player.isCaught) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String msg = "CAUGHT";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, getWidth() / 2 - fm.stringWidth(msg) / 2, getHeight() / 2);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String subMsg = "You have been tagged!";
            g.drawString(subMsg, getWidth() / 2 - g.getFontMetrics().stringWidth(subMsg) / 2, getHeight() / 2 + 40);
        }

        //draw smoke effect overlay (blocks seeker's vision)
        if (player != null && player.role == Player.Role.SEEKER && !player.isCaught) {
            for (SmokeEffect smoke : activeSmokeEffects) {
                //calculate distance from player to smoke center
                int dx = (player.x + player.width / 2) - smoke.x;
                int dy = (player.y + player.height / 2) - smoke.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < SMOKE_RADIUS) {
                    //seeker is in smoke range - block vision
                    //opacity increases as seeker gets closer to smoke center
                    float opacity = (float) (0.9 * (1 - distance / SMOKE_RADIUS));
                    opacity = Math.max(0.4f, Math.min(0.95f, opacity));

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

                    //tile the smoke.gif across the screen
                    if (smokeImg != null && smokeImg.getIconWidth() > 0) {
                        int smokeW = smokeImg.getIconWidth();
                        int smokeH = smokeImg.getIconHeight();
                        for (int x = 0; x < getWidth(); x += smokeW) {
                            for (int y = 0; y < getHeight(); y += smokeH) {
                                g2.drawImage(smokeImg.getImage(), x, y, smokeW, smokeH, this);
                            }
                        }
                    } else {
                        //fallback if smoke.gif not loaded
                        g2.setColor(new Color(128, 128, 128));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                    }

                    g2.dispose();

                    //draw "SMOKE!" text
                    g.setColor(new Color(255, 255, 255, (int) (opacity * 255)));
                    g.setFont(new Font("Arial", Font.BOLD, 40));
                    String smokeMsg = "SMOKE!";
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString(smokeMsg, getWidth() / 2 - fm.stringWidth(smokeMsg) / 2, getHeight() / 2);
                    break; //only show effect for closest smoke
                }
            }
        }

        //draw Round Over screen
        if (isRoundOver) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(winnerMessage, getWidth() / 2 - fm.stringWidth(winnerMessage) / 2, getHeight() / 2 - 20);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String subMsg = "Next round starting soon...";
            g.drawString(subMsg, getWidth() / 2 - g.getFontMetrics().stringWidth(subMsg) / 2, getHeight() / 2 + 30);
        }

        //draw final tally (Game Over)
        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String title = "FINAL SCORES";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(title, getWidth() / 2 - fm.stringWidth(title) / 2, 50);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            int y = 100;

            //draw round history
            for (String result : roundResults) {
                g.drawString(result, getWidth() / 2 - g.getFontMetrics().stringWidth(result) / 2, y);
                y += 25;
            }

            y += 20;
            g.drawLine(100, y, getWidth() - 100, y);
            y += 30;

            //draw scores
            g.setFont(new Font("Arial", Font.BOLD, 20));
            //sort scores
            java.util.List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
            sortedScores.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            for (Map.Entry<String, Integer> entry : sortedScores) {
                String scoreLine = entry.getKey() + ": " + entry.getValue();
                g.drawString(scoreLine, getWidth() / 2 - g.getFontMetrics().stringWidth(scoreLine) / 2, y);
                y += 30;
            }

            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            String exitMsg = "Close window to exit";
            g.drawString(exitMsg, getWidth() / 2 - g.getFontMetrics().stringWidth(exitMsg) / 2, getHeight() - 30);
        }
    }
}

