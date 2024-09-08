package com.wangyuanye.plugin.core.model;

import com.intellij.openapi.editor.LogicalPosition;

import java.io.Serializable;

/**
 * @author wangyuanye
 * @date 2024/9/1
 **/
public class MarkPointLine implements Serializable {
    private  String classPath; // 标记的类全路径
    private  String markContent; // 标记的内容
    public  int startLine;
    public  int startColumn;
    public  boolean startLeansForward;
    public  int endLine;
    public  int endColumn;
    public  boolean endLeansForward;
    private  String note;// 标记备注
    private  String bgColor;
    private  String bgColorDark;

    public MarkPointLine(String classPath, String markContent, LogicalPosition start, LogicalPosition end, String note,
                         String bgColor, String bgColorDark) {
        this.classPath = classPath;
        this.markContent = markContent;
        this.startLine = start.line;
        this.startColumn = start.column;
        this.startLeansForward = start.leansForward;
        this.endLine = end.line;
        this.endColumn = end.column;
        this.endLeansForward = end.leansForward;
        this.note = note;
        this.bgColor = bgColor;
        this.bgColorDark = bgColorDark;
    }

    public MarkPointLine(String classPath, String markContent, int startLine, int startColumn, boolean startLeansForward,
                         int endLine, int endColumn, boolean endLeansForward, String note, String bgColor, String bgColorDark) {
        this.classPath = classPath;
        this.markContent = markContent;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.startLeansForward = startLeansForward;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.endLeansForward = endLeansForward;
        this.note = note;
        this.bgColor = bgColor;
        this.bgColorDark = bgColorDark;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getMarkContent() {
        return markContent;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public boolean isStartLeansForward() {
        return startLeansForward;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public boolean isEndLeansForward() {
        return endLeansForward;
    }

    public String getNote() {
        return note;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getBgColorDark() {
        return bgColorDark;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public void setMarkContent(String markContent) {
        this.markContent = markContent;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public void setStartLeansForward(boolean startLeansForward) {
        this.startLeansForward = startLeansForward;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    public void setEndLeansForward(boolean endLeansForward) {
        this.endLeansForward = endLeansForward;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setBgColorDark(String bgColorDark) {
        this.bgColorDark = bgColorDark;
    }

    @Override
    public String toString() {
        return "MyMarker{" +
                "classPath='" + classPath + '\'' +
                ", markContent='" + markContent + '\'' +
                ", startLine=" + startLine +
                ", startColumn=" + startColumn +
                ", startLeansForward=" + startLeansForward +
                ", endLine=" + endLine +
                ", endColumn=" + endColumn +
                ", endLeansForward=" + endLeansForward +
                ", note='" + note + '\'' +
                ", bgColor='" + bgColor + '\'' +
                ", bgColorDark='" + bgColorDark + '\'' +
                '}';
    }
}
