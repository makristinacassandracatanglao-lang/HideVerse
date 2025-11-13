import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectionPanel extends JPanel{
    public SelectionPanel(ActionListener listener){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        ImageIcon logo = new ImageIcon("assets/title.gif");
        JLabel logoJLB = new JLabel(logo, SwingConstants.CENTER);
        add(logoJLB, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        centerPanel.setOpaque(false);

        ImageIcon levels = new ImageIcon("assets/levels.png");
        JLabel levJLB = new JLabel(levels, SwingConstants.CENTER);
        centerPanel.add(levJLB);
        
        JButton easyButton = new JButton(new ImageIcon("assets/easy.png"));
        JButton medButton = new JButton(new ImageIcon("assets/medium.png"));
        JButton hardButton = new JButton(new ImageIcon("assets/hard.png"));

        easyButton.setBorderPainted(false);
        easyButton.setContentAreaFilled(false);
        easyButton.setFocusPainted(false);

        medButton.setBorderPainted(false);
        medButton.setContentAreaFilled(false);
        medButton.setFocusPainted(false);

        hardButton.setBorderPainted(false);
        hardButton.setContentAreaFilled(false);
        hardButton.setFocusPainted(false);

        easyButton.setActionCommand("EASY");
        medButton.setActionCommand("MEDIUM");
        hardButton.setActionCommand("HARD");

        easyButton.addActionListener(listener);
        medButton.addActionListener(listener);
        hardButton.addActionListener(listener);

        centerPanel.add(easyButton);
        centerPanel.add(medButton);
        centerPanel.add(hardButton);

        add(centerPanel, BorderLayout.CENTER);
    }
}
