/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

/**
 *
 * @author jethro
 */
public class ConEdge extends Edge {

    public int departure;
    public Connection conection;
    
    public ConEdge(){
        this.type = TransportType.BUS;
    }
    
}
