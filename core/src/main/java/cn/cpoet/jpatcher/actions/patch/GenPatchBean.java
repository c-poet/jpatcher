package cn.cpoet.jpatcher.actions.patch;

import java.util.LinkedList;
import java.util.List;

/**
 * 生成补丁信息
 *
 * @author CPoet
 */
public class GenPatchBean {
    /**
     * 标记生成是否失败
     */
    private boolean isFailed;

    /**
     * 补丁输出路径
     */
    private String outputFolder;

    /**
     * 补丁文件名
     */
    private String fileName;

    /**
     * 项目类型
     */
    private GenPatchProjectTypeEnum projectType;

    /**
     * 补丁说明内容
     */
    private StringBuilder desc = new StringBuilder();

    /**
     * 补丁内容
     */
    private List<GenPatchItemBean> items = new LinkedList<>();

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public GenPatchProjectTypeEnum getProjectType() {
        return projectType;
    }

    public void setProjectType(GenPatchProjectTypeEnum projectType) {
        this.projectType = projectType;
    }

    public StringBuilder getDesc() {
        return desc;
    }

    public void setDesc(StringBuilder desc) {
        this.desc = desc;
    }

    public List<GenPatchItemBean> getItems() {
        return items;
    }

    public void setItems(List<GenPatchItemBean> items) {
        this.items = items;
    }
}
