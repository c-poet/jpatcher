package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.compatible.CompatibleService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

/**
 * Spring(boot)处理工具
 *
 * @author CPoet
 */
public class SpringUtil {
    /** Spring Boot类文件路径 */
    public static final String SB_CLASSES_PATH = "BOOT-INF/classes";
    /** Spring Boot 依赖库文件路径 */
    public static final String SB_LIB_PATH = "BOOT-INF/lib";

    /**
     * 判断模块是否有Spring程序入口
     *
     * @param project 项目
     * @param module  模块
     * @return 是否有Spring程序入口
     */
    public static boolean isSpringAppModule(Project project, Module module) {
        return CompatibleService.getInstance()
                .instance(SpringUtil9.class)
                .isSpringApp(project, module);
    }

    /**
     * 判断是否有Spring库
     *
     * @param project 项目
     * @return 是否有Spring库
     */
    public static boolean hasSpringLibrary(Project project) {
        return CompatibleService.getInstance()
                .instance(SpringUtil9.class)
                .hasSpringLibrary(project);
    }

    /**
     * 判断是否有Spring库
     *
     * @param module 模块
     * @return 是否有Spring库
     */
    public static boolean hasSpringLibrary(Module module) {
        return CompatibleService.getInstance()
                .instance(SpringUtil9.class)
                .hasSpringLibrary(module);
    }
}
