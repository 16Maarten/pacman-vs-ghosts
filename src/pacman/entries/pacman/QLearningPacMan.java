package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.*;
import pacman.game.Game;
import pacman.game.StateAction;

public class QLearningPacMan extends Controller<MOVE> {
	// Set the learning rate (alpha) to control the weight given to new information compared to existing Q-values.
	private double alpha = 0.0; // No learning, no overwrite knowledge [0.0] <----------> [1.0] Full learning, full overwrite

	// Set the discount factor (gamma) to balance immediate and future rewards. No
	private double gamma = 0.0; // Immediate rewards [0.0] <----------> [1.0] Future rewards equally important

	// Set the exploration rate (epsilon) to balance exploration and exploitation of the learned policy.
	private double epsilon = 0.0; // No exploration, always exploit [0.0] <----------> [1.0] Full exploration, never exploit

	// if a ghost is this close, run away
	private int minDistanceGhost = 20;

	private Map<StateAction, Double> qTable;
	private Random random;

	public QLearningPacMan() {
		qTable = new HashMap<>();
		random = new Random();
	}

	public QLearningPacMan(double alpha, double gamma, double epsilon) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
		qTable = new HashMap<>();
		random = new Random();
	}

	public QLearningPacMan(int minDistanceGhost) {
		this.minDistanceGhost = minDistanceGhost;
		qTable = new HashMap<>();
		random = new Random();
	}

	public QLearningPacMan(double alpha, double gamma, double epsilon, int minDistanceGhost) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
		this.minDistanceGhost = minDistanceGhost;
		qTable = new HashMap<>();
		random = new Random();
	}

	public MOVE getMove(Game game, long timeDue) {
		int current = game.getPacmanCurrentNodeIndex();

		// Get the available actions (moves) from the current state
		MOVE[] possibleMoves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());

		MOVE selectedMove = MOVE.NEUTRAL;
		// boolean ranFromGhost = false;

		// Explore or exploit, when not running from a ghost
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

		// Get the new state after taking the selected move
		int newState = copy.getPacmanCurrentNodeIndex();

		// Calculate the reward
		double reward = calculateReward(game, copy);

		// Update the Q-value for the previous state-action pair
		StateAction stateAction = new StateAction(current, selectedMove);
		double oldQValue = getQValue(stateAction);
		double maxQValue = getMaxQValue(newState, possibleMoves);
		// Q(s, a) = (1 - alpha) * Q(s, a) + alpha * (r + gamma * max Q(s', a'))
		double newQValue = oldQValue + alpha * (reward + gamma * maxQValue - oldQValue);
		setQValue(stateAction, newQValue);

		// double highestReward = Double.MIN_VALUE;
		// MOVE bestMove = MOVE.NEUTRAL;

		// for (MOVE move : possibleMoves) {
		// 	Game copy2 = game.copy();
		// 	copy2.updatePacMan(move);

		// 	double newReward = calculateReward(game, copy2);
		// 	if(newReward > highestReward) {
		// 		highestReward = newReward;
		// 		bestMove = move;
		// 	}
		// }
		// return bestMove;

		return selectedMove;
	}

	private double calculateReward(Game previousState, Game newState) {
		// Get the current positions of Pacman in the previous state and new state
		int previousPosition = previousState.getPacmanCurrentNodeIndex();
		int newPosition = newState.getPacmanCurrentNodeIndex();

		// ---------------------REWARD--------------------- Run away from ghost reward
		// Check if Pacman moved away from a non-edible ghost
		double ghostReward = 0.0;
		for (GHOST ghost : GHOST.values()) {
			if (previousState.getGhostEdibleTime(ghost) == 0 &&
					previousState.getGhostLairTime(ghost) == 0 && newState.getGhostEdibleTime(ghost) == 0 &&
					newState.getGhostLairTime(ghost) == 0) {
				if (previousState.getShortestPathDistance(previousPosition,
						previousState.getGhostCurrentNodeIndex(ghost)) < minDistanceGhost) {
					int previousDistance = previousState.getShortestPathDistance(previousPosition,
							previousState.getGhostCurrentNodeIndex(ghost));
					int newDistance = newState.getShortestPathDistance(newPosition,
							newState.getGhostCurrentNodeIndex(ghost));
							
					if (newDistance > previousDistance) {
						ghostReward = 200.0;
						System.out.println("Run away from ghost reward: " + ghostReward);
						break;
					} else {
						ghostReward = -200.0;
						System.out.println("Did not run away from ghost reward: " + ghostReward);
						break;
					}
				}
			}
		}

		// ---------------------REWARD--------------------- Move towards closest edible
		// ghost
		// Check if Pacman moved towards closest edible ghost
		double movedToGhostReward = 0.0;
		boolean edibleGhosts = false;
		int minDistance = Integer.MAX_VALUE;
		int newDistanceMinGhost = 0;

		for (GHOST ghost : GHOST.values()) {
			if (previousState.getGhostEdibleTime(ghost) > 0 && newState.getGhostEdibleTime(ghost) > 0) {
				int previousDistance = previousState.getShortestPathDistance(previousPosition,
						previousState.getGhostCurrentNodeIndex(ghost));

				if (previousDistance < minDistance) {
					minDistance = previousDistance;
					newDistanceMinGhost = newState.getShortestPathDistance(newPosition,
							newState.getGhostCurrentNodeIndex(ghost));
					edibleGhosts = true;
				}
			}
		}

		if (edibleGhosts) {
			if (newDistanceMinGhost < minDistance) {
				movedToGhostReward = 10.0;
			} else {
				movedToGhostReward = -10.0;
			}
		}

		// ---------------------REWARD--------------------- Move towards closest pill or
		// powerpill
		// Check if Pacman moved towards a pill or power pill
		int[] pills = previousState.getPillIndices();
		int[] powerPills = previousState.getPowerPillIndices();
		ArrayList<Integer> targets = new ArrayList<>();

		for (int i = 0; i < pills.length; i++) {
			if (previousState.isPillStillAvailable(i)) {
				targets.add(pills[i]);
			}
		}

		for (int i = 0; i < powerPills.length; i++) {
			if (previousState.isPowerPillStillAvailable(i)) {
				targets.add(powerPills[i]);
			}
		}

		int[] targetsArray = new int[targets.size()];

		for (int i = 0; i < targetsArray.length; i++) {
			targetsArray[i] = targets.get(i);
		}

		int closestTarget = previousState.getClosestNodeIndexFromNodeIndex(previousPosition,
				targetsArray, DM.PATH);
		int previousDistanceToTarget = previousState.getShortestPathDistance(previousPosition, closestTarget);
		int newDistanceToTarget = newState.getShortestPathDistance(newPosition,
				closestTarget);

		double movedToClosestPillReward = newDistanceToTarget < previousDistanceToTarget ? 5.0 : -5.0;

		// ---------------------REWARD--------------------- Pill eaten reward
		// Get the pill indices of the previous state and new state
		int[] previousPills = previousState.getPillIndices();
		int[] newPills = newState.getPillIndices();

		// Count the number of pills eaten
		int pillsEaten = previousPills.length - newPills.length;

		// Assign positive reward for each pill eaten
		double pillEatenReward = pillsEaten * 7.5;

		// ---------------------REWARD--------------------- Power pill eaten when ghost
		// close reward
		// Get the power pill indices of the previous state and new state
		int[] previousPowerPills = previousState.getPowerPillIndices();
		int[] newPowerPills = newState.getPowerPillIndices();

		// Count the number of power pills eaten
		boolean powerPillEaten = previousPowerPills.length - newPowerPills.length == 1;

		// Check if any non-edible ghost is close (less than 10 nodes away)
		int closeGhostsWhenPowerPill = 0;
		if (powerPillEaten) {
			for (GHOST ghost : GHOST.values()) {
				if (newState.getGhostEdibleTime(ghost) > 0)
					if (newState.getShortestPathDistance(newState.getPacmanCurrentNodeIndex(),
							newState.getGhostCurrentNodeIndex(ghost)) < 10)
						closeGhostsWhenPowerPill++;
			}
		}

		// Assign positive reward for each power pill eaten
		double powerPillCloseGhostReward = closeGhostsWhenPowerPill == 0 ? 0.0 : closeGhostsWhenPowerPill * 5.0;

		// ---------------------REWARD--------------------- Empty position reward
		// Check if Pacman moved to an empty position
		boolean movedToEmptyPosition = (newState.getPillIndex(newPosition) == -1 &&
				newState.getPowerPillIndex(newPosition) == -1);

		// Assign a negative reward for moving to an empty position
		double emptyPositionReward = movedToEmptyPosition ? -1.0 : 1.0;

		// ---------------------REWARD--------------------- Score difference reward
		// Calculate the score difference reward
		int previousScore = previousState.getScore();
		int newScore = newState.getScore();
		double scoreReward = newScore - previousScore;

		// Calculate the overall reward as the sum of individual rewards
		// double reward = ghostReward;
		// double reward = movedToGhostReward;
		// double reward = ghostReward + movedToGhostReward;
		double reward = ghostReward + movedToGhostReward + movedToClosestPillReward + pillEatenReward
				+ powerPillCloseGhostReward
				+ emptyPositionReward + scoreReward;

		return reward;
	}

	private MOVE getBestMove(int state, MOVE[] possibleMoves) {
		// Find the move with the highest Q-value for the given state
		double maxQValue = Double.NEGATIVE_INFINITY;
		MOVE bestMove = null;
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
		// Find the maximum Q-value for the given state among all possible moves
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
		// Retrieve the Q-value from the Q-table
		return qTable.getOrDefault(stateAction, 0.0);
	}

	private void setQValue(StateAction stateAction, double qValue) {
		// Update the Q-value in the Q-table
		qTable.put(stateAction, qValue);
	}

	public void setParameters(double alpha, double gamma, double epsilon) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
	}

	public void setMinDistanceGhost(int minDistanceGhost) {
		this.minDistanceGhost = minDistanceGhost;
	}
}