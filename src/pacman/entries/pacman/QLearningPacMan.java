package pacman.entries.pacman;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.*;
import pacman.game.Game;
import pacman.game.StateAction;

public class QLearningPacMan extends Controller<MOVE> {
	// Benchmark 0 0 0 with an average of
	// Set the learning rate (alpha) to control the weight given to new information
	// compared to existing Q-values.
	private double alpha = 0.0; // No learning, no overwrite knowledge [0.0] <----------> [1.0] Full learning, full overwrite
	// Set the discount factor (gamma) to balance immediate and future rewards. No
	private double gamma = 0.0; // Immediate rewards [0.0] <----------> [1.0] Future rewards equally important
	// Set the exploration rate (epsilon) to balance exploration and exploitation of
	// the learned policy.
	private double epsilon = 0.0; // No exploration, always exploit [0.0] <----------> [1.0] Full exploration, never exploit
	// if a ghost is this close, run away
	private int minDistanceGhost = 4;

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

	public QLearningPacMan(double alpha, double gamma, double epsilon , int minDistanceGhost) {
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
		boolean ranFromGhost = false;

		// If any non-edible ghost is too close (less than minDistanceGhost), run away
		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0)
				if (game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost)) < minDistanceGhost) {
					selectedMove = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost), DM.PATH);
					ranFromGhost = true;
				}
		}

		// Explore or exploit, when not running from a ghost
		if (!ranFromGhost) {
			if (random.nextDouble() < epsilon) {
				// Exploration: Choose a random move
				selectedMove = possibleMoves[random.nextInt(possibleMoves.length)];
			} else {
				// Exploitation: Choose the move with the highest Q-value
				selectedMove = getBestMove(current, possibleMoves);
			}
		}

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

	public void setParameters(double alpha, double gamma, double epsilon) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.epsilon = epsilon;
	}

	public void setMinDistanceGhost(int minDistanceGhost) {
		this.minDistanceGhost = minDistanceGhost;
	}
}
/*
 * In this implementation, the Q-learning algorithm is integrated into the
 * `getMove()` method of the `QLearningPacMan` class. The Q-values are stored in
 * a `HashMap` called
 * `qTable`, and the class uses the `StateAction` class as a key to index the
 * Q-values in the table.
 * Please note that the code provided is a starting point and may need further
 * customization and tuning to fit your specific game environment and reward
 * logic.
 */

// To implement a reinforcement learning algorithm for controlling Pac-Man based
// on the given Java class, we'll use the Q-learning algorithm. Q-learning is a
// model-free,
// off-policy reinforcement learning algorithm that aims to learn an optimal
// policy for an agent in a Markov Decision Process (MDP).

// Here's a step-by-step guide on how to adapt the given class to work with
// Q-learning:

// 1. Define the state space:
// - Identify the relevant information from the game state that can be used to
// characterize the current state. For example, the positions of Pac-Man, the
// ghosts,
// the pills, and power pills could be important.
// - Determine how to represent this state information in a suitable format for
// the Q-learning algorithm, such as a feature vector or a hashable
// representation.

// 2. Define the action space:
// - Identify the possible actions Pac-Man can take in the game, based on the
// available moves defined in the class (`MOVE` enum).

// 3. Initialize the Q-table:
// - Create a lookup table (or data structure) to store the Q-values for each
// state-action pair.
// - Initialize the Q-values randomly or to some initial values.

// 4. Define the learning parameters:
// - Set the learning rate (alpha) to control the weight given to new
// information compared to existing Q-values.
// - Set the discount factor (gamma) to balance immediate and future rewards.
// - Set the exploration rate (epsilon) to balance exploration and exploitation
// of the learned policy.

// 5. Update the `getMove` method:
// - Replace the existing logic with the Q-learning algorithm.
// - At each time step, observe the current state (based on the game state).
// - Choose an action using an epsilon-greedy strategy:
// - With probability (1 - epsilon), select the action with the highest Q-value
// for the current state.
// - With probability epsilon, select a random action to encourage exploration.
// - Execute the chosen action in the game and observe the new state and the
// resulting reward.
// - Update the Q-value for the previous state-action pair using the Q-learning
// update rule:
// ```
// Q(s, a) = (1 - alpha) * Q(s, a) + alpha * (r + gamma * max Q(s', a'))
// ```
// where:
// - Q(s, a) is the Q-value for state s and action a.
// - alpha is the learning rate.
// - r is the observed reward after taking action a in state s.
// - gamma is the discount factor.
// - s' is the new state after taking action a.
// - max Q(s', a') is the maximum Q-value for the new state s' over all possible
// actions a'.

// 6. Repeat steps 5 and 6 until the learning process is complete (e.g., a
// certain number of iterations or convergence criteria are met).

// 7. Use the learned Q-values to control Pac-Man:
// - During gameplay, use the learned Q-values to select the best action based
// on the current state.
// - This can be done by choosing the action with the highest Q-value for the
// current state.

// Please note that implementing a complete reinforcement learning algorithm
// goes beyond the scope of a simple response. It requires careful design,
// training,
// and integration with the game environment. The steps provided above serve as
// a general guideline to adapt the given class to work with reinforcement
// learning.