package dic.wordspolarity;

import dic.wordspolarity.graphbook.Bag;
import dic.wordspolarity.graphbook.In;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*************************************************************************
 *  Compilation:  javac EdgeWeightedGraph.java
 *  Execution:    java EdgeWeightedGraph filename.txt
 *  Dependencies: Bag.java Edge.java In.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/43mst/tinyEWG.txt
 *
 *  An edge-weighted undirected graph, implemented using adjacency lists.
 *  Parallel edges and self-loops are permitted.
 *
 *  % java EdgeWeightedGraph tinyEWG.txt 
 *  8 16
 *  0: 6-0 0.58000  0-2 0.26000  0-4 0.38000  0-7 0.16000  
 *  1: 1-3 0.29000  1-2 0.36000  1-7 0.19000  1-5 0.32000  
 *  2: 6-2 0.40000  2-7 0.34000  1-2 0.36000  0-2 0.26000  2-3 0.17000  
 *  3: 3-6 0.52000  1-3 0.29000  2-3 0.17000  
 *  4: 6-4 0.93000  0-4 0.38000  4-7 0.37000  4-5 0.35000  
 *  5: 1-5 0.32000  5-7 0.28000  4-5 0.35000  
 *  6: 6-4 0.93000  6-0 0.58000  3-6 0.52000  6-2 0.40000
 *  7: 2-7 0.34000  1-7 0.19000  0-7 0.16000  5-7 0.28000  4-7 0.37000
 *
 *************************************************************************/
