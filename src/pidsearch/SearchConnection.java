/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author jethro
 */
public class SearchConnection {
    Vertex [] vertices;
    Connection [] connections;
    ConEdge [] edges;
    WalkEdge [] walks;

    public SearchConnection(Vertex [] vertices,
            Connection [] connections,
            ConEdge [] edges,
            WalkEdge [] walks) {
        this.vertices = vertices;
        this.connections = connections;
        this.edges = edges;
        this.walks = walks; 
    }
    
    
    public List<Arrival> searchConnection(Vertex from, Vertex to, Calendar when) {
        System.out.println("Searching connection from " + from.name + " to " + to.name);

        HashMap<Vertex, Boolean> usedVertex;

        usedVertex = new HashMap<Vertex, Boolean>();
        for (Vertex v : vertices) {
            usedVertex.put(v, Boolean.FALSE);
        }
        
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
