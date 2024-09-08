package com.wangyuanye.plugin.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
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
        if(virtualFile.isWritable()) return;

        MyMarkerServiceImpl markerService = MyMarkerServiceImpl.INSTANCE;
        String fileName = virtualFile.getPath();
        List<MarkPointLine> markLines = markerService.getMarkLines(fileName);
        if(markLines.isEmpty()) {
            return;
        }
        MarkPointLine loadMarker = markLines.get(0);
        boolean equals = loadMarker.getClassPath().equals(fileName);
        if(!equals) {
            return; // 类文件不同
        }
        // 创建逻辑位置
        LogicalPosition logicalStartPosition = new LogicalPosition(loadMarker.startLine, loadMarker.startColumn);
        LogicalPosition logicalEndPosition = new LogicalPosition(loadMarker.endLine, loadMarker.endColumn);

        // 设置颜色
        Color color = getColor(loadMarker.getBgColor());
        Color darkColor = getColor(loadMarker.getBgColorDark());;
        setMarkerPointBgColor(editor, logicalStartPosition, logicalEndPosition, color,darkColor);
        // todo 拼接备注,通过注册到plugin.xml来实现, IDEA中鼠标悬浮类提示的实现
//        EditorLinePainter painter = new EditorLinePainter() {
//            @Override
//            public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile virtualFile, int lineNumber) {
//                // 创建一个结果集合来存储行扩展信息
//                List<LineExtensionInfo> result = new ArrayList<>();
//                // 配置颜色和样式
//                Color noteColor = JBColor.BLUE; // 示例颜色
//                // 创建并添加行扩展信息
//                result.add(new LineExtensionInfo(" [Note] ",
//                        new TextAttributes(null, null, noteColor, null, Font.PLAIN)));
//                result.add(new LineExtensionInfo(loadMarker.getNote(),
//                        new TextAttributes(null, null, noteColor, null, Font.PLAIN)));
//                return result;
//            }
//        };




        // 定位到逻辑点
        // 光标移动到位置点
        editor.getCaretModel().moveToLogicalPosition(logicalStartPosition);
        // 屏幕移动到光标点
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
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
        Document document = editor.getDocument();
        boolean writable = document.isWritable();
        if(writable) {
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
