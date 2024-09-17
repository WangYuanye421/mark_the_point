package com.wangyuanye.plugin.idea;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyCache;
import com.wangyuanye.plugin.core.service.MyMarkerService;
import com.wangyuanye.plugin.idea.listeners.CustomMsgListener;
import com.wangyuanye.plugin.util.IdeaBaseUtil;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
import com.wangyuanye.plugin.util.IdeaMessageUtil;
import com.wangyuanye.plugin.util.IdeaWindowUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.wangyuanye.plugin.util.IdeaBaseUtil.getLogger;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class HeadTab implements Disposable {
    private static final Logger logger = getLogger(HeadTab.class);
    public static final String TAB_NAME = IdeaMessageUtil.getMessage("head.tab.name");
    private final HeadModel headModel;
    private List<MarkPointHead> headList;
    private final JBTable headTable;
    private Project project;
    // 加载数据
    private final MyMarkerService myMarkerService;

    public HeadTab() {
        // 加载数据
        myMarkerService = MyCache.CACHE_INSTANCE;
        headList = new ArrayList<>(myMarkerService.getMarkHeads());
        headModel = new HeadModel(headList);
        headTable = new JBTable(headModel);
        headTable.setShowGrid(false);
        headTable.setFocusable(false);
        headTable.setFocusable(false);
        headTable.getTableHeader().setReorderingAllowed(false);// 禁止列拖动
        headTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        IdeaWindowUtil.tableEmptyText(headTable);
        onMessage();
    }

    private void onMessage() {
        // 监听消息
        MessageBus messageBus = IdeaBaseUtil.getMessageBus();
        MessageBusConnection connect = messageBus.connect();
        connect.subscribe(CustomMsgListener.TOPIC, new CustomMsgListener() {
            @Override
            public void onMessageReceived(MarkPointLine line) {
                System.out.println("收到消息: " + line.toString());
                refresh();
            }
        });
    }

    private void refresh() {
        headList = myMarkerService.getMarkHeads();
        headModel.setHeadList(headList);
    }

    public TabInfo buildHeadTab(JBTabs jbTabs, Project project) {
        this.project = project;
        headList = new ArrayList<>(myMarkerService.getMarkHeads());
        headModel.setHeadList(headList);

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
                        int selectedIndex = headTable.getSelectedRow();
                        if (selectedIndex < 0 || selectedIndex >= headModel.getRowCount()) {
                            return;
                        }
                        MarkPointHead sourceHead = headList.get(selectedIndex);
                        List<MarkPointLine> lines = myMarkerService.getMarkLines(sourceHead.getClassPath());
                        if (!lines.isEmpty()) {
                            IdeaMessageUtil.myTipsI18n("tips.delete.head");
                            return;
                        }
                        myMarkerService.removeMarkPointHead(sourceHead.getId());
                        headList.remove(selectedIndex);
                        TableUtil.removeSelectedItems(headTable);
                    }
                })
                .disableUpDownActions()
                .createPanel(), BorderLayout.CENTER);

        // double-click
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent e) {
                // 获取选中的Head
                int selectedRow = headTable.getSelectedRow();
                HeadModel model = (HeadModel) headTable.getModel();
                MarkPointHead selectHead = (MarkPointHead) model.getRowData(selectedRow);
                // 打开源码文件
                VirtualFile virtualFile = IdeaFileEditorUtil.openFileEditor(project, selectHead.getClassPath());
                // 创建新的tab
                LineTab lineTab = new LineTab(selectHead, project, virtualFile);
                TabInfo tab = lineTab.buildLineTab(jbTabs);
                tab.setText(selectHead.getShowName());
                jbTabs.addTab(tab);
                jbTabs.select(tab, true);// 激活当前tab
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
        DialogHead dialog = new DialogHead(headTable, editHead);
        IdeaWindowUtil.setRelatedLocation(dialog);
        if (!dialog.showAndGet()) {
            return;
        }
        logger.info("head edit. head:" + editHead.toString());
        headList.set(selectedIndex, editHead);
        myMarkerService.updateMarkPointHead(editHead.getId(), editHead.getShowName());// db
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
