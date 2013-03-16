package pidsearch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Uilities for searching.
 *
 * This class is a collection of methods, whose are used across the program and 
 * does not require context, such as parsing date. All these methods are static. 
 * 
 * @author jethro
 */
public class Utilities {
    private static Map<String,Vertex> vertexForName;
    
    /** Generate Map nameOfTheStation->Vertex.
     * 
     * This methods gets the array of vertices and creates a Map, where keys are
     * names of the vertices and values are Vertex objects with that name. If there
     * are more objects with same name, one of them is chosen. This method must
     * be called before {@link pidsearch.Utilities#getVertexForName(java.lang.String)  getVertexForName}
     * or {@link pidsearch.Utilities#isVertexForName(java.lang.String) isVertexForName}
     * is called.
     *
     * @param vertices Array of vertices to create Map from.
     */
    public static void genVertexForName(Vertex[] vertices){
        vertexForName = new HashMap<String, Vertex>();
        for (Vertex v : vertices) {
            vertexForName.put(v.name, v);
        }
    }
    
    /** Gets Vertex with given name.
     *
     * @param name Name of the station.
     * @return Vertex for the given name or null if it doesn't exists.
     * @see pidsearch.Utilities#genVertexForName(pidsearch.Vertex[]) 
     */
    public static Vertex getVertexForName(String name){
        if (vertexForName == null)
            throw new NullPointerException("vertexForName unitialized");
        else return vertexForName.get(name);
    }
    
    /** Is there a station of given name?.
     *
     * @param name Name of the station.
     * @return true, if there exist a station of given name, else false.
     * @see pidsearch.Utilities#genVertexForName(pidsearch.Vertex[]) 
     */
    public static boolean isVertexForName(String name){
        if (vertexForName == null)
            throw new NullPointerException("vertexForName unitialized");
        else return vertexForName.containsKey(name);
    }
    

    /** Make a string from given time.
     *
     * @param time time in minutes after midnight
     * @return String in formar H.MM, where H is hour in 24-hour format and MM 
     * is two-digit minutes number.
     */
    public static String strTime(int time) {
        return String.format("%d.%02d", time / 60, time % 60);
    }
    /** Make a string from given time.
     *
     * @param cal Calendar object with set time to show.
     * @return String in formar H.MM, where H is hour in 24-hour format and MM 
     * is two-digit minutes number.
     */
    public static String strTime(Calendar cal){
        return new SimpleDateFormat("HH.mm").format(cal.getTime());
    }
    
    /** Make a string from given date.
     *
     * @param cal Calendar object with set date to show.
     * @return String in format D.M.YYYY, where D is day of month, M is number
     * of month and YYYY is four-digit year.
     */
    public static String strDate(Calendar cal){
        return new SimpleDateFormat("d.M.yyyy").format(cal.getTime());
    }
    
    /** Make a string from given date and time in current locale.
     *
     * @param cal Calendar object with set date and time to show
     * @return String in current locale format with given date and time.
     */
    public static String debugDate(Calendar cal){
        return DateFormat.getInstance().format(cal.getTime());
    }

    /** Parse date from string.
     * 
     * This method parses date from given string and creates a new Calendar object 
     * with set date. Time and other fields in that object are set to current value.
     *
     * @param str Date string in format DD[.MM[.YYYY]], where DD is day, MM is month
     * and YYYY is year. If MM or YYYY is omitted, it is set to current.
     * @return Calendar object with set date or null uf there was a problem while
     * parsing date.
     */
    public static Calendar parseDate(String str) {
        return parseDate(str, Calendar.getInstance());
    }

