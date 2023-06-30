package pl.edu.pjwstk.jhalas.game;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoreTableWindow extends JFrame {

    private final JList<String> scoreList;

    public ScoreTableWindow() {

        setTitle("High Scores");
        setSize(100, 200);
        setLocationRelativeTo(null);
        DefaultListModel<String> model = new DefaultListModel<>();
        scoreList = new JList<>(model);

        List<String> dataRows = readDataFromFile();
        for (String rowData : dataRows) {
            model.addElement(rowData);
        }

        JScrollPane scrollPane = new JScrollPane(scoreList);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private List<String> readDataFromFile() {
        List<String> dataRows = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("wynik.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                dataRows.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataRows;
    }
}
