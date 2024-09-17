package com.wangyuanye.plugin.idea.toolWindow;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.idea.HeadTab;
import org.jetbrains.annotations.NotNull;

/**
 * 插件窗口
 *
 * @author wangyuanye
 * @since 2024/8/20
 **/
@Service
public final class PluginWindow implements Disposable {
    private static final Logger logger = Logger.getInstance(PluginWindow.class);
    public Project currentProject;
    private HeadTab headTab;

    public PluginWindow() {
        headTab = new HeadTab();
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow, @NotNull Project project) {
        this.currentProject = project;
        JBTabs jbTabs = JBTabsFactory.createTabs(project);
        TabInfo buildHeadTab = headTab.buildHeadTab(jbTabs, currentProject);
        jbTabs.addTab(buildHeadTab);


        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(null, null, false);
        content.setComponent(jbTabs.getComponent());
        ContentManager myContentManager = toolWindow.getContentManager();
        myContentManager.addContent(content);
    }


    public @NotNull Project getCurrentProject() {
        return this.currentProject;
    }

    @Override
    public void dispose() {
        headTab = null;
    }
}
