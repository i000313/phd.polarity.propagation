package dic.gephi;

import dic.wordspolarity.LoaderPAPEL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author PSantos
 */
public class GelphiGexf {
    private Map<String, GelphiNode> nodes = null;
    private Map<String, GelphiEdge> edges = null;
    
    private String max_time = null;
    public LoaderPAPEL papel = null;
    public int it[] = null;    
    
    public GelphiGexf() {
        nodes = new HashMap<String, GelphiNode>();
        edges = new HashMap<String, GelphiEdge>();
    }
    
    public GelphiGexf(LoaderPAPEL papel) {
        nodes = new HashMap<String, GelphiNode>();
        edges = new HashMap<String, GelphiEdge>();
        this.papel = papel;
    }    
    
    public void setIterations(int[] it) {
        this.it = it;
    }
    
    public void addNode(String id, String start, String end) {
        // Add only if the not yet inserted
        if (nodes.get(id) == null) {
            nodes.put(id, new GelphiNode(id, start, end));
            max_time = max(max_time, start);
            max_time = max(max_time, end);
        }
    }

    public void addNodeAtt(String node_id, String for_idx, String value, String start, String end) {
        // Get node
        GelphiNode n = nodes.get(node_id);
        n.addAtt(for_idx, value, start, end);
    }

    public void addNodeAttSpecial(String node_id, String for_idx, String value, String start, String end) {
        // Get node
        GelphiNode n = nodes.get(node_id);
        n.addAttSpecial(for_idx, value, start, end);
    }
    
    public void addEdge(String id, String source, String target, String start, String end) {
        // Add only if the not yet inserted
        if (edges.get(id) == null) {
            edges.put(id, new GelphiEdge(id, source, target, start, end));
            max_time = max(max_time, start);
            max_time = max(max_time, end);            
        }
    }

    @Override
    public String toString() {
        
        System.out.println(toStringHeader());
        
        System.out.println("<nodes>");
        Iterator<GelphiNode> it = nodes.values().iterator();
        while (it.hasNext()) {
            GelphiNode n = it.next();
            
            // define um end para o nó. A indicação de um "start" sem "end" no XML, parece não funcionar (Gelphi 0.8 beta).
            n.setEnd(max_time);
            
            String label = null;
            String pos_tag = null;
            if (papel != null) // se foi passado um objecto com informção sobre as palvras, usa-o.
            {
                int v = Integer.parseInt(n.getId());
                label = papel.getWordById(v) /*+ separator + papel.getClasseGramatical(v)*/;
                pos_tag = papel.getClasseGramatical(v);
                n.setLabel(label);
                
                n.addAtt("3", "" + this.it[v], null, null);
            }
            
            n.toString();
        }
        System.out.println("</nodes>");

        System.out.println("<edges>");
        Iterator<GelphiEdge> it_edges = edges.values().iterator();
        while (it_edges.hasNext()) {
            it_edges.next().toString();
        }
        System.out.println("</edges>");
        
            System.out.println("</graph>");
            System.out.println("</gexf>");
            
        return null;
    }
    
    private String toStringHeader() {
  //
            // Header
            //
            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
                    + "\n<gexf xmlns=\"http://www.gexf.net/1.2draft\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\" version=\"1.2\">"
                    + "\n<meta lastmodifieddate=\"2009-03-20\">"
                    + "\n  <creator>Gexf.net</creator>"
                    + "\n  <description>A Web network changing over time</description>"
                    + "\n</meta>";
            //out_file.write(header);

            String gType = false /*G.isDirectedGraph()*/ ? "directed" : "undirected";
            
            String graph = "\n<graph mode=\"dynamic\" defaultedgetype=\""+gType+"\">";
            String attrib = graph + "\n<attributes class=\"node\" mode=\"static\"> ";
            attrib += "\n\t<attribute id=\"0\" title=\"pos\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"1\" title=\"neg\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"2\" title=\"neu\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"3\" title=\"it\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"4\" title=\"pos_tag\" type=\"string\"/>";
            attrib += "\n</attributes>"; 
            attrib += "\n<attributes class=\"node\" mode=\"dynamic\"> ";
            attrib += "\n\t<attribute id=\"5\" title=\"pol\" type=\"integer\"/>";
            attrib += "\n\t<attribute id=\"6\" title=\"wei\" type=\"float\"/>";
//            attrib += "\n\t  <default>-1</default>";
//            attrib += "\n\t</attribute>";
            attrib += "\n</attributes>";   
            
            return header + attrib;
    }
    
    
    private String max(String fst, String snd) {
        if(fst != null)
        if(snd == null || (Integer.valueOf(fst) > Integer.valueOf(snd)))
            return fst;
        
        return snd;
    }
}
