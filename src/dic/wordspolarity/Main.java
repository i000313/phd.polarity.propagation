
package dic.wordspolarity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Classe para tornar a aplicação executável a partir de um jar.
 * Para ver as experiências da PROPOR 2012, consultar a classe "EdgeWeightedGraph".  
 * 
 * @author PSantos
 */
public class Main {
    

    public static void main(String[] args) {
       
        //
        // Configuração da experiência
        //
        boolean directedGraph = false;
        boolean avoidRicochet = true;
        boolean evalAsEPIA2011 = false;// true = epia2011, false = propor2012
       
        boolean verbose = false; // Output extra information
        
        String seedWordsFile = "seed_words.txt"; // "E:\\Development\\Graph-Java\\seedsTeste.txt"; //seedsPosNegNeuMANSelected.txt"; // seedsPosNegNeuMANSelected.txt seedsPosNegNeu3585.txt seedsPosNeg.txt
        String papelFile = "relacoes_final_SINONIMIA.txt"; // "E:\\Development\\Graph-Java\\relacoes_final.txt";                // relacoes_final.txt triplos_da.txt triplos_wikcionario_tudo.txt
        String evalFile = null; // "E:\\Development\\Graph-Java\\seedsPosNegNeu3585.txt";             // seedsPosNegNeu3585.txt"; 460pal_anotadorAndre_SemAmb.csv
        
        String outputFile = null; // "E:\\Development\\Graph-Java\\polarityLexicon.txt"; 
        boolean outputOnlyWordsWithPolarity = false;
        
        //
        // Read the configuration file and override the abode configuration
        //
        Properties prop = new Properties();
 
//        System.out.println("Args:");
//        for (String s: args) {
//            System.out.println(s);
//        } 
        
    	try {
               //load a properties file
               //prop.load(new FileInputStream("E:/Development/ProjectoDoutoramento/JavaProjects/DoutoramentoPolarityLexicon/config.properties"));
                prop.load(new FileInputStream("config.properties"));
            
               //get the property value and print it out
//    		System.out.println(prop.getProperty("fileAdjacencyList"));
                papelFile = prop.getProperty("fileAdjacencyList");
                
//                System.out.println(prop.getProperty("seedWordsFile"));
                seedWordsFile = prop.getProperty("seedWordsFile");
                
//    		System.out.println(prop.getProperty("outputFile"));
                outputFile = prop.getProperty("outputFile");
                
//                System.out.println(prop.getProperty("outputOnlyWordsWithPolarity"));
                outputOnlyWordsWithPolarity = Boolean.parseBoolean(
                        prop.getProperty("outputOnlyWordsWithPolarity"));
                        
//                System.out.println(prop.getProperty("directedGraph"));
                directedGraph = Boolean.parseBoolean(prop.getProperty("directedGraph"));
                
//                System.out.println(prop.getProperty("avoidRicochet"));
                avoidRicochet = Boolean.parseBoolean(prop.getProperty("avoidRicochet"));
                
//               System.out.println(prop.getProperty("verbose"));
               verbose = Boolean.parseBoolean(prop.getProperty("verbose"));

    	} catch (IOException ex) {
    		ex.printStackTrace();
        }        
        
        
        
        LoaderPAPEL papel = new LoaderPAPEL(papelFile);
        if(verbose)
            papel.printRelations();
            
        
        EdgeWeightedGraph G = EdgeWeightedGraph.createGraphPAPEL(papel, directedGraph);
//            System.out.println("\n------ GRAPH CONSTRUCTION INFO ------");
//            //System.out.print(G); // Imprimir tabela de adjacências
//            System.out.println("AVG Degree: " + 2.0 * G.E() / G.V());
//            System.out.println("---- End Graph ----- INI POLARITY PROPAG. ------");

            //
            // Carregar RANDOM seed words
            //
            LoadSeedWords seedWordsLoader = new LoadSeedWords(seedWordsFile);
            List<String> seeds = seedWordsLoader.getAllSeedWords();
            List<Integer> seedsPol = seedWordsLoader.getAllSeedWordsPolarity();

            //
            // Visita o grafo e propaga a polaridade
            //
            //BreadthFirstPaths bfs = new BreadthFirstPaths(G, papel, false, "E:\\Development\\Graph-Java\\seedsLixo.txt", 12);
            BreadthFirstPaths bfs = new BreadthFirstPaths(G, papel, avoidRicochet, seeds, seedsPol);
            bfs.savePolarityLexicon(papel, outputOnlyWordsWithPolarity, outputFile);

//            bfs.testarGraphDelPhi();
            
//            bfs.createGraphForGephi(papel, true, 
//                "E:\\TEMP\\Gephi\\polarity\\polarityGraph.gexf");
                    
            if(verbose) {
                // bfs.printGraphPol(papel, 10, true);
                bfs.printGraphResume();   
            }
            

            // 460pal_anotadorAndre_SemAmb.csv 
            if(evalFile != null) {
                Evaluation eval = new Evaluation(papel, G, evalAsEPIA2011, 
                        bfs.getPos(), bfs.getNeg(), bfs.getNeu(), bfs.getIt(), evalFile);
                if(verbose)
                    eval.printResult();
            }
   
//            System.out.print("Num known Seeds: " + bfs.seedWordsKnown + " [");
//            for (int i = 0; i < bfs.getSeedWords().size(); i++) {
//                if (i > 0) {
//                    System.out.print(", ");
//                }
//                System.out.print(papel.getWordById(bfs.getSeedWords().get(i)));
//            }
//            System.out.println("]");

//            System.out.printf("Precision (+,-,0): %.2f;%.2f;%.2f%n", eval.precisionPosIgnoreAmb() * 100, eval.precisionNegIgnoreAmb()*100, eval.precisionNeuIgnoreAmb()*100); 
//            System.out.printf("Recall    (+,-,0): %.2f;%.2f;%.2f%n", eval.recallPosIgnoreAmb() * 100, eval.recallNegIgnoreAmb()*100, eval.recallNeuIgnoreAmb()*100); 
//            System.out.printf("F1        (+,-,0): %.2f;%.2f;%.2f%n", eval.f1_measurePosIgnoreAmb() * 100, eval.f1_measureNegIgnoreAmb()*100, eval.f1_measureNeuIgnoreAmb()*100);
                   
        
    }
}
