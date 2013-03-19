package pidsearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;


/** Main class of the project.
 * 
 * This class contains main method, and provides parsing command line arguments
 * and loading the timetable from file.
 *
 * @author jethro
 */
public class PIDsearch {

    	/**
	 * Array of vertices.
	 */
	public Vertex[] vertices;
    	/**
	 * Array of connections.
	 */
	public Connection[] connections;
    	/**
	 * Array of connection edges.
	 */
	public ConEdge[] edges;
    	/**
	 * Array of walk edges.
	 */
	public WalkEdge[] walks;
    	/** 
	 * Filename of persistent PrepareData object.
	 */
	public static String dataFile = "PrepareData.obj";
    	/**
	 * Object for searching connection.
	 */
	public SearchConnection search;

    /** Load data and prepare for searching.
     * 
     * This method loads timetable data form persistent object or makes them from
     * the data files, if object is not available. SearchConnection object is also
     * created.
     *
     */
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

    /** Main method.
     * 
     * This method parses the command line arguments, prepares everything for 
     * searching and then runs graphical or command line searching interface.
     * 
     * @see pidsearch.PIDsearch#parseCommandLine
     * @param args the command line arguments
     */
    public static void main(String[] args){
	SearchPreferences prefs;
        prefs = PIDsearch.parseCommandLine(args);
        PIDsearch pidSearch;
        pidSearch = new PIDsearch();
        
        if (prefs.graphics) {
            GraphicalInterface.main(pidSearch.search);
        } else {
           do {
            TextInterface.main(pidSearch.search,prefs);
	    prefs = new SearchPreferences();
	   }
           while (prefs.repeat);
        }
    }

    

    	/** Parse commad line arguments.
	 *
	 * This method gets the command line arguments and parses them. 
	 * Available arguments:
	 * <ul>
	 * <li> 
	 *	{@code -f FROM}<br />
	 *	Name of the station to search connection from.
	 * </li>
	 * <li> 
	 *	{@code -t TO}<br />
	 *	Name of the station to search conection to.
	 * </li>
	 * <li> 
	 *	{@code -D DD[.MM[.YYYY]]}<br />
	 *	Date for searching connection {@link pidsearch.Utilities#parseDate}.
	 * </li>
	 * <li> 
	 *	{@code -d TIMESTAMP}<br />
	 *	Unix timestamp set to date and time when search connection.
	 * </li>
	 * <li> 
	 *	{@code -T HH.MM}<br />
	 *	Time for searchin a connection {@link pidsearch.Utilities#parseTime}.
	 * </li><!--
	 * <li> 
	 *	{@code -q}<br />
	 *	Be not so verbose.
	 * </li>!-->	 
	 * <li> 
	 *	{@code -g}<br />
	 *	Search connection in GUI. All other parameters from command line
	 *	are ignored.
	 * </li>	 
	 * <li> 
	 *	{@code -r}<br />
	 *	Make repeated searches from command line. First search looks on 
	 *	command line parameters, next searches are interactive.
	 * </li><!-->	 
	 * <li> 
	 *	{@code -}<br />
	 * 
	 * </li></!-->
	 * 
	 * </ul>
	 * 
	 * @param args arguments from command line.
	 * @return SearchPreferences object with set values from command line.
	 */
	public static SearchPreferences parseCommandLine(String[] args) {
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
            } else if (arg.equals("-D")) { // Date DD.MM.YYYY
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -D");
                    System.exit(3);
                }
                i++;
                prefs.when = Utilities.parseDate(args[i], prefs.when);
                if (prefs.when==null){
                    System.exit(3);
                }

            } else if (arg.equals("-d")) { // Timestamp
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -d");
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
            } else if (arg.equals("-T")) { // Time
                if (i == args.length - 1) {
                    System.out.println("Missing argument for -T");
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
