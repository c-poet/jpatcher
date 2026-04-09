package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.component.FilterCheckboxTree;
import cn.cpoet.jpatcher.component.FilterCheckedTreeNode;
import cn.cpoet.jpatcher.constant.CommonConst;
import cn.cpoet.jpatcher.model.TreeNodeInfo;
import cn.cpoet.jpatcher.util.TreeUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.ColoredTreeCellRenderer;

import javax.swing.*;
import java.util.Set;

/**
 * 生成补丁树形
 *
 * @author CPoet
 */
public class GenPatchTree extends FilterCheckboxTree {

    public GenPatchTree(Project project, Set<String> selectedItems) {
        super(new GenPatchPackageTreeCellRenderer(), TreeUtil.buildWithProject(project, (obj) -> {
            FilterCheckedTreeNode checkedTreeNode = new FilterCheckedTreeNode();
            checkedTreeNode.setChecked(obj instanceof VirtualFile && selectedItems.contains(((VirtualFile) obj).getPath()));
            return checkedTreeNode;
        }));
    }

    private static class GenPatchPackageTreeCellRenderer extends CheckboxTreeCellRenderer {
        @Override
        public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            TreeNodeInfo nodeInfo = (TreeNodeInfo) ((CheckedTreeNode) value).getUserObject();
            ColoredTreeCellRenderer textRenderer = getTextRenderer();
            if (nodeInfo.getObject() instanceof Module) {
                textRenderer.setIcon(AllIcons.Nodes.Module);
            } else if (nodeInfo.getObject() instanceof VirtualFile file) {
                if (file.getName().endsWith(".jar")) {
                    textRenderer.setIcon(AllIcons.Nodes.PpJar);
                } else if (file.isDirectory()) {
                    textRenderer.setIcon(AllIcons.Nodes.Folder);
                } else {
                    textRenderer.setIcon(file.getFileType().getIcon());
                }
            } else if (CommonConst.LIBRARIES_NAME.equals(nodeInfo.getObject())) {
                textRenderer.setIcon(AllIcons.Nodes.PpLibFolder);
            }
            textRenderer.append(nodeInfo.getName());
        }
    }
}
