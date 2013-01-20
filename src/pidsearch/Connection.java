/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

/**
 *
 * @author jethro
 */
public class Connection {
    public TransportType type;
    public String name;
  
    public String validityString;
    public boolean validity[];
  
    public String company;
    public String attrib[];
    
    public boolean goesAt(int timestamp)
    {
        return true;
    }
    
}


