package pidsearch;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/** Class for representing information about connection.
 * 
 * Connection means one run of the transport vehicle from starting starting station
 * to ending station.
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
    
    /** Make a new empty connection.
     *
     */
    public Connection(){
        validity = new boolean[366];
    }
    
    /** Generate a validity bitmap from given string.
     * 
     * This method takes the String in given format, calculates from it the 
     * bitmap of validity and sets the validity attribute. This method must be
     * called before {@link pidsearch.Connection#goesAt goesAt} is called.
     * 
     * @param valStr String in format {@code stamp days wc1:wm1 wc2:wm2 ...} where
     * <ul>
     * <li>{@code stamp} is timestamp of beginning of validity the timetable</li>
     * <li>{@code days} is number of days, when connection is valid</li>
     * <li>{@code wcn:wmn} week count : week mask, where week count is number 
     * of weeks, when is week mask valid and week mask has 1 on days of week,
     * when connection is valid</li>
     * </ul>
     */
    public void genValidityBitmap(String valStr){
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
        }
        
    }
    
    /** Goes the connection at specified day?.
     * 
     * @see pidsearch.Connection#genValidityBitmap genValidityBitmap
     * 
     * @param when Calendar object with set date
     * @return true, if connection goes at specified date, false otherwise.
     */
    public boolean goesAt(Calendar when)
    {
        if (validity[when.get(Calendar.DAY_OF_YEAR)])
            return true;
        else return false;
    }
    
}