/**
 *  The <tt>EdgeWeightedGraph</tt> class represents an undirected graph of vertices
 *  named 0 through V-1, where each edge has a real-valued weight.
 *  It supports the following operations: add an edge to the graph,
 *  in the graph, iterate over all of the neighbors incident to a vertex.
 *  Parallel edges and self-loops are permitted.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class EdgeWeightedGraph {

    private final int V;
    private int E;
    private Bag<Edge>[] adj;
    // my 
    private boolean directedGraph; // true = directedGraph, false = undirectedGraph 
    private List<Integer> seedWords = new ArrayList<Integer>();
    private boolean avoidRicochete; // Evitar que uma propagação de A -> B, volte se exisitir B -> A 
    private int numRicocheteAvoided = 0;
//    private static final int SYN_REL = 1;
//    private static final int ANT_REL = 2;

    /**
     * Create an empty edge-weighted graph with V vertices.
     */
    public EdgeWeightedGraph(int V) {
        if (V < 0) {
            throw new RuntimeException("Number of vertices must be nonnegative");
        }
        this.V = V;
        this.E = 0;
        adj = (Bag<Edge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Edge>();
        }
        // my
        initMyVars(true/*, false*/);
    }

    // My 
    public EdgeWeightedGraph(int V, boolean directedGraph /*, boolean avoidRicochete*/) {
        if (V < 0) {
            throw new RuntimeException("Number of vertices must be nonnegative");
        }
        this.V = V;
        this.E = 0;
        adj = (Bag<Edge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Edge>();
        }
        // my
        initMyVars(directedGraph /*, avoidRicochete*/);
    }

    // My
    private void initMyVars(boolean directedGraph /*, boolean avoidRicochete*/) {
//        pos = new int[V];
//        neg = new int[V];
//        neu = new int[V];
//        it = new int[V];
//
//        for (int i = 0; i < V; i++) {
//            it[i] = -1; // importante que it seja inicializado com valor negativo
//            pos[i] = 0;
//            neg[i] = 0;
//        }

        this.directedGraph = directedGraph; // true, Para ter o comportamento original do algoritmo EPIA
        //this.avoidRicochete = avoidRicochete; // false, Para ter o comportamento original do algoritmo EPIA   

//        if (!directedGraph && !avoidRicochete) {
//            System.out.println("AVISO: Num grafo não direcionado é aconselhável ligar o avoidRicochete (avoidRicochete=true).");
//        }
    }

    /**
     * Create a random edge-weighted graph with V vertices and E edges.
     * The expected running time is proportional to V + E.
     */
    public EdgeWeightedGraph(int V, int E) {
        this(V);
        if (E < 0) {
            throw new RuntimeException("Number of edges must be nonnegative");
        }
        for (int i = 0; i < E; i++) {
            int v = (int) (Math.random() * V);
            int w = (int) (Math.random() * V);
            double weight = Math.round(100 * Math.random()) / 100.0;
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }

    /**
     * Create a weighted graph from input stream.
     */
    public EdgeWeightedGraph(In in) {
        this(in.readInt());
        int E = in.readInt();
        for (int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
            double weight = in.readDouble();
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }

    /**
     * Copy constructor.
     */
    public EdgeWeightedGraph(EdgeWeightedGraph G) {
        this(G.V());
        this.E = G.E();
        for (int v = 0; v < G.V(); v++) {
            // reverse so that adjacency list is in same order as original
            Stack<Edge> reverse = new Stack<Edge>();
            for (Edge e : G.adj[v]) {
                reverse.push(e);
            }
            for (Edge e : reverse) {
                adj[v].add(e);
            }
        }
    }

    /**
     * Return the number of vertices in this graph.
     */
    public int V() {
        return V;
    }

    /**
     * Return the number of edges in this graph.
     */
    public int E() {
        return E;
    }

    /**
     * Add the edge e to this graph.
     */
    public void addEdge(Edge e) {
        int v = e.either();
        int w = e.other(v);
        //adj[v].add(e);    
        //adj[w].add(e);

        // My
        if (this.directedGraph) { // Se directed graph
            adj[v].add(e);
            E++;
        } else { // Se undirected graph
            boolean edgeJaExiste = false;
            for (Edge e_aux : this.adj(v)) {
                if (v == e_aux.dest() && w == e_aux.either()) {
                    edgeJaExiste = true;
                    break;
                }
            }
            if (!edgeJaExiste) {
                adj[v].add(e);
                adj[w].add(e);
                E++;
            }
        }
        // End My

        // E++; (passou para dentro do IF
    }

    /**
     * Return the edges incident to vertex v as an Iterable.
     * To iterate over the edges incident to vertex v, use foreach notation:
     * <tt>for (Edge e : graph.adj(v))</tt>.
     */
    public Iterable<Edge> adj(int v) {
        return adj[v];
    }

    /**
     * Return all edges in this graph as an Iterable.
     * To iterate over the edges, use foreach notation:
     * <tt>for (Edge e : graph.edges())</tt>.
     */
    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for (int v = 0; v < V; v++) {
            int selfLoops = 0;
            for (Edge e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                } // only add one copy of each self loop
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) {
                        list.add(e);
                    }
                    selfLoops++;
                }
            }
        }
        return list;
    }

    /**
     * Return a string representation of this graph.
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (Edge e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    /**
     * Test client.
     */
    public static void main(String[] args) {
        //
        // Configuração da experiÊncia
        //
        int NUM_EXP_A_REALIZAR = 1; 
        int NUM_SEED_WORD_SETS = 30;       // cada set tem 3 seed words
        boolean randomSeedWords = false;  // seedwords random ou sequenciais
        boolean directedGraph = false;
        boolean avoidRicochet = true;
        boolean evalAsEPIA2011 = false; // true = epia2011, false = propor2012
        String seedWordsFile = "E:\\Development\\Graph-Java\\seedsPosNegNeuMANSelected.txt"; // seedsPosNegNeuMANSelected.txt seedsPosNegNeu3585.txt seedsPosNeg.txt
        String papelFile = "E:\\Development\\Graph-Java\\relacoes_final.txt";                // relacoes_final.txt triplos_da.txt triplos_wikcionario_tudo.txt
        String evalFile = "E:\\Development\\Graph-Java\\seedsPosNegNeu3585.txt";    // seedsPosNegNeu3585.txt"; 460pal_anotadorAndre_SemAmb.csv
        
        
        float[] accuracyByExp = new float[NUM_EXP_A_REALIZAR];
        int[] numEvaluetedWordsByExp = new int[NUM_EXP_A_REALIZAR];
        int[] numWordsProducedByExp = new int[NUM_EXP_A_REALIZAR];
        List<int[][]> resStatsByExp = new ArrayList<int[][]>();
        
        int EVAL_TO_ITERATION = 5; 
        float[] accuracyXByExpUntilIteration =  new float[NUM_EXP_A_REALIZAR];
        int[] numEvaluetedWordsByExpUntilIteration = new int[NUM_EXP_A_REALIZAR]; 
        int[] numWordsProducedByExpUntilIteration  = new int[NUM_EXP_A_REALIZAR];
        
        // seedsPosNegNeuMANSelected seedsPosNegNeu3585 seedsPosNeg
        LoadSeedWords seedWordsLoader = new LoadSeedWords(seedWordsFile);
        for (int numExp = 0; numExp < NUM_EXP_A_REALIZAR; numExp++) {
            
//           System.out.printf("\n\n=== EXPERIENCIA %d ===%n", numExp);
            
            // relacoes_final_SINONIMIA.txt | papel_amostra.txt
            System.out.println("------ PAPEL INFO ---------");
            LoaderPAPEL papel = new LoaderPAPEL(papelFile);
            papel.printRelations();
            System.out.println("Num Loaded triples          : " + papel.getNumLoadedTriples());
            System.out.println("Num Ignored triples         : " + papel.getNumTriplosIgnorados());
            System.out.println("Num Ignored relations       : " + papel.getNumLinhasIgnoradas());    
            System.out.println("Num TOTAL Lines             : " + (papel.getNumLoadedTriples() + papel.getNumTriplosIgnorados() + papel.getNumLinhasIgnoradas()));
            System.out.println("Num Distinct words          : " + papel.getNumDistinctOfWords());
            System.out.println("Num Palavras > 1 cat.Gram.  : " + papel.getNumMaxWordsComMaisQue1ClasseGramatical());
            System.out.println("---- End PAPEL INFO ----- INI GRAPH ------");

            //
            EdgeWeightedGraph G = createGraphPAPEL(papel, directedGraph);
            //System.out.print(G); // Imprimir tabela de adjacências
            System.out.println("AVG Degree: " + 2.0 * G.E() / G.V());
            
            System.out.println("---- End Graph ----- INI POLARITY PROPAG. ------");

            //
            // Carregar RANDOM seed words
            //
            List<String> seeds = new ArrayList<String>();
            List<Integer> seedsPol = new ArrayList<Integer>();
            for(int numSeedsByPol = 0; numSeedsByPol < NUM_SEED_WORD_SETS; numSeedsByPol++) {
                if(randomSeedWords) {
                    seeds.add(seedWordsLoader.getRandomPosWord(papel)); seedsPol.add(1);
                    seeds.add(seedWordsLoader.getRandomNegWord(papel)); seedsPol.add(-1);
                    seeds.add(seedWordsLoader.getRandomNeuWord(papel)); seedsPol.add(0);
                } else {
                    seeds.add(seedWordsLoader.getSequencialPosWord()); seedsPol.add(1);
                    seeds.add(seedWordsLoader.getSequencialNegWord()); seedsPol.add(-1);
                    seeds.add(seedWordsLoader.getSequencialNeuWord()); seedsPol.add(0); 
                }
            }
            
            // Visita o grafo e propaga a polaridade
            //BreadthFirstPaths bfs = new BreadthFirstPaths(G, papel, false, "E:\\Development\\Graph-Java\\seedsLixo.txt", 12);
            BreadthFirstPaths bfs = new BreadthFirstPaths(G, papel, avoidRicochet, seeds, seedsPol);
            bfs.printGraphPol(papel, 10);
            bfs.printGraphResume();      
//            bfs.printWordCounters(papel, 2775);  
//            bfs.printWordCounters(papel, 32270); 
//            bfs.printWordCounters(papel, 15704);
//            System.out.println("---- End polarity propag. ----- INI EVAL ------");

            // 460pal_anotadorAndre_SemAmb.csv 
            Evaluation eval = new Evaluation(papel, G, evalAsEPIA2011, bfs.getPos(), bfs.getNeg(), bfs.getNeu(), bfs.getIt(), evalFile);
            eval.printResult();
            //eval.printAdjectivesEvaluation();
            //eval.printNomEvaluation();            
            //eval.printUnknownClassGramEvaluation(); // Bom para ver se existem palavas para às não foram obtidas classes gramaticais.
            System.out.println("---- End EVAL ----");


            accuracyByExp[numExp] = eval.accuracyIgnoreAmb;
            numEvaluetedWordsByExp[numExp] = eval.getNumPalavrasEvaluetedIgnoreAmb();
            numWordsProducedByExp[numExp] = bfs.getNumPalavrasWithPolIgnoreAmb();
            resStatsByExp.add(eval.resStats); 
            
            accuracyXByExpUntilIteration[numExp] = eval.accuracyAtIteration(EVAL_TO_ITERATION);
            numEvaluetedWordsByExpUntilIteration[numExp] = eval.getNumPalavrasEvaluetedIgnoreAmb(EVAL_TO_ITERATION);
            numWordsProducedByExpUntilIteration[numExp] = bfs.getNumPalavrasWithPolIgnoreAmb(EVAL_TO_ITERATION);
            System.out.printf("ACCURACY: [+,-,0,AMB: %.2f]%n", eval.accuracyAllMatrix * 100);
            System.out.printf("ACCURACY: [+,-,0: %.2f] #Words Evaluated: %d Produced (Ig.amb.): %d %n", eval.accuracyIgnoreAmb * 100, eval.getNumPalavrasEvaluetedIgnoreAmb(), bfs.getNumPalavrasWithPolIgnoreAmb());
            System.out.printf("ACCURACY: [+&0,-: %.2f]%n", eval.accuracyMergePosNeutralIgnoreAmb*100);
            System.out.printf("ACCURACY at It.3: [+,-,0: %.2f] #Words Evaluated: %d #Word Produced (Ig.amb.): %d %n", 
                                    eval.accuracyAtIteration(EVAL_TO_ITERATION) * 100,
                                    eval.getNumPalavrasEvaluetedIgnoreAmb(EVAL_TO_ITERATION),
                                    bfs.getNumPalavrasWithPolIgnoreAmb(EVAL_TO_ITERATION));
            
                    
            System.out.println("Graph Orientado: " + G.isDirectedGraph());
            System.out.println("Avoid Ricochete: " + bfs.avoidRicochete());

            System.out.print("Num known Seeds: " + bfs.seedWordsKnown + " [");
            for (int i = 0; i < bfs.getSeedWords().size(); i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.print(papel.getWordById(bfs.getSeedWords().get(i)));
            }
            System.out.println("]");

            System.out.printf("Precision (+,-,0): %.2f;%.2f;%.2f%n", eval.precisionPosIgnoreAmb() * 100, eval.precisionNegIgnoreAmb()*100, eval.precisionNeuIgnoreAmb()*100); 
            System.out.printf("Recall    (+,-,0): %.2f;%.2f;%.2f%n", eval.recallPosIgnoreAmb() * 100, eval.recallNegIgnoreAmb()*100, eval.recallNeuIgnoreAmb()*100); 
            System.out.printf("F1        (+,-,0): %.2f;%.2f;%.2f%n", eval.f1_measurePosIgnoreAmb() * 100, eval.f1_measureNegIgnoreAmb()*100, eval.f1_measureNeuIgnoreAmb()*100); 
        }   
        
        
        
        System.out.println("\n\n##Experiências Realizadas: " + NUM_EXP_A_REALIZAR + " ##");
        
        //
        // Calcular a accuracy média e SD (da população) das experiências realizadas
        //
        float[] accuracyMeann = meanSDMinMax(accuracyByExp);
        System.out.printf("Accuracy Média +/- SD: %.2f +/- %.2f MIN: %.2f MAX: %.2f %n" ,
                    accuracyMeann[0] * 100, accuracyMeann[1] * 100, accuracyMeann[2] * 100, accuracyMeann[3] * 100);

        float[] numEvaluetedWordsByExpMean = meanSDMinMax(numEvaluetedWordsByExp);
        System.out.printf("Evaluated Words em Média +/- SD: %.2f +/- %.2f MIN: %.2f MAX: %.2f %n" ,
                    numEvaluetedWordsByExpMean[0], numEvaluetedWordsByExpMean[1], numEvaluetedWordsByExpMean[2], numEvaluetedWordsByExpMean[3]);
   
        float[] numProducedWordsByExpMean = meanSDMinMax(numWordsProducedByExp);
        System.out.printf("Produced  Words em Média +/- SD: %.2f +/- %.2f MIN: %.2f MAX: %.2f %n" ,
                    numProducedWordsByExpMean[0], numProducedWordsByExpMean[1], numProducedWordsByExpMean[2], numProducedWordsByExpMean[3]);      
        
        //
        // Calcular as micro-Medidas
        //
        int[][] resStatsACumulado = new int[4][4];
        for(int i = 0; i < resStatsByExp.size(); i++) {
            resStatsACumulado[0][0] += resStatsByExp.get(i)[0][0];
            resStatsACumulado[0][1] += resStatsByExp.get(i)[0][1];
            resStatsACumulado[0][2] += resStatsByExp.get(i)[0][2];
            resStatsACumulado[1][0] += resStatsByExp.get(i)[1][0];
            resStatsACumulado[1][1] += resStatsByExp.get(i)[1][1];
            resStatsACumulado[1][2] += resStatsByExp.get(i)[1][2];
            resStatsACumulado[2][0] += resStatsByExp.get(i)[2][0];
            resStatsACumulado[2][1] += resStatsByExp.get(i)[3][1];
            resStatsACumulado[2][2] += resStatsByExp.get(i)[3][2];
        }
        System.out.printf("Micro Prec./Rec./F1 + : %.2f ; %.2f ; %.2f %n",  
                micro_precisionPosIgnoreAmb(resStatsACumulado) * 100, 
                micro_recallPosIgnoreAmb(resStatsACumulado) * 100, 
                micro_f1_measurePosIgnoreAmb(micro_precisionPosIgnoreAmb(resStatsACumulado) , micro_recallPosIgnoreAmb(resStatsACumulado)) * 100);
     
         
        //
        // Calcular a accuracy média e SD (da população) das experiências realizadas, MAS
        // só contabilizando até uma determinada iteração:
        //
        System.out.println("\n** Avaliação Até Iteração: " + EVAL_TO_ITERATION);
        float[] accuracyMeannUntilIt = meanSDMinMax(accuracyXByExpUntilIteration);
        System.out.printf("Accuracy Média +/- SD: %.2f +/- %.2f MIN: %.2f MAX: %.2f %n" ,
                    accuracyMeannUntilIt[0] * 100, accuracyMeannUntilIt[1] * 100, accuracyMeannUntilIt[2] * 100, accuracyMeannUntilIt[3] * 100);

        float[] numEvaluetedWordsByExpUntilIt = meanSDMinMax(numEvaluetedWordsByExpUntilIteration);
        System.out.printf("Evaluated Words em Média +/- SD: %.2f +/- %.2f MIN: %.2f MAX: %.2f %n" ,
                    numEvaluetedWordsByExpUntilIt[0], numEvaluetedWordsByExpUntilIt[1], numEvaluetedWordsByExpUntilIt[2], numEvaluetedWordsByExpUntilIt[3]);
   
        float[] numProducedWordsByExpUntilIt = meanSDMinMax(numWordsProducedByExpUntilIteration);
        System.out.printf("Produced  Words em Média +/- SD: %.2f +/- %.2f MIN: %.2f MAX: %.2f %n" ,
                    numProducedWordsByExpUntilIt[0], numProducedWordsByExpUntilIt[1], numProducedWordsByExpUntilIt[2], numProducedWordsByExpUntilIt[3]);
    }

    
    
    
    
    public static EdgeWeightedGraph createGraphDeTeste(LoaderPAPEL papel, boolean directedGraph /*, boolean avoidRicochete*/) {

        EdgeWeightedGraph G = new EdgeWeightedGraph(papel.getNumDistinctOfWords(), directedGraph /*, avoidRicochete*/);

        int word_id1 = -1;
        int word_id2 = -1;
        int rel_iddd = -1;
        int rel_type = -1;
        //int rel_type2 = -1;

        // Construir a tabela de adjacencias com base no id das palavras e relações
        for (int i = 0; i < papel.getNumLoadedTriples(); i++) {
            word_id1 = papel.getIdWordOrigenFromTriple(i);
            word_id2 = papel.getIdWordDestinFromTriple(i);
            rel_iddd = papel.getIdRelationFromTriple(i);
            rel_type = papel.getRelationType(i);
                    
            //rel_type2 = (rel_iddd < 100) ? SYN_REL : ANT_REL;
            // Estou assumir que ficheiro só tem dois tipos de rel. e o primeiro triplo do ficheiro será de sinonomia.
            G.addEdge(new Edge(word_id1, word_id2, rel_iddd, rel_type));
        }

        //G.setSeedWord(papel.getIdWord("I"), 0, 1);
//        G.setSeedWord(papel.getIdWord("A"), 1);
//        G.setSeedWord(papel.getIdWord("B"), -1);

        return G;
    }

    public static EdgeWeightedGraph createGraphPAPEL(LoaderPAPEL papel, boolean directedGraph,
            String seedWords_file) {
        return createGraphPAPEL(papel, true/*, seedWords_file, 0*/);
    }

    /**
     * 
     * @param papel
     * @param directedGraph true ou false.
     * Se for pedido para criar um Grafo NÃO Orientado, e forem fornecidos dois ou mais
     * arcos a ligar os mesmos nós, apenas um é representado.
     * Exemplo, se existirem os arcos A -> B e B -> A, apenas será representado o arco A - B. 
     * Este arco aparecerá representado, na lista de arcos, tanto do nó A como do nó B.
     * 
     * Se for pedido para criar um Grafo Orientado, e forem fornecidos dois ou mais
     * arcos a ligar os mesmos nós, todos eles serão representados.
     * 
     * @param avoidRicochete
     * @param seedWords_file
     * @param maxSeedsToLoad
     * @return 
     */
    public static EdgeWeightedGraph createGraphPAPEL(LoaderPAPEL papel,
            boolean directedGraph/*,
            String seedWords_file, int maxSeedsToLoad*/) {

        EdgeWeightedGraph G = new EdgeWeightedGraph(papel.getNumDistinctOfWords(), directedGraph /*, avoidRicochete*/);

        int word_id1 = -1;
        int word_id2 = -1;
        int rel_iddd = -1;
        int rel_type = -1;
        //int rel_type2 = -1;

        // Construir a tabela de adjacencias com base no id das palavras e relações
        for (int i = 0; i < papel.getNumLoadedTriples(); i++) {
            word_id1 = papel.getIdWordOrigenFromTriple(i);
            word_id2 = papel.getIdWordDestinFromTriple(i);
            rel_iddd = papel.getIdRelationFromTriple(i);
            rel_type = papel.getRelationType(i);
            
            //rel_type2 = (rel_iddd < 100) ? SYN_REL : ANT_REL;
            // Word origem e destino JÁ não (no grafo undir. é ind.) são trocadas para que: palavra_definição <- palavra_defenida
            G.addEdge(new Edge(word_id1, word_id2, rel_iddd, rel_type));
        }

//////        //
//////        // Carregar seed words do ficheiro
//////        // 
//////        if (seedWords_file != null) {
//////            In in = new In(seedWords_file);
//////
//////            int seedWordLines = 0; // Número de linhas que indicam seed words
//////            int seedWords = 0;     // Número de seed words que aparecem no grafo (permite verificar se foi indicada alguma seed word, que não aparece no grafo, logo não será usada para propagar a polaridade).
//////            List<String> seedWordsUnknown = new ArrayList<String>();
//////            String seedW = null;
//////            Integer wordId = 0;
//////            int pol = 0;
//////
//////            String line = null;
//////            String[] fields = null;
//////            while ((line = in.readLine()) != null) {
//////                if (line.matches("^\\s{0,}(#|/\\*|//).{0,}") || line.trim().isEmpty()) // Se linha comentada, ignora-a
//////                {
//////                    continue;
//////                }
//////
//////
//////                fields = line.split(";|:|,");
//////                // Se não dividiu antes, tenta dividir por espaços. Atenção que os dois splits separados
//////                // são diferentes de realizar um unico split: line.split("\\s+|;|:|,");
//////                if (fields == null || fields.length == 0) 
//////                {
//////                    fields = line.split("\\s+");
//////                }
//////
//////                if (fields.length >= 2) 
//////                {
//////                    seedWordLines++;
//////                    seedW = fields[0].trim();
//////                    pol = Integer.valueOf(fields[1].trim());
//////                    wordId = papel.getIdWord(seedW);
//////                    if (wordId != null) {
//////                        G.setSeedWord(wordId, pol);
//////                        seedWords++;
//////                    } else {
//////                        seedWordsUnknown.add(seedW);
//////                    }
//////
//////                    // Se foi indicado um número máximo de seed para carregar e esse
//////                    // máximo foi alcançado, não carrega mais seed words
//////                    if (maxSeedsToLoad > 0 && maxSeedsToLoad == seedWords) {
//////                        break;
//////                    }
//////                } else {
//////                    System.out.println("AVISO: Ficheiro SeedWords com conteúdo num formato inválido "
//////                            + "[ " + line + "]."
//////                            + "Formato válido: palavra, polaridade");
//////                }
//////            }
//////
//////            System.out.println("Seed words Lines: " + seedWordLines
//////                    + " (Known Seeds: " + seedWords + ") (Unknown Seeds: " + seedWordsUnknown.toString() + ")");
//////        }

        return G;
    }

