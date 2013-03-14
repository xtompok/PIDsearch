
package pidsearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;


/**
 *
 * @author jethro
 */
public class PIDsearch {

    //Map<String, Vertex> vertexForName;
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

        Utilities.genVertexForName(vertices);
        search = new SearchConnection(vertices, connections, edges, walks);
        

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PIDsearch pidSearch;
        pidSearch = new PIDsearch();
        SearchPreferences prefs;
        prefs = pidSearch.parseCommandLine(args);
        if (prefs.graphics) {
            GraphicalInterface.main(pidSearch.search);
            return;
        } else {
           do 
            TextInterface.main(pidSearch.search,prefs);
           while (prefs.repeat);
        }
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
                prefs.from = Utilities.getVertexForName(args[i]);
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
                prefs.to = Utilities.getVertexForName(args[i]);
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
            } else if (arg.equals("-t")) { // Time
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -t");
                    System.exit(5);
                }
                i++;
                prefs.when = Utilities.parseTime(args[i],prefs.when);
                if (prefs.when==null){
                    System.exit(5);
                }
            } else if (arg.equals("-g")) { // GUI
                prefs.graphics = true;
            } else if (arg.equals("-r")) { // repeat
                prefs.repeat = true;
            }
            
            else {
                System.out.println("Unknown argument " + args[i]);
                System.exit(240);
            }
            i++;

        }
        return prefs;
    }


}
