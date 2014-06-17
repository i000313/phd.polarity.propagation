
package pt.psantos.phd.polarity.propagation.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.graph.SimpleGraph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityPropagation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityStats;
import pt.psantos.phd.polarity.propagation.algorithm.Word;
import pt.psantos.phd.polarity.propagation.loaders.PapelLoader;
import pt.psantos.phd.polarity.propagation.loaders.PapelLoader.POS;
import pt.psantos.phd.polarity.propagation.outputers.CsvOutput;

/**
 * This example shows how to propagateNew the positive, negative, and neutral polarity 
 of words, from three seed words to the reaming words, over a <b>undirected 
 * graph</b>.
 * <p>In this graph each node represents a word and each edge represents a 
 * synonym between two words.
 * <br/>In this example we use PAPEL (See references below) for building the graph.
 * PAPEL has a set of relations in the form of: 
 * {@literal <lexical_item1> <lexical_relation> <lexical_item2>}
 * that are used to build the graph.
 * </p>
 * 
 * <p>REFERENCES:
 * <br/>PAPEL - http://www.linguateca.pt/PAPEL/
 * </p>
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class ExamplePapel {

    public static void main(String args[]) throws IOException {
        
        // Get a graph from the PAPEL relations (a Portuguse resourse)
        PapelLoader papelLoader = new PapelLoader();
        SimpleGraph<Word, LexicalRelation> initialGraph 
                = papelLoader.load(
                 new File("examples/02-graph-portuguese/papel-2.0-relacoes_final_SINONIMIA-utf8.txt")
                , "utf-8"
                , POS.ADJECTIVE);
       
        // Choose the seed words
        List<Word> seedWords = new ArrayList<Word>(); 
        seedWords.add((new Word("bonito")).setAsPositiveSeed());
        seedWords.add((new Word("talento")).setAsPositiveSeed());
        seedWords.add((new Word("interessante")).setAsPositiveSeed());
        
        seedWords.add((new Word("feio")).setAsNegativeSeed());
        seedWords.add((new Word("conspurcado")).setAsNegativeSeed());
        seedWords.add((new Word("discriminação")).setAsNegativeSeed());
        seedWords.add((new Word("ruinosamente")).setAsNegativeSeed()); 
        seedWords.add((new Word("deploravelmente")).setAsNegativeSeed()); 
        seedWords.add((new Word("falsamente")).setAsNegativeSeed());
        
        // Propagate the polarity from the seed words to the remaining words
        SimpleGraph<Word, LexicalRelation> finalGraph = 
                PolarityPropagation.propagate(initialGraph, seedWords);
        
        // Show some statistics about the final graph
        PolarityStats stats = new PolarityStats(finalGraph);
        System.out.println(stats);
        
        CsvOutput csv = new CsvOutput(new File("dic-adjectives-polarity.csv"));
        csv.write(finalGraph);
    }
    
}
