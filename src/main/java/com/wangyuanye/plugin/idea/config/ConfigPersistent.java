package com.wangyuanye.plugin.idea.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Tag;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 配置数据的持久化
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
@Service(Service.Level.APP)
@State(name = "config", storages = {@Storage("mark_the_point/config.xml")})
public final class ConfigPersistent implements PersistentStateComponent<ConfigPersistent.MTPConfig> {
    private MTPConfig state = new MTPConfig();

    /**
     * 配置对象
     */
    @Getter
    @Setter
    public static class MTPConfig {
        @Tag("path")
        private String path;
        @Tag("style")
        private Integer markStyle;
        @Tag("color1")
        private String noteRegularColor;
        @Tag("color2")
        private String noteDarkColor;
    }

    @Override
    public @Nullable ConfigPersistent.MTPConfig getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull MTPConfig state) {
        this.state = state;
    }
}


