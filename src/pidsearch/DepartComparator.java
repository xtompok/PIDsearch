/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author jethro
 */
public class DepartComparator implements Comparator<Integer>{
        List<ConEdge> edges;

        public DepartComparator(List<ConEdge> e) {
            edges = e;
        }
        
        
        @Override
        public int compare(Integer t, Integer t1) {
            ConEdge e1,e2;
            e1 = edges.get(t);
            e2 = edges.get(t1);
            return e1.departure - e2.departure;
        }
    
    }
