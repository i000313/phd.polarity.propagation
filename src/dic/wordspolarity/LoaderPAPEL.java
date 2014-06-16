
package dic.wordspolarity;

import dic.wordspolarity.graphbook.In;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author PSantos
 */
public class LoaderPAPEL {

    private In in;                      // File that contains the triples 
    private int linhas_triplos_ignorados;   // Número de linhas que não correspondem a triplos carregados
    private int linhas_nao_carregadas;  // Linhas não carregadas. Acontece, por exemplo, quando a relação não é para ser carregada
    // Dados de entrada (triplos da forma: palavra1 relação palavra2)
    private List<String> noOrigem = new ArrayList<String>();
    private List<String> noDestino = new ArrayList<String>();
    private List<String> relacao = new ArrayList<String>();
    // Mapa entre "palavras distintas" e "Ids". E entre "relacoes distintas" e "Ids"
    private Hashtable<String, Integer> words_id_map = new Hashtable<String, Integer>();
    private List<String> words_inverted_index = new ArrayList<String>();
    private Hashtable<String, Integer> relations_id_map = new Hashtable<String, Integer>();
    private List<String> relations_inverted_index = new ArrayList<String>();
    private int[] num_triples_by_reltion = new int[50]; // Assume que nunca existiram mais do que 50 tipos de relações
    private int numDistinctWords = 0;
    private int distinctRelations = 0;
    private List<String> words_classe_gramatical = new ArrayList<String>();
    private int numMaxWordsCom2OuMaisClassesGram = 0;

    private static final int SYN_REL = 1;
    private static final int ANT_REL = 2;
    private static final int PEJ_REL = 3;
    
    public LoaderPAPEL(String fileToLoad) {
        this.in = new In(fileToLoad);

        loadTriples();
        mapWordsAndRelationsOnIds();
    }

    /**
     * Carrega os triplos de ficheiro para 3 ArrayList
     */
    private void loadTriples() {
        this.linhas_triplos_ignorados = 0;
        this.linhas_nao_carregadas = 0;
        String line = null;
        String[] fields = null;

        while ((line = in.readLine()) != null) {
            fields = line.split("\\s+");
            if (allowedRelation(fields[1])) {
                if (fields.length == 3 || (fields.length == 4  /*&& !fields[3].matches(".*(irón|fig|pej).*")*/)) {
                    this.noOrigem.add(fields[0]);  // Palavra Destino (palavra_definição)
                    this.noDestino.add(fields[2]); // Palavra Origem (palavra_definida)
                    
                   if(fields.length == 4 && fields[3].matches(".*(irón).*")) // troca a relação por Antónimo
                        fields[1] = fields[1] + "_ANT";
                   else 
                       if(fields.length == 4 && fields[3].matches(".*(pej).*")) // troca a relação
                        fields[1] = fields[1] + "_PEJ";
                   else
                      if(fields.length == 4 && fields[3].matches(".*(depr).*")) // troca a relação
                        fields[1] = fields[1] + "_DEPR";     

                   this.relacao.add(fields[1]);
                    //System.out.printf("%s %s %s%n", fields[0], fields[1], fields[2]);
                } //else if(fields.length == 4 && fields[3].matches(".*irón.*")) {
                 //   System.out.print("OK");
               // } 
            else {
                    this.linhas_triplos_ignorados++;
                }
            } else {
                this.linhas_nao_carregadas++;
            }
        }
    }

    /**
     * Indica quais as relações que podem passar, para que posteriormente façam
     * parte do grafo, usado para efectuar a propagação.
     * 
     * @param rel_name
     * @return 
     */
    private boolean allowedRelation(String rel_name) {
            if(rel_name.trim().toUpperCase().matches("SIN.*") || 
                    rel_name.trim().toUpperCase().matches("SYN.*") ||
                    rel_name.trim().toUpperCase().matches("ANT.*"))
                return true; 
//            if(rel_name.trim().matches(".*REFERENTE_A")) //PROPRIEDADE_DE_ALGO_REFERENTE_A
//                return true; 
//                        if(rel_name.trim().matches(".*_DO_QUE")) //PROPRIEDADE_DO_QUE
//                return true; 
            return false;
    }
    
