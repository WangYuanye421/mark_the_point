package com.wangyuanye.plugin.core.model;

import java.io.Serializable;

/**
 * 标记
 *
 * @author wangyuanye
 * @date 2024/9/1
 **/
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getLinesFileName() {
        return linesFileName;
    }

    public void setLinesFileName(String linesFileName) {
        this.linesFileName = linesFileName;
    }
}
