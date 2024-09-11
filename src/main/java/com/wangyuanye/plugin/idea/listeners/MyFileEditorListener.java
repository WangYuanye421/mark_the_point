package com.wangyuanye.plugin.idea.listeners;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * 监听文件的打开
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
public class MyFileEditorListener implements FileEditorManagerListener {

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("fileOpened");
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("fileClosed");
    }
}
