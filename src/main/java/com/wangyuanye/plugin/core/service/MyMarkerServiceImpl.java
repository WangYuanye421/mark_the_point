package com.wangyuanye.plugin.core.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.util.MyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author wangyuanye
 * @date 2024/9/1
 **/
public class MyMarkerServiceImpl implements MyMarkerService {
    private static final Logger logger = Logger.getInstance(MyMarkerServiceImpl.class);
    private static final String LOCATION = PathManager.getPluginsPath();
    /**
     * xxx/.marker_data
     */
    public static final String PLUGIN_DIR = LOCATION + File.separator + "mark_the_point";
    /**
     * xxx/.marker_data/head.json
     */
    private static final String MARK_HEAD_FILE = PLUGIN_DIR + File.separator + "head.json";// head数据

    public static final MyMarkerServiceImpl INSTANCE = MyMakerServiceFactory.getInstance();


    // Singleton
    public static class MyMakerServiceFactory {

        public static MyMarkerServiceImpl getInstance() {
            return new MyMarkerServiceImpl();
        }
    }

    private MyMarkerServiceImpl() {
        initDir();
    }


    private void initDir() {
        File dir = new File(PLUGIN_DIR);
        // 检查文件夹
        if (!FileUtil.exist(dir)) {
            FileUtil.mkdir(PLUGIN_DIR);
            FileUtil.touch(new File(MARK_HEAD_FILE));
        }
        if (!FileUtil.exist(MARK_HEAD_FILE)) {
            FileUtil.touch(new File(MARK_HEAD_FILE));
        }
    }


    @Override
    public @Nullable MarkPointHead getMarkPointHead(@NotNull String classPath) {
        List<MarkPointHead> markHeads = getMarkHeads();
        if (markHeads.isEmpty()) return null;
        for (MarkPointHead markHead : markHeads) {
            if (classPath.equals(markHead.getClassPath())) {
                return markHead;
            }
        }
        return null;
    }

    @Override
    public @NotNull List<MarkPointHead> getMarkHeads() {
        FileReader fileReader = new FileReader(new File(MARK_HEAD_FILE));
        String fileString = fileReader.readString();
        if (fileString.isEmpty()) {
            return new ArrayList<>();
        }
        JSONArray jsonArray = JSONUtil.parseArray(fileString);
        return JSONUtil.toList(jsonArray, MarkPointHead.class);
    }

    @Override
    public @NotNull List<MarkPointLine> getMarkLines(@NotNull String classPath) {
        String filePath = checkBeforeSaveLine(classPath);
        FileReader fileReader = new FileReader(new File(filePath));
        String fileString = fileReader.readString();
        if (fileString.isEmpty()) {
            return new ArrayList<>();
        }
        JSONArray jsonArray = JSONUtil.parseArray(fileString);
        return JSONUtil.toList(jsonArray, MarkPointLine.class);
    }

    @Override
    public void lockMarkPointHead(@NotNull Long id, @NotNull String projectName) {
        List<MarkPointHead> markHeads = getMarkHeads();
        if (!markHeads.isEmpty()) {
            markHeads.stream().filter(e -> e.getId().equals(id)).findFirst().ifPresent(e -> {
                e.setLockName(projectName);
            });
            updateHeadFile(markHeads);
        }
    }

    @Override
    public void updateMarkPointHead(@NotNull Long id, @NotNull String showName) {
        List<MarkPointHead> markHeads = getMarkHeads();
        if (!markHeads.isEmpty()) {
            markHeads.stream().filter(e -> e.getId().equals(id)).findFirst().ifPresent(e -> {
                e.setShowName(showName);
            });
            updateHeadFile(markHeads);
        }
    }

    @Override
    public void removeMarkPointHead(@NotNull Long id) {
        List<MarkPointHead> markHeads = getMarkHeads();
        if (!markHeads.isEmpty()) {
            markHeads.removeIf(markPointHead -> id.equals(markPointHead.getId()));
            updateHeadFile(markHeads);
        }
    }

    @Override
    public @Nullable MarkPointLine getMarkLine(@NotNull String classPath, int lineNum) {
        List<MarkPointLine> markLines = getMarkLines(classPath);
        for (MarkPointLine markLine : markLines) {
            if (lineNum == markLine.getStartLine()) {
                return markLine;
            }
        }
        return null;
    }

    @Override
    public void saveMarkLine(@NotNull MarkPointLine line) {
        logger.info("save line : " + line);
        // 文件路径
        String lineFilePath = checkBeforeSaveLine(line.getClassPath());
        logger.info("lineFilePath : " + lineFilePath);
        // 获取该文件所有marker
        List<MarkPointLine> markLineList = getMarkLines(line.getClassPath());
        // 保存
        if (!line.isAdd()) {
            // 移除旧的
            Optional<MarkPointLine> first = markLineList.stream().filter(e -> line.getLineId().equals(e.getLineId())).findFirst();
            if (first.isPresent()) {
                MarkPointLine exist = first.get();
                markLineList.remove(exist);
            }
        } else {
            line.setLineId(System.nanoTime());
        }
        markLineList.add(line);
        updateLineFile(lineFilePath, markLineList);
    }

    @Override
    public void removeMarkLine(@NotNull String classPath, @NotNull Long id) {
        String lineFilePath = checkBeforeSaveLine(classPath);
        List<MarkPointLine> markLineList = getMarkLines(classPath);
        Optional<MarkPointLine> first = markLineList.stream().filter(e -> id.equals(e.getLineId())).findFirst();
        if (first.isPresent()) {
            MarkPointLine exist = first.get();
            markLineList.remove(exist);
        }
        updateLineFile(lineFilePath, markLineList);
    }

    /**
     * 校验
     *
     * @param classPath 标记文件的全路径
     * @return markLine文件路径
     */
    private String checkBeforeSaveLine(String classPath) {
        // 路径转名称
        String filename = MyUtils.genFilenameFromPath(classPath);
        // line文件是否存在
        String filePath = PLUGIN_DIR + File.separator + filename;
        if (!FileUtil.exist(filePath)) {
            FileUtil.touch(filePath);
        }
        // 检查head中是否存在
        List<MarkPointHead> markHeads = getMarkHeads();
        Optional<MarkPointHead> first = markHeads.stream().filter(e -> e.getClassPath().equals(classPath)).findFirst();
        if (first.isEmpty()) {
            MarkPointHead markPointHead = new MarkPointHead(classPath, classPath, filePath);
            logger.info("add head : " + markPointHead.toString());
            markHeads.add(markPointHead);
            updateHeadFile(markHeads);
        }
        return filePath;
    }

    /**
     * 保存head,覆盖原文件
     *
     * @param headList headList
     */
    public void updateHeadFile(List<MarkPointHead> headList) {
        FileWriter fileWriter = new FileWriter(new File(MARK_HEAD_FILE));
        String jsonStr = JSONUtil.toJsonStr(headList);
        fileWriter.write(jsonStr);
    }

    /**
     * 保存line,覆盖原文件
     *
     * @param filePath line路径
     * @param lineList data
     */
    public void updateLineFile(String filePath, List<MarkPointLine> lineList) {
        FileWriter fileWriter = new FileWriter(new File(filePath));
        String jsonStr = JSONUtil.toJsonStr(lineList);
        fileWriter.write(jsonStr);
    }

}
