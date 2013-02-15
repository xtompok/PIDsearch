/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jethro
 */
public class Vertex{
    public String name;
    public List<ConEdge> departs;
    public List<WalkEdge> walks;
    public String attrib[];
    public int xCoord;
    public int yCoord;
    
    public Vertex(){
        departs = new ArrayList<ConEdge>(); 
        walks = new LinkedList<WalkEdge>();
    }
    
    public Vertex(Vertex.Serial v){
            name = v.name;
            attrib = v.attrib;
            xCoord = v.xCoord;
            yCoord = v.yCoord;  
    }
    
     static class Serial implements Serializable{
    
        public String name;
        public int [] departsIdxs;
        public int [] walksIdxs;
        public String attrib[];
        public int xCoord;
        public int yCoord;
        
        public Serial(){}
        
        public Serial(Vertex v){
            name = v.name;
            attrib = v.attrib;
            xCoord = v.xCoord;
            yCoord = v.yCoord;
        }
    }
}
