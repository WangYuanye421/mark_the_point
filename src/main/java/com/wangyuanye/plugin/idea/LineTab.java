package com.wangyuanye.plugin.idea;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.*;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 插件窗口
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
public final class LineTab implements Disposable {
    private static final Logger logger = Logger.getInstance(LineTab.class);
    public static final String TAB_NAME = MyUtils.getMessage("cmd.tab.name");
    private LineModel lineModel;
    private List<MarkPointLine> lineList;
    private JBTable lineTable;
    // 加载数据
    private MyMarkerServiceImpl myMarkerServiceImpl;
    private List<MarkPointHead> headList;


    public LineTab() {
        // 加载数据
//        lineList = myMarkerService.list(defaultSchema.getId());
//        lineModel = new LineModel(lineList);
//        lineTable = new JBTable(lineModel);
//        lineTable.setShowGrid(false);
//        lineTable.setFocusable(false);
//        lineTable.getTableHeader().setReorderingAllowed(false);// 禁止列拖动
//        MyUtils.setEmptyText(lineTable);
    }

    public void refreshTable(String schemaId) {
       // lineList = myMarkerService.list(schemaId);
        lineTable.removeAll();
        lineModel = new LineModel(lineList);
        lineTable.setModel(lineModel);
    }

    public TabInfo buildCommandTab(JBTabs jbTabs, HeadTab headTab) {

        // Column "name"
        TableColumn columnName = lineTable.getColumnModel().getColumn(0);
        columnName.setPreferredWidth(100);
        columnName.setMinWidth(100);


        // Column "remark"
        TableColumn remark = lineTable.getColumnModel().getColumn(1);
        remark.setPreferredWidth(100);
        remark.setMinWidth(100);

        JPanel commandsPanel = new JPanel(new BorderLayout());
        ActionRun actionRun = new ActionRun(lineTable);// 运行
        commandsPanel.add(ToolbarDecorator.createDecorator(lineTable)
                .setAddAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        System.out.println("line add");
                    }
                })
                .setEditAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        editSelectedCommand();
                    }
                })
                .setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        System.out.println("line remove");
                    }
                })
                .addExtraAction(actionRun)
                .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        // double-click in "Patterns" table should also start editing of selected pattern
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent e) {
                editSelectedCommand();
                return true;
            }
        }.installOn(lineTable);
        return new TabInfo(commandsPanel).setText(LineTab.TAB_NAME);
    }


    private void editSelectedCommand() {
        stopEditing();
        System.out.println("line edit");
    }

    protected void stopEditing() {
        if (lineTable.isEditing()) {
            TableCellEditor editor = lineTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
        if (lineTable.isEditing()) {
            TableCellEditor editor = lineTable.getCellEditor();
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
