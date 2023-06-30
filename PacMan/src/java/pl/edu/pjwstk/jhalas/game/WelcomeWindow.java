package pl.edu.pjwstk.jhalas.game;

import javax.swing.*;
import java.awt.*;


public class WelcomeWindow extends JFrame {

    public WelcomeWindow() {
        super("Pac-Man");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> startNewGame());

        JButton highScoreButton = new JButton("High Score");
        highScoreButton.addActionListener(e -> showHighScore());

        JButton endButton = new JButton("End");
        endButton.addActionListener(e -> closeGame());

        panel.add(newGameButton);
        panel.add(highScoreButton);
        panel.add(endButton);

        add(panel);

        setVisible(true);
    }

    private void startNewGame() {
        SwingUtilities.invokeLater(NewGameWindow::new);
    }

    private void showHighScore() {
        SwingUtilities.invokeLater(ScoreTableWindow::new);
        System.out.println("High Score displayed");
    }

    private void closeGame() {
        System.out.println("Game closed");
        System.exit(0);
    }

}
