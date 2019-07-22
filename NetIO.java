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
  public static void saveNetwork(NeuronLayer inputLayer, NeuronLayer outputLayer, String path) {
    try {
      DataOutputStream dos = new DataOutputStream(new FileOutputStream(path));
      double[][] inputWeights = inputLayer.getWeights();
      dos.writeInt(inputWeights.length);
      dos.writeInt(inputWeights[0].length);
      for(int i = 0; i < inputWeights.length; i++) {
        for(int j = 0; j < inputWeights[i].length; j++) {
          dos.writeDouble(inputWeights[i][j]);
        }
      }
      
      double[][] outputWeights = outputLayer.getWeights();
      dos.writeInt(outputWeights.length);
      dos.writeInt(outputWeights[0].length);
      for(int i = 0; i < outputWeights.length; i++) {
        for(int j = 0; j < outputWeights[i].length; j++) {
          dos.writeDouble(outputWeights[i][j]);
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
      double[][] inputWeights = new double[dis.readInt()][dis.readInt()];
      for(int i = 0; i < inputWeights.length; i++) {
        for(int j = 0; j < inputWeights.length; j++) {
          inputWeights[i][j] = dis.readDouble();
        }
      }
      
      double[][] outputWeights = new double[dis.readInt()][dis.readInt()];
      for(int i = 0; i < outputWeights.length; i++) {
        for(int j = 0; j < outputWeights[i].length; j++) {
          outputWeights[i][j] = dis.readDouble();
        }
      }
      
      NeuronLayer inputLayer = new NeuronLayer(inputWeights[0].length, inputWeights.length);
      NeuronLayer outputLayer = new NeuronLayer(outputWeights[0].length, outputWeights.length);
      
      inputLayer.setWeights(inputWeights);
      outputLayer.setWeights(outputWeights);
      
      dis.close();
      return new NeuralNet(inputLayer, outputLayer);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error reading from file " + path);
    }
  }
}