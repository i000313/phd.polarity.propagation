
package pt.psantos.phd.polarity.propagation.loaders;

/**
 * This Interface should be implemented by Loader classes that want to choose the 
 * triples to be read from file to a graph in memory.
 * 
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public interface LoadingFilter {
    
    /**
     * Returns {@code true} if the triple {@code tripleFields} passed as argument 
     * should be loaded, and {@code false} otherwise.
     * 
     * Classes implementing this methos will be called every time a triple
     * is read from file.
     * 
     * @param triple fields of the triple read from the file.
     * @return {@code true} if the triple {@code tripleFields} passed as argument 
     * should be loaded, and {@code false} otherwise.
     */
    boolean loadThis(String[] triple);
}
