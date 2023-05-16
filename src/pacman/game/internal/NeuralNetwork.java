package pacman.game.internal;
import java.util.Random;

public class NeuralNetwork {
    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    private double[][] weights1;
    private double[][] weights2;

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.weights1 = new double[inputSize][hiddenSize];
        this.weights2 = new double[hiddenSize][outputSize];
        initializeWeights();
    }

    private void initializeWeights() {
        Random random = new Random();

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weights1[i][j] = random.nextDouble() - 0.5;
            }
        }

        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights2[i][j] = random.nextDouble() - 0.5;
            }
        }
    }

    public double[] forwardPropagation(double[] input) {
        double[] hiddenLayer = new double[hiddenSize];
        double[] outputLayer = new double[outputSize];

        // Calculate values for the hidden layer
        for (int i = 0; i < hiddenSize; i++) {
            double sum = 0;
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weights1[j][i];
            }
            hiddenLayer[i] = sigmoid(sum);
        }

        // Calculate values for the output layer
        for (int i = 0; i < outputSize; i++) {
            double sum = 0;
            for (int j = 0; j < hiddenSize; j++) {
                sum += hiddenLayer[j] * weights2[j][i];
            }
            outputLayer[i] = sigmoid(sum);
        }
        return outputLayer;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}
