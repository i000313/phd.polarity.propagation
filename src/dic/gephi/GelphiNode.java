package dic.gephi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PSantos
 */
public class GelphiNode {

    private String id = null;
    private String label = null;
    private String start = null;
    private String end = null;
    private List<GelphiNodeAtt> attrbs = null;
            
    public GelphiNode(String id, String start, String end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }
 
    public void setLabel(String label) {
        this.label = label;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void addAtt(String for_idx, String value, String start, String end) {
        if (attrbs == null) {
            attrbs = new ArrayList<GelphiNodeAtt>();
        }

        attrbs.add(new GelphiNodeAtt(for_idx, value, start, end));
    }

    public void addAttSpecial(String for_idx, String value, String start, String end) {
        if (attrbs == null) {
            attrbs = new ArrayList<GelphiNodeAtt>();
            
            attrbs.add(new GelphiNodeAtt(for_idx, value, start, end));
        }
    }
    
    @Override
    public String toString() {

        System.out.print("  <node id=\"" + id + "\" label=\"" + label + "\" start=\"" + start + "\"");
            
        // The end time is optional
        if (end != null) {
            System.out.print(" end=\"" + end + "\"");
        }

        // if the node has attributes
        if (attrbs != null) {
            // close the node XML element
            System.out.println(">");
            System.out.println("    <attvalues>");
            for (GelphiNodeAtt att : attrbs) {
                System.out.println("    " + att);
            }
            System.out.println("    </attvalues>");

            System.out.println("  </node>");
        } else {
            // close the node XML element
            System.out.println("/>");
        }


        return null;
    }
//           // node attributs
//           private class GelphiNodeAtt {
//               private String for_idx = null;
//               private String value = null;
//               private String start = null;
//               private String end = null;
//               
//               public GelphiNodeAtt(String for_idx, String value, String start, String end) {
//                   this.for_idx = for_idx; 
//                   this.value = value; 
//                   this.start = start; 
//                   this.end = end; 
//               }
//               
//               @Override
//                public String toString() {
//                     String a = "<attvalue for=\""+for_idx+"\" value=\""+value+"\" ";
//                     if(start != null) a += "value=\""+value+"\"";
//                     if(end != null) a += "end=\"" +end+ "\"";
//                     a += "/>";
//                     return a; 
//                }
//                           
//           } // End class GelphiNodeAtt
}
