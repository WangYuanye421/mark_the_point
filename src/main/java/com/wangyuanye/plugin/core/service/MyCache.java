package com.wangyuanye.plugin.core.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.util.IdeaBaseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 缓存
 *
 * @author yuanye.wang
 * @since v0.1
 **/
public class MyCache implements MyMarkerService {
    private static final Logger logger = IdeaBaseUtil.getLogger(MyCache.class);
    private final MyMarkerServiceImpl markerService;
    public static final MyCache CACHE_INSTANCE = MyCacheFactory.getInstance();
    /**
     * 最近最久未使用
     */
    private static final Cache<String, List<MarkPointLine>> cache = CacheUtil.newLRUCache(32);

    private MyCache(MyMarkerServiceImpl markerService) {
        this.markerService = markerService;
    }

    // Singleton
    public static class MyCacheFactory {
        public static MyCache getInstance() {
            return new MyCache(MyMarkerServiceImpl.INSTANCE);
        }
    }


    @Override
    public @Nullable MarkPointHead getMarkPointHead(@NotNull String filePath) {
        // todo 缓存
        return markerService.getMarkPointHead(filePath);
    }

    @Override
    public @NotNull List<MarkPointHead> getMarkHeads() {
        return markerService.getMarkHeads();
    }

    @Override
    public void updateMarkPointHead(@NotNull String id, @NotNull String showName) {
        markerService.updateMarkPointHead(id, showName);
    }

    @Override
    public @NotNull List<MarkPointLine> getMarkLines(@NotNull String filePath) {
        return markerService.getMarkLines(filePath);
    }

    @Override
    public @Nullable MarkPointLine getMarkLine(@NotNull String filePath, int caretLine, int caretColumn) {
        return markerService.getMarkLine(filePath, caretLine, caretColumn);
    }

    @Override
    public void addMarkLine(@NotNull MarkPointLine line) {
        markerService.addMarkLine(line);
    }

    @Override
    public void updateMarkLine(@NotNull MarkPointLine line) {
        markerService.updateMarkLine(line);
    }

    @Override
    public void removeMarkLine(@NotNull String id) {
        markerService.removeMarkLine(id);
    }


    public static void put(String key, List<MarkPointLine> markPointLines) {
        cache.put(key, markPointLines);
    }

    public static List<MarkPointLine> get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static void updateCache(String key){
        if (cache.containsKey(key)) {
            // todo 对于特定Key的全量更新
        }
    }
}
