package com.wangyuanye.plugin.idea.action;

import com.intellij.openapi.Disposable;
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
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.idea.DialogMarklineDetail;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.wangyuanye.plugin.util.MyUtils.string2Color;

/**
 * 保存标记
 *
 * @author wangyuanye
 * @date 2024/9/1
 **/
public class SaveAction extends AnAction implements Disposable {


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        // 获取当前类文件并判断
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile.isWritable()) return;

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
        MarkPointLine markPointLine = setMarklineDetail(editor, fileName, markerContent, logicalStartPosition, logicalEndPosition,
                anActionEvent);
        // 创建行尾元素
        IdeaFileEditorUtil.buildInlayElement(editor, markPointLine);
        editor.getSelectionModel();
    }




    // 详情设置
    private MarkPointLine setMarklineDetail(Editor editor, String fileName, String markerContent, LogicalPosition begin,
                                   LogicalPosition end, AnActionEvent event) {
        // 构建对象
        MarkPointLine markPointLine = new MarkPointLine(fileName, markerContent, begin, end);

        DialogMarklineDetail marklineDetail = new DialogMarklineDetail(editor, markPointLine);
        if (marklineDetail.showAndGet()) {
            // 将选中内容进行颜色标记
            System.out.println("press ok btn");
            System.out.println(markPointLine.toString());
            // 高亮色自定义
            setMarkerPointBgColor(editor, begin, end,
                    string2Color(markPointLine.getRegularColor()),
                    string2Color(markPointLine.getDarkColor()));

            // 保存标记
            MyMarkerServiceImpl.INSTANCE.addMarkLine(markPointLine);
            Messages.showMessageDialog(event.getProject(), markPointLine.toString(), "PSI Info", null);
        } else {
            System.out.println("quit the dialog");
        }
        return markPointLine;
    }

    private void setMarkerPointBgColor(Editor editor, LogicalPosition start, LogicalPosition end, Color regular, Color dark) {
        int startOffset = editor.logicalPositionToOffset(start);
        int endOffset = editor.logicalPositionToOffset(end);
        TextAttributes textAttributes = new TextAttributes();
        textAttributes.setBackgroundColor(new JBColor(regular, dark)); // 设置背景色为黄色

        MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = markupModel.addRangeHighlighter(
                startOffset,
                endOffset,
                HighlighterLayer.SELECTION - 1,
                textAttributes,
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

