public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Player player = new Player(board, -1);
        Player player1 = new NetworkPlayer(board, 1);
        boolean turn = false;
        boolean redo = false;
        boolean playing = true;
        while(playing) {
            if (!redo) {
                turn = !turn;
            }
            System.out.println(board.getString());
            int[] move = turn ? player.GetInput() : player1.GetInput();
            String status = ""; // I really hate that this is how you do this in java
            if (turn) {
                status = board.Move(turn, move[0], move[1], true);
            } else {
                status = board.Move(turn, move[0], move[1]);
            }
            if (status != "Success") {
                System.out.println(status);
                redo = true;
                continue;
            }
            if (board.getWinner() == 1) {
                System.out.println("Player 1 Wins!");
                playing = false;
            }
            if (board.getWinner() == -1) {
                System.out.println("Player 2 Wins!");
                playing = false;
            }
            if (board.getWinner() == 2) {
                System.out.println("Tie!");
                playing = false;
            }
            redo = false;
        }



    }
}