    /** Parse date from string.
     * 
     * This method parses date from string and sets the specified fields in given
     * Calendar object, others are untouched and return that object.
     *
     * @param str Date string in format DD[.MM[.YYYY]], where DD is day, MM is month
     * and YYYY is year. If MM or YYYY is omitted, it stays untouched. 
     * @param cal Calendar object to modify.
     * @return Modified cal or null if there was a problem while parsing date.
     */
    public static Calendar parseDate(String str, Calendar cal) {
        String[] parts;
        parts = str.split("\\.");
        if (parts.length > 3) {
            System.out.println("Wrong date " + str);
            return null;
        }
        int day = 0;
        int month = 0;
        int year = 0;
        if (parts.length > 0) {
            try {
                day = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                System.out.println("Wrong date " + str);
                return null;
            }
        }
        if (parts.length > 1) {
            try {
                month = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
            }
        }
        if (parts.length > 2) {
            try {
                year = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
            }
        }
        if (day != 0) {
            cal.set(Calendar.DAY_OF_MONTH, day);
        }
        if (month != 0) {
            cal.set(Calendar.MONTH, month-1);
        }
        if (year != 0) {
            cal.set(Calendar.YEAR, year);
        }
        return cal;
    }

    /** Parse time from string.
     *
     * This method parses time from string and makes a Calendar object with set 
     * time. Others values stays untouched.
     * 
     * @param str String in format HH.MM, where HH is hour in 24-hour day and MM
     * are minutes. 
     * @return Calendar object with set time or null if there was a problem while
     * parsing time.
     */
    public static Calendar parseTime(String str) {
        return parseTime(str, Calendar.getInstance());
    }

    /** Parse time from string.
     *
     * This method parses time from string and modifies time in given Calendar
     * object. Others values stays untouched.
     * 
     * @param str String in format HH.MM, where HH is hour in 24-hour day and MM
     * are minutes.
     * @param cal Calendar object to modify.
     * @return Modified Calendar object with set time or null if there was 
     * a problem while parsing time.
     */
    public static Calendar parseTime(String str, Calendar cal) {
        String[] parts;
        parts = str.split("\\.");
        if (parts.length != 2) {
            System.out.println("Wrong time seg "+ parts.length+"," + str);
            return null;
        }
        int hour;
        int min;
        try {
            hour = Integer.parseInt(parts[0]);
            min = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong time " + str);
            return null;
        }
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        return cal;
    }
    
    /** Merge sequence of edges of one connection to one edge.
     * 
     * This method is designed to simplify connection before it is showed to 
     * the user. If one Connection is used for more stations, it is showed as
     * more sequential edges. This method takes theese edges and merges them 
     * into one edge. On the output there aren't two walk edges in sequence or 
     * two ConEdges which belongs to the same Connection in sequence.
     *
     * @param edges List of edges of one found connection
     * @return Simplified List of edges of the found connection
     */
    public static List<Edge> condenseEdges(List<Edge> edges) {
        List<Edge> list;
        list = new LinkedList<Edge>();
        WalkEdge we;
        we = null;
        ConEdge ce;
        ce = null;
        for (Edge e : edges) {
            if (e instanceof WalkEdge) {
                if (ce != null) {
                    list.add(ce);
                    ce = null;
                }
                if (we == null) {
                    we = new WalkEdge();
                    we.from = e.from;
                    we.length = 0;
                }
                we.to = e.to;
                we.length += e.length;
            } else if (e instanceof ConEdge) {
                if (we != null) {
                    list.add(we);
                    we = null;
                }
                if (ce == null) {
                    ce = new ConEdge();
                    ce.departure = ((ConEdge) e).departure;
                    ce.connection = ((ConEdge) e).connection;
                    ce.from = e.from;
                }

                if (!ce.connection.equals(((ConEdge) e).connection)) {
                    list.add(ce);
                    ce = new ConEdge();
                    ce.departure = ((ConEdge) e).departure;
                    ce.from = e.from;
                    ce.connection = ((ConEdge) e).connection;
                }

                ce.to = ((ConEdge) e).to;
                ce.length += e.length;
            }
        }
        if ((we != null) && (!we.from.name.equals(we.to.name))) {
            list.add(we);
        }
        if (ce != null) {
            list.add(ce);
        }
        return list;
    }
}
