package com.wangyuanye.plugin.idea;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.InlayModel;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.idea.ex.MyCustomElementRenderer;
import com.wangyuanye.plugin.idea.listeners.CustomMsgListener;
import com.wangyuanye.plugin.util.IdeaBaseUtil;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.wangyuanye.plugin.idea.action.SaveAction.LAY_NUM;
import static com.wangyuanye.plugin.idea.action.SaveAction.removeHighlighter;

/**
 * 插件窗口
 *
 * @author wangyuanye
 * @since 2024/8/20
 **/
public final class LineTab implements Disposable {
    private static final Logger logger = Logger.getInstance(LineTab.class);
    public static final String TAB_NAME = "Note";
    // 加载数据
    private MyMarkerServiceImpl myMarkerServiceImpl;
    private MarkPointHead head;
    private List<MarkPointLine> lineList;
    //private Map<Long, MarkPointLine> pointLineMap;
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
        lineList = myMarkerServiceImpl.getMarkLines(head.getClassPath());
        //pointLineMap = lineList.stream().collect(Collectors.toMap(MarkPointLine::getLineId, Function.identity()));
        lineModel = new LineModel(lineList);
        lineTable = new JBTable(lineModel);
        lineTable.setColumnSelectionAllowed(false);
        lineTable.getTableHeader().setReorderingAllowed(false);// 禁止列拖动
        lineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        onMessage();
    }

    private void onMessage() {
        // 监听消息
        MessageBus messageBus = IdeaBaseUtil.getMessageBus();
        MessageBusConnection connect = messageBus.connect();
        connect.subscribe(CustomMsgListener.TOPIC, new CustomMsgListener() {
            @Override
            public void onMessageReceived(MarkPointLine line) {
                refresh();
            }
        });
    }

    private void refresh() {
        lineList = myMarkerServiceImpl.getMarkLines(head.getClassPath());
        //pointLineMap = lineList.stream().collect(Collectors.toMap(MarkPointLine::getLineId, Function.identity()));
        lineModel.setLineList(lineList);
    }

    public TabInfo buildLineTab(JBTabs jbTabs) {
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
        commandsPanel.add(ToolbarDecorator.createDecorator(lineTable)
                .setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        // 移除表格
                        int selectedIndex = lineTable.getSelectedRow();
                        if (selectedIndex < 0 || selectedIndex >= lineModel.getRowCount()) {
                            return;
                        }
                        MarkPointLine selectLine = lineList.get(selectedIndex);
                        // 移除数据库
                        myMarkerServiceImpl.removeMarkLine(selectLine.getClassPath(), selectLine.getLineId());
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                        boolean fileOpen = fileEditorManager.isFileOpen(virtualFile);
                        if (fileOpen) {
                            FileEditor selectedEditor = fileEditorManager.getSelectedEditor(virtualFile);
                            if (selectedEditor instanceof TextEditor) {
                                Editor editor = ((TextEditor) selectedEditor).getEditor();
                                // 去除高亮
                                removeHighlighter(editor, selectLine.getBeginPosition(), selectLine.getEndPosition(),
                                        editor.getMarkupModel(), LAY_NUM);
                                // 移除inlay对象
                                removeInlayElement(editor, selectLine);
                            }
                        }
                        // 刷新table
                        refresh();
                    }
                })
                .addExtraAction(new ActionCloseTab(jbTabs))
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

    public static void removeInlayElement(Editor editor, MarkPointLine markPointLine) {
        InlayModel inlayModel = editor.getInlayModel();
        int lineStartOffset = editor.getDocument().getLineStartOffset(markPointLine.getStartLine());
        int lineEndOffset = editor.getDocument().getLineEndOffset(markPointLine.getStartLine());

        // 获取该行内的所有Inlay元素
        List<Inlay<?>> inlayElements = inlayModel.getInlineElementsInRange(lineStartOffset, lineEndOffset);

        // 遍历所有Inlay并检查是否匹配markPointLine
        for (Inlay<?> inlay : inlayElements) {
            if (inlay.getRenderer() instanceof MyCustomElementRenderer) {
                inlay.dispose();
            }
        }
    }

    @Override
    public void dispose() {
        System.out.println("应用关闭，执行清理");
    }
}
