import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Player {
    public String name = "Player";
    public Board board;
    int turn;

    public Player(Board board1, int turn1) {
        board = board1;
        turn = turn1;
    }

    public int[] GetInput() {
        Scanner myObj = new Scanner(System.in);
        System.out.print("X coord:");
        int x = myObj.nextInt();
        System.out.print("Y coord:");
        int y = myObj.nextInt();
        int[] coord = new int[2];
        coord[0] = x - 1;
        coord[1] = y - 1;

        return coord;
    }
}

class Computer extends Player {
    public String name = "Player";
    Random rand = new Random();
    int turn;

    public Computer(Board board1, int turn1) {
        super(board1, turn1);
    }

//        public int[] GetInput() {
//        int[] coord = new int[2];
//        int r1 = rand.nextInt(0,3);
//        int r2 = rand.nextInt(0,3);
//        coord[0] = r1;
//        coord[1] = r2;
//        while (board.getBoard()[coord[0]][coord[1]] != 0){
//            r1 = rand.nextInt(0, 3);
//            r2 = rand.nextInt(0, 3);
//            coord[0] = r1;
//            coord[1] = r2;
//        }
//        return coord;
//    }

    public int MiniMax(Board node, boolean maximize ) {
        if (node.getWinner() != 0) {
            if (node.getWinner() == 2) {
                return 0;
            } else {
                return node.getWinner();
            }
        }
        if (maximize) {
            int maxEval = -1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Board child = new Board();
                    child.setBoard(node.getBoard());
                    if (node.getBoard()[i][j] == 0) {
                        child.Move(false, j, i);
                        maxEval = Math.max(maxEval, MiniMax(child, false));
                    }

                }
            }
            return maxEval;
        } else {
            int minEval = 1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Board child = new Board();
                    child.setBoard(node.getBoard());
                    if (node.getBoard()[i][j] == 0) {
                        child.Move(true, j, i);
                        minEval = Math.min(minEval, MiniMax(child, true));
                    }
                }
            }
            return minEval;
        }
    }
    public int[] GetInput() { // Check all possible outcomes approach b/c tic-tac-toe is so small
        // Ex Board:
        // [1, 0, -1]
        // [1, 1, -1]
        // [-1,0, -1]

        if (turn == 1) {
            int maxEval = -2; // so that we always get a move
            int[] bestMove = new int[2];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Board child = new Board();
                    child.setBoard(board.getBoard());
                    if (board.getBoard()[i][j] == 0) {
                        child.Move(false, j, i);
                        int score = MiniMax(child, false);
                        if (score > maxEval) {
                            maxEval = score;
                            bestMove[0] = j;
                            bestMove[1] = i;
                        }

                    }
                    return bestMove;
                }
            }
        } else {
            int minEval = 2; // so that we always get a move
            int[] bestMove = new int[2];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Board child = new Board();
                    child.setBoard(board.getBoard());
                    if (board.getBoard()[i][j] == 0) {
                        child.Move(true, j, i);
                        int score = MiniMax(child, true);
                        if (score < minEval) {
                            minEval = score;
                            bestMove[0] = j;
                            bestMove[1] = i;
                        }
                    }
                    return bestMove;
                }
            }
        }
        return new int[2]; // no error handling like a boss
    }
}

class NetworkPlayer extends Player {
    int movePort = 5123;
    int port = 4123;
    public NetworkPlayer(Board board1, int turn1) {
        super(board1, turn1);
    }

    public int[] GetInput(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Send board state
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("0.0.0.0"); // Use the appropriate server address

            byte[] sendData = new byte[9];
            for (int i = 0; i < 3; i++ ) {
                for (int j = 0; j < 3; j++ ) {
                    sendData[(i * 3) + j] = (byte) board.getBoard()[i][j];
                }
            }
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (DatagramSocket serverSocket = new DatagramSocket(movePort)) {
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

//                int[] newBoard = new int[2];
//
//                for (int i = 0; i < 4; i++ ) {
//                    for (int j = 0; j < 4; j++ ) {
//                        newBoard[i][j] = receiveData[(i * 3) + j];
//                    }
//                }
//                board.setBoard(newBoard);

                int[] move = new int[2];
                move[0] = (int)receiveData[0];
                move[1] = (int)receiveData[1];
                System.out.println("RECEIVED: " + Arrays.toString(move));
                serverSocket.close();
                return move;


                // Optionally, the server can also send a response back to the client here
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        int[] move = new int[2];
        move[0] = 100; // no idea how exceptions work so this is what I'm doing
        move[1] = 100;
        return move;

    }

}
