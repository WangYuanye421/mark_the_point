package com.wangyuanye.plugin.idea;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
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
    public static final String TAB_NAME = "Note";
    // 加载数据
    private MyMarkerServiceImpl myMarkerServiceImpl;
    private MarkPointHead head;
    private List<MarkPointLine> lineList;
    private JBTable lineTable;
    private LineModel lineModel;
    private Project project;
    private VirtualFile virtualFile;

    public LineTab() {

    }

    public LineTab(@NotNull MarkPointHead head, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        this.head = head;
        this.project = project;
        this.virtualFile = virtualFile;

        // 查询head下的所有笔记
        myMarkerServiceImpl = MyMarkerServiceImpl.INSTANCE;
        lineList = myMarkerServiceImpl.getMarkLines(head.getLinesFileName());
        lineModel = new LineModel(lineList);
        lineTable = new JBTable(lineModel);
        lineTable.setColumnSelectionAllowed(false);
        lineTable.getTableHeader().setReorderingAllowed(false);// 禁止列拖动
        lineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public TabInfo buildLineTab() {
        // todo 构建markline表格
        // Column "mark"
        TableColumn columnName = lineTable.getColumnModel().getColumn(0);
        columnName.setPreferredWidth(100);
        columnName.setMinWidth(100);


        // Column "remark"
        TableColumn remark = lineTable.getColumnModel().getColumn(1);
        remark.setPreferredWidth(60);
        remark.setMinWidth(60);
        remark.setMaxWidth(60);

        JPanel commandsPanel = new JPanel(new BorderLayout());
        //ActionRun actionRun = new ActionRun(lineTable);// 运行
        commandsPanel.add(ToolbarDecorator.createDecorator(lineTable)
//                .setAddAction(new AnActionButtonRunnable() {
//                    @Override
//                    public void run(AnActionButton button) {
//                        stopEditing();
//                        System.out.println("line add");
//                    }
//                })
//                .setEditAction(new AnActionButtonRunnable() {
//                    @Override
//                    public void run(AnActionButton button) {
//
//                    }
//                })
                .setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        System.out.println("line remove");
                    }
                })
                //.addExtraAction(actionRun)
                .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        // 双击
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent e) {
                int selectedIndex = lineTable.getSelectedRow();
                if (selectedIndex < 0 || selectedIndex >= lineModel.getRowCount()) {
                    return true;
                }
                MarkPointLine line = lineList.get(selectedIndex);
                LogicalPosition position = new LogicalPosition(line.getStartLine(), line.getStartColumn(), line.isStartLeansForward());
                IdeaFileEditorUtil.openAndNavigate(project, virtualFile, position);
                return true;
            }
        }.installOn(lineTable);
        return new TabInfo(commandsPanel).setText(LineTab.TAB_NAME);
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
