package pt.psantos.phd.polarity.propagation.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.SimpleGraph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;
import pt.psantos.phd.polarity.propagation.algorithm.Word;

/**
 * This class represents a triple loader.
 * This loader reads a file of triples and load them as an undirected graph.
 * Each line of the file must be in the format:
 * <pre>
 * {@code <word1> <relation_name> <word2>}
 * </pre>
 * WHERE
 * <ul> 
 * <li> {@code <word1> and <word2>} are sequence of character with no spaces.</li>
 * <li> {@code <relation_name>} are sequence of character with no spaces. It represents
 * a synonym or an antonym relation between {@code <word1> and <word2>}.
 * Valid names are: "syn.*" and "sin.*" for specifying a synonym relation. 
 * And the string "ant.*" for specifying an antonym relation.
 * The relation names are case insensitive. </li>
 * </ul>
 * 
 * <p>
 * Example of a valid file:
 * <pre>{@code
 * # This is a file of triples. Lines starting by # are ignored.
 * bad synonym_of evil
 * bad synonym_of unpleasant
 * bad antonym_of enjoyable
 * god synonym_of pleasant
 * pleasant synonym_of enjoyable
 * }
 * </pre>
 * </p>
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class TriplesLoader {

    /**
     * Allows to ignore lines from file that have self relations. PAPEL contains
     * a few self relations. For instance:
     * {@code ferreiro SINONIMO_N_DE ferreiro}. For building a
     * {@link org.jgrapht.graph.SimpleGraph} from the PAPEL relations we need to
     * ignore "self relations" because this kind of graph does not allow self
     * loops.
     */
    protected boolean ignoreSelfRelations = true;

    /**
     * List of filters that allows to choose what triples are loaded.
     * An empty list means that all the triples should be loaded from file.
     * @see #loadWorker(java.io.File, java.lang.String) 
     */
    private List<LoadingFilter> filters = null;
    
    /**
     * Add a filter that allows to choose the triples to load.
     * If no filter is added, all valid triples will be loaded from file.
     * 
     * @param filter a filter that allows to choose the triples to load.
     * @return this object.
     * @see LoadingFilter
     */
    public TriplesLoader addFilter(LoadingFilter filter) {
        if(filters == null) {
            this.filters = new ArrayList<LoadingFilter>();
        }
        
        filters.add(filter);
        return this;
    }

    public void setIgnoreSelfRelations(boolean ignoreSelfRelations) {
        this.ignoreSelfRelations = ignoreSelfRelations;
    }
    
    /**
     * Loads a set of triples from file. Each line of the file must be in the
     * form of: {@literal <word1> <RELATION> <word2>}.
     *
     * <ul>
     * <li>Lines starting by "#" are ignored.</li>
     * <li>Self loops are not allowed (nodes/words pointing to itself).</li>
     * </ul>
     *
     * @param file file to load.
     * @param charsetName a charset name or {@code null} to use the default charset.
     * @return an undirected graph.
     * @throws java.io.FileNotFoundException
     * @throws java.lang.IllegalArgumentException if there are direct loops.
     * This is if there are words pointing to itself.
     */
    public SimpleGraph load(File file, String charsetName) throws FileNotFoundException, IOException {
        
       SimpleGraph graph
                = new SimpleGraph<Word, LexicalRelation>(
                        new ClassBasedEdgeFactory<Word, LexicalRelation>(LexicalRelation.class));
        
        return (SimpleGraph)loadWorker(graph, file
                , (charsetName != null ? charsetName : Charset.defaultCharset().name()));
    }

    public DirectedPseudograph<Word, LexicalRelation> loadAsDirectedGraph(File file, String charsetName) throws FileNotFoundException, IOException {
        
        DirectedPseudograph<Word, LexicalRelation> graph
          = new DirectedPseudograph<Word, LexicalRelation>(
                new ClassBasedEdgeFactory<Word, LexicalRelation>(LexicalRelation.class));
        
        return (DirectedPseudograph)loadWorker(graph, file
                , (charsetName != null ? charsetName : Charset.defaultCharset().name()));
    }

    private AbstractBaseGraph loadWorker(AbstractBaseGraph<Word, LexicalRelation> graph
            ,File file , String charsetName) throws FileNotFoundException, IOException {

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

            // Ignore relations such as: {@code ferreiro SINONIMO_N_DE ferreiro}
            // because the word "ferreiro" is pointing to itself throught the 
            // relation "SINONIMO_N_DE" (synonym of).
            if (this.ignoreSelfRelations && fields[0].equals(fields[2])) {
                continue;
            }

            // If we want to filter relations by part of speech
            if(filters != null && !filters.isEmpty()) {
                boolean loadThisTriple = true;
                for(LoadingFilter filter : filters) {
                    // If there is a filter that return false
                    if(!filter.loadThis(fields)) {
                        loadThisTriple = false;
                        break;
                    }
                }
                // If we do not want to load this triple
                if(!loadThisTriple) {
                    continue;
                }
            }
            
            // Get the relation type
            LexicalRelation.Type relType = relationType(fields[1]);            
            // Check the relation type
            if(relType.equals(LexicalRelation.Type.UNKNOWN)) {
                continue; // Unknown relation type. Ignore it.
            }
            
            // Create two temporary nodes
            Word node1 = new Word(fields[0]);
            Word node2 = new Word(fields[2]);

            // If the graph contains node1
            if (graph.containsVertex(node1)) {
                // Get all the edges touching node1
                Set<LexicalRelation> edges = graph.edgesOf(node1);
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
            if (graph.containsVertex(node2)) {
                Set<LexicalRelation> edges = graph.edgesOf(node2);
                if (edges != null && !edges.isEmpty()) {
                    LexicalRelation edge = edges.iterator().next();
                    node2 = edge.getNodeSelf(node2);
                }
            }

            graph.addVertex(node1);
            graph.addVertex(node2);
            
            //System.out.println(node1 + " -> " + node2);
            graph.addEdge(node1, node2
                    , new LexicalRelation(node1, node2, relType));

        } // End while
        return graph;
    } // End method

    protected LexicalRelation.Type relationType(String relation) {
        
            relation = relation.toLowerCase();
            LexicalRelation.Type relType = LexicalRelation.Type.UNKNOWN;
            
            // Check the relation type
            if(relation.startsWith("syn") || relation.startsWith("sin")) {
                relType = LexicalRelation.Type.SYNONYM;
            } else if(relation.startsWith("ant")) {
                relType = LexicalRelation.Type.ANTONYM;
            } 
            
            return relType;
    }
}
