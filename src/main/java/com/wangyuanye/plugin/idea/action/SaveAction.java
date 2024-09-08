package com.wangyuanye.plugin.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * 保存标记
 * @author wangyuanye
 * @date 2024/9/1
 **/
public class SaveAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }

        // 获取当前类文件并判断
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if(virtualFile.isWritable()) return;
        String fileName = virtualFile.getPath();

        // 获取选中模型
        SelectionModel selectionModel = editor.getSelectionModel();
        int selectionStart = selectionModel.getSelectionStart();
        int selectionEnd = selectionModel.getSelectionEnd();

        // 转为逻辑位置
        LogicalPosition logicalStartPosition = editor.offsetToLogicalPosition(selectionStart);
        LogicalPosition logicalEndPosition = editor.offsetToLogicalPosition(selectionEnd);

        // 回显标记内容
        String markerContent = getMarkerPoint(editor, logicalStartPosition, logicalEndPosition);
        // 将选中内容进行颜色标记
        setMarkerPointBgColor(editor, logicalStartPosition, logicalEndPosition);
        // 保存标记
        markThePoint(fileName, markerContent, logicalStartPosition, logicalEndPosition, anActionEvent);
    }
    // todo 添加笔记
    private String addNote(){

        return null;
    }

    private void markThePoint(String fileName, String markerContent, LogicalPosition start, LogicalPosition end, AnActionEvent event){
        MarkPointLine markPointLine = new MarkPointLine(fileName, markerContent, start, end,
                "hello world", "255, 255, 0","255, 255, 0");
        MyMarkerServiceImpl.INSTANCE.addMarkLine(markPointLine);
        Messages.showMessageDialog(event.getProject(), markPointLine.toString(), "PSI Info", null);
    }


    private void setMarkerPointBgColor(Editor editor, LogicalPosition start, LogicalPosition end) {
        int startOffset = editor.logicalPositionToOffset(start);
        int endOffset = editor.logicalPositionToOffset(end);
        TextAttributes textAttributes = new TextAttributes();
        textAttributes.setBackgroundColor(new JBColor(new Color(255, 255, 0), new Color(255, 255, 0))); // 设置背景色为黄色

        MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = markupModel.addRangeHighlighter(
                startOffset,
                endOffset,
                HighlighterLayer.SELECTION - 1,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
        );
        // 移除高亮
        //markupModel.removeHighlighter(highlighter);
    }

    private String getMarkerPoint(Editor editor, LogicalPosition start, LogicalPosition end){
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
        if(psiFile.getVirtualFile().isWritable()) {
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
}
