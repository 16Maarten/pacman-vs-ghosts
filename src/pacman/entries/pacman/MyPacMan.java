package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.StateAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyPacMan extends Controller<MOVE> {
	private static final double ALPHA = 0.1; // Learning rate
	private static final double GAMMA = 0.9; // Discount factor
	private static final double EPSILON = 0.1; // Exploration rate

	private Map<StateAction, Double> qTable;
	private Random random;

	public MyPacMan() {
		qTable = new HashMap<>();
		random = new Random();
	}

	public MOVE getMove(Game game, long timeDue) {
		int current = game.getPacmanCurrentNodeIndex();

		// Get the available actions (moves) from the current state
		MOVE[] possibleMoves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());

		// Explore or exploit
		MOVE selectedMove;
		if (random.nextDouble() < EPSILON) {
			// Exploration: Choose a random move
			selectedMove = possibleMoves[random.nextInt(possibleMoves.length)];
		} else {
			// Exploitation: Choose the move with the highest Q-value
			selectedMove = getBestMove(current, possibleMoves);
		}
		
		// Execute the selected move in the game
		Game copy = game.copy();
		copy.updatePacMan(selectedMove);

		// Get the new state after taking the selected move
		int newState = game.getPacmanCurrentNodeIndex();

		// Calculate the reward
		double reward = calculateReward(game, copy);

		// Update the Q-value for the previous state-action pair
		StateAction stateAction = new StateAction(current, selectedMove);
		double oldQValue = getQValue(stateAction);
		double maxQValue = getMaxQValue(newState, possibleMoves);
		double newQValue = oldQValue + ALPHA * (reward + GAMMA * maxQValue - oldQValue);
		setQValue(stateAction, newQValue);

		return selectedMove;
	}

	private double calculateReward(Game previousState, Game newState) {
		// Define the reward logic based on the game state changes
		// You can customize this logic based on your game's specific requirements
		// For example, you can assign positive rewards for eating pills or power pills,
		// negative rewards for getting caught by ghosts, etc.
		// Here's a simple reward logic based on the game score difference:
		int previousScore = previousState.getScore();
		int newScore = newState.getScore();
		return newScore - previousScore;
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
}
