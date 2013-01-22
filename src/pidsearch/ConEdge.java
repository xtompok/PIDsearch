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
public class ConEdge extends Edge implements Serializable{

    public int departure;
    public Connection connection;
    
    public ConEdge(){
        this.type = TransportType.BUS;
    }
    
}
