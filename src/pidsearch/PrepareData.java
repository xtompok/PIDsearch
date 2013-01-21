/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author jethro
 */
public class PrepareData {

    static String dataDir = "data";
    static String stationsFile = "stations-utf8.dat";
    static String mapfile = "map.dat";
    static String decttFile = "pid.out";
    
    Map map;
    List stat;
    List<Vertex> vertices;
    List<Connection> connections;
    List<ConEdge> edges;
    List<WalkEdge> walks;

    
    public PrepareData(){
        
        map = loadMap(dataDir + "/" + mapfile);
        System.out.println("Map loaded");
        
        stat = loadStations(dataDir + "/" + stationsFile);
        System.out.println("Stations loaded");
        
        vertices = makeVertices(stat, map);
        System.out.println("Vertices made");
        
        walks = makeAutoWalks(vertices);
        System.out.println("Walks made");
        
        List timeTable;
        timeTable = loadTimeTable(dataDir+"/"+decttFile,vertices);
        connections = (List<Connection>)timeTable.get(0);
        edges = (List<ConEdge>) timeTable.get(1);
        System.out.print("Timetable loaded, ");
        System.out.print(connections.size()+" connections, ");
        System.out.println(edges.size()+" edges");
    
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        PrepareData pd = new PrepareData();
        System.out.println(pd.vertices.size()+" vertices, "+pd.edges.size()+" edges");
       /* for (int i=0;i<100;i++){
            Edge e;
            e = pd.edges.get(i);
            System.out.println(pd.connections.get(e.conection).name);
            System.out.println(pd.vertices.get(e.from).name);
            System.out.println(pd.vertices.get(e.to).name);
            System.out.println(e.length);
            System.out.println();
        }*/

        
        Vertex v = pd.vertices.get(3334);
        System.out.println(v.name);
        for (ConEdge e:v.departs){
            int dep = e.departure;
            int h = dep/60;
            int m = dep%60;
            String to = e.to.name;
            String spoj = e.conection.name;
            System.out.println(h+"."+m+" -> "+spoj+":"+to);
        }
        
        for (WalkEdge w: pd.walks){
            System.out.print(w.from.name);
            System.out.print("->");
            System.out.print(w.to.name);
            System.out.println(" "+w.length);

        }
        
    }

    public  Map<Integer, int[]> loadMap(String mapFile) {
        Map<Integer,int[]> records;
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
    
    public  List<List> loadStations(String stationsFile){
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
    
    public  List makeVertices(List<List> stat,Map<Integer,int[]> map){
        List<Vertex> stations;
        stations = new ArrayList();
        for (List s:(List<List>)stat){
            Vertex v;
            v = new Vertex();
            v.name = (String)s.get(0);
            int[] coord = map.get((Integer)s.get(1));
            if (coord==null){
                System.err.println("No map position for "+v.name);
            } else {
                v.xCoord = coord[0];
                v.yCoord = coord[1];
            }
            stations.add(v);
        }
        return stations;
    }
    
    public List<WalkEdge> makeAutoWalks(List<Vertex> vertices){
        
        List<WalkEdge> walks;
        walks = new ArrayList<WalkEdge>();
        
        Map<String,List<Vertex>> sameName;
        sameName = new HashMap<String, List<Vertex>>();
        
        for (Vertex v:vertices){
            if (sameName.containsKey(v.name)){
                sameName.get(v.name).add(v);
            } else
            {
                List<Vertex> l;
                l = new LinkedList<Vertex>();
                l.add(v);
                sameName.put(v.name,l );
            }
        }
        
        for (List<Vertex> list: sameName.values()){
            for (Vertex v1: list){
                for (Vertex v2: list){
                    if (v1==v2) continue;
                    int dist = distance(v1, v2);
                    WalkEdge e;
                    e = new WalkEdge();
                    e.from = v1;
                    e.to = v2;
                    e.length = dist/60;
                    walks.add(e);
                    v1.walks.add(e);
                    v2.walks.add(e);
                }
            }
        }
        
        return walks;
    }
    
    public int distance(Vertex v1, Vertex v2){
        return (int)(Math.sqrt((v2.xCoord-v1.xCoord)*(v2.xCoord-v1.xCoord)+
                (v2.yCoord-v1.yCoord)*(v2.yCoord-v1.yCoord)));
    }
    
    public List loadTimeTable(String ttFile,List<Vertex> vertices)
    {
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
        conID=-1;
        int edgeID;
        edgeID=-1;
        
        int memStat;
        int memTime;
        memStat=-1;
        memTime=-1;
        
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0)
                    continue;
                if (line.charAt(0) == ';')
                    continue;
                if (line.charAt(0) == '#')
                {
                    String lineName;
                    lineName = line.split(" ")[1];
                    con = new Connection();
                    connections.add(con);
                    conID++;
                    con.name = lineName;
                    memStat=-1;
                    memTime=-1;
                }
                if (line.charAt(0) == 'D')
                {
                    con.makeValidityBitmap(line.substring(2));
                }
                if (line.charAt(0) == '\t')
                {
                    String [] cols;
                    cols = line.split("\t");
                    int time = Integer.parseInt(cols[1]);
                    int stat = Integer.parseInt(cols[3]);
                    if ((memStat!=-1)&&(memTime!=-1)){
                        ConEdge e;
                        e = new ConEdge();
                        e.conection = con;
                        e.departure = memTime;
                        e.length = time-memTime;
                        e.from=vertices.get(memStat);
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
        
        for (Vertex v: vertices){
            Collections.sort(v.departs,depComp);
        }
        
        List timeTable;
        timeTable = new LinkedList();
        timeTable.add(connections);
        timeTable.add(edges);
        return timeTable;
    }
    
}
