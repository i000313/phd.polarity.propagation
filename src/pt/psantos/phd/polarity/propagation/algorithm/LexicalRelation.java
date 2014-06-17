package pt.psantos.phd.polarity.propagation.algorithm;

import org.jgrapht.graph.DefaultEdge;
import pt.psantos.phd.polarity.propagation.tests.DirectedPropagation;

/**
 *
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class LexicalRelation  extends DefaultEdge {

    public enum Type {

        SYNONYM, ANTONYM, UNKNOWN
    }

    private Word from;
    private Word to;
    private Type type;
    /**
     * Create a lexical relationship (a directed edge) between the word {@code from}
     * and the word {@code to}.
     * @param from origin edge.
     * @param to destiny edge.
     * @param type relationship between the {@code from} and {@code to} edges.
     */
    public LexicalRelation(Word from, Word to, Type type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public Word getNodeFrom() {
        return from;
    }

    public Word getNodeTo() {
        return to;
    }

    public Word getNodeOther(Word node) {
        if (from.equals(node)) {
            return to;
        } else if (to.equals(node)) {
            return from;
        } else {
            throw new RuntimeException("Inconsistent edge From: " 
                     + from + " To: " + to + " (node '" + node + "' not found)");
        }
    }
    
    
    public Word getNodeSelf(Word node) {
        
        if (from.equals(node)) {
            return from;
        } else if (to.equals(node)) {
            return to;
        } else {
            throw new RuntimeException("Self node not found. Edge From: " 
                     + from + " To: " + to + " (method argument '" + node + "')");
        }
    }
    
    public Type getRelationType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
