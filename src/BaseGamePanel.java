import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public abstract class BaseGamePanel extends JPanel {
    protected static final int TILE = 45;
    protected Player player;
    protected java.util.List<Shape> walls = new ArrayList<>();

    public BaseGamePanel() {
        setFocusable(true);

        //ensures focus when panel is shown
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                requestFocusInWindow();
            }

            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {
            }

            public void ancestorMoved(javax.swing.event.AncestorEvent event) {
            }
        });

        setupInput();
        setupWalls();
    }

    protected abstract void setupWalls();

    protected void setupInput() {
        InputMap im = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("W"), "up");
        im.put(KeyStroke.getKeyStroke("S"), "down");
        im.put(KeyStroke.getKeyStroke("A"), "left");
        im.put(KeyStroke.getKeyStroke("D"), "right");

        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                movePlayer(0, -player.speed);
            }
        });
        am.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                movePlayer(0, player.speed);
            }
        });
        am.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                movePlayer(-player.speed, 0);
            }
        });
        am.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                movePlayer(player.speed, 0);
            }
        });
    }

    protected void movePlayer(int dx, int dy) {
        if (player == null)
            return;

        int nextX = player.x + dx;
        int nextY = player.y + dy;

        if (!checkCollision(nextX, nextY)) {
            player.x = nextX;
            player.y = nextY;
            repaint();
            System.out.println("Move successful. New Pos: (" + player.x + "," + player.y + ")");
        } else {
            System.out.println("Collision detected!");
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
}

