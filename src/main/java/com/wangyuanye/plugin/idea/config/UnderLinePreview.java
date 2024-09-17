package com.wangyuanye.plugin.idea.config;

import javax.swing.*;
import java.awt.*;

/**
 * 下划线预览
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
public class UnderLinePreview extends JLabel {

    private final Color underlineColor;  // 下划线颜色

    public UnderLinePreview(String text, Color underlineColor) {
        super(text);
        this.underlineColor = underlineColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 获取 Graphics2D 对象进行高级绘制
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 获取字体的度量信息
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int underlineY = getHeight() - fm.getDescent() + 2;  // 下划线的Y坐标

        // 设置下划线颜色并绘制下划线
        g2.setColor(underlineColor);
        g2.setStroke(new BasicStroke(2));  // 设置下划线的粗细
        g2.drawLine(0, underlineY, textWidth, underlineY);  // 绘制下划线
    }
}
