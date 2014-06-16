package dic.wordspolarity;

import dic.gephi.GelphiGexf;
import dic.wordspolarity.graphbook.In;
import dic.wordspolarity.graphbook.StdOut;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*************************************************************************
 *  Compilation:  javac BreadthFirstPaths.java
 *  Execution:    java BreadthFirstPaths G s
 *  Dependencies: Graph.java Queue.java Stack.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/41undirected/tinyCG.txt
 *
 *  Run breadth first search on an undirected graph.
 *  Runs in O(E + V) time.
 *
 *  %  java Graph tinyCG.txt
 *  6 8
 *  0: 2 1 5 
 *  1: 0 2 
 *  2: 0 1 3 4 
 *  3: 5 4 2 
 *  4: 3 2 
 *  5: 3 0 
 *
 *  %  java BreadthFirstPaths tinyCG.txt 0
 *  0 to 0 (0):  0
 *  0 to 1 (1):  0-1
 *  0 to 2 (1):  0-2
 *  0 to 3 (2):  0-2-3
 *  0 to 4 (2):  0-2-4
 *  0 to 5 (1):  0-5
 *
 *************************************************************************/
public class BreadthFirstPaths {

    private static final int INFINITY = Integer.MAX_VALUE;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path
    // my
    private boolean directedGraph; // true = directedGraph, false = undirectedGraph 
    private int[] pos;              // número de polaridades positivas recebidas por cada palavra
    private int[] neg;              // número de polaridades negativas recebidas por cada palavra
    private int[] neu;              // número de polaridades neutras recebidas por cada palavra
    private int[] it;               // distancia a que cada palavra está da seed word mais próxima
    private List<Integer> seedWords = new ArrayList<Integer>();
    public int seedWordsKnown;    // Número de seed words que aparecem no grafo (permite verificar se foi indicada alguma seed word, que não aparece no grafo, logo não será usada para propagar a polaridade).
    private boolean avoidRicochete; // Evitar que uma propagação de A -> B, volte se exisitir B -> A 
    private int numRicocheteAvoided = 0;
    private static final int SYN_REL = 1;
    private static final int ANT_REL = 2;
    private static final int PEJ_REL = 3;
    private EdgeWeightedGraph G;
    private LoaderPAPEL papel = null;

    // single source
    public BreadthFirstPaths(EdgeWeightedGraph G, int s) {
        marked = new boolean[G.V()];
        distTo = new int[G.V()];
        edgeTo = new int[G.V()];
        bfs(G, s);

        assert check(G, s);
    }

