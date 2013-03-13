/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jethro
 */
public class Utilities {
    public static Map<String,Vertex> vertexForName;
    
    public static void genVertexForName(Vertex[] vertices){
        vertexForName = new HashMap<String, Vertex>();
        for (Vertex v : vertices) {
            vertexForName.put(v.name, v);
        }
    }
    
    public static Vertex getVertexForName(String name){
        if (vertexForName == null)
            throw new NullPointerException("vertexForName unitialized");
        else return vertexForName.get(name);
    }
    
    public static boolean isVertexForName(String name){
        if (vertexForName == null)
            throw new NullPointerException("vertexForName unitialized");
        else return vertexForName.containsKey(name);
    }
    

    //TODO rewrite using DateFormat 
    public static String strTime(int time) {
        return String.format("%d.%02d", time / 60, time % 60);
    }
    public static String strTime(Calendar cal){
        return new SimpleDateFormat("HH.mm").format(cal.getTime());
    }
    
    public static String strDate(Calendar cal){
        return new SimpleDateFormat("d.M.yyyy").format(cal.getTime());
    }
    
    public static String debugDate(Calendar cal){
        return DateFormat.getInstance().format(cal.getTime());
    }

    public static Calendar parseDate(String str) {
        return parseDate(str, Calendar.getInstance());
    }

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

    public static Calendar parseTime(String str) {
        return parseTime(str, Calendar.getInstance());
    }

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
