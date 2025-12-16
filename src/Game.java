import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JFrame implements ActionListener {
    private CardLayout layout;
    private JPanel stack;
    private String selectedDifficulty;
    private StartScreen startScreen;
    private CharacterSelectionPanel charSelect;
    private EasyPanel easyPanel;
    private MediumPanel medPanel;
    private HardPanel hardPanel;

    public Game() {
        layout = new CardLayout();
        stack = new JPanel(layout);

        easyPanel = new EasyPanel();
        medPanel = new MediumPanel();
        hardPanel = new HardPanel();

        startScreen = new StartScreen(this);
        SelectionPanel selection = new SelectionPanel(this);
        charSelect = new CharacterSelectionPanel(this);

        stack.add(startScreen, "START");
        stack.add(selection, "SELECT");
        stack.add(charSelect, "CHAR_SELECT");
        stack.add(easyPanel, "EASY");
        stack.add(medPanel, "MEDIUM");
        stack.add(hardPanel, "HARD");

        add(stack);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        layout.show(stack, "START");
    }

    public void showLevelSelection() {
        layout.show(stack, "SELECT");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Difficulty selected from SelectionPanel
        selectedDifficulty = e.getActionCommand();
        layout.show(stack, "CHAR_SELECT");
    }

    public void startGame(String characterPath) {
        BaseGamePanel panel = null;
        switch (selectedDifficulty) {
            case "EASY":
                panel = easyPanel;
                break;
            case "MEDIUM":
                panel = medPanel;
                break;
            case "HARD":
                panel = hardPanel;
                break;
        }

        if (panel != null) {
            panel.setPlayerCharacter(characterPath);
            layout.show(stack, selectedDifficulty);
            panel.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}
