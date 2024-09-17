package com.wangyuanye.plugin.idea.config;

import com.intellij.ui.ColorPicker;
import com.wangyuanye.plugin.util.MyUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.wangyuanye.plugin.core.service.MyMarkerServiceImpl.PLUGIN_DIR;

/**
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
@Getter
public class MyConfig {
    private JLabel markStyle;
    private JRadioButton borderStyle;
    private JRadioButton bgStyle;
    private JRadioButton underLineStyle;
    private JLabel noteColor;
    private JLabel regular;
    private JLabel dark;
    private JPanel regularPanel;
    private JPanel darkPanel;
    private JLabel storage;
    private JPanel preview;
    private JPanel main;
    private JPanel dataPathPanel;
    ConfigPersistent configPersistent;
    final BorderPreviewPanel borerStylePreview;
    final UnderLinePreview underLineStylePreview;
    final JLabel bgStylePreview;
    Integer styleConfig = 2;

    public MyConfig(ConfigPersistent configPersistent) {
        this.configPersistent = configPersistent;
        borerStylePreview = new BorderPreviewPanel("hello world");
        underLineStylePreview = new UnderLinePreview("hello world", new Color(255, 68, 133, 255));
        bgStylePreview = new JLabel("<html><span style='background-color: #FF4485;'>hello world</span></html>");
        init();
    }

    private void init() {
        // 初始化格式预览
        ConfigPersistent.MTPConfig state = configPersistent.getState();
        if (state != null) {
            Integer style = state.getMarkStyle();
            if (style != null) {
                styleConfig = style;

            }
        }
        initPreview(styleConfig);
        // 初始化按钮
        initBtn();
        initNoteColor();

    }

    private void initNoteColor() {
        // 获取
        Color regular = new Color(255, 68, 133, 255);
        Color dark = Color.green;
        ConfigPersistent.MTPConfig state = configPersistent.getState();
        if (state != null) {
            String noteRegularColor = state.getNoteRegularColor();
            if (noteRegularColor != null) {
                regular = MyUtils.string2Color(noteRegularColor);
            }
            String noteDarkColor = state.getNoteDarkColor();
            if (noteDarkColor != null) {
                dark = MyUtils.string2Color(state.getNoteDarkColor());
            }
        }
        regularPanel.setBackground(regular);
        // 添加点击事件监听
        regularPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 打开取色器
                openColorPicker(regularPanel);
            }
        });

        darkPanel.setBackground(dark);
        darkPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 打开取色器
                openColorPicker(darkPanel);
            }
        });
    }

    private void openColorPicker(Component parent) {
        Color newColor = ColorPicker.showDialog(parent, "选择颜色", parent.getBackground(),
                false, null, false);
        if (newColor != null) {
            System.out.println("select color : " + newColor.toString());
            String color2String = MyUtils.color2String(newColor);
            parent.setBackground(newColor);
        }
    }

    private void initBtn() {
        switch (styleConfig) {
            case 0:
                borderStyle.setSelected(true);
                break;
            case 1:
                bgStyle.setSelected(true);
                break;
            default:
                underLineStyle.setSelected(true);
                break;
        }
        // 单选按钮
        borderStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borderStyle.setSelected(true);
                styleConfig = 0;
                initPreview(0);
                bgStyle.setSelected(false);
                underLineStyle.setSelected(false);
            }
        });
        bgStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bgStyle.setSelected(true);
                styleConfig = 1;
                initPreview(1);
                borderStyle.setSelected(false);
                underLineStyle.setSelected(false);
            }
        });
        underLineStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                underLineStyle.setSelected(true);
                styleConfig = 2;
                initPreview(2);
                bgStyle.setSelected(false);
                borderStyle.setSelected(false);
            }
        });
    }

    private void createUIComponents() {
        preview = new JPanel();
        dataPathPanel = new JPanel(new BorderLayout());
        dataPathPanel.add(new JLabel(PLUGIN_DIR), BorderLayout.WEST);
    }

    private void initPreview(int type) {
        switch (type) {
            case 0:
                preview.removeAll();
                preview.add(borerStylePreview);
                break;
            case 1:
                preview.removeAll();
                preview.add(bgStylePreview);
                break;
            default:
                preview.removeAll();
                preview.add(underLineStylePreview);
                break;
        }
        // 强制刷新和重绘组件
        preview.revalidate();
        preview.repaint();
    }

    public JPanel getPanel() {
        return main;
    }

}
