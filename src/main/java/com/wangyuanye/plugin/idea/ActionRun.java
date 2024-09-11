package com.wangyuanye.plugin.idea;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.util.IdeaBaseUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class ActionRun extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionRun.class);
    private JBTable commandTable;

    public ActionRun() {
        super("未知", "未知", AllIcons.Actions.Execute);
    }

    public ActionRun(JBTable commandTable) {
        super("未知", "未知", AllIcons.Actions.Execute);
        this.commandTable = commandTable;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (commandTable == null) {
            e.getPresentation().setEnabled(false);
        } else {
            e.getPresentation().setEnabled(commandTable.getSelectedRow() != -1);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = IdeaBaseUtil.getCurProject();
        System.out.println("action run");
    }


}