    /**
     * Carrega os triplos dos 3 ArrayList para a HashTable.
     * A intenção é obter um id que vai de [0 ao número distinto de palavra-1] e obter
     * também um id para cada relação que vai de [0 ao número distinto de relações-1].
     * 
     */
    private void mapWordsAndRelationsOnIds() {
        this.numDistinctWords = 0;
        this.distinctRelations = 0;

        Integer word_id1 = null;
        Integer word_id2 = null;
        Integer rel_iddd = null;
        String word = null;
        String relName = null;
        String classeGramatical = null;
        String classeGramaticalExistente = null;

        // Para cada triplo
        for (int i = 0; i < this.noOrigem.size(); i++) {

            relName = this.relacao.get(i);

            word = this.noOrigem.get(i);
            word_id1 = this.words_id_map.get(word);

            if (word_id1 == null) {
                this.words_id_map.put(word, this.numDistinctWords);
                this.words_inverted_index.add(this.numDistinctWords, word);

                classeGramatical = getClasseGramatica(relName, 1);
                this.words_classe_gramatical.add(this.numDistinctWords, classeGramatical); // 

                this.numDistinctWords++;
            } else {
                // Antes de inserir classe gramatical da palavra, verifica se palavra já 
                // tinha alguma classe gramatical inserida enteriormente. Se tiver concatena.
                classeGramatical = getClasseGramatica(relName, 1);

                classeGramaticalExistente = this.words_classe_gramatical.get(word_id1);
                if (!classeGramaticalExistente.matches(".*" + classeGramatical + ".*")) {
                    classeGramatical = classeGramaticalExistente + "," + classeGramatical;

                    this.words_classe_gramatical.remove((int) word_id1);
                    this.words_classe_gramatical.add(word_id1, classeGramatical);
                    this.numMaxWordsCom2OuMaisClassesGram++;
                }


            }



            word = this.noDestino.get(i);
            word_id2 = this.words_id_map.get(word);
            if (word_id2 == null) {
                this.words_id_map.put(word, this.numDistinctWords);
                this.words_inverted_index.add(this.numDistinctWords, word);

                classeGramatical = getClasseGramatica(relName, 2);
                this.words_classe_gramatical.add(this.numDistinctWords, classeGramatical); // 

                this.numDistinctWords++;
            } else {
                // Antes de inserir classe gramatical da palavra, verifica se palavra já 
                // tinha alguma classe gramatical inserida enteriormente. No caso de ter, concatena.
                classeGramatical = getClasseGramatica(relName, 2);

                classeGramaticalExistente = this.words_classe_gramatical.get(word_id2);
                if (!classeGramaticalExistente.matches(".*" + classeGramatical + ".*")) {
                    classeGramatical = classeGramaticalExistente + "," + classeGramatical;
                    this.words_classe_gramatical.remove((int) word_id2);
                    this.words_classe_gramatical.add(word_id2, classeGramatical);
                    this.numMaxWordsCom2OuMaisClassesGram++;
                }
            }

            rel_iddd = relations_id_map.get(relName);
            if (rel_iddd == null) {
                this.relations_id_map.put(this.relacao.get(i), this.distinctRelations);
                this.relations_inverted_index.add(this.distinctRelations, this.relacao.get(i));
                this.num_triples_by_reltion[this.distinctRelations] = 1;
                this.distinctRelations++;
            } else {
                this.num_triples_by_reltion[rel_iddd]++;
            }
        }
    }

    public int getNumDistinctOfWords() {
        return this.numDistinctWords;
    }

    public int getNumMaxWordsComMaisQue1ClasseGramatical() {
        return this.numMaxWordsCom2OuMaisClassesGram;
    }

    public int getNumLoadedTriples() {
        return (this.noOrigem != null) ? this.noOrigem.size() : 0;
    }

    public int getNumLinhasIgnoradas() {
        return this.linhas_nao_carregadas;
    }
    
    public int getNumTriplosIgnorados() {
        return this.linhas_triplos_ignorados;
    }

    // Primeira palavra do triplo. Index = [0, numTriplos-1]
    public int getIdWordOrigenFromTriple(int index) {
        return (this.noOrigem != null)
                ? this.words_id_map.get(this.noOrigem.get(index)) : -1;
    }

    // Segunda palavra do triplo. Index = [0, numTriplos-1]
    public int getIdWordDestinFromTriple(int index) {
        return (this.noDestino != null)
                ? this.words_id_map.get(this.noDestino.get(index)) : -1;
    }

