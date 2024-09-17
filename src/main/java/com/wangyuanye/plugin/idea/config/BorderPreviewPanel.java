package com.wangyuanye.plugin.idea.config;

import javax.swing.*;
import java.awt.*;

/**
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
public class BorderPreviewPanel extends JPanel {
    private final JLabel label;

    public BorderPreviewPanel(String text) {
        label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // 添加一些内边距
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置边框颜色和圆角半径
        g2.setColor(new Color(0xFF4485));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
    }
}
