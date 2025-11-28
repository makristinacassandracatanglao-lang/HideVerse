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

        JButton easyButton = new JButton(new ImageIcon("assets/easy.png"));
        JButton medButton = new JButton(new ImageIcon("assets/medium.png"));
        JButton hardButton = new JButton(new ImageIcon("assets/hard.png"));

        easyButton.setActionCommand("EASY");
        easyButton.addActionListener(listener);
        easyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        easyButton.setBorderPainted(false);
        easyButton.setContentAreaFilled(false);
        easyButton.setFocusPainted(false);

        medButton.setActionCommand("MEDIUM");
        medButton.addActionListener(listener);
        medButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        medButton.setBorderPainted(false);
        medButton.setContentAreaFilled(false);
        medButton.setFocusPainted(false);

        hardButton.setActionCommand("HARD");
        hardButton.addActionListener(listener);
        hardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hardButton.setBorderPainted(false);
        hardButton.setContentAreaFilled(false);
        hardButton.setFocusPainted(false);

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
}
