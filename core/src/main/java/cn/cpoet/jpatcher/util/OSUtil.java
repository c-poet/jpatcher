package cn.cpoet.jpatcher.util;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

/**
 * 系统工具
 *
 * @author CPoet
 */
public abstract class OSUtil {
    private OSUtil() {
    }

    /**
     * 获取用户桌面路径
     *
     * @return 桌面路径，获取不到返回null
     */
    public static String getDesktopPath() {
        File desktop = new File(SystemUtils.getUserHome(), "Desktop");
        if (desktop.exists() && desktop.isDirectory()) {
            return desktop.getPath();
        }
        return null;
    }

    /**
     * 执行命令
     *
     * @param commandAndArgs 命令及参数
     */
    public static boolean execCommand(String... commandAndArgs) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(commandAndArgs);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
