package com.wangyuanye.plugin.core.service;

import com.wangyuanye.plugin.core.model.MarkPointHead;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author yuanye.wang
 * @since
 **/
public interface MyMarkerService {

    /**
     * 获取指定文件的markHead
     *
     * @param filePath 文件路径
     * @return markHead
     */
    @Nullable
    MarkPointHead getMarkPointHead(@NotNull String filePath);

    /**
     * 查询所有markHead
     *
     * @return list
     */
    @NotNull
    List<MarkPointHead> getMarkHeads();

    /**
     * 更新showName
     *
     * @param id       markHead id
     * @param showName 列表展示的名称
     */
    void updateMarkPointHead(@NotNull String id, @NotNull String showName);

    /**
     * 获取文件的所有marker
     *
     * @param filePath 文件路径
     * @return list
     */
    @NotNull
    List<MarkPointLine> getMarkLines(@NotNull String filePath);

    /**
     * 获取指定marker
     * 如果光标落在某个marker的选区范围内,则获取
     *
     * @param filePath    编辑器打开的文件路径
     * @param caretLine   光标行
     * @param caretColumn 光标列
     * @return markline
     */
    @NotNull
    MarkPointLine getMarkLine(@NotNull String filePath, int caretLine, int caretColumn);

    /**
     * 保存markline
     *
     * @param line markline
     */
    void addMarkLine(@NotNull MarkPointLine line);

    /**
     * 更新markline
     *
     * @param line markline
     */
    void updateMarkLine(@NotNull MarkPointLine line);

    /**
     * 删除markline
     *
     * @param id markline id
     */
    void removeMarkLine(@NotNull String id);

}