//    // noVisitado = nó em que estou.
//    public void propagate(int noVisitado, Edge edge) {
////        int noOrig = edge.either();
////        int noDest = edge.other(noOrig); 
////        int type = edge.type();
//        int noOrig = noVisitado;
//        int noDest = edge.other(noVisitado);
//        int type = edge.type();
//
//        if (this.avoidRicochete) {
//            for (Edge e : this.adj(noDest)) {
//                if (e.other(noDest) == noOrig && e.isVisited()) {
//                    this.numRicocheteAvoided++;
//                    return; // Evita o ricochete da propagação
//                }
//            }
//        }
//
//        if (type == SYN_REL) { // assume sinónimo
////            if (this.pos[noOrig] > this.neg[noOrig]) {
////                pos[noDest]++;
////            } else if (this.pos[noOrig] < this.neg[noOrig]) {
////                neg[noDest]++;
////            }
//            int[] res = findMax(this.pos[noOrig], this.neg[noOrig], this.neu[noOrig]);
//            switch (res[0]) {
//                case 1:
//                    pos[noDest]++;
//                    break;
//                case 2:
//                    neg[noDest]++;
//                    break;
//                case 3:
//                    neu[noDest]++;
//                    break;
//            }
//        } else { // assume antónimo
////            if (this.pos[noOrig] > this.neg[noOrig]) {
////                neg[noDest]++;
////            } else if (this.pos[noOrig] < this.neg[noOrig]) {
////                pos[noDest]++;
////            }
//            int[] res = findMax(this.pos[noOrig], this.neg[noOrig], this.neu[noOrig]);
//            switch (res[0]) {
//                case 1:
//                    neg[noDest]++;
//                    break;
//                case 2:
//                    pos[noDest]++;
//                    break;
//                case 3:
//                    neu[noDest]++;
//                    break;
//            }
//        }
//
//        // Incrementar a iteração
//        if (this.it[noDest] < 0) {
//            this.it[noDest] = this.it[noOrig] + 1;
//        }
//
//        // Memoriza as propagações feitas. Desta forma tem-se a info necessário para 
//        // evitar o efeito "ricochete directo", caso se pretenda  
////        if(this.avoidRicochete)
////        for (Edge e : this.adj(noOrig)) {
////            if (e.dest() == noDest) {
////                e.setAsVisited();
////                break;
////            }
////        }
//        // No caso de um grafo orientado é irrelevante, mas no caso de um grafo não orientado é preciso
//        edge.setAsVisited();
//
//    }
//
//    private int[] findMax(int first, int second, int third) {
//        int max = first;
//        int position = 1;
//
//        if (second > max) {
//            max = second;
//            position = 2;
//        }
//        if (third > max) {
//            max = third;
//            position = 3;
//        }
//
//        // Se existir mais que um máximo  
//        if (position == 1 && (first == second || first == third)) {
//            position = 4;
//        } else if (position == 2 && (second == first || second == third)) {
//            position = 4;
//        } else if (position == 3 && (third == first || third == second)) {
//            position = 4;
//        }
//
//        int[] ret = new int[2];
//        ret[0] = position;
//        ret[1] = max;
//        return ret;
//    }
    public List<Integer> getSeedWords() {
        return seedWords;
    }
    
    //My 
    public int getInputOutputDegree(int node_id) {
        return adj[node_id].size();
    }
    
