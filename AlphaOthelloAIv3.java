import java.util.ArrayList;

public final class AlphaOthelloAIv3 implements IOthelloAI {

    private static int maxIndex;
    private static int minIndex;

    public Position decideMove(final GameState state) {
        maxIndex = state.getPlayerInTurn() - 1;
        minIndex = 1 - maxIndex;

        return findBestMove(state);
    }

    private static Position findBestMove(final GameState state) {

        final int depth = 8;
        final ArrayList<Position> moves = state.legalMoves();

        if (moves.size() == 1)
            return moves.get(0);

        int maxEvaluation = Integer.MIN_VALUE;
        Position bestMove = new Position(-1, -1);

        for (final Position move : moves) {
            final GameState newState = newState(state, move);

            final int evaluation = minimax(newState, Integer.MIN_VALUE, Integer.MAX_VALUE, depth - 1, false);

            if (maxEvaluation <= evaluation) {
                maxEvaluation = evaluation;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int minimax(final GameState state, int alpha, int beta,
                               final int depth, final boolean maximizingPlayer) {

        if (depth <= 0 || state.isFinished())
            return evaluation(state);

        final ArrayList<Position> moves = state.legalMoves();

        if (moves.size() == 0) {
            final GameState newState = newState(state, new Position(-1, -1));

            return minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);
        }

        if (maximizingPlayer) {
            int maxEvaluation = Integer.MIN_VALUE;

            for (final Position move : moves) {
                final GameState newState = newState(state, move);

                final int evaluation = minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);

                maxEvaluation = Math.max(evaluation, maxEvaluation);
                alpha = Math.max(alpha, maxEvaluation);

                if (alpha >= beta)
                    break;
            }

            return maxEvaluation;
        } else {
            int minEvaluation = Integer.MAX_VALUE;

            for (final Position move : moves) {
                final GameState newState = newState(state, move);

                final int evaluation = minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);

                minEvaluation = Math.min(evaluation, minEvaluation);
                beta = Math.min(beta, minEvaluation);

                if (alpha >= alpha)
                    break;
            }
            
            return minEvaluation;
        }
    }

    private static int evaluation(final GameState state) {
        final int[] counts = state.countTokens();

        if (state.isFinished()) {
            final int boardLength = state.getBoard().length;

            if (counts[maxIndex] > counts[minIndex])
                return boardLength * boardLength;

            if (counts[maxIndex] < counts[minIndex])
                return -boardLength * boardLength;

            return 0;
        }

        return counts[maxIndex] - counts[minIndex];
    }

    private static GameState newState(final GameState state, final Position move) {
        final GameState newState = new GameState(state.getBoard(), state.getPlayerInTurn());
        newState.insertToken(move);
        return newState;
    }
}
