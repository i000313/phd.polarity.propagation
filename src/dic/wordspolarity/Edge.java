package dic.wordspolarity;

/**
 *
 * @author PSantos
 */
public class Edge implements Comparable<Edge> {

    private final int v; // one vertex
    private final int w; // the other vertex
    private final double weight; // edge weight
    
    // my
    private final int relId;
    private final int type; // edge type
    private boolean visited; //

    public Edge(int v, int w, double weight) {
        this.v = v;
        this.w = w;
        this.weight = weight;
        
        this.relId = -1;
        this.type = 0;
        this.visited = false;
    }

    // My
    public Edge(int v, int w, int relId) {
        this.v = v;
        this.w = w;
        this.weight = 0;
        
        this.relId = relId;
        this.type = 0;
        this.visited = false;
    }
    
    // My
    public Edge(int v, int w, int relId, int type) {
        this.v = v;
        this.w = w;
        this.weight = 0;
        
        this.relId = relId;
        this.type = type;
        this.visited = false;
    }
    
    // My
    public Edge(int v, int w, double weight, int type) {
        this.v = v;
        this.w = w;
        this.weight = weight;
        
        this.relId = -1;
        this.type = type;
        this.visited = false;
    }

    public double weight() {
        return weight;
    }

    public int type() {
        return type;
    }

    public int either() {
        return v;
    }

    //my
    public int dest() {
        return w;
    }
    
    //my
    public boolean isVisited() {
        return visited;
    }
    
    public void setAsVisited() {
        this.visited = true;
    }
    
    public int other(int vertex) {
        if (vertex == v) {
            return w;
        } else if (vertex == w) {
            return v;
        } else {
            throw new RuntimeException("Inconsistent edge");
        }
    }

    // TODO: alterar implementação 
    public int compareTo(Edge that) {
        if (this.weight() < that.weight()) {
            return -1;
        } else if (this.weight() > that.weight()) {
            return +1;
        } else {
            return 0;
        }
    }

    public String toString() {
        //return String.format("%d-%d %.2f %d", v, w, weight, type);
        return String.format("%d-%d r%d", v, w, type);
    }
}
