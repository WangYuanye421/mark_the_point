package com.wangyuanye.plugin.idea.ex;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.idea.config.ConfigPersistent;
import com.wangyuanye.plugin.idea.toolWindow.MyToolWindowFactory;
import com.wangyuanye.plugin.util.MyUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义inlay行尾
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
@Getter
public class MyCustomElementRenderer implements EditorCustomElementRenderer {
    private final MarkPointLine markPointLine;
    private final Icon icon;
    private final String text;
    private final Color noteColor;

    // 构造函数传入图标和文本
    public MyCustomElementRenderer(MarkPointLine markPointLine) {
        this.markPointLine = markPointLine;
        // 自定义图标和文本
        this.icon = getIconBasedOnTheme();
        String note = markPointLine.getNote();
        if (note != null && note.length() > 15) {
            this.text = note.substring(0, 15) + "....";
        } else {
            this.text = note;
        }
        ConfigPersistent configPersistent = ApplicationManager.getApplication().getService(ConfigPersistent.class);
        ConfigPersistent.MTPConfig state = configPersistent.getState();
        if (state != null && state.getNoteDarkColor() != null) { // 只要进行了设置,dark和regular都会有值,不用两个同时判断
            Color regular = MyUtils.string2Color(state.getNoteRegularColor());
            Color dark = MyUtils.string2Color(state.getNoteDarkColor());
            noteColor = new JBColor(regular, dark);
        } else {
            noteColor = MyToolWindowFactory.defaultNoteColor;
        }
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        // 计算总宽度：图标宽度 + 字符串宽度
        FontMetrics fontMetrics = inlay.getEditor().getContentComponent().getFontMetrics(inlay.getEditor().getColorsScheme().getFont(EditorFontType.PLAIN));
        int textWidth = 0;
        if (text != null && !text.isEmpty()) {
            textWidth = fontMetrics.stringWidth(text);
        }
        return icon.getIconWidth() + textWidth + 5; // 5 是图标和文本之间的间距
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        // 绘制图标
        int iconX = 10 + targetRegion.x;
        int iconY = targetRegion.y + (targetRegion.height - icon.getIconHeight()) / 2;  // 垂直居中
        icon.paintIcon(inlay.getEditor().getComponent(), g, iconX, iconY);

        // 绘制文本
        int textX = iconX + icon.getIconWidth() + 5; // 在图标右侧绘制文本，5 是间距
        FontMetrics fontMetrics = g.getFontMetrics();
        int textHeight = fontMetrics.getHeight();
        int textY = targetRegion.y + (targetRegion.height - textHeight) / 2 + fontMetrics.getAscent();  // 文字垂直居中
        // 设置文字颜色
        g.setColor(noteColor);
        g.drawString(text, textX, textY);
    }

    // 根据当前主题选择不同图标
    private Icon getIconBasedOnTheme() {
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        boolean isDarkTheme = scheme.getEditorFontName().contains("Dark") || scheme.getName().contains("Dark");
        if (isDarkTheme) {
            return IconLoader.getIcon("/icons/note_dark.svg", MyCustomElementRenderer.class); // Dark 主题图标
        } else {
            return IconLoader.getIcon("/icons/note.svg", MyCustomElementRenderer.class); // Light 主题图标
        }
    }
}
