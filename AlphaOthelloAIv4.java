import java.util.ArrayList;

public class AlphaOthelloAIv4 implements IOthelloAI {
    private int maxIndex;
    private int minIndex;

    public Position decideMove(GameState state) {
        // Decide weather it is player 1 or 2
        maxIndex = state.getPlayerInTurn() - 1;
        minIndex = 1 - maxIndex;

        return findBestMove(state);
    }

    private Position findBestMove(GameState state) {
        ArrayList<Position> moves = state.legalMoves();

        // If there is only one available move, take that one
        if (moves.size() == 1)
            return moves.get(0);

        int depth = 9;
        int maxEvaluation = Integer.MIN_VALUE;
        Position bestMove = new Position(-1, -1);

        // Start by running maximizing first
        // This is done since we just want the minimax function to return the evaluation
        for (Position move : moves) {
            GameState newState = copyState(state);
            newState.insertToken(move);

            int evaluation = minimax(newState, maxEvaluation, Integer.MAX_VALUE, depth - 1, false);

            // If evaluation is better, set that as the best move
            if (maxEvaluation <= evaluation) {
                maxEvaluation = evaluation;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Minimax function where Min og Max is combined
    private int minimax(GameState state, int alpha, int beta, int depth, boolean maximizingPlayer) {
        // The cutoff function
        if (depth <= 0 || state.isFinished())
            return evaluation(state);

        ArrayList<Position> moves = state.legalMoves();
        // If there is no legal moves
        // change player and continue the search
        if (moves.size() == 0) {
            GameState newState = copyState(state);
            newState.changePlayer();

            return minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);
        }

        // If player is maximizing evaluation
        if (maximizingPlayer) {
            int maxEvaluation = Integer.MIN_VALUE;

            for (Position move : moves) {
                GameState newState = copyState(state);
                newState.insertToken(move);

                maxEvaluation = Math.max(maxEvaluation,
                        minimax(newState, alpha, beta, depth - 1, !maximizingPlayer));
                alpha = Math.max(alpha, maxEvaluation);

                // Pruning
                if (alpha >= beta)
                    break;
            }

            return maxEvaluation;

        }
        // If okay is minimizing evaluation
        else {
            int minEvaluation = Integer.MAX_VALUE;

            for (Position move : moves) {
                GameState newState = copyState(state);
                newState.insertToken(move);

                minEvaluation = Math.min(minEvaluation,
                        minimax(newState, alpha, beta, depth - 1, !maximizingPlayer));
                beta = Math.min(beta, minEvaluation);

                // Pruning
                if (alpha <= beta)
                    break;
            }

            return minEvaluation;
        }
    }

    // The evaluation function
    private int evaluation(GameState state) {
        if (state.isFinished())
            return finishedEvaluation(state);

        // Evaluation of the current state if not a finished state
        return cornerEvaluation(state, 70) + mobilityEvaluation(state, 10) + countEvaluation(state, 50);
    }

    // Evaluation function for when it is a finished state.
    public int finishedEvaluation(GameState state) {
        int[] counts = state.countTokens();
        int player1 = counts[maxIndex];
        int player2 = counts[minIndex];

        // Winning
        if (player1 > player2)
            return 1000;

        // Losing
        if (player1 < player2)
            return -1000;

        // Draw
        return 0;
    }

    // Gives an evaluation based on the amount of corners
    public int cornerEvaluation(GameState state, int weight) {
        int[][] board = state.getBoard();

        int boardLength = board.length - 1;
        int[] corners = new int[] {
                board[0][0],
                board[0][boardLength],
                board[boardLength][0],
                board[boardLength][boardLength],
        };

        int cornerCount = 0;
        for (int corner : corners) {
            if (corner == maxIndex + 1) {
                cornerCount++;
            } else if (corner != 0) {
                cornerCount--;
            }
        }

        return weight * cornerCount / 4;
    }

    // Gives an evaluation based on the amount of legal moves
    public int mobilityEvaluation(GameState state, int weight) {
        GameState newState = copyState(state);

        int player1 = newState.legalMoves().size();
        newState.changePlayer();
        int player2 = newState.legalMoves().size();

        if ((player1 + player2) != 0) {
            return weight * (player1 - player2) / (player1 + player2);
        }

        return 0;
    }

    // Gives an evaluation based on the amount of points
    public int countEvaluation(GameState state, int weight) {
        int[] counts = state.countTokens();
        int player1 = counts[maxIndex];
        int player2 = counts[minIndex];

        return weight * (player1 - player2) / (player1 + player2);
    }

    // Creates a copy of the current state
    private GameState copyState(GameState state) {
        return new GameState(state.getBoard(), state.getPlayerInTurn());
    }
}
