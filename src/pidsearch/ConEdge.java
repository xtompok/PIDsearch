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
public class ConEdge extends Edge{

    public int departure;
    public Connection connection;
    
    public ConEdge(){
        this.type = TransportType.BUS;
    }
    
    public ConEdge(ConEdge.Serial e){
        super(e);
        departure = e.departure;
    }
    
    static class Serial extends Edge.Serial implements Serializable{
        public int departure;
        public int conIndex;

        public Serial(){}
        
        public Serial(ConEdge e){
            super(e);
            departure = e.departure;
        }
        
    }
    
}
