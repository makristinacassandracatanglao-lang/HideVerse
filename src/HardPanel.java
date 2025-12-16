import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class HardPanel extends BaseGamePanel {
    private static final int GRID_W = 14; // columns
    private static final int GRID_H = 10; // rows

    // corner images
    private BufferedImage topLeftCornerImg, topRightCornerImg;
    private BufferedImage bottomLeftCornerImg, bottomRightCornerImg;

    // side images
    private BufferedImage topSideImg, bottomSideImg;
    private BufferedImage leftSideImg, rightSideImg;

    // barrier images
    private BufferedImage jBarrierImg;
    private BufferedImage lBarrierLeftBottomImg;
    private BufferedImage lBarrierLeftBottomMImg;
    private BufferedImage wholeBarrierImg;
    private BufferedImage wholeBarrierBigImg;
    private BufferedImage wholeBarrierSmallImg;
    private BufferedImage cornerBarrierImg;
    private BufferedImage wholeBarrierSmall2Img;

    // barrier container
    private java.util.List<Barrier> barriers = new ArrayList<>();
    // items list is inherited from BaseGamePanel

    // Player and walls are in BaseGamePanel

    private static final double TP_SCALE = 0.75;
    private static final double SB_SCALE = 0.50;
    private static final double SP_SCALE = 0.75;

    private ImageIcon telepadImg;
    private ImageIcon smokeBombImg;
    private ImageIcon speedPadImg;

    // Constants inherited from BaseGamePanel

    public HardPanel() {
        super(); // Calls BaseGamePanel constructor (setupInput, focus)
        setPreferredSize(new Dimension(GRID_W * TILE, GRID_H * TILE));
        Color DARK_BLUE = new Color(6, 23, 44);
        setBackground(DARK_BLUE);

        // Set Game Duration: 5 Minutes
        this.gameDuration = 300000;

        try {
            // load corners
            topLeftCornerImg = ImageIO.read(new File("assets/corner_wall.png"));
            topRightCornerImg = ImageIO.read(new File("assets/corner_wall_2.png"));
            bottomLeftCornerImg = ImageIO.read(new File("assets/corner_wall_3.png"));
            bottomRightCornerImg = ImageIO.read(new File("assets/corner_wall_4.png"));

            // load sides
            topSideImg = ImageIO.read(new File("assets/side_wall.png"));
            bottomSideImg = ImageIO.read(new File("assets/side_wall_d.png"));
            leftSideImg = ImageIO.read(new File("assets/side_wall_l.png"));
            rightSideImg = ImageIO.read(new File("assets/side_wall_r.png"));

            // load barriers
            jBarrierImg = ImageIO.read(new File("assets/j_barrier.png"));
            lBarrierLeftBottomImg = ImageIO.read(new File("assets/l_barrier_leftBottom.png"));
            lBarrierLeftBottomMImg = ImageIO.read(new File("assets/l_barrier_leftBottomM.png"));
            wholeBarrierImg = ImageIO.read(new File("assets/whole_barrier.png"));
            wholeBarrierBigImg = ImageIO.read(new File("assets/whole_barrier_big.png"));
            wholeBarrierSmallImg = ImageIO.read(new File("assets/whole_barrier_small.png"));
            cornerBarrierImg = ImageIO.read(new File("assets/corner_big.png"));
            wholeBarrierSmall2Img = ImageIO.read(new File("assets/whole_barrier_small2.png"));
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Could not load static images (e.g., walls).");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading wall/barrier images: " + e.getMessage(),
                    "Asset Loading Error", JOptionPane.ERROR_MESSAGE);
        }

        // Visual Barriers (Images)
        barriers.add(new Barrier(180, -7, wholeBarrierSmallImg, "whole_small"));
        barriers.add(new Barrier(330, 23, jBarrierImg, "j_barrier"));
        barriers.add(new Barrier(24, 173, lBarrierLeftBottomImg, "l_leftBottom"));
        barriers.add(new Barrier(190, 326, lBarrierLeftBottomMImg, "l_leftBottomM"));
        barriers.add(new Barrier(10, 23, wholeBarrierImg, "whole_medium"));
        barriers.add(new Barrier(110, 90, wholeBarrierBigImg, "whole_big"));
        barriers.add(new Barrier(290, 227, cornerBarrierImg, "corner_barrier"));
        barriers.add(new Barrier(507, 227, wholeBarrierSmall2Img, "whole_barrier_small_2"));

        // Manual Collision Walls are setup in setupWalls() called by super()

        telepadImg = new ImageIcon("assets/teleport_pad.gif");
        smokeBombImg = new ImageIcon("assets/smoke_bomb.gif");
        speedPadImg = new ImageIcon("assets/speed_pad.gif");

        // Initialize Player
        // Format: new Player(startX, startY, size, speed)
        player = new Player(250, 120, 30, 5);
    }

    @Override
    protected void setupItems() {
        // Speed Pads
        items.add(new GameItem(250, 250, SPEEDPAD_UP));
        items.add(new GameItem(160, 150, SPEEDPAD_DOWN));
        items.add(new GameItem(515, 280, SPEEDPAD_UP));
        items.add(new GameItem(370, 195, SPEEDPAD_RIGHT));

        // Telepads
        items.add(new GameItem(55, 50, TELEPAD));
        items.add(new GameItem(500, 50, TELEPAD));
        items.add(new GameItem(335, 390, TELEPAD));
        items.add(new GameItem(565, 390, TELEPAD));

        // Smoke Bombs
        items.add(new GameItem(65, 225, SMOKEBOMB));
        items.add(new GameItem(580, 50, SMOKEBOMB));
        items.add(new GameItem(440, 280, SMOKEBOMB));
    }

    @Override
    protected void setupWalls() {
        // 1. Frame Boundaries (Top, Bottom, Left, Right)
        walls.add(new Rectangle(0, 0, 630, 21));
        walls.add(new Rectangle(0, 427, 630, 23));
        walls.add(new Rectangle(0, 0, 23, 430));
        walls.add(new Rectangle(608, 0, 23, 430));

        // 2. Barrier Walls (Manual Coordinates)
        walls.add(new Rectangle(259, 11, 17, 80)); // whole_barrier_small
        walls.add(new Rectangle(330, 74, 21, 87)); // j_barrier
        walls.add(new Rectangle(351, 74, 200, 21));
        walls.add(new Rectangle(530, 23, 21, 140));
        walls.add(new Rectangle(22, 174, 100, 21)); // l_leftBottom
        walls.add(new Rectangle(102, 174, 19, 197));
        walls.add(new Rectangle(190, 329, 197, 19)); // l_leftBottomM
        walls.add(new Rectangle(368, 329, 19, 100));
        walls.add(new Rectangle(87, 22, 22, 100)); // whole_barrier_medium
        walls.add(new Rectangle(185, 90, 22, 170)); // whole_barrier_big
        walls.add(new Rectangle(290, 227, 200, 22)); // corner_barrier_big
        walls.add(new Rectangle(469, 227, 20, 200)); // corner_barrier_small
        walls.add(new Rectangle(539, 227, 64, 21)); // whole_barrier_small_2
    }

    @Override
    protected void drawMap(Graphics g) {
        drawFrame(g);

        // draw barriers
        for (Barrier b : barriers) {
            b.draw(g);
        }
    }

    @Override
    protected void drawItems(Graphics g) {
        for (GameItem item : items) {
            switch (item.type) {
                case TELEPAD:
                    if (telepadImg != null) {
                        int scaledWidth = (int) (telepadImg.getIconWidth() * TP_SCALE);
                        int scaledHeight = (int) (telepadImg.getIconHeight() * TP_SCALE);
                        int objX = item.x - scaledWidth / 2;
                        int objY = item.y - scaledHeight / 2;
                        g.drawImage(telepadImg.getImage(), objX, objY, scaledWidth, scaledHeight, this);
                    }
                    break;
                case SMOKEBOMB:
                    if (smokeBombImg != null) {
                        int scaledWidth = (int) (smokeBombImg.getIconWidth() * SB_SCALE);
                        int scaledHeight = (int) (smokeBombImg.getIconHeight() * SB_SCALE);
                        int objX = item.x - scaledWidth / 2;
                        int objY = item.y - scaledHeight / 2;
                        g.drawImage(smokeBombImg.getImage(), objX, objY, scaledWidth, scaledHeight, this);
                    }
                    break;
                case SPEEDPAD_UP:
                    drawRotatedSpeedPad(g, item.x, item.y, Math.toRadians(90));
                    break;
                case SPEEDPAD_DOWN:
                    drawRotatedSpeedPad(g, item.x, item.y, Math.toRadians(-90));
                    break;
                case SPEEDPAD_RIGHT:
                    drawRotatedSpeedPad(g, item.x, item.y, Math.toRadians(180));
                    break;
            }
        }
    }

    private void drawRotatedSpeedPad(Graphics g, int pixelX, int pixelY, double angleRad) {
        if (speedPadImg == null || speedPadImg.getIconWidth() <= 0) {
            // Fallback: Draw a colored arrow or rectangle
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(pixelX, pixelY);
            g2.rotate(angleRad);
            g2.setColor(Color.CYAN);
            g2.fillRect(-15, -15, 30, 30);
            g2.setColor(Color.BLACK);
            g2.drawRect(-15, -15, 30, 30);
            g2.drawString(">>>", -10, 5);
            g2.dispose();
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int origW = speedPadImg.getIconWidth();
        int origH = speedPadImg.getIconHeight();
        int padW = (int) (origW * SP_SCALE);
        int padH = (int) (origH * SP_SCALE);

        g2.translate(pixelX, pixelY);
        g2.rotate(angleRad);
        g2.drawImage(speedPadImg.getImage(), -padW / 2, -padH / 2, padW, padH, this);

        g2.dispose();
    }

    private void drawFrame(Graphics g) {
        // top row
        for (int x = 0; x < GRID_W; x++) {
            int y = 0;
            if (x == 0 && topLeftCornerImg != null) {
                g.drawImage(topLeftCornerImg, x * TILE, y * TILE, TILE, TILE, null);
            } else if (x == GRID_W - 1 && topRightCornerImg != null) {
                g.drawImage(topRightCornerImg, x * TILE, y * TILE, TILE, TILE, null);
            } else if (topSideImg != null) {
                g.drawImage(topSideImg, x * TILE, y * TILE, TILE, TILE, null);
            }
        }

        // bottom row
        for (int x = 0; x < GRID_W; x++) {
            int y = GRID_H - 1;
            if (x == 0 && bottomLeftCornerImg != null) {
                g.drawImage(bottomLeftCornerImg, x * TILE, y * TILE, TILE, TILE, null);
            } else if (x == GRID_W - 1 && bottomRightCornerImg != null) {
                g.drawImage(bottomRightCornerImg, x * TILE, y * TILE, TILE, TILE, null);
            } else if (bottomSideImg != null) {
                g.drawImage(bottomSideImg, x * TILE, y * TILE, TILE, TILE, null);
            }
        }

        // left side (skip corners)
        for (int y = 1; y < GRID_H - 1; y++) {
            if (leftSideImg != null) {
                g.drawImage(leftSideImg, 0, y * TILE, TILE, TILE, null);
            }
        }

        // right side (skip corners)
        for (int y = 1; y < GRID_H - 1; y++) {
            if (rightSideImg != null) {
                g.drawImage(rightSideImg, (GRID_W - 1) * TILE, y * TILE, TILE, TILE, null);
            }
        }
    }

    @Override
    protected Point getSeekerSpawnPoint() {
        // Spawn seeker above the center barrier to avoid getting stuck
        int centerX = getWidth() / 2;
        if (centerX == 0)
            centerX = 315;
        return new Point(centerX - 15, 150);
    }

    private static class Barrier {
        int x, y;
        BufferedImage img;
        String name; // for debugging / easier adjustments

        Barrier(int x, int y, BufferedImage img, String name) {
            this.x = x;
            this.y = y;
            this.img = img;
            this.name = name;
        }

        void draw(Graphics g) {
            if (img != null) {
                g.drawImage(img, x, y, null); // draw at natural size
            } else {
                // placeholder visual if image missing
                g.setColor(Color.MAGENTA);
                g.fillRect(x, y, 40, 20);
                g.setColor(Color.BLACK);
                g.drawString(name, x + 2, y + 12);
            }
        }
    }
}
