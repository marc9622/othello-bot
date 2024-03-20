import java.util.ArrayList;

public class AlphaOthelloAIv4 implements IOthelloAI {
    private int maxIndex;
    private int minIndex;

    public Position decideMove(GameState state) {
        maxIndex = state.getPlayerInTurn() - 1;
        minIndex = 1 - maxIndex;

        return findBestMove(state);
    }

    private Position findBestMove(GameState state) {
        ArrayList<Position> moves = state.legalMoves();

        if (moves.size() == 1)
            return moves.get(0);

        int depth = 9;
        int maxEvaluation = Integer.MIN_VALUE;
        Position bestMove = new Position(-1, -1);
        for (Position move : moves) {
            GameState newState = copyState(state);
            newState.insertToken(move);

            int evaluation = minimax(newState, maxEvaluation, Integer.MAX_VALUE, depth - 1, false);

            if (maxEvaluation <= evaluation) {
                maxEvaluation = evaluation;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(GameState state, int alpha, int beta, int depth, boolean maximizingPlayer) {
        if (depth <= 0 || state.isFinished())
            return evaluation(state);

        ArrayList<Position> moves = state.legalMoves();
        if (moves.size() == 0) {
            GameState newState = copyState(state);
            newState.changePlayer();

            return minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);
        }

        if (maximizingPlayer) {
            int maxEvaluation = Integer.MIN_VALUE;

            for (Position move : moves) {
                GameState newState = copyState(state);
                newState.insertToken(move);

                maxEvaluation = Math.max(maxEvaluation,
                        minimax(newState, alpha, beta, depth - 1, !maximizingPlayer));
                alpha = Math.max(alpha, maxEvaluation);

                if (alpha >= beta)
                    break;
            }

            return maxEvaluation;
        } else {
            int minEvaluation = Integer.MAX_VALUE;

            for (Position move : moves) {
                GameState newState = copyState(state);
                newState.insertToken(move);

                minEvaluation = Math.min(minEvaluation,
                        minimax(newState, alpha, beta, depth - 1, !maximizingPlayer));
                beta = Math.min(beta, minEvaluation);

                if (alpha <= beta)
                    break;
            }

            return minEvaluation;
        }
    }

    private int evaluation(GameState state) {
        if (state.isFinished())
            return finishedHeuristic(state);

        return cornerHeuristic(state, 70) + mobilityHeuristic(state, 10) + countHeuristic(state, 50);
    }

    public int finishedHeuristic(GameState state) {
        int[] counts = state.countTokens();
        int player1 = counts[maxIndex];
        int player2 = counts[minIndex];

        if (player1 > player2)
            return 1000;

        if (player1 < player2)
            return -1000;

        return 0;
    }

    public int cornerHeuristic(GameState state, int weight) {
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

    public int mobilityHeuristic(GameState state, int weight) {
        GameState newState = copyState(state);

        int player1 = newState.legalMoves().size();
        newState.changePlayer();
        int player2 = newState.legalMoves().size();

        if ((player1 + player2) != 0) {
            return weight * (player1 - player2) / (player1 + player2);
        }

        return 0;
    }

    public int countHeuristic(GameState state, int weight) {
        int[] counts = state.countTokens();
        int player1 = counts[maxIndex];
        int player2 = counts[minIndex];

        return weight * (player1 - player2) / (player1 + player2);
    }

    private GameState copyState(GameState state) {
        return new GameState(state.getBoard(), state.getPlayerInTurn());
    }
}
