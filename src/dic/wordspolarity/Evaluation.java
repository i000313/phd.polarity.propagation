/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dic.wordspolarity;

import dic.wordspolarity.graphbook.In;
import java.util.Hashtable;

/**
 *
 * @author PSantos
 */
public class Evaluation {

    private int[] resultClassification; // 
    private int unknown;
    private int wordsPolNull; // Palavras que não receberam qualquer polaridade 
    private int wordsWithPol; // Palavras com que receberam polaridade
    private int wordsNotInGoldCol; // Palavras que receberam polridade, mas não aparecem na goldColection
    public int[][] resStats; // Matrix 4x4. Significado Linhas e Colunas: +, -, 0, A 
    private int[][] resStatsAdj;
    private int[][] resStatsAdv;
    private int[][] resStatsNom;
    private int[][] resStatsVrb;
    public int[][] resStatsUnk; // palavras que não são ou não conseguiu determinar se são: adj, adv, nom, nem Vrb
    private static final int COL_POS = 0;
    private static final int COL_NEG = 1;
    private static final int COL_NEU = 2;
    private static final int COL_AMB = 3;
    private static final int LINE_POS = 0;
    private static final int LINE_NEG = 1;
    private static final int LINE_NEU = 2;
    private static final int LINE_AMB = 3;
    private boolean ignore1stLineFromFile = false; // ficheiro que contém as avalições
    // Resultados
    public float accuracyAllMatrix; // Accuracy considerando todas as classes (+,-,0,Amb) e valores que aparecem na matriz.
    public float accuracyIgnoreAmb; // Accuracy não consideranto a classe ambigua (considera: +,-,0).
    public float accuracyMergePosNeutralIgnoreAmb; // Accuracy juntando a classe positiva e neutra, ignorando a classe ambigua (considera: +,-).   
    public int[] countWordsByIt = new int[50]; // Número de palavras avaliadas, por iteração
    public int[] countWordsRightByIt = new int[50]; // Número de palavras avaliadas e correctas, por iteração    
    public int[] countWordsByIt2MergMatrix = new int[50]; // Número de palavras avaliadas, por iteração
    public int[] countWordsRightByIt2MergMatrix = new int[50]; // Número de palavras avaliadas e correctas, por iteração

    /**
     * 
     * @param papel
     * @param pos
     * @param neg
     * @param it
     * @param gold_col 
     *      Hashtable<String, Integer> gold_col = new Hashtable<String, Integer>();
     *      gold_col.put("A", 1);
     *      gold_col.put("B", -1);
     *      gold_col.put("C", 1);
     *      gold_col.put("D", 0);
     *      gold_col.put("E", -1);
     */
//    public Evaluation(LoaderPAPEL papel, int[] pos, int[] neg, int[] it,
//            Hashtable<String, Integer> gold_col) {
//        
//        evaluate(papel, pos, neg, it, gold_col);
//    }
//
//    public Evaluation(LoaderPAPEL papel, int[] pos, int[] neg, int[] it,
//            String gold_colection_file) {
//        
//        Hashtable<String, Integer> gold_col = loadGoldColectionFomFile(gold_colection_file);
//        evaluate(papel, pos, neg, it, gold_col);
//
//    }
    public Evaluation(LoaderPAPEL papel, EdgeWeightedGraph G, boolean evalAsEPIA2011, int[] pos, int[] neg, int[] neu, int[] it,
            String gold_colection_file) {

        Hashtable<String, Integer> gold_col = loadGoldColectionFomFile(gold_colection_file);
        if (gold_col == null || gold_col.size() == 0) {
            System.out.println("AVISO: Verificar formato do ficheiro. Ficheiro não carregado: " + gold_colection_file);
        }
        if (evalAsEPIA2011) // Avaliação ao estilo EPIA
        {
            evaluateEPIA(papel, pos, neg, neu, it, gold_col);
        } else // Avaliação ao estilo PROPOR
        {
            evaluate(papel, G, pos, neg, neu, it, gold_col);
        }

    }

