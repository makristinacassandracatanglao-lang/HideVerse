import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectionPanel extends JPanel {
    public SelectionPanel(ActionListener listener) {
        setLayout(new BorderLayout());
        Color TEAL = new Color(9, 33, 60);
        setBackground(TEAL);

        ImageIcon logo = new ImageIcon("assets/title.gif");
        JLabel logoJLB = new JLabel(logo, SwingConstants.CENTER);
        logoJLB.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        ImageIcon levels = new ImageIcon("assets/levels.png");
        JLabel levJLB = new JLabel(levels, SwingConstants.CENTER);
        levJLB.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton easyButton = createLevelButton("assets/easy.png", "EASY", listener);
        JButton medButton = createLevelButton("assets/medium.png", "MEDIUM", listener);
        JButton hardButton = createLevelButton("assets/hard.png", "HARD", listener);

        centerPanel.add(Box.createVerticalStrut(60));
        centerPanel.add(logoJLB);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(levJLB);
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(easyButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(medButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(hardButton);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createLevelButton(String imagePath, String command, ActionListener listener) {
        JButton btn = new JButton(new ImageIcon(imagePath));
        btn.setActionCommand(command);
        btn.addActionListener(listener);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