    // multiple sources
    public BreadthFirstPaths(EdgeWeightedGraph G, Iterable<Integer> sources) {
        marked = new boolean[G.V()];
        distTo = new int[G.V()];
        edgeTo = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            distTo[v] = INFINITY;
        }
        bfs(G, sources);
    }

    // myff
    /**
     * 
     * @param G
     * @param papel
     * @param avoidRicochete
     * @param seed_words 
     * @param seedWordsPol 
     */
    public BreadthFirstPaths(EdgeWeightedGraph G, LoaderPAPEL papel,
            /*boolean directedGraph,*/ boolean avoidRicochete, List<String> seed_words, List<Integer> seedWordsPol) {
        
        initMyVars(G, /*directedGraph,*/ avoidRicochete);
        loadSeedWordsFromArray(papel, seed_words, seedWordsPol);
        Iterable<Integer> sources = this.seedWords;
        marked = new boolean[G.V()];
        distTo = new int[G.V()];
        edgeTo = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            distTo[v] = INFINITY;
        }

        // definido para ser usado no Gephi (por causa das lebels dos nós
        this.papel = papel;

        bfs(G, sources);
    }

    // my 
    public BreadthFirstPaths(EdgeWeightedGraph G, LoaderPAPEL papel,
            /*boolean directedGraph,*/ boolean avoidRicochete, String seedWords_file, int maxSeedsToLoad) {
        initMyVars(G, /*directedGraph,*/ avoidRicochete);
        loadSeedWords(papel, seedWords_file, maxSeedsToLoad);
        Iterable<Integer> sources = seedWords;
        marked = new boolean[G.V()];
        distTo = new int[G.V()];
        edgeTo = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            distTo[v] = INFINITY;
        }
        bfs(G, sources);
    }

    // my
    private void initMyVars(EdgeWeightedGraph G /*, boolean directedGraph*/, boolean avoidRicochete) {
        int V = G.V();
        pos = new int[V];
        neg = new int[V];
        neu = new int[V];
        it = new int[V];

        for (int i = 0; i < V; i++) {
            it[i] = -1; // importante que it seja inicializado com valor negativo
            pos[i] = 0;
            neg[i] = 0;
        }

        this.directedGraph = G.isDirectedGraph();  // true, Para ter o comportamento original do algoritmo EPIA
        this.avoidRicochete = avoidRicochete; // false, Para ter o comportamento original do algoritmo EPIA   

//        if (!directedGraph && !avoidRicochete) {
//            System.out.println("AVISO: Num grafo não direcionado é aconselhável ligar o avoidRicochete (avoidRicochete=true).");
//        }

        this.G = G;
    }

    // BFS from single soruce
    private void bfs(EdgeWeightedGraph G, int s) {
        Queue<Integer> q = new Queue<Integer>();
        for (int v = 0; v < G.V(); v++) {
            distTo[v] = INFINITY;
        }
        distTo[s] = 0;
        marked[s] = true;
        q.enqueue(s);

        int edgeIdForGhephi = 0;

        while (!q.isEmpty()) {
            int v = q.dequeue();
            //System.out.print("Visitou: " + v);
            for (Edge edge : G.adj(v)) {
                int w = edge.other(v); // O objectivo ou destino, é ir para o OUTRO nó em que não estou
                //System.out.println(" -> " + w);
                //System.out.println("\t<edge id=\""+(edgeIdForGhephi++)+"\" source=\""+v+"\" target=\""+w+"\"/>");
                propagate(v, edge);
                if (!marked[w]) {
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    marked[w] = true;
                    q.enqueue(w);
                }
            }
            //System.out.println();
        }
    }

    // BFS from multiple sources
    private void bfs(EdgeWeightedGraph G, Iterable<Integer> sources) {

        // my - Gephi
        GelphiGexf graph = new GelphiGexf(this.papel);
        int nodeTime = 0;
        int edgeTime = 0;
        int edgeIdForGhephi = 0;
        //graph.addNode("14750", "0", "250");
        //graph.addNodeAtt("14750", "0", "1", null, null);

        // my
        if (sources == null) {
            sources = G.getSeedWords();
        }

        Queue<Integer> q = new Queue<Integer>();
        for (int s : sources) {
            marked[s] = true;
            distTo[s] = 0;
            q.enqueue(s);

            // gephi. Todos os nós semente aparecem ao mesmo tempo e permanecem infinitamente
            graph.addNode("" + s, "" + nodeTime, null);

            String polAndWeight = getPolarityAndWeightForGephi(s);
            if (polAndWeight != null) {
                String parts[] = polAndWeight.split(";");
                graph.addNodeAtt("" + s, "5", parts[0], "" + nodeTime, null);
//                graph.addNodeAtt("" + s, "6", parts[1], "" + nodeTime, null);
            }
        }

        nodeTime = nodeTime + 3;
//        nodeTime++;
        while (!q.isEmpty()) {
            int v = q.dequeue();

            // Os nós vizinhos de v aparecem num momento do tempo posterior e ao mesmo tempo
            nodeTime++;

            // Fazer com que os próximos edges apareçam depois dos nós aparecer, mas não ao mesmo tempo.
            edgeTime = nodeTime;
            edgeTime++;


//            if (this.it[v] >= 2) {
//                break;
//            }


            //System.out.println("Visita nó: " + v + "e propaga para: ");
            for (Edge edge : G.adj(v)) {
                int w = edge.other(v); // O objectivo ou destino, é ir para o OUTRO nó em que não estou
                //System.out.print(" [" + w + "]");

                // Todos os nós devem aparecer no tempo passado como parametro
                edgeIdForGhephi = propagate(v, edge, graph, nodeTime, edgeTime, edgeIdForGhephi);

                if (!marked[w]) {
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    marked[w] = true;
                    q.enqueue(w);
                }
            }
            //System.out.println();
            nodeTime = edgeTime; // define o tempo para o no igual ao edge, para que seja usado na proxima iteração do ciclo
        }

        // gephi
//        graph.setIterations(this.it);
//           graph.toString();
    }

    // is there a path between s (or sources) and v?
    public boolean hasPathTo(int v) {
        return marked[v];
    }

    // length of shortest path between s (or sources) and v
    public int distTo(int v) {
        return distTo[v];
    }

    // shortest path bewteen s (or sources) and v; null if no such path
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) {
            return null;
        }
        Stack<Integer> path = new Stack<Integer>();
        int x;
        for (x = v; distTo[x] != 0; x = edgeTo[x]) {
            path.push(x);
        }
        path.push(x);
        return path;
    }

    // check optimality conditions for single source
    private boolean check(EdgeWeightedGraph G, int s) {

        // check that the distance of s = 0
        if (distTo[s] != 0) {
            StdOut.println("distance of source " + s + " to itself = " + distTo[s]);
            return false;
        }

        // check that for each edge v-w dist[w] <= dist[v] + 1
        // provided v is reachable from s
        for (int v = 0; v < G.V(); v++) {
            for (Edge edge : G.adj(v)) {
                int w = edge.dest();
                if (hasPathTo(v) != hasPathTo(w)) {
                    StdOut.println("edge " + v + "-" + w);
                    StdOut.println("hasPathTo(" + v + ") = " + hasPathTo(v));
                    StdOut.println("hasPathTo(" + w + ") = " + hasPathTo(w));
                    return false;
                }
                if (hasPathTo(v) && (distTo[w] > distTo[v] + 1)) {
                    StdOut.println("edge " + v + "-" + w);
                    StdOut.println("distTo[" + v + "] = " + distTo[v]);
                    StdOut.println("distTo[" + w + "] = " + distTo[w]);
                    return false;
                }
            }
        }

        // check that v = edgeTo[w] satisfies distTo[w] + distTo[v] + 1
        // provided v is reachable from s
        for (int w = 0; w < G.V(); w++) {
            if (!hasPathTo(w) || w == s) {
                continue;
            }
            int v = edgeTo[w];
            if (distTo[w] != distTo[v] + 1) {
                StdOut.println("shortest path edge " + v + "-" + w);
                StdOut.println("distTo[" + v + "] = " + distTo[v]);
                StdOut.println("distTo[" + w + "] = " + distTo[w]);
                return false;
            }
        }

        return true;
    }

