import java.util.ArrayList;

public class AlphaOthelloAIv3 implements IOthelloAI {
    public Position decideMove(GameState state) {
        return findBestMove(state);
    }

    private Position findBestMove(GameState state) {
        ArrayList<Position> moves = state.legalMoves();

        if (moves.size() == 1)
            return moves.get(0);

        int depth = 7;
        float maxEvaluation = -100f;
        Position bestMove = null;
        for (Position move : moves) {
            GameState newState = copyState(state);
            newState.insertToken(move);

            float evaluation = minimax(newState, -100f, 100f, depth - 1, false);

            if (maxEvaluation <= evaluation) {
                maxEvaluation = evaluation;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private float minimax(GameState state, float alpha, float beta, int depth, boolean maximizingPlayer) {
        if (depth <= 0 || state.isFinished())
            return evaluation(state);

        ArrayList<Position> moves = state.legalMoves();
        if (moves.size() == 0) {
            GameState newState = copyState(state);
            newState.changePlayer();

            return minimax(newState, alpha, beta, depth - 1, !maximizingPlayer);
        }

        if (maximizingPlayer) {
            float maxEvaluation = -100f;

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
            float minEvaluation = 100f;

            for (Position move : moves) {
                GameState newState = copyState(state);
                newState.insertToken(move);

                minEvaluation = Math.min(minEvaluation,
                        minimax(newState, alpha, beta, depth - 1, !maximizingPlayer));

                if (alpha <= minEvaluation)
                    break;

                beta = Math.min(beta, minEvaluation);
            }

            return minEvaluation;
        }
    }

    private float evaluation(GameState state) {
        int[] counts = state.countTokens();

        if (state.isFinished()) {
            if (counts[0] > counts[1])
                return state.getBoard().length * state.getBoard().length;

            if (counts[0] < counts[1])
                return -state.getBoard().length * state.getBoard().length;

            return 0;
        }

        return counts[0] - counts[1];
    }

    private GameState copyState(GameState state) {
        return new GameState(state.getBoard(), state.getPlayerInTurn());
    }
}
