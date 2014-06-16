/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dic.wordspolarity;

import dic.wordspolarity.graphbook.In;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe criada para carregar seed words podendo-se indicar alguns critérios.
 * 
 * @author PSantos
 */
public class LoadSeedWords {
    // Save the seed words grouped by polarity
    private List<String> posSeedWords;
    private List<String> negSeedWords;
    private List<String> neuSeedWords;
    
    // Save the seed words in a sequencial order
    private List<String> seedWords;
    private List<Integer> seedPolarity;
    
    public LoadSeedWords(String seedWords_file) {
        
        posSeedWords = new ArrayList<String>();
        negSeedWords = new ArrayList<String>();
        neuSeedWords = new ArrayList<String>();
        
        seedWords = new ArrayList<String>();
        seedPolarity = new ArrayList<Integer>();
        
        loadSeeds(seedWords_file);
    }
    
    private void loadSeeds(String seedWords_file) {
        
        In in = new In(seedWords_file);
        
        int seedWordLines = 0; // Número de linhas que indicam seed words
        List<String> seedWordsUnknown = new ArrayList<String>();
        String seedW = null;
        Integer wordId = 0;
        int pol = 0;
        
         String line;
         String[] fields = null;
         while ((line = in.readLine()) != null) {
            // Se linha comentada, ignora-a
            if (line.matches("^\\s{0,}(#|/\\*|//).{0,}") || line.trim().isEmpty()) 
            {
                continue;
            }

            fields = line.split(";|:|,");
            
            // Se não dividiu antes, tenta dividir por espaços. Atenção que os dois splits separados
            // são diferentes de realizar um unico split: line.split("\\s+|;|:|,");
            if (fields == null || fields.length == 0) {
                fields = line.split("\\s+");
            }

            if (fields.length >= 2) {
                seedWordLines++;
                seedW = fields[0].trim();
                pol = Integer.valueOf(fields[1].trim());
               
                if(pol > 0)
                    this.posSeedWords.add(seedW);
                else if(pol < 0)
                    this.negSeedWords.add(seedW);
                else if(pol == 0)
                    this.neuSeedWords.add(seedW);
                else 
                    System.out.println("AVISO: Polaridade não está a ser dada por um número.");
                
                this.seedWords.add(seedW);
                this.seedPolarity.add(pol);
            } else {
                System.out.println("AVISO: Ficheiro SeedWords com conteúdo num formato inválido "
                        + "[ " + line + "]."
                        + "Formato válido: palavra, polaridade");
            }
        }
    }
    
    
    /**
     * Devolve sempre uma palavra ainda não devolvida anteriormente.
     * 
     * @return null se já não existem mais palavraspara serem devolvidas.
     */
    public String getRandomPosWord() {
        return getRandomWord(this.posSeedWords, null);
    }
 
   // Devolve sempre uma palavra ainda não devolvida antes
    public String getRandomNegWord() {
        return getRandomWord(this.negSeedWords, null);
    }
  
   // Devolve sempre uma palavra ainda não devolvida antes
    public String getRandomNeuWord() {
        return getRandomWord(this.neuSeedWords, null);
    }
    
        
    public String getRandomPosWord(LoaderPAPEL papel) {
        return getRandomWord(this.posSeedWords, papel);
    }
 
   // Devolve sempre uma palavra ainda não devolvida antes
    public String getRandomNegWord(LoaderPAPEL papel) {
        return getRandomWord(this.negSeedWords, papel);
    }
  
   // Devolve sempre uma palavra ainda não devolvida antes
    public String getRandomNeuWord(LoaderPAPEL papel) {
        return getRandomWord(this.neuSeedWords, papel);
    }
    
    public String getSequencialPosWord() {
        return getSequencialWord(this.posSeedWords);
    }
 
   // Devolve sempre uma palavra ainda não devolvida antes
    public String getSequencialNegWord() {
        return getSequencialWord(this.negSeedWords);
    }
  
   // Devolve sempre uma palavra ainda não devolvida antes
    public String getSequencialNeuWord() {
        return getSequencialWord(this.neuSeedWords);
    }
         
    // Permite obter todas as seed words lidas do ficheiro. 
    // Depois de obtidas estas seedwords, deve-se chamar o método para obter todas as polaridades.
    public List<String> getAllSeedWords() {
        return this.seedWords;
    }
    public List<Integer> getAllSeedWordsPolarity() {
        return this.seedPolarity;
    }
        
    private String getRandomWord(List<String> seedWords, LoaderPAPEL papel) {
        if(seedWords == null || seedWords.size() == 0)
            return null;
        
        int index = -1;
        String word = null;
        while(seedWords.size()>0) {
            index = showRandomInteger(0, seedWords.size()-1, new Random());
            word = seedWords.get(index);
            seedWords.remove(index);
            if(papel != null) {
                if(papel.getWordId(word) != null)
                    break;
            } else
                break;
        }
        
        return word;   
    }
    
    private String getSequencialWord(List<String> seedWords) {
        if(seedWords == null || seedWords.size() == 0)
            return null;
                
        String word = seedWords.get(0);
        seedWords.remove(0);

        return word;   
    }
    
    
    private static int showRandomInteger(int aStart, int aEnd, Random aRandom) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        //System.out.println("Generated : " + randomNumber);
        return randomNumber;
    }
}
