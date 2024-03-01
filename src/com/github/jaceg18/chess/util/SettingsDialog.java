package com.github.jaceg18.chess.util;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private JComboBox<String> boardDesignComboBox;
    private JComboBox<Integer> aiDepthComboBox;

    public SettingsDialog(Frame owner) {
        super(owner, "Settings", true);
        setupUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Board Design:"));
        boardDesignComboBox = new JComboBox<>(new String[]{"Gradient", "Clear", "Marble", "Copper", "Rainbow", "Rigid", "Space"});
        add(boardDesignComboBox);



        add(new JLabel("AI Depth:"));
        aiDepthComboBox = new JComboBox<>(new Integer[]{2, 4, 6, 8, 10, 12});
        add(aiDepthComboBox);


        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> setVisible(false));
        add(okButton);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public String getSelectedBoardDesign() {
        return (String) boardDesignComboBox.getSelectedItem();
    }

    public int getSelectedAIDepth() {
        return (Integer) aiDepthComboBox.getSelectedItem();
    }
}

