
package pidsearch;

import java.io.Serializable;


/** Class for representing walks between stations.
 *
 * @author jethro
 */
public class WalkEdge extends Edge{
    /**
     *
     */
    public WalkEdge(){
        type = TransportType.WALK;
    }
    
    /**
     *
     * @param e
     */
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
