package com.wangyuanye.plugin.core.service;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONUtil;
import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * @author yuanye.wang
 * @since
 **/
public interface MyMarkerService {

    /**
     * 获取指定文件的markHead
     *
     * @param classPath 源码文件路径
     * @return markHead
     */
    @Nullable
    MarkPointHead getMarkPointHead(@NotNull String classPath);

    /**
     * 查询所有markHead
     *
     * @return list
     */
    @NotNull
    List<MarkPointHead> getMarkHeads();

    /**
     * 锁定head
     * @param id
     * @param projectName
     */
    void lockMarkPointHead(@NotNull Long id, @NotNull String projectName);

    /**
     * 更新showName
     *
     * @param id       markHead id
     * @param showName 列表展示的名称
     */
    void updateMarkPointHead(@NotNull Long id, @NotNull String showName);

    void removeMarkPointHead(@NotNull Long id);

    /**
     * 获取文件的所有marker
     *
     * @param classPath 源文件路径
     * @return list
     */
    @NotNull
    List<MarkPointLine> getMarkLines(@NotNull String classPath);

    /**
     * 获取指定marker
     * 如果光标落在某个marker的选区范围内,则获取
     *
     * @param classPath 源文件路径
     * @param lineNum   行号
     * @return markline
     */
    @Nullable
    MarkPointLine getMarkLine(@NotNull String classPath, int lineNum);

    /**
     * 保存markline
     *
     * @param line markline
     */
    void saveMarkLine(@NotNull MarkPointLine line);


    /**
     * 删除markline
     *
     * @param classPath classPath
     * @param id        markline id
     */
    void removeMarkLine(@NotNull String classPath, @NotNull Long id);

    /**
     * 保存head,覆盖原文件
     *
     * @param headList headList
     */
    void updateHeadFile(List<MarkPointHead> headList) ;

    /**
     * 保存line,覆盖原文件
     *
     * @param filePath line路径
     * @param lineList data
     */
    void updateLineFile(String filePath, List<MarkPointLine> lineList) ;
}
