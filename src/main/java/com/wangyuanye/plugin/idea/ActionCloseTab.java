package com.wangyuanye.plugin.idea;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 关闭tab
 *
 * @author wangyuanye
 * @date 2024/8/29
 **/
public class ActionCloseTab extends AnAction {
    private JBTabs jbTabs;

    public ActionCloseTab() {
        super("Close Tab", MyUtils.getMessage("schema.tab.close"), AllIcons.Actions.Cancel);
    }

    public ActionCloseTab(JBTabs jbTabs) {
        super("Close Tab", MyUtils.getMessage("schema.tab.close"), AllIcons.Actions.Cancel);
        this.jbTabs = jbTabs;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        List<TabInfo> tabs = jbTabs.getTabs();
        Optional<TabInfo> first = tabs.stream().filter(t -> HeadTab.TAB_NAME.equals(t.getText())).findFirst();
        first.ifPresent(tabInfo -> jbTabs.removeTab(tabInfo));

    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }
}
