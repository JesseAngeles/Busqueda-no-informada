package com.mycompany.busquedanoinformada;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class gameController {

    private int bfsLimit = 21;
    private int dfsLimit = 24000;

    private byte x_pos;
    private byte y_pos;

    private boolean reset = false;

    private byte[][] board;
    private Graphic graphic;

    public gameController(Graphic graphic, byte x_pos, byte y_pos) {
        this.x_pos = x_pos;
        this.y_pos = y_pos;

        this.graphic = graphic;
        this.board = graphic.getBoard();
    }

    void move(byte x, byte y) {
        if (Math.abs(this.x_pos - x) + Math.abs(this.y_pos - y) == 1) {
            System.out.println("Move: " + x + ',' + y);
            graphic.updateButtons(x, y, this.x_pos, this.y_pos);

            Object[] values = swap(this.board, x, y, this.x_pos, this.y_pos);
            this.board = (byte[][]) values[0];
            this.x_pos = (byte) values[1];
            this.y_pos = (byte) values[2];

            this.graphic.setSolved(winVerification(this.board));

            graphic.setBoard(board);
        } else {
            System.out.println("No move: " + x + ',' + y);
        }
    }

    private Object[] swap(byte[][] originalBoard, byte x_new, byte y_new, byte x_old, byte y_old) {
        // Crear una copia profunda de originalBoard
        byte[][] swapBoard = new byte[originalBoard.length][];
        for (byte i = 0; i < originalBoard.length; i++) {
            swapBoard[i] = originalBoard[i].clone();
        }

        // Realizar el byteercambio en la copia
        byte swapValue = swapBoard[x_new][y_new];
        swapBoard[x_new][y_new] = swapBoard[x_old][y_old];
        swapBoard[x_old][y_old] = swapValue;

        // Devolver la copia modificada y las nuevas coordenadas
        return new Object[]{swapBoard, x_new, y_new};
    }

    public Runnable bfsThread = () -> {
        this.graphic.setAllowMovement(false);

        int sizeCounter = 0;
        byte moveCounter = 0;
        boolean win = winVerification(this.board);

        // Queue de tablero
        byte[][] currentBoard;
        Queue<byte[][]> boardsQueue = new LinkedList<>();

        // Queue de movimientos
        byte[] firstMove = getSpace(this.board);
        ArrayList<byte[]> moveArray = new ArrayList<>();
        Queue<ArrayList<byte[]>> movesQueue = new LinkedList<>();

        // Set de tablero visitados
        String visitedHex = new String();
        Set<String> visited = new HashSet<>();

        // Encola primer elemento
        currentBoard = duplicate(this.board);
        boardsQueue.offer(currentBoard);

        moveArray.add(firstMove);
        movesQueue.offer(moveArray);

        visitedHex = boardToString(currentBoard);
        visited.add(visitedHex);

        while (!boardsQueue.isEmpty() && !win) {
            byte[][] newBoard = boardsQueue.poll();
            ArrayList<byte[]> newMoves = movesQueue.poll();

            byte[] newSpace = getSpace(newBoard);
            byte x_space = newSpace[0];
            byte y_space = newSpace[1];

            ArrayList<byte[]> possibleNextMoves = getPossibleMoves(x_space, y_space);
            for (byte[] nextMove : possibleNextMoves) {
                ArrayList<byte[]> movesArray = new ArrayList<>(newMoves);

                byte[][] boardSwaped = new byte[4][4];
                byte[] posSwaped = new byte[2];
                String newVisited = new String();

                Object[] swapValues = swap(newBoard, nextMove[0], nextMove[1], x_space, y_space);

                boardSwaped = duplicate((byte[][]) swapValues[0]);
                posSwaped[0] = (byte) swapValues[1];
                posSwaped[1] = (byte) swapValues[2];

                newVisited = boardToString(boardSwaped);

                if (!visited.contains(newVisited)) {
                    visited.add(newVisited);
                    boardsQueue.offer(boardSwaped);
                    movesArray.add(posSwaped);
                    movesQueue.offer(movesArray);

                    if (winVerification(boardSwaped)) {
                        win = true;
                        moveArray = movesArray;
                    }

                } else {
                    if (movesArray.size() == this.bfsLimit) {

                        System.out.println("" + this.bfsLimit);
                        this.graphic.showPannel("Moves exceded");
                        this.graphic.setAllowMovement(true);
                        return;
                    }
                    if (sizeCounter != movesArray.size()) {
                        System.out.println("Movements yet: " + movesArray.size());
                        sizeCounter = movesArray.size();
                    }
                }
            }
        }

        for (byte[] move : moveArray) {
            moveCounter++;
            move(move[0], move[1]);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String message = "Solved in: " + --moveCounter + " movements";
        graphic.showPannel(message);
        this.graphic.setAllowMovement(true);
    };

    public Runnable dfsThread = () -> {
        this.graphic.setAllowMovement(false);

        int sizeCounter = 0;
        byte moveCounter = 0;
        boolean win = winVerification(this.board);

        // Stack de tablero
        byte[][] currentBoard;
        Stack<byte[][]> boardsStack = new Stack<>();

        // Stack de movimientos
        byte[] firstMove = getSpace(this.board);
        ArrayList<byte[]> moveArray = new ArrayList<>();
        Stack<ArrayList<byte[]>> movesStack = new Stack<>();

        // Set de tablero visitados
        String visitedHex = new String();
        Set<String> visited = new HashSet<>();

        // Apilar primer elemento
        currentBoard = duplicate(this.board);
        boardsStack.push(currentBoard);

        moveArray.add(firstMove);
        movesStack.push(moveArray);

        visitedHex = boardToString(currentBoard);
        visited.add(visitedHex);

        while (!boardsStack.isEmpty() && !win) {
            byte[][] newBoard = boardsStack.pop();
            ArrayList<byte[]> newMoves = movesStack.pop();

            byte[] newSpace = getSpace(newBoard);
            byte x_space = newSpace[0];
            byte y_space = newSpace[1];

            ArrayList<byte[]> possibleNextMoves = getPossibleMoves(x_space, y_space);

            for (byte[] nextMove : possibleNextMoves) {
                ArrayList<byte[]> movesArray = new ArrayList<>(newMoves);

                byte[][] boardSwaped = new byte[4][4];
                byte[] posSwaped = new byte[2];
                String newVisited = new String();

                Object[] swapValues = swap(newBoard, nextMove[0], nextMove[1], x_space, y_space);

                boardSwaped = duplicate((byte[][]) swapValues[0]);
                posSwaped[0] = (byte) swapValues[1];
                posSwaped[1] = (byte) swapValues[2];

                newVisited = boardToString(boardSwaped);

                if (!visited.contains(newVisited)) {
                    visited.add(newVisited);
                    boardsStack.push(boardSwaped);
                    movesArray.add(posSwaped);
                    movesStack.push(movesArray);

                    if (winVerification(boardSwaped)) {
                        win = true;
                        moveArray = movesArray;
                    }

                } else {
                    if (movesArray.size() == this.dfsLimit) {

                        System.out.println("" + this.dfsLimit);
                        this.graphic.showPannel("Moves exceded");
                        this.graphic.setAllowMovement(true);
                        return;
                    }
                    if (sizeCounter != movesArray.size()) {
                        System.out.println("Movements yet: " + movesArray.size());
                        sizeCounter = movesArray.size();
                    }
                }
            }
        }

        for (byte[] move : moveArray) {
            moveCounter++;
            move(move[0], move[1]);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String message = "Solved in: " + --moveCounter + " movements";
        graphic.showPannel(message);
        this.graphic.setAllowMovement(true);
    };

    private String boardToString(byte[][] board) {
        StringBuilder valuesChain = new StringBuilder();
        for (byte y = 0; y < 4; y++) {
            for (byte x = 0; x < 4; x++) {
                valuesChain.append(Integer.toHexString(board[x][y]));
            }
        }

        return valuesChain.toString();
    }

    public void randomize(int moves) {
        Random random = new Random();
        while (--moves > 0) {
            ArrayList<byte[]> possibleMove = getPossibleMoves(this.x_pos, this.y_pos);
            int numRandom = random.nextInt(possibleMove.size());
            byte[] nextMove = possibleMove.get(numRandom);
            move(nextMove[0], nextMove[1]);
        }
    }

    private ArrayList<byte[]> getPossibleMoves(byte x_pos, byte y_pos) {
        ArrayList<byte[]> possibleMove = new ArrayList<>();

        if (x_pos > 0) {
            possibleMove.add(new byte[]{(byte) (x_pos - 1), y_pos});
        }
        if (x_pos < 3) {
            possibleMove.add(new byte[]{(byte) (x_pos + 1), y_pos});
        }
        if (y_pos > 0) {
            possibleMove.add(new byte[]{x_pos, (byte) (y_pos - 1)});
        }
        if (y_pos < 3) {
            possibleMove.add(new byte[]{x_pos, (byte) (y_pos + 1)});
        }
        return possibleMove;
    }

    private boolean winVerification(byte[][] board) {
        byte count = 0;
        for (byte y = 0; y < 4; y++) {
            for (byte x = 0; x < 4; x++) {
                if (count++ != board[x][y]) {
                    return false;
                }

            }
        }
        return true;
    }

    private void printBoard(byte[][] board) {
        for (byte y = 0; y < 4; y++) {
            for (byte x = 0; x < 4; x++) {
                System.out.print("" + board[x][y] + "  ");
            }
            System.out.println("");
        }
    }

    public byte[] getSpace(byte[][] board) {
        for (byte y = 0; y < 4; y++) {
            for (byte x = 0; x < 4; x++) {
                if (board[x][y] == 0) {
                    return new byte[]{x, y};
                }
            }

        }

        return new byte[]{0, 0};
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    private byte[][] duplicate(byte[][] board) {
        byte[][] newBoard = new byte[4][4];

        for (byte y = 0; y < 4; y++) {
            for (byte x = 0; x < 4; x++) {
                newBoard[x][y] = board[x][y];
            }
        }

        return newBoard;
    }
}
