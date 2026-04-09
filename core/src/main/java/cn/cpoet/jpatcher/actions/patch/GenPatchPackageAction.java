package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.FileEditorNavigatable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 生成补丁包（适用于增量发包的情况）
 *
 * @author CPoet
 */
public class GenPatchPackageAction extends AnAction {

    public GenPatchPackageAction() {
        super(I18nUtil.td("actions.patch.GenPatchPackageAction.title"), I18nUtil.td("actions.patch.GenPatchPackageAction.description"), null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        Set<String> selectedItems = getSelectedItems(e);
        DialogBuilder dialogBuilder = new DialogBuilder(project);
        dialogBuilder.setTitle(I18nUtil.t("actions.patch.GenPatchPackageAction.title"));
        GenPatchPanel packagePanel = new GenPatchPanel(project, selectedItems, dialogBuilder.getDialogWrapper());
        dialogBuilder.setCenterPanel(packagePanel);
        dialogBuilder.addOkAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.generate"));
        dialogBuilder.setOkOperation(packagePanel::generate);
        dialogBuilder.addCancelAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.cancel"));
        dialogBuilder.showNotModal();
    }

    private Set<String> getSelectedItems(AnActionEvent e) {
        Navigatable[] navigatables = e.getData(CommonDataKeys.NAVIGATABLE_ARRAY);
        if (navigatables == null || navigatables.length == 0) {
            return Collections.emptySet();
        }
        Set<String> selectedItems = new HashSet<>(navigatables.length);
        for (Navigatable navigatable : navigatables) {
            if (navigatable instanceof FileEditorNavigatable) {
                selectedItems.add(((FileEditorNavigatable) navigatable).getFile().getPath());
            } else if (navigatable instanceof NamedLibraryElementNode node) {
                node.getChildren().forEach(child -> {
                    VirtualFile file = ((ProjectViewNode<?>) child).getVirtualFile();
                    if (file != null) {
                        selectedItems.add(file.getPath());
                    }
                });
            } else if (navigatable instanceof ProjectViewNode) {
                VirtualFile file = ((ProjectViewNode<?>) navigatable).getVirtualFile();
                if (file != null) {
                    selectedItems.add(file.getPath());
                }
            }
        }
        return selectedItems;
    }
}
