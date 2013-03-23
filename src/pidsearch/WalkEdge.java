
package pidsearch;

import java.io.Serializable;


/** Class for representing walks between stations.
 * 
 * Attribute {@link pidsearch.Edge#length length} means distance in meters between
 * the stations. This is because variable walk speed could be set.
 *
 * @author jethro
 */
public class WalkEdge extends Edge{
    /** Make a new WalkEdge with type set to walk.
     *
     */
    public WalkEdge(){
        type = TransportType.WALK;
    }
    
    WalkEdge(WalkEdge.Serial e){
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
