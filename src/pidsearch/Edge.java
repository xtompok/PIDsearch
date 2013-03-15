/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.Serializable;

/** Class for representing transfer between two stations.
 *
 * @author jethro
 */
public class Edge {
    /**
     * Station from which is the transfer.
     */
    public Vertex from;
    /**
     * Station to which is the transfer.
     */
    public Vertex to;
    /**
     * Length of the transfer.
     */
    public int length;
    /**
     * Type of vehicle (or walk).
     */
    public TransportType type;
    
    /**
     *
     */
    public Edge(){}
    
    /**
     *
     * @param e
     */
    public Edge(Edge.Serial e){
        length = e.length;
        type = e.type;    
    }
    
    static class Serial implements Serializable{
        public int fromIndex;
        public int toIndex;
        public int length;
        public TransportType type;
        
        public Serial(){}
        
        public Serial(Edge e){
            length = e.length;
            type = e.type;
        }
    }
    
}
