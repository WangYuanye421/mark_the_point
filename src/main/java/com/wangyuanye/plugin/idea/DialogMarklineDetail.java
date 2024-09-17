package com.wangyuanye.plugin.idea;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ColorPicker;
import com.intellij.ui.components.JBTextArea;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 详情配置窗口
 *
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class DialogMarklineDetail extends DialogWrapper implements Disposable {
    private static final Logger log = LoggerFactory.getLogger(DialogMarklineDetail.class);
    private final MarkPointLine pointLine;// markline
    private final JBTextArea noteArea; // 文本域
    // 小色块的大小和颜色初始化
    private final JPanel lightColorPanel;
    private final JPanel darkColorPanel;
    private final Editor editor;

    private final static Color regularColor = new Color(255, 68, 133, 255);
    private final static Color darkColor = Color.GREEN;

    public DialogMarklineDetail(Editor editor, MarkPointLine pointLine) {
        super(true);
        this.editor = editor;
        this.pointLine = pointLine;
        if (pointLine.getDarkColor() == null) {
            pointLine.setDarkColor(MyUtils.color2String(darkColor));
        }
        if (pointLine.getRegularColor() == null) {
            pointLine.setRegularColor(MyUtils.color2String(regularColor));
        }
        this.noteArea = new JBTextArea(this.pointLine.getNote());
        this.noteArea.setToolTipText("添加笔记");
        // 创建一个表示颜色的小面板
        lightColorPanel = createColorPanel(getContentPanel(), regularColor, false);
        darkColorPanel = createColorPanel(getContentPanel(), darkColor, true);
        init();
    }

    @Override
    protected void init() {
        super.init();
        // 将逻辑位置信息转换为屏幕坐标
        CaretModel caretModel = editor.getCaretModel();
        LogicalPosition caretPosition = caretModel.getLogicalPosition();
        LogicalPosition targetPosition = new LogicalPosition(caretPosition.line + 1, caretPosition.column);
        Point screenPoint = editor.logicalPositionToXY(targetPosition);

        // 将坐标从编辑器的相对位置转换为屏幕上的绝对位置
        Point locationOnScreen = editor.getContentComponent().getLocationOnScreen();
        int x = locationOnScreen.x + screenPoint.x;
        int y = locationOnScreen.y + screenPoint.y;
        setLocation(x, y);
        setSize(270, 220);
        setTitle("添加笔记");

    }

    // 自定义按钮
//    @Override
//    protected Action @NotNull [] createActions() {
//        // 获取默认的OK和Cancel按钮
//        Action okAction = getOKAction();
//        Action cancelAction = getCancelAction();
//
//        // 设置OK按钮的图标
//        okAction.putValue(Action.SMALL_ICON, AllIcons.Actions.Commit);
//        // 设置Cancel按钮的图标
//        cancelAction.putValue(Action.SMALL_ICON, AllIcons.Actions.Cancel);
////        JButton okButton = new JButton(okAction);
////        JButton cancelButton = new JButton(cancelAction);
////        // 设置按钮的首选大小
////        Dimension buttonSize = new Dimension(50, 30); // 宽100，高50
////        okButton.setPreferredSize(buttonSize);
////        cancelButton.setPreferredSize(buttonSize);
//
//
//        return new Action[]{okAction, cancelAction};
//    }


    private JPanel createColorPanel(Component parent, Color color, boolean isDark) {
        JPanel panel = new JPanel();
        panel.setBackground(color);  // 初始颜色
        panel.setPreferredSize(new Dimension(18, 18)); // 设置色块大小
        panel.setMinimumSize(new Dimension(18, 18)); // 设置色块大小
        panel.setMaximumSize(new Dimension(18, 18));
        panel.setBorder(BorderFactory.createLineBorder(color));

        // 添加点击事件监听
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 打开取色器
                openColorPicker(parent, isDark);
            }
        });
        return panel;
    }

    private void openColorPicker(Component parent, boolean isDark) {
        // 使用已有的 ColorPicker，传入当前Dialog作为parent
        Color newColor = ColorPicker.showDialog(parent, "选择颜色", regularColor, false, null, false);
        if (newColor != null) {
            System.out.println("select color : " + newColor.toString());
            String color2String = MyUtils.color2String(newColor);
            // 更新色块背景颜色
            if (isDark) {
                darkColorPanel.setBackground(newColor);
                pointLine.setDarkColor(color2String);
            } else {
                lightColorPanel.setBackground(newColor);
                pointLine.setRegularColor(color2String);
            }
        }
    }

    // 打开dialog后，光标聚焦组件
    @Override
    public JComponent getPreferredFocusedComponent() {
        return noteArea;
    }

    @Override
    protected void doOKAction() {
        pointLine.setNote(noteArea.getText());
        close(OK_EXIT_CODE);
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        close(CANCEL_EXIT_CODE);
        super.doCancelAction();
    }

    @NotNull
    @Override
    protected List<ValidationInfo> doValidateAll() {
        return super.doValidateAll();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建上半部分的色块区域，1行2列的网格布局
        JPanel colorPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平方向填充
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // 不允许色块区域横向扩展
        gbc.insets = new Insets(0, 0, 0, 10); // 设置内边距


        // 创建左边的色块区域（明亮主题）
        JPanel lightPanel = new JPanel(new BorderLayout());
        lightPanel.setPreferredSize(new Dimension(80, 20)); // 固定宽度
        JLabel l1 = new JLabel("明亮主题 :");
        lightPanel.add(l1, BorderLayout.WEST);
        lightPanel.add(lightColorPanel, BorderLayout.CENTER); // 确保色块在中间
        colorPanel.add(lightPanel, gbc);
        gbc.gridx = 1;
        colorPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        gbc.gridx = 2;
        colorPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        gbc.gridx = 3;
        JPanel darkPanel = new JPanel(new BorderLayout());
        darkPanel.setPreferredSize(new Dimension(80, 20)); // 固定宽度
        JLabel l2 = new JLabel("黑暗主题 :");
        darkPanel.add(l2, BorderLayout.WEST);
        darkPanel.add(darkColorPanel, BorderLayout.CENTER);
        colorPanel.add(darkPanel, gbc);

        // 创建文本域区域
        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // 增加内部边距
        notePanel.add(noteArea, BorderLayout.CENTER);

        // 将色块面板添加到主面板的顶部
        mainPanel.add(colorPanel, BorderLayout.NORTH);
        mainPanel.add(new JSeparator(), BorderLayout.CENTER);
        // 将文本域面板添加到主面板的中间
        mainPanel.add(notePanel, BorderLayout.CENTER);
        return mainPanel;
    }

    @Override
    public void dispose() {
        // 移除全局鼠标监听器
        super.dispose(); // 确保正确销毁dialog
    }
}
