package com.wangyuanye.plugin.idea;

import com.intellij.util.ui.ItemRemovable;
import com.wangyuanye.plugin.core.model.MarkPointHead;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class HeadModel extends AbstractTableModel implements ItemRemovable {
    private final String[] ourColumnNames = new String[]{
            "源码文件",
            "笔记数量"
    };
    private final Class[] ourColumnClasses = new Class[]{String.class, String.class};

    private List<MarkPointHead> headList;

    HeadModel(List<MarkPointHead> headList) {
        this.headList = headList;
    }

    public void setHeadList(List<MarkPointHead> headList) {
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
     * @param rowIndex
     * @return
     */
    public Object getRowData(int rowIndex) {
        return headList.get(rowIndex);
    }

    @Override
    public Object getValueAt(int row, int column) {
        MarkPointHead head = headList.get(row);
        return head.getShowName();
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        MarkPointHead head = headList.get(row);
        head.setShowName((String) value);
    }

    @Override
    public void removeRow(int index) {
        headList.remove(index);
        fireTableRowsDeleted(index, index);
    }
}
