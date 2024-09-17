package com.wangyuanye.plugin.idea;

import com.intellij.util.ui.ItemRemovable;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import lombok.Setter;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author wangyuanye
 * @since v0.0.1
 **/
public class HeadModel extends AbstractTableModel implements ItemRemovable {
    private final String[] ourColumnNames = new String[]{
            "源码文件",
            "备注"
    };
    private final Class[] ourColumnClasses = new Class[]{String.class, String.class};

    @Setter
    private List<MarkPointHead> headList;

    HeadModel(List<MarkPointHead> headList) {
        this.headList = headList;
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
        return headList.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * 获取行对象
     *
     * @param rowIndex 行号
     * @return 行对象
     */
    public Object getRowData(int rowIndex) {
        return headList.get(rowIndex);
    }

    @Override
    public Object getValueAt(int row, int column) {
        MarkPointHead head = headList.get(row);
        return switch (column) {
            case 0 -> head.getClassPath();
            case 1 -> head.getShowName();
            default -> new RuntimeException("错误的列: " + column);
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        MarkPointHead head = headList.get(row);
        switch (column) {
            case 0 -> head.setClassPath((String) value);
            case 1 -> head.setShowName((String) value);
            default -> throw new RuntimeException("错误的列: " + column);
        }
    }

    @Override
    public void removeRow(int index) {
        headList.remove(index);
        fireTableRowsDeleted(index, index);
    }
}
