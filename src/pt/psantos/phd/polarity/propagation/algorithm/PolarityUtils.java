

package pt.psantos.phd.polarity.propagation.algorithm;

import java.util.Set;
import org.jgrapht.graph.AbstractBaseGraph;

/**
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class PolarityUtils {

   
   public static void printGraph(AbstractBaseGraph<Word, LexicalRelation> graph) {
        for(Word w : graph.vertexSet()) {
            Set<LexicalRelation> rels = graph.edgesOf(w);
            for(LexicalRelation r : rels) {
                System.out.println(w + " " + r.getRelationType() + " " + r.getNodeOther(w));
            }
        }        
   }
    
   /**
    * Print all graph nodes and their counters.
    * 
    * @param graph graph which has the nodes to print.
    * @see #printGraphNodes(org.jgrapht.graph.AbstractBaseGraph, int) 
    */
   public static void printGraphNodes(AbstractBaseGraph<Word, LexicalRelation> graph) {
        for(Word w : graph.vertexSet()) {
            System.out.println(w);
        }        
   }
   
   /**
    * Print the n graph nodes and their counters.
    * 
    * @param graph graph which has the nodes to print.
    * @param n number of nodes to print
    */
   public static void printGraphNodes(AbstractBaseGraph<Word, LexicalRelation> graph, int n) {
       int i = 0; 
       for(Word w : graph.vertexSet()) {
            System.out.println(w);
            
            if(++i >= n)
                return;
        }        
    }
}
