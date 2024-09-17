package com.wangyuanye.plugin.idea.action;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyCache;
import com.wangyuanye.plugin.core.service.MyMarkerService;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.idea.DialogMarklineDetail;
import com.wangyuanye.plugin.idea.config.ConfigPersistent;
import com.wangyuanye.plugin.idea.ex.RoundedBoxHighlighterRenderer;
import com.wangyuanye.plugin.idea.listeners.CustomMsgListener;
import com.wangyuanye.plugin.util.IdeaBaseUtil;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
import com.wangyuanye.plugin.util.IdeaMessageUtil;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.wangyuanye.plugin.idea.LineTab.removeInlayElement;
import static com.wangyuanye.plugin.util.MyUtils.string2Color;

/**
 * 保存标记
 *
 * @author wangyuanye
 * @since v0.0.1
 **/
public class SaveAction extends AnAction implements Disposable {
    public static final int LAY_NUM = HighlighterLayer.SELECTION - 1;

    public SaveAction() {
        super(IdeaMessageUtil.getMessage("name.action"),
                IdeaMessageUtil.getMessage("desc.action"),
                IconLoader.getIcon("/icons/note.svg", SaveAction.class));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        ConfigPersistent configPersistent = ApplicationManager.getApplication().getService(ConfigPersistent.class);
        ConfigPersistent.MTPConfig state = configPersistent.getState();
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        // 获取当前类文件并判断
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile.isWritable()) return;
        String classPath = virtualFile.getPath();
        Pair<Boolean, String> hasLock = fileHasLock(anActionEvent.getProject(), classPath);
        if (hasLock.first) {
            IdeaMessageUtil.myTipsI18n("tips.lock", hasLock.second);
            return;
        }
        // 获取选中模型
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            IdeaMessageUtil.myTips("未选中文本区域");
            return;// 插件是以高亮为主体,并非note,所以没有选择文本进行高亮,则不执行
        }
        int selectionStart = selectionModel.getSelectionStart();
        int selectionEnd = selectionModel.getSelectionEnd();
        // 转为逻辑位置
        LogicalPosition begin = editor.offsetToLogicalPosition(selectionStart);
        LogicalPosition end = editor.offsetToLogicalPosition(selectionEnd);
        // 不支持跨行
        if (begin.line != end.line) {
            IdeaMessageUtil.myTips("不支持跨行选择");
            return;
        }
        // 标记内容
        String markerContent = getHighLightText(editor, begin, end);
        // 是否已存在标记,存在则认为是修改,否则是新增
        MarkPointLine existLine = MyUtils.getService().getMarkLine(classPath, begin.line);
        if (existLine == null) {
            // add new line
            MarkPointLine markPointLine = new MarkPointLine(classPath, markerContent, begin, end);
            popupDialog(editor, markPointLine, null, null, anActionEvent);
        } else {
            // update line
            LogicalPosition oldBegin = existLine.getBeginPosition();
            LogicalPosition oldEnd = existLine.getEndPosition();
            existLine.setBeginPosition(begin);
            existLine.setEndPosition(end);
            existLine.setMarkContent(markerContent);
            popupDialog(editor, existLine, oldBegin, oldEnd, anActionEvent);
        }
        // 一旦进行了添加,则认为当前项目锁定了文件
        setFileLock(anActionEvent.getProject(), classPath);
    }

    /**
     * 加锁文件, 在监听器中解锁
     *
     * @param project   项目
     * @param classPath 文件全路径
     * @see com.wangyuanye.plugin.idea.listeners.MyProjectManagerListener
     */
    private void setFileLock(Project project, String classPath) {
        MyMarkerService myService = MyCache.CACHE_INSTANCE;
        MarkPointHead head = myService.getMarkPointHead(classPath);
        if (head != null) {
            myService.lockMarkPointHead(head.getId(), project.getName());
        }
    }

    // 是否在别的项目中编辑
    private Pair<Boolean, String> fileHasLock(Project project, String classPath) {
        MarkPointHead head = MyCache.CACHE_INSTANCE.getMarkPointHead(classPath);
        if (head != null) {
            String lockName = head.getLockName();
            if (lockName != null && !lockName.isEmpty()) {
                if (!lockName.equals(project.getName())) {
                    return new Pair<>(true, lockName);
                }
            }
        }
        return new Pair<>(false, null);
    }

    // 详情设置
    private MarkPointLine popupDialog(Editor editor, MarkPointLine markPointLine, LogicalPosition oldBegin,
                                      LogicalPosition oldEnd, AnActionEvent event) {
        DialogMarklineDetail marklineDetail = new DialogMarklineDetail(editor, markPointLine);
        if (marklineDetail.showAndGet()) {
            // 高亮色自定义
            processHighlighter(markPointLine.isAdd(), editor, markPointLine,
                    oldBegin, oldEnd,
                    string2Color(markPointLine.getRegularColor()),
                    string2Color(markPointLine.getDarkColor()));
            // 保存标记
            MyMarkerServiceImpl.INSTANCE.saveMarkLine(markPointLine);
            // 创建行尾元素
            IdeaFileEditorUtil.buildInlayElement(editor, markPointLine);
            // 发布消息
            IdeaBaseUtil.getMessageBus()
                    .syncPublisher(CustomMsgListener.TOPIC)
                    .onMessageReceived(markPointLine);
        }
        return markPointLine;
    }

    // 处理高亮
    private void processHighlighter(boolean isAdd, Editor editor, MarkPointLine markPointLine,
                                    LogicalPosition oldBegin, LogicalPosition oldEnd,
                                    Color regular, Color dark) {
        int startOffset = editor.logicalPositionToOffset(markPointLine.getBeginPosition());
        int endOffset = editor.logicalPositionToOffset(markPointLine.getEndPosition());
        TextAttributes textAttributes = new TextAttributes();

        MarkupModel markupModel = editor.getMarkupModel();

        if (!isAdd) {
            // 移除已存在的,修改场景
            removeHighlighter(editor, oldBegin, oldEnd, markupModel, LAY_NUM);
            // 移除inlay
            removeInlayElement(editor, markPointLine);
        }
        markupModel.addRangeHighlighter(
                startOffset,
                endOffset,
                LAY_NUM,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
        ).setCustomRenderer(new RoundedBoxHighlighterRenderer(regular, dark));
    }

    public static void removeHighlighter(Editor editor, LogicalPosition oldBegin, LogicalPosition oldEnd,
                                         MarkupModel markupModel, int layNum) {
        int oldBeginOffset = editor.logicalPositionToOffset(oldBegin);
        int oldEndOffset = editor.logicalPositionToOffset(oldEnd);
        @NotNull RangeHighlighter[] allHighlighters = markupModel.getAllHighlighters();
        for (RangeHighlighter allHighlighter : allHighlighters) {
            if (layNum == allHighlighter.getLayer()) {
                if (allHighlighter.getStartOffset() == oldBeginOffset
                        && allHighlighter.getEndOffset() == oldEndOffset) {
                    markupModel.removeHighlighter(allHighlighter);
                    break;
                }
            }
        }
    }

    private String getHighLightText(Editor editor, LogicalPosition start, LogicalPosition end) {
        int startOffset = editor.logicalPositionToOffset(start);
        int endOffset = editor.logicalPositionToOffset(end);
        Document document = editor.getDocument();
        return document.getText(new TextRange(startOffset, endOffset));
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            e.getPresentation().setVisible(false);
            return;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            e.getPresentation().setVisible(false);
            return;
        }
        if (psiFile.getVirtualFile().isWritable()) {
            e.getPresentation().setVisible(false);
            return;
        }
        // todo 未选中内容,不显示
        e.getPresentation().setVisible(true);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


    @Override
    public void dispose() {
        System.out.println("save action close...");
    }

}

