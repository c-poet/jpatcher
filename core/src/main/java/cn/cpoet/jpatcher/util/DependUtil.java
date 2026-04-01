package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.constant.CommonConst;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;

import java.util.*;

/**
 * 项目/模块依赖工具
 *
 * @author CPoet
 */
public abstract class DependUtil {
    private DependUtil() {
    }

    public static List<VirtualFile> getDependJars(Module[] modules) {
        Stack<Module> moduleStack = new Stack<>();
        for (Module module : modules) {
            moduleStack.push(module);
        }
        Set<Module> moduleSet = new HashSet<>();
        Set<VirtualFile> libFileSet = new HashSet<>();
        while (!moduleStack.isEmpty()) {
            Module module = moduleStack.pop();
            if (moduleSet.contains(module)) {
                continue;
            }
            moduleSet.add(module);
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            List<VirtualFile> dependJars = getDependJars(moduleRootManager);
            if (CollectionUtils.isEmpty(dependJars)) {
                continue;
            }
            libFileSet.addAll(dependJars);
        }
        return CollectionUtils.isEmpty(libFileSet) ? List.of() : List.copyOf(libFileSet);
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
        int index = path.indexOf(CommonConst.DEPEND_JAR_PATH_MARK);
        if (index == -1) {
            return path;
        }
        if (path.length() == index + CommonConst.DEPEND_JAR_PATH_MARK.length()) {
            return sourceFile.getNameWithoutExtension() + CommonConst.FILE_EXT_JAR_FULL;
        }
        return FilenameUtils.getName(path.substring(0, index)) + CommonConst.FILE_EXT_JAR_FULL + path.substring(index + CommonConst.DEPEND_JAR_NAME_MARK.length());
    }
}
