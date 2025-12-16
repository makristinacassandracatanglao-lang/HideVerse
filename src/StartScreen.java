import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartScreen extends JPanel {
    private Image bgImage;

    public StartScreen(Game game) {
        setLayout(new GridBagLayout()); //use GridBagLayout for centering

        //load background image
        ImageIcon icon = new ImageIcon("assets/new game.png");
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            bgImage = icon.getImage();
        } else {
            System.err.println("Error loading background image: assets/new game.png");
            setBackground(new Color(9, 33, 60)); //fallback color
        }

        //create buttons
        JButton startBtn = createButton("START", "assets/start.png");
        JButton exitBtn = createButton("EXIT", "assets/exit.png");

        //add actions
        startBtn.addActionListener(e -> game.showLevelSelection());
        exitBtn.addActionListener(e -> System.exit(0));

        //layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); //vertical spacing

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startBtn);
        buttonPanel.add(exitBtn);

        //adjust padding to move it up from the very bottom
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));

        add(buttonPanel, gbc);
    }

    private JButton createButton(String text, String imagePath) {
        JButton btn;
        ImageIcon icon = new ImageIcon(imagePath);

        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            //image button
            btn = new JButton(icon);
            btn.setBorder(BorderFactory.createEmptyBorder());
        } else {
            //text button fallback
            btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 24));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(0, 0, 0, 150));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(10, 40, 10, 40)));
        }

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    //no border for image buttons, just cursor change
                } else {
                    btn.setBackground(new Color(255, 255, 255, 50));
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.YELLOW, 2),
                            BorderFactory.createEmptyBorder(10, 40, 10, 40)));
                    btn.setOpaque(true);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    btn.setBorder(BorderFactory.createEmptyBorder());
                } else {
                    btn.setBackground(new Color(0, 0, 0, 150));
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.WHITE, 2),
                            BorderFactory.createEmptyBorder(10, 40, 10, 40)));
                    btn.setOpaque(true);
                }
            }
        });

        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}


