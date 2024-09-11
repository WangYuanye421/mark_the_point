package com.wangyuanye.plugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.idea.toolWindow.MyToolWindowFactory;
import com.wangyuanye.plugin.idea.toolWindow.PluginWindow;

import javax.swing.*;
import java.awt.*;


/**
 * 窗口工具类
 *
 * @author wangyuanye
 * @since v1.0
 **/
public class IdeaWindowUtil {

    public static PluginWindow getToolWindow() {
        return ApplicationManager.getApplication().getService(PluginWindow.class);
    }

    /**
     * 设置dialog和插件窗口的关联位置
     * dialog只会显示在插件窗口内
     *
     * @param dialog dialog
     */
    public static void setRelatedLocation(DialogWrapper dialog) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(IdeaBaseUtil.getCurProject())
                .getToolWindow(MyToolWindowFactory.myToolWindowId);
        if (toolWindow != null) {
            JComponent component = toolWindow.getComponent();
            Point toolWindowLocation = component.getLocationOnScreen();
            Dimension toolWindowSize = component.getSize();

            // 假设 dialog 是你的 DialogWrapper 对象
            Dimension dialogSize = dialog.getPreferredSize();

            int x = toolWindowLocation.x + (toolWindowSize.width - dialogSize.width) / 2;
            int y = toolWindowLocation.y + (toolWindowSize.height - dialogSize.height) / 2;

            dialog.setLocation(x, y);
        }
    }

    /**
     * 空表显示内容
     *
     * @param table table
     */
    public static void tableEmptyText(JBTable table) {
        String noData = IdeaMessageUtil.getMessage("no_data");
        table.getEmptyText().setText(noData);
    }


}