    private Hashtable<String, Integer> loadGoldColectionFomFile(String gold_colection_file) throws NumberFormatException {
        In in = new In(gold_colection_file);
        Hashtable<String, Integer> gold_col = new Hashtable<String, Integer>();
        String line = null;
        String[] fields = null;
        while ((line = in.readLine()) != null) {
            if (this.ignore1stLineFromFile) { // Ignorar a 1ª linha se esta está marcada para ser ignorada 
                this.ignore1stLineFromFile = false;
                continue;
            }

            if (line.matches("^\\s{0,}(#|/\\*|//).{0,}")) // Se linha comentada, ignora-a
            {
                continue;
            }

            fields = line.split(";|:|,");
            // Se não dividiu antes, tenta dividir por espaços. Atenção que os dois splits separados
            // são diferentes de realizar um unico split: line.split("\\s+|;|:|,");
            if (fields == null || fields.length == 0) {
                fields = line.split("\\s+");
            }

            // Verifica se ficheiro tem pelo menos dois campos
            if (fields != null && fields.length >= 2) {
                if (gold_col.get(fields[0]) == null) {
                    gold_col.put(fields[0], Integer.valueOf(fields[1].trim())); // word, polarity
                } else {
                    System.out.println("AVISO: Palavra repetida na gold col.:" + fields[0]);
                }
            }
        }
        return gold_col;
    }

//    private void evaluate(LoaderPAPEL papel, int[] pos, int[] neg, int[] it,
//            Hashtable<String, Integer> gold_col) {
//      
//        this.resStats = new int[4][4];
//        this.result = new int[papel.getNumDistinctOfWords()];
//
//        String word2eval = null;
//        Integer polarity = null;
//        for (int i = 0; i < papel.getNumDistinctOfWords(); i++) {
//            word2eval = papel.getWordById(i);
//            polarity = gold_col.get(word2eval);
//            // Palavra não aparece na colecção dourada || palavra não recebeu qualquer propagação (nó do grafo não conectado) 
//            if (polarity == null || (pos[i] == 0 && neg[i] == 0)) {
//                this.result[i] = 'U';
//                this.unknown++;
//            } else { // Palavra aparece na colecção dourada
//                switch (polarity) {
//                    case -10:  // palavra marcada como ambigua -10
//                    case -100: // palavra com significado desconhecido (-100)
//                        if (pos[i] > neg[i]) {
//                            resStats[LINE_AMB][COL_POS]++;
//                        } else if (pos[i] < neg[i]) {
//                            resStats[LINE_AMB][COL_NEG]++;
//                        } else {
//                            resStats[LINE_AMB][COL_NEU]++;
//                        }
//                        break;
//
//                    case 1:
//                        if (pos[i] > neg[i]) {
//                            resStats[LINE_POS][COL_POS]++;
//                        } else if (pos[i] < neg[i]) {
//                            resStats[LINE_POS][COL_NEG]++;
//                        } else {
//                            resStats[LINE_POS][COL_NEU]++;
//                        }
//                        break;
//
//                    case -1:
//                        if (pos[i] > neg[i]) {
//                            resStats[LINE_NEG][COL_POS]++;
//                        } else if (pos[i] < neg[i]) {
//                            resStats[LINE_NEG][COL_NEG]++;
//                        } else {
//                            resStats[LINE_NEG][COL_NEU]++;
//                        }
//                        break;
//                    case 0:
//                        if (pos[i] > neg[i]) {
//                            resStats[LINE_NEU][COL_POS]++;
//                        } else if (pos[i] < neg[i]) {
//                            resStats[LINE_NEU][COL_NEG]++;
//                        } else {
//                            resStats[LINE_NEU][COL_NEU]++;
//                        }
//                        break;
//                    default:
//                        System.out.println("AVISO: Recuurco dourado, com palavra classificada"
//                                + " de forma desconhecida. (classes conhecidas: -1,0,1,-10,-100)");
//                }
////                if(polarity == -10 || polarity == -100) {
////                    this.result[i] = 'Z'; // Palavra aparece na gold col. mas está
////                    this.other++;       // marcada como ambigua (-10) ou significado desconhecido (-100)
////                }else if (pos[i] > neg[i] && polarity == 1) {
////                    this.result[i] = 'R'; // Palavra Right
////                    this.right++;
////                } else if (pos[i] < neg[i] && polarity == -1) {
////                    this.result[i] = 'R'; // Palavra Right
////                    this.right++;
////                } else if (pos[i] == neg[i] && polarity == 0) {
////                    this.result[i] = 'R'; // Palavra Right
////                    this.right++;
////                } else {
////                    this.result[i] = 'W'; // Wrong
////                    this.wrong++;
////                }
//            }
//        }
//    }
    private void evaluate(LoaderPAPEL papel, EdgeWeightedGraph G,
            int[] pos, int[] neg, int[] neu, int[] it, Hashtable<String, Integer> gold_col) {
        this.wordsWithPol = 0;
        this.wordsPolNull = 0;
        this.wordsNotInGoldCol = 0;

        this.resStats = new int[4][4];
        this.resStatsAdj = new int[4][4];
        this.resStatsAdv = new int[4][4];
        this.resStatsNom = new int[4][4];
        this.resStatsVrb = new int[4][4];
        this.resStatsUnk = new int[4][4];
        this.resultClassification = new int[papel.getNumDistinctOfWords()];

        String word2eval = null;
        Integer polarity_human = null;
        for (int i = 0; i < papel.getNumDistinctOfWords(); i++) {
            word2eval = papel.getWordById(i);
            polarity_human = gold_col.get(word2eval);


            // Palavra não recebeu qualquer polaridade durante a propagação (nó do grafo não conectado)
            // Atenção que verificar apenas se it[i] < 0 não é suficiente, pois existem palavras com
            // it[i] > 0 e não receberam qualquer polaridade, porque o algoritmo não tinha informação para tal.
            if (pos[i] == 0 && neg[i] == 0 && neu[i] == 0) {
                this.wordsPolNull++;
                continue;
            }

            if (polarity_human == null) { // Palavra recebeu propagação, mas não aparece na colecção dourada
                this.wordsNotInGoldCol++;
                this.wordsWithPol++;
                continue;
            }

            //
            // Para se chegar aqui, está-se perante palavra que recebeu uma
            // (ou mais) poladirades durante propagação e aparece na colecção dourada.
            //
            this.wordsWithPol++;

            //if(it[i] > 4) continue;

            int line2Fill = LINE_AMB;
            switch (polarity_human) {
                case -10:  // palavra marcada como ambigua -10
                case -100: // palavra com significado desconhecido (-100)
                    line2Fill = LINE_AMB;
                    break;
                case 1:
                    line2Fill = LINE_POS;
                    break;
                case -1:
                    line2Fill = LINE_NEG;
                    break;
                case 0:
                    line2Fill = LINE_NEU;
                    break;
                default:
                    System.out.println("AVISO: Recurso dourado, com palavra classificada"
                            + " de forma desconhecida. (classes conhecidas: -1,0,1,-10,-100)");
                    continue;
            }

            // Determina a polaridade da palavra com base nos seus contadores
            int[] res = findMax(pos[i], neg[i], neu[i]);
            int aux2printOnly = -100;
            int col2fill = COL_AMB;
            switch (res[0]) {
                case 1:
                    col2fill = COL_POS;
                    aux2printOnly = 1;
                    break;
                case 2:
                    col2fill = COL_NEG;
                    aux2printOnly = -1;
                    break;
                case 3:
                    col2fill = COL_NEU;
                    aux2printOnly = 0;
                    break;
                case 4:
                    col2fill = COL_AMB;
                    aux2printOnly = -10;
                    break;
            }

            if (papel.isAdj(i)) {
                resStatsAdj[line2Fill][col2fill]++;
            }
            if (papel.isNom(i)) {
                resStatsNom[line2Fill][col2fill]++;
            }
            if (papel.isVerb(i)) {
                resStatsVrb[line2Fill][col2fill]++;
            }
            if (papel.isVerb(i)) {
                resStatsAdv[line2Fill][col2fill]++;
            }
            if (papel.isOtherGramticalClass(i)) {
                resStatsUnk[line2Fill][col2fill]++;
            }

            resStats[line2Fill][col2fill]++;
//            switch (res[0]) {
//                case 1:
//                    resStats[line2Fill][COL_POS]++;
//                    right0 = (line2Fill == LINE_POS);
//                    right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
//                    break;
//                case 2:
//                    resStats[line2Fill][COL_NEG]++;
//                    right0 = (line2Fill == LINE_NEG);
//                    right = (line2Fill == LINE_NEG);
//                    //System.out.println("KIKI:" + i);
//                    break;
//                case 3:
//                    resStats[line2Fill][COL_NEU]++;
//                    right0 = (line2Fill == LINE_NEU);
//                    right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
//                    //System.out.printf("KIKI: %d %d %d %n", pos[i], neg[i], neu[i]);
//                    break;
//                case 4:
//                    resStats[line2Fill][COL_AMB]++;
//                    right0 = (line2Fill == LINE_AMB);
//                    //right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
//                    //System.out.printf("AMB: %d %d %d %n", pos[i], neg[i], neu[i]);
//                    break;
//            }
            this.resultClassification[i] = line2Fill * 10 + res[0];

//            //wordId;Word;PoS;Human;Machine;Right-Wrong;#+;#-;#0;It.;NodeDegree
//            System.out.println(i + ";" + word2eval + ";" + papel.getClasseGramatical(i) + ";"
//                    + polarity_human + ";"
//                    + aux2printOnly + ";"
//                    + (line2Fill == col2fill ? "r" : "w")
//                    + ";" + pos[i]
//                    + ";" + neg[i]
//                    + ";" + neu[i]
//                    + ";" + it[i] 
//                    + ";" + G.getInputOutputDegree(i));

            boolean right0 = false;
            boolean right = false; // Var que apenas serve para auxiliar a contagem de classificações correctas

            switch (col2fill) {
                case COL_POS:
                    right0 = (line2Fill == LINE_POS);
                    right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
                    break;

                case COL_NEG:
                    right0 = (line2Fill == LINE_NEG);
                    right = (line2Fill == LINE_NEG);
                    break;

                case COL_NEU:
                    right0 = (line2Fill == LINE_NEU);
                    right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
                    break;

                case COL_AMB:
                    right0 = (line2Fill == LINE_AMB);
                    //right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
                    //System.out.printf("AMB: %d %d %d %n", pos[i], neg[i], neu[i]);
                    break;
            }

            // Conta o número de palavras avaliadas, por iteração
            if (res[0] != 4) {
                this.countWordsByIt[it[i]]++;
                this.countWordsByIt2MergMatrix[it[i]]++;
            }

            if (right0) {
                this.countWordsRightByIt[it[i]]++;
            }

            if (right) {
                this.countWordsRightByIt2MergMatrix[it[i]]++;
            }

        }
    }

