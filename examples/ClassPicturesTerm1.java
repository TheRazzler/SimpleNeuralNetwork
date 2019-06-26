import java.io.File;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class ClassPicturesTerm1 {
  public static void main(String[] args) {
    File pictures = new File("pictures");
    File[] classMembers = pictures.listFiles();
    /*int totalPictures = 0;
    for(int i = 0; i < classMembers.length; i++) {
      totalPictures += classMembers[i].listFiles().length;
    }
    
    double[][] inputs = new double[totalPictures][16384];
    double[][] outputs = new double[totalPictures][classMembers.length];
    int idx = 0;
    for(int i = 0; i < classMembers.length; i++) {
      File[] faces = classMembers[i].listFiles();
      for(int j = 0; j < faces.length; j++) {
        inputs[idx] = picToWeights(faces[j].getAbsolutePath());
        outputs[idx++][i] = 1;
      }
    }
    
    NeuronLayer[] layers = loadNetwork("faceRecog1.ntwk");
    NeuralNet network = new NeuralNet(layers[0], layers[1]);
    network.train(inputs, outputs, 5000);
    saveNetwork(layers[0], layers[1], "faceRecog1.ntwk");*/
    
    NeuronLayer[] layers = loadNetwork("faceRecog1.ntwk");
    NeuralNet network = new NeuralNet(layers[0], layers[1]);
    for(int i = 0; i < classMembers.length; i++) {
      File[] faces = classMembers[i].listFiles();
      for(int j = 0; j < faces.length; j++) {
        resultsOfPicture(faces[j].getPath(), network, classMembers);
      }
    }
    
  }
  
  public static void resultsOfPicture(String imgPath, NeuralNet network, File[] classMembers) {
    double[][] testPic = new double[1][16384];
    testPic[0] = picToWeights(imgPath);
    network.think(testPic);
    double[] outputs = network.getOutput()[0];
    double maxVal = -1;
    int maxIdx = -1;
    
    for(int i = 0; i < outputs.length; i++) {
      if(outputs[i] > maxVal) {
        maxVal = outputs[i];
        maxIdx = i;
      }
    }
    
    System.out.println(imgPath + " looks the most like " + classMembers[maxIdx].getName());
  }
  
  public static double[] picToWeights(String path) {
    BufferedImage image = null;
    try {
      image = ImageIO.read(new File(path));
      
    } catch (IOException e) {
      throw new IllegalArgumentException("Problem reading file " + path);
    }
    double[] weights = new double[16384];
    int idx = 0;
    for(int i = 0; i < image.getWidth(); i++) {
      for(int j = 0; j < image.getHeight(); j++) {
        Color c = new Color(image.getRGB(i, j));
        weights[idx++] = c.getRed() * 0.21 + c.getGreen() * 0.72 + c.getBlue() * 0.07;
      }
    }
    return weights;
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
  
  public static NeuronLayer[] loadNetwork(String path) {
    try {
      DataInputStream dis = new DataInputStream(new FileInputStream(path));
      double[][] inputWeights = new double[dis.readInt()][dis.readInt()];
      for(int i = 0; i < inputWeights.length; i++) {
        for(int j = 0; j < inputWeights[i].length; j++) {
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
      return new NeuronLayer[] {inputLayer, outputLayer};
    } catch (IOException e) {
      throw new IllegalArgumentException("Error reading from file " + path);
    }
  }
}