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
    String LIBRARIES_NAME = "Libraries";

    /**
     * jar包扩展
     */
    String FILE_EXT_JAR = "jar";

    /**
     * jar包扩展
     */
    String FILE_EXT_FULL_JAR = "." + FILE_EXT_JAR;


    /**
     * 依赖jar包展开标识
     */
    String JAR_EXPAND_FLAG = ".jar!";

    /**
     * 文件系统协议
     */
    String FS_PROTOCOL_FILE = "file://";
}
