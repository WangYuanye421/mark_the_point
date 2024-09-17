package com.wangyuanye.plugin.idea;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.util.IdeaMessageUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 关闭tab
 *
 * @author wangyuanye
 * @date 2024/8/29
 **/
public class ActionCloseTab extends AnAction {
    private JBTabs jbTabs;

    public ActionCloseTab() {
        super("Close Tab", IdeaMessageUtil.getMessage("line.tab.close"), AllIcons.Actions.Rollback);
    }

    public ActionCloseTab(JBTabs jbTabs) {
        super("Close Tab", IdeaMessageUtil.getMessage("line.tab.close"), AllIcons.Actions.Rollback);
        this.jbTabs = jbTabs;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        TabInfo selectedTab = jbTabs.getSelectedInfo();
        if (selectedTab != null) {
            // 移除当前选中的 Tab
            jbTabs.removeTab(selectedTab);
        }

    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }
}
