package com.wangyuanye.plugin.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/9/1
 **/
public class LoadAction extends AnAction {

    /**
     * MyMarker{classPath='/usr/local/Cellar/openjdk/22.0.2/libexec/openjdk.jdk/Contents/Home/lib/src.zip!/java.base/java/lang/String.java',
     * markContent='rangeCheck', startLine=313, startColumn=24, startLeansForward=false, endLine=313,
     * endColumn=34, endLeansForward=false, note='hello world', bgColor='255, 255, 0', bgColorDark='255, 255, 0'}
     *
     * @param anActionEvent
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        // 文件是否只读
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile.isWritable()) return;

        MyMarkerServiceImpl markerService = MyMarkerServiceImpl.INSTANCE;
        String fileName = virtualFile.getPath();
        List<MarkPointLine> markLines = markerService.getMarkLines(fileName);
        if (markLines.isEmpty()) {
            return;
        }
        MarkPointLine loadMarker = markLines.get(0);
        boolean equals = loadMarker.getClassPath().equals(fileName);
        if (!equals) {
            return; // 类文件不同
        }
        // 创建逻辑位置
        LogicalPosition logicalStartPosition = new LogicalPosition(loadMarker.startLine, loadMarker.startColumn);
        LogicalPosition logicalEndPosition = new LogicalPosition(loadMarker.endLine, loadMarker.endColumn);

        // 设置颜色
        Color color = MyUtils.string2Color(loadMarker.getRegularColor());
        Color darkColor = MyUtils.string2Color(loadMarker.getDarkColor());
        setMarkerPointBgColor(editor, logicalStartPosition, logicalEndPosition, color, darkColor);



        // 定位到逻辑点
        // 光标移动到位置点
        editor.getCaretModel().moveToLogicalPosition(logicalStartPosition);
        // 屏幕移动到光标点
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
    }


    private void setMarkerPointBgColor(Editor editor, LogicalPosition start, LogicalPosition end, Color color, Color darkColor) {
        int startOffset = editor.logicalPositionToOffset(start);
        int endOffset = editor.logicalPositionToOffset(end);
        TextAttributes attributes = new TextAttributes();

        attributes.setBackgroundColor(new JBColor(color, darkColor)); // 设置背景色

        MarkupModel markupModel = editor.getMarkupModel();
        markupModel.addRangeHighlighter(
                startOffset,
                endOffset,
                HighlighterLayer.SELECTION,
                attributes,
                HighlighterTargetArea.EXACT_RANGE
        );
    }

    private String getMarkerPoint(Editor editor, LogicalPosition start, LogicalPosition end) {
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
        Document document = editor.getDocument();
        boolean writable = document.isWritable();
        if (writable) {
            e.getPresentation().setVisible(false);
            return;
        }
        e.getPresentation().setVisible(true);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
