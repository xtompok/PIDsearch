
package pidsearch;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author jethro
 */
public class SearchConnection {

    Vertex[] vertices;
    Connection[] connections;
    ConEdge[] edges;
    WalkEdge[] walks;

    public SearchConnection(Vertex[] vertices,
            Connection[] connections,
            ConEdge[] edges,
            WalkEdge[] walks) {
        this.vertices = vertices;
        this.connections = connections;
        this.edges = edges;
        this.walks = walks;
    }

    public Set<Arrival> searchConnection(SearchPreferences prefs) {
        Vertex from = prefs.from;
        Vertex to = prefs.to;
        Calendar when = prefs.when;
        System.err.println("Searching connection from " + from.name + " to " + to.name);

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
            Arrival a;
            a = new Arrival(e);
            if (!stubs.contains(a)) {
                stubs.add(a);
            }
        }

        for (WalkEdge v : from.walks) {
            departs = findDepartures(v.to, minute, when, wait);
            for (ConEdge e : departs) {
                Arrival a;
                a = new Arrival(e);
                if (!stubs.contains(a)) {
                    stubs.add(a);
                }
            }
        }

        usedVertex.put(from, Boolean.TRUE);

        Arrival first;
        boolean found;
        found = false;

        while (!found) {
            first = stubs.poll();
            if (first == null) {
                return null;
            }

            departs = findDepartures(first.edge.to, first.arrival, when, wait);

            for (ConEdge e : departs) {

                if (e.to.name.equals(to.name)) { //FIXME Fix in future releases
                    found = true;
                }
                if (!usedVertex.get(e.to)) {
                    Arrival a;
                    a = new Arrival(first, e);
                    if (!stubs.contains(a)) {
                        stubs.add(a);
                    }
                    usedVertex.put(e.to, Boolean.TRUE);
                }

            }

            for (WalkEdge e : first.edge.to.walks) {
                if (e.to.name.equals(to.name)) {
                    found = true;
                }
                if (!usedVertex.get(e.to)) {
                    Arrival a;
                    a = new Arrival(first,e,prefs);
                    if (!stubs.contains(a)) {
                        stubs.add(a);
                    }
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

    private Set<Arrival> toFound(PriorityQueue<Arrival> stubs, Vertex to) {
        Set<Arrival> found;
        found = new HashSet<Arrival>();
        for (Arrival stub : stubs) {
            if (stub.edge.to.name.equals(to.name)) {
                    found.add(stub);
            }
        }
        return found;
    }
}
