package cn.cpoet.jpatcher.model;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * 文件信息
 *
 * @author CPoet
 */
public class FileInfo {
    /** 源文件所在路径 */
    private VirtualFile sourceRoot;

    /** 源文件 */
    private VirtualFile sourceFile;

    /** 输出文件 */
    private VirtualFile outputFile;

    /** 输出文件相对路径 */
    private String outputRelativePath;

    public VirtualFile getSourceRoot() {
        return sourceRoot;
    }

    public void setSourceRoot(VirtualFile sourceRoot) {
        this.sourceRoot = sourceRoot;
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

    public String getOutputRelativePath() {
        return outputRelativePath;
    }

    public void setOutputRelativePath(String outputRelativePath) {
        this.outputRelativePath = outputRelativePath;
    }
}
