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
public class WalkEdge extends Edge{
    public WalkEdge(){
        type = TransportType.WALK;
    }
    
    public WalkEdge(WalkEdge.Serial e){
        super(e);
    }
    
    static class Serial extends Edge.Serial implements Serializable {
        public Serial(){
            type = TransportType.WALK;
        }
        public Serial(WalkEdge e){
            super(e);
        }
    }
    
}
