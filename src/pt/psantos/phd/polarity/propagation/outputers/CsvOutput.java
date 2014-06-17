package pt.psantos.phd.polarity.propagation.outputers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.graph.AbstractBaseGraph;
import pt.psantos.phd.polarity.propagation.algorithm.LexicalRelation;
import pt.psantos.phd.polarity.propagation.algorithm.Word;

/**
 * Class for writing the graph nodes (words and their polarity) as a csv file.
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class CsvOutput {

    /**
     * A char variable can't be empty. Therefore let's use this special
     * character to check if the char variable was initialised.
     */
    private final char NULL_CHAR = '\0';
    
    /**
     * Class to keep all the csv configurations in one place.
     */
    public class CsvConfig {
        // Write an header line with the field names?
        public boolean HEADER = true;

        // Fields configuration
        public String FIELDS_TERMINATED_BY = ",";
        public char FIELDS_ENCLOSED_BY = NULL_CHAR;
        public char FIELDS_ESCAPED_BY = NULL_CHAR;

        // Lines configuration
        public String LINES_STARTING_BY = "";
        public String LINES_TERMINATED_BY = System.getProperty("line.separator");
    }

    /**
     * CSV configurations
     */
    private CsvConfig defaultCsvConfig = new CsvConfig();
    
    /**
     * Csv file name
     */
    private File outputFile;
    /**
     * Csv file encoding
     */
    private String encoding;





    /**
     * Creates an CsvOutput that uses the default character encoding and default
     * csv configurations.
     * 
     * @param file a File.
     * @see #write(org.jgrapht.graph.AbstractBaseGraph)
     */
    public CsvOutput(File file) {
        initialise(file, Charset.defaultCharset().name(), defaultCsvConfig);
    }
    
    /**                                                       
     * Creates an CsvOutput that uses the character encoding passed on the parameter
     * {@code encoding} and using the default csv configurations. 
     *
     * @param file a File
     * @param encoding the name of a supported {@link java.nio.charset.Charset}.
     * @see #write(org.jgrapht.graph.AbstractBaseGraph)
     */
    public CsvOutput(File file, String encoding) {
        initialise(file, encoding, defaultCsvConfig);
    }
    
    /**                                                       
     * Creates an CsvOutput that uses the character encoding passed on the parameter
     * {@code encoding} and the csv configurations passed by the parameter 
     * {@code csvConfig}. 
     *
     * @param file a File
     * @param encoding the name of a supported {@link java.nio.charset.Charset}.
     * @param csvConfig a {@link CsvConfig}
     * @see #write(org.jgrapht.graph.AbstractBaseGraph)
     */    
   public CsvOutput(File file, String encoding, CsvConfig csvConfig) {
       initialise(file, encoding, csvConfig);
    }
   
   /**
    * Initialise all the required variables.
    * 
    * @param file csv file to be created.
    * @param encoding the name of a supported {@link java.nio.charset.Charset}.
    * @param csvConfig a {@link CsvConfig}
    */
   private void initialise(File file, String encoding, CsvConfig csvConfig) {
        this.outputFile = file;
        this.encoding = encoding;
        this.defaultCsvConfig = csvConfig;
   }
   
    /**
     * Write all the words and their polarity to file.
     * 
     * @param graph a graph where nodes are words.
     */
    //@TODO deal with the "FIELDS_ESCAPED_BY" character
    public void write(AbstractBaseGraph<Word, LexicalRelation> graph) {

        Set<Word> graphNodes = graph.vertexSet();
        Writer out = null;
        try {
            
            // A não indicação do charSet, faz com que grave em UTF-8 (final parece
            // que não grava em UTF-8, mas sim aquilo que o sistema mandar).
            // Ao fazer o LOAD para o mysql indicar "CHARACTER SET utf8" 
            FileOutputStream fos = new FileOutputStream(outputFile);
            out = new BufferedWriter(encoding == null ? new OutputStreamWriter(fos)
                    : new OutputStreamWriter(fos, encoding));//new OutputStreamWriter(new FileOutputStream(fileName));

            // If we want to write a header
            if (this.defaultCsvConfig.HEADER) {
                out.write(this.defaultCsvConfig.LINES_STARTING_BY);
                out.write("words");
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write("polarity");
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write("negativeCounter");
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write("neutralCounter");
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write("positiveCounter");
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write("iteration");
                out.write(this.defaultCsvConfig.LINES_TERMINATED_BY);
            }

            // For each word (graph node)
            for (Word w : graphNodes) {
                out.write(this.defaultCsvConfig.LINES_STARTING_BY);

                if (this.defaultCsvConfig.FIELDS_ENCLOSED_BY != NULL_CHAR) {
                    out.write(this.defaultCsvConfig.FIELDS_ENCLOSED_BY);
                }

                out.write(w.getWord());

                if (this.defaultCsvConfig.FIELDS_ENCLOSED_BY != NULL_CHAR) {
                    out.write(this.defaultCsvConfig.FIELDS_ENCLOSED_BY);
                }

                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);

                out.write(String.valueOf(getPolarity(w)));
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write(String.valueOf(w.getNegativeCounter()));
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write(String.valueOf(w.getNeutralCounter()));
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write(String.valueOf(w.getPositiveCounter()));
                out.write(this.defaultCsvConfig.FIELDS_TERMINATED_BY);
                out.write(String.valueOf(w.getIteration()));

                out.write(this.defaultCsvConfig.LINES_TERMINATED_BY);
            }

        } catch (IOException ex) {
            Logger lgr = Logger.getLogger(CsvOutput.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                }
            }
        }

    }

    /**
     * Returns a character tht represents the word polarity.
     * 
     * @param w
     * @return '+' if the word is positive. 
     *         '-' if the word is negative.
     *         '0' if the word is neutral.
     *         'A' if the polarity is ambiguous (has mora than one polarity).
     *         'U' if the polarity is not set.
     */
    public static char getPolarity(Word w) {
        if(w.isPositive()) {
            return '+';
        } else if(w.isNegative()) {
            return '-';
        } else if(w.isNeutral()) {
            return '0';
        } else if(w.isAmbiguous()){
            return 'A';
        } else {
            return 'U';
        }
    }
    
    public static void main(String args[]) {
        char ch1 = '\u0000';
        char ch2;
        System.out.println("OK: " + (ch1 == '\0'));
        System.out.println("OK: " + (ch1 == '\u0000'));
    }
}
