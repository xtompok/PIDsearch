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
public class WalkEdge extends Edge implements Serializable {
    public WalkEdge(){
        this.type = TransportType.WALK;
    }
    
}