////    // polarity = valor inteiro positivo, nrgativo ou neutro
////    public void setSeedWord(int wordId, int polarity) {
////        seedWords.add(wordId); // guardar o id das seedwords
////
////        if (polarity > 0) {
////            this.pos[wordId] = polarity; // define logo a polaridade inicial da SeedWord
////            this.neg[wordId] = 0;
////            this.neu[wordId] = 0;
////        } else if (polarity < 0) {
////            this.pos[wordId] = 0;
////            this.neg[wordId] = polarity * -1; // 
////            this.neu[wordId] = 0;
////        } else {
////            this.pos[wordId] = 0;
////            this.neg[wordId] = 0; // 
////            this.neu[wordId] = 1;
////        }
////
////        this.it[wordId] = 0;
////    }
//    public void setSeedWord(int wordId, int pos, int neg, int neu) {
//        seedWords.add(wordId); // guardar o id das seedwords
//
//        this.pos[wordId] = pos; // define logo a polaridade inicial da SeedWord
//        this.neg[wordId] = neg;
//        this.neu[wordId] = neu;
//        this.it[wordId] = 0;
//    }
    public boolean isDirectedGraph() {
        return this.directedGraph;
    }

    /**
     * Permite indicar que se pretende evitar que a propagação faça ricochete directo.
     * Isto significa que, se existir um arco A -> B e um outro B -> A, A irá propagar
     * a sua polaridade para B, mas B não irá propagar a polaridade de volta.
     */
    public void setAvoidRicochete() {
        this.avoidRicochete = true;
    }

