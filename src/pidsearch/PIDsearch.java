/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Compare;

/**
 *
 * @author jethro
 */
public class PIDsearch {

    Map<String, Vertex> vertexForName;
    List<Vertex> vertices;
    List<Connection> connections;
    List<ConEdge> edges;
    List<WalkEdge> walks;
    HashMap<Vertex, Boolean> usedVertex;
    static String dataFile = "PrepareData.obj";

    public PIDsearch() {

        PrepareData pd;
        pd = null;
        boolean save;
        save = false;
        /*
         try {
         ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile));
         System.out.println("Reading data from file");
         pd = (PrepareData) in.readObject();
         in.close();
         } catch (IOException e) {*/
        System.out.println("Reading failed, generating");
        pd = new PrepareData();
        /*  save = true;
         } catch (ClassNotFoundException e) {
         System.err.println("Class for data not found");
         }
         */
        save = false;
        if (save) {
            System.out.println("Saving data to file " + dataFile);
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFile));
                System.out.println("Stream opened");
                out.writeObject(pd);
                out.close();
                System.out.println("Data saved");
            } catch (IOException e) {
                System.err.println("Error while saving data");
            }
        }

        vertices = pd.vertices;
        connections = pd.connections;
        edges = pd.edges;
        walks = pd.walks;

        vertexForName = new HashMap<String, Vertex>();
        for (Vertex v : vertices) {
            vertexForName.put(v.name, v);
        }

        usedVertex = new HashMap<Vertex, Boolean>();
        for (Vertex v : vertices) {
            usedVertex.put(v, Boolean.FALSE);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        PIDsearch search;
        search = new PIDsearch();
        search.CLI();
    }

    public Vertex getStation(String type) {
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(System.in));

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
        } while (!vertexForName.containsKey(st));
        return vertexForName.get(st);
    }

    public void CLI() {
        Vertex from;
        Vertex to;
        while (true) {
            from = getStation("vychozi");
            to = getStation("cilove");
            Calendar cal;
            cal = Calendar.getInstance();
            //cal.set(Calendar.HOUR_OF_DAY, 12);
            //Date d = cal.getTime();
            List<Arrival> cons;
            cons = searchConnection(from, to, cal);
            printConnections(cons);
        }
    }

    public void printConnections(List<Arrival> cons) {
        if (cons == null){
            System.out.println("Spojeni nenalezeno");
            return;
        }
        for (Arrival a : cons) {
            List<Edge> edges = condenseEdges(a.asList());
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

    public List<Edge> condenseEdges(List<Edge> edges) {
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
                    ce.length = ((ConEdge) e).departure - ce.departure + e.length;
                    list.add(ce);
                    ce = new ConEdge();
                    ce.departure = ((ConEdge) e).departure;
                    ce.from = e.from;
                    ce.connection = ((ConEdge)e ).connection;
                }

                ce.to = ((ConEdge) e).to;
            }
        }
        if (we != null) {
            list.add(we);
        }
        if (ce != null) {
            list.add(ce);
        }
        return list;
    }

    public void printConEdge(ConEdge e) {
        System.out.print(e.connection.name + " " + e.from.name + "(" + strTime(e.departure) + ") -> ");
        System.out.println(e.to.name + "(" + strTime(e.departure + e.length) + ")");
    }

    public void printWalkEdge(WalkEdge e) {
        System.out.println(e.from.name + " -> " + e.to.name + "(" + e.length + ")");
    }

    public String strTime(int time) {
        return (time / 60) + "." + (time % 60);
    }

    class ArrivalComparator implements Comparator<Arrival> {

        @Override
        public int compare(Arrival a1, Arrival a2) {
            return a1.arrival - a2.arrival;
        }
    }

    public List<Arrival> searchConnection(Vertex from, Vertex to, Calendar when) {
        System.out.println("Searching connection from " + from.name + " to " + to.name);

        ArrivalComparator ac;
        ac = new ArrivalComparator();

        PriorityQueue<Arrival> stubs;
        stubs = new PriorityQueue<Arrival>(100, ac);
        int minute = when.get(Calendar.HOUR_OF_DAY) * 60 + when.get(Calendar.MINUTE);


        int wait = 100;

        List<ConEdge> departs;
        departs = findDepartures(from, minute, when, wait);

        for (ConEdge e : departs) {
            stubs.add(new Arrival(e));
        }

        for (WalkEdge v : from.walks) {
            departs = findDepartures(v.to, minute, when, wait);
            for (ConEdge e : departs) {
                stubs.add(new Arrival(e));
            }
        }

        usedVertex.put(from, Boolean.TRUE);

        Arrival first;
        first = null;
        boolean found;
        found = false;

        while (!found) {
            first = stubs.poll();
            if (first==null){
                return null;
            }

            departs = findDepartures(first.edge.to, first.arrival, when, wait);

            for (ConEdge e : departs) {

                if (e.to.equals(to)) {
                    found = true;
                }
                if (!usedVertex.get(e.to)) {
                    stubs.add(new Arrival(first, e));
                    usedVertex.put(e.to, Boolean.TRUE);
                }

            }

            for (WalkEdge e : first.edge.to.walks) {
                if (e.to.name.equals(to.name)) {
                    found = true;
                }
                if (!usedVertex.get(e.to)) {
                    stubs.add(new Arrival(first, e));
                    usedVertex.put(e.to, Boolean.TRUE);
                }
            }
        }

        return toFound(stubs, to);
    }

    public List<ConEdge> findDepartures(Vertex from, int minute, Calendar date, int range) {
        List<ConEdge> departs;
        departs = from.departs;


        ConEdge whenEdge;
        whenEdge = new ConEdge();

        DepartComparator dc;
        dc = new DepartComparator();

        int minIndex;
        whenEdge.departure = minute;
        minIndex = Collections.binarySearch(departs, whenEdge, dc);
        minIndex = (minIndex < 0) ? -(minIndex + 1) : minIndex;

        int maxIndex;
        whenEdge.departure = minute + range;
        maxIndex = Collections.binarySearch(departs, whenEdge, dc);
        maxIndex = (maxIndex < 0) ? -(maxIndex + 1) : maxIndex;

        List<ConEdge> departEdges;
        departEdges = new LinkedList<ConEdge>();
        if (minIndex > maxIndex) {
            return departEdges;
        }
        for (ConEdge e : departs.subList(minIndex, maxIndex)) {
            if (e.connection.goesAt(date)) {
                departEdges.add(e);
            }
        }
        return departEdges;
    }

    public List<Arrival> toFound(PriorityQueue<Arrival> stubs, Vertex to) {
        List<Arrival> found;
        found = new LinkedList<Arrival>();
        for (Arrival stub : stubs) {
            if (stub.edge.to.name.equals(to.name)) {
                found.add(stub);
            }
        }
        return found;
    }
}
