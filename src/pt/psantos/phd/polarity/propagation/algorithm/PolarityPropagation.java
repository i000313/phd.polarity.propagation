package pt.psantos.phd.polarity.propagation.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.SimpleGraph;

/**
 * Implementation of some algorithms to propagateNew the polarity of words over
 a graph.
 *
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class PolarityPropagation {

    /**
     * Implementation of the algorithm described on the paper mention below. 
     * This algorithm is used to propagateNew the polarity of an initial set of 
 words, to a set of unlabeled words, using a <b>directed graph</b>.
     *
     * <p>
     * REFERENCES:<br/>
     * {@code
     * Santos, A. P., Ramos, C., & Marques, N. C. (2011). Determining the Polarity of
     * Words through a Common Online Dictionary. In Proceedings of 15th Portuguese
     * Conference on Artificial on Artificial intelligence (EPIA 2011), volume 7026
     * of LNCS, pp. 649-663. Lisbon, Portugal. Springer.
     * }
     * </p>
     *
     * @param graph a directed graph of words connected by lexical relations.
     * @param seedWords initial set of words classified with a polarity.
     * @return the input graph after propagating the polarity from the seed
     * words to the unlabled words.
     */   
       public static DirectedPseudograph<Word, LexicalRelation> propagate(
            DirectedPseudograph<Word, LexicalRelation> graph, List<Word> seedWords) {

        // @TODO to replace this data structures by faster data structures
        List<Word> nodesToVisit = new ArrayList<Word>(seedWords);
        //Deque<Word> nodesVisitedQueue = new ArrayDeque<Word>();
        HashSet<Word> nodesVisited = new HashSet<Word>();

        // 2. Retrieve the1rst word w1 from the queue Q
        for (int i = 0; i < nodesToVisit.size(); i++) {
            Word node = nodesToVisit.get(i);
            //System.out.println("Vertex: " + node);
            // Get all their neighbors
            for (LexicalRelation ed : graph.outgoingEdgesOf(node)) {

                Word neighborNode = ed.getNodeTo();

                // Avoids propagation back a polarity received fron its neighbors
                boolean avoidBackPropagation = false;

                // If we want to avoid back propagation and the direct neigbor 
                // node was already visited.
                if (avoidBackPropagation && graph.containsEdge(neighborNode, node)
                        && nodesVisited.contains(neighborNode)) {
                    continue;
                }

                // For each neighbor visited for the 1rst time, set the iteration 
                // counter to value of iteration counter of currentWrd + 1.
                if (!neighborNode.isIterationSet()) {
                    neighborNode.setIteration(node.getIteration() + 1);
                }

                if (ed.getRelationType().equals(LexicalRelation.Type.SYNONYM)) {
                    if (node.isPositive()) {
                        neighborNode.increasePosCounter();
                    } else if (node.isNegative()) {
                        neighborNode.increaseNegCounter();
                    } else if (node.isNeutral()) {
                        neighborNode.increaseNeutralCounter();
                    }
                } else if (ed.getRelationType().equals(LexicalRelation.Type.ANTONYM)) {
                    if (node.isPositive()) {
                        neighborNode.increaseNegCounter();
                    } else if (node.isNegative()) {
                        neighborNode.increasePosCounter();
                    } else if (node.isNeutral()) {
                        neighborNode.increaseNeutralCounter();
                    }
                }

                // (b) If nbi does not exists on queue Q nor list V, add him to the end of Q.
                if (!nodesToVisit.contains(neighborNode)
                        && !nodesVisited.contains(neighborNode)) {
                    nodesToVisit.add(neighborNode);
                }
                //System.out.println("edge: " + ed + " of " + ed.getNodeTo());
            }
            nodesVisited.add(node);
        }
        return graph;
    }
       
       
       
