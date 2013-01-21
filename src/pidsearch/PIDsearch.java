/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Compare;

/**
 *
 * @author jethro
 */
public class PIDsearch {

    Map<String,Vertex> vertexForName;
    List<Vertex> vertices;
    List<Connection> connections;
    List<ConEdge> edges;
    List<WalkEdge> walks;

    
    public PIDsearch() {

        
        PrepareData pd;
        pd = new PrepareData();
        vertices = pd.vertices;
        connections = pd.connections;
        edges = pd.edges;
        walks = pd.walks;
        
        vertexForName = new HashMap<String, Vertex>();
        for (Vertex v: vertices){
            vertexForName.put(v.name,v);
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
    
    public Vertex getStation(String type){
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(System.in));
        
        String st;
        st = null;
        do {
            System.out.print("Zadejte jmeno "+type+" stanice:");
            try {
                st = in.readLine();
            } catch (IOException ex) {
                System.err.println("Failed to read a station name");
                System.exit(1);
            }
        } while (! vertexForName.containsKey(st));
        return vertexForName.get(st);
    } 
    
    public void CLI(){
        Vertex from;
        Vertex to;
        from = getStation("vychozi");
        to = getStation("cilove");
        Calendar cal;
        cal = Calendar.getInstance();
        //Date d = cal.getTime();
        List<Arrival> cons;
        cons = searchConnection(from, to, cal);
        printConnections(cons);
    }
    
    public void printConnections(List<Arrival> cons){
        for (Arrival a: cons){
            System.out.print("Spojeni z "+a.path.get(0).from.name);
            System.out.println(" do "+a.path.get(a.path.size()-1).to.name);
            for (Edge e: a.path){
                if (e instanceof ConEdge){
                    printConEdge((ConEdge)e);
               }
            }
            System.out.println();
        }
    }
    
    public void printConEdge(ConEdge e){
        System.out.print(e.from.name+"("+strTime(e.departure)+") -> ");
        System.out.println(e.to.name+"("+strTime(e.departure+e.length)+")");
    }
    
    public void printWalkEdge(WalkEdge e){
        System.out.println(e.from.name+" -> "+e.to.name+"("+e.length+")");
    }
    
    public String strTime(int time){
        return (time/60)+"."+(time%60);
    }
    
    class Arrival{
        int arrival;
        List<Edge> path;
        
        Arrival(ConEdge e){
            path = new LinkedList<Edge>();
            path.add(e);
            arrival = e.departure+e.length;
        }  
        Arrival(Arrival a,ConEdge e){
            path = new LinkedList<Edge>(a.path);
            path.add(e);
            arrival = e.departure+e.length;
        }
        Arrival(Arrival a,WalkEdge e){
            path = new LinkedList<Edge>(a.path);
            path.add(e);
            arrival = a.arrival+e.length;
        }
        
    }
            
        class ArrivalComparator implements Comparator<Arrival>{

            @Override
            public int compare(Arrival a1, Arrival a2) {
                return a1.arrival - a2.arrival;
            }
        
        }
    
    public List<Arrival> searchConnection(Vertex from,Vertex to,Calendar when){
        System.out.println("Searching connection from "+from.name+" to "+to.name);
        List<Arrival> stubs;
        stubs = new ArrayList<Arrival>();
        int minute = when.get(Calendar.HOUR_OF_DAY)*60+when.get(Calendar.MINUTE);

        
        int wait = 10;
        
        List<ConEdge> departs;
        departs = findDepartures(from,minute, when, wait);
        
        for (ConEdge e: departs){
            stubs.add(new Arrival(e));
        }
        
        for (WalkEdge v: from.walks){
            departs = findDepartures(v.to, minute,when, wait);
            for (ConEdge e: departs){
                stubs.add(new Arrival(e));
            }
        }
       
        ArrivalComparator ac;
        ac = new ArrivalComparator();
        Arrival first;
        Edge lastEdge;
        
        while (toFound(stubs, to).isEmpty()){
         //   System.out.println("# of stubs:"+stubs.size());
            Collections.sort(stubs,ac);
            first = stubs.remove(0);
            lastEdge = first.path.get(first.path.size()-1);
            /*System.out.println("Arrival:"+first.arrival);
            if (lastEdge instanceof ConEdge)
                printConEdge((ConEdge)lastEdge);
            else 
                printWalkEdge((WalkEdge)lastEdge);*/
                
            departs = findDepartures(lastEdge.to,first.arrival,when, wait);

            for (ConEdge e : departs) {
                boolean cycle=false;
                for (Edge pe :first.path){
                    if (e.to.equals(pe.from)){
                        cycle=true;
                        break;
                    }          
                }
                if (!cycle)
                    stubs.add(new Arrival(first,e));

            }

            for (WalkEdge v : lastEdge.to.walks) {
                    if ((!v.to.name.equals(lastEdge.from.name)) &&
                            (!v.to.equals(lastEdge.from)))
                        stubs.add(new Arrival(first,v));
                     
            }

        }
        
        
        return toFound(stubs, to);
    }
    
    public List<ConEdge> findDepartures(Vertex from,int minute, Calendar date, int range){
        List<ConEdge> departs;
        departs = from.departs;

        
        ConEdge whenEdge;
        whenEdge = new ConEdge();
        
        DepartComparator dc;
        dc = new DepartComparator();
        
        int minIndex;
        whenEdge.departure = minute;
        minIndex = Collections.binarySearch(departs,whenEdge,dc);
        minIndex = (minIndex<0)?-(minIndex+1):minIndex;
        
        int maxIndex;
        whenEdge.departure = minute+range;
        maxIndex = Collections.binarySearch(departs, whenEdge, dc);
        maxIndex = (maxIndex<0)?-(maxIndex+1):maxIndex;
        
        List<ConEdge> departEdges;
        departEdges = new LinkedList<ConEdge>();
        if (minIndex>maxIndex) return departEdges;
        for (ConEdge e:departs.subList(minIndex, maxIndex)){
            if (e.conection.goesAt(date))
                departEdges.add(e);
        }
        return departEdges;
    }
    
    public List<Arrival> toFound(List<Arrival> stubs,Vertex to){
        List<Arrival> found;
        found = new LinkedList<Arrival>();
        for (Arrival stub: stubs){
            if (stub.path.get(stub.path.size()-1).to==to){
                found.add(stub);
            }
        }
        return found;
    }
    
    public void stepWalks(List<List<Edge>> stubs){
        for (List<Edge> e:stubs){
            if (e.get(e.size()-1).type==TransportType.WALK)
            {
                
            }
        }
    
    }
    
}
