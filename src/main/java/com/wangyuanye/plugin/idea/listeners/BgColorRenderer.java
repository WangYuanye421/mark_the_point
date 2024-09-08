package com.wangyuanye.plugin.idea.listeners;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyCache;
import com.wangyuanye.plugin.core.service.MyMarkerService;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * 背景渲染
 *
 * @author wangyuanye
 **/
public class BgColorRenderer implements EditorFactoryListener {
    private final MyMarkerService markerService;

    public BgColorRenderer() {
        this.markerService = new MyCache(MyMarkerServiceImpl.INSTANCE); // 缓存代理
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        renderBgColor(editor, virtualFile);
    }

    private void renderBgColor(Editor editor, VirtualFile virtualFile){
        if (editor == null || virtualFile == null) return;
        if (virtualFile.isWritable()) return;// 文件是否只读
        String fileName = virtualFile.getPath();
        List<MarkPointHead> markHeads = markerService.getMarkHeads();
        Optional<MarkPointHead> match = markHeads.stream().filter(e -> e.matchClassPath(fileName)).findFirst();
        if(match.isEmpty()) return;
        MarkPointHead markPointHead = match.get();
        List<MarkPointLine> markLines = markerService.getMarkLines(markPointHead.getLinesFileName());
        if(markLines.isEmpty()) {
            return;
        }
        for (MarkPointLine markLine : markLines) {
            render(markLine, editor);
        }

    }

    private void render(MarkPointLine loadMarker, Editor editor){
        // 创建逻辑位置
        LogicalPosition logicalStartPosition = new LogicalPosition(loadMarker.startLine, loadMarker.startColumn);
        LogicalPosition logicalEndPosition = new LogicalPosition(loadMarker.endLine, loadMarker.endColumn);

        // 设置颜色
        Color color = getColor(loadMarker.getBgColor());
        Color darkColor = getColor(loadMarker.getBgColorDark());;
        setMarkerPointBgColor(editor, logicalStartPosition, logicalEndPosition, color,darkColor);
    }

    private Color getColor(String colorStr) {
        String[] rgbValues = colorStr.split(",\\s*");

        // 将RGB分量转换为int
        int red = Integer.parseInt(rgbValues[0]);
        int green = Integer.parseInt(rgbValues[1]);
        int blue = Integer.parseInt(rgbValues[2]);

        // 创建Color对象
        return new Color(red, green, blue);
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
}
