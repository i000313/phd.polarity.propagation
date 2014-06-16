package dic.gephi;

/**
 *
 * @author PSantos
 */
public class GelphiEdge {

    private String id = null;
    private String source = null;
    private String target = null;
    private String start = null;
    private String end = null;
    
    public GelphiEdge(String id, String source, String target, String start, String end) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.start = start;
        this.end = end;      
    }

    @Override
    public String toString() {
        String e = "<edge id=\"" + id + "\" source=\"" + source + "\" target=\"" + target + "\"";

        if (start != null) {
            e += " start=\"" + start + "\"";
        }
        if (end != null) {
            e += " end=\"" + end + "\"";
        }
        e += "/>";

        System.out.println("\t" + e);
        return null;
    }
}
