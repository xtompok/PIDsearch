/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.Serializable;

/** Class for representing one transfer between two stations by public transport
 *
 * @author jethro
 */
public class ConEdge extends Edge{

    /**
     * Departure time in minutes from midnight.
     */
    public int departure;
    /**
     * To which connection this transfer belongs.
     */
    public Connection connection;
    
    /**
     *
     */
    public ConEdge(){
        this.type = TransportType.BUS;
    }
    
    /**
     *
     * @param e
     */
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
