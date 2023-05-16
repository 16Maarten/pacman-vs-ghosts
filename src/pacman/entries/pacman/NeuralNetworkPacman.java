package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.NeuralNetwork;

import java.util.ArrayList;
import java.util.Random;

public class NeuralNetworkPacman extends Controller<MOVE> {
    private MOVE myMove = MOVE.NEUTRAL;
    private NeuralNetwork neuralNetwork;

    public NeuralNetworkPacman() {
        // Initialize the neural network
        int inputSize = 4; // Change this according to your input features
        int hiddenSize = 10; // Change this according to the desired size of the hidden layer
        int outputSize = 4; // Change this according to the number of possible moves
        neuralNetwork = new NeuralNetwork(inputSize, hiddenSize, outputSize);
    }

    public MOVE getMove(Game game, long timeDue) {
        // Get input features from the game state
        double[] input = getInputFeatures(game);

        // Perform forward propagation using the neural network
        double[] output = neuralNetwork.forwardPropagation(input);

        // Determine the best move based on the output of the neural network
        int bestMoveIndex = getMaxIndex(output);
        myMove = MOVE.values()[bestMoveIndex];

        return myMove;
    }

    private double[] getInputFeatures(Game game) {
        // Implement your logic to extract relevant features from the game state
        // and represent them as an array of doubles.
        // Example: Distance to nearest ghost, number of remaining pills, etc.
        // The size of the input array should match the inputSize of the neural network.

        // Placeholder code, replace with your implementation
        double nearestGhost = 0.0;
        int nearestEdibleGhost = 0;
        int locationPacman = game.getPacmanCurrentNodeIndex();
        var lastMove = game.getPacmanLastMoveMade();
        for(Constants.GHOST ghost : Constants.GHOST.values()){
            if(game.getGhostCurrentNodeIndex(ghost) > 0) {
                if (game.getShortestPathDistance(locationPacman,game.getGhostCurrentNodeIndex(ghost)) > nearestGhost) {
                    nearestGhost = game.getShortestPathDistance(locationPacman,game.getGhostCurrentNodeIndex(ghost));
                }
                if (game.isGhostEdible(ghost)) {
                    nearestEdibleGhost = (int) nearestGhost;
                }
            }
        }

        int[] pills=game.getPillIndices();
        int[] powerPills=game.getPowerPillIndices();

        ArrayList<Integer> targets=new ArrayList<Integer>();

        for(int i=0;i<pills.length;i++)					//check which pills are available
            if(game.isPillStillAvailable(i))
                targets.add(pills[i]);

        for(int i=0;i<powerPills.length;i++)			//check with power pills are available
            if(game.isPowerPillStillAvailable(i))
                targets.add(powerPills[i]);

        int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array

        for(int i=0;i<targetsArray.length;i++)
            targetsArray[i]=targets.get(i);

        double[] input = new double[4];
        input[0] = (double)nearestGhost;
        input[1] = (double)game.getClosestNodeIndexFromNodeIndex(locationPacman,game.getActivePowerPillsIndices(), Constants.DM.PATH);
        input[2] = (double) game.getManhattanDistance(locationPacman,nearestEdibleGhost);
        input[3] = (double)game.getClosestNodeIndexFromNodeIndex(locationPacman,targetsArray, Constants.DM.PATH);

        return input;
    }

    private int getMaxIndex(double[] array) {
        int maxIndex = 0;
        double maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxIndex = i;
                maxValue = array[i];
            }
        }
        return maxIndex;
    }
}
