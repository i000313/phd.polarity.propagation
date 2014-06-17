
package pt.psantos.phd.polarity.propagation.app;

import java.io.File;
import java.util.List;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.SimpleGraph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityPropagation;
import pt.psantos.phd.polarity.propagation.algorithm.PolarityUtils;
import pt.psantos.phd.polarity.propagation.algorithm.Word;
import pt.psantos.phd.polarity.propagation.loaders.PapelLoader;
import pt.psantos.phd.polarity.propagation.loaders.SeedWordsLoader;
import pt.psantos.phd.polarity.propagation.outputers.CsvOutput;

/**
 * Class for running the application from the command line.
 * 
 * Example:
 * {@code java -jar polarity-propagation-x.x.x-jar -seed file_name -graph file_name}
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class CommandLine {

    /** Mandatory command line arguments */
    private static File seedWordsFile = null;
    private static File graphFile = null;
    /** Optional command line arguments */
    private static File outpFile = null;
    private static String encoding = null; // Encoding for all the files
    
    private static boolean undirectedGraph = true;
    /** Check the arguments of the main method? */
    private static boolean checkMainArgs = true;
    /** Was the -h = help argument passed in the command line? */
    private static boolean helpArg = false; 
    
    public static void main(String[] args) throws Exception {
        
        // If is to check the arguments of the main method
        if(checkMainArgs) {
            if(args == null || args.length == 0 ) {
               Gui.main(null); 
               return;
            }
            parseCommandLine(args); // parse the command line
        }
        
        // Check if
        // - we already have the required arguments from the command line or GUI
        // - the help argument was passed on the command line
        if (helpArg || !hasTheRequiredArgs()) {
            comandLineUsage();
            // By convention, a nonzero status code indicates abnormal termination.
            System.exit(helpArg ? 0 : 1);
        }
        
//        System.out.println(seedWordsFile);
//        System.out.println(graphFile);
//        System.out.println(outpFile);
//        System.out.println(encoding); 
//        System.out.println("undirectedGraph: " + undirectedGraph);
        
        SeedWordsLoader sLoader = new SeedWordsLoader();
        List<Word> seedWords = sLoader.load(seedWordsFile, encoding);
        System.out.println(String.format("# of positive seed words: %d %n"
                + "# of negative seed words: %d %n"
                + "# of neutral seed words: %d"
                    , sLoader.getNumPositiveSeedWords()
                    , sLoader.getNumNegativeSeedWords()
                    , sLoader.getNumNeutralWords()));
        
        // Reads the graph from file
        PapelLoader loader = new PapelLoader();
        AbstractBaseGraph<Word, LexicalRelation> finalGraph = null;
        
        if(undirectedGraph) {
            SimpleGraph<Word, LexicalRelation> initialGraph 
                    = loader.load(graphFile, encoding);

           SimpleGraph<Word, LexicalRelation> graph 
                   = PolarityPropagation.propagate(initialGraph, seedWords);
           finalGraph = graph;
        } else {
          DirectedPseudograph<Word, LexicalRelation> initialGraph 
                  = loader.loadAsDirectedGraph(graphFile, encoding);

           DirectedPseudograph<Word, LexicalRelation> graph 
                   = PolarityPropagation.propagateNew(initialGraph, seedWords);
           finalGraph = graph;
        }

        
        if (outpFile != null) {
            CsvOutput csv = new CsvOutput(outpFile, encoding);
            csv.write(finalGraph);

            System.out.println("Output file: " + outpFile.getAbsolutePath());
        } else {
            // @TODO replace this method by a method similar to the CsvOutup
            PolarityUtils.printGraphNodes(finalGraph);
        }
        
    }
    
    
    
  /**
   * Parse command line options.
   */
  private static void parseCommandLine(String[] args) throws Exception {

    // iterate over all options (arguments starting with '-')
    for(int i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
      switch(args[i].charAt(1)) {

        // -e encoding = character encoding for all files
        case 'e':
          CommandLine.encoding = args[++i];
          break;

        // -g file_name = name of the file containing the undirected graph
        // -u file_name = name of the file containing the undirected graph
        // -d file_name = name of the file containing the directed graph
        case 'g':
        case 'u':
        case 'd':
          CommandLine.undirectedGraph = (args[i].charAt(1) != 'd');
          CommandLine.graphFile = new File(args[++i]);
          break;

        // -h = help
        case 'h':
          CommandLine.helpArg = true;
          break;

        // -o file_name = name of the file to output the final list of words
        case 'o':
          CommandLine.outpFile = new File(args[++i]);
          break;

        // -s file_name = name of the file containing the list of seed words.
        case 's':
          CommandLine.seedWordsFile = new File(args[++i]);
          break;

            
        default:
          System.err.println("Unrecognised option " + args[i]);
      }
    }
  }
  
  /**
   * Returns {@code true} if the required arguments to run the application are
   * set.
   * 
   * @return {@code true} if the required arguments to run the application are
   * set. Returns {@code false} otherwise.
   */
  protected static boolean hasTheRequiredArgs() {
      
      boolean ok = true;
      
      // Check the seed words file name
      if(CommandLine.seedWordsFile == null) {
          System.err.println("Enter the seed words file name!");
          ok = false;
      } else if (!CommandLine.seedWordsFile.exists()) {
          System.err.println("File not found: " + CommandLine.seedWordsFile.getAbsolutePath());
          ok = false;
      } 
      
      // Check the graph file name
      if(CommandLine.graphFile == null) {
          System.err.println("Enter the graph file name!");
          ok = false;
      } else if (!CommandLine.graphFile.exists()) {
          System.err.println("File not found: " + CommandLine.graphFile.getAbsolutePath());
          ok = false;
      }
      
      return ok;
  }
  
  private static void comandLineUsage() {
      System.out.println("USAGE: java -jar polarity-propagation-x.x.x-jar -seeds file_name "
              + "-graph file_name [-output file_name ] [-encoding encoding_name]");
  }
    
  
  
   //<editor-fold defaultstate="collapsed" desc="Gets and set methods">
   public static void setSeedWordsFile(File seedWordsFile) {
        CommandLine.seedWordsFile = seedWordsFile;
    }

    public static void setGraphFile(File graphFile) {
        CommandLine.graphFile = graphFile;
    }

    public static void setEncoding(String encoding) {
        CommandLine.encoding = encoding;
    }

    public static File getOutpFile() {
        return outpFile;
    }

    public static void setOutpFile(File outpFile) {
        CommandLine.outpFile = outpFile;
    }

    public static boolean isUndirectedGraph() {
        return undirectedGraph;
    }
    
    public static void setUndirectedGraph(boolean undirectedGraph) {
        CommandLine.undirectedGraph = undirectedGraph;
    }    
    
   public static void setCheckMainArgs(boolean checkMainArgs) {
        CommandLine.checkMainArgs = checkMainArgs;
    }    
    //</editor-fold>


}
