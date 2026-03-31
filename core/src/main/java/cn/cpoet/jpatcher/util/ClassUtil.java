package cn.cpoet.jpatcher.util;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.ClassUtils;

/**
 * 类处理工具
 *
 * @author CPoet
 */
public abstract class ClassUtil {

    public final static String CLASS_EXT = "class";

    private ClassUtil() {
    }

    /**
     * 获取类路径
     *
     * @param className 类全限定名称
     * @return 类路径
     */
    public static String convertNameToPath(String className) {
        return className.replaceAll("\\.", FileUtil.UNIX_SEPARATOR);
    }

    /**
     * 获取所有内部类输出的文件列表
     *
     * @param classFile 类输出文件
     * @return 内部类输出文件列表
     */
    public static VirtualFile[] getInnerOutputFiles(VirtualFile classFile) {
        if (classFile == null || !CLASS_EXT.equals(classFile.getExtension())) {
            return new VirtualFile[0];
        }
        String filePrefix = classFile.getNameWithoutExtension() + ClassUtils.INNER_CLASS_SEPARATOR;
        return FileUtil.getChildren(classFile.getParent(), file -> file.getName().startsWith(filePrefix));
    }

    /**
     * 尝试获取类，不存在返回NULL
     *
     * @param className 类名
     * @return 返回类
     */
    public static Class<?> tryGetClass(String className) {
        try {
            return ClassUtil.class.getClassLoader().loadClass(className);
        } catch (Exception ignored) {
        }
        return null;
    }
}
