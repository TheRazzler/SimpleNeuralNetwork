import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A neural network program which takes in numbers and decides whether they're even or odd
 * @author Spencer Yoder
 */
public class EvenOdd {
  
  /**
   * The main method, starts the program
   * @param args commmand line arguments
   */
  public static void main(String[] args) {
    //Create inputs. This is the training set consisting of 3 even numbers and 3 odd numbers.
    double[][] inputs = new double[][] {
      toBinary(77),
      toBinary(34),
      toBinary(132),
      toBinary(3),
      toBinary(255),
      toBinary(42)
    };
    
    //Create outputs. These are the correct answers, 0 for even, 1 for odd.
    double[][] outputs = new double[][] {
      {1},
      {0},
      {0},
      {1},
      {1},
      {0}
    };
    
    //This is the input layer. Each number will be turned into an 8-bit binary number
    //Since each number is 8 bits, each node takes 8 inputs. I arbitrarily chose 8 nodes.
    NeuronLayer inputLayer = new NeuronLayer(8, 8);
    
    //This is the output layer. It has 1 node (which outputs 1 for odd numbers, 0 for even).
    //It takes 8 inputs: 1 for every node in the previous layer.
    NeuronLayer outputLayer = new NeuronLayer(1, 8);
    
    //Create the neural network. All we need to do is tell it the input and output layer and it will
    //structure the rest.
    NeuralNet network = loadNetwork("evenOdd.ntwk");
    
    //Now that we have our training data and network, we can train the network.
    //This instructs it to step down the cost function 10000 times.
//    network.train(inputs, outputs, 10000);
    
    //Now we can test the network. Has it learned what even and odd numbers are?
    int count = 0;
    for(int i = 0; i <= 255; i++) {
      //Run i (in binary) through the network. 
      network.think(new double[][] {toBinary(i)});
      //network.getOutput()[0][0] gets the first value from the first output node.
      //(i.e. the only value in this case)
      if(network.getOutput()[0][0] < 0.5) {
        if(i % 2 == 0) {
          count++;
        }
      } else {
        if(i % 2 == 1) {
          count++;
        }
      }
    }
    System.out.println("The network got " + count + " numbers right out of 255.");
    saveNetwork(inputLayer, outputLayer, "evenOdd.ntwk");
  }
  
  /**
   * @param x any 8-bit integer (0-255)
   * @return an array of doubles matching the binary value of x (5 returns {0, 0, 0, 0, 0, 1, 0, 1})
   */
  public static double[] toBinary(int x) {
    double[] binary = new double[8];
    int idx = 7;
    while(x > 0) {
      binary[idx] = x % 2;
      idx--;
      x = x / 2;
    }
    return binary;
  }
  
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