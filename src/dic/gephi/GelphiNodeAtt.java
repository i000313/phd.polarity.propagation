package dic.gephi;

/**
 *
 * @author PSantos
 */
public class GelphiNodeAtt {

    private String for_idx = null;
    private String value = null;
    private String start = null;
    private String end = null;

    public GelphiNodeAtt(String for_idx, String value, String start, String end) {
        this.for_idx = for_idx;
        this.value = value;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        String a = "<attvalue for=\"" + for_idx + "\" value=\"" + value + "\" ";
        if (start != null) {
            a += "start=\"" + start + "\"";
        }
        if (end != null) {
            a += "end=\"" + end + "\"";
        }
        a += "/>";
        return a;
    }
}
