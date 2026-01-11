/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package yk38.color_path_tetrablocks;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author yigit
 */
public class Color_path_tetrablocks {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Color-Path Tetrablocks");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        CardLayout cardLayout = new CardLayout();

        JPanel gamePanel = new JPanel(cardLayout);

        JPanel mainMenu = new JPanel();
        mainMenu.setLayout(new GridBagLayout());
        mainMenu.setBackground(Color.BLACK);
        mainMenu.setPreferredSize(new Dimension(500, 600));

        JLabel title = new JLabel("Color-Path Tetrablocks");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        mainMenu.add(title);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JButton lvl1Btn = new JButton("Level 1: Easy");
        lvl1Btn.setFont(new Font("Arial", Font.PLAIN, 18));
        lvl1Btn.setFocusable(false);
        mainMenu.add(lvl1Btn, gbc);

        lvl1Btn.addActionListener(e -> {
            TetrablocksGame game = new TetrablocksGame(1, 700, cardLayout);
            gamePanel.add(game, "game");
            cardLayout.show(gamePanel, "game");
            game.requestFocusInWindow();
        });

        JButton lvl2Btn = new JButton("Level 2: Medium");
        lvl2Btn.setFont(new Font("Arial", Font.PLAIN, 18));
        lvl2Btn.setFocusable(false);
        mainMenu.add(lvl2Btn, gbc);

        lvl2Btn.addActionListener(e -> {
            TetrablocksGame game = new TetrablocksGame(2, 400, cardLayout);
            gamePanel.add(game, "game");
            cardLayout.show(gamePanel, "game");
            game.requestFocusInWindow();
        });

        JButton lvl3Btn = new JButton("Level 3: Hard");
        lvl3Btn.setFont(new Font("Arial", Font.PLAIN, 18));
        lvl3Btn.setFocusable(false);
        mainMenu.add(lvl3Btn, gbc);

        lvl3Btn.addActionListener(e -> {
            TetrablocksGame game = new TetrablocksGame(3, 200, cardLayout);
            gamePanel.add(game, "game");
            cardLayout.show(gamePanel, "game");
            game.requestFocusInWindow();
        });

        gamePanel.add(mainMenu, "main");

        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
}
