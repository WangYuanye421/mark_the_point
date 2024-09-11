package com.wangyuanye.plugin.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 基础api工具类
 *
 * @author yuanye.wang
 * @since v1.0
 **/
public class IdeaBaseUtil {
    private static final String GROUP_ID;
    private static final String CONTENT_TITLE;
    private static final String CONFIG_DATA_PATH = "config";

    static {
        GROUP_ID = getPluginConfig("notice.group.id");
        CONTENT_TITLE = getPluginConfig("notice.content.title");
    }

    public static final Notification NOTIFICATION = new Notification(
            GROUP_ID, // 通知组ID
            CONTENT_TITLE,        // 通知标题
            NotificationType.INFORMATION // 通知类型 (INFORMATION, WARNING, ERROR)
    );

    /**
     * 获取日志
     *
     * @param cl class
     * @return Logger
     */
    public static Logger getLogger(@NotNull Class<?> cl) {
        return Logger.getInstance(cl);
    }

    /**
     * 获取通知对象
     *
     * @return Notification
     */
    public static Notification getNotification() {
        return NOTIFICATION;
    }

    /**
     * 获取插件配置
     *
     * @param key key
     * @return value
     */
    public static @NotNull String getPluginConfig(String key) {
        ResourceBundle BUNDLE_DATA = ResourceBundle.getBundle(CONFIG_DATA_PATH, Locale.getDefault());
        return BUNDLE_DATA.getString(key);
    }

    /**
     * 获取当前项目
     *
     * @return Project
     */
    public static @NotNull Project getCurProject() {
        // 获取当前焦点的 IDE 框架窗口
        IdeFrame ideFrame = IdeFocusManager.getGlobalInstance().getLastFocusedFrame();

        // 检查 ideFrame 是否为空，并返回关联的项目
        if (ideFrame != null) {
            Project project = ideFrame.getProject();
            if (project != null) {
                return project;
            } else {
                IdeaMessageUtil.myTipsI18n("project.not.found");
                throw new IllegalStateException("Can't get current project");
            }
        }
        IdeaMessageUtil.myTipsI18n("project.not.found");
        throw new IllegalStateException("Can't get current project");
    }

    /**
     * 获取消息总线
     *
     * @return MessageBus
     */
    public static @NotNull MessageBus getMessageBus() {
        return getCurProject().getMessageBus();
    }
}
