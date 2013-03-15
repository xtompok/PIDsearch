package pidsearch;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pidsearch.Vertex.Serial;


/**
 *
 * @author jethro
 */
public class PrepareData implements Externalizable {

    /**
     *
     */
    public static final long serialVersionUID = 14;
    static String dataDir = "data";
    static String stationsFile = "stations-utf8.dat";
    static String mapfile = "map.dat";
    static String decttFile = "pid.out";
    static String walksFile = "walks.dat";
    /**
     *
     */
    public Vertex[] vertices;
    /**
     *
     */
    public Connection[] connections;
    /**
     *
     */
    public ConEdge[] edges;
    /**
     *
     */
    public WalkEdge[] walks;

    /**
     *
     */
    public PrepareData() {
    }

    /**
     *
     */
    public void makeData() {

        Map map;
        List stat;
        List<Vertex> verticesList;
        List<Connection> connectionsList;
        List<ConEdge> edgesList;
        List<WalkEdge> walksList;
        map = loadMap(dataDir + "/" + mapfile);
        System.out.println("Map loaded");

        stat = loadStations(dataDir + "/" + stationsFile);
        System.out.println("Stations loaded");

        verticesList = makeVertices(stat, map);
        System.out.println("Vertices made");
        
        walksList = loadWalks(dataDir+ "/" +walksFile, verticesList);
        walksList.addAll(makeAutoWalks(verticesList));
        System.out.println("Walks made");

        List timeTable;
        timeTable = loadTimeTable(dataDir + "/" + decttFile, verticesList);
        connectionsList = (List<Connection>) timeTable.get(0);
        edgesList = (List<ConEdge>) timeTable.get(1);

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
     *
     * @param args
     */
    public static void main(String[] args) {

        PrepareData pd = new PrepareData();
        pd.makeData();
        // System.out.println(pd.verticesList.size() + " vertices, " + pd.edgesList.size() + " edges");
        /* for (int i=0;i<100;i++){
         Edge e;
         e = pd.edges.get(i);
         System.out.println(pd.connections.get(e.conection).name);
         System.out.println(pd.vertices.get(e.from).name);
         System.out.println(pd.vertices.get(e.to).name);
         System.out.println(e.length);
         System.out.println();
         }*/
        class Station{
            int id;
            Set<String> lines;
            String name;
            
            @Override
            public String toString(){
                String s = name+" ("+id+"): ";
                for(String str:lines){
                    s += str+", ";
                }
                return s;
            }
        }
        
        for (int i=0;i<pd.vertices.length;i++){
            Vertex v;
            v = pd.vertices[i];
            Station s;
            s = new Station();
            s.id = i;
            s.name = v.name;
            s.lines = new HashSet<String>();
            for (ConEdge e:v.departs){
                s.lines.add(e.connection.name);
            }
        //    System.out.println(s.toString());
        }
/*

        Vertex v = pd.vertices[3334];
        System.out.println(v.name);
        for (ConEdge e : v.departs) {
            int dep = e.departure;
            int h = dep / 60;
            int m = dep % 60;
            String to = e.to.name;
            String spoj = e.connection.name;
            System.out.println(h + "." + m + " -> " + spoj + ":" + to);
        }*/
        for (WalkEdge w : pd.walks) {
            System.out.print(w.from.name);
            System.out.print("->");
            System.out.print(w.to.name);
            System.out.println(" " + w.length);

        }

    }

    private Map<Integer, int[]> loadMap(String mapFile) {
        Map<Integer, int[]> records;
        records = new HashMap();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(mapFile));
        } catch (FileNotFoundException ex) {
            System.err.println("Map file doesn't exist");
            return null;
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] cols;
                cols = line.split(" ");
                int coord[];
                coord = new int[2];
                coord[0] = Integer.parseInt(cols[0]);
                coord[1] = Integer.parseInt(cols[1]);
                int id = Integer.parseInt(cols[2]);
                records.put(id, coord);
            }
        } catch (IOException e) {
            System.err.println("Error while reading map file");
        }

        return records;
    }

    private List<List> loadStations(String stationsFile) {
        List stations;
        stations = new LinkedList();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(stationsFile));
        } catch (FileNotFoundException ex) {
            System.err.println("Stations file doesn't exist");
            return null;
        }

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String cols[];
                cols = line.split(" ", 6);
                int mapID = Integer.parseInt(cols[0]);
                String name = cols[5];
                List s;
                s = new LinkedList();
                s.add(name);
                s.add(mapID);
                stations.add(s);
            }
        } catch (IOException e) {
            System.err.println("Error while reading stations file");
        }
        return stations;
    }

    /**
     *
     * @param stat
     * @param map
     * @return
     */
    public List<Vertex> makeVertices(List<List> stat, Map<Integer, int[]> map) {
        List<Vertex> stations;
        stations = new ArrayList();
        for (List s : (List<List>) stat) {
            Vertex v;
            v = new Vertex();
            v.name = (String) s.get(0);
            int[] coord = map.get((Integer) s.get(1));
            if (coord == null) {
                System.err.println("No map position for " + v.name);
            } else {
                v.xCoord = coord[0];
                v.yCoord = coord[1];
            }
            stations.add(v);
        }
        return stations;
    }

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
                        len = Integer.parseInt(cols[2]);
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

    /**
     *
     * @param vertices
     * @return
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
            for (int i=0;i<list.size();i++) {
                Vertex v1 = list.get(i);
                for (int j=i+1;j<list.size();j++){
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

    private int distance(Vertex v1, Vertex v2) {
        return (int) (Math.sqrt((v2.xCoord - v1.xCoord) * (v2.xCoord - v1.xCoord)
                + (v2.yCoord - v1.yCoord) * (v2.yCoord - v1.yCoord)));
    }

    /**
     *
     * @param ttFile
     * @param vertices
     * @return
     */
    public List loadTimeTable(String ttFile, List<Vertex> vertices) {
        List<ConEdge> edges;
        List<Connection> connections;
        edges = new ArrayList<ConEdge>();
        connections = new ArrayList<Connection>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(ttFile));
        } catch (FileNotFoundException ex) {
            System.err.println("Timetable file doesn't exist");
            return null;
        }

        String line;
        Connection con;
        con = null;

        int conID;
        conID = -1;
        int edgeID;
        edgeID = -1;

        int memStat;
        int memTime;
        memStat = -1;
        memTime = -1;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.charAt(0) == ';') {
                    continue;
                }
                if (line.charAt(0) == '#') {
                    String lineName;
                    lineName = line.split(" ")[1];
                    con = new Connection();
                    connections.add(con);
                    conID++;
                    con.name = lineName;
                    memStat = -1;
                    memTime = -1;
                }
                if (line.charAt(0) == 'D') {
                    con.makeValidityBitmap(line.substring(2));
                }
                if (line.charAt(0) == '\t') {
                    String[] cols;
                    cols = line.split("\t");
                    int time = (Integer.parseInt(cols[1])) % (24 * 60);
                    int stat = Integer.parseInt(cols[3]);
                    if ((memStat != -1) && (memTime != -1)) {
                        ConEdge e;
                        e = new ConEdge();
                        e.connection = con;
                        e.departure = memTime;
                        e.length = time - memTime;
                        e.from = vertices.get(memStat);
                        e.to = vertices.get(stat);
                        edges.add(e);
                        edgeID++;
                        vertices.get(memStat).departs.add(e);

                    }
                    memStat = stat;
                    memTime = time;
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading timetable file");
        }

        DepartComparator depComp;
        depComp = new DepartComparator();

        for (Vertex v : vertices) {
            Collections.sort(v.departs, depComp);
        }

        List timeTable;
        timeTable = new LinkedList();
        timeTable.add(connections);
        timeTable.add(edges);
        return timeTable;
    }

    /**
     *
     * @param oo
     * @throws IOException
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

    /**
     *
     * @param oi
     * @throws IOException
     * @throws ClassNotFoundException
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
