package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.constant.CommonConst;
import cn.cpoet.jpatcher.constant.FileBuildTypeExtEnum;
import cn.cpoet.jpatcher.constant.OSExplorerConst;
import cn.cpoet.jpatcher.exception.JPatcherException;
import cn.cpoet.jpatcher.model.FileInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.util.OS;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 文件操作工具
 *
 * @author CPoet
 */
public abstract class FileUtil {

    /**
     * Unix路径分隔符
     */
    public final static String UNIX_SEPARATOR = "/";
    /**
     * Windows路径分隔符
     */
    public final static String WINDOWS_SEPARATOR = "\\";

    private FileUtil() {
    }

    /**
     * 获取文件编译后缀名
     *
     * @param filePath 文件路径
     * @return 后缀名
     */
    public static String getBuildExt(String filePath) {
        FileBuildTypeExtEnum buildExtEnum = getBuildExtEnum(filePath);
        return buildExtEnum == null ? null : buildExtEnum.getBuildExt();
    }

    /**
     * 获取文件编译后缀
     *
     * @param filePath 文件路径
     * @return 后缀
     */
    public static FileBuildTypeExtEnum getBuildExtEnum(String filePath) {
        return FileBuildTypeExtEnum.ofSourceExt(FilenameUtils.getExtension(filePath));
    }

    /**
     * 在资源管理器中打开目录
     *
     * @param path 目录路径
     */
    public static void openFolder(String path) {
        if (OS.isLinux()) {
            if (!OSUtil.execCommand(OSExplorerConst.LINUX_GNOME, path)
                    && !OSUtil.execCommand(OSExplorerConst.LINUX_NAUTILUS, path)) {
                OSUtil.execCommand(OSExplorerConst.LINUX_KDE, path);
            }
        } else if (OS.isMacOSX()) {
            OSUtil.execCommand(OSExplorerConst.MACOS, path);
        } else {
            OSUtil.execCommand(OSExplorerConst.WINDOWS, path);
        }
    }

    /**
     * 在资源管理器中选中文件
     *
     * @param file 文件
     */
    public static void selectFile(VirtualFile file) {
        String filePath = FilenameUtils.separatorsToSystem(file.getPath());
        selectFile(filePath);
    }

    /**
     * 在资源管理器中选中文件
     *
     * @param filePath 文件路径
     */
    public static void selectFile(String filePath) {
        if (OS.isLinux()) {
            OSUtil.execCommand(OSExplorerConst.LINUX_GNOME, filePath);
        } else if (OS.isMacOSX()) {
            OSUtil.execCommand(OSExplorerConst.MACOS, filePath);
        } else {
            OSUtil.execCommand(OSExplorerConst.WINDOWS, "/e,/select," + filePath);
        }
    }

    /**
     * 判断是否是子路径
     *
     * @param parent 父路径
     * @param child  子路径
     * @return 是否为子路径
     */
    public static boolean isFileChild(VirtualFile parent, VirtualFile child) {
        if (parent != null && child != null) {
            while (child != null && !parent.equals(child)) {
                child = child.getParent();
            }
        }
        return parent == null || child != null;
    }

    /**
     * 获取文件的输出路径
     *
     * @param sourcePath 源文件路径
     */
    public static String getOutputFilePath(String sourcePath) {
        String ext = getBuildExt(sourcePath);
        if (StringUtils.isNotEmpty(ext)) {
            sourcePath = FilenameUtils.removeExtension(sourcePath) + FilenameUtils.EXTENSION_SEPARATOR + ext;
        }
        return sourcePath;
    }

    /**
     * 获取文件的输出路径
     *
     * @param root 输出根目录
     * @param file 源文件
     * @return 输出路径
     */
    public static String getOutputFilePath(VirtualFile root, VirtualFile file) {
        String filePath = getRelativePath(root.getPath(), file.getPath());
        return getOutputFilePath(filePath);
    }

    /**
     * 获取相对路径
     *
     * @param rootPath 根路径
     * @param filePath 文件路径
     * @return 相对路径
     */
    public static String getRelativePath(String rootPath, String filePath) {
        return filePath.substring(rootPath.length());
    }

    /**
     * 获取虚拟文件实例
     *
     * @param filePath  文件路径
     * @param isRefresh 是否刷新
     * @return 虚拟文件
     */
    public static VirtualFile getVirtualFile(String filePath, boolean isRefresh) {
        if (isRefresh) {
            return VirtualFileManager.getInstance().refreshAndFindFileByUrl(filePath);
        }
        return VirtualFileManager.getInstance().findFileByUrl(filePath);
    }

    /**
     * 获取虚拟文件实例
     *
     * @param parent    父级
     * @param filePath  文件路径
     * @param isRefresh 是否刷新
     * @return 虚拟文件
     */
    public static VirtualFile getVirtualFile(VirtualFile parent, String filePath, boolean isRefresh) {
        if (parent == null) {
            return null;
        }
        if (!filePath.startsWith(UNIX_SEPARATOR)) {
            filePath = UNIX_SEPARATOR + filePath;
        }
        return getVirtualFile(parent.getUrl() + filePath, isRefresh);
    }

    /**
     * 移出路径开始的分隔符
     *
     * @param path 路径
     * @return 移出路径开始分隔符的路径
     */
    public static String removeStartSeparator(String path) {
        if (path != null && !path.isEmpty()) {
            if (path.startsWith(UNIX_SEPARATOR)) {
                return path.substring(UNIX_SEPARATOR.length());
            }
            if (path.startsWith(WINDOWS_SEPARATOR)) {
                return path.substring(WINDOWS_SEPARATOR.length());
            }
        }
        return path;
    }

