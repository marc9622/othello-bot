
public class AlphaOthelloAIv1 implements IOthelloAI {

    private record ActionValue(Position pos, int val) {};

    @Override
    public Position decideMove(GameState state) {
        return maxValue(state, Constants.DEPTH).pos;
    }

    private ActionValue maxValue(GameState state, int depth) {
        depth--;

        if (isCutoff(state, depth))
            return new ActionValue(null, eval(state));

        ActionValue curMax = new ActionValue(new Position(-1, -1), Integer.MIN_VALUE);
        for (Position pos : state.legalMoves()) {
            GameState newState = copyState(state);

            if (!newState.insertToken(pos))
                throw new IllegalStateException();

            int newVal = minValue(newState, depth).val;

            if (newVal > curMax.val)
                curMax = new ActionValue(pos, newVal);
        }

        return curMax;
    }

    private ActionValue minValue(GameState state, int depth) {
        depth--;

        if (isCutoff(state, depth))
            return new ActionValue(null, eval(state));

        ActionValue curMin = new ActionValue(new Position(-1, -1), Integer.MAX_VALUE);
        for (Position pos : state.legalMoves()) {
            GameState newState = copyState(state);

            if (!newState.insertToken(pos))
                throw new IllegalStateException();

            int newVal = maxValue(newState, depth).val;

            if (newVal < curMin.val)
                curMin = new ActionValue(pos, newVal);
        }

        return curMin;
    }

    private static boolean isCutoff(GameState state, int depth) {
        return depth <= 0 || state.isFinished();
    }

    private static int eval(GameState state) {
        int[] counts = state.countTokens();
        return Integer.compare(counts[0], counts[1]);
    }

    private static GameState copyState(GameState state) {
        return new GameState(state.getBoard(), state.getPlayerInTurn());
    }

}
