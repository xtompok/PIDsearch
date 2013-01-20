/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.Date;

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
    
    public boolean goesAt(Date when)
    {
        return true;
    }
    
}


