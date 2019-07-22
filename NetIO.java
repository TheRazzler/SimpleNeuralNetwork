import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
/**
 * This class allows for the saving and loading of neural networks to and from files.
 * @author Spencer Yoder
 */
public class NetIO {
  /**
   * Takes the given input layer and output layer and saves them to the file with name path
   * @param inputLayer the input layer of the network to save
   * @param outputLayer the output layer of the network to save
   * @param path the path name of the file the network will be saved into
   */
  public static void saveNetwork(NeuronLayer[] layers, String path) {
    try {
      DataOutputStream dos = new DataOutputStream(new FileOutputStream(path));
      dos.writeInt(layers.length);
      for(int k = 0; k < layers.length; k++) {
        double[][] inputWeights = layers[k].getWeights();
        dos.writeInt(inputWeights.length);
        dos.writeInt(inputWeights[0].length);
        for(int i = 0; i < inputWeights.length; i++) {
          for(int j = 0; j < inputWeights[i].length; j++) {
            dos.writeDouble(inputWeights[i][j]);
          }
        }
      }
      dos.close();
    } catch (IOException e) {
      throw new IllegalArgumentException("Error writing to file " + path);
    }
  }
  
  /**
   * Takes the given path name and returns the neural network located there
   * @param path the location of the file where the network was saved
   */
  public static NeuralNet loadNetwork(String path) {
    try {
      DataInputStream dis = new DataInputStream(new FileInputStream(path));
      double[][][] weights = new double[dis.readInt()][][];
      for(int k = 0; k < weights.length; k++) {
        int length = dis.readInt();
        int width = dis.readInt();
        double[][] inputWeights = new double[length][width];
        for(int i = 0; i < inputWeights.length; i++) {
          for(int j = 0; j < inputWeights[i].length; j++) {
            inputWeights[i][j] = dis.readDouble();
          }
        }
        weights[k] = inputWeights;
      }
      
      NeuronLayer[] layers = new NeuronLayer[weights.length];
      for(int i = 0; i < layers.length; i++) {
        layers[i] = new NeuronLayer(weights[i][0].length, weights[i].length);
        layers[i].setWeights(weights[i]);
      }
      
      dis.close();
      return new NeuralNet(layers);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error reading from file " + path);
    }
  }
}