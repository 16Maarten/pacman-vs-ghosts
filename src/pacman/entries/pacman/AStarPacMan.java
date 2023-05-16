package pacman.entries.pacman;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Heuristic;
import pacman.game.internal.NodeTree;

import java.util.*;
import java.util.stream.Collectors;

public class AStarPacMan extends Controller<MOVE> {

    private Queue<NodeTree> games = new LinkedList<NodeTree>();
    private Queue<Double> scores = new LinkedList<Double>();

    private Stack<MOVE> moves = new Stack<MOVE>();
    private Stack<NodeTree> possibleMoves = new Stack<NodeTree>();

    private StarterGhosts ghosts = new StarterGhosts();

    private static final int MIN_DISTANCE=10;

    private Game bestGame = null;

    public void aStarFill (Game game, int maxDepth) {

        Game copy = game.copy();
        NodeTree head = new NodeTree();
        head.curr = copy;
        head.prev = null;

        double initScore = 0;
        double bestScore = 0;

        games.add(head);
        scores.add(initScore);
        while (!games.isEmpty()){
            NodeTree currGame = games.remove();
            Game thisgame = currGame.curr;
            double thisScore = scores.remove();

            if (thisScore > bestScore) {
                bestScore = thisScore;
                bestGame = thisgame;
                possibleMoves.push(currGame);
            }

            if (thisgame.gameOver()){
                if (thisgame.getNumberOfActivePills() == 0 && thisgame.getNumberOfActivePowerPills() == 0){
                    break;
                }
                else {
                    return;
                }
            }

            else {

                if (currGame.depth < maxDepth) {

                    LinkedList<NodeTree> n = new LinkedList<NodeTree>();
                    LinkedList<Double> d = new LinkedList<Double>();

                    for (MOVE move : thisgame.getPossibleMoves(thisgame.getPacmanCurrentNodeIndex())) {

                        Game newGame = thisgame.copy();
                        NodeTree newNode = new NodeTree();
                        newNode.prev = thisgame;
                        newNode.depth = currGame.depth +1;
                        newGame.advanceGame(move, ghosts.getMove());
                        newNode.curr = newGame;
                        n.add(newNode);
                        d.add((double)newGame.getScore() + Heuristic.heuristicFuncForNext(newGame));

                    }

                    while (!n.isEmpty()) {
                        int high = -1;
                        int highIndex = -1;
                        for (int i = 0; i < n.size(); i++) {
                            if (n.get(i).curr.getScore() > high) {
                                high = n.get(i).curr.getScore();
                                highIndex = i;
                            }
                        }
                        games.add(n.get(highIndex));
                        scores.add(d.get(highIndex));
                        n.remove(highIndex);
                        d.remove(highIndex);
                    }

                }

            }

        }

        while (!possibleMoves.isEmpty()) {
            if (possibleMoves.peek().curr == bestGame) {
                moves.push(possibleMoves.peek().curr.getPacmanLastMoveMade());
                bestGame = possibleMoves.peek().prev;
            }
            possibleMoves.pop();
        }

    }

    public MOVE getMove(Game game, long timeDue) {
        for(Constants.GHOST ghost : Constants.GHOST.values())
            if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
                if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
                    return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost), Constants.DM.PATH);
        if (moves.isEmpty()) {
            aStarFill(game, 10);
        }
        if (moves.isEmpty()) {
            return MOVE.NEUTRAL;
        }
        return moves.pop();
    }

}

