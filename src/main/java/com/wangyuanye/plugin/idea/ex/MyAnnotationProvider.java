package com.wangyuanye.plugin.idea.ex;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author yuanye.wang
 * @since v1.0.0
 **/
public class MyAnnotationProvider implements Annotator {
    private static final Icon ICON = new ImageIcon("path/to/icon.png");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // 判断是否为要标记的元素
        if (isTargetElement(element)) {
            Annotation annotation = holder.createInfoAnnotation(element, "Custom Icon");
            annotation.setGutterIconRenderer(new GutterIconRenderer() {
                @Override
                public boolean isDumbAware() {
                    return super.isDumbAware();
                }

                @Override
                public Icon getIcon() {
                    return ICON;
                }

                @Override
                public AnAction getClickAction() {
                    return new AnAction() {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent e) {
                            // 图标点击的行为
                        }
                    };
                }

                @Override
                public boolean equals(Object obj) {
                    return false;
                }

                @Override
                public int hashCode() {
                    return 0;
                }
            });
        }
    }

    private boolean isTargetElement(PsiElement element) {
        // 判断是否是目标元素
        return true; // 替换为实际的条件
    }
}
