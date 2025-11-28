import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MediumPanel extends BaseGamePanel {
    private static final int GRID_W = 14; //columns
    private static final int GRID_H = 10; //rows

    //corner images
    private BufferedImage topLeftCornerImg, topRightCornerImg;
    private BufferedImage bottomLeftCornerImg, bottomRightCornerImg;

    //side images
    private BufferedImage topSideImg, bottomSideImg;
    private BufferedImage leftSideImg, rightSideImg;

    //barrier images
    private BufferedImage cornerImgLT, cornerImgRT, cornerImgLB;
    private BufferedImage openLImg, openZImg, openNImg;

    private java.util.List<Barrier> barriers = new ArrayList<>();
    private java.util.List<GameItem> items = new ArrayList<>();

    private static final double TP_SCALE = 0.75;
    private static final double SB_SCALE = 0.50;
    private static final double SP_SCALE = 0.75;

    private ImageIcon telepadImg;
    private ImageIcon smokeBombImg;
    private ImageIcon speedPadImg;

    private static final int SPEEDPAD_UP = 21;
    private static final int SPEEDPAD_DOWN = 22;
    private static final int SPEEDPAD_SLANTU = 24;
    private static final int SPEEDPAD_SLANTD = 23;

    private static final int TELEPAD = 30;
    private static final int SMOKEBOMB = 31;

    public MediumPanel() {
        super();
        setPreferredSize(new Dimension(GRID_W * TILE, GRID_H * TILE));
        Color DARK_BLUE = new Color(6, 23, 44);
        setBackground(DARK_BLUE);

        try {
            //load corners
            topLeftCornerImg = ImageIO.read(new File("assets/corner_wall.png"));
            topRightCornerImg = ImageIO.read(new File("assets/corner_wall_2.png"));
            bottomLeftCornerImg = ImageIO.read(new File("assets/corner_wall_3.png"));
            bottomRightCornerImg = ImageIO.read(new File("assets/corner_wall_4.png"));

            //load sides
            topSideImg = ImageIO.read(new File("assets/side_wall.png"));
            bottomSideImg = ImageIO.read(new File("assets/side_wall_d.png"));
            leftSideImg = ImageIO.read(new File("assets/side_wall_l.png"));
            rightSideImg = ImageIO.read(new File("assets/side_wall_r.png"));

            //load barriers
            cornerImgLT = ImageIO.read(new File("assets/corner_leftTop.png"));
            cornerImgRT = ImageIO.read(new File("assets/corner_rightTop.png"));
            cornerImgLB = ImageIO.read(new File("assets/corner_leftBottom.png"));
            openLImg = ImageIO.read(new File("assets/open_l.png"));
            openZImg = ImageIO.read(new File("assets/open_z.png"));
            openNImg = ImageIO.read(new File("assets/open_n.png"));
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Could not load static images (e.g., walls).");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading wall/barrier images: " + e.getMessage(),
                    "Asset Loading Error", JOptionPane.ERROR_MESSAGE);
        }

        barriers.add(new Barrier(70, 23, cornerImgLT, "cornerLT"));
        barriers.add(new Barrier(455, 23, cornerImgRT, "cornerRT"));
        barriers.add(new Barrier(175, 327, cornerImgLB, "cornerLB"));
        barriers.add(new Barrier(340, 126, openLImg, "openL"));
        barriers.add(new Barrier(310, 290, openZImg, "openZ"));
        barriers.add(new Barrier(75, 155, openNImg, "openN"));

        telepadImg = new ImageIcon("assets/teleport_pad.gif");
        smokeBombImg = new ImageIcon("assets/smoke_bomb.gif");
        speedPadImg = new ImageIcon("assets/speed_pad.gif");

        //speed pads
        items.add(new GameItem(580, 250, SPEEDPAD_UP));
        items.add(new GameItem(50, 280, SPEEDPAD_DOWN));
        items.add(new GameItem(210, 150, SPEEDPAD_SLANTD));
        items.add(new GameItem(380, 220, SPEEDPAD_SLANTU));

        //teleport pads
        items.add(new GameItem(115, 70, TELEPAD));
        items.add(new GameItem(520, 310, TELEPAD));

        //smoke bombs
        items.add(new GameItem(220, 385, SMOKEBOMB));
        items.add(new GameItem(510, 60, SMOKEBOMB));

        //initialize player
        player = new Player(230, 30, 30, 5);
    }

    @Override
    protected void setupWalls() {
        //frame boundaries (top, bottom, left, right)
        walls.add(new Rectangle(0, 0, 630, 21));
        walls.add(new Rectangle(0, 427, 630, 23));
        walls.add(new Rectangle(0, 0, 23, 430));
        walls.add(new Rectangle(608, 0, 23, 430));

        //barriers
        walls.add(new Rectangle(70, 100, 100, 23));
        walls.add(new Rectangle(146, 23, 23, 100));

        walls.add(new Rectangle(455, 100, 100, 23));
        walls.add(new Rectangle(455, 23, 23, 100)); 

        walls.add(new Rectangle(252, 327, 23, 100));
        walls.add(new Rectangle(175, 327, 100, 23));

        walls.add(new Rectangle(457, 240, 100, 23));
        walls.add(new Polygon(
                new int[] { 470, 457, 360, 344 },
                new int[] { 240, 265, 130, 143 },
                4));

        walls.add(new Rectangle(310, 290, 100, 23));
        walls.add(new Rectangle(463, 355, 96, 22));
        walls.add(new Polygon(
                new int[] { 463, 467, 410, 400 },
                new int[] { 377, 355, 290, 313 },
                4));

        walls.add(new Rectangle(75, 230, 23, 100));
        walls.add(new Rectangle(253, 155, 21, 100));
        walls.add(new Rectangle(75, 230, 193, 23));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawFrame(g);

        //draw items
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

                case SPEEDPAD_SLANTU:
                    drawRotatedSpeedPad(g, item.x, item.y, Math.toRadians(45));
                    break;

                case SPEEDPAD_SLANTD:
                    drawRotatedSpeedPad(g, item.x, item.y, Math.toRadians(-45));
                    break;
            }
        }

        //draw player
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
        //top row
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

        //bottom row
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

        //left side (skip corners)
        for (int y = 1; y < GRID_H - 1; y++) {
            if (leftSideImg != null) {
                g.drawImage(leftSideImg, 0, y * TILE, TILE, TILE, null);
            }
        }

        //right side (skip corners)
        for (int y = 1; y < GRID_H - 1; y++) {
            if (rightSideImg != null) {
                g.drawImage(rightSideImg, (GRID_W - 1) * TILE, y * TILE, TILE, TILE, null);
            }
        }

        //draw barriers
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
                g.drawImage(img, x, y, null); //draw at natural size
            } else {
                //placeholder visual if image missing
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

