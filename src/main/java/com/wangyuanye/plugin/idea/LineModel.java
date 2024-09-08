package com.wangyuanye.plugin.idea;

import com.intellij.util.ui.ItemRemovable;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.util.MyUtils;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class LineModel extends AbstractTableModel implements ItemRemovable {
    private final String[] ourColumnNames = new String[]{
            MyUtils.getMessage("cmd.table.col_name"),
            MyUtils.getMessage("cmd.table.col_remark")
    };
    private final Class[] ourColumnClasses = new Class[]{String.class, String.class};

    private final List<MarkPointLine> lineList;

    LineModel(List<MarkPointLine> lineList) {
        this.lineList = lineList;
    }

    @Override
    public String getColumnName(int column) {
        return ourColumnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return ourColumnClasses[column];
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return lineList.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        MarkPointLine line = lineList.get(row);
        return switch (column) {
            case 0 -> line.getMarkContent();
            case 1 -> line.getNote();
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        MarkPointLine line = lineList.get(row);
        switch (column) {
            case 0 -> line.setMarkContent((String) value);
            case 1 -> line.setNote((String) value);
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void removeRow(int index) {
        lineList.remove(index);
        fireTableRowsDeleted(index, index);
    }
}
