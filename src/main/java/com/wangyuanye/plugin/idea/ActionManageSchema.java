package com.wangyuanye.plugin.idea;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 管理分类
 *
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class ActionManageSchema extends AnAction {
    private JBTabs jbTabs;
    private HeadTab headTab;

    public ActionManageSchema() {
        super(MyUtils.getMessage("schema.manage_btn.text"), MyUtils.getMessage("schema.manage_btn.desc"), AllIcons.Modules.EditFolder);
    }

    public ActionManageSchema(JBTabs jbTabs, HeadTab headTab) {
        super(MyUtils.getMessage("schema.manage_btn.text"), MyUtils.getMessage("schema.manage_btn.desc"), AllIcons.Modules.EditFolder);
        this.jbTabs = jbTabs;
        this.headTab = headTab;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TabInfo> tabs = jbTabs.getTabs();
        Optional<TabInfo> first = tabs.stream().filter(t -> HeadTab.TAB_NAME.equals(t.getText())).findFirst();
        if (first.isPresent()) {
            IdeaApiUtil.myTips(MyUtils.getMessage("schema.tab.opened"));
            return;
        }
        TabInfo tab = headTab.buildHeadTab(jbTabs);
        jbTabs.addTab(tab);
        jbTabs.select(tab, true);// 激活当前tab
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }
}
