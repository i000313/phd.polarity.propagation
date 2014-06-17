
package pt.psantos.phd.polarity.propagation.algorithm;

import java.util.Set;
import org.jgrapht.graph.AbstractBaseGraph;

/**
 * Compute some stats from a graph.
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class PolarityStats {

    private AbstractBaseGraph<Word, LexicalRelation> graph;
    
    /** number of positive, negative and neutral words */
    private int positiveWords;
    private int negativeWords;
    private int neutralWords;
    /** number of words with have more than one polarity */
    private int ambiguousWords;
    /** number of words without polarity */
    private int notSetWords;
    
    /** total number of nodes. This should be the same as sum of the previous 5
     counters. */
    private int totalWords;
    
    /** number of seed words */
    private int seedWords;
    
    public PolarityStats(AbstractBaseGraph<Word, LexicalRelation> graph) {
        this.graph = graph;
        computeStats();
    }
    
    private void computeStats() {
        Set<Word> graphNodes = graph.vertexSet();
        this.totalWords = graphNodes.size();
                
        for(Word w : graphNodes) {
            
            if(w.isPositive()) {
                this.positiveWords++;
            } else if(w.isNegative()) {
                this.negativeWords++;
            } else if(w.isNeutral()) {
                this.neutralWords++;
            } else if(w.isAmbiguous()) {
                this.ambiguousWords++;
            } else if(!w.hasPolarity()) {
                this.notSetWords++;
            }
            
            if(w.isSeed()) {
                seedWords++;
            }
        }
    }

    public int getPositiveWords() {
        return positiveWords;
    }

    public int getNegativeWords() {
        return negativeWords;
    }

    public int getNeutralWords() {
        return neutralWords;
    }

    public int getAmbiguousWords() {
        return ambiguousWords;
    }

    public int getNotSetWords() {
        return notSetWords;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public int getSeedWords() {
        return seedWords;
    }

    
    @Override
    public String toString() {
        
        int checkSum = getPositiveWords() + getNegativeWords() + getNeutralWords() 
                + getAmbiguousWords() + getNotSetWords();
                
         return "\n========== stats ===============" +
                 "\nTOTAL NUMBER OF WORDS: " + getTotalWords() + " (Checksum: " + checkSum + ")"+
                 "\nDivided into" +
                 "\nPositive: " + getPositiveWords() +
                 "\nNegative: " + getNegativeWords() +
                 "\nNeutral.: " + getNeutralWords() +
                 "\nAmbiguous: " + getAmbiguousWords() +
                 "\nPolarity not set: " + getNotSetWords() +
                 "\n===============================" +
                 "\nSeed words: " + getSeedWords() +
                 "\n===============================";
    }
    
    
}
