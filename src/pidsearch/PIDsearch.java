/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jethro
 */
public class PIDsearch {

    Map<String, Vertex> vertexForName;
    Vertex [] vertices;
    Connection [] connections;
    ConEdge [] edges;
    WalkEdge [] walks;
    static String dataFile = "PrepareData.obj";
    
    SearchConnection search;

    public PIDsearch() {

        PrepareData pd;
        pd = null;
        boolean save;
        save = false;
        long start;
        long end;
         try {
         ObjectInputStream in = new ObjectInputStream(
                 new BufferedInputStream(
                    new FileInputStream(dataFile)));
         System.out.println("Reading data from file");
         start = System.nanoTime();
         pd = (PrepareData) in.readObject();
         in.close();
         end = System.nanoTime();
         System.err.println("Loading took "+(end-start)/1000000000+" seconds.");
         } catch (IOException e) {
            System.out.println("Reading failed, generating");
            start = System.nanoTime();
            pd = new PrepareData();
            pd.makeData();
            end = System.nanoTime();
            System.err.println("Generating took "+(end-start)/1000000000+" seconds.");
            save = true;
         } catch (ClassNotFoundException e) {
            System.err.println("Class for data not found");
         }
        if (save) {
            System.out.println("Saving data to file " + dataFile);
            try {
                ObjectOutputStream out = new ObjectOutputStream(
                        new BufferedOutputStream(
                            new FileOutputStream(dataFile)));
                System.out.println("Stream opened");
                start = System.nanoTime();
                out.writeObject(pd);
                out.close();
                end = System.nanoTime();
                System.err.println("Saving took "+((end-start)/1000000000)+" seconds.");
                System.out.println("Data saved");
            } catch (IOException e) {
                System.err.println(e);
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
        
        search = new SearchConnection(vertices, connections, edges, walks);

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
        in = new BufferedReader(new InputStreamReader(System.in,Charset.forName("utf-8")));

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
            cons = search.searchConnection(from, to, cal);
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
                    list.add(ce);
                    ce = new ConEdge();
                    ce.departure = ((ConEdge) e).departure;
                    ce.from = e.from;
                    ce.connection = ((ConEdge)e ).connection;
                }

                ce.to = ((ConEdge) e).to;
                ce.length += e.length;
            }
        }
        if ((we != null)&&(!we.from.name.equals(we.to.name))) {
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
        System.out.println(e.from.name + " -> " + e.to.name + " (" + e.length + ")");
    }

    public String strTime(int time) {
        return String.format("%d.%02d", time/60,time%60);
    }


    
}
