/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author jethro
 */
public class GraphicsSearch extends javax.swing.JFrame {

    PIDsearch search;

    /**
     * Creates new form GraphicsSearch
     */
    public GraphicsSearch(PIDsearch s) {
        search = s;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        searchPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fromField = new javax.swing.JTextField();
        toField = new javax.swing.JTextField();
        timeField = new javax.swing.JTextField();
        dateField = new javax.swing.JTextField();
        vyhledatBut = new javax.swing.JButton();
        fromErrorLabel = new javax.swing.JLabel();
        toErrorLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        jScrollPane = new javax.swing.JScrollPane();
        foundPane = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        searchPanel.setDoubleBuffered(false);
        searchPanel.setVerifyInputWhenFocusTarget(false);

        jLabel1.setText("Odkud:");

        jLabel2.setText("Kam:");

        jLabel3.setText("Datum:");

        jLabel4.setText("Čas:");

        fromField.setActionCommand("<Not Set>");
        fromField.setDoubleBuffered(true);
        fromField.setNextFocusableComponent(toField);

        toField.setNextFocusableComponent(timeField);

        timeField.setText("00:00");
        timeField.setNextFocusableComponent(dateField);

        dateField.setText("31.12.2013");
        dateField.setNextFocusableComponent(vyhledatBut);

        vyhledatBut.setText("Vyhledat");
        vyhledatBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vyhledatButActionPerformed(evt);
            }
        });

        fromErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        fromErrorLabel.setText("E");

        toErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        toErrorLabel.setText("E");

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("Chyba lávky!");

        org.jdesktop.layout.GroupLayout searchPanelLayout = new org.jdesktop.layout.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(searchPanelLayout.createSequentialGroup()
                        .add(vyhledatBut, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(errorLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(searchPanelLayout.createSequentialGroup()
                        .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fromField)
                            .add(searchPanelLayout.createSequentialGroup()
                                .add(timeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(30, 30, 30)
                                .add(jLabel3)
                                .add(18, 18, 18)
                                .add(dateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(0, 252, Short.MAX_VALUE))
                            .add(toField)))
                    .add(searchPanelLayout.createSequentialGroup()
                        .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(fromErrorLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 316, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(toErrorLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 316, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(160, 160, 160)))
                .addContainerGap())
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(fromField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fromErrorLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(toField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .add(2, 2, 2)
                .add(toErrorLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(timeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(dateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vyhledatBut)
                    .add(errorLabel))
                .addContainerGap(211, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Parametry", searchPanel);

        org.jdesktop.layout.GroupLayout foundPaneLayout = new org.jdesktop.layout.GroupLayout(foundPane);
        foundPane.setLayout(foundPaneLayout);
        foundPaneLayout.setHorizontalGroup(
            foundPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 542, Short.MAX_VALUE)
        );
        foundPaneLayout.setVerticalGroup(
            foundPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 384, Short.MAX_VALUE)
        );

        jScrollPane.setViewportView(foundPane);

        jTabbedPane1.addTab("Výsledky", jScrollPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vyhledatButActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vyhledatButActionPerformed
        boolean cancel = false;
        SearchPreferences pref;
        pref = new SearchPreferences();

        System.err.println("Clicked");
        if (!search.vertexForName.containsKey(fromField.getText())) {
            fromErrorLabel.setText("Zastavka nebyla nalezena.");
            fromErrorLabel.setVisible(true);
            cancel = true;
        } else {
            pref.from = search.vertexForName.get(fromField.getText());
            fromErrorLabel.setVisible(false);
        }
        if (!search.vertexForName.containsKey(toField.getText())) {
            toErrorLabel.setText("Zastavka nebyla nalezena.");
            toErrorLabel.setVisible(true);
            cancel = true;
        } else {
            toErrorLabel.setVisible(false);
            pref.to = search.vertexForName.get(toField.getText());
        }

        pref.when = Utilities.parseDate(dateField.getText());
        if (pref.when==null){
            errorLabel.setText("Chybne datum");
            errorLabel.setVisible(true);
            cancel = true;
        } else {
        pref.when = Utilities.parseTime(timeField.getText(),pref.when);
        if (pref.when==null){
            errorLabel.setText("Chybny cas");
            errorLabel.setVisible(true);
            cancel = true;
        }}
        

        if (cancel) {
            return;
        }
        
        System.err.println(Utilities.debugDate(pref.when));
        System.err.println("Searching...");
        errorLabel.setText(pref.from.name + "->" + pref.to.name);
        
        List<Arrival> cons;
        cons = search.search.searchConnection(pref);
        search.printConnections(cons);
        
        foundPane.setLayout(new GridLayout(0,1));
        System.err.println(foundPane.getLayout());
        
        List<ResultsTableModel> tm;
        tm = new LinkedList<ResultsTableModel>();
        for (Arrival arr: cons){
            JTable table;
            table = new JTable(new ResultsTableModel(arr));
            ColumnResizer.adjustColumnPreferredWidths(table);
            table.setPreferredScrollableViewportSize
                (new Dimension(500, table.getRowCount() * table.getRowHeight()));
            table.setFillsViewportHeight(false);
            JScrollPane sp;
            sp = new JScrollPane(table);
            sp.setPreferredSize(table.getPreferredScrollableViewportSize());
            foundPane.add(sp);
        }
        
        jTabbedPane1.setSelectedIndex(1);
        
    }//GEN-LAST:event_vyhledatButActionPerformed

    class ResultsTableModel implements TableModel{
        
        String[] colNames = {"Spoj","Odkud","Odj.","Kam","Přj."};
        String[][] table;
        

        
        public ResultsTableModel(Arrival arrival){
            List<Edge> edges = Utilities.condenseEdges(arrival.asList());
            List<String []> rows;
            rows = new LinkedList<String[]>();
            
            for (Edge e: edges){
                String [] row;
                row = new String[colNames.length];
                row[1] = e.from.name;
                row[3] = e.to.name;
                if (e instanceof WalkEdge){
                    row[0] = "Přesun";
                } else if (e instanceof ConEdge){
                    row[0] = ((ConEdge)e).connection.name;
                    row[2] = Utilities.strTime(((ConEdge) e).departure);
                    row[4] = Utilities.strTime(((ConEdge) e).departure + e.length);
                }
                rows.add(row);
            }
            table = new String[rows.size()][colNames.length];
            for (int i=0;i<rows.size();i++){
                table[i] = rows.get(i);
            }
            
        
        }
        
        
        @Override
        public int getRowCount() {
            return table.length;
        }

        @Override
        public int getColumnCount() {
            return colNames.length;
        }

        @Override
        public String getColumnName(int i) {
            return colNames[i];
        }

        @Override
        public Class<?> getColumnClass(int i) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int i, int i1) {
            return false;
        }

        @Override
        public Object getValueAt(int i, int i1) {
            return table[i][i1];
        }

        @Override
        public void setValueAt(Object o, int i, int i1) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void addTableModelListener(TableModelListener tl) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeTableModelListener(TableModelListener tl) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(final PIDsearch search) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GraphicsSearch gs;
                gs = new GraphicsSearch(search);
                gs.errorLabel.setVisible(false);
                gs.fromErrorLabel.setVisible(false);
                gs.toErrorLabel.setVisible(false);
                Calendar cal = Calendar.getInstance();
                gs.timeField.setText(Utilities.strTime(cal));
                gs.dateField.setText(Utilities.strDate(cal));
                gs.setVisible(true);
                gs.searchPanel.getRootPane().setDefaultButton(gs.vyhledatBut);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField dateField;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel foundPane;
    private javax.swing.JLabel fromErrorLabel;
    private javax.swing.JTextField fromField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField timeField;
    private javax.swing.JLabel toErrorLabel;
    private javax.swing.JTextField toField;
    private javax.swing.JButton vyhledatBut;
    // End of variables declaration//GEN-END:variables
}
