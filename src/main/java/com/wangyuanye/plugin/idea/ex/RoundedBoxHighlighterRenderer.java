package com.wangyuanye.plugin.idea.ex;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import com.wangyuanye.plugin.idea.config.ConfigPersistent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * 自定义高亮背景框
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
public class RoundedBoxHighlighterRenderer implements CustomHighlighterRenderer {
    private final ConfigPersistent configPersistent;
    Color regular;
    Color darkColor;

    public RoundedBoxHighlighterRenderer(Color regular, Color darkColor) {
        this.regular = regular;
        this.darkColor = darkColor;
        this.configPersistent = ApplicationManager.getApplication().getService(ConfigPersistent.class);
    }

    @Override
    public void paint(@NotNull Editor editor, @NotNull RangeHighlighter highlighter, @NotNull Graphics g) {
        ConfigPersistent.MTPConfig state = configPersistent.getState();
        if (state == null || state.getMarkStyle() == null) {
            // 默认使用下划线
            underLine(editor, highlighter, (Graphics2D) g);
        } else {
            Integer style = state.getMarkStyle();
            if (style == 0) {
                border(editor, highlighter, (Graphics2D) g);
            } else if (style == 1) {
                bg(editor, highlighter, (Graphics2D) g);
            } else {
                underLine(editor, highlighter, (Graphics2D) g);
            }
        }

    }

    private void underLine(Editor editor, RangeHighlighter highlighter, Graphics2D g) {
        int UNDERLINE_GAP = 3; // 两条下划线之间的距离
        int UNDERLINE_HEIGHT = 3; // 下划线的高度

        int startOffset = highlighter.getStartOffset();
        int endOffset = highlighter.getEndOffset();

        // 获取起始和结束的可视坐标
        Point startPoint = editor.visualPositionToXY(editor.offsetToVisualPosition(startOffset));
        Point endPoint = editor.visualPositionToXY(editor.offsetToVisualPosition(endOffset));
        g.setColor(new JBColor(regular, darkColor)); // 设置下划线颜色
        // 计算下划线的y坐标（基于文本底部位置）
        int lineHeight = editor.getLineHeight();
        int baselineY = startPoint.y + lineHeight - 2;  // 计算下划线的基线位置
        // 绘制第一条下划线
        g.fillRect(startPoint.x, baselineY, endPoint.x - startPoint.x, UNDERLINE_HEIGHT);
        // 绘制第二条下划线，距离第一条线一定间距
        //g.fillRect(startPoint.x, baselineY + UNDERLINE_GAP, endPoint.x - startPoint.x, UNDERLINE_HEIGHT);
    }

    private void border(Editor editor, RangeHighlighter highlighter, Graphics2D g) {
        int startOffset = highlighter.getStartOffset();
        int endOffset = highlighter.getEndOffset();

        // 获取开始和结束的可视位置坐标（返回 Point）
        Point startPoint = editor.visualPositionToXY(editor.offsetToVisualPosition(startOffset));
        Point endPoint = editor.visualPositionToXY(editor.offsetToVisualPosition(endOffset));

        g.setColor(new JBColor(regular, darkColor)); // 设置边框颜色
        g.setStroke(new BasicStroke(1));  // 设置边框粗细

        // 计算矩形的宽度和高度
        int width = endPoint.x - startPoint.x;
        int height = editor.getLineHeight();  // 使用编辑器的行高作为矩形高度

        // 绘制圆角矩形边框
        g.drawRoundRect(startPoint.x, startPoint.y, width, height, 10, 10);
    }

    private void bg(Editor editor, RangeHighlighter highlighter, Graphics2D g) {
        // 获取要高亮的文本范围
        TextRange textRange = new TextRange(highlighter.getStartOffset(), highlighter.getEndOffset());

        // 将文本范围的起始和结束位置转换为视图坐标
        int startOffset = highlighter.getStartOffset();
        int endOffset = highlighter.getEndOffset();

        // 获取起始和结束位置在屏幕上的位置信息
        Point startPoint = editor.visualPositionToXY(editor.offsetToVisualPosition(startOffset));
        Point endPoint = editor.visualPositionToXY(editor.offsetToVisualPosition(endOffset));

        // 设置高亮颜色和填充样式
        g.setColor(new JBColor(regular, darkColor));

        // 绘制带圆角的矩形来实现高亮效果
        g.fillRoundRect(startPoint.x, startPoint.y, endPoint.x - startPoint.x, editor.getLineHeight(), 10, 10);

        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(startPoint.x, startPoint.y, endPoint.x - startPoint.x, editor.getLineHeight(), 10, 10);
    }
}
