package cn.cpoet.jpatcher.actions.patch;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.LinkedList;
import java.util.List;

/**
 * @author CPoet
 */
public class GenPatchItemBean {

    /**
     * 文件相对路径（不含文件名）
     */
    private String fullPath;

    /**
     * 文件所在的模块
     */
    private GenPatchModuleBean patchModule;

    /**
     * 源文件
     */
    private VirtualFile sourceFile;

    /**
     * 输出文件
     */
    private VirtualFile outputFile;

    /**
     * 输出文件名
     */
    private String outputFileName;

    /**
     * 附加输出文件：例如JAVA内部类
     */
    private List<VirtualFile> attachOutputFiles;

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public GenPatchModuleBean getPatchModule() {
        return patchModule;
    }

    public void setPatchModule(GenPatchModuleBean patchModule) {
        this.patchModule = patchModule;
    }

    public VirtualFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(VirtualFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public VirtualFile getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(VirtualFile outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public List<VirtualFile> getAttachOutputFiles() {
        return attachOutputFiles;
    }

    public List<VirtualFile> getAndInitAttachOutputFiles() {
        if (attachOutputFiles == null) {
            attachOutputFiles = new LinkedList<>();
        }
        return attachOutputFiles;
    }

    public void setAttachOutputFiles(List<VirtualFile> attachOutputFiles) {
        this.attachOutputFiles = attachOutputFiles;
    }
}
