package cn.cpoet.jpatcher.model;

/**
 * 树形节点信息
 *
 * @author CPoet
 */
public class TreeNodeInfo {

    /** 节点显示名称 */
    private String name;

    /** 当前节点自定义对象 */
    private Object object;

    /** 当前节点的父级节点 */
    private TreeNodeInfo parent;

    public TreeNodeInfo() {
    }

    public TreeNodeInfo(String name, Object object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public TreeNodeInfo getParent() {
        return parent;
    }

    public void setParent(TreeNodeInfo parent) {
        this.parent = parent;
    }
}
