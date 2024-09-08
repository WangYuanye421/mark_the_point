package com.wangyuanye.plugin.idea;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MyUtils;
import com.wangyuanye.plugin.util.UiUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class ActionRun extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionRun.class);
    private JBTable commandTable;

    public ActionRun() {
        super(MyUtils.getMessage("cmd.run_btn.text"), MyUtils.getMessage("cmd.run_btn.desc"), AllIcons.Actions.Execute);
    }

    public ActionRun(JBTable commandTable) {
        super(MyUtils.getMessage("cmd.run_btn.text"), MyUtils.getMessage("cmd.run_btn.desc"), AllIcons.Actions.Execute);
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
        Project project = IdeaApiUtil.getProject();
        System.out.println("action run");
    }



}

