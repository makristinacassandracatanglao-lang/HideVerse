import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CharacterSelectionPanel extends JPanel {
    private Game game;
    private String[] charAssets = {
            "assets/boy1.gif", "assets/boy2.gif", "assets/boy3.gif",
            "assets/girl1.gif", "assets/girl2.gif", "assets/girl3.gif"
    };

    public CharacterSelectionPanel(Game game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(new Color(9, 33, 60)); // TEAL

        // Title
        JLabel title = new JLabel("CHOOSE YOUR CHARACTER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // Character Grid
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        for (String asset : charAssets) {
            JButton btn = createCharacterButton(asset);
            grid.add(btn);
        }

        add(grid, BorderLayout.CENTER);
    }

    private JButton createCharacterButton(String assetPath) {
        // Use PNG for static display in selection menu
        String pngPath = assetPath.replace(".gif", ".png");
        ImageIcon icon = new ImageIcon(pngPath);

        // Scale up for visibility
        Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT);
        JButton btn = new JButton(new ImageIcon(img));

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        btn.addActionListener(e -> {
            game.startGame(assetPath);
        });

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
        });

        return btn;
    }
}
