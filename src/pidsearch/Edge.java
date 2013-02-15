/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.Serializable;

/**
 *
 * @author jethro
 */
public class Edge {
    public Vertex from;
    public Vertex to;
    public int length;
    public TransportType type;
    
    public Edge(){}
    
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
