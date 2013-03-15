/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/** Class for representing information about connection.
 * 
 * Connection means one run of the transport vehicle from starting starting station
 * to ending station.
 * 
 *
 * @author jethro
 */
public class Connection implements Serializable{
    /** 
     * Type of vehicle.
     */
    public TransportType type;
    /**
     * Name of the line.
     */
    public String name;
  
    /**
     * 
     */
    public int weekMask;
    /**
     * Bit array with true if it goes that day.
     * 
     * This is bit array representing year from 1st January, which is 0 and if
     * the connection goes that day, there is true. If the day is out of range
     * of validity current timetable data, it is false.
     */
    public boolean validity[];
  
    /**
     * Name of the comapny which operates the connection (currently unused).
     */
    public String company;
    /**
     * Attributes of the connection (currently unused).
     */
    public String attrib[];
    
    /**
     *
     */
    public Connection(){
        validity = new boolean[366];
    }
    
    /**
     *
     * @param valStr
     */
    public void makeValidityBitmap(String valStr){
        String [] cols;
        cols = valStr.split(" ");
        Calendar cal;
        cal = Calendar.getInstance();
        cal.setTimeInMillis(Integer.parseInt(cols[0])*1000);
        int start = cal.get(Calendar.DAY_OF_YEAR);
        int validity = Integer.parseInt(cols[1]);
        for (int i=2;i<(cols.length);i++){
            String[] weekCols;
            weekCols = cols[i].split(":");
            int weeks = Integer.parseInt(weekCols[0]);
            int weekMask = Integer.parseInt(weekCols[1], 16);
            for (int j=0;j<weeks*7;j++){
                if ((weekMask&(1<<(j%7)))!=0)
                    this.validity[start+j]=true;
            }
            start+=weeks*7;
            this.weekMask = weekMask;
        }
        
    }
    
    /**
     *
     * @param when
     * @return
     */
    public boolean goesAt(Calendar when)
    {
        if (validity[when.get(Calendar.DAY_OF_YEAR)])
            return true;
        else return false;
    }
    
}


