import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AlphaOthelloAIv4 implements IOthelloAI {

    private final Map<Integer, Integer> cache = new HashMap<>();

    private Integer getCached(GameState state) {
        return cache.get(Arrays.hashCode(state.getBoard()));
    }

    private void putCached(GameState state, int evaluation) {
        cache.put(Arrays.hashCode(state.getBoard()), evaluation);
    }

    private int maxIndex;
    private int minIndex;

    public final Position decideMove(GameState state) {
        maxIndex = state.getPlayerInTurn() - 1;
        minIndex = 1 - maxIndex;

        return findBestMove(state);
    }

    private Position findBestMove(GameState state) {
        List<Position> moves = state.legalMoves();

        if (moves.size() == 1)
            return moves.get(0);

        int depth = 8;
        int maxEvaluation = Integer.MIN_VALUE;
        Position bestMove = new Position(-1, -1);
        for (Position move : moves) {
            GameState newState = copyState(state);
            newState.insertToken(move);

            /* Check cached values */ {
                Integer evaluation = getCached(newState);
                if (evaluation != null) {
                    // System.out.println("Used cached");
                    if (maxEvaluation <= evaluation) {
                        maxEvaluation = evaluation;
                        bestMove = move;
                    }
                    continue;
                }
            }

            int evaluation = minimax(newState, maxEvaluation, Integer.MAX_VALUE, depth - 1, false);
            putCached(newState, evaluation);

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

        List<Position> moves = state.legalMoves();
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

                /* Check cached values */ {
                    Integer evaluation = getCached(newState);
                    if (evaluation != null) {
                        // System.out.println("Used cached");
                        maxEvaluation = Math.max(maxEvaluation, evaluation);
                        alpha = Math.max(alpha, maxEvaluation);
                        if (alpha >= beta)
                            break;
                        continue;
                    }
                }

                int evaluation = minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);
                putCached(newState, evaluation);

                maxEvaluation = Math.max(maxEvaluation, evaluation);
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

                /* Check cached values */ {
                    Integer evaluation = getCached(newState);
                    if (evaluation != null) {
                        // System.out.println("Used cached");
                        minEvaluation = Math.min(minEvaluation, evaluation);
                        beta = Math.min(beta, minEvaluation);
                        if (alpha <= beta)
                            break;
                        continue;
                    }
                }

                int evaluation = minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);
                putCached(newState, evaluation);

                minEvaluation = Math.min(minEvaluation, evaluation);
                beta = Math.min(beta, minEvaluation);

                if (alpha <= beta)
                    break;
            }

            return minEvaluation;
        }
    }

    private int evaluation(GameState state) {
        if (state.isFinished())
            return finishedEvaluation(state);

        return cornerEvaluation(state, 70) + mobilityEvaluation(state, 10) + countEvaluation(state, 50);
    }

    public int finishedEvaluation(GameState state) {
        int[] counts = state.countTokens();
        int player1 = counts[maxIndex];
        int player2 = counts[minIndex];

        if (player1 > player2)
            return 1000;

        if (player1 < player2)
            return -1000;

        return 0;
    }

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

    public int countEvaluation(GameState state, int weight) {
        int[] counts = state.countTokens();
        int player1 = counts[maxIndex];
        int player2 = counts[minIndex];

        return weight * (player1 - player2) / (player1 + player2);
    }

    private GameState copyState(GameState state) {
        return new GameState(state.getBoard(), state.getPlayerInTurn());
    }

}
