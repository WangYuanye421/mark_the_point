package com.wangyuanye.plugin.idea.ex;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.model.Pointer;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 自定义快速文档窗口
 *
 * @author yuanye.wang
 * @since
 **/
public class MyDocumentationProvider implements DocumentationTarget {

    private final PsiElement element;

    public MyDocumentationProvider() {
        this.element = element;
    }

    @Override
    public @NotNull Pointer<? extends DocumentationTarget> createPointer() {
        return Pointer.hardPointer(this);
    }

    @Override
    public @NotNull TargetPresentation getTargetPresentation() {
        return TargetPresentation.builder(element.getText()).presentation();
    }

    @NotNull
    @Override
    public TargetPresentation computePresentation() {
        return null;
    }

    // 你可以添加其他自定义方法来支持文档的显示逻辑
}
