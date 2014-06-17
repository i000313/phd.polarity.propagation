
package pt.psantos.phd.polarity.propagation.examples;

import java.util.ArrayList;
import java.util.List;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.SimpleGraph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityPropagation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityStats;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityUtils;
import pt.psantos.phd.polarity.propagation.algorithm.Word;

/**
 * 
 * This example shows how to propagateNew the positive, negative, and neutral polarity 
 of words, from three seed words to the reaming words, over a <b>undirected 
 * graph</b>.
 * <br/>In this tiny graph each node represents a word (each word is 
 * represented by a number from 0 to 12), and each edge represents a synonym
 * between two words.
 * 
 * <br/>Graph representation as an adjacency list:
 * <pre>
 * {@code
 * 0: 5 6 7 8
 * 1: 4 5 7
 * 2: 3
 * 3: 2 7
 * 4: 1
 * 5: 0 1
 * 6: 0
 * 7: 1 3
 * 8: 0 9 10
 * 9: 8 10
 * 10: 8 9
 * 11: 12
 * 12: 11
 * }
 * </pre>
 *
 * In this graph 0, 1 and 2 are seed words. 0 is a positive word, 1 a negative
 * word and 2 a neutral word. A visual representation of this graph can be seen
 * on figure 1, of the paper mentioned below.
 *
 * <p>REFERENCES:
 * <br/>This example is shown on Figure 1, section 3 of the paper:
 * </p>
 * <p>
 * {@code
 * Santos, A. P., Gonçalo Oliveira, H., Ramos, C., & Marques, N. C. (2012). 
 * A Bootstrapping Algorithm for Learning the Polarity of Words. In Proceedings 
 * of 10th International Conference - Computational Processing of the Portuguese 
 * Language (PROPOR 2012), volume 7243 of LNCS, pp. 229-234. Coimbra, Portugal. 
 * Springer.
 * }
 * </p>
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class ExampleTinyUndirectedGraph {

    /**
     * Words that will make part of graph. They will be graph nodes.
     */
    private List<Word> nodes;
    private SimpleGraph<Word, LexicalRelation> simpleGraph;
    
    
   public SimpleGraph<Word, LexicalRelation> createUndirectedGraph() {
        if (this.simpleGraph == null) {
            createGraph();
        }
        return this.simpleGraph;
    }
 
   /**
     * Returns a list of seed words. This is an initial list of words classified
     * as positive, negative and eventually with the neutral polarity.
     *
     * @return a list of seed words
     */
    public List<Word> getSeedWords() {
        List<Word> seedWords = new ArrayList<Word>();
        
        if(this.simpleGraph == null) {
            createGraph();
        }
        
        for (Word w : nodes) {
            if (w.isSeed()) {
                seedWords.add(w);
            }
        }
        return (seedWords.size() > 0 ? seedWords : null);
    }
    
    private void createGraph() {
        
        // Words that will be used as graph nodes
        Word _0 = new Word("0");
        Word _1 = new Word("1");
        Word _2 = new Word("2");
        Word _3 = new Word("3");
        Word _4 = new Word("4");
        Word _5 = new Word("5");
        Word _6 = new Word("6");
        Word _7 = new Word("7");
        Word _8 = new Word("8");
        Word _9 = new Word("9");
        Word _10 = new Word("10");
        Word _11 = new Word("11");
        Word _12 = new Word("12");

        nodes = new ArrayList<Word>();
        nodes.add(_0);
        nodes.add(_1);
        nodes.add(_2);
        nodes.add(_3);
        nodes.add(_4);
        nodes.add(_5);
        nodes.add(_6);
        nodes.add(_7);
        nodes.add(_8);
        nodes.add(_9);
        nodes.add(_10);
        nodes.add(_11);
        nodes.add(_12);
        
        // Set "A" and "B" as seed words.
        _0.setAsPositiveSeed();
        _1.setAsNegativeSeed();
        _2.setAsNeutralSeed();
        
           this.simpleGraph
                = new SimpleGraph<Word, LexicalRelation>(
                        new ClassBasedEdgeFactory<Word, LexicalRelation>(LexicalRelation.class));

            for (Word w : nodes) {
                simpleGraph.addVertex(w);
            }

            simpleGraph.addEdge(_0, _5, new LexicalRelation(_0, _5, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_0, _6, new LexicalRelation(_0, _6, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_0, _7, new LexicalRelation(_0, _7, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_0, _8, new LexicalRelation(_0, _8, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_5, _1, new LexicalRelation(_5, _1, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_7, _1, new LexicalRelation(_7, _1, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_7, _3, new LexicalRelation(_7, _3, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_8, _9, new LexicalRelation(_8, _9, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_8,_10, new LexicalRelation(_8,_10, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_9,_10, new LexicalRelation(_9,_10, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_1, _4, new LexicalRelation(_1, _4, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_1, _7, new LexicalRelation(_1, _7, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_7, _3, new LexicalRelation(_7, _3, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_3, _2, new LexicalRelation(_3, _2, LexicalRelation.Type.SYNONYM));
            simpleGraph.addEdge(_11,_12, new LexicalRelation(_11,_12, LexicalRelation.Type.SYNONYM));
    }    
    
    
    public static void main(String args[]) {
        
        // Loads the sample data.
        ExampleTinyUndirectedGraph exampleData = new ExampleTinyUndirectedGraph();
        SimpleGraph<Word, LexicalRelation> initialGraph = exampleData.createUndirectedGraph();
        List<Word> seedWords = exampleData.getSeedWords();
        
        // Applies the propagation algorithm over the undirected graph
        SimpleGraph<Word, LexicalRelation> finalGraph = PolarityPropagation.propagate(initialGraph, seedWords);
        
        // Print the list of "words" to the screen
        PolarityUtils.printGraphNodes(finalGraph);
        
        // Compute and show a few statistics about the final graph
        PolarityStats stats = new PolarityStats(finalGraph);
        System.out.println(stats);               
    }    
}
