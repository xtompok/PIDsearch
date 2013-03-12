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
    Vertex[] vertices;
    Connection[] connections;
    ConEdge[] edges;
    WalkEdge[] walks;
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
            System.err.println("Loading took " + (end - start) / 1000000000 + " seconds.");
        } catch (IOException e) {
            System.out.println("Reading failed, generating");
            start = System.nanoTime();
            pd = new PrepareData();
            pd.makeData();
            end = System.nanoTime();
            System.err.println("Generating took " + (end - start) / 1000000000 + " seconds.");
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
                start = System.nanoTime();
                out.writeObject(pd);
                out.close();
                end = System.nanoTime();
                System.err.println("Saving took " + ((end - start) / 1000000000) + " seconds.");
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
        SearchPreferences prefs;
        prefs = search.parseCommandLine(args);
        if (prefs.graphics) {
            GraphicsSearch.main(search);
            return;
        }
        if (prefs.from == null || prefs.to == null || prefs.when == null) {
            prefs = search.interactiveSearch();
        }
        List<Arrival> found;
        found = search.search.searchConnection(prefs);
        search.printConnections(found);

    }

    public Vertex getStation(String type) {
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
        } while (!vertexForName.containsKey(st));
        return vertexForName.get(st);
    }

    SearchPreferences parseCommandLine(String[] args) {
        // -f -t -d -D -q -t
        int i = 0;
        String arg;

        SearchPreferences prefs;
        prefs = new SearchPreferences();


        prefs.when = Calendar.getInstance();

        while (i < args.length) {
            arg = args[i];
            if (arg.equals("-f")) {
                if (i == args.length - 1) { // From
                    System.out.println("Missing argument for -f");
                    System.exit(1);
                }
                i++;
                prefs.from = vertexForName.get(args[i]);
                if (prefs.from == null) {
                    System.out.println("Can't find station " + args[i]);
                    System.exit(1);
                }
            } else if (arg.equals("-t")) { // To
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -t");
                    System.exit(2);
                }
                i++;
                prefs.to = vertexForName.get(args[i]);
                if (prefs.to == null) {
                    System.out.println("Can't find station " + args[i]);
                    System.exit(1);
                }
            } else if (arg.equals("-d")) { // Date DD.MM.YYYY
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -d");
                    System.exit(3);
                }
                i++;
                prefs.when = Utilities.parseDate(args[i], prefs.when);
                if (prefs.when==null){
                    System.exit(3);
                }

            } else if (arg.equals("-D")) { // Timestamp
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -D");
                    System.exit(4);
                }
                i++;
                int timestamp = 0;
                try {
                    timestamp = Integer.parseInt(args[i]);
                } catch (NumberFormatException e) {
                    System.out.println(args[i] + " is not valid timestamp");
                    System.exit(4);
                }
                prefs.when.setTimeInMillis(timestamp * 1000);

            } else if (arg.equals("-q")) { // Quiet
                prefs.quiet = true;
            } else if (arg.equals("-t")) {
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -t");
                    System.exit(5);
                }
                i++;
                prefs.when = Utilities.parseTime(args[i],prefs.when);
                if (prefs.when==null){
                    System.exit(5);
                }
            } else if (arg.equals("-g")) {
                prefs.graphics = true;
            } else {
                System.out.println("Unknown argument " + args[i]);
                System.exit(240);
            }
            i++;

        }
        return prefs;
    }

    public SearchPreferences interactiveSearch() {
        SearchPreferences prefs;
        prefs = new SearchPreferences();
        prefs.from = getStation("vychozi");
        prefs.to = getStation("cilove");
        Calendar cal;
        prefs.when = Calendar.getInstance();
        //cal.set(Calendar.HOUR_OF_DAY, 12);
        return prefs;

    }

    public void printConnections(List<Arrival> cons) {
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

    

    public void printConEdge(ConEdge e) {
        System.out.print(e.connection.name /*+ "("+e.connection.hashCode()+")"*/ + " " + e.from.name + "(" + Utilities.strTime(e.departure) + ") -> ");
        System.out.println(e.to.name + "(" + Utilities.strTime(e.departure + e.length) + ")");
    }

    public void printWalkEdge(WalkEdge e) {
        System.out.println(e.from.name + " -> " + e.to.name + " (" + e.length + ")");
    }


}
