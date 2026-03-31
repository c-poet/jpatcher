package cn.cpoet.jpatcher.compatible;

/**
 * @author CPoet
 */
public class CompatibleItem {
    /**
     * 兼容的类
     */
    private String source;

    /**
     * 实现的类
     */
    private String impl;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }
}
