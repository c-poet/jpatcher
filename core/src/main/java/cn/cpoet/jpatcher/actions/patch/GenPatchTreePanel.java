package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.component.CustomComboBox;
import cn.cpoet.jpatcher.component.SimpleHPanel;
import cn.cpoet.jpatcher.component.TitledPanel;
import cn.cpoet.jpatcher.constant.CommonConst;
import cn.cpoet.jpatcher.model.TreeNodeInfo;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 生成补丁树面板
 *
 * @author CPoet
 */
public class GenPatchTreePanel extends JBSplitter {

    private final Project project;
    private final GenPatchTree tree;
    private EditorTextField patchDescEditor;

    public GenPatchTreePanel(Project project, Set<String> selectedItems) {
        super(true);
        this.project = project;
        tree = new GenPatchTree(project, selectedItems);
        GenPatchSetting setting = GenPatchSetting.getInstance(project);
        buildTreePanel(setting);
        buildDescriptionPanel();
    }

    private void buildTreePanel(GenPatchSetting setting) {
        GenPatchSetting.State state = setting.getState();
        BorderLayoutPanel treePanel = JBUI.Panels.simplePanel();
        BorderLayoutPanel toolbarBorderLayoutPanel = JBUI.Panels.simplePanel();
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("Project file tree toolbar", getTreeToolbarActionGroup(), true);
        actionToolbar.setTargetComponent(toolbarBorderLayoutPanel);
        toolbarBorderLayoutPanel.addToLeft(actionToolbar.getComponent());

        SimpleHPanel treeFilterPanel = new SimpleHPanel();
        treeFilterPanel.add(new JBLabel(I18nUtil.t("actions.patch.GenPatchPackageAction.treeFilterType.label")));
        CustomComboBox<GenPatchTreeFilterTypeEnum> treeFilterTypeComboBox = new CustomComboBox<>();
        for (GenPatchTreeFilterTypeEnum item : GenPatchTreeFilterTypeEnum.values()) {
            treeFilterTypeComboBox.addItem(item);
        }
        treeFilterTypeComboBox.setSelectedItem(state.treeFilterType);
        treeFilterTypeComboBox.customText(GenPatchTreeFilterTypeEnum::getTitle);
        treeFilterTypeComboBox.setFocusable(false);
        treeFilterTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                doFilterTree((GenPatchTreeFilterTypeEnum) e.getItem());
                setting.getState().treeFilterType = ((GenPatchTreeFilterTypeEnum) e.getItem()).getCode();
            }
        });
        treeFilterPanel.add(treeFilterTypeComboBox);
        toolbarBorderLayoutPanel.addToRight(treeFilterPanel);
        treePanel.addToTop(toolbarBorderLayoutPanel);
        JBScrollPane treeScrollPane = new JBScrollPane(tree);
        treePanel.addToCenter(treeScrollPane);
        setFirstComponent(treePanel);
    }

    private void doFilterTree(GenPatchTreeFilterTypeEnum filterType) {
        switch (filterType) {
            case EDITOR:
                doFilterTreeEditor();
                break;
            case CHANGE:
                doFilterTreeChange();
                break;
            case SELECTED:
                doFilterTreeSelected();
                break;
            case JAR:
                doFilterTreeJar();
                break;
            case PROJECT:
            default:
                tree.removeFilter();
        }
    }

    private void doFilterTreeEditor() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        tree.applyFilter((node -> {
            TreeNodeInfo nodeInfo = (TreeNodeInfo) node.getUserObject();
            if (nodeInfo.getObject() instanceof VirtualFile) {
                return fileEditorManager.isFileOpen((VirtualFile) nodeInfo.getObject());
            }
            return false;
        }));
    }

    private void doFilterTreeChange() {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        tree.applyFilter(node -> {
            TreeNodeInfo nodeInfo = (TreeNodeInfo) node.getUserObject();
            if (nodeInfo.getObject() instanceof VirtualFile) {
                Change change = changeListManager.getChange((VirtualFile) nodeInfo.getObject());
                return change != null;
            }
            return false;
        });
    }

    private void doFilterTreeSelected() {
        tree.applyFilter(CheckedTreeNode::isChecked);
    }

    private void doFilterTreeJar() {
        tree.applyFilter(node -> {
            if (node.getUserObject() instanceof TreeNodeInfo info) {
                return info.getName().endsWith(CommonConst.FILE_EXT_JAR_FULL);
            }
            return false;
        });
    }

    private ActionGroup getTreeToolbarActionGroup() {
        AnAction[] toolbarActions = new AnAction[]{
                new AnAction("Expand All", null, AllIcons.Actions.Expandall) {
                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        TreeUtil.expandAll(tree);
                    }
                },
                new AnAction("Collapse All", null, AllIcons.Actions.Collapseall) {
                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        TreeUtil.collapseAll(tree, -1);
                    }
                }
        };
        return new ActionGroup() {
            @Override
            public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
                return toolbarActions;
            }
        };
    }

    private void buildDescriptionPanel() {
        TitledPanel descTitledPanel = new TitledPanel(I18nUtil.t("actions.patch.GenPatchPackageAction.description.title"));
        patchDescEditor = new EditorTextField();
        patchDescEditor.setOneLineMode(false);
        descTitledPanel.add(patchDescEditor);
        setSecondComponent(descTitledPanel);
    }

    public String getPatchDesc() {
        return patchDescEditor.getText();
    }

    public GenPatchTree getTree() {
        return tree;
    }
}
