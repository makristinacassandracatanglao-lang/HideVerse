import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class EasyPanel extends BaseGamePanel {
    private static final int GRID_W = 14; // columns
    private static final int GRID_H = 10; // rows

    // scale for the GIFs
    private static final double TP_SCALE = 0.75;
    private static final double SB_SCALE = 0.50;
    private static final double SP_SCALE = 0.75;

    // Images for the outer frame
    private BufferedImage topLeftCornerImg, topRightCornerImg;
    private BufferedImage bottomLeftCornerImg, bottomRightCornerImg;

    private BufferedImage topSideImg, bottomSideImg;
    private BufferedImage leftSideImg, rightSideImg;

    // Image for your 4 objects
    private BufferedImage cornerImgLT; // left-top barrier
    private BufferedImage cornerImgRT; // right-top barrier
    private BufferedImage cornerImgLB; // left-bottom barrier
    private BufferedImage cornerImgRB; // right-bottom barrier

    // Image for slant barrier + 4 rotated variants
    private BufferedImage slantBarrierLR, slantBarrierRL;

    private java.util.List<Barrier> barriers = new ArrayList<>();
    private java.util.List<GameItem> items = new ArrayList<>();

    // Use ImageIcon for GIFs to support animation
    private ImageIcon telepadImg;
    private ImageIcon smokeBombImg;
    private ImageIcon speedPadImg;

    private static final int SPEEDPAD_UP = 21;
    private static final int SPEEDPAD_DOWN = 22;
    private static final int SPEEDPAD_LEFT = 23;
    private static final int SPEEDPAD_RIGHT = 24;

    private static final int TELEPAD = 30;
    private static final int SMOKEBOMB = 31;

    public EasyPanel() {
        super(); // Calls BaseGamePanel constructor
        setPreferredSize(new Dimension(GRID_W * TILE, GRID_H * TILE));
        Color DARK_BLUE = new Color(6, 23, 44);
        setBackground(DARK_BLUE);

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
            cornerImgLT = ImageIO.read(new File("assets/corner_leftTop.png"));
            cornerImgRT = ImageIO.read(new File("assets/corner_rightTop.png"));
            cornerImgLB = ImageIO.read(new File("assets/corner_leftBottom.png"));
            cornerImgRB = ImageIO.read(new File("assets/corner_rightBottom.png"));

            slantBarrierLR = ImageIO.read(new File("assets/slant_barrier_1.png"));
            slantBarrierRL = ImageIO.read(new File("assets/slant_barrier_2.png"));
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Could not load static images (e.g., walls).");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading wall/barrier images: " + e.getMessage(),
                    "Asset Loading Error", JOptionPane.ERROR_MESSAGE);
        }

        barriers.add(new Barrier(70, 70, cornerImgLT, "cornerLT"));
        barriers.add(new Barrier(455, 70, cornerImgRT, "cornerRT"));
        barriers.add(new Barrier(70, 280, cornerImgLB, "cornerLB"));
        barriers.add(new Barrier(455, 280, cornerImgRB, "cornerRB"));
        barriers.add(new Barrier(330, 110, slantBarrierRL, "slantRL"));
        barriers.add(new Barrier(330, 260, slantBarrierLR, "slantLR"));
        barriers.add(new Barrier(210, 260, slantBarrierRL, "slantUL"));
        barriers.add(new Barrier(210, 110, slantBarrierLR, "slantUR"));

        // Load GIFs using ImageIcon
        telepadImg = new ImageIcon("assets/teleport_pad.gif");
        smokeBombImg = new ImageIcon("assets/smoke_bomb.gif");
        speedPadImg = new ImageIcon("assets/speed_pad.gif");

        // Speed Pads
        items.add(new GameItem(580, 300, SPEEDPAD_UP));
        items.add(new GameItem(50, 150, SPEEDPAD_DOWN));
        items.add(new GameItem(420, 230, SPEEDPAD_RIGHT));
        items.add(new GameItem(210, 230, SPEEDPAD_LEFT));

        // Telepads
        items.add(new GameItem(110, 110, TELEPAD));
        items.add(new GameItem(520, 340, TELEPAD));

        // Smoke Bombs
        items.add(new GameItem(110, 340, SMOKEBOMB));
        items.add(new GameItem(520, 110, SMOKEBOMB));

        // Initialize Player
        player = new Player(300, 200, 30, 5);
    }

    @Override
    protected void setupWalls() {
        // 1. Frame Boundaries (Top, Bottom, Left, Right)
        walls.add(new Rectangle(0, 0, 630, 21));
        walls.add(new Rectangle(0, 427, 630, 23));
        walls.add(new Rectangle(0, 0, 23, 430));
        walls.add(new Rectangle(608, 0, 23, 430));

        // Corner Barriers (L-shapes)
        walls.add(new Rectangle(70, 147, 100, 23));
        walls.add(new Rectangle(145, 70, 23, 100));
        walls.add(new Rectangle(455, 70, 23, 100));
        walls.add(new Rectangle(455, 147, 100, 23));
        walls.add(new Rectangle(70, 280, 100, 23));
        walls.add(new Rectangle(145, 280, 23, 100));
        walls.add(new Rectangle(455, 280, 23, 100));
        walls.add(new Rectangle(455, 280, 100, 23));

        // Slanted Barriers (Polygons)
        walls.add(new Polygon(
                new int[] { 400, 415, 345, 330 },
                new int[] { 195, 180, 113, 128 },
                4));

        walls.add(new Polygon(
                new int[] { 400, 415, 350, 330 },
                new int[] { 261, 280, 345, 331 },
                4));

        walls.add(new Polygon(
                new int[] { 280, 295, 225, 212 },
                new int[] { 345, 330, 261, 280 },
                4));

        walls.add(new Polygon(
                new int[] { 280, 295, 225, 210 },
                new int[] { 111, 126, 195, 181 },
                4));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawFrame(g);

        // Draw items
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
                case SPEEDPAD_LEFT:
                    drawRotatedSpeedPad(g, item.x, item.y, Math.toRadians(0));
                    break;
            }
        }

        // Draw Player
        if (player != null) {
            player.draw(g);
        }
    }

    private void drawRotatedSpeedPad(Graphics g, int pixelX, int pixelY, double angleRad) {
        if (speedPadImg == null || speedPadImg.getIconWidth() <= 0)
            return;

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

        // draw barriers
        for (Barrier b : barriers) {
            b.draw(g);
        }
    }

    public void setBarrierPosition(String name, int newX, int newY) {
        for (Barrier b : barriers) {
            if (b.name != null && b.name.equals(name)) {
                b.x = newX;
                b.y = newY;
                repaint();
                return;
            }
        }
    }

    private static class Barrier {
        int x, y;
        BufferedImage img;
        String name;

        Barrier(int x, int y, BufferedImage img, String name) {
            this.x = x;
            this.y = y;
            this.img = img;
            this.name = name;
        }

        void draw(Graphics g) {
            if (img != null) {
                g.drawImage(img, x, y, null);
            } else {
                g.setColor(Color.MAGENTA);
                g.fillRect(x, y, 40, 20);
                g.setColor(Color.BLACK);
                g.drawString(name, x + 2, y + 12);
            }
        }
    }

    private static class GameItem {
        int x, y;
        int type;

        GameItem(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }
}
