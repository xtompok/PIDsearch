package pidsearch;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pidsearch.Vertex.Serial;

/** Class for preparing, saving and loading graph objects.
 *
 * This class is used for parsing original files and creating a persistent object,
 * where all information about vertices, edges and connections are saved. If called
 * independently, generates list of stations and walk edges.
 * 
 * @author jethro
 */
public class PrepareData implements Externalizable {

	/** 
	 * Version if data for serialization.
	 */
	public static final long serialVersionUID = 22;
	/**
	 * Directory containing files for generation the graph.
	 */
	public static String dataDir = "/home/jethro/Programy/idos-decrypt/GTFS/output";
	/**
	 * Filename of file containing stations definitions.
	 */
	public static String stationsFile = "stops.txt";
	/**
	 * Filename of file containing map positions of stations.
	 */
	public static String conFile = "routes.txt";
        
        public static String validityFile = "calendar_dates.txt";
        
        public static String ttFile = "stop_times.txt";
        
        public static String tripsFile = "trips.txt";
	/**
	 * Filename of file containing decoded timetables.
	 */
	public static String decttFile = "pid.out";
	/**
	 * Filename of file containing walk definitions.
	 */
	public static String walksFile = "walks.dat";
	/**
	 * Array of vertices of the graph.
	 */
	public Vertex[] vertices;
	/**
	 * Array of connections from timetable.
	 */
	public Connection[] connections;
	/**
	 * Array of connection edges.
	 */
	public ConEdge[] edges;
	/**
	 * Array of walks.
	 */
	public WalkEdge[] walks;
        
        
        //HACK
        private Map<String,Vertex> idToStation;

	/**
	 * Create an empty PrepareData object.
	 *
	 * @see pidsearch.PrepareData#makeData
	 */
	public PrepareData() {
	}

	/**
	 * Fill object with data.
	 *
	 * This method parses the data files and generates new graph of
	 * connections. This could consume large amount of memory and time,
	 * consider saving this object after generating.
	 */
	public void makeData() {

		List<Vertex> verticesList;
		List<Connection> connectionsList;
		List<ConEdge> edgesList;
		List<WalkEdge> walksList;
                Map<String,boolean []> validity;
                verticesList = loadGTFSStations(dataDir+"/"+stationsFile);
                System.out.println("Stations loaded");
                validity = loadGTFSValidity(dataDir+"/"+validityFile,dataDir+"/"+tripsFile);
                System.out.println("Validity loaded");
                connectionsList = loadGTFSConnections(dataDir+"/"+conFile,validity);
                System.out.println("Connections loaded");
                edgesList = loadGTFSTimeTable(dataDir+"/"+ttFile, verticesList, connectionsList);
                System.out.println("Timetable loaded");
                walksList = new LinkedList<WalkEdge>();
                DepartComparator dc;
                dc = new DepartComparator();
                for (int i=0;i<verticesList.size();i++)
                {
                    Vertex v;
                    v = verticesList.get(i);
                    Collections.sort(v.departs,dc);
                    verticesList.set(i, v);
                }
                /*map = loadMap(dataDir + "/" + mapfile);
		System.out.println("Map loaded");

		stat = loadStations(dataDir + "/" + stationsFile);
		System.out.println("Stations loaded");

		verticesList = makeVertices(stat, map);
		System.out.println("Vertices made");

		walksList = loadWalks(dataDir + "/" + walksFile, verticesList);
		walksList.addAll(makeAutoWalks(verticesList));
		System.out.println("Walks made");

		List timeTable;
		timeTable = loadTimeTable(dataDir + "/" + decttFile, verticesList);
		connectionsList = (List<Connection>) timeTable.get(0);
		edgesList = (List<ConEdge>) timeTable.get(1);
*/
		System.out.print("Timetable loaded, ");
		System.out.print(connectionsList.size() + " connections, ");
		System.out.println(edgesList.size() + " edges");

		vertices = new Vertex[verticesList.size()];
		vertices = verticesList.toArray(vertices);
		connections = new Connection[connectionsList.size()];
		connections = connectionsList.toArray(connections);
		edges = new ConEdge[edgesList.size()];
		edges = edgesList.toArray(edges);
		walks = new WalkEdge[walksList.size()];
		walks = walksList.toArray(walks);
                 
	}

