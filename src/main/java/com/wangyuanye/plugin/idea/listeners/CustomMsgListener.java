package com.wangyuanye.plugin.idea.listeners;

import com.intellij.util.messages.Topic;
import com.wangyuanye.plugin.core.model.MarkPointLine;

/**
 * 消息发布订阅
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
public interface CustomMsgListener {
    Topic<CustomMsgListener> TOPIC = Topic.create("MTP Topic", CustomMsgListener.class);

    // 监听器中定义的事件处理方法
    void onMessageReceived(MarkPointLine line);
}
