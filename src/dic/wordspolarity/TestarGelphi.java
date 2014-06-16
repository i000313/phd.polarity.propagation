/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dic.wordspolarity;

/**
 *
 * @author PSantos
 */
public class TestarGelphi {
    
    public static void main(String args[]) {
        
//        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
//                        "<gexf xmlns=\"http://www.gexf.net/1.2draft\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\" version=\"1.2\">" +
//                        "<meta lastmodifieddate=\"2009-03-20\">" +
//                            "<creator>Gexf.net</creator>" +
//                            "<description>A Web network changing over time</description>" +
//                        "</meta>";
//        
//        
//        String footer = "</gexf>";
//        
//        String graph = "<graph defaultedgetype=\"directed\">";
//        graph += "<nodes>";
//        graph += "<node id=\"0\" label=\"Hello\"/>";
//        graph += "<node id=\"1\" label=\"Word\"/>";
//        graph += "</nodes>";
//        graph += "<edges>";
//        graph += "<edge id=\"0\" source=\"0\" target=\"1\"/>";
//        graph += " </edges>";
//        graph += "</graph>";
//        
//        System.out.println(header);
//        System.out.println(graph);
//        System.out.println(footer); 
        
        
        EdgeWeightedGraph G = new EdgeWeightedGraph(6, true);
        
        G.addEdge(new Edge(0, 1, 5.0));
        G.addEdge(new Edge(0, 2, 5.0));
        G.addEdge(new Edge(0, 3, 5.0));
        G.addEdge(new Edge(0, 4, 5.0));
        
        G.addEdge(new Edge(3, 4, 5.0));
        G.addEdge(new Edge(3, 5, 5.0));
        
        System.out.println( G.toString() );
        
        BreadthFirstPaths bfs = new BreadthFirstPaths(G, 0);
        
        
//        bfs.createGraphForGephi(G, null, false, 
//                "E:\\TEMP\\Gephi\\polarity\\polarityGraph.gexf");
    }
}
