package com.wangyuanye.plugin.idea;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.util.IdeaMessageUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class DialogMyCmd extends DialogWrapper {
    private final MarkPointLine pointLine;
    private final JBTextField cmdField;
    private final JBTextField remarkField;
    private final int myCmdIndex;
    private final List<MarkPointLine> myExistingLineList;
    private final JLabel helpIcon;
    private Component parent;


    DialogMyCmd(Component parent, MarkPointLine pointLine, int cmdIndex, List<MarkPointLine> existingLineList) {
        super(parent, true);
        this.parent = parent;
        setSize(300, 120);
        myCmdIndex = cmdIndex;
        myExistingLineList = existingLineList;
        setTitle(IdeaMessageUtil.getMessage("cmd.dialog.add.title"));
        setResizable(false);
        this.pointLine = pointLine;
        cmdField = new JBTextField(this.pointLine.getNote());

        remarkField = new JBTextField(this.pointLine.getNote());
        helpIcon = new JLabel(AllIcons.General.ContextHelp);
        helpIcon.setToolTipText(IdeaMessageUtil.getMessage("cmd.name_label_tip"));
        init();
    }


    // 打开dialog后，光标聚焦组件
    @Override
    public JComponent getPreferredFocusedComponent() {
        return cmdField;
    }

    @Override
    protected void doOKAction() {
//        pointLine.setName(cmdField.getText().trim());
//        pointLine.setRemark(remarkField.getText().trim());
        super.doOKAction();
    }

    @NotNull
    @Override
    protected List<ValidationInfo> doValidateAll() {
//        String cmdText = cmdField.getText().trim();
//        if (cmdText.isEmpty()) {
//            return Collections.singletonList(new ValidationInfo(MyUtils.getMessage("cmd.add.error_name"), cmdField));
//        }
//        for (int i = 0; i < myExistingMyCmdList.size(); i++) {
//            MyCmd pattern = myExistingMyCmdList.get(i);
//            if (myCmdIndex != i && cmdText.equals(pattern.getName())) {
//                return Collections.singletonList(new ValidationInfo(MyUtils.getMessage("cmd.add.error_name_exist"), cmdField));
//            }
//        }
        return super.doValidateAll();
    }

    @Override
    protected JComponent createCenterPanel() {
        // 第一个文本框和提示标签
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(cmdField, BorderLayout.CENTER);
        panel1.add(helpIcon, BorderLayout.EAST);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(remarkField, BorderLayout.CENTER);
        JLabel placeholder = new JLabel();
        placeholder.setPreferredSize(helpIcon.getPreferredSize());
        panel2.add(placeholder, BorderLayout.EAST);

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(IdeaMessageUtil.getMessage("cmd.dialog.label_name"), panel1)
                .addLabeledComponent(IdeaMessageUtil.getMessage("cmd.dialog.label_remark"), panel2)
                .getPanel();
    }
}
