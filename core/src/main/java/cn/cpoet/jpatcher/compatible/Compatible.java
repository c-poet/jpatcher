package cn.cpoet.jpatcher.compatible;

import java.util.List;

/**
 * 兼容信息
 *
 * @author CPoet
 */
public class Compatible {

    /**
     * 指定所在包名
     */
    private String packageName;

    /**
     * 最小兼容版本
     */
    private String since;

    /**
     * 兼容实现列表
     */
    private List<CompatibleItem> items;


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public List<CompatibleItem> getItems() {
        return items;
    }

    public void setItems(List<CompatibleItem> items) {
        this.items = items;
    }
}
