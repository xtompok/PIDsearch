/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jethro
 */
public class Arrival {

    int arrival;
    Edge edge;
    Arrival prev;

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

    Arrival(Arrival a, WalkEdge e) {
        edge = e;
        prev = a;
        arrival = a.arrival + e.length;
    }

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
}

    class ArrivalComparator implements Comparator<Arrival> {

        @Override
        public int compare(Arrival a1, Arrival a2) {
            return a1.arrival - a2.arrival;
        }
    }
