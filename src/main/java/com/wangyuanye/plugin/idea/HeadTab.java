package com.wangyuanye.plugin.idea;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.*;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.idea.toolWindow.MyToolWindowFactory;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class HeadTab implements Disposable {
    private static final Logger logger = Logger.getInstance(HeadTab.class);
    public static final String TAB_NAME = MyUtils.getMessage("schema.tab.name");
    private final HeadModel headModel;
    private List<MarkPointHead> headList;
    private final JBTable headTable;
    // 加载数据
    private final MyMarkerServiceImpl myMarkerServiceImpl;

    public HeadTab() {
        // 加载数据
        myMarkerServiceImpl = MyMarkerServiceImpl.INSTANCE;
        headList = new ArrayList<>(myMarkerServiceImpl.getMarkHeads());
        headModel = new HeadModel(headList);
        headTable = new JBTable(headModel);
        headTable.setShowGrid(false);
        headTable.setFocusable(false);
        headTable.setFocusable(false);
        headTable.getTableHeader().setReorderingAllowed(false);// 禁止列拖动
        MyUtils.setEmptyText(headTable);

    }


    public TabInfo buildHeadTab(JBTabs jbTabs) {
        headList = new ArrayList<>(myMarkerServiceImpl.getMarkHeads());
        headModel.setHeadList(headList);
        // Column "name"
        TableColumn columnName = headTable.getColumnModel().getColumn(0);
//        columnName.setPreferredWidth(100);
//        columnName.setMinWidth(100);

        ToolWindow toolWindow = ToolWindowManager.getInstance(IdeaApiUtil.getProject()).getToolWindow(MyToolWindowFactory.myToolWindowId);
        JComponent toolWindowComponent = toolWindow.getComponent();

        JPanel schemasPanel = new JPanel(new BorderLayout());
        schemasPanel.add(ToolbarDecorator.createDecorator(headTable)
                .setEditAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        editSelectedSchema();
                    }
                })
                .setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        System.out.println("head remove, all sub line will remove");
                    }
                }).addExtraAction(new ActionCloseTab(jbTabs))
                .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        // double-click
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent e) {
                editSelectedSchema();
                return true;
            }
        }.installOn(headTable);
        return new TabInfo(schemasPanel).setText(HeadTab.TAB_NAME);
    }


    private void editSelectedSchema() {
        stopEditing();
        int selectedIndex = headTable.getSelectedRow();
        if (selectedIndex < 0 || selectedIndex >= headModel.getRowCount()) {
            return;
        }
        MarkPointHead sourceHead = headList.get(selectedIndex);
        MarkPointHead editHead = sourceHead.clone();
        DialogHead dialog = new DialogHead(headTable, editHead, selectedIndex, headList);
        IdeaApiUtil.setRelatedLocation(dialog);
        dialog.setTitle(MyUtils.getMessage("schema.dialog.edit.title"));
        if (!dialog.showAndGet()) {
            return;
        }
        logger.info("schema edit. schema:" + editHead.toString());
        headList.set(selectedIndex, editHead);
        //schemaService.updateSchema(editHead);// db
        headModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
        headTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
    }

    protected void stopEditing() {
        if (headTable.isEditing()) {
            TableCellEditor editor = headTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
        if (headTable.isEditing()) {
            TableCellEditor editor = headTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    @Override
    public void dispose() {
        System.out.println("应用关闭，执行清理");
    }
}