    // Relação do triplo. Index = [0, numTriplos-1]
    public int getIdRelationFromTriple(int index) {
        return (this.relacao != null)
                ? this.relations_id_map.get(this.relacao.get(index)) : -1;
    }
    
    public int getRelationType(int index) {
        if(this.relacao.get(index).matches(".*_ANT") || this.relacao.get(index).matches("ANT.*"))
            return ANT_REL;
        else if(this.relacao.get(index).matches(".*_PEJ.*") || this.relacao.get(index).matches(".*_DEPR.*"))
            return PEJ_REL;
        else
            return SYN_REL; //ANT_REL;
    }

    // Dada uma palavra devolve o seu id
    public Integer getWordId(String word) {
        return (this.words_id_map != null) ? this.words_id_map.get(word) : null;
    }

    // p
    public String getWordById(int wordId) {
        return (this.words_inverted_index != null)
                ? this.words_inverted_index.get(wordId) : null;
    }

    public String getClasseGramatical(int wordId) {
        return (this.words_inverted_index != null)
                ? this.words_classe_gramatical.get(wordId) : null;
    }

    public boolean isAdj(int wordId) {
        return getClasseGramatical(wordId).matches(".*adj.*");
    }
 
    public boolean isAdv(int wordId) {
        return getClasseGramatical(wordId).matches(".*adv.*");
    }
    
   public boolean isNom(int wordId) {
        return getClasseGramatical(wordId).matches(".*nom.*");
    }
   
    public boolean isVerb(int wordId) {
        return getClasseGramatical(wordId).matches(".*vrb.*");
    }
    
    public boolean isOtherGramticalClass(int wordId) {
        return getClasseGramatical(wordId).matches(".*unk.*");
    }
    

    
    /**
     * Devolve a categoria gramatical da palavra, com base no nome da relação
     * e posição da palavra .
     * 
     * @param relationName relações conhecidos pelo PAPEL
     * @param wordPos 1 ou 2 de acordo com a ordem que aparece no triplo.
     * @return 
     */
    private String getClasseGramatica(String relationName, int wordPos) {

        // Relações SINONIMIA
        if (relationName.trim().toUpperCase().matches("SINONIMO_ADJ_.*")) { // Assim admite: SINONIMO_ADJ_DE e SINONIMO_ADJ_DE_PEJ (PEJORATIVO)
            return "adj";
        } else if (relationName.trim().toUpperCase().matches("SINONIMO_ADV_.*")) {
            return "adv";
        } else if (relationName.trim().toUpperCase().matches("SINONIMO_N_.*")) {
            return "nom";
        } else if (relationName.trim().toUpperCase().matches("SINONIMO_V_.*")) {
            return "vrb";
        } // Relações REFERENTE
        else if (relationName.trim().toUpperCase().matches("PROPRIEDADE_DE_ALGO_REFERENTE_A")) {
            return (wordPos == 1 ? "adj" : "nom");
        } else if (relationName.trim().toUpperCase().matches("PROPRIEDADE_DO_QUE")) {
            return (wordPos == 1 ? "adj" : "vrb");
        } else {
            return "unk";
        }
    }

    public void printRelations() {
        System.out.println("------ TRIPLES INFO ------");
        System.out.printf("%-25s : %6d %n", "Number of distinct relations", this.distinctRelations);

        if (this.relations_inverted_index != null) {
            for (int i = 0; i < this.relations_inverted_index.size(); i++) {
                System.out.printf("%2d %-25s : %6d %n", (i+1), this.relations_inverted_index.get(i),
                                this.num_triples_by_reltion[i]);
            }
        }
        
        System.out.println("Num. Loaded triples          : " + getNumLoadedTriples());
        System.out.println("Num. Ignored triples         : " + getNumTriplosIgnorados());
        System.out.println("Num. Ignored relations       : " + getNumLinhasIgnoradas());    
        System.out.println("Num. TOTAL Lines             : " + (getNumLoadedTriples() + getNumTriplosIgnorados() + getNumLinhasIgnoradas()));
        System.out.println("Num. Distinct words          : " + getNumDistinctOfWords());
        System.out.println("Num. Palavras > 1 cat.Gram.  : " + getNumMaxWordsComMaisQue1ClasseGramatical());
        System.out.println("------ End triples INFO ------");
    }

    public void printTEMP() {
        Enumeration e = words_id_map.keys();

        while (e.hasMoreElements()) {
            System.out.println(e.nextElement());
        }
    }
}
