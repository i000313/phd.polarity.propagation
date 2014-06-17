package pt.psantos.phd.polarity.propagation.algorithm;

/**
 * Represents a word and its polarity.
 *
 * @since 0.6.0
 * @version 0.6.0
 * @author PSantos
 */
public class Word {

    /**
     * Positive counter. Keeps the number of positive polarity received from its
     * neighbors words.
     */
    private int positiveCounter;

    /**
     * Negative counter. Keeps the number of negative polarity received from its
     * neighbors words.
     */
    private int neutralCounter;

    /**
     * Neutral counter. Keeps the number of neutral polarity received from its
     * neighbors words.
     */
    private int negativeCounter;

    /**
     * Iteration counter. Keeps the distance (the number of edges) from this
     * word to the closest seed word.
     */
    private int iteration;

    /**
     * The word itself.
     */
    private String text;

    private enum Polarity {
        POSITIVE, NEGATIVE, NEUTRAL, AMBIGUOUS, NOT_SET
    }

    /**
     * Creates a word without a polarity. This means that this word is neither
     * positive, negative nor neutral.
     *
     * @param word word to create.
     * @see #setAsNegativeSeed()
     * @see #setAsNeutralSeed()
     * @see #setAsPositiveSeed()
     */
    public Word(String word) {
        initialise(word, 0, 0, 0, -1);
    }

//    /**
//     * Create a word with the values passed as parameter.
//     * 
//     * @param word word to create.
//     * @param posCount a value greater or equal to 0.
//     * @param negCount a value greater or equal to 0.
//     * @param neuCount a value greater or equal to 0.
//     * @param iteration 0 if this is a seed word. A value less than 0 if  
//     */
//    public Word(String word, int posCount, int negCount, int neuCount, int iteration){
//        initialise(word, posCount, negCount, neuCount, iteration);
//    }
    private void initialise(String word, int posCount, int negCount, int neuCount, int iteration) {
        this.text = word;
        this.positiveCounter = posCount;
        this.negativeCounter = negCount;
        this.neutralCounter = neuCount;
        this.iteration = iteration;
    }

    /**
     * Returns the word itself. Example: "car", "bad".
     */
    public String getWord() {
        return text;
    }

    public int getPositiveCounter() {
        return positiveCounter;
    }

    public int getNeutralCounter() {
        return neutralCounter;
    }

    public int getNegativeCounter() {
        return negativeCounter;
    }

    
    public Word increasePosCounter() {
        positiveCounter++;
        return this;
    }

    public Word increaseNeutralCounter() {
        neutralCounter++;
        return this;
    }

    public Word increaseNegCounter() {
        negativeCounter++;
        return this;
    }

    public Word increaseIteration() {
        iteration++;
        return this;
    }

    /**
     * Set this word as a positive seed word. This means to set the posivite counter
     * to 1 and the iteration counter to 0.
     *
     * @return this word as a positive seed word.
     */
    public Word setAsPositiveSeed() {
        positiveCounter = 1;
        negativeCounter = 0;
        neutralCounter = 0;
        iteration = 0;
        return this;
    }

    /**
     * Set this word as a negative seed word. This means to set the negative counter
     * to 1 and the iteration counter to 0.
     *
     * @return this word as a negative seed word.
     */
    public Word setAsNegativeSeed() {
        positiveCounter = 0;
        negativeCounter = 1;
        neutralCounter = 0;
        iteration = 0;
        return this;
    }

    /**
     * Set this word as a neutral seed word. This means to set the neutral counter
     * to 1 and the iteration counter to 0.
     *
     * @return this word as a neutral seed word.
     */
    public Word setAsNeutralSeed() {
        positiveCounter = 0;
        negativeCounter = 0;
        neutralCounter = 1;
        iteration = 0;
        return this;
    }

    public boolean isSeed() {
        return iteration == 0;
    }

    public boolean isPositive() {
        return polarity().equals(Polarity.POSITIVE);
    }

    public boolean isNegative() {
        return polarity().equals(Polarity.NEGATIVE);
    }

    public boolean isNeutral() {
        return polarity().equals(Polarity.NEUTRAL);
    }

    /**
     * Returns true if the word has an ambiguous polarity (e.g. it is positive
     * and negative at same time, it is ngastive and neutral, etc.).
     *
     * @return {@code true} if the word has an ambiguous polarity, {@code false}
     * otherwise.
     */
    public boolean isAmbiguous() {
        return polarity().equals(Polarity.AMBIGUOUS);
    }

    public boolean hasPolarity() {
        // Any value less than -1 means that this word has no polarity
        return !polarity().equals(Polarity.NOT_SET);
    }

    public boolean isIterationSet() {
        return this.iteration >= 0;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }
            
    
    /**
     * Returns if this word is positive, negative, neutral, ambiguous or the 
     * polarity is not set.
     *
     * @return 
     */
    private Polarity polarity() {
        if (positiveCounter <= 0 && negativeCounter <= 0
                && neutralCounter <= 0) {
            return Polarity.NOT_SET;
        } else if (positiveCounter > negativeCounter && positiveCounter > neutralCounter) {
            return Polarity.POSITIVE; // Positive word
        } else if (negativeCounter > positiveCounter && negativeCounter > neutralCounter) {
            return Polarity.NEGATIVE; // Negative word.
        } else if (neutralCounter > positiveCounter && neutralCounter > negativeCounter) {
            return Polarity.NEUTRAL; // Neutral word.
        } else {
            return Polarity.AMBIGUOUS;
        }
    }

    /**
     * Copy all the values of all the fields, from the word {@code from} to this 
     * one.
     * @param from the word with the fields to be copied.
     * @return this word.
     */
    public Word copyState(Word from) {
        this.positiveCounter = from.getPositiveCounter();
        this.negativeCounter = from.getNegativeCounter();
        this.neutralCounter = from.getNeutralCounter();
        this.iteration = from.getIteration();
        this.text = getWord();
        return this;
    }
            
    /**
     * Check if two words are equal. Two words are equal if 
     * {@code w1.getWord().equals(w2.getWord())} .
     * Note that his method just checks one of the fields (the text field), not all.
     * So two Word objects that are considered equal can actually have different
     * values.
     * 
     * Override the equals method this way is done by others as we can see by the
     * following link:
     * http://stackoverflow.com/questions/7283338/getting-an-element-from-a-set
     * 
     * @return {@code true} if the words are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Word && ((Word)obj).getWord().equals(this.getWord())); 
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.text != null ? this.text.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.text + "[+:" + positiveCounter + " -:" + negativeCounter 
                + " 0:" + neutralCounter + " I:" + iteration + "]";
    }

    
}