    private void evaluateEPIA(LoaderPAPEL papel, int[] pos, int[] neg, int[] neu, int[] it,
            Hashtable<String, Integer> gold_col) {
        this.wordsWithPol = 0;
        this.wordsPolNull = 0;
        this.wordsNotInGoldCol = 0;

        this.resStats = new int[4][4];
        this.resultClassification = new int[papel.getNumDistinctOfWords()];

        String word2eval = null;
        Integer polarity = null;
        for (int i = 0; i < papel.getNumDistinctOfWords(); i++) {
            word2eval = papel.getWordById(i);
            polarity = gold_col.get(word2eval);


            // Palavra não recebeu qualquer propagação (nó do grafo não conectado)
            // Outra forma de verificar, talvez: pos[i] == 0 && neg[i] == 0 && neu[i] == 0
            if (it[i] < 0) {
                this.wordsPolNull++;
            } else if (polarity == null) { // Palavra recebeu propagação, mas não aparece na colecção dourada
                this.wordsNotInGoldCol++;
                this.wordsWithPol++;
            } else { // Palavra aparece na colecção dourada
                this.wordsWithPol++;

                //if(it[i] > 4) continue;

                int line2Fill = LINE_AMB;
                switch (polarity) {
                    case -10:  // palavra marcada como ambigua -10
                    case -100: // palavra com significado desconhecido (-100)
                        line2Fill = LINE_AMB;
                        break;
                    case 1:
                        line2Fill = LINE_POS;
                        break;
                    case -1:
                        line2Fill = LINE_NEG;
                        break;
                    case 0:
                        line2Fill = LINE_NEU;
                        break;
                    default:
                        System.out.println("AVISO: Recurso dourado, com palavra classificada"
                                + " de forma desconhecida. (classes conhecidas: -1,0,1,-10,-100)");
                        continue;
                }

                int[] res = findMax(pos[i], neg[i], neu[i]);
                boolean right0 = false;
                boolean right = false; // Var que apenas serve para auxiliar a contagem de classificações correctas
                switch (res[0]) {
                    case 1:
                        resStats[line2Fill][COL_POS]++;
                        right0 = (line2Fill == LINE_POS);
                        right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
                        break;
                    case 2:
                        resStats[line2Fill][COL_NEG]++;
                        right0 = (line2Fill == LINE_NEG);
                        right = (line2Fill == LINE_NEG);
                        //System.out.println("KIKI:" + i);
                        break;
                    case 3:
                        resStats[line2Fill][COL_NEU]++;
                        right0 = (line2Fill == LINE_NEU);
                        right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
                        //System.out.printf("KIKI: %d %d %d %n", pos[i], neg[i], neu[i]);
                        break;
                    case 4:
                        resStats[line2Fill][COL_NEU]++;
                        right0 = (line2Fill == LINE_AMB);
                        right = (line2Fill == LINE_POS || line2Fill == LINE_NEU);
                        //System.out.printf("AMB: %d %d %d %n", pos[i], neg[i], neu[i]);
                        break;
                }
                this.resultClassification[i] = line2Fill * 10 + res[0];

                int totalPropag = pos[i] + neg[i] + neu[i];
//                System.out.println(i + ";" + it[i]+";" + 
//                        (line2Fill==(res[0]-1) ? "r" : "w" ) +
//                        ";" + pos[i] + 
//                        ";" + neg[i] +
//                        ";" + neu[i]); 


                // Conta o número de palavras avaliadas, por iteração
                // if(res[0] != 4) {
                this.countWordsByIt[it[i]]++;
                this.countWordsByIt2MergMatrix[it[i]]++;
                // }

                if (right0) {
                    this.countWordsRightByIt[it[i]]++;
                }

                if (right) {
                    this.countWordsRightByIt2MergMatrix[it[i]]++;
                }
            }
        }
    }

