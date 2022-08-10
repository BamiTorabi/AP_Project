package client.graphical;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class WrappableTableRenderer extends JTextArea implements TableCellRenderer {
    public WrappableTableRenderer(){
        setLineWrap(true);
        setWrapStyleWord(true);
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
        if (table.getRowHeight(row) != getPreferredSize().height) {
            table.setRowHeight(row, getPreferredSize().height);
        }
        return this;
    }
}
