import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Board {
    private int[][] board = new int[3][3]; // was gonna do bitmap but 9 isn't a great number
    private String boardRep =
            """
                    ---
                    ---
                    ---
                    """;

    public Board() {
    }

    public String Move(boolean turn, int x, int y) {
        if (x > 2 || y > 2 || x < 0 || y < 0) {
            return "Invalid Coordinate";
        }
        if (board[y][x] != 0) {
            return "Occupied Coordinate";
        }
        if (turn) {
            board[y][x] = 1;
        } else {
            board[y][x] = -1;
        }
        return "Success";
    }
    public String Move(boolean turn, int x, int y, boolean sendMove) {
        if (x > 2 || y > 2 || x < 0 || y < 0) {
            return "Invalid Coordinate";
        }
        if (board[y][x] != 0) {
            return "Occupied Coordinate";
        }
        if (turn) {
            board[y][x] = 1;
        } else {
            board[y][x] = -1;
        }
        if (sendMove) {
            int movePort = 5123;
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("0.0.0.0"); // Use the appropriate server address

                byte[] sendData = new byte[2];
                sendData[0] = (byte)x;
                sendData[1] = (byte)y;
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, movePort);
                clientSocket.send(sendPacket);
                System.out.println(sendData.toString());
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Success";
    }

    private void updateBoardRep() {
        boardRep = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                switch (board[i][j]) {
                    case 0:
                        boardRep += " - ";
                        break;
                    case 1:
                        boardRep += " X ";
                        break;
                    case -1:
                        boardRep += " O ";
                        break;
                }
            }
            boardRep += "\n";
        }
    }

    public void updateFromNetwork() {
        int port = 4123;
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                int[][] newBoard = new int[3][3];

                for (int i = 0; i < 3; i++ ) {
                    for (int j = 0; j < 3; j++ ) {
                        newBoard[i][j] = receiveData[(i * 3) + j];
                    }
                }
                board = newBoard;
                serverSocket.close();
                break;
                // Optionally, the server can also send a response back to the client here
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getWinner() {
        // check rows
        for (int i = 0; i < 3; i++) {
            int sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += board[i][j];
            }
            if (sum == 3) {
                return 1;
            } else if (sum == -3){
                return -1;
            }
        }
        // Check Columns
        for (int i = 0; i < 3; i++) {
            int sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += board[j][i];
            }
            if (sum == 3) {
                return 1;
            } else if (sum == -3){
                return -1;
            }
        }
        // Check diagonal
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += board[i][i];
        }
        if (sum == 3) {
            return 1;
        } else if (sum == -3){
            return -1;
        }
        // Check other diagonal
        sum = 0;
        for (int i = 0; i < 3; i++) {
            int j = 2 - i;
            sum += board[i][j];
        }
        if (sum == 3) {
            return 1;
        } else if (sum == -3){
            return -1;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return 0; // No winner
                }
            }
        }
        return 2; // Tie
    }

    public String getString() {
        updateBoardRep();
        return boardRep;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board1) {
        board = board1;
    }

}
