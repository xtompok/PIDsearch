
package pidsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** Text interface for searching a connection.
 * 
 * This class provides a text interface for searching connections. All methods of
 * this class are static, no instance is created. It has methods for interactive 
 * search and for printing found connections out.
 *
 * @author jethro
 */
public class TextInterface {
    static SearchConnection search;
    static SearchPreferences prefs;

    
    /** Run text interface.
     * 
     * This method checks, if from, to and when attribute of SearchPreferences is
     * defined and if not, runs interactive search. Then searches the connection
     * and prints out found connections.
     *
     * @param srch Initialized search object.
     * @param pref Search preferences filled with command line attributes.
     */
    public static void main(SearchConnection srch, SearchPreferences pref){
        search = srch;
        prefs = pref;
        
        if (prefs.from == null || prefs.to == null || prefs.when == null) {
            prefs = interactiveSearch();
        }

        printConnections(search.searchConnection(prefs));
    
    }

    /** Get search parameters interactively.
     * 
     * Interactively asks for name of from station, to station and sets date and
     * time to current date and time.
     * 
     * @return SearchPreferences with filled from, to and date.
     */
    public static SearchPreferences interactiveSearch() {
        SearchPreferences prefs;
        prefs = new SearchPreferences();
        prefs.from = getStation("vychozi");
        prefs.to = getStation("cilove");
        prefs.when = Calendar.getInstance();
        return prefs;

    }

    /** Interactively get station.
     * 
     * This method asks for name of the station, until it gets valid name of Vertex.
     *
     * @see pidsearch.Utilities#isVertexForName isVertexForName
     * @param type String in czech describing type of the station, will be inserted
     * in sentece "Zadejte jmeno" + {@code type} + "stanice:"
     * @return Vertex with name of given station.
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

    /** Print found connections.
     * 
     * This method gets set of found connections, 
     * {@link pidsearch.Utilities#condenseEdges condense} them and prints them to 
     * the standard output. For each edge uses 
     * {@link pidsearch.TextInterface#printConEdge printConEdge} respectively
     * {@link pidsearch.TextInterface#printWalkEdge printWalkEdge} for 
     * {@link pidsearch.ConEdge ConEdge} respectively 
     * {@link pidsearch.WalkEdge WalkEdge}.
     * 
     * If the connection was not found, prints {@code Spojeni nenalezeno}
     * 
     * @param cons Set of found connections.
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

    /** Print ConEdge to standard output.
     * 
     * Format: {@code from (departure) -> to (arrival) }
     *
     * @param edge Edge to print.
     */
    public static void printConEdge(ConEdge edge) {
        System.out.print(edge.connection.name  + " " + edge.from.name + "(" + Utilities.strTime(edge.departure) + ") -> ");
        System.out.println(edge.to.name + "(" + Utilities.strTime(edge.departure + edge.length) + ")");
    }

    /** Print WalkEdge to standard output.
     *
     * Format: {@code from -> to (length)}
     * 
     * @param edge Edge to print.
     */
    public static void printWalkEdge(WalkEdge edge) {
        System.out.println(edge.from.name + " -> " + edge.to.name + " (" + edge.length + ")");
    }
}
