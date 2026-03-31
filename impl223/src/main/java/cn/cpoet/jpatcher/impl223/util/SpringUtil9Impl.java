package cn.cpoet.jpatcher.impl223.util;

import cn.cpoet.jpatcher.util.SpringUtil9;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.spring.SpringLibraryUtil;
import com.intellij.spring.SpringManager;
import com.intellij.spring.contexts.model.SpringModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

/**
 * @author CPoet
 */
public class SpringUtil9Impl implements SpringUtil9 {
    @Override
    public boolean isSpringApp(Project project, Module module) {
        Set<SpringModel> springModels = SpringManager.getInstance(project).getAllModelsWithoutDependencies(module);
        return CollectionUtils.isNotEmpty(springModels);
    }

    @Override
    public boolean hasSpringLibrary(Project project) {
        return SpringLibraryUtil.hasSpringLibrary(project);
    }

    @Override
    public boolean hasSpringLibrary(Module module) {
        return SpringLibraryUtil.hasSpringLibrary(module);
    }
}