    // Função Repetida
    private int[] findMax(int first, int second, int third) {
        int max = first;
        int position = 1;

        if (second > max) {
            max = second;
            position = 2;
        }
        if (third > max) {
            max = third;
            position = 3;
        }

        // Se existir mais que um máximo  
        if (position == 1 && (first == second || first == third)) {
            position = 4;
        } else if (position == 2 && (second == first || second == third)) {
            position = 4;
        } else if (position == 3 && (third == first || third == second)) {
            position = 4;
        }

        int[] ret = new int[2];
        ret[0] = position;
        ret[1] = max;
        return ret;
    }

   
     
     
     
    public float precisionPosIgnoreAmb() {
        float temp =  (float)this.resStats[LINE_POS][COL_POS]/
               (this.resStats[LINE_POS][COL_POS] + 
                this.resStats[LINE_NEG][COL_POS] + 
                this.resStats[LINE_NEU][COL_POS] ) ; 
        return temp;
    }
     
   
     public float precisionNegIgnoreAmb() {
        return (float)this.resStats[LINE_NEG][COL_NEG]/
               (this.resStats[LINE_POS][COL_NEG] + 
                this.resStats[LINE_NEG][COL_NEG] + 
                this.resStats[LINE_NEU][COL_NEG] ) ; 
    }
      
