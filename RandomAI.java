import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomAI implements IOthelloAI {

    public Position decideMove(GameState s) {
        List<Position> moves = s.legalMoves();
        return !moves.isEmpty()
            ? moves.get(ThreadLocalRandom.current().nextInt(moves.size()))
            : new Position(-1, -1);
    }
}
