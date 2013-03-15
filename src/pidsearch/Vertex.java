/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** Class for representing stations
 *
 * @author jethro
 */
public class Vertex{
    /** 
     * Name of the station.
     */
    public String name;
    /**
     * List of departures from the station, sorted from midnight.
     */
    public List<ConEdge> departs;
    /**
     * List of walks from the station.
     */
    public List<WalkEdge> walks;
    /**
     * Attributes of the station (currently unused).
     */
    public String attrib[];
    /**
     * x-axis coordinate of the station in S-42.
     */
    public int xCoord;
    /**
     * y-axis coordinate of the station in S-42.
     */
    public int yCoord;
    
    /**
     *
     */
    public Vertex(){
        departs = new ArrayList<ConEdge>(); 
        walks = new LinkedList<WalkEdge>();
    }
    
    /**
     *
     * @param v
     */
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