    public float precisionNeuIgnoreAmb() {
        return (float)this.resStats[LINE_NEU][COL_NEU]/
               (this.resStats[LINE_POS][COL_NEU] + 
                this.resStats[LINE_NEG][COL_NEU] + 
                this.resStats[LINE_NEU][COL_NEU]) ; 
    }
 
 public float recallPosIgnoreAmb() {

    float pos_pos = this.resStats[LINE_POS][COL_POS];
    float pos_neg =this.resStats[LINE_POS][COL_NEG] ;
    float pos_neu = this.resStats[LINE_POS][COL_NEU];
    float temp = pos_pos / (pos_pos + pos_neg + pos_neu);
         /*(float)this.resStats[LINE_POS][COL_POS]/
               (this.resStats[LINE_POS][COL_POS] + 
                this.resStats[LINE_POS][COL_NEG] + 
                this.resStats[LINE_POS][COL_NEU] ) ; */
     return temp;
    }  
     
 public float recallNegIgnoreAmb() {
        return (float)this.resStats[LINE_NEG][COL_NEG]/
               (this.resStats[LINE_NEG][COL_POS] + 
                this.resStats[LINE_NEG][COL_NEG] + 
                this.resStats[LINE_NEG][COL_NEU] ) ; 
    }  
 
  public float recallNeuIgnoreAmb() {
        return (float)this.resStats[LINE_NEU][COL_NEU]/
               (this.resStats[LINE_NEU][COL_POS] + 
                this.resStats[LINE_NEU][COL_NEG] + 
                this.resStats[LINE_NEU][COL_NEU] ) ; 
    }
  
   public float f1_measurePosIgnoreAmb() {
        return 2 * (precisionPosIgnoreAmb() * recallPosIgnoreAmb() 
                    /
                (precisionPosIgnoreAmb() + recallPosIgnoreAmb()) ) ; 
    }
  
    public float f1_measureNegIgnoreAmb() {
        return 2 * (precisionNegIgnoreAmb() * recallNegIgnoreAmb() 
                    /
                (precisionNegIgnoreAmb() + recallNegIgnoreAmb()) ) ; 
    }
    
