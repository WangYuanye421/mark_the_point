package com.wangyuanye.plugin.idea;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.util.IdeaFileEditorUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 打开新的tab
 *
 * @author wangyuanye
 **/
public class ActionOpenTab extends AnAction {
    private JBTabs jbTabs;
    private JBTable headTable;

    public ActionOpenTab() {
        super("详情", "详情", AllIcons.Modules.EditFolder);
    }

    public ActionOpenTab(JBTabs jbTabs, JBTable headTable) {
        super("详情", "详情", AllIcons.Modules.EditFolder);
        this.jbTabs = jbTabs;
        this.headTable = headTable;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TabInfo> tabs = jbTabs.getTabs();
//        Optional<TabInfo> first = tabs.stream().filter(t -> HeadTab.TAB_NAME.equals(t.getText())).findFirst();
//        if (first.isPresent()) {
//            IdeaApiUtil.myTips(MyUtils.getMessage("schema.tab.opened"));
//            return;
//        }
        // 获取选中的Head
        int selectedRow = headTable.getSelectedRow();
        HeadModel model = (HeadModel) headTable.getModel();
        MarkPointHead selectHead = (MarkPointHead) model.getRowData(selectedRow);
        // 打开源码文件
        VirtualFile virtualFile = IdeaFileEditorUtil.openFileEditor(e.getProject(), selectHead.getClassPath());
        // 创建新的tab
        LineTab lineTab = new LineTab(selectHead, e.getProject(), virtualFile);
        TabInfo tab = lineTab.buildLineTab();
        jbTabs.addTab(tab);
        jbTabs.select(tab, true);// 激活当前tab
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }
}
