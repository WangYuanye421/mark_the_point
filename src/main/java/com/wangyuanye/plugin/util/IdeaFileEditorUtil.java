package com.wangyuanye.plugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.awt.RelativePoint;
import com.wangyuanye.plugin.core.model.MarkPointLine;
import com.wangyuanye.plugin.idea.ex.MyCustomElementRenderer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * 文件和编辑器的工具类
 *
 * @author yuanye.wang
 * @since v1.0
 **/
public class IdeaFileEditorUtil {

    /**
     * 打开文件编辑器
     *
     * @param project   project
     * @param classPath 文件路径
     * @return VirtualFile
     */
    public static VirtualFile openFileEditor(Project project, String classPath) {
        FileEditorManager fileEditor = FileEditorManager.getInstance(project);
        VirtualFile virtualFile = JarFileSystem.getInstance().findFileByPath(classPath);
        if (virtualFile != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                fileEditor.openFile(virtualFile, true);
            });
        } else {
            String msg = "Source code File not found. param[classPath]: " + classPath;
            IdeaMessageUtil.myTips(msg);
            throw new RuntimeException(msg);
        }
        return virtualFile;
    }

    /**
     * 屏幕导航到指定位置
     *
     * @param editor   Editor
     * @param position LogicalPosition
     */
    public static void navigateToPosition(Editor editor, LogicalPosition position) {
        editor.getCaretModel().moveToLogicalPosition(position);// 设置光标位置
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);// 屏幕移动到光标点
    }

    public static void openAndNavigate(Project project, VirtualFile virtualFile, LogicalPosition position) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        boolean fileOpen = fileEditorManager.isFileOpen(virtualFile);
        if (fileOpen) {
            FileEditor selectedEditor = fileEditorManager.getSelectedEditor(virtualFile);
            if (selectedEditor instanceof TextEditor) {
                Editor editor = ((TextEditor) selectedEditor).getEditor();
                navigateToPosition(editor, position);
            }
        } else {
            // 打开文件
            ApplicationManager.getApplication().invokeLater(() -> {
                FileEditor[] fileEditors = fileEditorManager.openFile(virtualFile, true);// 不管文件是否已打开,都使用该方法打开文件或激活窗口
                for (FileEditor ele : fileEditors) {
                    if (ele instanceof TextEditor) {
                        if (virtualFile.equals(ele.getFile())) {
                            Editor editor = ((TextEditor) ele).getEditor();
                            navigateToPosition(editor, position);
                        }
                    }
                }
            });
        }
    }

    /**
     * 创建inlay行尾元素
     *
     * @param editor        editor
     * @param markPointLine markPointLine
     * @return Inlay
     */
    public static Inlay<MyCustomElementRenderer> buildInlayElement(Editor editor, MarkPointLine markPointLine) {
        InlayModel inlayModel = editor.getInlayModel();
        int lineEndOffset = editor.getDocument().getLineEndOffset(markPointLine.getStartLine());
        return inlayModel.addInlineElement(lineEndOffset, true, new MyCustomElementRenderer(markPointLine));
    }

    public static void addInlayListener(Editor editor, LogicalPosition position, String note, Color noteColor) {
        Inlay<?> previousInlay = null; // 记录上一次的 Inlay
        Point lastMousePosition = null; // 记录上次鼠标位置
        InlayModel inlayModel = editor.getInlayModel();
        editor.addEditorMouseMotionListener(new EditorMouseMotionListener() {
            @Override
            public void mouseMoved(@NotNull EditorMouseEvent e) {
                Inlay<?> inlay = e.getInlay();
                if (inlay != null) {
                    // 判断Inlay类型
                    EditorCustomElementRenderer renderer = inlay.getRenderer();
                    if (renderer instanceof MyCustomElementRenderer) {
                        // 在鼠标悬停时手动触发弹窗
                        showCustomPopup(e.getMouseEvent(), editor);
                    }
                }
            }
        });
    }

    private static void showCustomPopup(MouseEvent event, Editor editor) {
        JBPopup popup = JBPopupFactory.getInstance()
                .createMessage("This is a custom documentation popup.");
        popup.show(new RelativePoint(event));
    }
}