   public float f1_measureNeuIgnoreAmb() {
        return 2 * (precisionNeuIgnoreAmb() * recallNeuIgnoreAmb() 
                    /
                (precisionNeuIgnoreAmb() + recallNeuIgnoreAmb()) ) ; 
    }
   
   //
   // Medidas Com base na Iteração 
   //
   public float accuracyAtIteration(int iteration) {
        int totalTemp = 0;
        int totalRight = 0;
        
        for (int i = 0; i < countWordsByIt.length && i <= iteration; i++) {
            if (countWordsByIt[i] <= 0) {
                continue;
            }

            totalTemp += countWordsByIt[i];
            totalRight += countWordsRightByIt[i];
//            System.out.printf("%2d, %6d | %10d  %8d (%.2f) %n",
//                    i,
//                    countWordsByIt[i], totalTemp, totalRight,
//                    ((float) totalRight / totalTemp) * 100);

        }    
        return ((float) totalRight / totalTemp);
   }
    /**
     * Imprime uma matriz e devolve a sua accuracy, que é calculada por:
     * total diagonal / total matrix
     * @param resMatrix
     * @return 
     */
    private float[] printMatrix(int[][] resMatrix) {
        
        int totalMatrix = 0;
        int totalDiag = 0;
        int totalOutDiag = 0;
        int totalMatrixIgnoreAmb = 0;
        for (int line = 0; line < resMatrix.length; line++) {
            for (int col = 0; col < resMatrix[line].length; col++) {
                totalMatrix += resMatrix[line][col];

                if (line == col) {
                    totalDiag += resMatrix[line][col];
                } else {
                    totalOutDiag += resMatrix[line][col];
                }

                if (line != LINE_AMB && col != COL_AMB) {
                    totalMatrixIgnoreAmb += resMatrix[line][col];
                }
            }
        }
            
        System.out.println("     --- Machine --- ");
        System.out.println("|    POS NEG NEU AMB");
        System.out.printf("M  + %3d %3d %3d %3d%n", resMatrix[LINE_POS][COL_POS], resMatrix[LINE_POS][COL_NEG], resMatrix[LINE_POS][COL_NEU], resMatrix[LINE_POS][COL_AMB]);
        System.out.printf("A  - %3d %3d %3d %3d%n", resMatrix[LINE_NEG][COL_POS], resMatrix[LINE_NEG][COL_NEG], resMatrix[LINE_NEG][COL_NEU], resMatrix[LINE_NEG][COL_AMB]);
        System.out.printf("N  0 %3d %3d %3d %3d%n", resMatrix[LINE_NEU][COL_POS], resMatrix[LINE_NEU][COL_NEG], resMatrix[LINE_NEU][COL_NEU], resMatrix[LINE_NEU][COL_AMB]);
        System.out.printf("|  A %3d %3d %3d %3d%n", resMatrix[LINE_AMB][COL_POS], resMatrix[LINE_AMB][COL_NEG], resMatrix[LINE_AMB][COL_NEU], resMatrix[LINE_AMB][COL_AMB]);
        
        float accuracy = ((float) totalDiag / totalMatrix);
        float accuracyIgnoreAmbigous = ((float) (totalDiag - resMatrix[LINE_AMB][COL_AMB]) / totalMatrixIgnoreAmb);

        System.out.printf("TOTAL MATRIX: %d [Diag: %d (%.4f or ignoring AMB %.4f), Out Diag: %d (%.4f)]%n",
                totalMatrix,
                totalDiag, accuracy,
                accuracyIgnoreAmbigous,
                totalOutDiag, ((float) totalOutDiag / totalMatrix));
        
        return new float[]{accuracy, accuracyIgnoreAmbigous};
    }
            
    public void printAdjectivesEvaluation() {        
        System.out.println("*** Adjectives ***");
        printMatrix(resStatsAdj);
    }
 
    public void printNomEvaluation() {        
        System.out.println("*** Names ***");
        printMatrix(resStatsNom);
    }
    
    public void printVerbsEvaluation() {        
        System.out.println("*** Verbs ***");
        printMatrix(resStatsVrb);
    }
   
    public void printAdverbsEvaluation() {        
        System.out.println("*** Adverbs ***");
        printMatrix(resStatsAdv);
    }
    public void printUnknownClassGramEvaluation() {        
        System.out.println("*** Unknown Gramatical class ***");
        printMatrix(resStatsUnk);
    } 
    
