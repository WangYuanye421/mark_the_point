package com.wangyuanye.plugin.idea.listeners;

import cn.hutool.core.lang.Pair;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyCache;
import com.wangyuanye.plugin.core.service.MyMarkerService;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import com.wangyuanye.plugin.idea.DialogMarklineDetail;
import com.wangyuanye.plugin.idea.ex.MyCustomElementRenderer;
import com.wangyuanye.plugin.idea.ex.RoundedBoxHighlighterRenderer;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static com.wangyuanye.plugin.util.MyUtils.string2Color;

/**
 * 文件标记渲染
 *
 * @author wangyuanye
 **/
public class MarklineEditorRenderer implements EditorFactoryListener {
    private final MyMarkerService markerService;
    EditorMouseMotionListener mouseMotionListener;
    EditorMouseListener mouseListener;

    public MarklineEditorRenderer() {
        this.markerService = MyCache.CACHE_INSTANCE; // 缓存代理
//        mouseMotionListener = new EditorMouseMotionListener() {
//            @Override
//            public void mouseMoved(@NotNull EditorMouseEvent e) {
//                Editor editor = e.getEditor();
//                if (e.getInlay() != null && e.getInlay().getRenderer() instanceof MyCustomElementRenderer) {
//                    // 设置光标为手形
//                    editor.getContentComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                } else {
//                    // 当鼠标移出Inlay时，恢复为默认光标
//                    editor.getContentComponent().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
//                }
//            }
//        };
//        mouseListener = new EditorMouseListener() {
//            @Override
//            public void mouseClicked(@NotNull EditorMouseEvent e) {
//                if (e.getInlay() != null && e.getInlay().getRenderer() instanceof MyCustomElementRenderer myInlayRenderer) {
//                    MarkPointLine markPointLine = myInlayRenderer.getMarkPointLine();
//                    // 按钮点击事件
//                    showDocumentationPopup(e.getEditor(), markPointLine);
//                }
//            }
//        };
    }

    private void showDocumentationPopup(Editor editor, MarkPointLine markPointLine) {
        DialogMarklineDetail marklineDetail = new DialogMarklineDetail(editor, markPointLine);
        if (marklineDetail.showAndGet()) {
            // 将选中内容进行颜色标记
            LogicalPosition logicalStartPosition = new LogicalPosition(markPointLine.startLine, markPointLine.startColumn);
            LogicalPosition logicalEndPosition = new LogicalPosition(markPointLine.endLine, markPointLine.endColumn);
            // 高亮色自定义
            setMarkerPointBgColor(editor, logicalStartPosition, logicalEndPosition,
                    string2Color(markPointLine.getRegularColor()),
                    string2Color(markPointLine.getDarkColor()));

            // 保存标记
            MyMarkerServiceImpl.INSTANCE.saveMarkLine(markPointLine);
        } else {
            System.out.println("quit the dialog");
        }
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        if (virtualFile == null || virtualFile.isWritable()) return;

        renderBgColor(editor, virtualFile);
        // 鼠标监听
        //addMouseListener(editor, virtualFile);
    }

    private void addMouseListener(Editor editor, VirtualFile virtualFile) {
        Pair<Boolean, MarkPointHead> checked = checkMarkFile(virtualFile);
        if (!checked.getKey()) return;

        editor.addEditorMouseMotionListener(mouseMotionListener);

        editor.addEditorMouseListener(mouseListener);
    }

    // 当前文件是否是笔记文件
    private Pair<Boolean, MarkPointHead> checkMarkFile(VirtualFile virtualFile) {
        String fileName = virtualFile.getPath();
        MarkPointHead markPointHead = markerService.getMarkPointHead(fileName);
        boolean b = markPointHead != null;
        return new Pair<>(b, markPointHead);
    }

    private void renderBgColor(Editor editor, VirtualFile virtualFile) {
        Pair<Boolean, MarkPointHead> checked = checkMarkFile(virtualFile);
        if (!checked.getKey()) return;
        MarkPointHead markPointHead = checked.getValue();
        List<MarkPointLine> markLines = markerService.getMarkLines(markPointHead.getClassPath());
        if (markLines.isEmpty()) {
            return;
        }
        for (MarkPointLine markLine : markLines) {
            render(markLine, editor);
        }

    }

    private void render(MarkPointLine markPointLine, Editor editor) {
        // 创建逻辑位置
        LogicalPosition logicalStartPosition = new LogicalPosition(markPointLine.startLine, markPointLine.startColumn);
        LogicalPosition logicalEndPosition = new LogicalPosition(markPointLine.endLine, markPointLine.endColumn);

        // 设置颜色
        Color color = MyUtils.string2Color(markPointLine.getRegularColor());
        Color darkColor = MyUtils.string2Color(markPointLine.getDarkColor());
        setMarkerPointBgColor(editor, logicalStartPosition, logicalEndPosition, color, darkColor);

        // 构建inlay
        IdeaFileEditorUtil.buildInlayElement(editor, markPointLine);
    }

    private void setMarkerPointBgColor(Editor editor, LogicalPosition start, LogicalPosition end, Color color, Color darkColor) {
        int startOffset = editor.logicalPositionToOffset(start);
        int endOffset = editor.logicalPositionToOffset(end);
        TextAttributes attributes = new TextAttributes();
        MarkupModel markupModel = editor.getMarkupModel();
        // 移除已存在的
        @NotNull RangeHighlighter[] allHighlighters = markupModel.getAllHighlighters();
        for (RangeHighlighter allHighlighter : allHighlighters) {
            if (allHighlighter.getStartOffset() == startOffset
                    && allHighlighter.getEndOffset() == endOffset) {
                markupModel.removeHighlighter(allHighlighter);
                break;
            }
        }
        markupModel.addRangeHighlighter(
                startOffset,
                endOffset,
                HighlighterLayer.SELECTION - 1,
                attributes,
                HighlighterTargetArea.EXACT_RANGE
        ).setCustomRenderer(new RoundedBoxHighlighterRenderer(color, darkColor));
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        event.getEditor().removeEditorMouseMotionListener(mouseMotionListener);
        event.getEditor().removeEditorMouseListener(mouseListener);
    }
}
