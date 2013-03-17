/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.Comparator;

/** Comparator for ConEdges.
 * 
 * This class provides comparator for the ConEdge class. 
 *
 * @author jethro
 */
public class DepartComparator implements Comparator<ConEdge>{

     /** Compare two ConEdges according to departure.
      * 
      * This method should be used for comaring and sorting ConEdges according to
      * the time of departure. If used as comparator for sort, it sorts ConEdges
      * chronologicaly by departure from midnight.:w
      *
      * @param e1 First edge.
      * @param e2 Second Edge
      * @return Diference between departure of e1 and e2
      */
    @Override
        public int compare(ConEdge e1, ConEdge e2) {
            return e1.departure - e2.departure;
        }
    
    }
