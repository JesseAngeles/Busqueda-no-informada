package com.mycompany.busquedanoinformada;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class gameController {

    private int x_pos;
    private int y_pos;

    private int[][] board;
    private Graphic graphic;

    public gameController(Graphic graphic, int x_pos, int y_pos) {
        this.x_pos = x_pos;
        this.y_pos = y_pos;

        this.graphic = graphic;
        this.board = graphic.getBoard();
    }

    void move(int x, int y) {
        if (Math.abs(this.x_pos - x) + Math.abs(this.y_pos - y) == 1) {
            System.out.println("Move" + x + ',' + y);
            graphic.updateButtons(x, y, this.x_pos, this.y_pos);

            Object[] values = swap(this.board, x, y, this.x_pos, this.y_pos);
            this.board = (int[][]) values[0];
            this.x_pos = (int) values[1];
            this.y_pos = (int) values[2];

            graphic.setBoard(board);
        } else {
            System.out.println("No move" + x + ',' + y);
        }
    }

    private Object[] swap(int[][] originalBoard, int x_new, int y_new, int x_old, int y_old) {
        // Crear una copia profunda de originalBoard
        int[][] swapBoard = new int[originalBoard.length][];
        for (int i = 0; i < originalBoard.length; i++) {
            swapBoard[i] = originalBoard[i].clone();
        }

        // Realizar el intercambio en la copia
        int swapValue = swapBoard[x_new][y_new];
        swapBoard[x_new][y_new] = swapBoard[x_old][y_old];
        swapBoard[x_old][y_old] = swapValue;

        // Devolver la copia modificada y las nuevas coordenadas
        return new Object[]{swapBoard, x_new, y_new};
    }

    public int bfsSolver() {
        int count = 0;
        boolean win = winVerification(this.board);
        String visitedHex = new String();

        ArrayList<int[]> moveArray = new ArrayList<>();

        Set<String> visited = new HashSet<>();

        Queue<int[][]> boardsQueue = new LinkedList<>();
        Queue<ArrayList<int[]>> movesQueue = new LinkedList<>();

        int[] firstMove = getSpace(this.board);
        moveArray.add(firstMove);

        boardsQueue.offer(this.board);                            // Se agrega el primer tablero a QUEUE
        movesQueue.offer(moveArray);                        // Se agrega el primer movimiento a MOVES QUEUE

        visited.add(boardToString(this.board));
        System.out.println("visited" + boardToString(this.board));
        
        while (!boardsQueue.isEmpty() && !win) {
            int[][] newBoard = boardsQueue.poll();                    // Se elimina el primer tablero de QUEUE
            ArrayList<int[]> moves = movesQueue.poll();         // Se elimina el primer movimiento de MOVES QUEUE

            int[] currentSpaceLocation = getSpace(newBoard);
            int x_old = currentSpaceLocation[0];
            int y_old = currentSpaceLocation[1];

            ArrayList<int[]> possibleMoves = getPossibleMoves(x_old, y_old);
            for (int[] nextMove : possibleMoves) {              // Recorremos todos los posibles movimientos

                ArrayList<int[]> constMove = new ArrayList<>(moves);
                Object[] values = swap(newBoard, nextMove[0], nextMove[1], x_old, y_old);

                visitedHex = boardToString((int [][]) values[0]);
                
                if (constMove.size() >= 20) {
                    return -1;
                }
                
                if (!visited.contains(visitedHex)) {
                    visited.add(visitedHex);
                    boardsQueue.offer((int[][]) values[0]);                             // Se encola el nuevo tablero hijo
                    constMove.add(new int[]{(int) values[1], (int) values[2]});         // Se enlista el nuevo paso
                    movesQueue.offer(constMove);
                }

                if (winVerification((int[][]) values[0])) {
                    win = true;
                    moveArray = constMove;
                    break;
                }

            }
        }

        for (int[] move : moveArray) {
            count++;
            move(move[0], move[1]);
            //Thread.sleep(1000);

        }

        return count - 1;
    }

    public int dfsSolver() {

        return 0;
    }

    private String boardToString(int[][] board){
        StringBuilder valuesChain = new StringBuilder();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                 valuesChain.append(Integer.toHexString(board[x][y]));
            }
        }
        
        return valuesChain.toString();
    }
    
    public void randomize(int moves) {
        Random random = new Random();
        while (--moves > 0) {
            ArrayList<int[]> possibleMove = getPossibleMoves(this.x_pos, this.y_pos);
            int numRandom = random.nextInt(possibleMove.size());
            int[] nextMove = possibleMove.get(numRandom);
            move(nextMove[0], nextMove[1]);
        }
    }

    private ArrayList<int[]> getPossibleMoves(int x_pos, int y_pos) {
        ArrayList<int[]> possibleMove = new ArrayList<>();

        if (x_pos > 0) {
            possibleMove.add(new int[]{x_pos - 1, y_pos});
        }
        if (x_pos < 3) {
            possibleMove.add(new int[]{x_pos + 1, y_pos});
        }
        if (y_pos > 0) {
            possibleMove.add(new int[]{x_pos, y_pos - 1});
        }
        if (y_pos < 3) {
            possibleMove.add(new int[]{x_pos, y_pos + 1});
        }
        return possibleMove;
    }

    private boolean winVerification(int[][] board) {
        int count = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (count++ != board[x][y]) {
                    return false;
                }

            }
        }
        return true;
    }

    private void printBoard(int[][] board) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                System.out.print("-" + board[x][y] + "  ");
            }
            System.out.println("");
        }
    }

    public int[] getSpace(int[][] board) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (board[x][y] == 0) {
                    return new int[]{x, y};
                }
            }

        }

        return new int[]{0, 0};
    }

}
