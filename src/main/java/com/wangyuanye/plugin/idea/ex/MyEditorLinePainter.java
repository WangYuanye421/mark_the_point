package com.wangyuanye.plugin.idea.ex;

import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.core.service.MyMarkerServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

/**
 * abcdef
 * 行尾拓展
 */
public class MyEditorLinePainter extends EditorLinePainter {

    private final MyMarkerServiceImpl markerService;
    /**
     * filePath - map
     *      lineNum - marklineList
     */
    private final Map<String, Map<Integer, List<MarkPointLine>>> markPointLineMap = new HashMap<>(16);

    public MyEditorLinePainter() {
        super();
        this.markerService = MyMarkerServiceImpl.INSTANCE;

    }

    @Nullable
    @Override
    public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile virtualFile, int i) {

//        List<LineExtensionInfo> result = new ArrayList<>();
//        String filePath = virtualFile.getPath();
//        Map<Integer, List<MarkPointLine>> lineNumMap = markPointLineMap.get(filePath);
//        if (lineNumMap == null) {
//            // 通过文件查找mark
//            List<MarkPointLine> markLines = markerService.getMarkLines(filePath);
//            if(markLines.isEmpty()) {
//                markPointLineMap.put(filePath, new HashMap<>(0));
//                return null;
//            }
//            // 假如一行有多条marker
//            Map<Integer, List<MarkPointLine>> collect = markLines.stream()
//                    .collect(Collectors.groupingBy(MarkPointLine::getStartLine));
//            markPointLineMap.put(filePath, collect);
//        }
//        else if(lineNumMap.isEmpty()) {
//            return null;
//        }
//        else {
//            List<MarkPointLine> markPointLines = lineNumMap.get(i);
//
//        }
        return null;
    }

}
