
package pidsearch;

import java.util.Calendar;


/** Class with search preferences.
 * 
 * This class is like struct with preferences. It has no methods, only holds all
 * parameters of the search. 
 *
 * @author jethro
 */
public class SearchPreferences {
    	/** 
	 * Station from what search a connection.
	 */
	public Vertex from;
    	/** 
	 * Station to what search the connection.
	 */
	public Vertex to;
    	/**
	 * Calendar object in which date and time of search is hold.
	 */
	public Calendar when;
    	/**
	 * If set to true, program don't show less info about what is doing.
	 */
	public boolean quiet = false;
    	/**
	 * If set to true, it shows GUI and makes searches in it.
	 */
	public boolean graphics = false;
    	/**
	 * Speed of walks in meters per minute.
	 */
	public int walkSpeed = 60;
    	/**
	 * Repeated search if the graphics mode is not used.
	 */
	public boolean repeat = false;
        
}

