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
public class Edge implements Serializable{
    public Vertex from;
    public Vertex to;
    public int length;
    public TransportType type;
    
}