    public void printResult() {

          System.out.println("----- INI EVAL ------");  
          
//        System.out.println("    --- Machine --- ");
//        System.out.println("    POS NEG NEU AMB");
//        System.out.printf("H + %3d %3d %3d %3d%n", resStats[LINE_POS][COL_POS], resStats[LINE_POS][COL_NEG], resStats[LINE_POS][COL_NEU], resStats[LINE_POS][COL_AMB]);
//        System.out.printf("  - %3d %3d %3d %3d%n", resStats[LINE_NEG][COL_POS], resStats[LINE_NEG][COL_NEG], resStats[LINE_NEG][COL_NEU], resStats[LINE_NEG][COL_AMB]);
//        System.out.printf("  0 %3d %3d %3d %3d%n", resStats[LINE_NEU][COL_POS], resStats[LINE_NEU][COL_NEG], resStats[LINE_NEU][COL_NEU], resStats[LINE_NEU][COL_AMB]);
//        System.out.printf("  A %3d %3d %3d %3d%n", resStats[LINE_AMB][COL_POS], resStats[LINE_AMB][COL_NEG], resStats[LINE_AMB][COL_NEU], resStats[LINE_AMB][COL_AMB]);
//
        int totalMatrix = 0;
        int totalMatrixIgnoreAmb = 0;
        int totalDiag = 0;
        int totalOutDiag = 0;
//        for (int line = 0; line < resStats.length; line++) {
//            for (int col = 0; col < resStats[line].length; col++) {
//                totalMatrix += resStats[line][col];
//
//                if (line == col) {
//                    totalDiag += resStats[line][col];
//                } else {
//                    totalOutDiag += resStats[line][col];
//                }
//
//                if (line != LINE_AMB && col != COL_AMB) {
//                    totalMatrixIgnoreAmb += resStats[line][col];
//                }
//            }
//        }
//
//        this.accuracyAllMatrix = ((float) totalDiag / totalMatrix);
//        this.accuracyIgnoreAmb = ((float) (totalDiag - resStats[LINE_AMB][COL_AMB]) / totalMatrixIgnoreAmb);
//
//        System.out.printf("TOTAL MATRIX: %d [Diag: %d (%.4f or ignoring AMB %.4f), Out Diag: %d (%.4f)]%n",
//                totalMatrix,
//                totalDiag, this.accuracyAllMatrix,
//                this.accuracyIgnoreAmb,
//                totalOutDiag, ((float) totalOutDiag / totalMatrix));

        System.out.println("*** Overall Matrix ***");
        float[] temp = printMatrix(resStats);
        this.accuracyAllMatrix = temp[0];
        this.accuracyIgnoreAmb = temp[1];

        if (this.wordsWithPol > 0) { // Verifica se esta var e relacionadas foram usadas.
            System.out.println("WORDS with Pol.: " + this.wordsWithPol + ". Destas, foram avaliadas: *VER TOTAL MATRIX* Não avaliadas (not in GoldCol.): " + this.wordsNotInGoldCol);
            System.out.println("Words witout Pol.: " + this.wordsPolNull);
        }

        //
        // Merging da classe positiva e neutra
        //
        int[][] mergedMatrix = new int[2][2];
        int ignoredWords = 0; // Palavras ignoradas na avaliação, por estarem marcadas pelos anotadores como ambiguas
        // Line POS, col POS
        mergedMatrix[LINE_POS][COL_POS] = resStats[LINE_POS][COL_POS] + resStats[LINE_POS][COL_NEU] + resStats[LINE_NEU][COL_POS] + resStats[LINE_NEU][COL_NEU];
        mergedMatrix[LINE_POS][COL_NEG] = resStats[LINE_POS][COL_NEG] + resStats[LINE_NEU][COL_NEG];
        mergedMatrix[LINE_NEG][COL_POS] = resStats[LINE_NEG][COL_POS] + resStats[LINE_NEG][COL_NEU];
        mergedMatrix[LINE_NEG][COL_NEG] = resStats[LINE_NEG][COL_NEG];

        ignoredWords = resStats[LINE_AMB][COL_POS] + resStats[LINE_AMB][COL_NEG] + resStats[LINE_AMB][COL_NEU]
                + resStats[LINE_AMB][COL_AMB]
                + resStats[LINE_POS][COL_AMB] + resStats[LINE_NEG][COL_AMB] + resStats[LINE_NEU][COL_AMB];
        
        System.out.println("Resultados por Iteração (ignoring AMB):");
        printWordsEvaluatedByIt(this.countWordsByIt);

        System.out.println("----------------------");
        System.out.println("MERGED MATRIX (Palavras ambiguas ignoradas: " + ignoredWords + "): ");
        System.out.println("   +   -");
        System.out.printf("+ %3d %3d%n", mergedMatrix[LINE_POS][COL_POS], mergedMatrix[LINE_POS][COL_NEG]);
        System.out.printf("- %3d %3d%n", mergedMatrix[LINE_NEG][COL_POS], mergedMatrix[LINE_NEG][COL_NEG]);

        totalMatrix = mergedMatrix[LINE_POS][COL_POS] + mergedMatrix[LINE_POS][COL_NEG]
                + mergedMatrix[LINE_NEG][COL_POS] + mergedMatrix[LINE_NEG][COL_NEG];
        totalDiag = mergedMatrix[LINE_POS][COL_POS] + mergedMatrix[LINE_NEG][COL_NEG];
        totalOutDiag = mergedMatrix[LINE_POS][COL_NEG] + mergedMatrix[LINE_NEG][COL_POS];

        this.accuracyMergePosNeutralIgnoreAmb = ((float) totalDiag / totalMatrix);
        System.out.printf("TOTAL MATRIX: %d [Diag: %d (%.4f), Out Diag: %d (%.4f)]%n",
                totalMatrix,
                totalDiag, this.accuracyMergePosNeutralIgnoreAmb,
                totalOutDiag, ((float) totalOutDiag / totalMatrix));


        // Mostra o número de palavras avaliadas por iteracção
//        totalTemp = 0;
//        totalRight = 0;
//        System.out.println(this);
//        for (int i = 0; i < this.countWordsByIt2MergMatrix.length; i++) {
//            if (this.countWordsByIt2MergMatrix[i] <= 0) {
//                continue;
//            }
//
//            totalTemp += this.countWordsByIt2MergMatrix[i];
//            totalRight += this.countWordsRightByIt2MergMatrix[i];
//            System.out.printf("It. %d: %d (Sub. Tot. %d | R %d) - %.2f %n",
//                    i,
//                    this.countWordsByIt2MergMatrix[i], totalTemp, totalRight,
//                    ((float) totalRight / totalTemp) * 100);
//
//        }
//        System.out.println("TOTAL: " + totalTemp);
         System.out.println("Resultados por Iteração da Merged Matrix (ignoring AMB):");
        printWordsEvaluatedByIt(this.countWordsByIt2MergMatrix);
        System.out.println("----------------------");
        
        
           //eval.printAdjectivesEvaluation();
            //eval.printNomEvaluation();            
            //eval.printUnknownClassGramEvaluation(); // Bom para ver se existem palavas para às não foram obtidas classes gramaticais.
            System.out.println("---- End EVAL ----");        
        
    }

