import java.util.Collection;

public final class AlphaOthelloAIv2 implements IOthelloAI {

    private static final boolean debug = false;

    private record ActionValue(Position pos, int val) {};

    @Override
    public final Position decideMove(final GameState state) {
        if (debug) System.out.println("");
        return maxValue(state, Constants.DEPTH).pos;
    }

    private ActionValue maxValue(final GameState state, int depth) {
        depth--;

        if (isCutoff(state, depth))
            return new ActionValue(null, eval(state));

        Collection<Position> legalMoves = state.legalMoves();
        //legalMoves.stream().forEach(pos -> System.out.println(pos));

        ActionValue curMax = new ActionValue(new Position(-1, -1), Integer.MIN_VALUE);
        for (Position pos : legalMoves) {
            final GameState newState = copyState(state);

            if (!newState.insertToken(pos))
                throw new IllegalStateException();

            final int newVal = minValue(newState, depth).val;

            if (debug) System.out.println("Position: " + pos + "\tNew value: " + newVal + "\tCurrent Maximum: " + curMax.val);

            if (newVal > curMax.val)
                curMax = new ActionValue(pos, newVal);
        }

        return curMax;
    }

    private ActionValue minValue(final GameState state, int depth) {
        depth--;

        if (isCutoff(state, depth))
            return new ActionValue(null, eval(state));

        Collection<Position> legalMoves = state.legalMoves();

        ActionValue curMin = new ActionValue(new Position(-1, -1), Integer.MAX_VALUE);
        for (Position pos : legalMoves) {
            final GameState newState = copyState(state);

            if (!newState.insertToken(pos))
                throw new IllegalStateException();

            final int newVal = maxValue(newState, depth).val;

            if (debug) System.out.println("Position: " + pos + "\tNew value: " + newVal + "\tCurrent Minimum: " + curMin.val);

            if (newVal < curMin.val)
                curMin = new ActionValue(pos, newVal);
        }

        return curMin;
    }

    private static boolean isCutoff(final GameState state, final int depth) {
        return depth <= 0 || state.isFinished();
    }

    private static int eval(final GameState state) {
        final int[] counts = state.countTokens();
        if (state.isFinished()) {
            if (counts[0] > counts[1])
                return state.getBoard().length * state.getBoard().length;

            if (counts[0] < counts[1])
                return -state.getBoard().length * state.getBoard().length;

            return 0;
        }
        return counts[0] - counts[1];
    }

    private static GameState copyState(GameState state) {
        return new GameState(state.getBoard(), state.getPlayerInTurn());
    }

}