	/**
	 * Main method, print stations and walks.
	 * 
	 * If this class is called as a main class, list of station is printed 
	 * with lines going from each station. Also is printed a list of walk 
	 * edges.
	 * Format: {@code name (id): l1, l2, } for stations, 
	 * {@code from -> to length} for walk edges.
	 *
	 * @param args Arguments from command line, ignored.
	 */
	public static void main(String[] args) {

		PrepareData pd = new PrepareData();
		pd.makeData();
		class Station {

			int id;
			Set<String> lines;
			String name;

			@Override
			public String toString() {
				String s = name + " (" + id + "): ";
				for (String str : lines) {
					s += str + ", ";
				}
				return s;
			}
		}

		for (int i = 0; i < pd.vertices.length; i++) {
			Vertex v;
			v = pd.vertices[i];
			Station s;
			s = new Station();
			s.id = i;
			s.name = v.name;
			s.lines = new LinkedHashSet<String>();
			for (ConEdge e : v.departs) {
				s.lines.add(e.departure+" "+e.connection.name);
			}
			System.out.println(s.toString());
		}
		for (WalkEdge w : pd.walks) {
			System.out.print(w.from.name);
			System.out.print("->");
			System.out.print(w.to.name);
			System.out.println(" " + w.length);

		}
                
                Map<Connection,List<ConEdge>> consMap;
                consMap = new HashMap<Connection, List<ConEdge>>();
                for (Connection c:pd.connections){
                    consMap.put(c, new LinkedList<ConEdge>());
                }
                for (ConEdge e: pd.edges){
                    consMap.get(e.connection).add(e);
                }
                DepartComparator dc;
                dc = new DepartComparator();
                for (Connection c: consMap.keySet()){
                    List<ConEdge> edges;
                    edges = consMap.get(c);
                    Collections.sort(edges,dc);
                    ConEdge first = edges.get(0);
                    ConEdge last = edges.get(edges.size()-1);
                    System.out.println(c.name+" "+
                            first.from.name+"("+first.departure+") -> "+
                            last.to.name+"("+(last.departure+last.length)+")");
                }

	}

	/** Load the map from file.
	 * 
	 * This method loads positions of stations from given file. Every line
	 * describes one station.
	 * Format: {@code x y mapId}, where x and y are x and y coordinate of station
	 * with id mapId in S-42.
	 * 
	 * @param mapFile Path to map file.
	 * @return Map, where key is an mapId of station and value is array of [x,y]
	 * coordinates of station in S-42.
	 */
        
        private int YmdToDays(String str){
            String year = str.substring(0,4);
            String month = str.substring(4,6);
            String day = str.substring(6,8);
            Calendar cal;
            cal = Calendar.getInstance();
            int curYear = cal.get(Calendar.YEAR);
            if (curYear!=Integer.parseInt(year))
                return 374; // HACK
            cal.set(Calendar.YEAR, Integer.parseInt(year));
            cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            return cal.get(Calendar.DAY_OF_YEAR);
        }
        
        private Map<String, boolean[]> loadGTFSValidity(String validityFile, String tripsFile){
            CSVReader reader;
            try{
                reader = new CSVReader(new BufferedReader(new FileReader(validityFile)));
            } catch (FileNotFoundException ex){
                System.err.println("GTFS validity file doesn't exist");
                return null;
            }
           
            Map<Integer, boolean []> calValidity;
            calValidity = new HashMap<Integer, boolean[]>();
            try {
                String [] colhead = reader.readNext();
                Map<String,Integer> colMap  = new HashMap<String, Integer>();
                for (int i=0;i<colhead.length;i++){
                    colMap.put(colhead[i], i);
                }
                String [] nextLine;
                while ((nextLine = reader.readNext())!=null){
                    int id = Integer.parseInt(nextLine[colMap.get("service_id")]);
                    int bit = YmdToDays(nextLine[colMap.get("date")]);
                    boolean state = (nextLine[colMap.get("exception_type")].equals("1"));
                    
                    boolean [] val;
                    if (calValidity.containsKey(id)){
                        val = calValidity.get(id);
                    }else{
                        val = new boolean[375];
                    }
                    val[bit]=state;
                    calValidity.put(id, val);
                }
            } catch (IOException ex) {
                System.err.println("Error occured while reading validity file");
                return null;
            }
            
            
            try{
                reader = new CSVReader(new BufferedReader(new FileReader(tripsFile)));
            } catch (FileNotFoundException ex){
                System.err.println("GTFS trips file doesn't exist");
                return null;
            }
           
            Map<String, boolean []> validity;
            validity = new HashMap<String, boolean[]>();
            try {
                String [] colhead = reader.readNext();
                Map<String,Integer> colMap  = new HashMap<String, Integer>();
                for (int i=0;i<colhead.length;i++){
                    colMap.put(colhead[i], i);
                }
                String [] nextLine;
                while ((nextLine = reader.readNext())!=null){
                    validity.put(nextLine[colMap.get("route_id")],
                            calValidity.get(Integer.parseInt(nextLine[colMap.get("service_id")])));
       
                }
            } catch (IOException ex) {
                System.err.println("Error occured while reading trips file");
                return null;
            }
            
            return validity;  
            
        }
        
