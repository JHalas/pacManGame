package pl.edu.pjwstk.jhalas.game;

import javax.swing.*;
import java.awt.*;

public class NewGameWindow extends JFrame {
    private final JTextField widthField;
    private final JTextField heightField;

    public NewGameWindow() {
        super("Tape size board");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JLabel widthLabel = new JLabel("Width:");
        widthField = new JTextField();

        JLabel heightLabel = new JLabel("Height:");
        heightField = new JTextField();

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> startNewGame());

        panel.add(widthLabel);
        panel.add(widthField);
        panel.add(heightLabel);
        panel.add(heightField);
        panel.add(startButton);

        add(panel);

        setVisible(true);
    }

    private void startNewGame() {
        int width = Integer.parseInt(widthField.getText());
        int height = Integer.parseInt(heightField.getText());


        JFrame frame = new JFrame("Pac Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game mazeGame = new Game(width, height);
        frame.getContentPane().add(mazeGame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println("New Game started");
        System.out.println("New Game started on a " + width + "x" + height + " board");
        dispose();
    }

}