//    // test client
//    public static void main(String[] args) {
//        In in = new In(args[0]);
//        Graph G = new Graph(in);
//        // StdOut.println(G);
//
//        int s = Integer.parseInt(args[1]);
//        BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);
//
//        for (int v = 0; v < G.V(); v++) {
//            if (bfs.hasPathTo(v)) {
//                StdOut.printf("%d to %d (%d):  ", s, v, bfs.distTo(v));
//                for (int x : bfs.pathTo(v)) {
//                    if (x == s) StdOut.print(x);
//                    else        StdOut.print("-" + x);
//                }
//                StdOut.println();
//            }
//
//            else {
//                StdOut.printf("%d to %d (-):  not connected\n", s, v);
//            }
//
//        }
//    }
    public int[] getPos() {
        return this.pos;
    }

    public int[] getNeg() {
        return this.neg;
    }

    public int[] getNeu() {
        return this.neu;
    }

    public int[] getIt() {
        return this.it;
    }

    // Ignorar palavras ambiguas ?
    public int[] getNumOfWordsByIt(boolean ignoreAmb) {
        if (this.it != null) {
            int it = 0;
            int temp = 0;
            int[] counts = new int[50];
            for (int i = 0; i < 50; i++) {
                counts[i] = 0;
            }

            for (int i = 0; i < this.it.length; i++) {
                if (this.it[i] < 0) {
                    continue;
                }

                it = this.it[i];
                if (!ignoreAmb) {
                    counts[it]++;
                } else {
                    int[] res = findMax(this.pos[i], this.neg[i], this.neu[i]);
                    if (res[0] != 4) {
                        counts[it]++;
                    }
                }

            }
            return counts;
        }
        return null;
    }

    /**
     * Permite obter a lista de Ids das seed words. Estes Ids, são os mesmos que 
     * foram atribuidos às palavras, na construção do grafo.
     * @return 
     */
    public List<Integer> getSeedWords() {
        return seedWords;
    }

    public boolean avoidRicochete() {
        return this.avoidRicochete;
    }

    // my
    private void loadSeedWords(LoaderPAPEL papel, String seedWords_file, int maxSeedsToLoad) {
        //
        // Carregar seed words do ficheiro
        // 
        if (seedWords_file == null) {
            return;
        }

        In in = new In(seedWords_file);

        int seedWordLines = 0; // Número de linhas que indicam seed words
        this.seedWordsKnown = 0;  // Número de seed words que aparecem no grafo (permite verificar se foi indicada alguma seed word, que não aparece no grafo, logo não será usada para propagar a polaridade).
        List<String> seedWordsUnknown = new ArrayList<String>();
        String seedW = null;
        Integer wordId = 0;
        int pol = 0;

        String line = null;
        String[] fields = null;
        while ((line = in.readLine()) != null) {
            if (line.matches("^\\s{0,}(#|/\\*|//).{0,}") || line.trim().isEmpty()) // Se linha comentada, ignora-a
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
                wordId = papel.getWordId(seedW);
                if (wordId != null) {
                    setSeedWord(wordId, pol);
                    seedWordsKnown++;
                } else {
                    seedWordsUnknown.add(seedW);
                }

                // Se foi indicado um número máximo de seed para carregar e esse
                // máximo foi alcançado, não carrega mais seed words
                if (maxSeedsToLoad > 0 && maxSeedsToLoad == seedWordsKnown) {
                    break;
                }
            } else {
                System.out.println("AVISO: Ficheiro SeedWords com conteúdo num formato inválido "
                        + "[ " + line + "]."
                        + "Formato válido: palavra, polaridade");
            }
        }

        System.out.println("Seed words Lines: " + seedWordLines
                + " (Known Seeds: " + seedWordsKnown + ") (Unknown Seeds: " + seedWordsUnknown.toString() + ")");

    }

    // my
    private void loadSeedWordsFromArray(LoaderPAPEL papel,
            List<String> seedWords, List<Integer> seedWordsPol) {
        if (seedWords == null) {
            return;
        }

        int seedWordLines = seedWords.size();
        List<String> seedWordsUnknown = new ArrayList<String>();
        Integer wordId = null;
        int pol = 0;

        for (int i = 0; i < seedWords.size(); i++) {
            wordId = papel.getWordId(seedWords.get(i));
            pol = seedWordsPol.get(i);
            if (wordId != null) {
                setSeedWord(wordId, pol);
                seedWordsKnown++;
            } else {
                seedWordsUnknown.add(seedWords.get(i));
            }
        }

//                System.out.println("Num Seed Words: " + seedWordLines
//                + " (Known Seeds: " + seedWordsKnown + ") (Unknown Seeds: " + seedWordsUnknown.toString() + ")");

    }

    // Método privado para evitar que após a criação de um objecto deste tipo, seja ivocado
    // este método.
    // polarity = valor inteiro positivo, negativo ou neutro
    private void setSeedWord(int wordId, int polarity) {
        seedWords.add(wordId); // guardar o id das seedwords

        if (polarity > 0) {
            this.pos[wordId] = polarity; // define logo a polaridade inicial da SeedWord
            this.neg[wordId] = 0;
            this.neu[wordId] = 0;
        } else if (polarity < 0) {
            this.pos[wordId] = 0;
            this.neg[wordId] = polarity * -1; // 
            this.neu[wordId] = 0;
        } else {
            this.pos[wordId] = 0;
            this.neg[wordId] = 0; // 
            this.neu[wordId] = 1;
        }

        this.it[wordId] = 0;
    }

    // criado por causa do Gelphi.
    private int propagate(int noVisitado, Edge edge,
            GelphiGexf graph, int nodeTime, int edgeTime, int edgeIdForGhephi) {

        int noOrig = noVisitado;
        int noDest = edge.other(noVisitado);

        if (this.avoidRicochete) {
            for (Edge e : G.adj(noDest)) {
                if (e.other(noDest) == noOrig && e.isVisited()) {
                    this.numRicocheteAvoided++;
                    return edgeIdForGhephi; // Evita o ricochete da propagação
                }
            }
        }

        int type = edge.type();

        if (type == SYN_REL) { // assume sinónimo

            int[] res = findMax(this.pos[noOrig], this.neg[noOrig], this.neu[noOrig]);
            switch (res[0]) {
                case 1:
                    pos[noDest]++;
                    break;
                case 2:
                    neg[noDest]++;
                    break;
                case 3:
                    neu[noDest]++;
                    break;
            }
        } else if (type == PEJ_REL) { // relação pejorativa
            // System.out.printf("Sentido pejurativo nos nós e counter antes prop.: %d (+,-,0: %d,%d,%d) - %d (+,-,0: %d,%d,%d) %n", noOrig, pos[noOrig],neg[noOrig],neu[noOrig], noDest, pos[noDest],neg[noDest],neu[noDest]);
            neg[noOrig]++;
            neg[noDest]++;
        } else { // assume antónimo

            // Se criamos simplesmente um grafo que não é para propagar, o array vai estar a null
            if (this.pos != null) {
                int[] res = findMax(this.pos[noOrig], this.neg[noOrig], this.neu[noOrig]);
                switch (res[0]) {
                    case 1:
                        neg[noDest]++;
                        break;
                    case 2:
                        pos[noDest]++;
                        break;
                    case 3:
                        neu[noDest]++;
                        break;
                }
            }
        }

//        // 
//        graph.addNode("" + noDest, "" + nodeTime, null);
////        graph.addNodeAttSpecial(""+noDest, "5", "0", ""+nodeTime, null);
//
//        graph.addEdge("" + (edgeIdForGhephi++), "" + noOrig, "" + noDest, "" + nodeTime, null);
//
//        String polAndWeight = getPolarityAndWeightForGephi(noDest);
//        if(polAndWeight != null) {
//            String parts[] = polAndWeight.split(";");
//            
//            // adiciona apolaridade no mesmo momento de tempo da adição do edge
//            graph.addNodeAtt(""+noDest,"5", parts[0], ""+edgeTime, null); 
////            graph.addNodeAtt(""+noDest,"6", parts[1], ""+edgeTime, null); 
//        }

        // Incrementar a iteração
        if (this.it != null && this.it[noDest] < 0) {
            this.it[noDest] = this.it[noOrig] + 1;
        }

        // No caso de um grafo orientado é irrelevante, mas no caso de um grafo não orientado é preciso
        edge.setAsVisited();
        return edgeIdForGhephi;
    }

    private String getPolarityAndWeightForGephi(int v) {
        String pol = null;
        float weight = 0.0f;
        int[] res = findMax(this.pos[v], this.neg[v], this.neu[v]);
        float total = this.pos[v] + this.neg[v] + this.neu[v];
        switch (res[0]) {
            case 1:
                pol = "1";
                weight = (float) (this.pos[v] * 1.0f) / total;
                break;
            case 2:
                pol = "-1";
                weight = (float) this.neg[v] / total;
                break;
            case 3:
                pol = "0";
                weight = (float) (this.neu[v] * 1.0f) / total;
                break;
            default:
                pol = "2"; // ambigous
                weight = 0.0f;
                break;
        }

        if (pol == null) {
            return null; // sem polaridade e sem weight
        }
        return pol + ";" + weight;
    }

    // noVisitado = nó em que estou.
    public void propagate(int noVisitado, Edge edge) {
        propagate(noVisitado, edge, null, 0, 0, 0);
    }

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

    /**
     * Imprime o grafo após ter aplicado o algoritmo de propagação.
     * Imprimir o grafo corresponde a imprimir o lexico de polaridades
     * @param papel 
     */
    public void printGraphPolarity(LoaderPAPEL papel) {
        printGraphPol(papel, -1);
    }

    public void printGraphPol(LoaderPAPEL papel, int limit) {
        printGraphPol(papel, -1, false);
    }

    public void printGraphPol(LoaderPAPEL papel, int limit, boolean outputWordsWitoutPol) {
        outputPolarityLexicon(papel, outputWordsWitoutPol, limit, null);
    }

    public void savePolarityLexicon(LoaderPAPEL papel,
            boolean outputWordsWitoutPol, String output_lexicon_file) {

        outputPolarityLexicon(papel, outputWordsWitoutPol, 0, output_lexicon_file);
    }

    /**    
     * Método para imprimir para ficheiro ou ecrã...
     * 
     * @param papel 
     * @param outputWordsWitoutPol
     * @param limit <=0 para não limitir o número de palavras a imprimir.
     * @param output_lexicon_file 
     */
    private void outputPolarityLexicon(LoaderPAPEL papel,
            boolean outputWordsWitoutPol, int limit, String output_lexicon_file) {

        boolean printToFile = true;
        // Print para ficheiro ou ecrã ?
        if (output_lexicon_file == null || output_lexicon_file.equals("")) {
            printToFile = false;
        }

        Writer out_file = null;

        String separator = ";";
        String line = "Word" + separator + "POS" + separator + "Polarity" + separator + "#+" + separator + "#-" + separator + "#0" + separator + "#iteration" + "\n";

        try {

            if (printToFile) {
                out_file = new OutputStreamWriter(new FileOutputStream(output_lexicon_file));
            }

            if (printToFile) {
                out_file.write(line);
            } else {
                System.out.print(line);
            }

            for (int v = 0; v < G.V(); v++) {

                // Ignore words without polarity
                // Nota. Em 21 de Abril 2012, notei estranhamente que testar apenas 
                // it[v] < 0, não é suficiente para ignorar palavras sem polaridade. 
                // Por esta razão passei a testar também 
                if (!outputWordsWitoutPol && (it[v] < 0 || (pos[v] <= 0 && neg[v] <= 0 && neu[v] <= 0))) {
                    continue;
                }

                String pol = "";
                if (pos[v] > neg[v] && pos[v] > neu[v]) {
                    pol = "+";
                } else if (neg[v] > pos[v] && neg[v] > neu[v]) {
                    pol = "-";
                } else if (neu[v] > pos[v] && neu[v] > neg[v]) {
                    pol = "0";
                } else {
                    pol = "A";
                }


                if (papel != null) // se foi passado um objecto com informção sobre as palvras, usa-o.
                {
                    line = papel.getWordById(v) + separator + papel.getClasseGramatical(v);
                    if (printToFile) {
                        out_file.write(line);
                    } else {
                        System.out.print(line);
                    }
                }

                line = separator + pol + separator;
                line += pos[v] + separator + neg[v] + separator + neu[v] + separator + it[v] + separator;
                line += "\n";

                if (printToFile) {
                    out_file.write(line);
                } else {
                    System.out.print(line);
                }

                if (limit > 0 && v >= limit) {
                    return;
                }
            }

        } catch (IOException ex) {
        } finally {
            try {
                if (out_file != null) {
                    out_file.close();
                }
            } catch (IOException sqlEx) {
            }

            System.out.printf("Polarity lexicon output: " + output_lexicon_file);
        }

    }

    // 2012-03-20
    // cria um grafo para ser visualizado no Gelphi
    public void createGraphForGephi(/*EdgeWeightedGraph G,*/LoaderPAPEL papel,
            boolean only_reached_words, String output_lexicon_file) {

        Writer out_file = null;

        try {

            out_file = new OutputStreamWriter(new FileOutputStream(output_lexicon_file));

            //
            // Header
            //
            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
                    + "\n<gexf xmlns=\"http://www.gexf.net/1.2draft\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\" version=\"1.2\">"
                    + "\n<meta lastmodifieddate=\"2009-03-20\">"
                    + "\n  <creator>Gexf.net</creator>"
                    + "\n  <description>A Web network changing over time</description>"
                    + "\n</meta>";
            out_file.write(header);

            String gType = G.isDirectedGraph() ? "directed" : "undirected";

            String graph = "\n<graph mode=\"static\" defaultedgetype=\"" + gType + "\">";
            String attrib = graph + "\n<attributes class=\"node\"> ";
            attrib += "\n\t<attribute id=\"0\" title=\"pos\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"1\" title=\"neg\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"2\" title=\"neu\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"3\" title=\"it\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"4\" title=\"pos_tag\" type=\"string\"/>";
            attrib += "\n\t<attribute id=\"5\" title=\"pol\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"6\" title=\"wei\" type=\"float\"/>";
//            attrib += "\n\t  <default>-1</default>";
//            attrib += "\n\t</attribute>";
            attrib += "\n</attributes>";
            out_file.write(attrib + "\n");

            out_file.write("<nodes>\n");

            // Variável auxiliar para saber se um nó foi escrito no ficheiro
            boolean noEscolhido[] = null;
            if (G != null && G.V() > 0) {
                noEscolhido = new boolean[G.V()];
                for (int i = 0; i < G.V(); i++) {
                    noEscolhido[i] = false;
                }
            }

            for (int v = 0; v < G.V(); v++) {

                // Ignore words without polarity
                if (only_reached_words && it[v] < 0) {
                    continue;
                }

                String label = null;
                String pos_tag = null;
                if (papel != null) // se foi passado um objecto com informção sobre as palvras, usa-o.
                {
                    label = papel.getWordById(v) /*+ separator + papel.getClasseGramatical(v)*/;
                    pos_tag = papel.getClasseGramatical(v);
                }

                // se não obteve uma label para o nó, usa o seu próprio id
                if (label == null || label.equals("")) {
                    label = "" + v;
                }

                if (it[v] > 3) {
                    // System.out.println("Igonorou it de : " + v + " = " + it[v]);
                    continue;
                }

                // Filtro por POS tag
                if (pos_tag != null && !pos_tag.matches(".*adj.*")) {
                    continue;
                }

                // no vai ser escrito para ficheiro    
                noEscolhido[v] = true;

                // <node id="0" label="Gephi" start="2009-03-01">
                String node = "\t<node id=\"" + v + "\" label =\"" + label + "\" >"; // start=\"0\" end=\"250\"
//                out_file.write(pos[v] + separator + neg[v] + separator + neu[v] + separator + it[v] + separator);

                // Se o array pos existe assume que os restantes também existem
                if (pos != null) {
                    node += "\n\t  <attvalues>";
                    node += "\n\t    <attvalue for=\"0\" value=\"" + pos[v] + "\"/>";
                    node += "\n\t    <attvalue for=\"1\" value=\"" + neg[v] + "\"/>";
                    node += "\n\t    <attvalue for=\"2\" value=\"" + neu[v] + "\"/>";
                    node += "\n\t    <attvalue for=\"3\" value=\"" + it[v] + "\"/>";
                    if (pos_tag != null) {
                        node += "\n\t    <attvalue for=\"4\" value=\"" + pos_tag + "\"/>";
                    }

                    String polAndWeight = getPolarityAndWeightForGephi(v);
                    if (polAndWeight != null) {
                        String parts[] = polAndWeight.split(";");
                        node += "\n\t    <attvalue for=\"5\" value=\"" + parts[0] + "\"/>";
                        node += "\n\t    <attvalue for=\"6\" value=\"" + parts[1] + "\"/>";

                    }

                    node += "\n\t  </attvalues>";
                }
                node += "\n\t</node>";

                out_file.write(node + "\n");
            }
            out_file.write("</nodes>\n");


            //  
            // Edges
            //
            out_file.write("<edges>\n");

            int s = 0; // starting node
            Queue<Integer> q = new Queue<Integer>();
            for (int v = 0; v < G.V(); v++) {
                distTo[v] = INFINITY;
                marked[v] = false; // Faz o reset a esta var, pois pode ter sido uasa anteriormento, por exemplo, no método bfs
            }
            distTo[s] = 0;
            marked[s] = true;
            q.enqueue(s);

            int edgeIdForGhephi = 0;
            while (!q.isEmpty()) {
                int v = q.dequeue();
                //System.out.print("Visitou: " + v);
                for (Edge edge : G.adj(v)) {
                    int w = edge.other(v); // O objectivo ou destino, é ir para o OUTRO nó em que não estou

                    // Filtro por POS tag
                    String pos_tagw1 = null;
                    String pos_tagw2 = null;
                    if (papel != null) // se foi passado um objecto com informção sobre as palvras, usa-o.
                    {
                        pos_tagw1 = papel.getClasseGramatical(v);
                        pos_tagw2 = papel.getClasseGramatical(w);
                    }
              
//                    if (it[v] > 2 || it[w] > 2) {
//                        // System.out.println("Igonorou it de : " + v + " = " + it[v]);
//                        continue;
//                    }                    

                    // Filtro por POS tag
                    if (pos_tagw1 != null && !pos_tagw1.matches(".*adj.*")
                            && pos_tagw2 != null && !pos_tagw2.matches(".*adj.*")) {
                        continue;
                    }

                    // apenas escreve ligação se os nós foram anteriormente escritos
                    if (noEscolhido[v] && noEscolhido[w]) {
                        out_file.write("\t<edge id=\"" + edgeIdForGhephi + "\" source=\"" + v + "\" target=\"" + w + "\" />\n"); // start=\"" + lixo++ + "\"
                        edgeIdForGhephi++;
                    }
                    //propagate(v, edge);
                    if (!marked[w]) {
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                        marked[w] = true;
                        q.enqueue(w);
                    }
                }
                //System.out.println();
            }

            out_file.write("</edges>\n");
            out_file.write("</graph>\n");
            out_file.write("</gexf>");

        } catch (IOException ex) {
        } finally {
            try {
                if (out_file != null) {
                    out_file.close();
                }
            } catch (IOException sqlEx) {
            }

            System.out.println("Polarity lexicon output: " + output_lexicon_file);
        }

    }

    public void printWordCounters(LoaderPAPEL papel, int wordId) {
        System.out.println("word:" + papel.getWordById(wordId)
                + " wordId:" + wordId
                + " +:" + this.pos[wordId]
                + " -:" + this.neg[wordId]
                + " 0:" + this.neu[wordId]
                + " It.:" + this.it[wordId]);
    }

    public void printGraphResume() {

        System.out.printf("Directed Graph: %s \nAvoid ricochet: %s%n", this.directedGraph, this.avoidRicochete);
        System.out.println("Num. of avoided ricochets: " + this.numRicocheteAvoided);

        //
        // Imprimir o número de palavras que receberam uma polaridade, por iteração.
        //
        int[] wByIt = getNumOfWordsByIt(false);
        int[] wByItIgnorAmb = getNumOfWordsByIt(true);
        if (wByIt == null) {
            return;
        }

        int totalTemp = 0;
        int totalTempIgnorAmb = 0;
        System.out.println("It.  #words #SubTotal | #WordsIg.Amb #SubTotalIg.Amb");
        for (int i = 0; i < wByIt.length; i++) {
            if (wByIt[i] <= 0) {
                break;
            }

            totalTemp += wByIt[i];
            totalTempIgnorAmb += wByItIgnorAmb[i];
            System.out.printf("%3d, %6d  - %6d | %10d - %6d %n",
                    i, wByIt[i], totalTemp, wByItIgnorAmb[i], totalTempIgnorAmb);
        }
        System.out.println("TOTAL: " + totalTemp);
    }

    /**
     * Permite obter a quantidade acumulada de palavras, que receberem uma 
     * polaridade até à iteração iteration_limit (inclusivé).
     * 
     * Exemplo, se iteração 4, irá devolver 4713:
     * It.  #words #SubTotal | #WordsIg.Amb #SubTotalIg.Amb
    0,      3  -      3 |          3 -      3 
    1,     15  -     18 |         15 -     18 
    2,    150  -    168 |        146 -    164 
    3,   1012  -   1180 |        942 -   1106 
    4,   4105  -   5285 |       3607 -   4713 
    5,   7630  -  12915 |       6417 -  11130 
    6,   7979  -  20894 |       6629 -  17759 
     * 
     * @param iteration
     * @return 
     */
    public int getNumPalavrasWithPolIgnoreAmb() {
        return getNumPalavrasWithPolIgnoreAmb(-1); // sem limite
    }

    public int getNumPalavrasWithPolIgnoreAmb(int iteration_limit) {
        //
        // código do printGraphResume, com mas uma condição no ciclo for, para 
        // parar na iteration_limit.
        //
        int[] wByIt = getNumOfWordsByIt(false);
        int[] wByItIgnorAmb = getNumOfWordsByIt(true);
        if (wByIt == null) {
            return 0;
        }

        int totalTemp = 0;
        int totalTempIgnorAmb = 0;
        //System.out.println("It.  #words #SubTotal | #WordsIg.Amb #SubTotalIg.Amb");
        for (int i = 0; i < wByIt.length && (i <= iteration_limit || iteration_limit < 0); i++) {
            if (wByIt[i] <= 0) {
                break;
            }


            totalTemp += wByIt[i];
            totalTempIgnorAmb += wByItIgnorAmb[i];
            // System.out.printf("%3d, %6d  - %6d | %10d - %6d %n", 
            //         i, wByIt[i], totalTemp, wByItIgnorAmb[i], totalTempIgnorAmb);
        }
        return totalTempIgnorAmb;
    }

    public void testarGraphDelPhi() {
        GelphiGexf graph = new GelphiGexf();

        graph.addNode("14750", "0", "250");
        graph.addNodeAtt("14750", "0", "1", null, null);

        graph.toString();
    }
}
