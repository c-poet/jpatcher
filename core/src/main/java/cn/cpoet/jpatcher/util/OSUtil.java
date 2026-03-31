package cn.cpoet.jpatcher.util;

/**
 * 系统工具
 *
 * @author CPoet
 */
public abstract class OSUtil {
    private OSUtil() {
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