    /**
     * 获取目录下满足条件的文件列表
     *
     * @param file   目录文件
     * @param filter 过滤
     * @return 文件列表
     */
    public static VirtualFile[] getChildren(VirtualFile file, Predicate<VirtualFile> filter) {
        if (file == null || !file.isDirectory()) {
            return new VirtualFile[0];
        }
        return Stream.of(file.getChildren())
                .filter(filter)
                .toArray(VirtualFile[]::new);
    }

    /**
     * 写入文件
     *
     * @param originFile 源文件
     * @param filePath   目标文件路径
     */
    public static void writeToFile(VirtualFile originFile, String filePath) {
        writeToFile(originFile, new File(filePath));
    }

    /**
     * 写入文件
     *
     * @param originFile 源文件
     * @param toFile     目标文件
     */
    public static void writeToFile(VirtualFile originFile, File toFile) {
        try (InputStream in = originFile.getInputStream()) {
            writeToFile(com.intellij.openapi.util.io.FileUtil.loadBytes(in), toFile);
        } catch (Exception e) {
            throw new JPatcherException("Read Or Write file fail", e);
        }
    }

    /**
     * 写入文件
     *
     * @param data     数据
     * @param filePath 文件路径
     */
    public static void writeToFile(byte[] data, String filePath) {
        writeToFile(data, new File(filePath));
    }

    /**
     * 写入文件
     *
     * @param data 数据
     * @param file 文件路径
     */
    public static void writeToFile(byte[] data, File file) {
        try {
            com.intellij.openapi.util.io.FileUtil.writeToFile(file, data);
        } catch (Exception e) {
            throw new JPatcherException("Write file fail", e);
        }
    }


    /**
     * 获取输出文件
     *
     * @param module     模块
     * @param sourceFile 源文件
     * @return 输出文件
     */
    public static VirtualFile getOutputFile(Module module, VirtualFile sourceFile) {
        return getFileInfo(module, sourceFile).getOutputFile();
    }

    /**
     * 获取输出文件
     *
     * @param module   模块
     * @param filePath 输出文件路径
     * @return 输出文件
     */
    public static VirtualFile getOutputFile(Module module, String filePath) {
        CompilerModuleExtension compilerModuleExtension = Objects.requireNonNull(CompilerModuleExtension.getInstance(module));
        VirtualFile outputFile = getVirtualFile(compilerModuleExtension.getCompilerOutputPath(), filePath, true);
        if (outputFile == null) {
            outputFile = getVirtualFile(compilerModuleExtension.getCompilerOutputPathForTests(), filePath, true);
        }
        return outputFile;
    }

    /**
     * 获取源文件
     *
     * @param module   模块
     * @param filePath 源文件路径
     * @return 源文件
     */
    public static VirtualFile getSourceFile(Module module, String filePath) {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
        for (VirtualFile sourceRoot : sourceRoots) {
            VirtualFile file = getVirtualFile(sourceRoot, filePath, true);
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    /**
     * 获取某个文件所在模块源文件根目录
     *
     * @param module    模块
     * @param childFile 文件
     * @return 源文件根目录
     */
    public static VirtualFile getSourceRootFile(Module module, VirtualFile childFile) {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots();
        for (VirtualFile sourceRoot : sourceRoots) {
            if (isFileChild(sourceRoot, childFile)) {
                return sourceRoot;
            }
        }
        return null;
    }

    /**
     * 获取文件信息
     * <p>依赖的外部文件</p>
     *
     * @param sourceFile 源文件
     * @return 文件信息
     */
    public static FileInfo getFileInfo(VirtualFile sourceFile) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSourceFile(sourceFile);
        if (DependUtil.isFromJar(sourceFile)) {
            String fromJarPath = DependUtil.getFromJarPath(sourceFile);
            if (fromJarPath.indexOf(CommonConst.FILE_EXT_JAR_FULL) == fromJarPath.length() - CommonConst.FILE_EXT_JAR.length() - 1) {
                String path = sourceFile.getPath().substring(0, sourceFile.getPath().indexOf(fromJarPath)) + fromJarPath;
                VirtualFile virtualFile = FileUtil.getVirtualFile(CommonConst.FS_PROTOCOL_FILE + path, false);
                fileInfo.setOutputFile(virtualFile);
            } else {
                fileInfo.setOutputFile(sourceFile);
            }
            fileInfo.setOutputRelativePath(fromJarPath);
        }
        return fileInfo;
    }

    /**
     * 获取文件信息
     *
     * @param module     模块
     * @param sourceFile 源文件
     * @return 文件信息
     */
    public static FileInfo getFileInfo(Module module, VirtualFile sourceFile) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSourceFile(sourceFile);
        VirtualFile sourceRootFile = getSourceRootFile(module, sourceFile);
        if (sourceRootFile != null) {
            String outputFilePath = getOutputFilePath(sourceRootFile, sourceFile);
            VirtualFile outputFile = getOutputFile(module, outputFilePath);
            fileInfo.setSourceRoot(sourceRootFile);
            if (outputFile != null) {
                fileInfo.setOutputFile(outputFile);
            } else if (getBuildExtEnum(sourceFile.getName()) == null) {
                // 非编译类型可以直接使用源文件作为输出
                fileInfo.setOutputFile(sourceFile);
            }
            fileInfo.setOutputRelativePath(outputFilePath);
        }
        return fileInfo;
    }

    /**
     * 创建父级目录
     *
     * @param file 文件
     */
    public static void mkdirParent(File file) {
        try {
            FileUtils.forceMkdirParent(file);
        } catch (Exception ignored) {
        }
    }

    /**
     * 创建父级目录
     *
     * @param filePath 文件路径
     */
    public static void mkdirParent(String filePath) {
        mkdirParent(new File(filePath));
    }
}
