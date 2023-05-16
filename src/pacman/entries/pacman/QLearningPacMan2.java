package pacman.entries.pacman;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.*;
import pacman.game.Game;
import pacman.game.StateAction;

public class QLearningPacMan2 extends Controller<MOVE> {
    private double alpha; // Learning rate (0.0 to 1.0)
    private double gamma; // Discount factor (0.0 to 1.0)
    private double epsilon; // Exploration rate (0.0 to 1.0)
    private int minDistanceGhost; // Minimum distance to non-edible ghost

    private Map<StateAction, Double> qTable;
    private Random random;

    public QLearningPacMan2(double alpha, double gamma, double epsilon, int minDistanceGhost) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.minDistanceGhost = minDistanceGhost;
        qTable = new HashMap<>();
        random = new Random();
    }

    public MOVE getMove(Game game, long timeDue) {
        int current = game.getPacmanCurrentNodeIndex();
        MOVE[] possibleMoves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());

        MOVE selectedMove = MOVE.NEUTRAL;
        boolean ranFromGhost = false;

        // // If any non-edible ghost is too close (less than minDistanceGhost), run away
        // for (GHOST ghost : GHOST.values()) {
        //     if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
        //         if (game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost)) < minDistanceGhost) {
        //             selectedMove = game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
        //             ranFromGhost = true;
        //         }
        //     }
        // }

        // if (!ranFromGhost) {
            if (random.nextDouble() < epsilon) {
                // Exploration: Choose a random move
                selectedMove = possibleMoves[random.nextInt(possibleMoves.length)];
            } else {
                // Exploitation: Choose the move with the highest Q-value
                selectedMove = getBestMove(current, possibleMoves);
            }
        // }

        // Execute the selected move in the game
        Game copy = game.copy();
        copy.updatePacMan(selectedMove);
        int newState = copy.getPacmanCurrentNodeIndex();

        // Calculate the reward
        double reward = calculateReward2(game, copy);

        // Update the Q-value for the previous state-action pair
        StateAction stateAction = new StateAction(current, selectedMove);
        double oldQValue = getQValue(stateAction);
        double maxQValue = getMaxQValue(newState, possibleMoves);
        double newQValue = oldQValue + alpha * (reward + gamma * maxQValue - oldQValue);
        setQValue(stateAction, newQValue);

        return selectedMove;
    }

	private double calculateReward2(Game previousState, Game newState) {
		int previousScore = previousState.getScore();
		int newScore = newState.getScore();
	
		int previousPillsLeft = previousState.getNumberOfActivePills();
		int newPillsLeft = newState.getNumberOfActivePills();
	
		int previousGhostsEaten = previousState.getNumGhostsEaten();
		int newGhostsEaten = newState.getNumGhostsEaten();
	
		boolean pillEaten = (previousPillsLeft > newPillsLeft);
		boolean ghostEaten = (newGhostsEaten > previousGhostsEaten);
	
		double reward = 0.0;
	
		// Positive rewards
		if (pillEaten) {
			reward += 10.0; // Reward for eating a pill
		}
	
		if (ghostEaten) {
			reward += 50.0; // Reward for eating a ghost
		}
	
		// Negative rewards
		if (previousScore > newScore) {
			reward -= 1.0; // Penalty for losing score
		}
	
		if (previousPillsLeft < newPillsLeft) {
			reward -= 5.0; // Penalty for leaving pills uneaten
		}
	
		if (previousGhostsEaten > newGhostsEaten) {
			reward -= 20.0; // Penalty for losing ghosts eaten count
		}
	
		// Custom rewards based on game state
		// Add more reward conditions as per your game's specific requirements
	
		return reward;
	}
	

    private double calculateReward(Game previousState, Game newState) {
        int current = previousState.getPacmanCurrentNodeIndex();
        MOVE selectedMove = previousState.getPacmanLastMoveMade();

        double ghostReward = 0.0;
        double edibleGhostReward = 0.0;
        double emptyPositionReward = 0.0;

        // Strategy 1: If any non-edible ghost is too close (less than minDistanceGhost), run away
        for (GHOST ghost : GHOST.values()) {
			if (newState.getGhostEdibleTime(ghost) == 0 && newState.getGhostLairTime(ghost) == 0) {
				if (newState.getShortestPathDistance(current, newState.getGhostCurrentNodeIndex(ghost)) < minDistanceGhost) {
					ghostReward = -100.0; // Negative reward for being close to a non-edible ghost
					break;
				}
			}
		}

		// Strategy 2: Find the nearest edible ghost and go after them
		int minDistance = Integer.MAX_VALUE;
		GHOST minGhost = null;

		for (GHOST ghost : GHOST.values()) {
			if (newState.getGhostEdibleTime(ghost) > 0) {
				int distance = newState.getShortestPathDistance(current, newState.getGhostCurrentNodeIndex(ghost));

				if (distance < minDistance) {
					minDistance = distance;
					minGhost = ghost;
				}
			}
		}

		if (minGhost != null) {
			edibleGhostReward = 50.0 / minDistance; // Positive reward for being close to an edible ghost
		}

		// Strategy 3: Go after the pills and power pills
		int[] pills = newState.getPillIndices();
		int[] powerPills = newState.getPowerPillIndices();

		int closestPill = newState.getClosestNodeIndexFromNodeIndex(current, pills, DM.PATH);
		int closestPowerPill = newState.getClosestNodeIndexFromNodeIndex(current, powerPills, DM.PATH);

		int distanceToPill = newState.getShortestPathDistance(current, closestPill);
		int distanceToPowerPill = newState.getShortestPathDistance(current, closestPowerPill);

		double pillReward = 10.0 / distanceToPill; // Positive reward for being close to a pill
		double powerPillReward = 20.0 / distanceToPowerPill; // Positive reward for being close to a power pill

		// Strategy 4: Negative reward for moving over an empty position
		if (selectedMove != MOVE.NEUTRAL && newState.getPacmanLastMoveMade() == MOVE.NEUTRAL) {
			emptyPositionReward = -1.0;
		}

		return ghostReward + edibleGhostReward + pillReward + powerPillReward + emptyPositionReward;
	}

	private MOVE getBestMove(int state, MOVE[] possibleMoves) {
		double maxQValue = Double.NEGATIVE_INFINITY;
		MOVE bestMove = MOVE.NEUTRAL;
		for (MOVE move : possibleMoves) {
			StateAction stateAction = new StateAction(state, move);
			double qValue = getQValue(stateAction);
			if (qValue > maxQValue) {
				maxQValue = qValue;
				bestMove = move;
			}
		}
		return bestMove;
	}

	private double getMaxQValue(int state, MOVE[] possibleMoves) {
		double maxQValue = Double.NEGATIVE_INFINITY;
		for (MOVE move : possibleMoves) {
			StateAction stateAction = new StateAction(state, move);
			double qValue = getQValue(stateAction);
			if (qValue > maxQValue) {
				maxQValue = qValue;
			}
		}
		return maxQValue;
	}

	private double getQValue(StateAction stateAction) {
		return qTable.getOrDefault(stateAction, 0.0);
	}

	private void setQValue(StateAction stateAction, double qValue) {
		qTable.put(stateAction, qValue);
	}

	public void setParameters(double alpha, double gamma, double epsilon) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
	}
}

