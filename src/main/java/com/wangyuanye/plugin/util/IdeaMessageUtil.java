package com.wangyuanye.plugin.util;

import com.intellij.notification.Notification;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 消息工具类
 *
 * @author yuanye.wang
 * @since v1.0
 **/
public class IdeaMessageUtil {
    private static final String BUNDLE_MSG_PATH = "messages/messages";

    /**
     * 发送提示信息
     *
     * @param tip 提示信息
     */
    public static void myTips(String tip) {
        String tipTitle = IdeaBaseUtil.getPluginConfig("notice.content.title");
        Notification notification = IdeaBaseUtil.getNotification();
        notification.setContent(tipTitle + " " + tip);
        notification.notify(IdeaBaseUtil.getCurProject());
    }

    /**
     * 发送提示信息
     *
     * @param i18nKey 国际化key
     */
    public static void myTipsI18n(String i18nKey) {
        String tipTitle = IdeaBaseUtil.getPluginConfig("notice.content.title");
        Notification notification = IdeaBaseUtil.getNotification();
        notification.setContent(tipTitle + " " + getMessage(i18nKey));
        notification.notify(IdeaBaseUtil.getCurProject());
    }

    /**
     * 发送提示信息
     *
     * @param i18nKey 国际化key
     */
    public static void myTipsI18n(String i18nKey, String msg) {
        String tipTitle = IdeaBaseUtil.getPluginConfig("notice.content.title");
        Notification notification = IdeaBaseUtil.getNotification();
        notification.setContent(tipTitle + " " + getMessage(i18nKey) + msg);
        notification.notify(IdeaBaseUtil.getCurProject());
    }

    /**
     * 获取国际化数据
     *
     * @param key key
     * @return value
     */
    public static @NotNull String getMessage(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_MSG_PATH, Locale.getDefault());
        return bundle.getString(key);
    }
}
