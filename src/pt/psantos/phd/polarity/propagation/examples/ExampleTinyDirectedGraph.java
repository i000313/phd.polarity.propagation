package pt.psantos.phd.polarity.propagation.examples;

import java.util.ArrayList;
import java.util.List;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityPropagation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityStats;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityUtils;
import pt.psantos.phd.polarity.propagation.algorithm.Word;

/**
 * This example shows how to propagateNew the positive and negative polarity of
 words, from two seed words to the reaming words, over a directed graph.
 <br/>In this tiny graph each node represents a word (in fact each word is 
 * represented by a characher from A to I), and each edge represents either a 
 * synonym or an antonym between two words.
 *
 * <br/>The Graph represented as an adjacency list:
 * <pre>
 * {@code
 * A: C D
 * B: D E
 * C: F
 * D: F
 * E: G H
 * F: I G
 * G:
 * H: E
 * I:
 * }
 * </pre>
 *
 * In this graph, A and B are seed words. A is positive word and B is negative
 * word. A visual representation of this graph can be seen on figure 3, of the 
 * following paper.
 *
 * <p>REFERENCES:
 * <br/>This example was taken from Figure 3, section 2.3 of the paper:
 * </p>
 * <p>
 * {@code
 * Santos, A. P., Ramos, C., & Marques, N. C. (2011). Determining the Polarity of
 * Words through a Common Online Dictionary. In Proceedings of 15th Portuguese
 * Conference on Artificial on Artificial intelligence (EPIA 2011), volume 7026
 * of LNCS, pp. 649-663. Lisbon, Portugal. Springer.
 * }
 * </p>
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class ExampleTinyDirectedGraph {

    /**
     * Words that will make part of graph. They will be graph nodes.
     */
    private List<Word> words;
    private DirectedPseudograph<Word, LexicalRelation> directedPseudoGraph;
    
    public DirectedPseudograph<Word, LexicalRelation> createDirectedGraph() {
        if (this.directedPseudoGraph == null) {
            createGraph();
        }
        return this.directedPseudoGraph;
    }
    
    /**
     * Returns the dictionary words.
     *
     * @return a list of the dictionary words.
     */
    private List<Word> getWords() {
        return words;
    }

    /**
     * Returns a list of seed words. This is an initial list of words classified
     * as positive, negative and eventually with the neutral polarity.
     *
     * @return a list of seed words
     */
    public List<Word> getSeedWords() {
        List<Word> seedWords = new ArrayList<Word>();
        for (Word w : words) {
            if (w.isSeed()) {
                seedWords.add(w);
            }
        }
        return (seedWords.size() > 0 ? seedWords : null);
    }

    
    private void createGraph() {

        // Words that will be used as graph nodes
        Word A = new Word("A");
        Word B = new Word("B");
        Word C = new Word("C");
        Word D = new Word("D");
        Word E = new Word("E");
        Word F = new Word("F");
        Word G = new Word("G");
        Word H = new Word("H");
        Word I = new Word("I");

        words = new ArrayList<Word>();
        words.add(A);
        words.add(B);
        words.add(C);
        words.add(D);
        words.add(E);
        words.add(F);
        words.add(G);
        words.add(H);
        words.add(I);

        // Set "A" and "B" as seed words.
        A.setAsPositiveSeed();
        B.setAsNegativeSeed();

            // I changed from DirectedMultigraph to DirectedPseudograph. This was
            // required because the former does not support loops. This means does not
            // support a vertex from a edge to itself. 
            this.directedPseudoGraph
                    = new DirectedPseudograph<Word, LexicalRelation>(
                            new ClassBasedEdgeFactory<Word, LexicalRelation>(LexicalRelation.class));

            for (Word w : words) {
                directedPseudoGraph.addVertex(w);
            }

            directedPseudoGraph.addEdge(A, C, new LexicalRelation(A, C, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(A, D, new LexicalRelation(A, D, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(B, D, new LexicalRelation(B, D, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(B, E, new LexicalRelation(B, E, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(C, F, new LexicalRelation(C, F, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(D, F, new LexicalRelation(D, F, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(E, G, new LexicalRelation(E, G, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(E, H, new LexicalRelation(E, H, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(F, I, new LexicalRelation(F, I, LexicalRelation.Type.ANTONYM));
            directedPseudoGraph.addEdge(F, G, new LexicalRelation(F, G, LexicalRelation.Type.SYNONYM));
            directedPseudoGraph.addEdge(H, E, new LexicalRelation(H, E, LexicalRelation.Type.SYNONYM));
         
    }

    
    public static void main(String args[]) {
        
        // Loads the example data
        ExampleTinyDirectedGraph exampleData = new ExampleTinyDirectedGraph();
        DirectedPseudograph<Word, LexicalRelation> initialGraph = exampleData.createDirectedGraph();
        List<Word> seedWords = exampleData.getSeedWords();
        
        // Applies the propagation algorithm over the directed graph
        DirectedPseudograph<Word, LexicalRelation> finalGraph 
                = PolarityPropagation.propagate(initialGraph, seedWords);
        
        // Print the list of "words" to the screen
        PolarityUtils.printGraphNodes(finalGraph);
        
       // Compute and show a few statistics about the final graph
        PolarityStats stats = new PolarityStats(finalGraph);
        System.out.println(stats);        
    }
}