    /**
     * Mostra o número de palavras avaliadas por iteracção
     * @param countWordsByIt 
     */
    private void printWordsEvaluatedByIt(int[] countWordsByIt) {
        
        int totalTemp = 0;
        int totalRight = 0;
        
        System.out.println("It. #Words | #TotalAcumulado #Right (%)");
        for (int i = 0; i < countWordsByIt.length; i++) {
            if (countWordsByIt[i] <= 0) {
                continue;
            }

            totalTemp += countWordsByIt[i];
            totalRight += countWordsRightByIt[i];
            System.out.printf("%2d, %6d | %10d  %8d (%.2f) %n",
                    i,
                    countWordsByIt[i], totalTemp, totalRight,
                    ((float) totalRight / totalTemp) * 100);

        }
    }
    
    public int getNumPalavrasEvaluetedIgnoreAmb() {
        return getNumPalavrasEvaluetedIgnoreAmb(-1);
    }
    
    public int getNumPalavrasEvaluetedIgnoreAmb(int iteration_limit){
        //
        // Código adaptado da printWordsEvaluatedByIt
        //
        int totalTemp = 0;
        int totalRight = 0;
        
//        System.out.println("It. #Words | #TotalAcumulado #Right (%)");
        for (int i = 0; i < countWordsByIt.length && (i <= iteration_limit || iteration_limit <0); i++) {
            if (countWordsByIt[i] <= 0) {
                continue;
            }

            totalTemp += countWordsByIt[i];
            totalRight += countWordsRightByIt[i];
//            System.out.printf("%2d, %6d | %10d  %8d (%.2f) %n",
//                    i,
//                    countWordsByIt[i], totalTemp, totalRight,
//                    ((float) totalRight / totalTemp) * 100);

        }
        return totalTemp;
    }
}
