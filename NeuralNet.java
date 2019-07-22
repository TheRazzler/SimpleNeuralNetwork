import java.util.function.Function;

/**
 * https://medium.com/technology-invention-and-more/how-to-build-a-multi-layered-neural-network-in-python-53ec3d1d326a#.9kcfharq6
 * http://stevenmiller888.github.io/mind-how-to-build-a-neural-network-part-2/
 */
public class NeuralNet {
    private final NeuronLayer[] layers;
    private double[][][] outputLayers;
    private final double learningRate;

    public NeuralNet(NeuronLayer[] layers) {
        this(layers, 0.1);
    }

    public NeuralNet(NeuronLayer[] layers, double learningRate) {
        this.layers = layers;
        this.learningRate = learningRate;
        outputLayers = new double[layers.length][][];
    }

    /**
     * Forward propagation
     * <p>
     * Output of neuron = 1 / (1 + e^(-(sum(weight, input)))
     *
     * @param inputs
     */
    public void think(double[][] inputs) {
      outputLayers[0] = MatrixUtil.apply(NNMath.matrixMultiply(inputs, layers[0].weights), layers[0].activationFunction);
      for(int i = 1; i < outputLayers.length; i++) {
        outputLayers[i] = MatrixUtil.apply(NNMath.matrixMultiply(outputLayers[i - 1], layers[i].weights), layers[i].activationFunction); // 4x4
      }
    }

    public void train(double[][] inputs, double[][] outputs, int numberOfTrainingIterations) {
        int len = layers.length;
        double[][][] deltaLayers = new double[len][][]; 
        double[][][] errorLayers = new double[len][][]; 
        double[][][] adjustmentLayers = new double[len][][];
        for (int k = 0; k < numberOfTrainingIterations; ++k) {
            // pass the training set through the network
            think(inputs); // 4x3

            // adjust weights by error * input * output * (1 - output)

            // calculate the error for layer 2
            // (the difference between the desired output and predicted output for each of the training inputs)
            errorLayers[len - 1] = NNMath.matrixSubtract(outputs, outputLayers[len - 1]); // 4x1
            deltaLayers[len - 1] = NNMath.scalarMultiply(errorLayers[len - 1], MatrixUtil.apply(outputLayers[len - 1], layers[len - 1].activationFunctionDerivative)); // 4x1

            // calculate the error for layer 1
            // (by looking at the weights in layer 1, we can determine by how much layer 1 contributed to the error in layer 2)
            for(int i = len - 2; i >= 0; i--) {
              errorLayers[i] = NNMath.matrixMultiply(deltaLayers[i + 1], NNMath.matrixTranspose(layers[i + 1].weights)); // 4x4
              deltaLayers[i] = NNMath.scalarMultiply(errorLayers[i], MatrixUtil.apply(outputLayers[i], layers[i].activationFunctionDerivative)); // 4x4
            }

            // Calculate how much to adjust the weights by
            // Since we’re dealing with matrices, we handle the division by multiplying the delta output sum with the inputs' transpose!
            
            adjustmentLayers[0] = NNMath.matrixMultiply(NNMath.matrixTranspose(inputs), deltaLayers[0]); // 4x4
            for(int i = 1; i < len; i++) {
              adjustmentLayers[i] = NNMath.matrixMultiply(NNMath.matrixTranspose(outputLayers[i - 1]), deltaLayers[i]); // 4x1
            }

            for(int i = 0; i < len; i++) {
              adjustmentLayers[i] = MatrixUtil.apply(adjustmentLayers[i], (x) -> learningRate * x);
            }

            // adjust the weights
            for(int i = 0; i < len; i++) {
              this.layers[i].adjustWeights(adjustmentLayers[i]);
            }

            // if you only had one layer
            // synaptic_weights += dot(training_set_inputs.T, (training_set_outputs - output) * output * (1 - output))
            // double[][] errorLayer1 = NNMath.matrixSubtract(outputs, outputLayer1);
            // double[][] deltaLayer1 = NNMath.matrixMultiply(errorLayer1, MatrixUtil.apply(outputLayer1, NNMath::sigmoidDerivative));
            // double[][] adjustmentLayer1 = NNMath.matrixMultiply(NNMath.matrixTranspose(inputs), deltaLayer1);

            if(k % (numberOfTrainingIterations / 10) == 0){
                System.out.println(" Training iteration " + k + " of " + numberOfTrainingIterations);
            }
            //System.out.println(this);

        }
    }

    public double[][] getOutput() {
        return outputLayers[outputLayers.length - 1];
    }
    
    public NeuronLayer[] getLayers() {
      return layers;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < layers.length; i++) {
          result.append("Layer " + (i + 1) + "\n");
          result.append(layers[i].toString());
        }
        
        for(int i = 0; i < outputLayers.length; i++) {
          if (outputLayers[i] != null) {
              result.append("Layer " + (i + 1) + " output\n");
              result.append(MatrixUtil.matrixToString(outputLayers[i]));
          }
        }

        return result.toString();
    }
}