// @TODO needs to be revised. It is not is not producing a correct output.
    public static DirectedPseudograph<Word, LexicalRelation> propagateNew(
            DirectedPseudograph<Word, LexicalRelation> graph, List<Word> seedWords) {

        //
        // Specify which graph words are seed words
        //
        Set<Word> words = graph.vertexSet();
        int seedWordsFound = 0;
        HashMap<Integer, Word> seedWordsAux = new HashMap(seedWords.size());

        // For each seed word
        for (Word w : words) {
            // Check if the current word is a seed word
            int idx = seedWords.indexOf(w);
            // If the current word is a seed word
            if (idx >= 0) {
                Word seedWord = seedWords.get(idx); // Get the seed word
                w.copyState(seedWord); // Set the current word as a seed word
                //System.out.println(w);

                seedWordsAux.put(seedWordsFound, w);
                seedWordsFound++;
            }
        }

        // If no seed words were found in the graph 
        if (seedWordsFound <= 0) {
            throw new IllegalArgumentException("Seed words not found in the graph.");
        }

        //
        // Begining of the propagation algorithm
        //
        //List<Word> nodesToVisit = new ArrayList<Word>(seedWords);
        //LinkedList<Word> nodesToVisit = new LinkedList<Word>(seedWords);
        LinkedHashMap<Word, Word> nodesToVisit = new LinkedHashMap<Word, Word>();

        for (int i = 0; i < seedWordsFound; i++) {
            Word sw = seedWordsAux.get(i);
            if (sw != null) {
                nodesToVisit.put(sw, null);
            }
        }
        
        //Deque<Word> nodesVisitedQueue = new ArrayDeque<Word>();
        HashSet<Word> nodesVisited = new HashSet<Word>();
        
        // 2. Retrieve the1rst word w1 from the queue Q
        for (int i = 0; i < nodesToVisit.size(); i++) {
            Word node = nodesToVisit.entrySet().iterator().next().getKey();
            nodesToVisit.remove(node);
            System.out.println("Vertex: " + node);
            // Get all their neighbors
            for (LexicalRelation ed : graph.outgoingEdgesOf(node)) {

                Word neighborNode = ed.getNodeTo();

                // Avoids propagation back a polarity received fron its neighbors
                boolean avoidBackPropagation = true;

                // If we want to avoid back propagation and the direct neigbor 
                // node was already visited.
                if (avoidBackPropagation && graph.containsEdge(neighborNode, node)
                        && nodesVisited.contains(neighborNode)) {
                    continue;
                }

                // For each neighbor visited for the 1rst time, set the iteration 
                // counter to value of iteration counter of currentWrd + 1.
                if (!neighborNode.isIterationSet()) {
                    neighborNode.setIteration(node.getIteration() + 1);
                }

                if (ed.getRelationType().equals(LexicalRelation.Type.SYNONYM)) {
                    if (node.isPositive()) {
                        neighborNode.increasePosCounter();
                    } else if (node.isNegative()) {
                        neighborNode.increaseNegCounter();
                    } else if (node.isNeutral()) {
                        neighborNode.increaseNeutralCounter();
                    }
                } else if (ed.getRelationType().equals(LexicalRelation.Type.ANTONYM)) {
                    if (node.isPositive()) {
                        neighborNode.increaseNegCounter();
                    } else if (node.isNegative()) {
                        neighborNode.increasePosCounter();
                    } else if (node.isNeutral()) {
                        neighborNode.increaseNeutralCounter();
                    }
                }

                // (b) If nbi does not exists on queue Q nor list V, add him to the end of Q.
                if (!nodesToVisit.containsKey(neighborNode)
                        && !nodesVisited.contains(neighborNode)) {
                    nodesToVisit.put(neighborNode, neighborNode);
                }
                //System.out.println("edge: " + ed + " of " + ed.getNodeTo());
            }
            nodesVisited.add(node);
        }
        return graph;
    }       
       
       
    /**
     * Implementation of the algorithm described on the paper mention below. 
     * This algorithm is used to propagateNew the polarity of an initial set of 
 words, to a set of unlabeled words, using a <b>undirected graph</b>.
     *
     * <p>
     * REFERENCES:<br/>      
     * {@code
     * Santos, A. P., Gonçalo Oliveira, H., Ramos, C., & Marques, N. C. (2012).
     * A Bootstrapping Algorithm for Learning the Polarity of Words. In Proceedings
     * of 10th International Conference - Computational Processing of the Portuguese
     * Language (PROPOR 2012), volume 7243 of LNCS, pp. 229-234. Coimbra, Portugal.
     * Springer.
     * }
     * </p>
     * 
     * @param graph a directed graph of words connected by lexical relations.
     * @param seedWords initial set of words classified with a polarity.
     * @return the input graph after propagating the polarity from the seed
     * words to the unlabled words.
     */
    public static SimpleGraph<Word, LexicalRelation> propagate(
            SimpleGraph<Word, LexicalRelation> graph, List<Word> seedWords) {

        //
        // Specify which graph words are seed words
        //
        Set<Word> words = graph.vertexSet();
        int seedWordsFound = 0;
        HashMap<Integer, Word> seedWordsAux = new HashMap(seedWords.size());

        // For each seed word
        for (Word w : words) {
            // Check if the current word is a seed word
            int idx = seedWords.indexOf(w);
            // If the current word is a seed word
            if (idx >= 0) {
                Word seedWord = seedWords.get(idx); // Get the seed word
                w.copyState(seedWord); // Set the current word as a seed word
                //System.out.println(w);

                seedWordsAux.put(seedWordsFound, w);
                seedWordsFound++;
            }
        }

        // If no seed words were found in the graph 
        if (seedWordsFound <= 0) {
            throw new IllegalArgumentException("Seed words not found in the graph.");
        }

        //
        // Begining of the propagation algorithm
        //
        //List<Word> nodesToVisit = new ArrayList<Word>(seedWords);
        //LinkedList<Word> nodesToVisit = new LinkedList<Word>(seedWords);
        LinkedHashMap<Word, Word> nodesToVisit = new LinkedHashMap<Word, Word>();

        for (int i = 0; i < seedWordsFound; i++) {
            Word sw = seedWordsAux.get(i);
            if (sw != null) {
                nodesToVisit.put(sw, null);
            }
        }

        //Deque<Word> nodesVisitedQueue = new ArrayDeque<Word>();
        HashSet<Word> nodesVisited = new HashSet<Word>();

        // 2. Retrieve the1rst word w1 from the queue Q
        for (int i = 0; i < nodesToVisit.size();) {
            Word node = nodesToVisit.entrySet().iterator().next().getKey(); // nodesToVisit.get(i);
            nodesToVisit.remove(node);
            //System.out.println("Vertex: " + node);

            // If graph does not contains this word
            if (!graph.containsVertex(node)) {
                continue;
            }

            // Get all their neighbors.
            // NOTE: Calling graph.outgoingEdgesOf(node) gives the exception:
            // Exception in thread "main" java.lang.UnsupportedOperationException: no such operation in an undirected graph
            for (LexicalRelation ed : graph.edgesOf(node)) {

                Word neighborNode = ed.getNodeOther(node);

                // Avoids propagation back a polarity received fron its neighbors
                boolean avoidBackPropagation = true;

                // If we want to avoid back propagation and the neighbor node
                // was already visited.
                if (avoidBackPropagation && nodesVisited.contains(neighborNode)) {
                    continue;
                }

                // For each neighbor visited for the 1rst time, set the iteration 
                // counter to value of iteration counter of currentWrd + 1.
                if (!neighborNode.isIterationSet()) {
                    neighborNode.setIteration(node.getIteration() + 1);
                }

                if (ed.getRelationType().equals(LexicalRelation.Type.SYNONYM)) {
                    if (node.isPositive()) {
                        neighborNode.increasePosCounter();
                    } else if (node.isNegative()) {
                        neighborNode.increaseNegCounter();
                    } else if (node.isNeutral()) {
                        neighborNode.increaseNeutralCounter();
                    }
                } else if (ed.getRelationType().equals(LexicalRelation.Type.ANTONYM)) {
                    if (node.isPositive()) {
                        neighborNode.increaseNegCounter();
                    } else if (node.isNegative()) {
                        neighborNode.increasePosCounter();
                    } else if (node.isNeutral()) {
                        neighborNode.increaseNeutralCounter();
                    }
                }

                // (b) If nbi does not exists on queue Q nor on list V, add him to the end of Q.
                if (/*!nodesToVisit.contains(neighborNode)*/!nodesToVisit.containsKey(neighborNode)
                        && !nodesVisited.contains(neighborNode)) {
                    //nodesToVisit.add(neighborNode);
                    nodesToVisit.put(neighborNode, neighborNode);
                }
                //System.out.println("edge: " + ed + " of " + ed.getNodeTo());
            }
            nodesVisited.add(node);

//            if(i % 1000 == 0)
//                System.out.println(i);
        }
        return graph;
    }

}
