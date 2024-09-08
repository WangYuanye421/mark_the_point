package com.wangyuanye.plugin.idea;

import com.intellij.util.ui.ItemRemovable;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.util.MyUtils;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class HeadModel extends AbstractTableModel implements ItemRemovable {
    private final String[] ourColumnNames = new String[]{
            MyUtils.getMessage("schema.table.col_name"),
    };
    private final Class[] ourColumnClasses = new Class[]{String.class};

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
        return 1;
    }

    @Override
    public int getRowCount() {
        return headList.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //return columnIndex == 1;勾选框也禁用,避免频繁点击,省去"唯一默认"的界面校验
        return false;
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
