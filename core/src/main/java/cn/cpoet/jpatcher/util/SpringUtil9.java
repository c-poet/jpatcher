package cn.cpoet.jpatcher.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

/**
 * @author CPoet
 */
public interface SpringUtil9 {
    /**
     * @see SpringUtil#isSpringAppModule(Project, Module)
     */
    boolean isSpringApp(Project project, Module module);

    /**
     * @see SpringUtil#hasSpringLibrary(Project)
     */
    boolean hasSpringLibrary(Project project);

    /**
     * @see SpringUtil#hasSpringLibrary(Module)
     */
    boolean hasSpringLibrary(Module module);
}
