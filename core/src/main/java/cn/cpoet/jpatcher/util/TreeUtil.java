package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.component.FilterCheckedTreeNode;
import cn.cpoet.jpatcher.constant.CommonConst;
import cn.cpoet.jpatcher.model.TreeNodeInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ArrayUtil;
import org.apache.commons.collections.CollectionUtils;

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
        List<VirtualFile> dependJars = DependUtil.getDependJars(modules);
        if (!CollectionUtils.isEmpty(dependJars)) {
            T librariesNode = createLibrariesNode(func);
            for (VirtualFile dependJar : dependJars) {
                T dependJarNode = buildWithFile(dependJar, func);
                if (dependJarNode != null) {
                    addTreeNodeChild(librariesNode, dependJarNode);
                }
                if (librariesNode.getChildCount() > 0) {
                    addTreeNodeChild(projectNode, librariesNode);
                }
            }
        }
        return projectNode;
    }

    public static <T extends DefaultMutableTreeNode> T buildWithModule(Module module, Function<Object, T> func) {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots();
        // 模块源文件
        T moduleNode = null;
        if (sourceRoots.length != 0) {
            moduleNode = createModuleNode(module, func);
            for (VirtualFile sourceRoot : sourceRoots) {
                T fileNode = buildWithFile(sourceRoot, func);
                if (fileNode != null) {
                    addTreeNodeChild(moduleNode, fileNode);
                }
            }
        }
        return moduleNode == null || moduleNode.getChildCount() == 0 ? null : moduleNode;
    }

    private static <T extends DefaultMutableTreeNode> T createModuleNode(Module module, Function<Object, T> func) {
        T moduleNode = func.apply(module);
        moduleNode.setUserObject(new TreeNodeInfo(module.getName(), module));
        return moduleNode;
    }

    private static <T extends DefaultMutableTreeNode> T createLibrariesNode(Function<Object, T> func) {
        T libNode = func.apply(null);
        libNode.setUserObject(new TreeNodeInfo(CommonConst.LIBRARIES_NAME, CommonConst.LIBRARIES_NAME));
        return libNode;
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
        // 目录只有一个子节点时进行合并显示，排除依赖的jar包
        if (fileNode.getChildCount() == 1 && !file.getName().endsWith(CommonConst.FILE_EXT_JAR_FULL)) {
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
                if (filter == null || filter.accept((T) userObject)) {
                    collects.add((T) userObject);
                }
            }
            return;
        }
        // jar包且子节点全部选中的情况下，直接返回jar包
        if (node instanceof FilterCheckedTreeNode filterNode && node.getUserObject() instanceof TreeNodeInfo info) {
            if (info.getName().endsWith(CommonConst.FILE_EXT_JAR_FULL) && node.getChildCount() == filterNode.getCheckdChildCount()) {
                collects.add((T) info);
                return;
            }
        }
        for (int i = 0; i < node.getChildCount(); ++i) {
            TreeNode child = node.getChildAt(i);
            if (child instanceof CheckedTreeNode) {
                collectCheckedNodes(nodeType, filter, (CheckedTreeNode) child, collects);
            }
        }
    }
}
