/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pidsearch;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/** Class for resize column width in JTable.
 * 
 * @author vanadium
 */
public class ColumnResizer {

    /** Adjust width of columns in JTable.
     * 
     * This method adjusts column width according to the content of column. 
     * 
     * @see <a href="http://codeidol.com/java/swing/Tables-and-Trees/Size-Your-Columns-to-Suit-Your-JTables-Contents/">Copied from here</a>
     * @param table JTable to adjust.
     */
    public static void adjustColumnPreferredWidths(JTable table) {
        // strategy - get max width for cells in column and
        // make that the preferred width
        TableColumnModel columnModel = table.getColumnModel();
        for (int col = 0; col < table.getColumnCount(); col++) {

            int maxwidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer rend =
                        table.getCellRenderer(row, col);
                Object value = table.getValueAt(row, col);
                Component comp =
                        rend.getTableCellRendererComponent(table,
                        value,
                        false,
                        false,
                        row,
                        col);
                maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
            } // for row
            TableColumn column = columnModel.getColumn(col);
            column.setPreferredWidth(maxwidth);
        } // for col 
    }
}
