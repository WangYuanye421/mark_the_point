package com.wangyuanye.plugin.idea.listeners;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.service.MyCache;
import com.wangyuanye.plugin.core.service.MyMarkerService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * wangyunaye
 */
public class MyProjectManagerListener implements ProjectManagerListener {
    private static final Logger logger = Logger.getInstance(MyProjectManagerListener.class);
    private final MyMarkerService myService;

    public MyProjectManagerListener() {
        this.myService = MyCache.CACHE_INSTANCE;
    }

    @Override
    public void projectClosingBeforeSave(@NotNull Project project) {
        logger.info("[Mark Source Code] MyProjectManagerListener");
        // 解锁
        String projectName = project.getName();
        List<MarkPointHead> markHeads = myService.getMarkHeads();
        for (MarkPointHead head : markHeads) {
            if (projectName.equals(head.getLockName())) {
                head.setLockName(null);
            }
        }
        myService.updateHeadFile(markHeads);
        logger.info("[Mark Source Code] " + projectName +" 解锁文件成功");
    }
}