        private List<Vertex> loadGTFSStations(String statFile){
            CSVReader reader;
            try{
                reader = new CSVReader(new BufferedReader(new FileReader(statFile)));
            } catch (FileNotFoundException ex){
                System.err.println("GTFS stations file doesn't exist");
                return null;
            }
            
            idToStation = new HashMap<String, Vertex>();
            List<Vertex> stations;
            stations = new LinkedList<Vertex>();
            try {
                String [] colhead = reader.readNext();
                Map<String,Integer> colMap  = new HashMap<String, Integer>();
                for (int i=0;i<colhead.length;i++){
                    colMap.put(colhead[i], i);
                }
                String [] nextLine;
                while ((nextLine = reader.readNext())!=null){
                    Vertex v = new Vertex();
                    v.departs = new LinkedList<ConEdge>();
                    v.walks = new LinkedList<WalkEdge>();
                    v.name = nextLine[colMap.get("stop_name")];
                 //   v.xCoord = Integer.parseInt(nextLine[colMap.get("stop_lat")]);
                 //   v.yCoord = Integer.parseInt(nextLine[colMap.get("stop_len")]);
                    idToStation.put(nextLine[colMap.get("stop_id")], v);
                    stations.add(v);
                }
            } catch (IOException ex) {
                System.err.println("Error occured while reading stations file");
                return null;
            }
            return stations;
        }
       
