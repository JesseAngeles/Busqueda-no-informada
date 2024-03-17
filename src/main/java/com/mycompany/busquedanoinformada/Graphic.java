package com.mycompany.busquedanoinformada;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Graphic extends JFrame {

    private int randomMoves = 75;
    private boolean solved = true;
    private boolean allowMovement = false;

    private byte[][] board;

    private JButton[][] buttons;
    private ImageIcon background = new ImageIcon("Assets\\background.jpg");
    private gameController controller;

    public Graphic(int size) {
        super("Busqueda no informada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        /*
            MENU
         */
        JMenuBar menuBar = new JMenuBar();

        JMenu solveMenu = new JMenu("Solve by");
        JMenuItem breadthFunctionSolve = new JMenuItem("Breadth First Search");
        JMenuItem depthFunctionSolve = new JMenuItem("Depth First Search");

        JMenuItem randomize = new JMenuItem("Randomize");
        JMenuItem imageSelector = new JMenuItem("Select image");

        solveMenu.add(breadthFunctionSolve);
        solveMenu.add(depthFunctionSolve);

        menuBar.add(solveMenu);
        menuBar.add(randomize);
        menuBar.add(imageSelector);

        setJMenuBar(menuBar);

        /*
            GRID LAYOUT
         */
        JPanel panel = initMatrix(size);

        this.add(panel);
        this.controller = new gameController(this, (byte) 0, (byte) 0);

        breadthFunctionSolve.addActionListener((e) -> {
            Thread bfs = new Thread(this.controller.bfsThread);
            bfs.start();
        });

        depthFunctionSolve.addActionListener((e) -> {
            Thread dfs = new Thread(this.controller.dfsThread);
            dfs.start();
        });

        randomize.addActionListener((e) -> {
            controller.randomize(this.randomMoves);
        });

        imageSelector.addActionListener((e) -> {
            if (this.solved) {
                JFileChooser imageChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes", "jpg", "png");
                imageChooser.setFileFilter(filter);

                int returnValue = imageChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = imageChooser.getSelectedFile();
                    try {
                        BufferedImage fullImage = ImageIO.read(selectedFile);
                        int rows = buttons.length;

                        int sizeImage = 650;

                        // Reescala la imagen completa al tamaño total deseado
                        Image scaledImage = fullImage.getScaledInstance(sizeImage, sizeImage, Image.SCALE_SMOOTH);
                        BufferedImage newImage = new BufferedImage(sizeImage, sizeImage, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = newImage.createGraphics();
                        g2d.drawImage(scaledImage, 0, 0, null);
                        g2d.dispose();

                        // Actualiza las dimensiones de los fragmentos para el nuevo tamaño de imagen
                        int chunkSize = sizeImage / rows;

                        // Divide la imagen y asigna cada parte a los botones
                        for (int y = 0; y < rows; y++) {
                            for (int x = 0; x < rows; x++) {
                                int startX = x * chunkSize;
                                int startY = y * chunkSize;
                                BufferedImage subImage = newImage.getSubimage(startX, startY, chunkSize, chunkSize);
                                ImageIcon icon = new ImageIcon(subImage);
                                buttons[x][y].setIcon(icon);
                                buttons[x][y].setText("");
                            }
                        }
                        buttons[0][0].setIcon(background);
                        this.allowMovement = true;
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    private JPanel initMatrix(int size) {
        this.buttons = new JButton[size][size];
        this.board = new byte[size][size];

        JPanel panel = new JPanel(new GridLayout(size, size));

        byte count = 0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                this.board[x][y] = count;
                JButton button = new JButton(String.valueOf(count++));

                int current_x = x;
                int current_y = y;

                button.setBorderPainted(true);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);

                button.addActionListener((e) -> {
                    if (this.allowMovement) {
                        controller.move((byte) current_x, (byte) current_y);
                    }
                });
                this.buttons[x][y] = button;
                panel.add(button);
            }
        }

        return panel;
    }

    public void setBoard(byte[][] board) {
        this.board = board;
    }

    void updateButtons(byte new_x, byte new_y, byte last_x, byte last_y) {
        buttons[last_x][last_y].setIcon(buttons[new_x][new_y].getIcon());
        buttons[new_x][new_y].setIcon(background);
    }
 
    public void showPannel(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public byte[][] getBoard() {
        return this.board;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean getAllowMovement() {
        return this.allowMovement;
    }

    public void setAllowMovement(boolean allowMovement) {
        this.allowMovement = allowMovement;
    }
}
