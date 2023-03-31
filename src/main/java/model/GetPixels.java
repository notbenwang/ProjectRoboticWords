package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;


public class GetPixels {
    private String FILE_OUTPUT = "src/main/stuff/output/output.txt";
    private String IMAGE_NAME = "src/main/stuff/input/badjoke3.jpg";
    public int[][] rgb_matrix;
    private int height,width;
    public GetPixels(){
        try {
            FileWriter writer = new FileWriter(FILE_OUTPUT);
            File file = new File(IMAGE_NAME);
            BufferedImage img = ImageIO.read(file);
//            int smallest = img.getHeight() < img.getWidth() ? img.getHeight() : img.getWidth();
//            int segment = smallest/100;
            int segment = 5;
            height = img.getHeight()/segment;
            width = img.getWidth()/segment;
            int[][] rgb_matrix = new int[height][width];
            for (int y = 0; y <height; y+=segment){
                for (int x = 0; x < width; x+=segment){

                    int y_coordinate = y*segment;
                    int x_coordinate = x*segment;
                    int pixel = img.getRGB(x_coordinate, y_coordinate);
                    Color color = new Color(pixel, true);
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();
                    int value = red*green*blue;

                    rgb_matrix[y][x]=value;

                    writer.append(red+":");
                    writer.append(green+":");
                    writer.append(blue+":");
                    writer.append(value+":");
                    writer.append("\n");
                    writer.flush();
                }
            }
            writer.close();
            System.out.println("FINISHED");
            this.rgb_matrix = rgb_matrix;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static ArrayList<ArrayList<Pair>> build_adjl(int[][] matrix){
        // Builds adjacency list from a 2d graph matrix
        int height = matrix.length;
        int width = matrix[0].length;
        System.out.println(height);
        System.out.println(width);
        int base_value = matrix[0][0];

        ArrayList<ArrayList<Pair>> adjl = new ArrayList<ArrayList<Pair>>();
        for (int i = 0; i < height*width; i++){
            adjl.add(new ArrayList<Pair>());
        }
        for (int y=0;y<matrix.length;y++){
            for (int x=0;x<matrix[y].length;x++){
                int node_number = y*width + x;
                int weight = matrix[y][x] - base_value;
                for (int y1=0;y1<matrix.length;y1++){
                    for (int x1=0; x1<matrix[y1].length;x1++){
                        int other_node = y1*width + x1;
                        // System.out.println("NextNode: "+other_node);
                        int other_weight = matrix[y1][x1];
                        int distance = weight-other_weight;
                        if (distance<0) distance *= -1;
                        Pair edge = new Pair(other_node, distance);
                        adjl.get(node_number).add(edge);
                    }
                }



            }
        }
        return adjl;
    }
    public void printAdjList(ArrayList<ArrayList<Pair>> graph){ // Helper function to visualize graphs
        for (int i=0;i<graph.size();i++){
            ArrayList<Pair> neighbors = graph.get(i);
            System.out.println("NODE "+i+"-----");
            for (int j=0;j<neighbors.size();j++){
                Pair edge = neighbors.get(j);
                System.out.println("EDGE "+edge.node+"| Weight: "+edge.weight);
            }
        }
    }
    public int[] cluster_cost(int k, ArrayList<ArrayList<Pair>> graph){
        int n = graph.size();
        if (n<=1) return null;
        int[] branch = new int[k-1];
        int[] tree_distances = new int[graph.size()]; // tree distances for return ease
        ArrayList<Pair> tree = new ArrayList<>(); // tree data structure

        // Bunch of initializing stuff
        boolean[] done = new boolean[graph.size()];
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for (int i = 0; i < n; i++){
            tree_distances[i] = Integer.MAX_VALUE;
            tree.add(new Pair(0,0));
        }

        // Set starting node to zeros
        tree_distances[0] = 0;
        pq.add(new Pair(0, 0));

        // MST Algorithm, altered Dijkstra's from PA2
        int count =0;
        while(pq.size()>0){
            count++;
            if (count%100000==0) System.out.println(count);
            Pair current = pq.poll(); // Extract the node with the minimum distance
            int current_node = current.node;
            done[current_node] = true;
            ArrayList<Pair> neighbors = graph.get(current_node);

            for (Pair item : neighbors){ // For each of its neighbors
                int next_node = item.node;
                int new_dist = item.weight;
                if (new_dist>6192) System.out.println(next_node +" | "+ new_dist);
                // if (new_dist!=0) System.out.println("NEW DIST:"+ new_dist+"| NEXT: "+next_node);
//                System.out.println("NextNode: "+next_node);
//                System.out.println("Weight "+new_dist);
                if (new_dist < tree_distances[next_node]){ // if distance is smaller

                    if (tree.get(current_node).node!=next_node) {

                        tree_distances[next_node] = new_dist;
                        tree.set(next_node, new Pair(current_node, new_dist));
                        pq.add(new Pair(next_node, new_dist));
                    }
                }
            }
        }
        /* Removing k-1 nodes will create k separate components. To maximize the connections between separate
         * components, remove the largest connections in the MST. To return the cluster cost, return the smallest
         * removed connection. This can be done by sorting the connections of the MST, then printing the k-1'th position
         * from the end.
         * */


        Arrays.sort(tree_distances);
        for (int i=0;i<k-1;i++){
            branch[i] = tree_distances[n-1-i];
        }
        // System.out.println(tree_distances[n-k+1]);
        printIntList(tree_distances);
        System.out.println(count);

        return branch;
    }
    public void printIntList(int[] list){
        for (int i=0; i<list.length;i++){
            System.out.println(list[i]);
        }
    }
    public static void main(String[] args){
        GetPixels model = new GetPixels();
        // ArrayList<Integer> colors = model.getColors();
        System.out.println("Entering Priority Queue: ");
        ArrayList<ArrayList<Pair>> graph = build_adjl(model.rgb_matrix);
        //model.printAdjList(graph);
        int[] branch = model.cluster_cost(5,graph);
        model.printIntList(branch);


    }
}
