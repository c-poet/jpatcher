package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.model.TreeNodeInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ArrayUtil;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 树形节点处理工具
 *
 * @author CPoet
 */
public abstract class TreeUtil {
    private TreeUtil() {
    }

    public static Module findModule(TreeNodeInfo nodeInfo) {
        while (nodeInfo != null && !(nodeInfo.getObject() instanceof Module)) {
            nodeInfo = nodeInfo.getParent();
        }
        return nodeInfo == null ? null : (Module) nodeInfo.getObject();
    }

    public static <T extends DefaultMutableTreeNode> T buildWithProject(Project project, Function<Object, T> func) {
        T projectNode = func.apply(project);
        projectNode.setUserObject(new TreeNodeInfo(project.getName(), project));
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            T moduleNode = buildWithModule(module, func);
            if (moduleNode != null) {
                addTreeNodeChild(projectNode, moduleNode);
            }
        }
        return projectNode;
    }

    public static <T extends DefaultMutableTreeNode> T buildWithModule(Module module, Function<Object, T> func) {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots();
        if (sourceRoots.length == 0) {
            return null;
        }
        T moduleNode = func.apply(module);
        moduleNode.setUserObject(new TreeNodeInfo(module.getName(), module));
        for (VirtualFile sourceRoot : sourceRoots) {
            T fileNode = buildWithFile(sourceRoot, func);
            if (fileNode != null) {
                addTreeNodeChild(moduleNode, fileNode);
            }
        }
        if (moduleNode.getChildCount() == 0) {
            return null;
        }
        return moduleNode;
    }

    @SuppressWarnings("all")
    public static <T extends DefaultMutableTreeNode> T buildWithFile(VirtualFile file, Function<Object, T> func) {
        // 文件直接返回节点
        if (!file.isDirectory()) {
            T fileNode = func.apply(file);
            fileNode.setUserObject(new TreeNodeInfo(file.getName(), file));
            fileNode.setAllowsChildren(false);
            return fileNode;
        }
        // 目录判断是否存在子节点
        VirtualFile[] children = file.getChildren();
        if (children.length == 0) {
            return null;
        }
        T fileNode = func.apply(file);
        fileNode.setUserObject(new TreeNodeInfo(file.getName(), file));
        for (VirtualFile child : children) {
            T childNode = buildWithFile(child, func);
            if (childNode != null) {
                addTreeNodeChild(fileNode, childNode);
            }
        }
        if (fileNode.getChildCount() == 0) {
            return null;
        }
        if (fileNode.getChildCount() == 1) {
            T childNode = (T) fileNode.getChildAt(0);
            TreeNodeInfo nodeInfo = (TreeNodeInfo) childNode.getUserObject();
            if (((VirtualFile) nodeInfo.getObject()).isDirectory()) {
                nodeInfo.setName(file.getName() + FileUtil.UNIX_SEPARATOR + nodeInfo.getName());
                return childNode;
            }
        }
        return fileNode;
    }

    private static void addTreeNodeChild(DefaultMutableTreeNode patent, DefaultMutableTreeNode child) {
        Object parentObj = patent.getUserObject();
        Object childObj = child.getUserObject();
        if (parentObj instanceof TreeNodeInfo && childObj instanceof TreeNodeInfo) {
            ((TreeNodeInfo) childObj).setParent((TreeNodeInfo) parentObj);
        }
        patent.add(child);
        // 子级延续父级的选中状态
        if (patent instanceof CheckedTreeNode && child instanceof CheckedTreeNode && ((CheckedTreeNode) patent).isChecked()) {
            ((CheckedTreeNode) child).setChecked(true);
        }
    }

    /**
     * 获取当前满足条件并选中的节点
     *
     * @param nodeType 用户类型
     * @param filter   自定义过滤
     * @param node     树节点
     * @param <T>      用户类型
     * @return 选中的树节点
     */
    public static <T> T[] getCheckedNodes(Class<T> nodeType, final Tree.NodeFilter<? super T> filter, CheckedTreeNode node) {
        ArrayList<T> collects = new ArrayList<>();
        collectCheckedNodes(nodeType, filter, node, collects);
        T[] result = ArrayUtil.newArray(nodeType, collects.size());
        collects.toArray(result);
        return result;
    }

    /**
     * 收集满足要求的选中的树节点信息
     *
     * @param nodeType 用户类型
     * @param filter   自定义过滤
     * @param node     树节点
     * @param collects 收集列表
     * @param <T>      用户类型
     */
    @SuppressWarnings("unchecked")
    private static <T> void collectCheckedNodes(Class<T> nodeType,
                                                final Tree.NodeFilter<? super T> filter,
                                                CheckedTreeNode node,
                                                List<T> collects) {
        if (node.isLeaf()) {
            Object userObject = node.getUserObject();
            if (node.isChecked()
                    && userObject != null
                    && nodeType.isAssignableFrom(userObject.getClass())) {
                if (filter != null && !filter.accept((T) userObject)) {
                    return;
                }
                collects.add((T) userObject);
            }
        } else {
            for (int i = 0; i < node.getChildCount(); ++i) {
                TreeNode child = node.getChildAt(i);
                if (child instanceof CheckedTreeNode) {
                    collectCheckedNodes(nodeType, filter, (CheckedTreeNode) child, collects);
                }
            }
        }
    }
}
