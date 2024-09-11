package com.wangyuanye.plugin.core.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
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
    private static final String LOCATION = "/Users/mars/code/plugin/mark_the_point";
    private static final String DIR = ".marker_data";
    /**
     * xxx/.marker_data
     */
    private static final String PLUGIN_DIR = LOCATION + File.separator + DIR;
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
    public @Nullable MarkPointHead getMarkPointHead(@NotNull String filePath) {
        List<MarkPointHead> markHeads = getMarkHeads();
        if (markHeads.isEmpty()) return null;
        for (MarkPointHead markHead : markHeads) {
            if(filePath.equals(markHead.getClassPath())){
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
    public @NotNull List<MarkPointLine> getMarkLines(@NotNull String fileName) {
        FileReader fileReader = new FileReader(new File(fileName));
        String fileString = fileReader.readString();
        if (fileString.isEmpty()) {
            return new ArrayList<>();
        }
        JSONArray jsonArray = JSONUtil.parseArray(fileString);
        return JSONUtil.toList(jsonArray, MarkPointLine.class);
    }

    @Override
    public void updateMarkPointHead(@NotNull String id, @NotNull String showName) {

    }

    @Override
    public @Nullable MarkPointLine getMarkLine(@NotNull String filePath, int caretLine, int caretColumn) {
        List<MarkPointLine> markLines = getMarkLines(filePath);
        for (MarkPointLine markLine : markLines) {
            int startLine = markLine.getStartLine();
            int endLine = markLine.getEndLine();
            int startColumn = markLine.getStartColumn();
            int endColumn = markLine.getEndColumn();
            if(caretLine >= startLine && caretLine <= endLine && caretColumn >= startColumn && caretColumn <= endColumn) {
                return markLine;
            }
        }
        return null;
    }

    @Override
    public void addMarkLine(@NotNull MarkPointLine line) {
        // 文件路径
        String lineFilePath = checkBeforeSaveLine(line.getClassPath());
        // 获取该文件所有marker
        List<MarkPointLine> markLineList = getMarkLines(lineFilePath);
        // 保存
        markLineList.add(line);
        logger.info("add mark line : " + line.toString());
        saveLine(lineFilePath, markLineList);
    }

    @Override
    public void updateMarkLine(@NotNull MarkPointLine line) {

    }

    @Override
    public void removeMarkLine(@NotNull String id) {

    }

    /**
     * 校验
     *
     * @param classPath 标记文件的全路径
     * @return markLine行数据文件path
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
            saveHead(markHeads);
        }
        return filePath;
    }

    /**
     * 保存head
     *
     * @param headList headList
     */
    private void saveHead(List<MarkPointHead> headList) {
        FileWriter fileWriter = new FileWriter(new File(MARK_HEAD_FILE));
        String jsonStr = JSONUtil.toJsonStr(headList);
        fileWriter.write(jsonStr);
    }

    /**
     * 保存line
     *
     * @param filePath line路径
     * @param lineList data
     */
    private void saveLine(String filePath, List<MarkPointLine> lineList) {
        FileWriter fileWriter = new FileWriter(new File(filePath));
        String jsonStr = JSONUtil.toJsonStr(lineList);
        fileWriter.write(jsonStr);
    }


}
