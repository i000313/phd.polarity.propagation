
package dic.wordspolarity;

import dic.wordspolarity.graphbook.In;

/**
 * Classe to load the dictionairy SentiLex to a database.
 * 
 * @author PSantos
 */
public class LoaderSentiLex {

    private In in;

    public LoaderSentiLex(String fileToLoad) {
        this.in = new In(fileToLoad);
        loadFileInfo();
    }

    private void loadFileInfo() {
        int linhas_conhecidas = 0;
        int linhas_desconhecidas = 0;
        String line = null;
        String[] fields = null;
        String[] subFieldsWord = null;
        String[] subFieldsPol = null;

        int palManAnotadas = 0;
        int palAlgAnotadas = 0;
        int palAnotadorDesc = 0;
        while ((line = in.readLine()) != null) {
            fields = line.split(";");
            if (fields.length == 5 || fields.length == 4) {
                linhas_conhecidas++;

                //System.out.println("Word: " + fields[0] + " Pol: " + fields[3]);
                if (fields[fields.length-1].matches("ANOT=MAN")) {
                    palManAnotadas++;
                    subFieldsWord = fields[0].split("\\.");
                    subFieldsPol = fields[3].split("=");
                    System.out.printf("%s,%s,%s%n", subFieldsWord[0], subFieldsPol[1], subFieldsWord[1]);
                } else if (fields[fields.length-1].matches("ANOT=JALC")) {
                    palAlgAnotadas++;
                } else {
                    palAnotadorDesc++;
                }

            } else {
                linhas_desconhecidas++;
                System.out.println("Linha desconhecida: " + line);
            }
        }
        System.out.println("Palavras anotadas:\n"
                + "Man.: " + palManAnotadas
                + " Alg.: " + palAlgAnotadas
                + " Des.: " + palAnotadorDesc);
    }

    public static void main(String[] args) {
        LoaderSentiLex testeLex = new LoaderSentiLex("E:\\Development\\Graph-Java\\SentiLex-lem-PT02.txt");
      
        // System.out.println("MATCH: " + "espantado SINONIMO_ADJ_DE surpreendido	;fig;".matches(".*(fig|irón).*"));
        
        
//        LoadSeedWords sdw = new LoadSeedWords("E:\\Development\\Graph-Java\\seedsPosNegNeu.txt");
//        
//        for(int i = 0; i < 3; i ++) {
//            System.out.println("POS: " + sdw.getRandomPosWord());
//            System.out.println("NEG: " + sdw.getRandomNegWord());
//            System.out.println("NEU: " + sdw.getRandomNeuWord());
//        }
    }

    
}
