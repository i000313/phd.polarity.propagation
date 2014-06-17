package pt.psantos.phd.polarity.propagation.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleGraph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;
import pt.psantos.phd.polarity.propagation.algorithm.Word;

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
@Deprecated
public class PAPELLoaderOLD {

    /**
     * Allows to ignore lines from file that have self relations. PAPEL contains
     * a few self relations. For instance:
     * {@code ferreiro SINONIMO_N_DE ferreiro}. For building a
     * {@link org.jgrapht.graph.SimpleGraph} from the PAPEL relations we need to
     * ignore "self relations" because this kind of graph does not allow self
     * loops.
     */
    private boolean ignoreSelfRelations = true;

    /**
     * Part of speech tag.
     */
    public enum POS {
        NOUN, VERB, ADJECTIVE, ADVERB, ALL
    }

    /**
     * Loads a set of triples from file. Each line of the file must be in the
     * form of: {@literal  <word1> <RELATION> <word2>}.
     *
     * <ul>
     * <li>Lines starting by "#" are ignored.</li>
     * <li>Self loops are not allowed (nodes/words pointing to itself).</li>
     * </ul>
     *
     * @param file file to load.
     * @param charsetName a charset.
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.lang.IllegalArgumentException if there are direct loops.
     * This is if there are words pointing to itself.
     */
    public SimpleGraph load(File file, String charsetName) throws FileNotFoundException, IOException {
        return loadWorker(file, charsetName, POS.ALL);
    }

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
        return loadWorker(file, charsetName, pos);
    }

    public SimpleGraph loadWorker(File file, String charsetName, POS pos) throws FileNotFoundException, IOException {

        SimpleGraph simpleGraph
                = new SimpleGraph<Word, LexicalRelation>(
                        new ClassBasedEdgeFactory<Word, LexicalRelation>(LexicalRelation.class));

        Reader reader = new InputStreamReader(new FileInputStream(file), charsetName);
        BufferedReader br = new BufferedReader(reader);
        String line;
        String[] fields = null;

        while ((line = br.readLine()) != null) {

            // Ignore comments (lines starting by "#")
            if (line.startsWith("#")) {
                continue;
            }

            // Split the triple in fields
            fields = line.split("\\s+");

            // Ignore every lines that does not have exactly 3 fields
            if (fields.length != 3) {
                continue;
            }

            // Ignore relations such as: {@code ferreiro SINONIMO_N_DE ferreiro}
            // because the word "ferreiro" is pointing to itself throught the 
            // relation "SINONIMO_N_DE" (synonym of).
            if (this.ignoreSelfRelations && fields[0].equals(fields[2])) {
                continue;
            }

            // If we want to filter relations by part of speech
            if(!pos.equals(POS.ALL)) {
                if(pos.equals(POS.NOUN) && !fields[1].endsWith("N_DE")) {
                    continue;
                } else if(pos.equals(POS.ADJECTIVE) && !fields[1].endsWith("ADJ_DE")) {
                    continue;
                } else if(pos.equals(POS.ADVERB) && !fields[1].endsWith("ADV_DE")) {
                    continue;
                } else if(pos.equals(POS.VERB) && !fields[1].endsWith("ADV_DE")) {
                    continue;
                }
            }
            
            // Create two temporary nodes
            Word node1 = new Word(fields[0]);
            Word node2 = new Word(fields[2]);

            // If the graph contains node1
            if (simpleGraph.containsVertex(node1)) {
                // Get all the edges touching node1
                Set<LexicalRelation> edges = simpleGraph.edgesOf(node1);
                // If node1 already exists in the graph
                if (edges != null && !edges.isEmpty()) {
                    // Get the first edge
                    LexicalRelation edge = edges.iterator().next();
                        // Replace the temporary node1 by the node already on the graph.
                    // The temporary node1 and the returned have the word but
                    // are diffrent Java objects. 
                    node1 = edge.getNodeSelf(node1);
                }
            }

            // If the graph contains node2
            if (simpleGraph.containsVertex(node2)) {
                Set<LexicalRelation> edges = simpleGraph.edgesOf(node2);
                if (edges != null && !edges.isEmpty()) {
                    LexicalRelation edge = edges.iterator().next();
                    node2 = edge.getNodeSelf(node2);
                }
            }

            simpleGraph.addVertex(node1);
            simpleGraph.addVertex(node2);

            //System.out.println(node1 + " -> " + node2);
            simpleGraph.addEdge(node1, node2, new LexicalRelation(node1, node2, LexicalRelation.Type.SYNONYM));

//                if (fields.length == 4 && fields[3].matches(".*(irón).*")) // troca a relação por Antónimo
//                {
//                    fields[1] = fields[1] + "_ANT";
//                } else if (fields.length == 4 && fields[3].matches(".*(pej).*")) // troca a relação
//                {
//                    fields[1] = fields[1] + "_PEJ";
//                } else if (fields.length == 4 && fields[3].matches(".*(depr).*")) // troca a relação
//                {
//                    fields[1] = fields[1] + "_DEPR";
//                }
        } // End while
        return simpleGraph;
    } // End method

}
