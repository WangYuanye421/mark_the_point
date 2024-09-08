package com.wangyuanye.plugin.idea.ex;

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.psi.PsiElement;

/**
 * @author yuanye.wang
 * @since
 **/
public class MyCustomDocumentationPopup {
    public void showDocumentation(PsiElement element, Editor editor) {
        DocumentationComponent documentationComponent = new DocumentationComponent();
        documentationComponent.setText("<html><body><h1>Custom Documentation</h1><p>This is custom documentation for " + element.getText() + ".</p></body></html>");

        JBPopup popup = PopupFactory.getInstance().createComponentPopupBuilder(documentationComponent, documentationComponent)
                .setRequestFocus(true)
                .setResizable(true)
                .setTitle("Custom Documentation")
                .createPopup();

        popup.showInBestPositionFor(editor);
    }
}
