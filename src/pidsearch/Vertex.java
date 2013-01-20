/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jethro
 */
public class Vertex {
    public String name;
    public List<Integer> departs;
    public List<Integer> walks;
    public String attrib[];
    public int xCoord;
    public int yCoord;
    
    public Vertex(){
        departs = new ArrayList<Integer>(); 
        walks = new LinkedList<Integer>();
    }
}
