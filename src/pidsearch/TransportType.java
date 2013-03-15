/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.Serializable;

/** Types of vehicles (or walk).
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
