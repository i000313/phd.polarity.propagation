
package pt.psantos.phd.polarity.propagation.loaders;

import java.io.File;
import java.io.IOException;
import org.jgrapht.graph.SimpleGraph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;

/**
 * Class for loading PAPEL 2.0. This class allows to load a set of relations
 * from file and build a graph.
 *
 ******************************************************************************
 * REFERENCES: PAPEL is resource created by Linguateca from the "Dicionário PRO
 * de Língua Portuguesa", a Portuguese dictionary. PAPEL was developed mainly by
 * Hugo Gonçalo Oliveira. More info about PAPEL, can be found at:
 * http://www.linguateca.pt/PAPEL/
 * ****************************************************************************
 *
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class PapelLoader extends TriplesLoader implements LoadingFilter {
    
    /**
     * Part of speech tag.
     */
    public enum POS {
        NOUN, VERB, ADJECTIVE, ADVERB, ALL
    }
    
    private POS DEFAULT_POS = POS.ALL;
    
     /**
     * Loads all triples from file {
     *
     * @file} that have the part of speech specify by the parameter {@code pos}.
     * Each line of the file must be in the form of:
     * {@literal  <word1> <RELATION> <word2>}.
     *
     * <ul>
     * <li>Lines starting by "#" are ignored.</li>
     * <li>Self loops are not allowed (nodes/words pointing to itself).</li>
     * </ul>
     *
     * @param file file to load.
     * @param charsetName a charset.
     * @param pos
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.lang.IllegalArgumentException if there are direct loops.
     * This is if there are words pointing to itself.
     */
     public SimpleGraph load(File file, String charsetName, POS pos) throws IOException {
         // If the pos argument is not null
         if(pos != null) {
             this.DEFAULT_POS = pos; // Set the POS tag of the triples that we want to load
         }
         
         // Add a filter that allows to choose which triples to load from file.
         // @see loadThis(String[] tripleFields)
         super.addFilter(this);
         super.setIgnoreSelfRelations(true);
         
         return super.load(file, charsetName);
     }
            
    @Override
    public boolean loadThis(String[] tripleFields) {
        // Ignore every lines that does not have exactly 3 fields
        if (tripleFields.length != 3) {
            return false;
        }
        
        // If we want to load relations with a specif part of speech
        POS pos = this.DEFAULT_POS;
        if (!pos.equals(POS.ALL)) {
            if (pos.equals(POS.NOUN) && !tripleFields[1].endsWith("N_DE")) {
                return false;
            } else if (pos.equals(POS.ADJECTIVE) && !tripleFields[1].endsWith("ADJ_DE")) {
                return false;
            } else if (pos.equals(POS.ADVERB) && !tripleFields[1].endsWith("ADV_DE")) {
                return false;
            } else if (pos.equals(POS.VERB) && !tripleFields[1].endsWith("ADV_DE")) {
                return false;
            }
        }
            
        return true;
    }
    

}
