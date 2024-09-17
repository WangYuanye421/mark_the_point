package com.wangyuanye.plugin.core.model;

import com.intellij.openapi.editor.LogicalPosition;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wangyuanye
 * @since 2024/9/1
 **/
@Data
public class MarkPointLine implements Serializable {
    private Long lineId;
    private String classPath; // 标记的类全路径
    private String markContent; // 标记的内容
    public int startLine;
    public int startColumn;
    public boolean startLeansForward;
    public int endLine;
    public int endColumn;
    public boolean endLeansForward;
    private String note;// 标记备注
    private String regularColor;
    private String darkColor;

    public MarkPointLine(String classPath, String markContent, LogicalPosition start, LogicalPosition end, String note,
                         String regularColor, String darkColor) {
        this.classPath = classPath;
        this.markContent = markContent;
        this.startLine = start.line;
        this.startColumn = start.column;
        this.startLeansForward = start.leansForward;
        this.endLine = end.line;
        this.endColumn = end.column;
        this.endLeansForward = end.leansForward;
        this.note = note;
        this.regularColor = regularColor;
        this.darkColor = darkColor;
    }

    public MarkPointLine(String classPath, String markContent, int startLine, int startColumn, boolean startLeansForward,
                         int endLine, int endColumn, boolean endLeansForward, String note, String regularColor, String darkColor) {
        this.classPath = classPath;
        this.markContent = markContent;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.startLeansForward = startLeansForward;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.endLeansForward = endLeansForward;
        this.note = note;
        this.regularColor = regularColor;
        this.darkColor = darkColor;
    }

    public MarkPointLine(String fileName, String markerContent, LogicalPosition begin, LogicalPosition end) {
        this.classPath = fileName;
        this.markContent = markerContent;
        this.startLine = begin.line;
        this.startColumn = begin.column;
        this.startLeansForward = begin.leansForward;
        this.endLine = end.line;
        this.endColumn = end.column;
        this.endLeansForward = end.leansForward;
    }

    public boolean isAdd() {
        return lineId == null;
    }

    public void setBeginPosition(LogicalPosition begin) {
        this.startLine = begin.line;
        this.startColumn = begin.column;
        this.startLeansForward = begin.leansForward;
    }

    public void setEndPosition(LogicalPosition end) {
        this.endLine = end.line;
        this.endColumn = end.column;
        this.endLeansForward = end.leansForward;
    }

    public LogicalPosition getBeginPosition() {
        return new LogicalPosition(startLine, startColumn, startLeansForward);
    }

    public LogicalPosition getEndPosition() {
        return new LogicalPosition(endLine, endColumn, endLeansForward);
    }
}
