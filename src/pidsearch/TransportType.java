package pidsearch;

import java.io.Serializable;

/** Types of vehicles (or walk).
 * 
 * This enum lists all type of transport, what can be used in connection.  
 * 
 * @author jethro
 */
public enum TransportType implements Serializable{
    /**
     * Tramway.
     */
    TRAM,
    /**
     * Bus.
     */
    BUS,
    /**
     * Undeground.
     */
    METRO,
    /**
     * Ferry.
     */
    BOAT,
    /**
     * Walk.
     */
    WALK,
    /**
     * Train.
     */
    TRAIN;
    
}