//    public void printGraphPol(LoaderPAPEL papel) {
//
//        for (int v = 0; v < V; v++) {
//            System.out.print("No: " + v + " Pos: "
//                    + pos[v] + " Neg: " + neg[v] + " Neu: " + neu[v] + " It: " + it[v]);
//
//            if (papel != null) // se foi passodo um objecto com informção sobre as palvras, usa-o.
//            {
//                System.out.print(" [" + papel.getWordById(v) + "]");
//            }
//            System.out.println();
//        }
//    }
    public void printTabelaAdjacencias() {
        System.out.println(this.toString());
    }
//    public void printGraphResume() {
//        System.out.println("Grafo Orientado: " + this.directedGraph + "\nEvitar Richochete: " + this.avoidRicochete);
//        //System.out.println("Num Ricochetes Evitados: " + this.numRicocheteAvoided);
//    }
    
    //
    //
    //
    public static float[] meanSDMinMax(float[] setOfValues) {
    
        if(setOfValues == null || setOfValues.length<=0)
            return null;
        
        float valueMax = setOfValues[0];
        float valueMin = setOfValues[0];
                    
        float totalValue = 0;
        for(int i=0; i<setOfValues.length; i++) {
            totalValue += setOfValues[i];
            
            if(valueMax < setOfValues[i])
                valueMax = setOfValues[i];
            
            if(valueMin > setOfValues[i])
                valueMin = setOfValues[i];
        }
        
        int numExpRealizadas = setOfValues.length;
        float mean = totalValue/numExpRealizadas;
        
        float diferencaAcumulada = 0;
        
        for(int i = 0; i < numExpRealizadas; i++) {

            diferencaAcumulada += (float) Math.pow((mean-setOfValues[i]), 2);
        }
        
        float tempp = diferencaAcumulada/numExpRealizadas;
        // SD (da população)     
        
        return new float[]{mean, (float)Math.sqrt(tempp), valueMin, valueMax};
    }
    
    public static float[] meanSDMinMax(int[] setOfValues) {
    
        if(setOfValues == null || setOfValues.length<=0)
            return null;
        
        float valueMax = setOfValues[0];
        float valueMin = setOfValues[0];
                    
        float totalValue = 0;
        for(int i=0; i<setOfValues.length; i++) {
            totalValue += setOfValues[i];
            
            if(valueMax < setOfValues[i])
                valueMax = setOfValues[i];
            
            if(valueMin > setOfValues[i])
                valueMin = setOfValues[i];
        }
        
        int numExpRealizadas = setOfValues.length;
        float mean = totalValue/numExpRealizadas;
        
        float diferencaAcumulada = 0;
        
        for(int i = 0; i < numExpRealizadas; i++) {

            diferencaAcumulada += (float) Math.pow((mean-setOfValues[i]), 2);
        }
        
        float tempp = diferencaAcumulada/numExpRealizadas;
        // SD (da população)     
        
        return new float[]{mean, (float)Math.sqrt(tempp), valueMin, valueMax};
    }
    
    //
    // Micro-Medidad
    //
     public static float micro_precisionPosIgnoreAmb(int resStats[][]) {
         int LINE_POS = 0;
         int LINE_NEG = 1;
         int LINE_NEU = 2;
         int COL_POS = 0;
        float temp =  (float)resStats[LINE_POS][COL_POS]/
               (resStats[LINE_POS][COL_POS] + 
                resStats[LINE_NEG][COL_POS] + 
                resStats[LINE_NEU][COL_POS] ) ; 
        return temp;
    }
     
         public static float micro_precisionNegIgnoreAmb(int resStats[][]) {
          int LINE_POS = 0;
         int LINE_NEG = 1;
         int LINE_NEU = 2;
         int COL_NEG = 1;
        return (float)resStats[LINE_NEG][COL_NEG]/
               (resStats[LINE_POS][COL_NEG] + 
                resStats[LINE_NEG][COL_NEG] + 
                resStats[LINE_NEU][COL_NEG] ) ; 
    }
      
    public static float micro_precisionNeuIgnoreAmb(int resStats[][]) {
        int LINE_POS = 0;
         int LINE_NEG = 1;
         int LINE_NEU = 2;
         int COL_NEU = 2;
        return (float)resStats[LINE_NEU][COL_NEU]/
               (resStats[LINE_POS][COL_NEU] + 
                resStats[LINE_NEG][COL_NEU] + 
                resStats[LINE_NEU][COL_NEU]) ; 
    }
 
 public static float micro_recallPosIgnoreAmb(int resStats[][]) {
        int LINE_POS = 0;
         int COL_POS = 0;
         int COL_NEG = 1;
         int COL_NEU = 2;
   
    return (float)resStats[LINE_POS][COL_POS]/
               (resStats[LINE_POS][COL_POS] + 
                resStats[LINE_POS][COL_NEG] + 
                resStats[LINE_POS][COL_NEU] ) ;
    
    }  
     
 public static float micro_recallNegIgnoreAmb(int resStats[][]) {
         int LINE_NEG = 0;
         int COL_POS = 0;
         int COL_NEG = 1;
         int COL_NEU = 2;
        return (float)resStats[LINE_NEG][COL_NEG]/
               (resStats[LINE_NEG][COL_POS] + 
                resStats[LINE_NEG][COL_NEG] + 
                resStats[LINE_NEG][COL_NEU] ) ; 
    }  
 
  public static float micro_recallNeuIgnoreAmb(int resStats[][]) {
         int LINE_NEU = 0;
         int COL_POS = 0;
         int COL_NEG = 1;
         int COL_NEU = 2;
        return (float)resStats[LINE_NEU][COL_NEU]/
               (resStats[LINE_NEU][COL_POS] + 
                resStats[LINE_NEU][COL_NEG] + 
                resStats[LINE_NEU][COL_NEU] ) ; 
    }
  
   public static float micro_f1_measurePosIgnoreAmb(float precisionPosIgnoreAmb, float recallPosIgnoreAmb) {
        return 2 * (precisionPosIgnoreAmb * recallPosIgnoreAmb 
                    /
                (precisionPosIgnoreAmb + recallPosIgnoreAmb) ) ; 
    }
  
    public static float micro_f1_measureNegIgnoreAmb(float precisionNegIgnoreAmb, float recallNegIgnoreAmb) {
        return 2 * (precisionNegIgnoreAmb * recallNegIgnoreAmb 
                    /
                (precisionNegIgnoreAmb + recallNegIgnoreAmb) ) ; 
    }
    
   public static float micro_f1_measureNeuIgnoreAmb(float precisionNeuIgnoreAmb, float recallNeuIgnoreAmb) {
        return 2 * (precisionNeuIgnoreAmb * recallNeuIgnoreAmb 
                    /
                (precisionNeuIgnoreAmb + recallNeuIgnoreAmb) ) ; 
    }
  
}
