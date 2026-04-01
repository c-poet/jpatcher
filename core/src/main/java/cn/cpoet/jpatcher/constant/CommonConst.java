package cn.cpoet.jpatcher.constant;

/**
 * 插件常量
 *
 * @author CPoet
 */
public interface CommonConst {

    /**
     * 插件名称
     */
    String TOOL_NAME = "JPatcher";

    /**
     * 配置文件名称
     */
    String SETTING_FILE_NAME = "jpatcher.xml";

    /**
     * 依赖库名称
     */
    String LIBRARIES_NAME = "External Libraries";

    /**
     * jar包扩展
     */
    String FILE_EXT_JAR = "jar";

    /**
     * jar包扩展
     */
    String FILE_EXT_JAR_FULL = "." + FILE_EXT_JAR;

    /**
     * 类文件后缀
     */
    String FILE_EXT_CLASS_FULL = ".class";

    /**
     * 内部类名称分割符
     */
    String INNER_CLASS_NAME_SPE = "$";

    /**
     * 外部依赖jar包标识
     */
    String DEPEND_JAR_NAME_MARK = ".jar!";

    /**
     * 外部依赖jar包标识
     */
    String DEPEND_JAR_PATH_MARK = DEPEND_JAR_NAME_MARK + "/";

    /**
     * 文件系统协议
     */
    String FS_PROTOCOL_FILE = "file://";
}