	/** Load walks from file.
	 * 
	 * This method loads user-defined wlaks from given file. 
	 * Format:{@code from to [len]}, where from and to are ids of stations
	 * between whose is the walk edge and len is length in minutes while going
	 * 1ms^-1. If the length is not set, it is calculated as an eucleindan 
	 * distance between stations.
	 * 
	 * @param walksFile Path to the file containing walks.
	 * @param vertices List of all vertices.
	 * @return List of Walk edges of loaded walks.
	 */
	private List<WalkEdge> loadWalks(String walksFile, List<Vertex> vertices) {
		List<WalkEdge> walks;
		walks = new LinkedList<WalkEdge>();

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(walksFile));
		} catch (FileNotFoundException ex) {
			System.err.println("Walks file doesn't exist");
			return walks;
		}

		String line;
		int lineNum = 0;
		try {
			while ((line = reader.readLine()) != null) {
				String cols[];
				cols = line.split(" ");
				try {
					int fromIndex = Integer.parseInt(cols[0]);
					int toIndex = Integer.parseInt(cols[1]);
					int len;
					if (cols.length == 3) {
						len = Integer.parseInt(cols[2])*60;
					} else {
						len = distance(vertices.get(fromIndex), vertices.get(toIndex));
					}

					Vertex from;
					Vertex to;
					from = vertices.get(fromIndex);
					to = vertices.get(toIndex);

					WalkEdge e1;
					e1 = new WalkEdge();
					e1.from = from;
					e1.to = to;
					e1.length = len;
					from.walks.add(e1);
					walks.add(e1);

					WalkEdge e2;
					e2 = new WalkEdge();
					e2.to = from;
					e2.from = to;
					e2.length = len;
					to.walks.add(e2);
					walks.add(e2);




				} catch (NumberFormatException ex) {
					System.err.println("Syntax error in walks file on line " + lineNum);
				}

				lineNum++;

			}
		} catch (IOException e) {
			System.err.println("Error while reading stations file");
		}
		return walks;
	}

	/** Make automatic walks.
	 * 
	 * This method takes the vertices and calculates walk edges between
	 * stations with same name. The distance is caluculated using 
	 * {@link pidsearch.PrepareData#distance distance} method. 
	 *
	 * @param vertices List of all vertices.
	 * @return List of Walk edges with generated walks.
	 */
	public List<WalkEdge> makeAutoWalks(List<Vertex> vertices) {

		List<WalkEdge> walks;
		walks = new ArrayList<WalkEdge>();

		Map<String, List<Vertex>> sameName;
		sameName = new HashMap<String, List<Vertex>>();

		for (Vertex v : vertices) {
			if (sameName.containsKey(v.name)) {
				sameName.get(v.name).add(v);
			} else {
				List<Vertex> l;
				l = new LinkedList<Vertex>();
				l.add(v);
				sameName.put(v.name, l);
			}
		}

		for (List<Vertex> list : sameName.values()) {
			for (int i = 0; i < list.size(); i++) {
				Vertex v1 = list.get(i);
				for (int j = i + 1; j < list.size(); j++) {
					Vertex v2 = list.get(j);
					int dist = distance(v1, v2);
					WalkEdge e1;
					e1 = new WalkEdge();
					e1.from = v1;
					e1.to = v2;
					e1.length = dist;
					walks.add(e1);
					v1.walks.add(e1);

					WalkEdge e2;
					e2 = new WalkEdge();
					e2.from = v2;
					e2.to = v1;
					e2.length = dist;
					walks.add(e2);
					v2.walks.add(e2);
				}
			}
		}

		return walks;
	}
	
	/** Calculate distance between two vertices.
	 * 
	 * Because S-42 is the carthesian coordinate system and the coordinates 
	 * are in meters, distance could be computed from Pythagorean theorem.
	 * 
	 * @param v1 First Vertex
	 * @param v2 Second Vertex.
	 * @return Distance between vertices in meters.
	 */
	private int distance(Vertex v1, Vertex v2) {
		return (int) (Math.sqrt((v2.xCoord - v1.xCoord) * (v2.xCoord - v1.xCoord)
			+ (v2.yCoord - v1.yCoord) * (v2.yCoord - v1.yCoord)));
	}
        
        
        public List<Connection> loadGTFSConnections(String conFile, Map<String,boolean []> validity){
            CSVReader reader;
            try{
                reader = new CSVReader(new BufferedReader(new FileReader(conFile)));
            } catch (FileNotFoundException ex){
                System.err.println("GTFS connections file doesn't exist");
                return null;
            }
           
            List<Connection> connections;
            connections = new LinkedList<Connection>();
            try {
                String [] colhead = reader.readNext();
                Map<String,Integer> colMap  = new HashMap<String, Integer>();
                for (int i=0;i<colhead.length;i++){
                    colMap.put(colhead[i], i);
                }
                String [] nextLine;
                while ((nextLine = reader.readNext())!=null){
                    Connection c = new Connection();
                    c.company = nextLine[colMap.get("agency_id")];
                    c.name = nextLine[colMap.get("route_short_name")]+" "+
                            nextLine[colMap.get("route_long_name")];
                    c.type = TransportType.TRAIN;
                    c.validity = validity.get(nextLine[colMap.get("route_id")]);
                    connections.add(c);
                }
            } catch (IOException ex) {
                System.err.println("Error occured while reading stations file");
                return null;
            }
            
            return connections;           
        }
        
        private int timeToInt(String time){
            String [] timeParts = time.split(":");
            return Integer.parseInt(timeParts[0])*60+Integer.parseInt(timeParts[1]);
        }
        
        public List<ConEdge> loadGTFSTimeTable(String ttFile,List<Vertex> vertices, List<Connection> connections){
        CSVReader reader;
            try{
                reader = new CSVReader(new BufferedReader(new FileReader(ttFile)));
            } catch (FileNotFoundException ex){
                System.err.println("GTFS timetable file doesn't exist");
                return null;
            }
           
            List<ConEdge> edges;
            edges = new LinkedList<ConEdge>();
            try {
                String [] colhead = reader.readNext();
                Map<String,Integer> colMap  = new HashMap<String, Integer>();
                for (int i=0;i<colhead.length;i++){
                    colMap.put(colhead[i], i);
                }
                String [] line;
                line = reader.readNext();
                String [] nextLine;
                nextLine = reader.readNext();
                while (nextLine!=null){
                    if (!nextLine[colMap.get("trip_id")].equals(line[colMap.get("trip_id")]))
                    {
                        line = nextLine;
                        nextLine = reader.readNext();
                        continue;
                    }
                    ConEdge e = new ConEdge();
                    e.connection = connections.get(Integer.parseInt(
                            nextLine[colMap.get("trip_id")]));
                    e.departure = timeToInt(line[colMap.get("departure_time")]);
                    int arrival = timeToInt(nextLine[colMap.get("arrival_time")]);
                    e.length = arrival-e.departure;
                    if (e.length<=0)
                        System.out.println("Timetravel detected!"+e.connection.name);
                    e.from = idToStation.get(line[colMap.get("stop_id")]);
                    e.to = idToStation.get(nextLine[colMap.get("stop_id")]);
                    e.type = TransportType.TRAIN;
                    
                    int index = vertices.indexOf(e.from);
                    vertices.get(index).departs.add(e);
                    edges.add(e);
               
                    line = nextLine;
                    nextLine = reader.readNext();
                }
            } catch (IOException ex) {
                System.err.println("Error occured while reading stations file");
                return null;
            }
            
            return edges;  
        }

	/** Custom serializer.
	 * 
	 * Because of saving data space and for faster loading, custom serialization
	 * is used. When serializing vertices and edges, specialized Serial subclasses
	 * are used, where referrences are replaced by indexes into arrays. This makes
	 * saving and loading faster and stack size does not have to be increased.
	 * In the future, ConEdges will be placed into the Connection objects for 
	 * faster loading and saving space.
	 *
	 * @param oo ObjectOutput to write to.
	 * @throws IOException if error occured.
	 */
	@Override
	public void writeExternal(ObjectOutput oo) throws IOException {
		/* public List<Vertex> vertices;
		 public List<Connection> connections; # neni potreba upravovat
		 public List<ConEdge> edges;
		 public List<WalkEdge> walks;*/
		Vertex.Serial vertexArray[];
		Connection conArray[];
		ConEdge.Serial conEdgeArray[];
		WalkEdge.Serial walkEdgeArray[];


		vertexArray = new Vertex.Serial[vertices.length];
		conArray = new Connection[connections.length];
		conEdgeArray = new ConEdge.Serial[edges.length];
		walkEdgeArray = new WalkEdge.Serial[walks.length];

		conArray = connections;

		Map<ConEdge, Integer> edgeIndexMap;
		edgeIndexMap = new HashMap<ConEdge, Integer>(connections.length);
		for (int i = 0; i < edges.length; i++) {
			edgeIndexMap.put(edges[i], i);
		}

		Map<Vertex, Integer> verticesIndexMap;
		verticesIndexMap = new HashMap<Vertex, Integer>(vertices.length);
		for (int i = 0; i < vertices.length; i++) {
			verticesIndexMap.put(vertices[i], i);
		}

		Map<Connection, Integer> conIndexMap;
		conIndexMap = new HashMap<Connection, Integer>(connections.length);
		for (int i = 0; i < connections.length; i++) {
			conIndexMap.put(connections[i], i);
		}

		Map<WalkEdge, Integer> walksIndexMap;
		walksIndexMap = new HashMap<WalkEdge, Integer>(walks.length);
		for (int i = 0; i < walks.length; i++) {
			walksIndexMap.put(walks[i], i);
		}

		System.out.println("Preparing vertices");
		for (int i = 0; i < vertices.length; i++) {
			Vertex v;
			v = vertices[i];
			Vertex.Serial ser;
			ser = new Vertex.Serial(v);
			ser.departsIdxs = new int[v.departs.size()];
			ser.walksIdxs = new int[v.walks.size()];
			for (int j = 0; j < v.departs.size(); j++) {
				ser.departsIdxs[j] = edgeIndexMap.get(v.departs.get(j));
			}
			for (int j = 0; j < v.walks.size(); j++) {
				ser.walksIdxs[j] = walksIndexMap.get(v.walks.get(j));
			}
			vertexArray[i] = ser;
		}

		System.out.println("Preparing walks");

		for (int i = 0; i < walks.length; i++) {
			WalkEdge e;
			e = walks[i];
			WalkEdge.Serial ser;
			ser = new WalkEdge.Serial(e);
			ser.fromIndex = verticesIndexMap.get(e.from);
			ser.toIndex = verticesIndexMap.get(e.to);
			walkEdgeArray[i] = ser;
		}

		System.out.println("Preparing connection edges");

		for (int i = 0; i < edges.length; i++) {
			ConEdge e;
			e = edges[i];
			ConEdge.Serial ser;
			ser = new ConEdge.Serial(e);
			ser.conIndex = conIndexMap.get(e.connection);
			ser.fromIndex = verticesIndexMap.get(e.from);
			ser.toIndex = verticesIndexMap.get(e.to);
			conEdgeArray[i] = ser;
		}

		System.out.println("Writing started");

		oo.writeObject(vertexArray);
		oo.writeObject(conArray);
		oo.writeObject(conEdgeArray);
		oo.writeObject(walkEdgeArray);
	}

	/** Custom deserialization.
	 * 
	 * Loads objects from disk and reconstruct original structures. 
	 * 
	 * @see pidsearch.PrepareData#writeExternal writeExternal
	 * @param oi Input to read objects from.
	 * @throws IOException if error while reading occured.
	 * @throws ClassNotFoundException if deserialized class does not exist.
	 */
	@Override
	public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
		/* public List<Vertex> vertices;
   
		 public List<Connection> connections; # neni potreba upravovat
		 public List<ConEdge> edges;
		 public List<WalkEdge> walks;*/

		Vertex.Serial vertexArray[];
		Connection conArray[];
		ConEdge.Serial conEdgeArray[];
		WalkEdge.Serial walkEdgeArray[];

		System.out.println("Reading vertices");
		vertexArray = (Serial[]) oi.readObject();
		System.out.println("Reading connections");
		conArray = (Connection[]) oi.readObject();
		System.out.println("Reading transport edges");
		conEdgeArray = (ConEdge.Serial[]) oi.readObject();
		System.out.println("Reading walks");
		walkEdgeArray = (WalkEdge.Serial[]) oi.readObject();

		vertices = new Vertex[vertexArray.length];
		connections = conArray;
		edges = new ConEdge[conEdgeArray.length];
		walks = new WalkEdge[walkEdgeArray.length];

		for (int i = 0; i < vertexArray.length; i++) {
			vertices[i] = new Vertex(vertexArray[i]);
		}

		for (int i = 0; i < conEdgeArray.length; i++) {
			edges[i] = new ConEdge(conEdgeArray[i]);
		}
		for (int i = 0; i < walkEdgeArray.length; i++) {
			walks[i] = new WalkEdge(walkEdgeArray[i]);
		}

		for (int i = 0; i < vertexArray.length; i++) {
			List departList = new ArrayList<ConEdge>();
			List walkList = new ArrayList<WalkEdge>();

			for (int j = 0; j < vertexArray[i].departsIdxs.length; j++) {
				departList.add(edges[vertexArray[i].departsIdxs[j]]);
			}

			for (int j = 0; j < vertexArray[i].walksIdxs.length; j++) {
				walkList.add(walks[vertexArray[i].walksIdxs[j]]);
			}


			vertices[i].departs = departList;
			vertices[i].walks = walkList;
		}

		for (int i = 0; i < conEdgeArray.length; i++) {
			edges[i].connection = connections[conEdgeArray[i].conIndex];
			edges[i].from = vertices[conEdgeArray[i].fromIndex];
			edges[i].to = vertices[conEdgeArray[i].toIndex];
		}

		for (int i = 0; i < walkEdgeArray.length; i++) {
			walks[i].from = vertices[walkEdgeArray[i].fromIndex];
			walks[i].to = vertices[walkEdgeArray[i].toIndex];
		}

	}
}
