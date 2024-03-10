package com.mycompany.busquedanoinformada;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
        System.out.println("clic:" + x + ',' + y);
        System.out.println("black:" + x_pos + ',' + y_pos);

        if (Math.abs(x_pos - x) + Math.abs(y_pos - y) == 1) {
            System.out.println("Move");
            graphic.updateButtons(x, y, x_pos, y_pos);
            Object[] values = swap(this.board, x, y, this.x_pos, this.y_pos);
            this.board = (int[][]) values[0];
            this.x_pos = (int) values[1];
            this.y_pos = (int) values[2];

            graphic.setBoard(board);
        } else {
            System.out.println("no move");
        }
    }

    private Object[] swap(int[][] board, int x_new, int y_new, int x_old, int y_old) {
        int swapValue = board[x_new][y_new];
        board[x_new][y_new] = 0;
        board[x_old][y_old] = swapValue;
        x_old = x_new;
        y_old = y_new;

        return new Object[]{board, x_old, y_old};
    }

    public int bfsSolver() {
        int count = 0;
        boolean win = false;

        Queue<int[][]> queue = new LinkedList<>();
        Queue<ArrayList<int[]>> movesTree = new LinkedList<>();
                
        queue.offer(board);

        while (!queue.isEmpty() && win) {
            //   extraer el primer camino de la COLA (DEQUEUE)
            int[][] newBoard = queue.peek();
            ArrayList<int[]> move = movesTree.peek();   // primera interacion diferente
            
            int[] spaceLocation = getSpace(newBoard);
            int x_old = spaceLocation[0];
            int y_old = spaceLocation[1];

            ArrayList<int[]> possibleMoves = getPossibleMoves(newBoard, x_old, y_old);
            for (int[] nextMove : possibleMoves) {
                // crear nuevos caminos a todos los hijos del camino extraido
                Object[] values = swap(newBoard, nextMove[0], nextMove[1], x_old, y_old);
                
                move.add(new int[]{(int) values[1], (int) values[2]});
                if (winVerification((int[][]) values[0])) {
                    win = true;
                } else {        // Agregar los nuevos caminos al final  de COLA (QUEUE)
                    queue.offer((int[][]) values[0]);
                    movesTree.offer(move);
                }
            }
        }
        
         

        return count;
    }

    public int dfsSolver() {

        return 0;
    }

    public void randomize(int moves) {
        Random random = new Random();
        while (--moves > 0) {
            ArrayList<int[]> possibleMove = getPossibleMoves(this.board, this.x_pos, this.y_pos);
            int numRandom = random.nextInt(possibleMove.size());
            int[] nextMove = possibleMove.get(numRandom);
            move(nextMove[0], nextMove[1]);
        }
    }

    private ArrayList<int[]> getPossibleMoves(int[][] board, int x_pos, int y_pos) {
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
            for (int x = 0; x < 10; x++) {
                if (count != board[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void printBoard() {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                System.out.print(board[x][y] + '\t');
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
