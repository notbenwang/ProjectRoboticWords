package model;

public class Pair implements Comparable<Pair>{
    public int weight;
    public int node;
    public Pair(int n, int w) {
        weight = w;
        node = n;
    }

    public boolean equals(Pair other){
        return (this.weight == other.weight) && (this.node == other.node);
    }

    public int hashCode(){
        return this.node ^ (int)this.weight;
    }

    public int compareTo(Pair other){
        if (this.weight != other.weight){
            return (int)(this.weight - other.weight);
        }
        return this.node - other.node;
    }

    public String toString(){
        return "(" + this.node + "," + this.weight + ")";
    }
}
