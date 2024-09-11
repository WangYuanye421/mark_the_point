package com.wangyuanye.plugin.core.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 标记
 *
 * @author wangyuanye
 * @since 2024/9/1
 **/
@Setter
@Getter
@Data
public class MarkPointHead implements Serializable, Cloneable {

    private Long id = System.nanoTime();
    private String showName;// 列表显示的名称
    private String classPath; //源码文件全路径
    private String linesFileName;//存储line数据的文件名

    public MarkPointHead(String showName, String classPath, String linesFileName) {
        this.showName = showName;
        this.classPath = classPath;
        this.linesFileName = linesFileName;
    }

    public Boolean matchClassPath(String classPath) {
        return this.classPath.equals(classPath);
    }

    @Override
    public MarkPointHead clone() {
        try {
            MarkPointHead myCmd = (MarkPointHead) super.clone();
            myCmd.setShowName(this.getShowName());
            myCmd.setId(this.getId());
            myCmd.setClassPath(this.getClassPath());
            myCmd.setLinesFileName(this.getLinesFileName());
            return myCmd;
        } catch (CloneNotSupportedException e) {

            return null;
        }
    }

}
