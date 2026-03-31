package cn.cpoet.jpatcher.impl223.component;

import cn.cpoet.jpatcher.component.FilterCheckboxTree;
import cn.cpoet.jpatcher.component.FilterCheckboxTree9;
import cn.cpoet.jpatcher.model.TreeNodeInfo;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.TreeSpeedSearch;

/**
 * @author CPoet
 */
public class FilterCheckBoxTree9Impl implements FilterCheckboxTree9 {
    @Override
    public void installSpeedSearch(FilterCheckboxTree filterCheckboxTree) {
        // Deprecated constructor in 2023.2.8
        new TreeSpeedSearch(filterCheckboxTree, true, treePath -> {
            CheckedTreeNode treeNode = (CheckedTreeNode) treePath.getLastPathComponent();
            return treeNode.getUserObject() instanceof TreeNodeInfo
                    ? ((TreeNodeInfo) treeNode.getUserObject()).getName()
                    : treeNode.toString();
        });
    }
}
