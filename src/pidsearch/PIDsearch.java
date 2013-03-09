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
        SearchPreferences prefs;
        prefs = search.parseCommandLine(args);
        if (prefs.from==null || prefs.to==null || prefs.when==null){
            prefs = search.interactiveSearch();
        }
        List<Arrival> found;
        found = search.search.searchConnection(prefs);
        search.printConnections(found);

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
    
    
    SearchPreferences parseCommandLine(String [] args){
        // -f -t -d -D -q -t
        int i=0;
        String arg;
       
        SearchPreferences prefs;
        prefs = new SearchPreferences();

        
        prefs.when = Calendar.getInstance();
        
        while (i<args.length){
            arg = args[i];
            if (arg.equals("-f")){
                if (i==args.length-1){ // From
                    System.out.println("Missing argument for -f");
                    System.exit(1);
                }
                i++;
                prefs.from = vertexForName.get(args[i]);
                if (prefs.from == null){
                    System.out.println("Can't find station "+args[i]);
                    System.exit(1);
                }
            }else if (arg.equals("-t")){ // To
                if (i==args.length-1){
                    System.out.println("Missing argument for -t");
                    System.exit(2);
                }
                i++;              
                prefs.to = vertexForName.get(args[i]);
                if (prefs.to == null){
                    System.out.println("Can't find station "+args[i]);
                    System.exit(1);
                }
            }else if (arg.equals("-d")){ // Date DD.MM.YYYY
                if (i==args.length-1){
                    System.out.println("Missing argument for -d");
                    System.exit(3);
                }
                i++;
                String [] parts;
                parts = args[i].split(".");
                if (parts.length!=3){
                    System.out.println("Wrong date "+args[i]);
                    System.exit(3);
                }
                int day=0;
                int month=0;
                int year=0;
                try {
                    day = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e){
                    System.out.println("Wrong day "+parts[0]);
                    System.exit(3);
                }
                try {
                    month = Integer.parseInt(parts[1]);
                }catch (NumberFormatException e){}
                try {
                    year = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e ){}
                prefs.when.set(Calendar.DAY_OF_MONTH, day);
                if (month!=0) prefs.when.set(Calendar.MONTH,month);
                if (year!=0) prefs.when.set(Calendar.YEAR, year);
                
            }else if (arg.equals("-D")){ // Timestamp
                if (i==args.length-1){
                    System.out.println("Missing argument for -D");
                    System.exit(4);
                }
                i++;
                int timestamp=0;
                try {
                    timestamp = Integer.parseInt(args[i]);
                } catch (NumberFormatException e){
                    System.out.println(args[i]+" is not valid timestamp");
                    System.exit(4);
                }
                prefs.when.setTimeInMillis(timestamp*1000);
                
            }else if (arg.equals("-q")){ // Quiet
                prefs.quiet = true;
            }else if (arg.equals("-t")){
                if (i==args.length-1){
                    System.out.println("Missing argument for -t");
                    System.exit(5);
                }
                i++;
                String [] parts;
                parts = args[i].split(".");
                if (parts.length!=2){
                    System.out.println("Wrong time "+args[i]);
                    System.exit(5);
                }
                int hour=0;
                int min=0;
                try {
                    hour = Integer.parseInt(parts[0]);
                    min = Integer.parseInt(parts[1]);
                }catch (NumberFormatException e){
                    System.out.println("Wrong time "+args[i]);
                    System.exit(5);
                }
                prefs.when.set(Calendar.HOUR,hour);
                prefs.when.set(Calendar.MINUTE,min);
            }
            
            else{
                System.out.println("Unknown argument "+args[i] );
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
        System.out.print(e.connection.name /*+ "("+e.connection.hashCode()+")"*/ +" " + e.from.name + "(" + strTime(e.departure) +") -> ");
        System.out.println(e.to.name + "(" + strTime(e.departure + e.length) + ")");
    }

    public void printWalkEdge(WalkEdge e) {
        System.out.println(e.from.name + " -> " + e.to.name + " (" + e.length + ")");
    }

    public String strTime(int time) {
        return String.format("%d.%02d", time/60,time%60);
    }


    
}
