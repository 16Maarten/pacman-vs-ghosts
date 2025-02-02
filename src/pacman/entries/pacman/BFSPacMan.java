package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.internal.NodeTree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import pacman.game.Constants.MOVE;

public class BFSPacMan extends Controller<MOVE> {
    private Stack<NodeTree> possibleMoves = new Stack<NodeTree>();
    public Queue<Game> games = new LinkedList<Game>();
    public Stack<MOVE> bestMoves = new Stack<MOVE>();

    private StarterGhosts ghosts = new StarterGhosts();
    private NodeTree a = new NodeTree();

    public Game bestGameState;
    public int bestScore;

    private void BFSRecursive(Game game) {

        for (MOVE move : game.getPossibleMoves(game.getPacmanCurrentNodeIndex())) {

            Game copy = game.copy();
            a.prev = copy;
            copy.advanceGame(move, ghosts.getMove());

            games.add(copy);

            // Handle game over
            // Win or lose

            if (copy.gameOver()) {
                if (copy.getNumberOfActivePills() == 0 && copy.getNumberOfActivePowerPills() == 0) {
                    a.curr = copy;
                    bestScore = copy.getScore();
                    bestGameState = copy;
                    possibleMoves.push(a);
                }
                return;
            }

            // update score

            if (copy.getScore() > bestScore) {
                a.curr = copy;
                possibleMoves.push(a);
                bestGameState = copy;
                bestScore = copy.getScore();
            }

        }

    }

    public void BFSDriver(Game game) {
        int numOfIter = 0;
        int numOfLoop = 0;
        bestGameState = game;
        Game copy = game.copy();
        games.add(copy);
        while (numOfIter < 20) {
            if (numOfLoop == 0) {
                numOfIter++;
                numOfLoop = games.size();
            }
            BFSRecursive(games.remove());
            numOfLoop -= 1;
        }

        while (!possibleMoves.isEmpty()) {
            if (bestGameState == possibleMoves.peek().curr) {
                bestMoves.push(possibleMoves.peek().curr.getPacmanLastMoveMade());
                bestGameState = possibleMoves.peek().prev;
            }
            possibleMoves.pop();
        }
        return;

    }

    public MOVE getMove(Game game, long timeDue) {
        if (bestMoves.isEmpty()) BFSDriver(game);
        if (bestMoves.isEmpty()) return MOVE.NEUTRAL;
        return bestMoves.pop();
    }
}
