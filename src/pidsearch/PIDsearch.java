/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jethro
 */
public class PIDsearch {

    Map<String,Integer> vertexForName;
    List<Vertex> vertices;
    List<Connection> connections;
    List<Edge> edges;

    
    public PIDsearch() {

        
        PrepareData pd;
        pd = new PrepareData();
        vertices = pd.vertices;
        connections = pd.connections;
        edges = pd.edges;
        
            vertexForName = new HashMap<String, Integer>();
        for (int i=0;i<vertices.size();i++){
            //System.out.println(vertices.get(i).name);
            vertexForName.put(vertices.get(i).name,i);
        }
    }
        
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        PIDsearch search;
        search = new PIDsearch();
        search.CLI();
    }
    
    public int getStation(String type){
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(System.in));
        
        String st;
        st = null;
        do {
            System.out.print("Zadejte jmeno "+type+" stanice:");
            try {
                st = in.readLine();
            } catch (IOException ex) {
                System.err.println("Failed to read a station name");
                System.exit(1);
            }
        } while (! vertexForName.containsKey(st));
        return vertexForName.get(st);
    } 
    
    public void CLI(){
        int from;
        int to;
        from = getStation("vychozi");
        to = getStation("cilove");
        Calendar cal;
        cal = Calendar.getInstance();
        Date d = cal.getTime();
        searchConnection(from, to, d);
    }
    
    public void searchConnection(int from,int to,Date when){
        System.out.println("Searching connection from "+from+" to "+to);
    
    }
}
