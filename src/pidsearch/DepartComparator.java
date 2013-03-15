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
public class DepartComparator implements Comparator<ConEdge>{

             
        /**
     *
     * @param e1
     * @param e2
     * @return
     */
    @Override
        public int compare(ConEdge e1, ConEdge e2) {
            return e1.departure - e2.departure;
        }
    
    }
