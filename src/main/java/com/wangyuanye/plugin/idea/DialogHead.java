package com.wangyuanye.plugin.idea;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.util.IdeaMessageUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author wangyuanye
 * @since 2024/8/28
 **/
public class DialogHead extends DialogWrapper {
    private final MarkPointHead myMarkPointHead;
    private final JBTextField showNameField;

    DialogHead(Component parent, MarkPointHead markPointHead) {
        super(parent, true);
        setSize(300, 120);
        setTitle(IdeaMessageUtil.getMessage("dialog.title.edit.head"));
        setResizable(false);
        myMarkPointHead = markPointHead;
        showNameField = new JBTextField(myMarkPointHead.getShowName());
        init();
    }

    // 打开dialog后，光标聚焦组件
    @Override
    public JComponent getPreferredFocusedComponent() {
        return showNameField;
    }

    @Override
    protected void doOKAction() {
        myMarkPointHead.setShowName(showNameField.getText().trim());
        super.doOKAction();
    }


    @Override
    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(IdeaMessageUtil.getMessage("dialog.label.edit.head"), showNameField)
                .getPanel();
    }
}
