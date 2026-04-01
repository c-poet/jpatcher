package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.constant.CommonConst;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目/模块依赖工具
 *
 * @author CPoet
 */
public abstract class DependUtil {
    private DependUtil() {
    }

    public static List<VirtualFile> getDependJars(ModuleRootManager rootManager) {
        OrderEntry[] orderEntries = rootManager.getOrderEntries();
        if (orderEntries.length == 0) {
            return List.of();
        }
        List<VirtualFile> jarFiles = null;
        for (OrderEntry orderEntry : orderEntries) {
            if (orderEntry instanceof LibraryOrderEntry libraryOrderEntry) {
                Library library = libraryOrderEntry.getLibrary();
                if (library == null) {
                    continue;
                }
                VirtualFile[] files = library.getFiles(OrderRootType.CLASSES);
                if (files.length == 0) {
                    continue;
                }
                if (jarFiles == null) {
                    jarFiles = new ArrayList<>();
                }
                jarFiles.addAll(Arrays.asList(files));
            }
        }
        return jarFiles == null ? List.of() : jarFiles;
    }

    public static boolean isFromJar(VirtualFile sourceFile) {
        return CommonConst.FILE_EXT_JAR.equals(sourceFile.getFileSystem().getProtocol());
    }

    public static String getFromJarPath(VirtualFile sourceFile) {
        String path = sourceFile.getPath();
        int index = path.indexOf(CommonConst.JAR_EXPAND_FLAG);
        if (index == -1) {
            return path;
        }
        if (path.length() == index + CommonConst.JAR_EXPAND_FLAG.length() + 1) {
            return sourceFile.getNameWithoutExtension() + CommonConst.FILE_EXT_FULL_JAR;
        }
        return path.substring(index + CommonConst.JAR_EXPAND_FLAG.length());
    }
}
