package com.wangyuanye.plugin.idea;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.util.IdeaMessageUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class DialogHead extends DialogWrapper {
    private final MarkPointHead myMarkPointHead;
    private final JBTextField nameField;
    private final int mySchemaIndex;
    private final List<MarkPointHead> myExistingList;

    DialogHead(Component parent, MarkPointHead markPointHead, int index, List<MarkPointHead> existingList) {
        super(parent, true);
        setSize(300, 120);
        mySchemaIndex = index;
        myExistingList = existingList;
        setTitle(IdeaMessageUtil.getMessage("schema.dialog.add.title"));
        setResizable(false);
        myMarkPointHead = markPointHead;
        nameField = new JBTextField(myMarkPointHead.getShowName());
        init();
    }

    // 打开dialog后，光标聚焦组件
    @Override
    public JComponent getPreferredFocusedComponent() {
        return nameField;
    }

    @Override
    protected void doOKAction() {
        myMarkPointHead.setShowName(nameField.getText().trim());
        super.doOKAction();
    }

    // 不做校验
    @NotNull
    @Override
    protected List<ValidationInfo> doValidateAll() {

        return super.doValidateAll();
    }

    @Override
    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(IdeaMessageUtil.getMessage("schema.dialog.label_name"), nameField)
                .getPanel();
    }
}
