package com.wangyuanye.plugin.idea.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 插件配置类
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
public class MyPluginConfigurable implements SearchableConfigurable {
    private final ConfigPersistent configPersistent;
    MyConfig myConfig;


    public MyPluginConfigurable() {
        this.configPersistent = ApplicationManager.getApplication().getService(ConfigPersistent.class);
    }

    @Override
    public @NotNull @NonNls String getId() {
        return "com.wangyuanye.plugin.msc.config";
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Mark The Point";
    }

    @Override
    public @Nullable JComponent createComponent() {
        this.myConfig = new MyConfig(configPersistent);
        return myConfig.getPanel();
    }

    @Override
    public boolean isModified() {
        ConfigPersistent.MTPConfig state = configPersistent.getState();
        if (state != null) {
            // 获取note颜色配置
            Color regular = myConfig.getRegularPanel().getBackground();
            Color dark = myConfig.getDarkPanel().getBackground();
            String regularStr = MyUtils.color2String(regular);
            String darkStr = MyUtils.color2String(dark);
            if (!regularStr.equals(state.getNoteRegularColor()) || !darkStr.equals(state.getNoteDarkColor())) {
                return true;
            }
            // 获取风格配置
            Integer style = myConfig.getStyleConfig();
            return !style.equals(state.getMarkStyle());
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        ConfigPersistent.MTPConfig state = configPersistent.getState();
        if (state != null) {
            state.setMarkStyle(myConfig.getStyleConfig());
            state.setNoteRegularColor(MyUtils.color2String(myConfig.getRegularPanel().getBackground()));
            state.setNoteDarkColor(MyUtils.color2String(myConfig.getDarkPanel().getBackground()));
        }
    }

}
