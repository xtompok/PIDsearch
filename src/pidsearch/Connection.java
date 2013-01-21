/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jethro
 */
public class Connection {
    public TransportType type;
    public String name;
  
    public int weekMask;
    public boolean validity[];
  
    public String company;
    public String attrib[];
    
    public Connection(){
        validity = new boolean[366];
    }
    
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
    
    public boolean goesAt(Calendar when)
    {
        if (validity[when.get(Calendar.DAY_OF_YEAR)])
            return true;
        else return false;
    }
    
}


