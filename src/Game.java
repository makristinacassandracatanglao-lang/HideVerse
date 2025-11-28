import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JFrame implements ActionListener {
    private CardLayout layout;
    private JPanel stack;

    public Game() {
        layout = new CardLayout();
        stack = new JPanel(layout);

        EasyPanel easyPanel = new EasyPanel();
        MediumPanel medPanel = new MediumPanel();
        HardPanel hardPanel = new HardPanel();

        SelectionPanel selection = new SelectionPanel(this);

        stack.add(selection, "SELECT");
        stack.add(easyPanel, "EASY");
        stack.add(medPanel, "MEDIUM");
        stack.add(hardPanel, "HARD");

        add(stack);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        layout.show(stack, "SELECT");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        layout.show(stack, e.getActionCommand());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}
