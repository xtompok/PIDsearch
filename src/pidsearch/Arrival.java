
package pidsearch;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;



/**
 *
 * @author jethro
 */
public class Arrival {

    public int arrival;
    public Edge edge;
    public Arrival prev;

    Arrival(ConEdge e) {
        edge = e;
        arrival = e.departure + e.length;
        prev = null;
    }

    Arrival(Arrival a, ConEdge e) {
        edge = e;
        prev = a;
        arrival = e.departure + e.length;
    }

    Arrival(Arrival a, WalkEdge e, SearchPreferences prefs) {
        edge = e;
        prev = a;
        arrival = a.arrival + (e.length/prefs.walkSpeed);
    }

    /**
     *
     * @return
     */
    public List<Edge> asList() {
        Arrival a;
        a = this;
        List<Edge> list;
        list = new LinkedList<Edge>();
        while (a != null) {
            list.add(0, a.edge);
            a = a.prev;
        }
        return list;
    }
    

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Arrival))
            return false;
        Arrival arr = (Arrival) o;
        if ((!this.edge.equals(arr.edge))||!(this.arrival==arr.arrival))
            return false;
        if (prev == arr.prev) return true;
        if ((prev==null)||(arr.prev==null)) return false;
        return prev.equals(arr.prev);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.arrival;
        hash = 13 * hash + (this.edge != null ? this.edge.hashCode() : 0);
        hash = 13 * hash + (this.prev != null ? this.prev.hashCode() : 0);
        return hash;
    }

}
    class ArrivalComparator implements Comparator<Arrival> {

        @Override
        public int compare(Arrival a1, Arrival a2) {
            return a1.arrival - a2.arrival;
        }
    }
