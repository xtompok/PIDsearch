
package pidsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author jethro
 */
public class TextInterface {
    static SearchConnection search;
    static SearchPreferences prefs;

    
    /**
     *
     * @param s
     * @param pref
     */
    public static void main(SearchConnection s, SearchPreferences pref){
        search = s;
        prefs = pref;
        
        if (prefs.from == null || prefs.to == null || prefs.when == null) {
            prefs = interactiveSearch();
        }
        Set<Arrival> found;
        found = search.searchConnection(prefs);
        printConnections(found);
    
    }

    /**
     *
     * @return
     */
    public static SearchPreferences interactiveSearch() {
        SearchPreferences prefs;
        prefs = new SearchPreferences();
        prefs.from = getStation("vychozi");
        prefs.to = getStation("cilove");
        Calendar cal;
        prefs.when = Calendar.getInstance();
        return prefs;

    }

    /**
     *
     * @param type
     * @return
     */
    public static Vertex getStation(String type) {
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(System.in, Charset.forName("utf-8")));

        String st;
        st = null;
        do {
            System.out.print("Zadejte jmeno " + type + " stanice:");
            try {
                st = in.readLine();
            } catch (IOException ex) {
                System.err.println("Failed to read a station name");
                System.exit(1);
            }
        } while (!Utilities.isVertexForName(st));
        return Utilities.getVertexForName(st);
    }

    /**
     *
     * @param cons
     */
    public static void printConnections(Set<Arrival> cons) {
        if (cons == null) {
            System.out.println("Spojeni nenalezeno");
            return;
        }
        for (Arrival a : cons) {
            List<Edge> edges = Utilities.condenseEdges(a.asList());
            System.out.print("Spojeni z " + edges.get(0).from.name);
            System.out.println(" do " + edges.get(edges.size() - 1).to.name);

            for (Edge e : edges) {
                if (e instanceof ConEdge) {
                    printConEdge((ConEdge) e);
                } else {
                    printWalkEdge((WalkEdge) e);
                }
            }
            System.out.println();
        }
    }

    /**
     *
     * @param e
     */
    public static void printConEdge(ConEdge e) {
        System.out.print(e.connection.name /*+ "("+e.connection.hashCode()+")"*/ + " " + e.from.name + "(" + Utilities.strTime(e.departure) + ") -> ");
        System.out.println(e.to.name + "(" + Utilities.strTime(e.departure + e.length) + ")");
    }

    /**
     *
     * @param e
     */
    public static void printWalkEdge(WalkEdge e) {
        System.out.println(e.from.name + " -> " + e.to.name + " (" + e.length + ")");
    }
}
