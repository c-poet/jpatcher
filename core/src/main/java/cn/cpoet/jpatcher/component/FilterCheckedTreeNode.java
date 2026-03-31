package cn.cpoet.jpatcher.component;

import cn.cpoet.jpatcher.model.TreeNodeInfo;
import com.intellij.ui.CheckedTreeNode;

import javax.swing.tree.TreePath;

/**
 * @author CPoet
 */
public class FilterCheckedTreeNode extends CheckedTreeNode implements Cloneable {

    private static final long serialVersionUID = -3544383260950329368L;

    /** 原节点 */
    private FilterCheckedTreeNode originNode;

    /** 缓存TreePath */
    private TreePath treePath;

    public FilterCheckedTreeNode() {
    }

    public FilterCheckedTreeNode(TreeNodeInfo userObject) {
        super(userObject);
    }

    public FilterCheckedTreeNode getOriginNode() {
        return originNode;
    }

    public void setOriginNode(FilterCheckedTreeNode originNode) {
        this.originNode = originNode;
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (originNode != null) {
            originNode.setChecked(checked);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (originNode != null) {
            originNode.setEnabled(enabled);
        }
    }

    public TreePath getTreePath() {
        return treePath;
    }

    public void setTreePath(TreePath treePath) {
        this.treePath = treePath;
    }

    public TreePath getAndInitTreePath() {
        if (treePath == null) {
            treePath = new TreePath(getPath());
        }
        return treePath;
    }

    @Override
    public FilterCheckedTreeNode clone() {
        FilterCheckedTreeNode node = (FilterCheckedTreeNode) super.clone();
        node.originNode = null;
        node.treePath = null;
        return node;
    }


}
